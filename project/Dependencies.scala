import sbt._

object Dependencies {
  // Configuration management
  val configDependencies = Seq(
    "com.typesafe" % "config" % "1.3.2",
  )
  
  // Redis
  val redis = "net.debasishg" %% "redisclient" % "3.7"
  
  // Testing
  val junit = "junit" % "junit" % "4.11"
  val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
}