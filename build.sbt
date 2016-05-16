name := """playfw_sample"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

val slickVersion = "3.1.1"
val playSlickVersion = "1.1.1"
 
 
libraryDependencies ++= Seq(
//  jdbc,
  cache,													//****cache modile***
  ws,
  evolutions,
  filters,
   "org.skinny-framework" %% "skinny-json" % "1.3.20",	
  "com.typesafe.play" %% "play-slick" % playSlickVersion,	//****DB FRM***
  "com.typesafe.play" %% "play-slick-evolutions" % playSlickVersion,		//****DB FRM***
//  "mysql" % "mysql-connector-java" % "5.1.36",
  "org.mariadb.jdbc" % "mariadb-java-client" % "1.4.2",		//****DB Driver***
  "com.typesafe.slick" %% "slick" % slickVersion,			//****DB FRM***
  "com.typesafe.slick" %% "slick-codegen" % slickVersion,	//****DB FRM***
//  "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0",
  "joda-time" % "joda-time" % "2.3",						//****Datetime ***
  "org.joda" % "joda-convert" % "1.6",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "jp.t2v" %% "play2-auth"        % "0.14.2",				//****Auth module***
  "jp.t2v" %% "play2-auth-social" % "0.14.2",				//****Auth module***
  "jp.t2v" %% "play2-auth-test"   % "0.14.2" % "test",		//****Auth module***
  play.sbt.Play.autoImport.cache,
  "com.nulab-inc" %% "play2-oauth2-provider" % "0.17.0",	//****OAuth2 Provider***
  "com.enragedginger" %% "akka-quartz-scheduler" % "1.5.0-akka-2.4.x",	//****Schedule Job like cron***
//  "org.webjars" %% "webjars-play" % "2.4.0-1",
//  "org.webjars" % "bootstrap" % "3.2.0",
  "com.adrianhurt" %% "play-bootstrap" % "1.0-P25-B4",		//****Bootstrap***
  "org.webjars.bower" % "jquery" % "2.1.3",					//****jquery-ui***
  "org.webjars" % "jquery-ui" % "1.11.4",					//****jquery-ui***
//  "org.webjars" % "font-awesome" % "4.5.0",
//  "org.webjars" % "bootstrap-datepicker" % "1.4.0",
  "com.typesafe.play" %% "play-mailer" % "5.0.0-M1",			//****mail plugin***
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0-RC1" % Test
)


scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-unused-import",
  "-Ywarn-value-discard"
  // 警告をエラーにする
//  , "-Xfatal-warnings"
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

