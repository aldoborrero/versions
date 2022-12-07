package io.accur8.neodeploy

import a8.shared.{CompanionGen, StringValue}
import a8.shared.SharedImports._
import io.accur8.neodeploy.HealthchecksDotIo.{ApiAuthToken, CheckReadOnly, CheckUpsertRequest, impl}
import io.accur8.neodeploy.MxHealthchecksDotIo._
import io.accur8.neodeploy.dsl.Step
import zio.ZIO

import java.net.http.{HttpClient, HttpRequest, HttpResponse}
import java.time.Duration

object HealthchecksDotIo {

  object impl {
    val httpClient =
      HttpClient
        .newBuilder()
        .version(HttpClient.Version.HTTP_2)
        .followRedirects(HttpClient.Redirect.NORMAL)
        .connectTimeout(Duration.ofSeconds(20))
        .build();

    def send(request: HttpRequest): HttpResponse[String] =
      httpClient.send(request, HttpResponse.BodyHandlers.ofString())

  }


  object CheckReadOnly extends MxCheckReadOnly
  @CompanionGen
  case class CheckReadOnly(
    name: String,
    slug: String,
    tags: String,
    desc: String,
    grace: Long,
    n_pings: Long,
    status: String,
    last_ping: Option[String] = None,
    next_ping: Option[String] = None,
    manual_resume: Boolean,
    methods: String,
    success_kw: String,
    failure_kw: String,
    filter_subject: Boolean,
    filter_body: Boolean,
    ping_url: String,
    update_url: String,
    pause_url: String,
    resume_url: String,
    channels: String,
    schedule: Option[String] = None,
    tz: Option[String] = None,
    subject: Option[String] = None,
    subject_fail: Option[String] = None,
    timeout: Option[Long] = None,
    last_duration: Option[Long] = None,
  )


  /**
    * https://healthchecks.io/docs/api/#create-check
    */
  object CheckUpsertRequest extends MxCheckUpsertRequest
  @CompanionGen
  case class CheckUpsertRequest(
    name: String,
    tags: Option[String] = None,
    desc: Option[String] = None,
    /** the period of this check */
    timeout: Option[Long] = None,
    grace: Option[Long] = None,
    /** cron schedule */
    schedule: Option[String] = None,
    tz: Option[String] = None,
    unique: Iterable[String] = Iterable.empty,
  )

  object ApiAuthToken extends StringValue.Companion[ApiAuthToken]
  case class ApiAuthToken(value: String) extends StringValue
}

case class HealthchecksDotIo(apiAuthToken: ApiAuthToken) { self =>

  def baseRequest =
    HttpRequest
      .newBuilder()
      .header("X-Api-Key", apiAuthToken.value)

  def listChecks(): Iterable[CheckReadOnly] = {
    val request =
      baseRequest
        .GET()
        .uri(java.net.URI.create("https://healthchecks.io/api/v1/checks/"))
        .build()

    val response = impl.send(request)

    if (response.statusCode() / 100 == 2) {
      val responseJsv = json.unsafeParse(response.body())
      responseJsv("checks").unsafeAs[Iterable[CheckReadOnly]]
    } else {
      throw new RuntimeException(s"Unexpected response from healthchecks.io: ${response.statusCode()} ${response.body()}")
    }
  }


  def fetchCheck(name: String): Option[CheckReadOnly] =
    listChecks()
      .find(_.name == name)


  def doesCheckExist(name: String): Boolean =
    listChecks()
      .exists(_.name == name)

  def insertCheckIfItDoesNotExist(check: CheckUpsertRequest): CheckReadOnly = {
    val jsonRequestBody: HttpRequest.BodyPublisher =
      HttpRequest.BodyPublishers.ofString(check.prettyJson)
    val request =
      baseRequest
        .POST(jsonRequestBody)
        .uri(java.net.URI.create("https://healthchecks.io/api/v1/checks/"))
        .header("Content-Type", "application/json")
        .build()

    val response = impl.send(request)

    if ( response.statusCode() / 100 == 2 ) {
      json.unsafeRead[CheckReadOnly](response.body())
    } else {
      throw new RuntimeException(s"Unexpected response from healthchecks.io: ${response.statusCode()} ${response.body()}")
    }

  }

  def pause(name: String): Unit = {
    fetchCheck(name) match {
      case Some(check) =>
        val request =
          baseRequest
            .POST(HttpRequest.BodyPublishers.ofString(""))
            .uri(java.net.URI.create(check.pause_url))
            .build()

        val response = impl.send(request)
        if (response.statusCode() / 100 == 2) {
          json.unsafeRead[CheckReadOnly](response.body())
        } else {
          throw new RuntimeException(s"Unexpected response from healthchecks.io: ${response.statusCode()} ${response.body()}")
        }

      case None =>
        ()
    }
  }

  object step {

    def pause(name: String): Step = {
      Step.rawEffect(
        s"disable healthchecks.io ${name} check",
        ZIO.attemptBlocking(!doesCheckExist(name)),
        ZIO.attemptBlocking(self.pause(name)),
      )
    }

    def upsert(check: CheckUpsertRequest): Step =
      Step.rawEffect(
        s"insert healthchecks.io ${check.name} check",
        ZIO.attemptBlocking(!doesCheckExist(check.name)),
        ZIO.attemptBlocking(insertCheckIfItDoesNotExist(check))
      )

  }

}
