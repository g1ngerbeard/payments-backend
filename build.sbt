
name := "payments-backend"

version := "0.1"

scalaVersion := "2.12.8"

scalacOptions in ThisBuild ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:higherKinds",
  "-Ypartial-unification"
)

val catsVersion = "1.6.0"
val scalatestVersion = "3.0.5"

libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-core" % catsVersion,
  "org.scalactic" %% "scalactic" % scalatestVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion % "test"
)