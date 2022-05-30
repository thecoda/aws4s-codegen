package thecoda.aws.boto

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import thecoda.aws.Common.makeStrictCodec

case class TreeRoot(
  sha: String,
  url: String,
  tree: Seq[TreeElem],
  truncated: Boolean
)

object TreeRoot {
  given JsonValueCodec[TreeRoot] = makeStrictCodec
}