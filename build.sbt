import Dependencies._
import com.typesafe.sbt.packager.docker._

// Default settings for all projects

ThisBuild / version      := "0.1.0"
ThisBuild / scalaVersion := "2.12.6"
ThisBuild / organization := "com.bird"

lazy val commonSettings = Seq(
  scalacOptions ++= Seq("-Ypartial-unification"),
  libraryDependencies ++= Seq(
    configDependencies,
    loggingDependencies
  ).flatten
)

// Projects
lazy val bird = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "bird",
    commonSettings,
    libraryDependencies ++= akkaDependencies,
    libraryDependencies += redis,
    libraryDependencies += scalaTest % Test,
  )