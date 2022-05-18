package models

import com.github.takezoe.slick.blocking.BlockingPostgresDriver.blockingApi._
import models.Implicits._
import play.api.cache.AsyncCacheApi
import play.api.db.slick.DatabaseConfigProvider
import util.DateFormat

import java.time.OffsetDateTime
import javax.inject.{Inject, Singleton}



case class Task(
                 id: Long,
                 color: String,
                 status: TaskStatus.Value,
                 project: Long,
                 lastModification: OffsetDateTime
               ) {

  def patch(color: Option[String], status: Option[TaskStatus.Value], project: Option[Long]): Task =
    this.copy(color = color.getOrElse(this.color),
      status = status.getOrElse(this.status),
      project = project.getOrElse(this.project),
      lastModification = OffsetDateTime.now())
  def lastModificationFormatted: String = DateFormat.humanReadable.format(lastModification)
}

object TaskStatus extends Enumeration {
  val ready: TaskStatus.Value = Value("ready")
  val set: TaskStatus.Value = Value("set")
  val go: TaskStatus.Value = Value("go")
}

@Singleton
class TaskRepo @Inject()(cache: AsyncCacheApi)
                        (protected val dbConfigProvider: DatabaseConfigProvider) extends DAO{

  def findById(id: Long)(implicit session: Session): Task =
    Tasks.filter(_.id === id)
      .first

  def findByColor(color: String)(implicit session: Session): Option[Task] =
    Tasks.filter(_.color === color)
      .firstOption

  def findByProjectId(projectId: Long)(implicit session: Session): List[Task] =
    Tasks.filter(_.project === projectId)
      .list

  def findByReadyStatus(implicit session: Session): List[Task] =
    Tasks.filter(_.status === TaskStatus.ready)
      .list


  def partialUpdate(id: Long, color: Option[String], status: Option[TaskStatus.Value], project: Option[Long])(implicit session: Session): Int = {

    val task = findById(id)
    Tasks.filter(_.id === id)
      .update(task.patch(color, status, project))
  }

  def all(implicit session: Session): Seq[Task] =
    Tasks.list

  def insert(task: Task)(implicit session: Session): Long =
    (Tasks returning Tasks.map(_.id))
      .insert(task)

  def insertOrUpdate(task: Task)(implicit session: Session): Unit =
    (Tasks returning Tasks.map(_.id))
      .insertOrUpdate(task)

  def _deleteAllInProject(projectId: Long)(implicit session: Session): Int =
    Tasks.filter(_.project === projectId)
      .delete

}

private class TasksTable(tag: Tag) extends Table[Task](tag, "task") {

  def id = column[Long]("id", O.AutoInc, O.PrimaryKey)
  def color = column[String]("color")
  def status = column[TaskStatus.Value]("status")
  def project = column[Long]("project")
  def lastModification: Rep[OffsetDateTime] = column[OffsetDateTime]("last_modification")

  def * = (id, color, status, project, lastModification) <> (Task.tupled, Task.unapply)
}

