import scala.math

trait Effect {
  def apply(stats: EntityStats): EntityStats
  def remove(stats: EntityStats): EntityStats
}

/**
 * A different class replacing the need of a Map[effect, duration].
 * Works similarly, but more efficiently.
 * @param effect      an effect passed in parameter
 * @param duration    a duration from the previously defined options
 */
case class EffectDuration(effect: Effect, duration: Duration)

/**
 * Some predefined effects types
 *  - IncreaseDamage:  modifies entity damage value
 *
 *  - ScaleDefense:    modifies entity defense
 *
 *  - Poison:          modifies health (probably in each tick)
 */


case class IncreaseDamage(value: Int) extends Effect {
  override def apply(stats: EntityStats): EntityStats = stats.copy(attack = stats.attack + value)
  override def remove(stats: EntityStats): EntityStats = ???

}
case class ScaleDefense(percentage: Double) extends Effect {
  override def apply(stats: EntityStats): EntityStats = stats.copy(defense = stats.defense + math.floor((stats.defense * 100)/percentage).toInt )
  override def remove(stats: EntityStats): EntityStats = ???

}
case class Poison(value: Int) extends Effect{
  override def apply(stats: EntityStats): EntityStats = stats.copy(regeneration = stats.regeneration - value)
  override def remove(stats: EntityStats): EntityStats = ???

}