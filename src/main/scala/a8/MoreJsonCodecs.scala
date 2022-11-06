package a8

import a8.shared.json.JsonCodec
import com.softwaremill.sttp.Uri

object MoreJsonCodecs {

  implicit val uri =
    JsonCodec.string.dimap[Uri](
      s => Uri.parse(s).get,
      _.toString,
    )

}
