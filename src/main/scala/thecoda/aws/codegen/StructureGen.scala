package thecoda.aws.codegen

import thecoda.aws.boto.servicemodel.*

case class StructureGen(name: String, sd: StructureShapeDef, ctx: CodeGenContext) {
  val requiredSet: Set[String] = sd.required.toSeq.flatten.toSet

  def docHeaderLines: Seq[String] =
    sd.documentation.toSeq.flatMap(DocParser.parseToLines) :+ ""

  def docParamLines: Seq[String] =
    sd.members.toSeq flatMap {
      case (name, ref) => ctx.docFor(ref).toSeq.flatMap(doc =>
        ("@param " + name + " " + DocParser.parseToString(doc)).split('\n')
      )
    }

  def docLines: Seq[String] = docHeaderLines ++ docParamLines

  def docString = docLines.mkString("/** ", "\n  * ", "\n  */")

  def paramLine(name: String, ref: ShapeRef) : String = {
    def sd: ShapeDef = ctx.lookup(ref.shape)
    val baseType = ctx.typeNameFor(ref)
    if requiredSet contains name
      then s"$name: $baseType,"
      else s"$name: Option[$baseType] = None,"
  }

  def paramLines: Seq[String] = sd.members.toSeq.sortBy(x => !(requiredSet contains x._1)) map paramLine

  def decl: String =
    s"case class $name (\n" +
      paramLines.map("  " + _).mkString("\n") +
      "\n)" +
      (if sd.exception contains true then " extends Exception" else "")

  def emit: String = docString + "\n" + decl

}
