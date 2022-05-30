package thecoda.aws.codegen

import thecoda.aws.boto.servicemodel.*

case class CodeGenContext(modelRoot: ServiceModelRoot) {

  def lookup(name: String): ShapeDef = modelRoot.shapes(name)
  def lookup(ref: ShapeRef): ShapeDef = lookup(ref.shape)

  def docFor(ref: ShapeRef): Option[String] =
    ref.documentation orElse lookup(ref).documentation

  def typeNameFor(ref: ShapeRef): String = {
    def sd: ShapeDef = lookup(ref)
    sd match {
      case esd: ElementalShapeDef if esd.isSimple => esd.scalaBaseType
      case lsd: ListShapeDef if lsd.isSimple => s"List[${typeNameFor(lsd.member)}]"
      case msd: MapShapeDef if msd.isSimple => s"Map[${typeNameFor(msd.key)}, ${typeNameFor(msd.value)}]"
      case _ => ref.shape
    }
  }
}
