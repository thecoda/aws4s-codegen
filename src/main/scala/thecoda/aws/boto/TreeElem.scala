package thecoda.aws.boto

import com.github.plokhotnyuk.jsoniter_scala.macros.*
import com.github.plokhotnyuk.jsoniter_scala.core.*
import thecoda.aws.Common.makeStrictCodec

case class TreeElem(
  path: String,
  mode: String,
  `type`: String,
  sha: String,
  size: Option[Long],
  url: String,
)

object TreeElem {
  given JsonValueCodec[TreeElem] = makeStrictCodec
}