name := """play-slick-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.4"

routesGenerator := InjectedRoutesGenerator

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
//resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases/"
resolvers += Resolver.jcenterRepo


libraryDependencies += "com.typesafe.play" %% "play-slick" % "4.0.2"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "4.0.2"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.9.2"
libraryDependencies += "com.h2database" % "h2" % "1.4.192"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % "test"
libraryDependencies += "com.github.takezoe" %% "blocking-slick-33" % "0.0.13"
libraryDependencies += specs2 % Test
libraryDependencies += guice
libraryDependencies += ehcache
libraryDependencies += "com.mohiva" %% "play-silhouette" % "6.1.1"
libraryDependencies += "com.mohiva" %% "play-silhouette-password-bcrypt" % "6.1.1"
libraryDependencies += "com.mohiva" %% "play-silhouette-persistence" % "6.1.1"
libraryDependencies += "com.mohiva" %% "play-silhouette-crypto-jca" % "6.1.1"
libraryDependencies += "net.codingwell" %% "scala-guice" % "4.2.6"
libraryDependencies += "com.iheart" %% "ficus" % "1.4.7"



