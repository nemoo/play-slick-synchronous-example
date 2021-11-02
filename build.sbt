import scala.sys.process.Process

name := """play-slick-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.7"

routesGenerator := InjectedRoutesGenerator

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases/"
resolvers += Resolver.jcenterRepo


libraryDependencies += "com.typesafe.play" %% "play-slick" % "5.0.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "5.0.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2"
libraryDependencies += "com.h2database" % "h2" % "1.4.200"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test"
libraryDependencies += "com.github.takezoe" %% "blocking-slick-33" % "0.0.13"
libraryDependencies += specs2 % Test
libraryDependencies += guice
libraryDependencies += ehcache
libraryDependencies += "org.webjars" % "bootstrap" % "4.6.0"
libraryDependencies += "org.webjars.npm" % "bootstrap-icons" % "1.7.0"
libraryDependencies += "org.webjars" % "popper.js" % "1.12.9-1"
libraryDependencies += "org.webjars" % "jquery" % "3.5.1"
libraryDependencies += "org.webjars" % "momentjs" % "2.29.1"
libraryDependencies += "com.mohiva" %% "play-silhouette" % "7.0.0"
libraryDependencies += "com.mohiva" %% "play-silhouette-password-bcrypt" % "7.0.0"
libraryDependencies += "com.mohiva" %% "play-silhouette-persistence" % "7.0.0"
libraryDependencies += "com.mohiva" %% "play-silhouette-crypto-jca" % "7.0.0"
libraryDependencies += "net.codingwell" %% "scala-guice" % "5.0.1"
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