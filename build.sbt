// (5) shadow sbt-scalajs' crossProject and CrossType until Scala.js 1.0.0 is released
import Settings._
import sbtcrossproject.{CrossType, crossProject}


resolvers in ThisBuild += "Atlassian Releases" at "https://maven.atlassian.com/public/"

lazy val formRoot = project.in(file(".")).
  aggregate(sharedJvm, sharedJs, server, client)
  .settings(organizationSettings)
  .settings(dockerComposeSettings)
  .settings(
    publish := {}
    , publishLocal := {}
    , publishArtifact := false
    , isSnapshot := true
    , run := {
      (run in server in Compile).evaluated
    }
  ).enablePlugins(DockerComposePlugin)

lazy val server = (project in file("server"))
  .configs(E2ETest)
  .configs(FunTest)
  .settings(inConfig(E2ETest)(Defaults.testTasks): _*)
  .settings(inConfig(FunTest)(Defaults.testTasks): _*)
  .settings(scalaJSProjects := Seq(client))
  .settings(sharedSettings(Some("server")))
  .settings(serverSettings)
  .settings(serverDependencies)
  .settings(jvmSettings)
  .enablePlugins(PlayScala, BuildInfoPlugin)
  .dependsOn(sharedJvm)



lazy val client = (project in file("client"))
  .settings(sharedSettings(Some("client")))
  .settings(clientSettings)
  .settings(clientDependencies)
  .settings(jsSettings)
  .enablePlugins(ScalaJSWeb)
  .dependsOn(sharedJs)

lazy val shared = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .settings(sharedSettings())
  .settings(sharedDependencies)
  .jsSettings(jsSettings)
  .jsSettings(sharedJsDependencies) // defined in sbt-scalajs-crossproject
  .jvmSettings(jvmSettings)
  .jsConfigure(_ enablePlugins ScalaJSWeb)

lazy val sharedJvm = shared.jvm
lazy val sharedJs = shared.js
