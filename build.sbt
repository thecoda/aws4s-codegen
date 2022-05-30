
inThisBuild(nocomma {
  scalaVersion := "3.1.1"
  version := "0-SNAPSHOT"
  organization := "net.thecoda"
  organizationName := "github.com/thecoda"
  semanticdbEnabled := true
  semanticdbVersion := scalafixSemanticdb.revision
})

//addCompilerPlugin("co.blocke" %% "scala-reflection" % "1.1.4")

val DependencySets = new {
  val sttp =
    Seq("core", "slf4j-backend", "jsoniter") //  "circe",
      .map("com.softwaremill.sttp.client3" %% _ % "3.5.1")

  val slf4j =
    Seq("slf4j-api", "slf4j-simple")
      .map("org.slf4j" % _ % "1.7.36")

  val jsoniter =
    Seq(
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-core" % "2.13.12",
      "com.github.plokhotnyuk.jsoniter-scala" %% "jsoniter-scala-macros" % "2.13.12" % "provided" //% "compile-internal"
    )

  val testDeps = Seq(
    "org.scalatest" %% "scalatest" % "3.2.12-RC2",
  ).map(_ % Test)

  val misc = Seq(
    "com.lihaoyi"                       %% "pprint"       % "0.7.3",
    "org.ekrich"                        %% "sconfig"      % "1.4.9",
    "org.jsoup"                         %  "jsoup"        % "1.14.3",
    "com.github.jknack"                 %  "handlebars"   % "4.3.0",
    "com.github.spullara.mustache.java" %  "compiler"     % "0.9.10",
    "com.github.blemale"                %% "scaffeine"    % "5.1.2",
  )

  val all = sttp ++ slf4j ++ jsoniter ++ misc ++ testDeps
}

lazy val root = (project in file("."))
  .settings( nocomma {
    name := "aws4s-codegen"
    libraryDependencies ++= DependencySets.all
  })

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
