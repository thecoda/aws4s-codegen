package thecoda.aws.codegen

import org.jsoup.Jsoup
import org.jsoup.nodes.*
import org.jsoup.select.Evaluator
import thecoda.aws.Common

import scala.jdk.CollectionConverters.*
import scala.util.matching.Regex

sealed trait Elem {
  def mkText: String
}
case class Text(content: String) extends Elem {
  def mkText: String = content
}
case class Bold(elems: List[Elem]) extends Elem {
  def mkText: String = "**" + elems.map(_.mkText).mkString("") + "**"
}
case class Italic(elems: List[Elem]) extends Elem {
  def mkText: String = "*" + elems.map(_.mkText).mkString("") + "*"
}
case class ExternalLink(text: String, href: String) extends Elem {
  def mkText: String = s"[$text]($href)"
}
case class Para(elems: List[Elem]) extends Elem {
  def mkText: String = elems.map(_.mkText).mkString("","","\n")
}
case class UnorderedList(items: List[ListItem]) extends Elem {
  def mkText: String = items.map(_.mkText).mkString("\n")
}
case class ListItem(elems: List[Elem]) extends Elem {
  def mkText: String = s" - ${elems.map(_.mkText).mkString("")}"
  def loseInnerParagraph: ListItem = {
    elems match {
      case Para(subelems) :: Nil => ListItem(subelems)
      case _ => this
    }
  }
}
case class Code(content: String) extends Elem {
  def mkText: String = s"[[$content]]"
}

object DocParser {

  def main(args: Array[String]): Unit = {
    val parsed = parseToElems(testString)
    parsed foreach { part =>
      Common.pp.pprintln(part)
    }

    parsed foreach { part =>
      println(part.mkText)
    }
  }

  def parseToString(html: String): String =
    parseToElems(html).map(_.mkText).mkString("\n")

  def parseToLines(html: String): List[String] =
    parseToString(html).split('\n').toList

  def parseToElems(html: String): List[Elem] =
    recurse(Jsoup.parseBodyFragment(html).body)

  def recurse(elem: Element): List[Elem] =
    elem.childNodes.asScala.toList flatMap {
      case e: Element if e.tagName equalsIgnoreCase "p" =>
        Some(Para(recurse(e)))
      case e: Element if e.tagName equalsIgnoreCase "ul" =>
        Some(UnorderedList(recurseUl(e)))
      case e: Element if e.tagName equalsIgnoreCase "code" =>
        Some(Code(e.text))
      case e: Element if e.tagName equalsIgnoreCase "b" =>
        Some(Bold(recurse(e)))
      case e: Element if e.tagName equalsIgnoreCase "i" =>
        Some(Italic(recurse(e)))
      case e: Element if e.tagName equalsIgnoreCase "a" =>
        Some(ExternalLink(e.text, e.attributes.get("href")))
      case tn: TextNode =>
        if tn.text.isBlank then None else Some(Text(tn.text))
      case n: Node =>
        println(s"Unmatched HTML in documentation [${n.getClass}]: $n")
        Some(Text(s"Unknown: $n"))
    }

  def recurseUl(elem: Element): List[ListItem] =
    elem.getElementsByTag("li").asScala.toList map { e =>
      ListItem(recurse(e)).loseInnerParagraph
    }

  def testString = "<p>The status of the access preview.</p> <ul> <li> <p> <code>Creating</code> - The access preview creation is in progress.</p> </li> <li> <p> <code>Completed</code> - The access preview is complete. You can preview findings for external access to the resource.</p> </li> <li> <p> <code>Failed</code> - The access preview creation has failed.</p> </li> </ul>"
}
