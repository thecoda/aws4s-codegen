package thecoda.aws.boto

import thecoda.aws.Common
import scala.jdk.StreamConverters._

import java.nio.file.Files

object ModelEnumerator {
  def modelSpecs: Seq[ModelSpec] = {
    for {
      svcFolder <- Files.list(Common.modelFolder).filter(Files.isDirectory(_)).toScala(List).sortBy(_.getFileName.toString)
      versFolder <- Files.list(svcFolder).toScala(List).find(
        _.getFileName.toString.matches("""\d{4}-\d{2}-\d{2}""")
      )
      jsonFiles = Files.list(versFolder).toScala(List).filter(_.toString.endsWith(".json"))
    } yield {
      ModelSpec(
        name = svcFolder.getFileName.toString,
        version =  versFolder.getFileName.toString,
        serviceFile = jsonFiles.find(_.getFileName.toString == "service-2.json").get,
        paginatorsFile = jsonFiles.find(_.getFileName.toString == "paginators-1.json"),
        paginatorExtrasFile = jsonFiles.find(_.getFileName.toString == "paginators-1.sdk-extras.json"),
        examplesFile = jsonFiles.find(_.getFileName.toString == "examples-1.json"),
        waitersFile = jsonFiles.find(_.getFileName.toString == "waiters-2.json"),
      )
    }
  }
}
