import scala.collection.Seq
import scala.sys.process.Process

name := """play-slick-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.12"

routesGenerator := InjectedRoutesGenerator

libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.1.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.1.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.0" % "test"
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.40.15" % "test"
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.40.15" % "test"

libraryDependencies += "com.github.takezoe" %% "blocking-slick" % "0.0.14"
libraryDependencies += "org.postgresql" % "postgresql" % "42.5.3"
libraryDependencies += specs2 % Test
libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += caffeine
libraryDependencies += "org.webjars" % "bootstrap" % "5.3.2"
libraryDependencies += "org.webjars.npm" % "bootstrap-icons" % "1.10.5"
libraryDependencies += "org.webjars" % "popper.js" % "2.9.3"
libraryDependencies += "org.webjars" % "jquery" % "3.6.4"
libraryDependencies += "org.webjars" % "momentjs" % "2.29.1"
libraryDependencies += "org.playframework.silhouette" %% "play-silhouette" % "10.0.0"
libraryDependencies += "org.playframework.silhouette" %% "play-silhouette-password-bcrypt" % "10.0.0"
libraryDependencies += "org.playframework.silhouette" %% "play-silhouette-persistence" % "10.0.0"
libraryDependencies += "org.playframework.silhouette" %% "play-silhouette-crypto-jca" % "10.0.0"
libraryDependencies += "net.codingwell" %% "scala-guice" % "6.0.0"
libraryDependencies += "com.iheart" %% "ficus" % "1.5.2"

ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)

val generateSha1: Unit =  {
  val sha1 = try {
    Process("git rev-parse HEAD").!!.linesIterator.toList.head
  } catch {
    case e: Exception => println("Could not extract sha1 hash, you need to install command line git."); "???"
  }
  IO.write(file("conf/version.conf"), s"""sha1="$sha1"""")
}

//enable displaying of link to intellij in error page
javaOptions += "-Dplay.editor=http://localhost:63342/api/file/?file=%s&line=%s"
