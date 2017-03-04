name := """App-play-silhouette-macwire-mongodb"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

parallelExecution in Test := true // the default in sbt

resolvers += Resolver.jcenterRepo

// Libs version
val reactiveMongoVer = "0.11.14"
val silhouetteVer = "4.0.0"

// Silhouette config
lazy val silhouetteLib = Seq(
  "com.mohiva" %% "play-silhouette" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-password-bcrypt" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-crypto-jca" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-persistence" % silhouetteVer,
  "com.mohiva" %% "play-silhouette-testkit" % silhouetteVer % "test"
)

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play"    %% "scalatestplus-play"   % "1.5.1" % Test,
  "com.softwaremill.macwire"  %% "macros"               % "2.3.0" % "provided",
  "com.softwaremill.macwire"  %% "util"                 % "2.3.0",
  "com.softwaremill.macwire"  %% "proxy"                % "2.3.0",
  "com.jason-goodwin"         % "authentikat-jwt_2.11"  % "0.4.5",
  "com.iheart"                %% "ficus"                % "1.2.6", // config lib, used by Silhouette,
  "org.reactivemongo"         %% "play2-reactivemongo"  % reactiveMongoVer

) ++ silhouetteLib

