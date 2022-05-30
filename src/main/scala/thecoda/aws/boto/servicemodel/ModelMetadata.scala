package thecoda.aws.boto.servicemodel

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import thecoda.aws.Common.makeStrictCodec
import thecoda.aws.boto.servicemodel.subelems.ProtocolSettings

case class ModelMetadata(
  apiVersion          : String,
  checksumFormat      : Option[String],
  endpointPrefix      : String,
  jsonVersion         : Option[String],
  globalEndpoint      : Option[String],
  protocol            : String,
  protocolSettings    : Option[ProtocolSettings],
  serviceAbbreviation : Option[String],
  serviceFullName     : String,
  serviceId           : String,
  signatureVersion    : String,
  signingName         : Option[String],
  targetPrefix        : Option[String],
  xmlNamespace        : Option[String],
  uid                 : Option[String],
)

object ModelMetadata {
  given JsonValueCodec[ModelMetadata] = makeStrictCodec
}