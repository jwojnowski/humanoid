// https://typelevel.org/sbt-typelevel/faq.html#what-is-a-base-version-anyway
ThisBuild / tlBaseVersion := "0.3"

ThisBuild / organization     := "me.wojnowski"
ThisBuild / organizationName := "Jakub Wojnowski"
ThisBuild / startYear        := Some(2023)
ThisBuild / licenses         := Seq(License.MIT)
ThisBuild / developers       := List(
  // your GitHub handle and name
  tlGitHubDev("jwojnowski", "Jakub Wojnowski")
)

// publish to s01.oss.sonatype.org (set to true to publish to oss.sonatype.org instead)
ThisBuild / tlSonatypeUseLegacyHost    := false
ThisBuild / tlCiReleaseBranches        := Seq("main")
ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec.temurin("21"))

val Scala213 = "2.13.15"
ThisBuild / crossScalaVersions := Seq(Scala213, "3.5.2")
ThisBuild / scalaVersion       := Scala213 // the default Scala

lazy val core =
  project
    .in(file("core"))
    .settings(
      name := "humanoid-core",
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-core"        % "2.12.0",
        "org.scalameta" %% "munit"            % "1.0.2" % Test,
        "org.scalameta" %% "munit-scalacheck" % "1.0.0" % Test
      )
    )

lazy val uuid =
  project
    .in(file("uuid"))
    .dependsOn(core)
    .settings(
      name := "humanoid-uuid",
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-effect"       % "3.5.5",
        "org.scalameta" %% "munit"             % "1.0.2" % Test,
        "org.typelevel" %% "munit-cats-effect" % "2.0.0" % Test
      )
    )

lazy val scuid =
  project
    .in(file("scuid"))
    .dependsOn(core)
    .settings(
      name := "humanoid-scuid",
      libraryDependencies ++= Seq(
        "me.wojnowski"  %% "scuid"             % "0.2.0",
        "org.scalameta" %% "munit"             % "1.0.2" % Test,
        "org.typelevel" %% "cats-effect"       % "3.5.5" % Test,
        "org.typelevel" %% "munit-cats-effect" % "2.0.0" % Test
      )
    )

lazy val circe =
  project
    .in(file("circe"))
    .dependsOn(core % "compile->compile;test->test")
    .settings(
      name := "humanoid-circe",
      libraryDependencies ++= Seq(
        "io.circe"      %% "circe-core"   % "0.14.10",
        "io.circe"      %% "circe-parser" % "0.14.10" % Test,
        "org.scalameta" %% "munit"        % "1.0.2"   % Test
      )
    )

lazy val tapir =
  project
    .in(file("tapir"))
    .dependsOn(core % "compile->compile;test->test")
    .settings(
      name := "humanoid-tapir",
      libraryDependencies ++= Seq(
        "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.11.9",
        "org.scalameta"               %% "munit"      % "1.0.2" % Test
      )
    )

lazy val root = tlCrossRootProject.aggregate(core, scuid, uuid, circe, tapir)
