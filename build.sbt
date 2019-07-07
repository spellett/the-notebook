import Dependencies._

// Default settings for all projects

ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "2.12.6"
ThisBuild / organization := "com.bird"

// Projects
lazy val bird = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "bird",
    libraryDependencies ++= configDependencies,
    libraryDependencies += redis,
    libraryDependencies += scalaTest % Test,
  )