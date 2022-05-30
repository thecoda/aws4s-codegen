package thecoda.aws.boto.servicemodel.subelems

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.JsonCodecMaker

case object EmptyObject {
  given JsonValueCodec[EmptyObject.type] = JsonCodecMaker.make
}
