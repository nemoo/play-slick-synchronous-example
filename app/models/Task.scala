package models

import play.api.cache.AsyncCacheApi

import javax.inject.{Inject, Singleton}
import scala.collection.mutable.ListBuffer



case class Task(id: Long, color: String, status: TaskStatus.Value, project: Long)

object TaskStatus extends Enumeration {
  val ready: TaskStatus.Value = Value("ready")
  val set: TaskStatus.Value = Value("set")
  val go: TaskStatus.Value = Value("go")
}

@Singleton
class TaskRepo @Inject()(cache: AsyncCacheApi) {

  val Tasks: ListBuffer[Task] = collection.mutable.ListBuffer(
    Task(1, "blue", TaskStatus.ready, 0),
    Task(2, "green", TaskStatus.set, 1),
  )

  def findById(id: Long): Task =
    Tasks.filter(_.id == id)
      .head

  def findByColor(color: String): Option[Task] =
    Tasks.find(_.color == color)

  def findByProjectId(projectId: Long): List[Task] =
    Tasks.filter(_.project == projectId)
      .toList

  def findByReadyStatus: List[Task] =
    Tasks.filter(_.status == TaskStatus.ready)
      .toList

  def all: Seq[Task] =
    Tasks.toSeq

  def insert(task: Task): Long ={
    val newTask = task.copy(id = Tasks.map(_.id).max + 1)
    Tasks += newTask
    newTask.id
  }

  def _deleteAllInProject(projectId: Long): Unit =
    Tasks.filterInPlace(_.project == projectId)
}


