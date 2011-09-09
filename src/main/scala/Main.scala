import dispatch._
import concurrent.ops._
import org.apache.http.conn.HttpHostConnectException

object Main extends App {
  val baseUrl = :/("10.121.75.151",80) / "slow"

  val futures = (1 to 4).map { t =>
    future {
      run(t)
      t
    }
  }
  println("all started")

  // wait for all the futures to finish
  futures.foreach(_())

  println("all done")

  def out(m: String) { print(m.padTo(30, ' ')) }

  def run(t: Int) {
    val http = new Http with NoLogging
    Thread.sleep(t * 200)

    for (i <- 1 to 2000) {

      val id = "%d-%d" format (t, i)

      try {
        val timeAtStart = System.currentTimeMillis()
        val url = baseUrl <<? Seq("i" -> id)

        out("started(" + id + ")")

        val result = http(url as_str)

        val duration = System.currentTimeMillis() - timeAtStart
        out("%s %dms" format (result.trim, duration))

      } catch {
        case StatusCode(code, _) => out("%d(%s)" format (code, id))
        case nc: HttpHostConnectException => out("NOCON(" + id + ")")
        case other => println("unexpected error: " + other)
      }

      Thread.sleep(1000)
    }

  }

}