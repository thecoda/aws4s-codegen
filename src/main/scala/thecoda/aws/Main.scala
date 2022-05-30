package thecoda.aws

import com.github.plokhotnyuk.jsoniter_scala.core.readFromArray
import org.ekrich.config.{Config, ConfigFactory}
import pprint.PPrinter
import thecoda.aws.Common.pp
import thecoda.aws.boto.ModelEnumerator
import thecoda.aws.boto.servicemodel.ServiceModelRoot
import thecoda.aws.codegen.CodeGen
import thecoda.aws.codegen.model.{Param, ServiceClass, ServiceMethod}
import thecoda.aws.codegen.mustache.MustacheEngine

import java.io.{Reader, StringReader, StringWriter}
import java.nio.file.Files
import scala.util.{Failure, Success, Try, Using}

object Main {

  def main(args: Array[String]): Unit = {

    val renderScope = ServiceClass(
      pkg = "my.fancy.package",
      name = "TheAwesomeOne",
      doc = "The main event.\nWatch it here.\nRight now.",
      methods = Seq(
        ServiceMethod(
          name = "methodTheFirst",
          doc = "this is a\nwell-documented\nmethod",
          returnType = "String",
          returnDoc = "It's readable!",
          params = Seq(
            Param(
              name = "param1",
              paramType = "String",
              doc = "Yup, a string",
            ),
            Param(
              name = "param2",
              paramType = "Int",
              doc = "This one\nis integral",
            )
          )
        )
      )
    )

    val output = MustacheEngine.run("service_class.mustache", renderScope).get
    println(output)

/*
    ModelEnumerator.modelSpecs foreach { ms =>
      Try(readFromArray[ServiceModelRoot](Files.readAllBytes(ms.serviceFile))) match {
        case Failure(err) =>
          println(ms.serviceFile.toString)
          pp.pprintln(err)
        case Success(modelRoot) if ms.name == "s3" =>
          println(ms.serviceFile.toString)
//            pp.pprintln(modelRoot)
          val cg = CodeGen(modelRoot)
          cg.genShapesCode() foreach { elem =>
            println(elem)
            println
          }
          cg.genOperationsCode() foreach { elem =>
            println(elem)
          }
        case Success(modelRoot) =>
        //          pp.pprintln(modelRoot.metadata)
        //          pp.pprintln(modelRoot.operations)
      }
    }
*/
    /*
    val json = Source.fromResource("services/s3/service-2.json").getLines().mkString("\n")
    parse(json).flatMap(_.as[ModelRoot]) match {
      case Left(err) => pp.pprintln(err)
      case Right(modelRoot) =>
        println(s"version: ${modelRoot.version}")
        pp.pprintln(modelRoot.metadata)

        println("")
        println("DOCUMENTATION")
        println("=============")
        println("")
        pp.pprintln(modelRoot.documentation)

        println("")
        println("OPERATIONS")
        println("==========")
        println("")
        pp.pprintln(modelRoot.operations)

        println("")
        println("SIMPLE SHAPES")
        println("=============")
        println("")
        pp.pprintln(modelRoot.simpleShapes)

        println("")
        println("COMPLEX SHAPES")
        println("==============")
        println("")
        pp.pprintln(modelRoot.listShapes)
        pp.pprintln(modelRoot.mapShapes)
        pp.pprintln(modelRoot.structureShapes)


      //        pp.pprintln(modelRoot.shapes.groupBy(_._2.`type`))
//        pp.pprintln(modelRoot.reifiedOperations)
    }
*/
  }
}
