import sbtcrossproject.CrossPlugin.autoImport.crossProject

lazy val common = crossProject(JSPlatform, JVMPlatform)
  .in(file("."))
  .settings(
      Common.settings ++ Common.publish ++ Seq(
          organization := "org.mule.common",
          name := "scala-common",
          version := "0.3.0",
          libraryDependencies ++= Seq(
              "org.scalactic" %%% "scalactic" % "3.0.1" % Test,
              "org.scalatest" %%% "scalatest" % "3.0.0" % Test
          ),
          credentials ++= Common.credentials()
      )
  )
  .jsSettings(scalaJSModuleKind := ModuleKind.CommonJSModule)
