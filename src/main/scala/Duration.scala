import Priority.Priority

trait Duration{
  val priority: Priority
}

case object Priority extends Enumeration {
  type Priority = Value
  val TicksLeft = Value(0)
  val UntilDeath = Value(1)
  val Permanent = Value(2)
}

/**
 * A Duration subtype, sets the effect duration to last until the given tick expires
 *
 * @param ticks for how long the effect should last
 */
case class TicksLeft(ticks: Int) extends Duration {
  def getRemainingTicks: Option[Duration] = {
    if (ticks <= 0) {
      None
    }
    else {
      Some(TicksLeft(ticks - 1))
    }
  }

  override val priority: Priority = Priority.TicksLeft
}

/**
 * A Duration subtype, sets the effect duration to last until player death
 */
case object UntilDeath extends Duration{
  override val priority: Priority = Priority.UntilDeath
}

/**
 * A Duration subtype, sets the effect duration to be permanent
 */
case object Permanent extends Duration{
  override val priority: Priority = Priority.Permanent
}


object DurationOrdering extends Ordering[Duration] {
  def compare(a: Duration, b: Duration): Int = {
    val res = a.priority.compare(b.priority)

    if(res == 0 && a.priority == Priority.TicksLeft){
      val ticksA = a.asInstanceOf[TicksLeft].ticks
      val ticksB = b.asInstanceOf[TicksLeft].ticks
      return ticksB - ticksA
    }
    res
  }
}
