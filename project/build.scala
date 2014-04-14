import sbt._
import Keys._
import org.scalatra.sbt._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._
import net.virtualvoid.sbt.graph.Plugin._

object D2gBuild extends Build {
	val Organization = "d2g"
	val Name = "d2g"
	val Version = "0.1.0-SNAPSHOT"
	val AkkaVersion = "2.1.2"
	val ScalaVersion = "2.10.3"
	val ScalatraVersion = "2.2.2"

	lazy val project = Project(
		"d2g",
		file("."),
		settings = Defaults.defaultSettings ++ ScalatraPlugin.scalatraWithJRebel ++ graphSettings ++ scalateSettings ++ Seq(
			organization := Organization,
			name := Name,
			version := Version,
			scalaVersion := ScalaVersion,
			resolvers += Classpaths.typesafeReleases,
			libraryDependencies ++= Seq(
				"com.typesafe.akka" %% "akka-actor" % AkkaVersion,
				"com.typesafe.akka" %% "akka-slf4j" % AkkaVersion,
				"com.typesafe.akka" %% "akka-testkit" % AkkaVersion % "test",
				"org.scalatra" %% "scalatra" % ScalatraVersion,
				"org.scalatra" %% "scalatra-scalate" % ScalatraVersion,
				"org.scalatra" %% "scalatra-auth" % ScalatraVersion,
				"org.scalatra" %% "scalatra-json" % ScalatraVersion,
				"org.scalatra" %% "scalatra-swagger"  % ScalatraVersion exclude("org.slf4j", "slf4j-log4j12"),
				"org.scalatra" %% "scalatra-specs2" % ScalatraVersion % "test",
				"org.json4s"   %% "json4s-native" % "3.2.4",
				"com.github.nscala-time" %% "nscala-time" % "0.6.0",
				"org.reactivemongo" %% "reactivemongo" % "0.9",
				"net.debasishg" %% "redisclient" % "2.12" exclude("com.typesafe.akka", "akka-actor_2.10"),
				"ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime",
				"org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container",
				"org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test" artifacts (Artifact("javax.servlet", "jar", "jar"))
			),
			scalateTemplateConfig in Compile <<= (sourceDirectory in Compile) {
				base =>
					Seq(
						TemplateConfig(
							base / "webapp" / "WEB-INF" / "templates",
							Seq.empty, /* default imports should be added here */
							Seq(
								Binding("context", "_root_.org.scalatra.scalate.ScalatraRenderContext", importMembers = true, isImplicit = true)
							), /* add extra bindings here */
							Some("templates")
						)
					)
			}
		)
	)
}
