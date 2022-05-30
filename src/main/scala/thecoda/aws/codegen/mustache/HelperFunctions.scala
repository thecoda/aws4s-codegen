package thecoda.aws.codegen.mustache

import java.time.ZonedDateTime
import scala.jdk.FunctionConverters.*

object HelperFunctions {
  def noop(input: String) = input

  def doccomment(input: String) =
    input
      .split("\n")
      .mkString("/** ", "\n  * ", "\n  */")

  def paramdoc(input: String) = {
    val allLines = input.split("\n").map(_.trim)
    val name = allLines.head
    val body = allLines.tail
    val preamble = s"@param $name"
    body.mkString(
      s"$preamble ",
      "\n" + " ".repeat(preamble.length +1),
      "\n"
    )
  }

  def now_str(input: String) = ZonedDateTime.now().toString

  val all = Map(
    "now_str" -> now_str.asJava,
    "noop" -> noop.asJava,
    "doccomment" -> doccomment.asJava,
    "paramdoc" -> paramdoc.asJava,
  )
}
