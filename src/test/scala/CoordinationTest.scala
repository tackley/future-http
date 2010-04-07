import actors.Actor._
import actors.{OutputChannel, Actor}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{Assertions, FunSuite}

case class StartFullIndex()
case class ProcessBatch(val batch: Batch)
case class BatchComplete(val batch: Batch)
case class GenerateBatches()
case class BatchList(val batches: List[Batch])

case class Batch(val start: Long, val end: Long)

class IndexBuildCoordinator(batchGenerator: Actor) extends Actor {
  var pendingBatches: List[Batch] = Nil
  var activeBatches: Set[Batch] = Set.empty
  var batchProcessors: List[Actor] = Nil

  def act() {
    loop {
      react {
        case StartFullIndex => {
          println("start full index")
          batchGenerator ! GenerateBatches
        }

        case BatchList(batches) => {
          println("batchlist got: "+ batches)
          pendingBatches = batches
          startBatchProcessors

          batchProcessors.foreach(sendNextBatchTo(_))
        }

        case BatchComplete(batch) => {
          println("batch complete: " + batch)
          activeBatches -= batch
          println("active batches: " + activeBatches)
          println("pending batches: " + pendingBatches)
          sendNextBatchTo(sender)
          if (activeBatches.isEmpty) {
            println("ALL DONE!")
          }
        }
      }
    }
  }

  def sendNextBatchTo(a: OutputChannel[Any]) {
    pendingBatches match {
      case head :: tail => {
        pendingBatches = tail
        activeBatches += head
        a ! ProcessBatch(head)
      }

      case Nil => println("nothing to send to "+ a)
    }
  }

  def startBatchProcessors() {
    val num = 10

    println("starting "+ num + " batch processors...")

    batchProcessors = (for (i <- 1 to num) yield {
      new BatchProcessor().start
    }).toList
  }
}

class BatchGenerator extends Actor {
  def act() {
    loop {
      react {
        case GenerateBatches => {
          println("generating batches")
          reply(BatchList(List(
            Batch(1, 5), Batch(7, 20), Batch(15, 20),
            Batch(1, 5), Batch(7, 20), Batch(15, 20),
            Batch(1, 5), Batch(7, 20), Batch(15, 20),
            Batch(1, 5), Batch(7, 20), Batch(15, 20),
            Batch(1, 5), Batch(7, 20), Batch(15, 20),
            Batch(1, 5), Batch(7, 20), Batch(15, 20)
            )))
        }
      }
    }
  }
}

class BatchProcessor extends Actor {
  def act() {
    loop {
      react {
        case ProcessBatch(batch) => {
          println("processing batch: " + batch)
          Thread.sleep(5000)
          println("processed batch: " + batch)
          reply(BatchComplete(batch))
        } 
      }
    }
  }
}

class CoordinationTest extends FunSuite with Assertions with ShouldMatchers {
  test("full index build spike") {
    val batchGenerator = new BatchGenerator().start
    val coordinator = new IndexBuildCoordinator(batchGenerator).start

    coordinator ! StartFullIndex

    println("hello")
  }
}