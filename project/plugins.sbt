// Comment to get more information during initialization
//logLevel := Level.Warn


addSbtPlugin("org.playframework" % "sbt-plugin" % "3.0.6")

addSbtPlugin("com.jamesward" %% "play-auto-refresh" % "0.0.18")

addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.13")
addSbtPlugin("com.github.sbt" % "sbt-web" % "1.5.7")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.4")
addSbtPlugin("com.typesafe.sbt" % "sbt-gzip" % "1.0.2")

ThisBuild / libraryDependencySchemes ++= Seq(
  "org.scala-lang.modules" %% "scala-xml" % VersionScheme.Always
)