package models

import com.dimafeng.testcontainers.{Container, ForEachTestContainer, PostgreSQLContainer}
import com.github.takezoe.slick.blocking.BlockingPostgresDriver.blockingApi._
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.play._
import org.scalatestplus.play.guice.GuiceOneAppPerTest
import org.testcontainers.utility.DockerImageName
import play.api.Application
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.ApplicationLifecycle
import slick.jdbc.JdbcProfile

import java.time.OffsetDateTime
import scala.concurrent.Future


class ModelScalaTestSpec extends PlaySpec with GuiceOneAppPerTest with ForEachTestContainer {

  var db: Database = _
  var projectRepo: ProjectRepo = _
  var taskRepo: TaskRepo = _

  override val container: PostgreSQLContainer = PostgreSQLContainer(dockerImageNameOverride = DockerImageName.parse("postgres:14"))

  override def fakeApplication(): Application = {
    val app = TestContainersPostgreSQLApplicationFactory.produceApplication(container)
    db = app.injector.instanceOf[DatabaseConfigProvider].get[JdbcProfile].db
    projectRepo = app.injector.instanceOf[ProjectRepo]
    taskRepo = app.injector.instanceOf[TaskRepo]
    app
  }

  "An item " should {
    "be inserted during the first test case" in {
      db.withSession { implicit session =>
        projectRepo.create("A")
        val result = projectRepo.all

        result mustBe List(Project(1, "A"))
      }
    }

    "not exist in the second test case" in {
      db.withSession { implicit session =>
        val result = projectRepo.all

        result mustBe List.empty
      }
    }
  }

  "An timestamp " should {
    "be persisted correctly" in {
      db.withSession { implicit session =>
        val projectId = projectRepo.create("A")

        val timestamp = OffsetDateTime.now()
        val taskId = taskRepo.insert(Task(0, "blue", TaskStatus.ready, projectId, timestamp))
        taskRepo.findById(taskId).lastModification mustBe timestamp
      }
    }
  }
}
