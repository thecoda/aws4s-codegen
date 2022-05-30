package thecoda.aws.boto.servicemodel.subelems

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import thecoda.aws.Common.makeStrictCodec

case class ProtocolSettings(
  h2: String
)

object ProtocolSettings {
  given JsonValueCodec[ProtocolSettings] = makeStrictCodec
}
