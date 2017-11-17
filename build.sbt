import org.scalajs.core.tools.linker.ModuleKind

name := "scala-common"

val settings = Common.settings ++ Seq(
  name := "scala-common",
  version := "0.1.0",

  libraryDependencies ++= Seq(
    "org.scalactic" %%% "scalactic" % "3.0.1",
    "org.scalatest" %%% "scalatest" % "3.0.0" % Test
  ),

  Common.publish,

  credentials ++= Common.credentials()
)

lazy val root = project.in(file(".")).aggregate(commonJS, commonJVM)

lazy val common = crossProject
  .in(file("."))
  .settings(
    settings: _*
  )
  .jvmSettings(
    // JVM-specific settings here
  )
  .jsSettings(
    // JS-specific settings here
      scalaJSModuleKind := ModuleKind.CommonJSModule
  )

lazy val commonJVM = common.jvm
lazy val commonJS = common.js
