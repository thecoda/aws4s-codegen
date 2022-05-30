package thecoda.aws.boto.servicemodel.subelems

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import thecoda.aws.Common.makeStrictCodec

case class Retryable(throttling: Boolean)

object Retryable {
  given JsonValueCodec[Retryable] = makeStrictCodec
}
