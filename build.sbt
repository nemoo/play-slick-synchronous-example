import scala.sys.process.Process

name := """play-slick-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.8"

routesGenerator := InjectedRoutesGenerator

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += Resolver.jcenterRepo


libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % "test"

libraryDependencies += specs2 % Test
libraryDependencies += guice
libraryDependencies += ehcache

libraryDependencies += "dev.zio" %% "zio"    % "2.0.2"
libraryDependencies += "dev.zio" %% "zio-streams" % "2.0.2"

//enable displaying of link to intellij in error page
javaOptions += "-Dplay.editor=http://localhost:63342/api/file/?file=%s&line=%s"