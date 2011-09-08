libraryDependencies ++= {
  val jettyVersion = "7.5.0.v20110901"
  Seq(
   "org.scalatest" % "scalatest_2.9.0" % "1.6.1" % "test",
   "org.eclipse.jetty" % "jetty-servlet" % jettyVersion,
   "org.eclipse.jetty" % "jetty-client" % jettyVersion,
   "org.eclipse.jetty" % "jetty-util" % jettyVersion,
   "org.eclipse.jetty" % "jetty-io" % jettyVersion,
   "org.eclipse.jetty" % "jetty-http" % jettyVersion,
   "org.eclipse.jetty" % "jetty-continuation" % jettyVersion,
   "commons-httpclient" % "commons-httpclient" % "3.1"
  )
}

ivyLoggingLevel := UpdateLogging.Full

scalaVersion := "2.9.1"



