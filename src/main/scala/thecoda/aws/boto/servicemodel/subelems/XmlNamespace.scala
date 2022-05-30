package thecoda.aws.boto.servicemodel.subelems

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import thecoda.aws.Common.makeStrictCodec
import thecoda.aws.SparsePrint

case class XmlNamespace(
  prefix : Option[String],
  uri    : String
) extends SparsePrint

object XmlNamespace {
  given JsonValueCodec[XmlNamespace] = makeStrictCodec
}
