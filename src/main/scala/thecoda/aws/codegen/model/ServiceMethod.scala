package thecoda.aws.codegen.model

case class ServiceMethod(
  name       : String,
  returnType : String,
  returnDoc  : String,
  doc        : String,
  params     : Iterable[Param],
)
