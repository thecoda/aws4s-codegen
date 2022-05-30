package thecoda.aws.codegen.mustache

import scala.jdk.CollectionConverters.*
import com.github.mustachejava.{Iteration, ObjectHandler}
import com.github.mustachejava.reflect.guards.{ClassGuard, DepthGuard, DotGuard, MapGuard, NullGuard, WrappedGuard}
import com.github.mustachejava.reflect.{Guard, MissingWrapper, ReflectionObjectHandler, ReflectionWrapper}
import com.github.mustachejava.util.{GuardException, Wrapper}

import java.io.Writer
import java.lang.reflect.{AccessibleObject, Field, Method}
import runtime.BoxedUnit
import scala.reflect.ClassTag
import java.util.{List => JList}
import thecoda.aws.Common.productElementMap

import java.util.List
import scala.collection.mutable


object ScalaObjectHandler extends ReflectionObjectHandler {
  

  override def coerce(value: AnyRef): AnyRef = value match {
    case m: collection.Map[_, _] => m.asJava
    case _: BoxedUnit => null
    case Some(v: AnyRef) => coerce(v)
    case None => null
    case p: Product => p.productElementMap.asJava
    case v => v
  }

  override def iterate(
    iteration: Iteration,
    prevWriter: Writer,
    value: AnyRef,
    scopes: JList[AnyRef]
  ): Writer = value match {
    case t: Iterable[_] =>
      t.foldLeft(prevWriter){
        case (writer, elem: AnyRef) => iteration.next(writer, coerce(elem), scopes)
      }
    case n: Number => if n.intValue == 0 then prevWriter else iteration.next(prevWriter, coerce(value), scopes)
    case _ => super.iterate(iteration, prevWriter, value, scopes)
  }

  override def falsey(
    iteration: Iteration,
    writer: Writer,
    value: AnyRef,
    scopes: JList[AnyRef]
  ): Writer = value match {
    case t: Iterable[_] => if t.isEmpty then iteration.next(writer, value, scopes) else writer
    case n: Number => if n.intValue == 0 then iteration.next(writer, coerce(value), scopes) else writer
    case _ => super.falsey(iteration, writer, value, scopes)
  }
}