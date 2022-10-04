package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.routing.{ActorRefRoutee, Router, SmallestMailboxRoutingLogic}
import play.api.mvc._

import javax.inject.{Inject, Singleton}

@Singleton
class JobsAkkaSimpleController @Inject()(val controllerComponents: ControllerComponents,
                                   actorSystem: ActorSystem) extends BaseController {

  class JobExecutor @Inject() extends Actor {
    override def receive: Receive = {
      case id: Int =>
        println(s"start " + "  " * id + id)
        Thread.sleep(3000)
      //      println(s"end   " + "  " * id + id)
    }
  }

  val actor: ActorRef = actorSystem.actorOf(Props(new JobExecutor))

  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    for (i <- 1 to 8 ) actor ! i
    Ok(s"enqueued jobs")
  }
}




