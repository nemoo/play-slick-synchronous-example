package models

import com.github.takezoe.slick.blocking.BlockingPostgresDriver.blockingApi._
import slick.ast.BaseTypedType
import slick.jdbc.JdbcType

object Implicits{
  implicit val taskStatusColumnType: JdbcType[TaskStatus.Value] with BaseTypedType[TaskStatus.Value] = MappedColumnType.base[TaskStatus.Value, String](
    _.toString, string => TaskStatus.withName(string))
}
