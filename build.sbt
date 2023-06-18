import scala.sys.process.Process

name := """play-slick-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.10"

routesGenerator := InjectedRoutesGenerator

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases/"
resolvers += Resolver.jcenterRepo


libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test"
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.40.17" % "test"
libraryDependencies += "com.dimafeng" %% "testcontainers-scala-postgresql" % "0.40.17" % "test"

libraryDependencies += "com.github.takezoe" %% "blocking-slick-33" % "0.0.13"
libraryDependencies += "org.postgresql" % "postgresql" % "42.5.3"
libraryDependencies += specs2 % Test
libraryDependencies += guice
libraryDependencies += ehcache
libraryDependencies += "org.webjars" % "bootstrap" % "5.2.2"
libraryDependencies += "org.webjars.npm" % "bootstrap-icons" % "1.8.0"
libraryDependencies += "org.webjars" % "popper.js" % "1.12.9-1"
libraryDependencies += "org.webjars" % "jquery" % "3.6.4"
libraryDependencies += "org.webjars" % "momentjs" % "2.29.1"
libraryDependencies += "io.github.honeycomb-cheesecake" %% "play-silhouette" % "7.0.4"
libraryDependencies += "io.github.honeycomb-cheesecake" %% "play-silhouette-password-bcrypt" % "7.0.4"
libraryDependencies += "io.github.honeycomb-cheesecake" %% "play-silhouette-persistence" % "7.0.4"
libraryDependencies += "io.github.honeycomb-cheesecake" %% "play-silhouette-crypto-jca" % "7.0.4"
libraryDependencies += "net.codingwell" %% "scala-guice" % "7.0.0"
libraryDependencies += "com.iheart" %% "ficus" % "1.5.1"

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