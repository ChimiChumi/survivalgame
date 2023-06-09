import Priority.Priority

trait Duration extends Serializable{
  val priority: Priority
}

/**
 * Setting priority for the effects for ordering them later.
 */
case object Priority extends Enumeration {
  type Priority = Value
  val TicksLeft: Priority.Value = Value(0)
  val UntilDeath: Priority.Value = Value(1)
  val Permanent: Priority.Value = Value(2)
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

/**
 * Orders effects by their priority.
 */
object DurationOrdering extends Ordering[Duration] {
  def compare(a: Duration, b: Duration): Int = {
    val res = a.priority.compare(b.priority)
    if (res == 0 && a.priority == Priority.TicksLeft) {
      a.asInstanceOf[TicksLeft].ticks.compareTo(b.asInstanceOf[TicksLeft].ticks)
    }
    else {
      res
    }
  }
}
