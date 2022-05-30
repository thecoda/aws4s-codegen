package thecoda.aws.codegen

import thecoda.aws.boto.servicemodel.*

import scala.collection.immutable.ListMap

case class CodeGen(modelRoot: ServiceModelRoot) {
  import modelRoot.{shapes, operations}

  def ctx: CodeGenContext = CodeGenContext(modelRoot)

  private def refineMinMax(base: String, min: Option[Long], max: Option[Long]): String =
    (min, max) match {
      case (Some(min), None) => s"$base Refined MinSize[$min]"
      case (None, Some(max)) => s"$base Refined MaxSize[$max]"
      case (Some(min), Some(max)) => s"$base Refined MinSize[$min] And MaxSize[$max]"
      case (None, None) => ""
    }

  private def genListCode(name: String, sd: ListShapeDef): String =
    refineMinMax(s"type $name = List[${sd.member.shape}]", sd.min, sd.max)

  private def genMapCode(name: String, sd: MapShapeDef): String =
    refineMinMax(s"type $name = Map[${sd.key.shape},${sd.value.shape}]", sd.min, sd.max)

  def genOperationsCode(): Iterable[String] =
    operations flatMap { case(name, op) =>
      OperationGen(name, op, ctx).genOperationCode().toList :+ ""
    }

  def genShapesCode(): Iterable[String] = {
    for((name,shapeDef) <- shapes) yield {
      shapeDef match {
        case sd: StructureShapeDef => StructureGen(name, sd, ctx).emit
        case sd: ListShapeDef => genListCode(name, sd)
        case sd: MapShapeDef => genMapCode(name, sd)
        case sd: ElementalShapeDef if sd.isSimple => ""
        case sd: StringShapeDef if sd.isSimple => s"type $name = String"
        case sd: StringShapeDef if sd.pattern.isDefined => s"""type $name = String Refined MatchesRegex["${sd.pattern.get}"]"""
        case sd: StringShapeDef if sd.isEnum => s"enum $name:\n   case ${sd.`enum`.get.mkString(", ")}"
        case sd: StringShapeDef => "*StringShapeDef"
        case sd: BooleanShapeDef => s"opaque type $name = Boolean"
        case sd: TimestampShapeDef => "*TimestampShapeDef"
        case sd: LongShapeDef => s"type $name = Long"
        case sd: DoubleShapeDef => s"type $name = Double"
        case sd: FloatShapeDef => s"type $name = Float"
        case sd: IntegerShapeDef => s"type $name = Int"
        case sd: BlobShapeDef => "*BlobShapeDef"
      }
    }
  } filterNot(_.isEmpty)
}
