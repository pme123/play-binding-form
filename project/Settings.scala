import com.tapad.docker.DockerComposePlugin.autoImport.composeFile
import com.typesafe.sbt.digest.Import.{DigestKeys, digest}
import com.typesafe.sbt.gzip.Import.gzip
import com.typesafe.sbt.packager.docker.Cmd
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport._
import com.typesafe.sbt.packager.linux.LinuxPlugin.autoImport.defaultLinuxInstallLocation
import com.typesafe.sbt.web.Import.Assets
import com.typesafe.sbt.web.SbtWeb.autoImport.pipelineStages
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._
import org.scalajs.sbtplugin.ScalaJSPlugin.AutoImport.jsDependencies
import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import org.scalajs.sbtplugin.Stage
import play.sbt.PlayImport.{filters, guice, ws}
import sbt.Keys._
import sbt.{Def, ExclusionRule, URL, _}
import sbtbuildinfo.BuildInfoPlugin.autoImport._
import scoverage.ScoverageSbtPlugin.autoImport._
import webscalajs.WebScalaJS.autoImport.scalaJSPipeline

object Settings {

  lazy val orgId = "pme123"
  lazy val orgHomepage = Some(new URL("https://github.com/pme123"))
  lazy val projectName = "play-binding-form"
  lazy val projectV = "0.0.2"

  // main versions
  lazy val scalaV = "2.12.6"
  lazy val bindingV = "11.5.0"
  lazy val jQueryV = "2.2.4"
  lazy val sloggingV = "0.6.1"
  lazy val semanticV = "2.4.1"
  lazy val silhouetteV = "5.0.5"
  lazy val doobieV = "0.5.3"
  lazy val scalaTestV = "3.0.5"

  lazy val organizationSettings: Seq[Def.Setting[_]] = Seq(
    organization := orgId
    , organizationHomepage := orgHomepage
  )

  lazy val testStage: Stage = sys.props.get("testOpt").map {
    case "full" => FullOptStage
    case "fast" => FastOptStage
  }.getOrElse(FastOptStage)

  lazy val FunTest = config("it") extend Test
  lazy val E2ETest = config("e2e") extend Test

  def funTestFilter(name: String): Boolean = name endsWith "Spec"

  def e2eTestFilter(name: String): Boolean = name endsWith "E2E"

  def unitTestFilter(name: String): Boolean = name endsWith "Test"

  lazy val serverSettings: Seq[Def.Setting[_]] = Def.settings(
    scalacOptions += "-Ypartial-unification",
    buildInfoSettings,
    pipelineStages in Assets := Seq(scalaJSPipeline),
    pipelineStages := Seq(digest, gzip),
    testOptions in Test := Seq(Tests.Filter(unitTestFilter)),
    testOptions in FunTest := Seq(Tests.Filter(funTestFilter)),
    testOptions in E2ETest := Seq(Tests.Filter(e2eTestFilter)),
    // triggers scalaJSPipeline when using compile or continuous compilation
    compile in Compile := ((compile in Compile) dependsOn scalaJSPipeline).value,
    // to have routing also in ScalaJS
    // Create a map of versioned assets, replacing the empty versioned.js
    DigestKeys.indexPath := Some("javascripts/versioned.js"),
    // Assign the asset index to a global versioned var
    DigestKeys.indexWriter ~= { writer => index => s"var versioned = ${writer(index)};" }
  )

  lazy val serverDependencies: Seq[Def.Setting[_]] = Def.settings(libraryDependencies ++= Seq(
    ws,
    guice,
    filters,
    "org.apache.commons" % "commons-email" % "1.3.1",
    "biz.enef" %% "slogging-slf4j" % sloggingV,
    "org.apache.poi" % "poi-ooxml" % "3.17",
    "com.github.kxbmap" %% "configs" % "0.4.4",
    "org.scalatra.scalate" %% "scalate-core" % "1.9.0",
    // scalajs for server
    "com.vmunier" %% "scalajs-scripts" % "1.1.1",
    "org.julienrf" %% "play-jsmessages" % "3.0.0",
    //Silhouette
    "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteV,
    "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteV,
    "com.mohiva" %% "play-silhouette-persistence" % silhouetteV,
    "com.mohiva" %% "play-silhouette-testkit" % silhouetteV % "test",
    // scala-guice
    "net.codingwell" %% "scala-guice" % "4.2.1",
    // guava
    "com.google.guava" % "guava" % "27.0.1-jre",

    // doobie
    // Start with this one
    "org.tpolecat" %% "doobie-core" % doobieV,
    "org.tpolecat" %% "doobie-h2" % doobieV,
    "org.tpolecat" %% "doobie-hikari" % doobieV,
    "org.tpolecat" %% "doobie-postgres" % doobieV,

    // webjars for Semantic-UI
    "org.webjars" %% "webjars-play" % "2.6.1",
    "org.webjars" % "Semantic-UI" % semanticV,
    "org.webjars" % "jquery" % jQueryV,
    "org.webjars" % "codemirror" % "5.41.0",
    // metrics
    "com.kenshoo" %% "metrics-play" % "2.6.6_0.6.2",
    // TEST
    "com.typesafe.akka" %% "akka-testkit" % "2.5.6" % Test,
    "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.6" % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
    "org.awaitility" % "awaitility" % "3.0.0" % Test,
    "org.seleniumhq.selenium" % "selenium-java" % "3.14.0" % Test,

    "org.scalatest" %% "scalatest" % scalaTestV % Test,
    "org.tpolecat" %% "doobie-scalatest" % doobieV % Test
  ).map(_.excludeAll(ExclusionRule("org.slf4j", "slf4j-log4j12")))
  )

  lazy val clientSettings: Seq[Def.Setting[_]] = Seq(
    scalacOptions ++= Seq("-Xmax-classfile-name", "78")
    , addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
    , jsDependencies ++= Seq(
      "org.webjars" % "jquery" % jQueryV / "jquery.js" minified "jquery.min.js"
      , "org.webjars" % "Semantic-UI" % semanticV / "semantic.js" minified "semantic.min.js" dependsOn "jquery.js"
    )
  )
  lazy val clientDependencies: Seq[Def.Setting[_]] = Def.settings(libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.6",
    "org.scala-lang.modules" %% "scala-xml" % "1.0.6",
    "com.typesafe.play" %%% "play-json" % "2.6.1",
    "com.thoughtworks.binding" %%% "dom" % bindingV,
    "com.thoughtworks.binding" %%% "route" % bindingV,
    "com.thoughtworks.binding" %%% "futurebinding" % bindingV,
    "fr.hmil" %%% "roshttp" % "2.0.2",
    "org.denigma" %%% "codemirror-facade" % "5.13.2-0.8",
    // java.time support for ScalaJS,
    "org.scala-js" %%% "scalajs-java-time" % "0.2.2",
    // jquery support for ScalaJS,
    "be.doeraene" %%% "scalajs-jquery" % "0.9.1",
    "org.scalatest" %%% "scalatest" % scalaTestV % Test,
  ))

  lazy val sharedDependencies: Seq[Def.Setting[_]] = Def.settings(libraryDependencies ++= Seq(
    "org.julienrf" %%% "play-json-derived-codecs" % "4.0.0",
    "com.beachape" %%% "enumeratum-play-json" % "1.5.14",
    "com.softwaremill.quicklens" %%% "quicklens" % "1.4.11",
    // logging lib that also works with ScalaJS
    "biz.enef" %%% "slogging" % sloggingV,
    "org.scalatest" %%% "scalatest" % scalaTestV % Test,

  ))

  lazy val jsSettings: Seq[Def.Setting[_]] = Seq(
    scalaJSStage in Global := testStage
    , coverageEnabled := false
  )

  lazy val jvmSettings: Seq[Def.Setting[_]] = Seq(
    coverageExcludedPackages := ".*\\.Reverse.*;views.*;adapters.*;controllers.*;.*\\.javascript.*"
  )

  lazy val sharedJsDependencies: Seq[Def.Setting[_]] = Def.settings(libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-java-time" % "0.2.3"
  ))

  def sharedSettings(moduleName: Option[String] = None): Seq[Def.Setting[_]] = Seq(
    scalaVersion := scalaV
    , name := s"$projectName${moduleName.map("-" + _).getOrElse("")}"
    , version := s"$projectV"
    , publishArtifact in packageDoc := false
  //  , sources in(Compile, doc) := Seq.empty
  ) ++ organizationSettings

  private lazy val buildInfoSettings = Seq(
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoOptions ++= { if (coverageEnabled.value) Seq() else Seq(BuildInfoOption.BuildTime) }, // <-- this line was changed
    buildInfoOptions += BuildInfoOption.ToJson,
    buildInfoPackage := "pme123.form.version"
  )

  lazy val dockerSettings: Seq[Def.Setting[_]] = Seq(
    dockerRepository := Some("docker.io"),
    dockerUsername := Some("pame"),
    defaultLinuxInstallLocation in Docker := "/pme123",
    dockerBaseImage := "openjdk:8-jre",
    dockerUpdateLatest := true,
    dockerExposedPorts := Seq(9000, 9443),
    dockerEntrypoint := Seq("/pme123/conf/docker_entrypoint.sh"),
    dockerCommands ++= Seq(
      Cmd("USER", "root"),
      Cmd("RUN", "mkdir", "/pme123/logs"),
      Cmd("RUN", "chmod", "-R", "777", "/pme123/logs"),
      Cmd("RUN", "chmod", "+x", "/pme123/conf/docker_entrypoint.sh"),
      Cmd("RUN", "chmod", "+x", "/pme123/bin/play-binding-form-server"),
      Cmd("RUN", "chgrp", "-R", "0", "/pme123", "&&", "chmod", "-R", "g=u", "/pme123"),
      Cmd("USER", "daemon")
    )

  )

  lazy val dockerComposeSettings: Seq[Def.Setting[_]] = Seq(
    composeFile := "./docker/docker-compose.yml"
  )
  lazy val noPublishSettings: Seq[Def.Setting[_]] = Seq(
    publish := {},
    publishLocal := {},
    publishArtifact := false,
    isSnapshot := true
  )

  lazy val noDockerPublishSettings: Seq[Def.Setting[_]] = Seq(
    publish in Docker := {},
    publishLocal in Docker := {}
  )
}
