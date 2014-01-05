import org.scalatra.sbt._
import com.mojolly.scalate.ScalatePlugin._
import ScalateKeys._

name := "d2g"

version := "0.1.0-SNAPSHOT"

scalaVersion := "2.10.3"

scalateSettings

ScalatraPlugin.scalatraWithJRebel

libraryDependencies ++= Seq(
	"org.scalatra" %% "scalatra" % "2.2.2",
	"org.scalatra" %% "scalatra-scalate" % "2.2.2",
	"org.scalatra" %% "scalatra-specs2" % "2.2.2" % "test",
	"org.scalatra" %% "scalatra-json" % "2.2.2",
	"org.json4s" %% "json4s-jackson" % "3.2.6",
	"org.reactivemongo" %% "reactivemongo" % "0.9",
	"ch.qos.logback" % "logback-classic" % "1.0.6" % "runtime",
	"org.eclipse.jetty" % "jetty-webapp" % "8.1.8.v20121106" % "container",
	"org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" % "container;provided;test" artifacts Artifact("javax.servlet", "jar", "jar")
)

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

resolvers ++= Seq(
	"SonatypeReleases" at "http://oss.sonatype.org/content/repositories/releases/",
	"Akka Repo" at "http://repo.akka.io/repository"
)
