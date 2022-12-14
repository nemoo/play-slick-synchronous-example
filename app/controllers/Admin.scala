package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions._
import models.{ProjectRepo, TaskRepo}
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import util.auth.{AdminOnly, AuthEnv, User}

import scala.concurrent.ExecutionContext

class Admin @Inject()(
                       projectRepo: ProjectRepo,
                       taskRepo: TaskRepo,
                       silhouette: Silhouette[AuthEnv],
                       config: util.Config,
                       val controllerComponents: ControllerComponents
                           )
                           extends BaseController {

  def overview: Action[AnyContent] = silhouette.SecuredAction(AdminOnly) { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    implicit val user: User = request.identity

    Ok(views.html.admin(config))
  }

}
