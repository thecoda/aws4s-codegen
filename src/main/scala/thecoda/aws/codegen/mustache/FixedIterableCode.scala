package thecoda.aws.codegen.mustache

import com.github.mustachejava.{Code, DefaultMustacheFactory, Mustache, MustacheException, TemplateContext, TemplateFunction}
import com.github.mustachejava.codes.IterableCode

import java.io.{IOException, StringWriter, Writer}
import java.util
import java.util.List
import scala.util.Using
import java.util.function.Function


class FixedIterableCode(
  tc: TemplateContext,
  df: DefaultMustacheFactory,
  mustache: Mustache,
  variable: String,
  `type`: String = "#",
) extends IterableCode(tc, df, mustache, variable, `type`) {

  override protected def handleFunction(
    writer: Writer,
    function: Function[_,_] @unchecked,
    scopes: List[AnyRef]
  ): Writer = {
    val sw = new StringWriter()
    runIdentity(sw)
    function match {
      case tf: TemplateFunction =>
        val templateText = try {
          tf.apply(sw.toString)
        } catch {
          case e: Exception => throw new MustacheException("Function failure", e, tc)
        }
        if (templateText != null) {
          writeTemplate(writer, templateText, scopes);
        } else writer
      case func: Function[Any, Any] @unchecked =>
        try {
          val capture = Using(new StringWriter()){ writeTemplate(_, sw.toString, scopes).toString }.get
          val apply = try {
            func.apply(capture)
          } catch { case e: Exception =>
            throw new MustacheException("Function failure", e, tc)
          }
          if (apply != null) {
            val applyStr = apply.toString
            writer.write(applyStr.toArray, 0, applyStr.length)
          }
          writer
        } catch { case e: IOException =>
          throw new MustacheException("Failed to write function result", e, tc)
        }
    }
  }

  // Don't ask, don't tell
  override def clone(): AnyRef = super.clone()
  override def clone(seen: util.Set[Code]): AnyRef = super.clone(seen)

}
