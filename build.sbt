ThisBuild / scalaVersion := "3.6.1"
ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .settings(
    name := "ec-authentication-example",
    javacOptions ++= Seq("-source", "11", "-target", "11"),
    libraryDependencies ++= Seq(
      // Jackson for JSON handling
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.15.2",
      // Testing dependencies
      "junit" % "junit" % "4.13.2" % Test,
      "com.novocode" % "junit-interface" % "0.11" % Test,
      // Logging
      "ch.qos.logback" % "logback-classic" % "1.4.11",
      "org.slf4j" % "slf4j-api" % "2.0.9"
    )
  )
