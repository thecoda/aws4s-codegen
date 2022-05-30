package thecoda.aws.codegen.mustache

import com.github.mustachejava.{Code, DefaultMustacheFactory, DefaultMustacheVisitor, Mustache, MustacheFactory, MustacheParser, MustacheVisitor, ObjectHandler, SpecMustacheFactory, SpecMustacheVisitor, TemplateContext}
import com.github.blemale.scaffeine.{Cache, Scaffeine}
import com.github.mustachejava.codes.{IterableCode, PartialCode}
import com.github.mustachejava.reflect.ReflectionObjectHandler
import com.github.mustachejava.resolver.DefaultResolver
import com.github.mustachejava.util.IndentWriter

import java.io.{StringReader, StringWriter, Writer}
import java.util
import java.util.List
import scala.util.{Try, Using}

object MustacheEngine {

  private[this] val cache: Cache[String, Mustache] =
    Scaffeine()
      .recordStats()
      .maximumSize(500)
      .build[String, Mustache]()

  class FixedSpecPartialCode(
    tc: TemplateContext,
    cf: DefaultMustacheFactory,
    variable: String,
    val indent: String
  ) extends PartialCode(tc, cf, variable) {
    override protected def executePartial(writer: Writer, scopes: util.List[AnyRef]): Writer = {
      partial.execute(new FixedIndentWriter(writer, indent), scopes)
      writer
    }
    // Don't ask, don't tell
    override def clone(): AnyRef = super.clone()
    override def clone(seen: util.Set[Code]): AnyRef = super.clone(seen)
  }
  class FixedVisitor(df: DefaultMustacheFactory) extends SpecMustacheVisitor(df) {
    override def iterable(templateContext: TemplateContext, variable: String, mustache: Mustache): Unit =
      list.add(new FixedIterableCode(templateContext, df, mustache, variable))

    override def partial(tc: TemplateContext, variable: String, indent: String): Unit = {
      val partialTC = new TemplateContext("{{", "}}", tc.file, tc.line, tc.startOfLine)
      list.add(new FixedSpecPartialCode(partialTC, df, variable, indent))
    }
  }

  private[this] object factory extends SpecMustacheFactory("mustache-templates") {
//    setObjectHandler(ScalaObjectHandler)
    setObjectHandler(PureScalaObjectHandler)
    override def encode(value: String, writer: Writer): Unit = writer.write(value)
    override def createMustacheVisitor(): MustacheVisitor = new FixedVisitor(this)
  }


  private[this] def prepareScope(scope: Seq[Any]): Array[Any] =
    (scope :+ HelperFunctions.all).toArray
  //    (scope :+ Map("now_str" -> { () => "NOWNOWNOW" })).toArray

  private[this] def runCompiled(compiled: Mustache, scope: Seq[Any]): Try[String] =
    Using(new StringWriter){ compiled.execute(_, prepareScope(scope)).toString }

  private[this] def compileLiteral(template: String, name: String): Try[Mustache] =
    Using(new StringReader(template)){ factory.compile(_, name) }

  def run(name: String, scope: Any*): Try[String] =
    runCompiled(cache.get(name, factory.compile), scope)

  def runLiteral(template: String, name: String, scope: Any*): Try[String] =
    compileLiteral(template, name).flatMap(runCompiled(_, scope))
}
