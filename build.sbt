name := "generateUpdated"

organization := "com.github.misberner.scala.generateUpdated"

version := "0.0.2-SNAPSHOT"

scalaVersion := "2.11.1"

crossScalaVersions := Seq("2.11.1")

scalacOptions += "-deprecation"

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

licenses := Seq("Apache License, Version 2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

homepage := Some(url("https://github.com/misberner/generateUpdated"))

pomExtra := (
  <scm>
    <url>git@github.com:misberner/generateUpdated.git</url>
    <connection>scm:git:git@github.com:misberner/generateUpdated.git</connection>
  </scm>
  <developers>
    <developer>
      <id>misberner</id>
      <name>Malte Isberner</name>
      <url>https://github.com/misberner</url>
    </developer>
  </developers>)
