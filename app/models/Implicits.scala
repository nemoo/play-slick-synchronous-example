package models

import com.github.takezoe.slick.blocking.BlockingPostgresDriver.blockingApi._

object Implicits{
  implicit val taskStatusColumnType = MappedColumnType.base[TaskStatus.Value, String](
    _.toString, string => TaskStatus.withName(string))
}
