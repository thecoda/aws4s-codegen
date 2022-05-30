package thecoda.aws.codegen.mustache

import java.io.{IOException, Writer}
import scala.annotation.tailrec

class FixedIndentWriter(
  val inner: Writer,
  val indent: String,
) extends Writer {
  private var prependIndent = false

  @tailrec
  private def setPrependIndent(): Unit = {
    this.prependIndent = true
    inner match {
      case iw: FixedIndentWriter => iw.setPrependIndent()
      case _ =>
    }
  }

  @throws[IOException]
  override def write(chars: Array[Char], off: Int, len: Int): Unit = {
    var newOff = off
    for (i <- newOff until len) {
      if (chars(i) == '\n') { // write character up to newline
        writeLine(chars, newOff, i + 1 - newOff)
        this.setPrependIndent()
        newOff = i + 1
      }
    }
    writeLine(chars, newOff, len - (newOff - off))
  }

  @throws[IOException]
  def flushIndent(): Unit = {
    if (this.prependIndent) {
      inner.append(indent)
      this.prependIndent = false
    }
  }

  @throws[IOException]
  private def writeLine(chars: Array[Char], off: Int, len: Int): Unit =
    if (len > 0) {
      this.flushIndent()
      inner.write(chars, off, len)
    }

  @throws[IOException]
  override def flush(): Unit = inner.flush()

  @throws[IOException]
  override def close(): Unit = inner.close()
}
