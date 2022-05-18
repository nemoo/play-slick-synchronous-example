package util

import com.github.takezoe.slick.blocking.BlockingJdbcProfile
import com.github.tminglei.slickpg._

//trait MyPostgresDriver extends ExPostgresProfile with PgDate2Support {
trait MyPostgresDriver extends BlockingJdbcProfile with PgDate2Support {

  override val api = new API with DateTimeImplicits {}
}

object MyPostgresDriver extends MyPostgresDriver