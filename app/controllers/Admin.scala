package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions._
import models.{ProjectRepo, TaskRepo}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import slick.jdbc.JdbcProfile
import util.auth.{AdminOnly, AuthEnv, User}

import scala.concurrent.ExecutionContext

class Admin @Inject()(
                       projectRepo: ProjectRepo,
                       taskRepo: TaskRepo,
                       silhouette: Silhouette[AuthEnv],
                       config: util.Config,
                       val controllerComponents: ControllerComponents
                           )(protected val dbConfigProvider: DatabaseConfigProvider,
                             val ex: ExecutionContext )
                           extends BaseController {

  val db = dbConfigProvider.get[JdbcProfile].db

  def overview: Action[AnyContent] = silhouette.SecuredAction(AdminOnly) { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    implicit val user: User = request.identity

    Ok(views.html.admin(config))
  }

}
