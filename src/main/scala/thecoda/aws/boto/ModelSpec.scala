package thecoda.aws.boto

import thecoda.aws.SparsePrint

import java.nio.file.Path

case class ModelSpec(
  name: String,
  version: String,
  serviceFile: Path, //service-2.json
  paginatorsFile: Option[Path], //paginators-1.json
  paginatorExtrasFile: Option[Path], //paginators-1.sdk-extras.json
  examplesFile: Option[Path], //examples-1.json
  waitersFile: Option[Path], //waiters-2.json
) extends SparsePrint
