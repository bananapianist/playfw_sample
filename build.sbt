name := """playfw_sample"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

val slickVersion = "3.1.1"
val playSlickVersion = "1.1.1"
 
 
libraryDependencies ++= Seq(
//  jdbc,
  cache,
  ws,
  evolutions,
  filters,
  "org.skinny-framework" %% "skinny-json" % "1.3.20",
  "com.typesafe.play" %% "play-slick" % playSlickVersion,
  "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion,
//  "mysql" % "mysql-connector-java" % "5.1.36",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.3.3",
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-codegen" % slickVersion,
//  "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0",
  "joda-time" % "joda-time" % "2.3",
  "org.joda" % "joda-convert" % "1.6",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator


fork in run := true


import com.typesafe.config._

// Slick code generator
slickCodeGen <<= slickCodeGenTask // register sbt command
//(compile in Compile) <<= (compile in Compile) dependsOn (slickCodeGenTask) // register automatic code generation on compile
lazy val config = ConfigFactory.parseFile(new File("./conf/application.conf"))
lazy val slickCodeGen = taskKey[Seq[File]]("slick-codegen")
lazy val slickCodeGenTask = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
  val slickDriver = config.getString("slick.dbs.default.driver").init
  val jdbcDriver = config.getString("slick.dbs.default.db.driver")
  val url = config.getString("slick.dbs.default.db.url")
  val outputDir = "app/"
  val pkg = "models"
  val username = config.getString("slick.dbs.default.db.user")
  val password = config.getString("slick.dbs.default.db.password")

  toError(
    r.run(
      "slick.codegen.SourceCodeGenerator",
      cp.files,
      Array(slickDriver, jdbcDriver, url, outputDir, pkg, username, password),
      s.log
    )
  )
  val fname = outputDir + "/Tables.scala"
  Seq(file(fname))
}

