import actors.Actor
import actors.Actor._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{FunSuite, Assertions}
import scala.collection

class MacTest extends FunSuite with Assertions with ShouldMatchers with TestJettyServlets {
  scala.actors.Debug.level = 5

  class Controller(cache: Cache, http: HttpFetch) extends Actor {
    def act() = {
      loop {
        receive {
          case msg @ FetchUrl(url) =>
            log("request to fetch url: " + msg)
            cache ! msg

          case CacheHit(url, value) =>
            log("hit! we're done!")

          case CacheMiss(url) =>
            log("it's a miss")
        }
      }
    }
  }

  class Cache extends Actor {
    val theCache = new scala.collection.mutable.HashMap[String, String]

    def act() = {
      loop {
        react {
          case FetchUrl(url) =>
            log("cache: request to fetch " + url)
            // right now this is synchronous, should be async to simulate memcached
            theCache.get(url) match {
              case None =>
                log(" -> miss")
                reply(CacheMiss(url))
              case Some(value) =>
                log(" -> hit")
                reply(CacheHit(url, value))
            }

        }
      }
    }

  }

  class HttpFetch extends Actor {
    def act() = {}

  }

  case class FetchUrl(val url: String)
  case class CacheMiss(val url: String)
  case class CacheHit(val url: String, val value: String)

  test("this is simulating the request") {
    val cache = new Cache
    val http = new HttpFetch
    val controller = new Controller(cache, http)

    controller.start
    cache.start
    http.start

    log("all started")

    controller !? FetchUrl("http://localhost:8080/slow?i=1")

  }

  def log(s: String) = {
    println(currentThread.getName +": "+ s)
  }
}