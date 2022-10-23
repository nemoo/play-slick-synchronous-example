package controllers

import javax.inject.{Inject, Singleton}
import models.{ProjectRepo, TaskRepo}
import play.api.mvc._
import com.github.takezoe.slick.blocking.BlockingPostgresDriver.blockingApi._
import com.mohiva.play.silhouette
import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions._
import play.Environment
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import util.{Config, WeatherService}
import util.auth.{AuthEnv, User}

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class Application @Inject()(
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

  def test: Action[AnyContent] = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok("test")
  }

  def addTaskToProject(color: String, projectId: Long): Action[AnyContent] = silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    db.withSession { implicit session =>
      projectRepo.addTask(color, projectId)
      Redirect(routes.Application.projects(projectId))
    }
  }

  def modifyTask(taskId: Long, color: Option[String]): Action[AnyContent] = silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
      db.withSession { implicit session =>

        val updatedRows = taskRepo.partialUpdate(taskId, color, None, None)

        Ok(s"Rows affected : $updatedRows")
      }
  }

  def createProject(name: String): Action[AnyContent] = silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    db.withSession { implicit session =>
      val id = projectRepo.create(name)
      Ok(s"project $id created")
    }
  }

  def listProjects: Action[AnyContent] = silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    db.withSession { implicit session =>
      implicit val user: User = request.identity

      val temperature = weather.forecast("NYC")
      val projects = projectRepo.all
       Ok(views.html.projects(projects, temperature, config))
    }
  }

  def projects(id: Long): Action[AnyContent] = silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    db.withSession { implicit session =>
      implicit val user: User = request.identity

      val project =  projectRepo.findById(id).get
      val tasks = taskRepo.findByProjectId(id)
      Ok(views.html.project(project, tasks, config))
    }
  }

  def delete(id: Long): Action[AnyContent] = silhouette.SecuredAction { implicit request: SecuredRequest[AuthEnv, AnyContent] =>
    db.withTransaction { implicit session =>
      projectRepo.delete(id)
      Redirect(routes.Application.listProjects)
    }
  }

}
