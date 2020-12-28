package controllers

import javax.inject.Inject

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions._
import models.{ProjectRepo, TaskRepo}
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import slick.jdbc.JdbcProfile
import utils.{AdminOnly, AuthEnv}

import scala.concurrent.ExecutionContext

class Admin @Inject()(
                             projectRepo: ProjectRepo,
                             taskRepo: TaskRepo,
                             silhouette: Silhouette[AuthEnv],
                             val controllerComponents: ControllerComponents
                           )(protected val dbConfigProvider: DatabaseConfigProvider,
                             val ex: ExecutionContext )
                           extends BaseController {

  val db = dbConfigProvider.get[JdbcProfile].db

  def overview = silhouette.SecuredAction(AdminOnly) { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    implicit val user = request.identity

    Ok(views.html.admin())
  }

}
