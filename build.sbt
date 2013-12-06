name := "d2g"

organization  := "org.d2g"

version       := "0.1"

scalaVersion  := "2.10.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

mainClass in (Compile, run) := Some("org.d2g.Boot")

ideaExcludeFolders ++= Seq(
    ".idea",
    ".idea_modules"
)

resolvers ++= Seq(
  "spray repo" at "http://repo.spray.io/",
  "Typesafe repository releases" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  "io.spray"            %   "spray-can"     % "1.2.0",
  "io.spray"            %   "spray-routing" % "1.2.0",
  "io.spray"            %   "spray-testkit" % "1.2.0",
  "com.typesafe.akka"   %%  "akka-actor"    % "2.2.3",
  "com.typesafe.akka"   %%  "akka-testkit"  % "2.2.3",
  "org.json4s"          %%  "json4s-native" % "3.2.4",
  "org.reactivemongo"   %%  "reactivemongo" % "0.9",
  "org.specs2"          %%  "specs2"        % "2.3.4" % "test"
)

seq(Revolver.settings: _*)