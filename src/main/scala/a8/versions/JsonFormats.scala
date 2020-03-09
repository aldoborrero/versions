package a8.versions

import a8.common.JsonAssist

object JsonFormats {

  implicit val formatIdentifier =
    JsonAssist.utils.stringValueFormat[ast.Identifier](
      s => if ( s.charAt(0).isLetter ) ast.VariableIdentifier(s) else ast.StringIdentifier(s),
      _.rawValue
    )

}
