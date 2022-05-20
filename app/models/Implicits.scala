package models

import util.MyPostgresDriver.blockingApi._

object Implicits{
  implicit val taskStatusColumnType = MappedColumnType.base[TaskStatus.Value, String](
    _.toString, string => TaskStatus.withName(string))
}
