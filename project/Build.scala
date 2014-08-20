import sbt._
import Keys._

object Settings {
  lazy val Defaults = Seq {
    organization := "com.katlex.scala-mt"
  }
}

object TheProject extends Build {
    lazy val root = project("monitor", file(".")) aggregate (installer)
    lazy val installer = project( "installer", file("installer"))

    protected def project(name: String, base:sbt.File) =
      Project(id = name, base = base) settings (Settings.Defaults: _*) settings (Keys.moduleName := name)
}
