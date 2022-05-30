package thecoda.aws

import com.github.plokhotnyuk.jsoniter_scala.core.JsonValueCodec
import com.github.plokhotnyuk.jsoniter_scala.macros.{CodecMakerConfig, JsonCodecMaker}
import org.ekrich.config.{Config, ConfigFactory}
import pprint.PPrinter
import sttp.client3.*
import sttp.model.Uri

import java.nio.file.Path
import scala.util.Try

object Common:

  lazy val pp: PPrinter =
    pprint.copy(
      additionalHandlers = {
        case sp: SparsePrint => pp.treeify(sp.sparseProduct, escapeUnicode = true, showFieldNames = true)
      },
      defaultHeight = 100000
    )

  val coreConfig: Config = ConfigFactory.load()

  val aws4sFolder: Path = coreConfig.getPath("aws4sFolder")
  val modelFolder: Path = coreConfig.getPath("modelFolder")
  val botoApiUri: Uri = coreConfig.getUri("botoApiUri")
  val botoRawFileUri: Uri = coreConfig.getUri("botoRawFileUri")

  val userConfig: Option[Config] = Try(
    ConfigFactory.parsePath(coreConfig.getPath("userConfigFile"))
  ).toOption

  val ghuser: Option[String] = userConfig.map(_.getString("ghuser"))
  val ghtoken: Option[String] = userConfig.map(_.getString("ghtoken"))

  extension (factory: ConfigFactory.type)
    def parsePath(path: Path): Config = factory.parseFile(path.toFile)

  extension (config: Config)
    def getPath(path: String): Path = Path of config.getString(path)
    def getUri(path: String): Uri = uri"${config.getString(path)}"

  extension (base: Uri)
    def /(part: String): Uri = uri"$base/$part"

  extension (base: Path)
    def /(part: String): Path = base resolve part

  extension (p: Product)
    def productElementMap: Map[String, Any] =
      (p.productElementNames zip p.productIterator).toMap

    def elementByName(name: String): Option[Any] =
      p.productElementNames.zipWithIndex.collectFirst {
        case (n, idx) if n == name => p.productElement(idx)
      }

  inline def makeStrictCodec[A]: JsonValueCodec[A] =
    JsonCodecMaker.make(
      CodecMakerConfig
        .withSkipUnexpectedFields(false)
        .withMapMaxInsertNumber(4096)
    )
