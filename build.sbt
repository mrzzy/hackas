
scalaVersion := "2.13.3"

// project metadata
name := "hackas"
organization := "co.mrzzy"
version := "1.0"

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
      Defaults.itSettings,
      libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "it,test"
  )
