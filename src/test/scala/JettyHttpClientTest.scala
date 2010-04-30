import org.eclipse.jetty.client.{ContentExchange, HttpExchange, HttpClient}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{FunSuite, Assertions}

class JettyHttpClientTest extends FunSuite with Assertions with ShouldMatchers with TestJettyServlets {
  test("try connecting with http client") {
    val client = new HttpClient
    client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL)
    //client.setIdleTimeout(5000)
    //client.setTimeout(2000)
    client.start

    for (i <- 1 to 10) {
      val exchange = new ContentExchange

      println("executing " + i)
      exchange.setURL("http://localhost:8080/slow?i=" + i)
      //exchange.setVersion("HTTP/1.0")
 
      client.send(exchange)

      val exchangeState = exchange.waitForDone

      println("state was " + exchangeState + " status: " + exchange.getResponseStatus + ": " + exchange.getResponseContent)

      Thread.sleep(1000)
    }

    Thread.sleep(10000)
  }
}