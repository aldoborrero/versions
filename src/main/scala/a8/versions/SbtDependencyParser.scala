package a8.versions


import a8.versions.ast.{Dependency, Identifier, StringIdentifier, VariableIdentifier}


object SbtDependencyParser {

  import fastparse.all._

  def parse(dependenciesStr: String): Iterable[Dependency] = {
    dependencies.parse(dependenciesStr).get.value
  }


  val dependencies = P( dependency.rep(sep = ",") ~ ws0 ~ ",".? ~ ws0 ~ End )

  val ws = P( CharsWhile(_.isWhitespace) )
  val ws0 = P( ws.? )

  val dependency: P[Dependency] =
    P(stringLit ~ scalaVersionSeparator ~ stringLit ~ separator ~ identifier ~ (separator ~ stringLit).? ~ exclusions.rep)
      .map { case (org, scalaArtifact, artifact, version, scope, exclusions) =>
        Dependency(
          organization = org,
          scalaArtifactSeparator = scalaArtifact,
          artifactName = artifact,
          version = version,
          configuration = scope,
          exclusions = exclusions,
        )
      }
//      .log()

  val exclusions: P[(String, String)] =
    P(ws0 ~ "exclude(" ~ stringLit ~ op(",") ~ stringLit ~ op(")"))

  val scalaVersionSeparator = P(op("%%%") | op("%%") | op("%")).!.map(_.trim)
  val separator = op("%")

  def op(operator: String) = P(ws0 ~ operator)



  val identifier: P[Identifier] =
    P(
      variable
      | stringLit.map(s => StringIdentifier(s))
    )

  val variable = {
    val firstCharInIdentifier = P(CharPred(ch => ch.isLetter || ch == '_'))
    val remainingCharInIdentifier = P(firstCharInIdentifier | CharsWhile(_.isDigit))
    P(ws0 ~ (firstCharInIdentifier ~ remainingCharInIdentifier.rep).!)
      .map(VariableIdentifier)
  }

  val stringLit = {
    val quoteChar = P("\"")
    val stringLitInside = P(CharsWhile(_ != '"').!)
    P(ws0 ~ quoteChar ~ stringLitInside.! ~ quoteChar)
//      .log()
  }

}
