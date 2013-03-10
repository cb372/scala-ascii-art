import sbt._
import Keys._

object AAProject extends Build {

  lazy val root = Project (
    id = "scala-ascii-art",
    base = file ("."),
    settings = Defaults.defaultSettings ++ Seq (
      organization := "com.github.cb372",
      name := "scala-ascii-art",
      version := "0.1",
      scalaVersion := "2.10.0",
      scalacOptions ++= Seq("-deprecation"),
      libraryDependencies ++= Seq(
        "net.databinder.dispatch" %% "dispatch-core" % "0.9.5",
        "org.slf4j" % "slf4j-nop" % "1.6.2",
        "com.github.cb372" %% "rainbow" % "0.2",
        "org.scala-sbt" % "command" % "0.12.2"
      ),
      resolvers += "Chris Birchall's Maven repo" at "http://cb372.github.com/m2/releases",
      publishTo := Some(Resolver.file("file",  new File( "../cb372.github.com/m2/releases" )))
    )
  )
}

