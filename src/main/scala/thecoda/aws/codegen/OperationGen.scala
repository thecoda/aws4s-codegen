package thecoda.aws.codegen
import thecoda.aws.Common
import thecoda.aws.boto.servicemodel.*

case class OperationGen(name: String, op: Operation, ctx: CodeGenContext) {
  def docHeaderLines: Seq[String] =
    op.documentation.toSeq.flatMap(DocParser.parseToLines)
      :++ op.documentationUrl.toSeq.flatMap(url => Seq("", s"@see []($url)"))
      :+ ""

  def docParamLines: Seq[String] =
    op.input.toSeq flatMap { ref =>
      ctx.docFor(ref).toSeq flatMap { doc =>
        ("@param input " + DocParser.parseToString(doc)).split('\n')
      }
    }

  def docLines: Seq[String] = docHeaderLines ++ docParamLines

  def docString = docLines.mkString("/** ", "\n  * ", "\n  */")

  def declLine = op.input match {
    case Some(ref) => s"def ${op.name}(input: ${ctx.typeNameFor(ref)}) {"
    case None => s"def ${op.name} {"
  }

  def bodyLines: Iterable[String] = {
    val response = op.output match {
      case Some(ref) => s"asJson[${ref.shape}].getRight"
      case None => "asString"
    }

    val headerEntries: Map[String, String] = op.input match {
      case Some(ref) =>
        val shapeDef = ctx.lookup(ref).asInstanceOf[StructureShapeDef]
        shapeDef.members collect {
          case(name, memberRef) if memberRef.location.contains("header") =>
            memberRef.locationName.get -> s"input.$name"
        }
      case None => Map.empty
    }

    val headersBlock = headerEntries map { case (name, value) =>
      s"    .header(\"$name\", $value)"
    } mkString("\n")

    //TODO: location = uri | querystring

    val paramEntries: Map[String, String] = op.input match {
      case Some(ref) =>
        val shapeDef = ctx.lookup(ref).asInstanceOf[StructureShapeDef]
        shapeDef.members collect {
          case(name, memberRef) if memberRef.location.contains("querystring") =>
            memberRef.locationName.get -> s"input.$name"
        }
      case None => Map.empty
    }

    val paramMapBlock =
      if paramEntries.nonEmpty then
        val innertext = paramEntries map { case (name, value) =>
          s"\"$name\" -> $value"
        } mkString(",\n    ")
        s"""
          |  val params: Map[String, String] = Map(
          |    $innertext
          |  ) flatMap {
          |    case (k,Some(v)) => Some(k -> v.toString)
          |    case (k,None) => None
          |    case (k,v) => Some(k -> v.toString)
          |  }
          |""".stripMargin
      else ""

    val paramsUrlSuffix = if paramEntries.nonEmpty then "&params" else ""

    s"""
      |$paramMapBlock
      |
      |  basicRequest
      |    .copy(uri=uri"${op.http.requestUri}$paramsUrlSuffix", method=Method.${op.http.method})
      |    .response($response)
      |$headersBlock
      |    .send(backend)
      |    .body
      |""".stripMargin.split('\n')
  }

  def genOperationCode(): Iterable[String] = {
//    Common.pp.pprintln(docLines)
    docString.split('\n') :+ declLine :++ bodyLines :+ "}"
  }
}
