package thecoda.aws.codegen
package model

import thecoda.aws.boto.servicemodel.ServiceModelRoot

import scala.util.Try

case class ServiceClass(
  pkg: String,
  name: String,
  doc: String,
  methods: Iterable[ServiceMethod],
)

//object ServiceClass {
//  def fromBoto(boto: ServiceModelRoot): ServiceClass = {
//    ServiceClass(
//      pkg = "thecoda.aws4s",
//      name = boto.metadata.serviceId,
//      doc = boto.documentation getOrElse "",
//      methods = boto.
//    )
//  }
//}