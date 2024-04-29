package models

import com.github.takezoe.slick.blocking.BlockingPostgresDriver.blockingApi._
import models.Implicits._
import play.api.cache.AsyncCacheApi
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcBackend

import javax.inject.{Inject, Singleton}



case class Task(id: Long, color: String, status: TaskStatus.Value, project: Long) {

  def patch(color: Option[String], status: Option[TaskStatus.Value], project: Option[Long]): Task =
    this.copy(color = color.getOrElse(this.color),
              status = status.getOrElse(this.status),
              project = project.getOrElse(this.project))

}

object TaskStatus extends Enumeration {
  val ready: TaskStatus.Value = Value("ready")
  val set: TaskStatus.Value = Value("set")
  val go: TaskStatus.Value = Value("go")
}

@Singleton
class TaskRepo @Inject()(cache: AsyncCacheApi)
                        (protected val dbConfigProvider: DatabaseConfigProvider) extends DAO{

  def findById(id: Long)(implicit session: JdbcBackend#Session): Task =
    Tasks.filter(_.id === id)
      .first

  def findByColor(color: String)(implicit session: JdbcBackend#Session): Option[Task] =
    Tasks.filter(_.color === color)
      .firstOption

  def findByProjectId(projectId: Long)(implicit session: JdbcBackend#Session): List[Task] =
    Tasks.filter(_.project === projectId)
      .list

  def findByReadyStatus(implicit session: JdbcBackend#Session): List[Task] =
    Tasks.filter(_.status === TaskStatus.ready)
      .list


  def partialUpdate(id: Long, color: Option[String], status: Option[TaskStatus.Value], project: Option[Long])(implicit session: JdbcBackend#Session): Int = {

    val task = findById(id)
    Tasks.filter(_.id === id)
      .update(task.patch(color, status, project))
  }

  def all(implicit session: JdbcBackend#Session): Seq[Task] =
    Tasks.list

  def insert(task: Task)(implicit session: JdbcBackend#Session): Long =
    (Tasks returning Tasks.map(_.id))
      .insert(task)

  def insertOrUpdate(task: Task)(implicit session: JdbcBackend#Session): Unit =
    (Tasks returning Tasks.map(_.id))
      .insertOrUpdate(task)

  def _deleteAllInProject(projectId: Long)(implicit session: JdbcBackend#Session): Int =
    Tasks.filter(_.project === projectId)
      .delete

}

private class TasksTable(tag: Tag) extends Table[Task](tag, "task") {

  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def color = column[String]("color")
  def status = column[TaskStatus.Value]("status")
  def project = column[Long]("project")

  def * = (id, color, status, project) <> ((Task.apply _).tupled, Task.unapply)
}

