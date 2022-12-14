package models

import javax.inject.Inject


/**
  * Creates test data for use during development
  */
class TestData @Inject()(taskRepo: TaskRepo, projectRepo: ProjectRepo) {

  def createTestData(): Unit = {
    if (projectRepo.all.length + taskRepo.all.length == 0) {
      val p1Id = projectRepo.create("Alpha")
      projectRepo.addTask("blue", p1Id)
      projectRepo.addTask("red", p1Id)
      projectRepo.create("Beta")
    }
  }
}
