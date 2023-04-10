trait Effect {
  def apply(stats: EntityStats): EntityStats

  /**
   * Altípusai : IncreaseDamage, ScaleDefense, Poison
   */
}

case class EffectDuration(effect: Effect, duration: Duration){
  def getEffect: Effect = ???
  def getDuration: Duration = ???
}

case class IncreaseDamage(value: Int) extends Effect {}
case class ScaleDefense(percentage: Double) extends Effect {}
case class Poison(value: Int) extends Effect {}