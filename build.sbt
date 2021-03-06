name := """play-websocket-sse-experiments"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.2"


libraryDependencies ++= Seq(
  guice,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.0.0" % Test,
  "com.typesafe.akka" %% "akka-stream" % "2.5.3",
  "com.github.javafaker" % "javafaker" % "0.13",
  "com.lightbend.play" %% "play-socket-io" % "1.0.0-beta-2",
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % Provided,
  "com.lightbend.akka" %% "akka-stream-alpakka-amqp" % "0.11"


)


// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
