package thecoda.aws.codegen.mustache

import com.github.mustachejava.*
import com.github.mustachejava.reflect.{Guard, GuardedBinding, MissingWrapper}
import com.github.mustachejava.util.Wrapper

import java.io.Writer
import java.util.List as JList
import scala.runtime.BoxedUnit
import scala.jdk.CollectionConverters.*
import thecoda.aws.Common.*


object PureScalaObjectHandler extends ObjectHandler {

  def mkWrapper(value: Any): Wrapper = _ => value

  def splitAtFirstDot(str: String): (String, Option[String]) = {
    val dotPos = str.indexOf('.')
    if dotPos > 0 then
      val prefix = str.substring(0, dotPos)
      val remaining = str.substring(dotPos + 1)
      prefix -> Some(remaining)
    else str -> None
  }

  inline def asRef(value: Any): AnyRef = value.asInstanceOf[AnyRef]
  inline def asRef(value: Option[Any]): Option[AnyRef] = value.map(_.asInstanceOf[AnyRef])

  def resolve(name: String, ctx: Any): Option[AnyRef] = asRef{
    ctx match {
      case m: collection.Map[Any @unchecked, Any] =>
        if !m.keys.forall(_.isInstanceOf[String])
          then throw new MustacheException(s"Map $name doesn't have a key type of String")
          else m.get(name)
      case _: BoxedUnit => None
      case Some(v: AnyRef) => Some(v)
      case None => None
      case p: Product => p.elementByName(name)
      //TODO: tuples
      //      case v => v
      case _ => None
    }
  }

  def findInScope(name: String, scope: Any): Option[Wrapper] = {
    val (prefix, optRemaining) = splitAtFirstDot(name)
    val descent: Option[Any] = resolve(prefix, scope)
    optRemaining match {
      case Some(remaining) => descent.flatMap(findInScope(remaining, _))
      case None => descent.map(mkWrapper)
    }
  }

  override def find(name: String, scopes: JList[AnyRef]): Wrapper =
    scopes
      .asScala
      .view
      .reverse
      .flatMap(findInScope(name, _))
      .headOption
      .getOrElse(throw new MustacheException(s"Couldn't find $name"))

  //TODO: Do we *really* need to force into java-land?
  override def coerce(value: Any): Any = value match {
    case m: collection.Map[_, _] => m
    case _: BoxedUnit => null
    case Some(v: AnyRef) => coerce(v)
    case None => null
    case p: Product => p.productElementMap
    case v => v
  }

  override def iterate(
    iteration: Iteration,
    writer: Writer,
    value: Any,
    scopes: JList[AnyRef]
  ): Writer = value match {
    case null => writer
    case false => writer
    case "" => writer
    //Explicitly match maps here, so they won't get seen as iterables
    case m: collection.Map[_, _] => iteration.next(writer, m, scopes)
    case it: IterableOnce[_] => it.iterator.foldLeft(writer){
      (nextWriter, x) => iteration.next(nextWriter, coerce(x), scopes)
    }
    case arr: Array[_] => arr.foldLeft(writer){
      (nextWriter, x) => iteration.next(nextWriter, coerce(x), scopes)
    }
    //TODO: Tuples
    case _ => iteration.next(writer, value, scopes)
  }


  override def falsey(iteration: Iteration, writer: Writer, value: Any, scopes: JList[AnyRef]): Writer = {
    val test = value match {
      case null => false
      case false => false
      case "" => false
      case arr: Array[_] if arr.isEmpty => false
      case it: Iterable[_] if it.isEmpty => false
      case it: IterableOnce[_] if it.knownSize == 0 => false
      case _ => true
    }
    if test then iteration.next(writer, value, scopes) else writer
  }

  override def createBinding(name: String, tc: TemplateContext, code: Code): Binding =
    (scopes: JList[AnyRef]) => find(name, scopes).call(scopes)

  override def stringify(value: Any): String = value.toString

}
