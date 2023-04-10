trait Duration

case class TicksLeft(ticks: Int) extends Duration{
  def getRemainingTicks: Option[Duration] = ???
}

case class TillDeath() extends Duration
case class Permanent() extends Duration
