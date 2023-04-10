trait Effect {
  def apply(stats: EntityStats): EntityStats
}

/**
 * A different class replacing the need of a Map[effect, duration].
 * Works similarly, but more efficiently.
 * @param effect      an effect passed in parameter
 * @param duration    a duration from the previously defined options
 */
case class EffectDuration(effect: Effect, duration: Duration){
  def getEffect: Effect = ???
  def getDuration: Duration = ???
}

/**
 * Some predefined effects types
 *  - IncreaseDamage:  modifies entity damage value
 *
 *  - ScaleDefense:    modifies entity defense
 *
 *  - Poison:          modifies health (probably in each tick)
 */
case class IncreaseDamage(value: Int) extends Effect {}
case class ScaleDefense(percentage: Double) extends Effect {}
case class Poison(value: Int) extends Effect {}