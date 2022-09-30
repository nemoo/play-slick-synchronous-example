package controllers

import play.api.mvc._
import zio.Console.printLine
import zio.{Schedule, durationInt}
//import play.api.mvc._
import zio.Unsafe
import zio.stream.ZStream

import javax.inject.{Inject, Singleton}

@Singleton
class JobsZio @Inject()(
  val controllerComponents: ControllerComponents,
  )
  extends BaseController {

  val jobsIteratorOld: Iterator[String] = new Iterator[String] {
    var count = 0
    override def hasNext: Boolean = count <= 2
    override def next(): String = {
      count += 1
      "yes"
    }
  }

  val jobsQueue = scala.collection.mutable.Queue[String]()

  val jobsIterator: Iterator[String] = new Iterator[String] {
//    override def hasNext: Boolean = jobsQueue.nonEmpty
    override def hasNext: Boolean = true
//    override def next(): String = jobsQueue.dequeue()
    override def next(): String = "ja"
  }


  val runtime = zio.Runtime.default

//  val zioStandardApp: ZIO[Any, IOException, Unit] =
//    zio.Console.printLine("Hello, World 2!!!")
//      .repeatN(1)
//
//  Unsafe.unsafe { implicit unsafe =>
//    runtime.unsafe.run(zioStandardApp).getOrThrowFiberFailure()
//  }

//  val streamApp: ZStream[Any, Throwable, String] =
//    ZStream
//      .fromIterator(jobsIterator)
//      .schedule(Schedule.spaced(1.second))
//
//  Unsafe.unsafe { implicit unsafe =>
//    runtime.unsafe.run(streamApp.foreach(printLine(_))).getOrThrowFiberFailure()
//  }

  def create(jobDescription: String): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    jobsQueue.addOne(jobDescription)
    Ok(s"creating job $jobDescription")
  }
}
