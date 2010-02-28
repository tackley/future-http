import io.{Source}
import org.eclipse.jetty.client.{ContentExchange, HttpExchange, HttpClient}
import scala.actors.Futures._
import java.net.URL
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterAll, GivenWhenThen, Assertions, FunSuite}


class ParallelisationTest extends FunSuite with Assertions with ShouldMatchers with TestJettyServlets {
  ignore("simple futures with java URL") {
    val futureList = for (i <- 1 to 100) yield {
      future {
        println("future " + i + " starting...")
        val myUrl = new URL("http://localhost:8080/slow?i=" + i)
        val s = Source.fromURL(myUrl).getLines().mkString("response: [", "\\n", "]")
        println("future " + i + " done")
        s
      }
    }

    for (f <- futureList) {
      println("back on the main thread: " + f())
    }
  }

  test("simple waitForDone with jetty http client") {
    val client = new HttpClient
    client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL)
    client.start

    val exchangeList = for (i <- 1 to 100) yield {
      println("exchange " + i + " starting...")

      val exchange = new ContentExchange
      exchange.setURL("http://localhost:8080/slow?i=" + i)

      client.send(exchange)
      println("exchange started")

      exchange
    }

    for (e: ContentExchange <- exchangeList) {
      e.waitForDone match {
        case HttpExchange.STATUS_COMPLETED => println("back on the main thread: "+ e.getResponseContent)
        case other => println("unexpected status: "+ other)
      }
    }

  }


}