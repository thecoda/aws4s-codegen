package thecoda.aws.boto.servicemodel

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import thecoda.aws.Common.makeStrictCodec
import thecoda.aws.SparsePrint
import thecoda.aws.boto.servicemodel.subelems.*

case class ShapeRef(
  shape             : String,
  documentation     : Option[String],
  location          : Option[String],
  locationName      : Option[String],
  xmlNamespace      : Option[XmlNamespace],
  deprecatedMessage : Option[String],
  pattern           : Option[String],
  resultWrapper     : Option[String],
  `enum`              : Option[List[String]],
  error             : Option[ErrorDef],
  queryName         : Option[String],
  xmlAttribute      : Option[Boolean],
  flattened         : Option[Boolean],
  deprecated        : Option[Boolean],
  streaming         : Option[Boolean],
  eventpayload      : Option[Boolean],
  hostLabel         : Option[Boolean],
  idempotencyToken  : Option[Boolean],
  box               : Option[Boolean],
  jsonvalue         : Option[Boolean],
  fault             : Option[Boolean],
  exception         : Option[Boolean],
  wrapper           : Option[Boolean],
) extends SparsePrint


object ShapeRef {
  given JsonValueCodec[ShapeRef] = makeStrictCodec
}