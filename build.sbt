val scala3Version = "3.7.4"

ThisBuild / scalaVersion := scala3Version
ThisBuild / organization := "io.github.nicolasfara"
ThisBuild / homepage := Some(
  url(
    "https://github.com/nicolasfara/Template-for-Scala-Multiplatform-Projects"
  )
)
ThisBuild / licenses := List(
  "Apache-2.0" -> url("https://www.apache.org/licenses/LICENSE-2.0")
)
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / developers := List(
  Developer(
    "nicolasfara",
    "Nicolas Farabegoli",
    "nicolas.farabegoli@gmail.com",
    url("https://nicolasfarabegoli.it")
  )
)
ThisBuild / scalacOptions ++= Seq(
  "-Werror",
  "-Wunused:all",
  "-Wvalue-discard",
  "-Wnonunit-statement",
  "-Yexplicit-nulls",
  "-Wsafe-init",
  "-Xcheck-macros",
  "-rewrite",
  "-indent",
  "-unchecked",
  "-explain",
  "-feature",
  "-language:strictEquality",
  "-language:implicitConversions",
)
ThisBuild / coverageEnabled := true
ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
// ThisBuild / wartremoverErrors ++= Warts.allBut(Wart.Any, Wart.Recursion)
ThisBuild / libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.19" % Test
)

lazy val root = project
  .in(file("."))
  .configs()
  .settings(
    name := "asmd-effect-systems-overview-code",
  )

lazy val stack = project
  .in(file("stack"))
  .settings(
    name := "stack",
  )
  .dependsOn(root)

lazy val mtl = project
  .in(file("mtl"))
  .settings(
    name := "mtl",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % "2.13.0",
      "org.typelevel" %% "cats-effect" % "3.7.0",
      "org.typelevel" %% "cats-mtl" % "1.4.0",
    )
  )
  .dependsOn(root)

lazy val direct = project
  .in(file("direct"))
  .settings(
    name := "direct",
    libraryDependencies ++= Seq()
  )
  .dependsOn(root)
