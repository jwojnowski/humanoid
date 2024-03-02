// https://typelevel.org/sbt-typelevel/faq.html#what-is-a-base-version-anyway
ThisBuild / tlBaseVersion := "0.1" // your current series x.y

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

val Scala213 = "2.13.12"
ThisBuild / crossScalaVersions := Seq(Scala213, "3.3.3")
ThisBuild / scalaVersion       := Scala213 // the default Scala

lazy val core =
  project
    .in(file("core"))
    .settings(
      name := "humanoid-core",
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-core"        % "2.10.0",
        "org.scalameta" %% "munit"            % "0.7.29" % Test,
        "org.scalameta" %% "munit-scalacheck" % "0.7.29" % Test
      )
    )

lazy val uuid =
  project
    .in(file("uuid"))
    .dependsOn(core)
    .settings(
      name := "humanoid-uuid",
      libraryDependencies ++= Seq(
        "org.typelevel" %% "cats-effect"         % "3.5.3",
        "org.scalameta" %% "munit"               % "0.7.29" % Test,
        "org.typelevel" %% "munit-cats-effect-3" % "1.0.7"  % Test
      )
    )

lazy val scuid =
  project
    .in(file("scuid"))
    .dependsOn(core)
    .settings(
      name := "humanoid-scuid",
      libraryDependencies ++= Seq(
        "me.wojnowski"  %% "scuid"               % "0.2.0",
        "org.scalameta" %% "munit"               % "0.7.29" % Test,
        "org.typelevel" %% "cats-effect"         % "3.5.3"  % Test,
        "org.typelevel" %% "munit-cats-effect-3" % "1.0.7"  % Test
      )
    )

lazy val root = tlCrossRootProject.aggregate(core, scuid)
