package controllers

import play.api.mvc._
import zio.Console.printLine
import zio.ZIO
import zio.stream.ZPipeline
import zio.Unsafe
import zio.stream.ZStream

import javax.inject.{Inject, Singleton}

@Singleton
class JobsZioController @Inject()( val controllerComponents: ControllerComponents ) extends BaseController {

  def create(input: Int): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    zstream.write(3) //apparently such a write function does not exist
    Ok(s"enqueued input $input")
  }

  val runtime = zio.Runtime.default

  val zstream: ZStream[Any, Throwable, Int] =
    ZStream
      .map{x =>
        ZIO.attemptBlocking{
          // here some expensive blocking code is running
          Thread.sleep(1000)
          x * 2
        }
      }

  val streamApp: ZIO[Any, Throwable, Unit] =
    zstream.foreach(item => printLine(s"z  $item"))

  Unsafe.unsafe { implicit unsafe =>
    runtime.unsafe.run(streamApp).getOrThrowFiberFailure()
  }

}
