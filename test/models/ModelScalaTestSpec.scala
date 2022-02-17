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

import scala.concurrent.Future


class ModelScalaTestSpec extends PlaySpec with GuiceOneAppPerTest with ForEachTestContainer {

  var db: Database = _
  var projectRepo: ProjectRepo = _

  override def container: Container = PostgreSQLContainer(dockerImageNameOverride = DockerImageName.parse("postgres:14"))

  override def fakeApplication(): Application = {
    val app = super.fakeApplication()
    db = app.injector.instanceOf[DatabaseConfigProvider].get[JdbcProfile].db
    projectRepo = app.injector.instanceOf[ProjectRepo]
    val lifecycle = app.injector.instanceOf[ApplicationLifecycle]
    lifecycle.addStopHook(() => Future.successful(
      Evolutions.cleanupEvolutions(app.injector.instanceOf[DBApi].database("default"))
    ))
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
}
