
name := "Challenge"

version := "0.1"

scalaVersion := "2.12.8"

val kafkaVersion = "2.5.0"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs@_*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % kafkaVersion,
  "org.apache.kafka" % "kafka-clients" % kafkaVersion,
  "io.spray"         %%  "spray-json"  % "1.3.5",
  "org.slf4j"        % "slf4j-simple"  % "1.7.30",
  "joda-time"        % "joda-time"     % "2.10.6"

)

