import sbt._

class FutureHttpProject(info: ProjectInfo) extends DefaultProject(info) {
//  val scalaTest = "org.scalatest" % "scalatest" % "1.0" % "test" withSources()

  val scalaToolsSnapshots = "Scala-Tools Maven2 Snapshots Repository" at "http://scala-tools.org/repo-snapshots"

  val scalaTest = "org.scalatest" % "scalatest" % "1.0.1-for-scala-2.8.0.Beta1-with-test-interfaces-0.3-SNAPSHOT" %
          "test" withSources()

  val jettyServlet = "org.eclipse.jetty" % "jetty-servlet" % "7.0.1.v20091125" withSources()
  val jettyClient = "org.eclipse.jetty" % "jetty-client" % "7.0.1.v20091125" withSources()

}