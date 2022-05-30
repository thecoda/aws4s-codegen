package thecoda.aws.boto.servicemodel.subelems

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import thecoda.aws.Common.makeStrictCodec

case class Placement(
  location : String,
  name     : String,
)

case class AuthorizationStrategy(
  name      : String,
  `type`    : String,
  placement : Placement
)

case class Authorizers(
  authorization_strategy : AuthorizationStrategy
)

object Authorizers {
  given JsonValueCodec[Placement] = makeStrictCodec
  given JsonValueCodec[AuthorizationStrategy] = makeStrictCodec
  given JsonValueCodec[Authorizers] = makeStrictCodec
}