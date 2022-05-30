package thecoda.aws.boto.servicemodel

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import thecoda.aws.Common.makeStrictCodec
import thecoda.aws.SparsePrint
import thecoda.aws.boto.servicemodel.subelems.EndpointDiscovery

object Operation {
  case class Http(
    method       : String,
    requestUri   : String,
    responseCode : Option[Double],
  ) extends SparsePrint

  case class Endpoint(
    hostPrefix   : String
  )

  case class HttpChecksum(
    requestAlgorithmMember      : Option[String],
    requestChecksumRequired     : Option[Boolean],
    requestValidationModeMember : Option[String],
    responseAlgorithms          : Option[List[String]],
  ) extends SparsePrint

  given JsonValueCodec[Http] = makeStrictCodec
  given JsonValueCodec[HttpChecksum] = makeStrictCodec
  given JsonValueCodec[Endpoint] = makeStrictCodec
  given JsonValueCodec[Operation] = makeStrictCodec
}

import Operation._

case class Operation(
  name                 : String,
  http                 : Http,
  input                : Option[ShapeRef],
  output               : Option[ShapeRef],
  errors               : Option[List[ShapeRef]],
  documentationUrl     : Option[String],
  documentation        : Option[String],
  alias                : Option[String],
  httpChecksum         : Option[HttpChecksum],
  httpChecksumRequired : Option[Boolean],
  deprecated           : Option[Boolean],
  deprecatedMessage    : Option[String],
  idempotent           : Option[Boolean],
  authtype             : Option[String],
  endpoint             : Option[Endpoint],
  endpointdiscovery    : Option[EndpointDiscovery],
  endpointoperation    : Option[Boolean],
) extends SparsePrint

