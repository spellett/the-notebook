import sbt._

object Dependencies {
  
  // Cache
  val redis = "net.debasishg" %% "redisclient" % "3.7"

  // Configuration management
  val configDependencies = Seq(
    "com.typesafe" % "config" % "1.3.2",
  )

  // JSON
  lazy val circeVersion = "0.11.1"

  val jsonDependencies = Seq(
    "io.circe" %% "circe-core" % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-generic-extras" % circeVersion,
    "io.circe" %% "circe-parser" % circeVersion
  )

  // Logging
  val logbackVersion = "1.2.3"
  val scalaLoggingVersion = "3.9.2"
  val slf4jVersion = "1.7.28"

  val loggingDependencies = Seq(
    "ch.qos.logback" % "logback-classic" % logbackVersion,
    "ch.qos.logback" % "logback-core" % logbackVersion,
    "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion,
    "org.slf4j" % "slf4j-api" % slf4jVersion
  )
  
  // Testing
  val junit = "junit" % "junit" % "4.11"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
}