package models

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import util.MyPostgresDriver.blockingApi._
import play.api.Logger


/**
  * Creates test data for use during development
  */
class TestData @Inject()(taskRepo: TaskRepo, projectRepo: ProjectRepo)(protected val dbConfigProvider: DatabaseConfigProvider) extends DAO {

  val logger: Logger = Logger(this.getClass)

  def createTestData(implicit session: Session) = {
    if (projectRepo.all.length + taskRepo.all.length == 0) {
      logger.info("creating test data ")
      val p1Id = projectRepo.create("Alpha")
      logger.info("finished creating ze project ")
      projectRepo.addTask("blue", p1Id)
      projectRepo.addTask("red", p1Id)
      projectRepo.create("Beta")
      logger.info("finished creating test data ")
    }
  }
}
