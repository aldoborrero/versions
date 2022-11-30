package io.accur8.neodeploy


import a8.shared.json.{JsonCodec, ast}
import a8.shared.json.ast.{JsDoc, JsStr}
import io.accur8.neodeploy.resolvedmodel.{ResolvedPgbackrestClient, ResolvedPgbackrestServer, ResolvedRSnapshotClient, ResolvedRSnapshotServer, ResolvedUser}
import org.typelevel.ci.CIString
import a8.shared.SharedImports._
import io.accur8.neodeploy.model.AuthorizedKey

object UserPlugin {

  object Factory {
    abstract class AbstractFactory[Descriptor : JsonCodec](name0: String) extends Factory {
      lazy val name = CIString(name0)
      def apply(jsd: JsDoc, user: ResolvedUser): Either[String,UserPlugin] = {
        jsd.as[Descriptor] match {
          case Left(re) =>
            Left(re.prettyMessage)
          case Right(descriptor) =>
            Right(apply(descriptor, user))
        }
      }

      def apply(descriptor: Descriptor, user: ResolvedUser): UserPlugin

    }
  }

  trait Factory {
    val name: CIString
    def apply(jsd: JsDoc, user: ResolvedUser): Either[String,UserPlugin]
  }

  lazy val factories: Vector[Factory] =
    Vector(
      ResolvedRSnapshotServer,
      ResolvedRSnapshotClient,
      ResolvedPgbackrestClient,
      ResolvedPgbackrestServer,
    )

  case class UserPlugins(jsd: JsDoc, user: ResolvedUser) {

    lazy val pluginInstances =
      rawPluginInstances
        .flatMap(_.toOption)

    lazy val rawPluginInstances: Seq[Either[String, UserPlugin]] = {

      def createPlugin(name: String): Either[String,UserPlugin] = {
        val nameCi = CIString(name)
        factories
          .find(_.name == nameCi)
          .map(_(jsd, user))
          .getOrElse(Left(s"unable to resolve plugin named ${name}"))
      }

      def error: Either[String,UserPlugin] =
        Left(s"unable to resolve plugin for ${user.qname} -- ${jsd.compactJson}")

      def impl(pluginJsv: JsDoc): Vector[Either[String,UserPlugin]] = {
        pluginJsv.actualJsVal match {
          case JsStr(name) =>
            Vector(createPlugin(name))
          case jso: ast.JsObj =>
            jso("name").actualJsVal match {
              case JsStr(name) =>
                Vector(createPlugin(name))
              case _ =>
                Vector(error)
            }
          case jsa: ast.JsArr =>
            jsa
              .values
              .toVector
              .flatMap(v => impl(v.toDoc))
          case _ =>
            Vector(error)
        }
      }

      impl(jsd)

    }

  lazy val resolvedRSnapshotServerOpt =
    pluginInstances
      .collect {
        case rrss: ResolvedRSnapshotServer =>
          rrss
      }
      .headOption

    lazy val resolvedRSnapshotClientOpt =
      pluginInstances
        .collect {
          case rrsc: ResolvedRSnapshotClient =>
            rrsc
        }
        .headOption


  }

}


trait UserPlugin {
  def authorizedKeys: Vector[AuthorizedKey] = Vector.empty
}

