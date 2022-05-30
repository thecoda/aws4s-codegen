package thecoda.aws.boto.servicemodel

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import thecoda.aws.Common.makeStrictCodec
import thecoda.aws.boto.servicemodel.subelems.*

import scala.collection.immutable.ListMap

case class ServiceModelRoot(
  version       : Option[String],
  metadata      : ModelMetadata,
  operations    : ListMap[String, Operation],
  shapes        : ListMap[String, ShapeDef],
  documentation : Option[String],
  authorizers   : Option[Authorizers],
  examples      : Option[EmptyObject.type],

) {
  private def unorderedStringShapes: Map[String, StringShapeDef] =
    shapes.collect { case (k, v: StringShapeDef) => k -> v}
  def boundStringShapes: Map[String, StringShapeDef] =
    unorderedStringShapes.collect { case (k, v) if v.isBound => k -> v}
  def enumStringShapes: Map[String, StringShapeDef] =
    unorderedStringShapes.collect { case (k, v) if v.isEnum => k -> v}
  def nonEnumStringShapes: Map[String, StringShapeDef] =
    unorderedStringShapes.collect { case (k, v) if !v.isEnum => k -> v}
  def simpleStringShapes: Map[String, StringShapeDef] =
    unorderedStringShapes.collect { case (k, v) if v.isSimple => k -> v }
  def stringShapes: Map[String, StringShapeDef] =
    ListMap.empty ++ boundStringShapes ++ enumStringShapes ++ simpleStringShapes

  def booleanShapes: Map[String, BooleanShapeDef] =
    shapes.collect { case (k, v: BooleanShapeDef) => k -> v}
  def integerShapes: Map[String, IntegerShapeDef] =
    shapes.collect { case (k, v: IntegerShapeDef) => k -> v}
  def longShapes: Map[String, LongShapeDef] =
    shapes.collect { case (k, v: LongShapeDef) => k -> v}
  def doubleShapes: Map[String, DoubleShapeDef] =
    shapes.collect { case (k, v: DoubleShapeDef) => k -> v}
  def floatShapes: Map[String, FloatShapeDef] =
    shapes.collect { case (k, v: FloatShapeDef) => k -> v}
  def timestampShapes: Map[String, TimestampShapeDef] =
    shapes.collect { case (k, v: TimestampShapeDef) => k -> v}
  def blobShapes: Map[String, BlobShapeDef] =
    shapes.collect { case (k, v: BlobShapeDef) => k -> v}

  def elementalShapes: Map[String, ElementalShapeDef] =
    shapes.collect { case (k, v: ElementalShapeDef) => k -> v}

  def listShapes: Map[String, ListShapeDef] =
    shapes.collect { case (k, v: ListShapeDef) => k -> v}
  def mapShapes: Map[String, MapShapeDef] =
    shapes.collect { case (k, v: MapShapeDef) => k -> v}
  def structureShapes: Map[String, StructureShapeDef] =
    shapes.collect { case (k, v: StructureShapeDef) => k -> v}

  def compoundShapes: Map[String, CompoundShapeDef] =
    shapes.collect { case (k, v: CompoundShapeDef) => k -> v}
}

object ServiceModelRoot {
  given JsonValueCodec[ServiceModelRoot] = makeStrictCodec
}