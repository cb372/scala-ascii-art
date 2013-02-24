organization := "com.github.cb372"

name:= "scala-ascii-art"

scalaVersion := "2.10.0"

resolvers += "Chris Birchall's Maven repo" at "http://cb372.github.com/m2/releases"

libraryDependencies ++= Seq(
  "net.databinder.dispatch" %% "dispatch-core" % "0.9.5",
  "org.slf4j" % "slf4j-nop" % "1.6.2",
  "com.github.cb372" %% "rainbow" % "0.2"
)

seq(com.github.retronym.SbtOneJar.oneJarSettings: _*)
