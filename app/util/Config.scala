package util

import javax.inject.Inject
import play.api.{Configuration, Logger}
import javax.inject.Singleton

@Singleton
class Config @Inject()(configuration: Configuration) {
  val logger = Logger(this.getClass())

  val sha1: String = configuration.get[String]("sha1").trim.take(7)

  val skyColor: String = configuration.get[String]("color.of.sky").trim
}