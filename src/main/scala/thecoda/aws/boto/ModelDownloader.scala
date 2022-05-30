package thecoda.aws.boto

import org.slf4j.LoggerFactory
import sttp.client3.*
import sttp.client3.jsoniter.*
import sttp.client3.logging.slf4j.Slf4jLoggingBackend
import sttp.model.Uri
import thecoda.aws.Common.*

import java.nio.file.{Files, Path}
import org.slf4j.Logger
import sttp.capabilities.Effect
import sttp.client3.internal.IsIdInRequest


class ModelDownloader extends AutoCloseable {
  val log: Logger = LoggerFactory.getLogger(this.getClass)

  val backend: SttpBackend[Identity, Any] =
    Slf4jLoggingBackend(
      HttpURLConnectionBackend(),
      includeTiming = true,
      logRequestBody = false,
      logResponseBody = false
    )

  def ensureDir(path: Path): Unit =
    if(!Files.exists(path)) {
      Files.createDirectory(path)
    }

  def fetchAllModels(dest: Path): Unit = {
    ensureDir(dest)

    fetchPath("botocore/data") foreach { root =>
        for {
          svc <-root.tree if svc.`type` == "tree"
          svcSubTree <- fetchTree(svc.url).tree.find(_.`type` == "tree")
          vers = fetchTree(svcSubTree.url)
        } {
          val svcDir = dest resolve svc.path
          val versDir = svcDir resolve svcSubTree.path
          ensureDir(svcDir)
          ensureDir(versDir)
          val files = vers.tree.collect { case f if f.`type` == "blob" => f.path }
          files foreach { filename =>
            fetchFile(
              botoRawFileUri / "botocore" / "data" / svc.path / svcSubTree.path / filename,
              versDir resolve filename
            )
          }
        }
    }
  }

  def fetchPath(path: String) : Option[TreeRoot] =
    walkToPath(path.split('/').toList)

  private def walkToPath(parts: List[String], start: TreeRoot = fetchRoot) : Option[TreeRoot] = {
    parts match {
      case h :: t =>
        start.tree.find(x => x.`type` == "tree" && x.path == h).flatMap { x =>
          walkToPath(t, fetchTree(x.url))
        }
      case _ => Some(start)
    }
  }

  def fetchTree(uri: String): TreeRoot = fetchTree(uri"$uri")

//  inline def makeAuthedRequest[T, R, F[_], P](
//    uri: Uri,
//    as: ResponseAs[T, R],
//    backend: SttpBackend[F, P],
//  ): F[Response[T]] = { // (using P & Effect[F] <:< R)
//    (ghuser, ghtoken) match {
//      case (Some(u), Some(t)) =>
//        basicRequest
//          .get(uri)
//          .auth.basic(u, t)
//          .response(as)
//          .send(backend)
//      case _ =>
//        basicRequest
//          .get(uri)
//          .response(as)
//          .send(backend)
//    }
//  }
//
//  def makeAuthedRequest[T, R](uri: Uri, as: ResponseAs[T, R]): T =
//    makeAuthedRequest(uri, as, backend).body

  def makeAuthedRequest[T, R](
    uri: Uri,
    as: ResponseAs[T, R]
  )(using Effect[Identity] <:< R): T = {
    (ghuser, ghtoken) match {
      case (Some(u), Some(t)) =>
        basicRequest
          .get(uri)
          .auth.basic(u, t)
          .response(as)
          .send(backend)
          .body
      case _ =>
        basicRequest
          .get(uri)
          .response(as)
          .send(backend)
          .body
    }
  }

  def fetchTree(uri: Uri): TreeRoot =
    makeAuthedRequest(uri, asJson[TreeRoot].getRight)

  def fetchFile(uri: Uri, path: Path): Either[String, Path] =
    makeAuthedRequest(uri, asPath(path))

  def fetchRoot: TreeRoot = fetchTree(botoApiUri / "trees" / "master")

  override def close(): Unit = backend.close()
}
