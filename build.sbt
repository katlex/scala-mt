name := "Scala MT4 bridge"

description := "An MQL4 expert and scala library allowing writing experts in scala"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.0-RC5"

libraryDependencies ++= Seq(
  "org.scala-sbt" % "launcher-interface" % "0.12.0" % "provided",
  "org.zeromq" %% "zeromq-scala-binding" % "0.0.9"
)

fork in run := true

connectInput in run := true

outputStrategy := Some(StdoutOutput)

resolvers ++= Seq(
  "katlex repo" at "http://katlex.github.com/maven2/releases",
  "Sonatype (releases)" at "https://oss.sonatype.org/content/repositories/releases/",
  Resolver.typesafeIvyRepo("releases")
)

publishTo := Some {
  val target = "snapshots"
  Resolver.file("katlex-repo", file(sys.props("user.home") + "/katlex.github.com/maven2/" + target))
}
