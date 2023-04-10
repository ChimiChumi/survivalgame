trait Duration

/**
 * A Duration subtype, sets the effect duration to last until the given tick expires
 * @param ticks   for how long the effect should last
 */
case class TicksLeft(ticks: Int) extends Duration{
  def getRemainingTicks: Option[Duration] = ???
}

/**
 * A Duration subtype, sets the effect duration to last until player death
 */
case class TillDeath() extends Duration

/**
 * A Duration subtype, sets the effect duration to be permanent
 */
case class Permanent() extends Duration
