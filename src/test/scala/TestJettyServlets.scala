import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.{ServletContextHandler, ServletHolder}
import org.scalatest.{Suite, BeforeAndAfterAll}

trait TestJettyServlets extends BeforeAndAfterAll { this: Suite =>
  val server = new Server(8080)

  override def beforeAll() {
    val root = new ServletContextHandler(server, "/");

    root.addServlet(new ServletHolder(new SlowReponseServlet), "/slow");
    server.start()
  }

  override def afterAll() {
    server.stop()
  }
}


class SlowReponseServlet extends HttpServlet {
  override def doGet(req: HttpServletRequest, resp: HttpServletResponse) = {
    val counter = req.getParameter("i")
    println("Servlet received request " + counter + "...")
    Thread sleep 1000
    println("Servlet responding to " + counter)

    resp.setHeader("Connection", "close")
    resp.getWriter.println("completed " + counter)
  }
}
