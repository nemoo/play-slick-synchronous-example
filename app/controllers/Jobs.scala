package controllers

import com.github.takezoe.slick.blocking.BlockingPostgresDriver.blockingApi._
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions._
import models.{ProjectRepo, TaskRepo}
import play.Environment
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import slick.jdbc.JdbcProfile
import util.auth.{AuthEnv, User}
import util.{Config, WeatherService}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class Jobs @Inject()(
                             projectRepo: ProjectRepo,
                             taskRepo: TaskRepo,
                             weather: WeatherService,
                             silhouette: Silhouette[AuthEnv],
                             val controllerComponents: ControllerComponents,
                             config: Config,
                             env: Environment
                           )(protected val dbConfigProvider: DatabaseConfigProvider,
                             val ex: ExecutionContext )
                           extends BaseController {
                             
  

  val db = dbConfigProvider.get[JdbcProfile].db

  def create(jobDescription: String): Action[AnyContent] = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(s"creating job $jobDescription")
  }
}
