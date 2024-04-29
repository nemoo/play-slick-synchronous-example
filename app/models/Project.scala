package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio
import slick.dbio.Effect.Read
import com.github.takezoe.slick.blocking.BlockingPostgresDriver.blockingApi._
import slick.jdbc.JdbcBackend


case class Project(id: Long, name: String)

@Singleton
class ProjectRepo @Inject()(taskRepo: TaskRepo)(protected val dbConfigProvider: DatabaseConfigProvider) extends DAO {

  def findById(id: Long)(implicit session: JdbcBackend#Session): Option[Project] =
    Projects.filter(_.id === id)
      .firstOption

  def findByName(name: String)(implicit session: JdbcBackend#Session): List[Project] =
    Projects.filter(_.name === name)
      .list

  def all(implicit session: JdbcBackend#Session): List[Project] =
    Projects.list

  def create(name: String)(implicit session: JdbcBackend#Session): Long = {
    val project = Project(0, name)
    (Projects returning Projects.map(_.id))
      .insert(project)
  }

  def delete(id: Long)(implicit session: JdbcBackend#Session): Unit = {
    val projects = findById(id)

    projects.map(p => taskRepo._deleteAllInProject(p.id))

    Projects.filter(_.id === id)
      .delete
  }

  def addTask(color: String, projectId: Long)(implicit session: JdbcBackend#Session): Long = {
    val project = findById(projectId).get
    taskRepo.insert(Task(0, color, TaskStatus.ready, project.id))
  }
}

class ProjectsTable(tag: Tag) extends Table[Project](tag, "project") {

  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def name = column[String]("name")

  def * = (id, name) <> (Project.tupled, Project.unapply)
  def ? = (id.?, name.?).shaped.<>({ r => import r._; _1.map(_ => Project.tupled((_1.get, _2.get))) }, (_: Any) => throw new Exception("Inserting into ? projection not supported."))
}