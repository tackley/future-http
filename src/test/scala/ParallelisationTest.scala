import actors.TIMEOUT
import io.{Source}
import java.lang.Throwable
import org.eclipse.jetty.client.{ContentExchange, HttpExchange, HttpClient}
import org.eclipse.jetty.io.Buffer
import org.eclipse.jetty.util.thread.QueuedThreadPool
import scala.actors.Futures._
import scala.actors.Actor._
import java.net.URL
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfterAll, GivenWhenThen, Assertions, FunSuite}


class ParallelisationTest extends FunSuite with Assertions with ShouldMatchers with TestJettyServlets {
  scala.actors.Debug.level = 5

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

  ignore("simple waitForDone with jetty http client") {
    val client = new HttpClient
    client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL)
    client.setIdleTimeout(1000)
    client.setTimeout(250)
    client.setMaxConnectionsPerAddress(200)
    client.setThreadPool(new QueuedThreadPool(250))
    client.start

    var exchangeList = for (i <- 1 to 100) yield {
      println("exchange " + i + " starting...")

      val exchange = new ContentExchange
      exchange.setURL("http://localhost:8080/slow?i=" + i)

      client.send(exchange)
      println("exchange started")

      exchange
    }

    for (e: ContentExchange <- exchangeList) {
      e.waitForDone match {
        case HttpExchange.STATUS_COMPLETED =>
          println("back on the main thread: " + e.getResponseContent)
          e.cancel
        case HttpExchange.STATUS_EXPIRED =>
          println("Timeout! - hey i'm not interested with anything else")
        case other => println("unexpected status: " + other)
      }
    }

    for (i <- 1 to 30) {
      println(i)
      Thread.sleep(1000)
    }

    exchangeList = null
    
    for (i <- 1 to 30) {
      println(i)
      Thread.sleep(1000)
    }
  }


  ignore("how do I reply to a non actor thread from an actor") {
    val a = actor {
      loop {
        react {
          case anything => {
            println("hey i got: " + anything + " from " + sender)
            Thread.sleep(500)
            println("i'm done")
            //reply("response from: "+ anything)
            sender ! "response from: " + anything
          }
        }
      }

      println("bye from the actor")
    }

    a ! "hello?"
    a ! "hello again?"
    println("that's all my messages sent")

    receiveWithin(2000) {
      case TIMEOUT => println("timeout!")
      case s => println("received: " + s)
    }

  }

  case class EXIT()

  ignore("http request with callback") {

    val client = new HttpClient
    client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL)
    client.start

    val httpRequestActor = actor {
      loop {
        react {
          case urlToHit: String => {
            println("invoking: " + urlToHit + "...")

            val exchange = new HttpExchange {
              val replyTo = sender

              override def onResponseStatus(version: Buffer, status: Int, reason: Buffer) = {
                val s = "onResponseStatus " + urlToHit + ": " + status
                replyTo ! s
              }


              override def onConnectionFailed(x: Throwable) = {
                println("connection failed for "+ urlToHit + ": " + x)
              }
            }

            exchange.setURL(urlToHit)

            client.send(exchange)
            println("exchange started")

          }
          case EXIT => println("exiting"); exit
        }
      }
    }


    for (i <- 1 to 100) {
      httpRequestActor ! "http://localhost:8080/slow?i=" + i
    }


    var finished = false
    while(!finished) {
      receiveWithin(5000) {
        case TIMEOUT => println("timeout!"); finished = true
        case s => println("received: " + s)
      }
    }

  }

}