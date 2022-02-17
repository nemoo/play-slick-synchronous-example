package models


import com.dimafeng.testcontainers.PostgreSQLContainer
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder


object TestContainersPostgreSQLApplicationFactory {

  /**
   * this application factory creates a fake application that expects a PostgreSQL container to be running
   * of what is configured in application.conf
   */
  def produceApplication(container: PostgreSQLContainer) : Application = produceBaseApplicationBuilder(container).build()

  /**
   *
   * @return GuiceApplicationBuilder with mysql testcontainer configured. You can add further configurations like this:
   *         .configure("my.config.entry" -> "code15")
   *         To retrieve the Application, call .build()
   */
  def produceBaseApplicationBuilder(container: PostgreSQLContainer) : GuiceApplicationBuilder = new GuiceApplicationBuilder()
    .configure("slick.dbs.default.profile" -> "slick.jdbc.PostgresProfile$")
    .configure("slick.dbs.default.db.driver" -> container.driverClassName)
    .configure("slick.dbs.default.db.url" -> container.jdbcUrl)
    .configure("slick.dbs.default.db.user" -> container.username)
    .configure("slick.dbs.default.db.password" -> container.password)
    .configure("play.evolutions.enabled" -> "true")
    .configure("play.evolutions.autoApply" -> "true")
}
