import sbt._

class FutureHttpProject(info: ProjectInfo) extends DefaultProject(info) with IdeaProject {
//  val scalaTest = "org.scalatest" % "scalatest" % "1.0" % "test" withSources()

  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"

  val scalaTest = "org.scalatest" % "scalatest" % "1.0.1-for-scala-2.8.0.Beta1-with-test-interfaces-0.3-SNAPSHOT" %
          "test" withSources()

  val jettyVersion = "7.0.2.v20100331"
  val jettyServlet = "org.eclipse.jetty" % "jetty-servlet" % jettyVersion withSources()
  val jettyClient = "org.eclipse.jetty" % "jetty-client" % jettyVersion withSources()
  val jettyUtil = "org.eclipse.jetty" % "jetty-util" % jettyVersion withSources()
  val jettyIo = "org.eclipse.jetty" % "jetty-io" % jettyVersion withSources()
  val jettyHttp = "org.eclipse.jetty" % "jetty-http" % jettyVersion withSources()
  val jettyContinuation = "org.eclipse.jetty" % "jetty-continuation" % jettyVersion withSources()

  val apacheHttpClient = "commons-httpclient" % "commons-httpclient" % "3.1"


  override def ivyUpdateLogging = UpdateLogging.Full
}
