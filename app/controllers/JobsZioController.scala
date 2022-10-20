package controllers

import play.api.mvc._
import zio.Console.printLine
import zio.{Chunk, Schedule, ZIO, durationInt}

import java.io.{InputStream, OutputStream}
import scala.collection.mutable
//import play.api.mvc._
import zio.Unsafe
import zio.stream.ZStream

import javax.inject.{Inject, Singleton}

@Singleton
class JobsZioController @Inject()( val controllerComponents: ControllerComponents ) extends BaseController {

  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    for (i <- 1 to 8) println(s"I want to put item $i into the zio stream")
    Ok(s"enqueued zio jobs")
  }

  val runtime = zio.Runtime.default

  val zstream: ZStream[Any, Throwable, Int] =
    ZStream
      .fromIterator((1 to 8).iterator)
      .mapZIOPar(2){x =>
        ZIO.attemptBlocking{
          Thread.sleep(1000)
          x
        }
      }

  val streamApp: ZIO[Any, Throwable, Unit] =
    zstream.foreach(item => printLine(s"z  $item"))

  Unsafe.unsafe { implicit unsafe =>
    runtime.unsafe.run(streamApp).getOrThrowFiberFailure()
  }

}
