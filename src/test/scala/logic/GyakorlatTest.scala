package logic

import org.scalatest.concurrent.{Signaler, TimeLimitedTests}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.time.{Millis, Span}

import util.{Random, Try}

class GyakorlatTest extends AnyFlatSpec with TimeLimitedTests {
  val timeLimit: Span = Span(1000, Millis)
  override val defaultTestSignaler: Signaler = new Signaler {
    override def apply(testThread: Thread): Unit = {
      println("Ez a teszt túl sokáig fut.")
      testThread.stop()
    } //unsafe, never használd.
  }
  def sample[T](data: IndexedSeq[T]) =
    data(Random.nextInt(data.size))

  def singleTest[Input, Output](input: Input, function: Input => Output, expected: Output ) = {
    val result = function(input)
    assert(result == expected, s"Hiba: '$input' esetén kéne: $expected, lett: $result")
  }

  def shuffle[T](data: IndexedSeq[T]) = data.sortBy( _ => Random.nextInt() )

}
