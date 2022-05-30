package thecoda.aws.boto.servicemodel.subelems

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import thecoda.aws.Common.makeStrictCodec

case class EndpointDiscovery(
  required: Option[Boolean]
)
object EndpointDiscovery {
  given JsonValueCodec[EndpointDiscovery] = makeStrictCodec
}
