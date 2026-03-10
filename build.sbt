Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val scala3 = "3.7.3"

ThisBuild / scalaVersion := scala3
ThisBuild / crossScalaVersions := Seq(scala3)
ThisBuild / semanticdbEnabled := true

lazy val V = new {
  val circe = "0.14.15"
  val cats = "2.13.0"
  val munit = "1.2.1"
}

lazy val root =
  (project in file(".")).aggregate(lib)

lazy val lib = project.in(file("lib")).settings(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % V.cats,
    "io.circe" %% "circe-core" % V.circe,
    "io.circe" %% "circe-generic" % V.circe,
    "io.circe" %% "circe-parser" % V.circe,
    "org.scalameta" %% "munit" % V.munit % Test
  ),
  scalacOptions -= "-Xfatal-warnings"
)
