import sbt._
import Keys._

object TheProject extends Build {
    lazy val root = Project(id = "scala-mt",
                       base = file(".")) aggregate (installer)

    lazy val installer = Project(id = "scala-mt-installer",
                       base = file("installer"))
}
