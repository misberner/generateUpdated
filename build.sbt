name := "updated"

organization := "com.github.misberner.scalamacros"

version := "0.0.3"

scalaVersion := "2.11.2"

crossScalaVersions := Seq("2.10.4", "2.11.2")

scalacOptions += "-deprecation"

resolvers += Resolver.sonatypeRepo("releases")

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-reflect" % _)

libraryDependencies <+= scalaVersion("org.scala-lang" % "scala-compiler" % _ % "provided")


unmanagedSourceDirectories in Compile <+= (scalaVersion, sourceDirectory in Compile) {
  case (v, dir) if v startsWith "2.10." => dir / "scala-2.10"
  case (v, dir) if v startsWith "2.11." => dir / "scala-2.11"
}

addCompilerPlugin("org.scalamacros" % "paradise" % "2.0.1" cross CrossVersion.full)

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
