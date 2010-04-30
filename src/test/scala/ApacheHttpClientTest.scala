import java.net.SocketTimeoutException
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.{SimpleHttpConnectionManager, MultiThreadedHttpConnectionManager, HttpConnectionManager, HttpClient}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{FunSuite, Assertions}

class ApacheHttpClientTest extends FunSuite with Assertions with ShouldMatchers with TestJettyServlets {
  test("try connecting with http client") {
    val manager = new MultiThreadedHttpConnectionManager
    // val manager = new SimpleHttpConnectionManager(true)
    val client = new HttpClient(manager)

    client.getParams.setSoTimeout(2000)

    for (i <- 1 to 10) {
      try {
      val getMethod = new GetMethod("http://localhost:8080/slow?i=" + i)

      println("executing " + i)
      val statusCode = client.executeMethod(getMethod)
      val result = getMethod.getResponseBodyAsString
      println("result was "+ statusCode + ": " + result)

      getMethod.releaseConnection
      } catch {
        case e: SocketTimeoutException => println("exception! "+ e)
      }
      
      Thread.sleep(1000)
    }
  }
}