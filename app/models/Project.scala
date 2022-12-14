package models

import javax.inject.{Inject, Singleton}

import scala.collection.mutable.ListBuffer


case class Project(id: Long, name: String)

@Singleton
class ProjectRepo @Inject()(taskRepo: TaskRepo) {

  val Projects: ListBuffer[Project] = collection.mutable.ListBuffer(
    Project(1,"A"),
    Project(2,"B"),
  )

  def findById(id: Long): Option[Project] =
    Projects.find(_.id == id)

  def findByName(name: String): List[Project] =
    Projects.filter(_.name == name)
      .toList

  def all: List[Project] =
    Projects.toList

  def create(name: String): Long = {
    val project = Project(Projects.map(_.id).max + 1, name)
    Projects += project
    project.id
  }

  def delete(id: Long): Unit = {
    val projects = findById(id)

    projects.foreach(p => taskRepo._deleteAllInProject(p.id))

    Projects.filterInPlace(_.id != id)
  }

  def addTask(color: String, projectId: Long): Long = {
    val project = findById(projectId).get
    taskRepo.insert(Task(0, color, TaskStatus.ready, project.id))
  }
}
