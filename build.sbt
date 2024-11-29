ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.13.12"

val AkkaVersion = "2.8.0"
val AkkaHttpVersion = "10.5.0"
val Slf4jVersion = "2.0.9"  // Updated to match your current slf4j-api version
val LogbackVersion = "1.4.11" // Updated to be compatible with slf4j 2.0.9

lazy val root = (project in file("."))
  .settings(
    name := "LLM-Conversational-Agent",

    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
      "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
      "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion,
      "io.github.ollama4j" % "ollama4j" % "1.0.79",
      "com.typesafe" % "config" % "1.4.2",

      // Updated logging dependencies with compatible versions
      "org.slf4j" % "slf4j-api" % Slf4jVersion,
      "ch.qos.logback" % "logback-classic" % LogbackVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
      "com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,

      // Testing dependencies
      "org.scalatest" %% "scalatest" % "3.2.15" % Test,
      "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
    ),

    Compile / mainClass := Some("Main")
  )

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("application.conf") => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x if x.endsWith("module-info.class") => MergeStrategy.discard
  case x if x.endsWith(".conf") => MergeStrategy.concat
  case _ => MergeStrategy.first
}

assembly / mainClass := Some("Main")
assembly / assemblyJarName := "llm-conversation-agent.jar"