package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.routing.{ActorRefRoutee, Router, SmallestMailboxRoutingLogic}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents, Request}
import javax.inject.{Inject, Singleton}

@Singleton
class JobsAkkaController @Inject()(val controllerComponents: ControllerComponents,
                                   actorSystem: ActorSystem) extends BaseController {

  val routerActor: ActorRef = actorSystem.actorOf(Props(new JobRouterActor))
  def create(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    for (i <- 1 to 8 ) routerActor ! i
    Ok(s"enqueued jobs")
  }
}

/**
  * dispatches incoming jobs to n actors, making sure that only n jobs are being processed at a time
  */
class JobRouterActor @Inject() extends Actor {
  val parallelActors = 3
  val jobRouter: Router = Router(
    SmallestMailboxRoutingLogic(),
    for (_ <- 1 to parallelActors) yield ActorRefRoutee(context.actorOf(Props(new JobExecutor)))
  )
  override def receive: Receive = { case id => jobRouter.route(id, sender()) }
}

class JobExecutor @Inject() extends Actor {
  override def receive: Receive = {
    case id: Int =>
      println(s"start " + "  " * id + id)
      Thread.sleep(3000)
//      println(s"end   " + "  " * id + id)
  }
}



