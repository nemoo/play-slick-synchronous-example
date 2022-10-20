package controllers

import play.api.mvc._
import zio.Console.printLine
import zio.ZIO

import scala.collection.mutable
//import play.api.mvc._
import zio.Unsafe
import zio.stream.ZStream

import javax.inject.{Inject, Singleton}

@Singleton
class JobsZioOldController @Inject()(val controllerComponents: ControllerComponents ) extends BaseController {

  val jobsIteratorOld: Iterator[String] = new Iterator[String] {
    var count = 0
    override def hasNext: Boolean = count <= 2
    override def next(): String = {
      count += 1
      "yes"
    }
  }

  val jobsQueue: mutable.Queue[String] = scala.collection.mutable.Queue[String]()

  val jobsIterator: Iterator[String] = new Iterator[String] {
//    override def hasNext: Boolean = jobsQueue.nonEmpty
    override def hasNext: Boolean = true
//    override def next(): String = jobsQueue.dequeue()
    override def next(): String = "ja"
  }


  val runtime = zio.Runtime.default

//  val zioStandardApp =
//    zio.Console.printLine("Hello!")
//      .repeatN(1)
//
//  Unsafe.unsafe { implicit unsafe =>
//    runtime.unsafe.run(zioStandardApp).getOrThrowFiberFailure()
//  }

  val zstream: ZStream[Any, Throwable, Int] =
    ZStream
      .fromIterator((1 to 8).iterator)
//      .schedule(Schedule.spaced(1.second))
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

  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    for (i <- 1 to 8) jobsQueue.addOne(i.toString)
    Ok(s"enqueued zio jobs")
  }
}
