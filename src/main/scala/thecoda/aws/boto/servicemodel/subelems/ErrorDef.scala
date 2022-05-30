package thecoda.aws.boto.servicemodel.subelems

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import thecoda.aws.Common.makeStrictCodec

case class ErrorDef(
  httpStatusCode : Int,
  senderFault    : Option[Boolean],
  code           : Option[String],
  fault          : Option[Boolean],
)
object ErrorDef {
  given JsonValueCodec[ErrorDef] = makeStrictCodec
}
