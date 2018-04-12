package a8.versions

import a8.common.JsonAssist
import coursier.Dependency
import net.model3.lang.StringX
import play.api.libs.json.Json

object JsonFormats {

  implicit val formatAttributes = Json.format[coursier.core.Attributes]
  implicit val formatCoursierModule = Json.format[coursier.core.Module]
  implicit val formatDependency = Json.format[Dependency]

  implicit val formatIdentifier =
    JsonAssist.utils.stringValueFormat[model.Identifier](
      s => if ( s.charAt(0).isLetter ) model.VariableIdentifier(s) else model.StringIdentifier(s),
      _.rawValue
    )

}
