import org.scalajs.core.tools.linker.ModuleKind

name := "scala-common"

val settings = Common.settings ++ Common.publish ++ Seq(
  organization := "org.mule.common",
  name := "scala-common",
  version := "0.1.3",

  libraryDependencies ++= Seq(
    "org.scalactic" %%% "scalactic" % "3.0.1",
    "org.scalatest" %%% "scalatest" % "3.0.0" % Test
  ),

  credentials ++= Common.credentials()
)

lazy val root = project.in(file(".")).aggregate(commonJS, commonJVM)

lazy val common = crossProject
  .in(file("."))
  .settings(settings: _*)
  .jvmSettings(
    // JVM-specific settings here
  )
  .jsSettings(
    // JS-specific settings here
      scalaJSModuleKind := ModuleKind.CommonJSModule
  )

lazy val commonJVM = common.jvm
lazy val commonJS = common.js
