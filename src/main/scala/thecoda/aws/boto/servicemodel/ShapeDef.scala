package thecoda.aws.boto.servicemodel

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker, named}
import thecoda.aws.SparsePrint
import thecoda.aws.boto.servicemodel.subelems.*

sealed trait ShapeDef extends SparsePrint with Product {
  def documentation     : Option[String]
  def exception         : Option[Boolean]
  def deprecated        : Option[Boolean]
  def deprecatedMessage : Option[String]
  def sensitive         : Option[Boolean]
  def box               : Option[Boolean]
}

sealed trait CompoundShapeDef extends ShapeDef

@named("structure")
case class StructureShapeDef(
  required          : Option[Set[String]],
  members           : Map[String, ShapeRef],
  documentation     : Option[String],
  error             : Option[ErrorDef],
  exception         : Option[Boolean],
  fault             : Option[Boolean],
  retryable         : Option[Retryable],
  event             : Option[Boolean],
  eventstream       : Option[Boolean],
  payload           : Option[String],
  xmlNamespace      : Option[XmlNamespace],
  locationName      : Option[String],
  union             : Option[Boolean],
  deprecated        : Option[Boolean],
  deprecatedMessage : Option[String],
  sensitive         : Option[Boolean],
  xmlOrder          : Option[Seq[String]],
  synthetic         : Option[Boolean],
  box               : Option[Boolean],
  document          : Option[Boolean],
  wrapper           : Option[Boolean],

) extends CompoundShapeDef

@named("list")
case class ListShapeDef(
  member            : ShapeRef,
  flattened         : Option[Boolean],
  documentation     : Option[String],
  exception         : Option[Boolean],
  min               : Option[Long],
  max               : Option[Long],
  sensitive         : Option[Boolean],
  deprecated        : Option[Boolean],
  deprecatedMessage : Option[String],
  box               : Option[Boolean],

) extends CompoundShapeDef {
  def isSimple: Boolean = min.isEmpty && max.isEmpty
}

@named("map")
case class MapShapeDef(
  key               : ShapeRef,
  value             : ShapeRef,
  documentation     : Option[String],
  exception         : Option[Boolean],
  min               : Option[Long],
  max               : Option[Long],
  sensitive         : Option[Boolean],
  deprecated        : Option[Boolean],
  deprecatedMessage : Option[String],
  flattened         : Option[Boolean],
  locationName      : Option[String],
  box               : Option[Boolean],
) extends CompoundShapeDef {
  def isSimple: Boolean = min.isEmpty && max.isEmpty
}

sealed trait ElementalShapeDef extends ShapeDef {
  def isSimple: Boolean
  def scalaBaseType: String
}

@named("string")
case class StringShapeDef(
  `enum`            : Option[List[String]],
  pattern           : Option[String],
  sensitive         : Option[Boolean],
  min               : Option[Int],
  max               : Option[Int],
  documentation     : Option[String],
  exception         : Option[Boolean],
  deprecated        : Option[Boolean],
  deprecatedMessage : Option[String],
  box               : Option[Boolean],

) extends ElementalShapeDef {
  def isEnum: Boolean = `enum`.isDefined
  def isBound: Boolean = pattern.isDefined || min.isDefined || max.isDefined
  def isSimple: Boolean = !isBound && !isEnum
  def scalaBaseType: String = "String"
}

@named("boolean")
case class BooleanShapeDef(
  documentation     : Option[String],
  exception         : Option[Boolean],
  box               : Option[Boolean],
  deprecated        : Option[Boolean],
  deprecatedMessage : Option[String],
  sensitive         : Option[Boolean],
) extends ElementalShapeDef {
  def isSimple: Boolean = true
  def scalaBaseType: String = "Boolean"
}

@named("timestamp")
case class TimestampShapeDef(
  timestampFormat   : Option[String],
  documentation     : Option[String],
  exception         : Option[Boolean],
  deprecated        : Option[Boolean],
  deprecatedMessage : Option[String],
  sensitive         : Option[Boolean],
  box               : Option[Boolean]

) extends ElementalShapeDef {
  def isSimple: Boolean = true
  def scalaBaseType: String = "java.time.ZonedDateTime"
}

@named("long")
case class LongShapeDef(
  documentation     : Option[String],
  exception         : Option[Boolean],
  min               : Option[Long],
  max               : Option[Long],
  box               : Option[Boolean],
  deprecated        : Option[Boolean],
  deprecatedMessage : Option[String],
  sensitive         : Option[Boolean],

) extends ElementalShapeDef {
  def isSimple: Boolean = min.isEmpty && max.isEmpty
  def scalaBaseType: String = "Long"
}

@named("double")
case class DoubleShapeDef(
  documentation     : Option[String],
  exception         : Option[Boolean],
  min               : Option[Double],
  max               : Option[Double],
  box               : Option[Boolean],
  deprecated        : Option[Boolean],
  deprecatedMessage : Option[String],
  sensitive         : Option[Boolean],

) extends ElementalShapeDef {
  def isSimple: Boolean = min.isEmpty && max.isEmpty
  def scalaBaseType: String = "Double"
}

@named("float")
case class FloatShapeDef(
  documentation     : Option[String],
  exception         : Option[Boolean],
  box               : Option[Boolean],
  min               : Option[Float],
  max               : Option[Float],
  deprecated        : Option[Boolean],
  deprecatedMessage : Option[String],
  sensitive         : Option[Boolean],

) extends ElementalShapeDef {
  def isSimple: Boolean = min.isEmpty && max.isEmpty
  def scalaBaseType: String = "Float"
}

@named("integer")
case class IntegerShapeDef(
  documentation     : Option[String],
  exception         : Option[Boolean],
  box               : Option[Boolean],
  min               : Option[Int],
  max               : Option[Int],
  deprecated        : Option[Boolean],
  deprecatedMessage : Option[String],
  sensitive         : Option[Boolean],
) extends ElementalShapeDef {
  def isSimple: Boolean = min.isEmpty && max.isEmpty
  def scalaBaseType: String = "Int"
}

@named("blob")
case class BlobShapeDef(
  documentation     : Option[String],
  exception         : Option[Boolean],
  min               : Option[Long],
  max               : Option[Long],
  sensitive         : Option[Boolean],
  streaming         : Option[Boolean],
  deprecated        : Option[Boolean],
  deprecatedMessage : Option[String],
  requiresLength    : Option[Boolean],
  box               : Option[Boolean],
) extends ShapeDef

object ShapeDef {
  given JsonValueCodec[ShapeDef] =
    JsonCodecMaker.make(
      CodecMakerConfig
        .withSkipUnexpectedFields(false)
        .withDiscriminatorFieldName(Some("type"))
        .withRequireDiscriminatorFirst(false)
    )
}

