package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import play.api.mvc._

import javax.inject.{Inject, Singleton}

@Singleton
class JobsAkkaSimpleController @Inject()(val controllerComponents: ControllerComponents,
                                   actorSystem: ActorSystem) extends BaseController {

  val actor: ActorRef = actorSystem.actorOf(Props(new JobExecutor))

  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    for (i <- 1 to 8 ) actor ! i
    Ok(s"enqueued jobs")
  }

  class JobExecutor extends Actor {
    override def receive: Receive = {
      case id: Int =>
        println(s"a " + "  " * id + id)
        Thread.sleep(1000)
    }
  }

}



