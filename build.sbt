import uk.gov.hmrc.DefaultBuildSettings
import scoverage.ScoverageKeys
ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "2.13.12"

val strictBuilding: SettingKey[Boolean] = StrictBuilding.strictBuilding
StrictBuilding.strictBuildingSetting

lazy val microservice = Project("ngr-login-register-frontend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
      libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
      scalacOptions ++= {
          if (StrictBuilding.strictBuilding.value) ScalaCompilerFlags.strictScalaCompilerOptions else Nil
      },
      // https://www.scala-lang.org/2021/01/12/configuring-and-suppressing-warnings.html
      // suppress warnings in generated routes files
      scalacOptions += "-Wconf:src=routes/.*:s",
      PlayKeys.playDefaultPort := 1502,
      scalacOptions += "-Wconf:cat=unused-imports&src=html/.*:s",
      pipelineStages := Seq(gzip),
      Compile / scalacOptions -= "utf8",
      scalacOptions += "-Wconf:cat=deprecation&msg=value name in trait Retrievals is deprecated:s",  // we have confidence level > 200 so we don't need to worry about this
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(WartRemoverSettings.wartRemoverSettings: _*)
  .settings(CodeCoverageSettings.settings: _*)
  .settings(SbtUpdatesSettings.sbtUpdatesSettings *)
  .disablePlugins(JUnitXmlReportPlugin)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings())
  .settings(libraryDependencies ++= AppDependencies.it)
