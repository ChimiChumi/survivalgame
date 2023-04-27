import scala.collection.immutable._
import DurationOrdering.compare

trait Entity {
  val name: String
  val id: String
  val currentEffects: Vector[EffectDuration] // pair of effects and duration
  val currentHP: Int
  val position: Position

  def heal(hp: Int): Entity // increase hp

  def takeDamage(hp: Int): Option[Entity] // lower hp

  def addEffect(ed: EffectDuration): Entity // placing effects on entity

  def removeEffects(p: Effect => Boolean): Entity // removing effects from entity

  def applyEffects: EntityStats // applying awaited effects, changing stats

  def moveTo(position: Position): Entity // change position on the map

  def tick: Option[Entity] // clock, needed to check entity status after each tick
}


case class Mob(
                name: String,
                id: String,
                baseStats: EntityStats,
                currentEffects: Vector[EffectDuration],
                currentHP: Int,
                position: Position
              ) extends Entity {


  override def heal(hp: Int): Entity = {
    if (currentHP + hp >= baseStats.maxHP) copy(currentHP = baseStats.maxHP)
    else copy(currentHP = currentHP + hp)
  }

  override def takeDamage(hp: Int): Option[Entity] = {
    if (currentHP - hp < 0) None
    else Option(copy(currentHP = currentHP - hp))
  }

  override def addEffect(ed: EffectDuration): Entity = {
    val index = currentEffects.indexWhere(currentEd => currentEd.effect == ed.effect)

    if (index < 0)
      copy(currentEffects = currentEffects.appended(ed))

    else {
      val res = DurationOrdering.compare(currentEffects(index).duration, ed.duration)
      if (res >= 0) copy(currentEffects = currentEffects.updated(index, ed))
    }

    this
  }

  override def removeEffects(p: Effect => Boolean): Entity = {
    val newEffects = currentEffects.filterNot(ed => p(ed.effect))
    copy(currentEffects = newEffects)
  }

  override def applyEffects: EntityStats = {
    currentEffects.foreach(ed => ed.effect.apply(baseStats))
    baseStats
  }

  override def moveTo(pos: Position): Entity = copy(position = pos)

  override def tick: Option[Entity] = {
    if (currentHP <= 0) None

    else {
      val temp = copy(baseStats = this.applyEffects)

      val newCurrentEffect = temp.currentEffects.zipWithIndex.foldLeft(temp.currentEffects)((currentEffects, tupleEd) => {
        tupleEd._1.duration match {
          case left: TicksLeft =>
            val remainingTicks = left.getRemainingTicks
            if (remainingTicks.isEmpty) {
              removeEffects(effect => tupleEd._1.effect == effect).currentEffects
            }
            else {
              temp.currentEffects.updated(tupleEd._2, tupleEd._1.copy(duration = remainingTicks.get))
            }
          case _ => currentEffects
        }
      })

      Some(copy(currentHP = temp.heal(temp.baseStats.regeneration).currentHP, currentEffects = newCurrentEffect))
    }
  }
}

/**
 *
 * @param name             player name
 * @param id               unique player id
 * @param currentEffects   current effects on the player
 * @param currentHP        current hp of the player
 * @param position         current position of the player
 * @param capacity         inventory size of the player
 * @param inventory        current empty inventory
 * @param equipmentSlots   dedicated slots for equipment
 * @param weapon           dedicated slot for weapon
 * @param armor            dedicated slot for armor
 * @param onCursor         temporary storage of items currently being moved around between player and Chest
 * @param respawnPosition  Coordinates where player will respawn after death
 * @param reachingDistance Blocks between the player and interaction
 */
case class Player(
                   name: String,
                   id: String,
                   baseStats: EntityStats,
                   currentEffects: Vector[EffectDuration],
                   currentHP: Int,
                   position: Position,
                   capacity: Int,
                   inventory: Chest,
                   equipmentSlots: Set[Equipment],
                   onCursor: ItemStack,
                   respawnPosition: Position,
                   reachingDistance: Double,
                   weaponOnPlayer: Option[Weapon],
                   armorOnPlayer: Option[Armor]
                 ) extends Entity {

  require(inventory.maxSlots == capacity, s"Inventory size has to match capacity! ($capacity)")
  require(equipmentSlots.size <= 4, "The player cannot carry more than 4 equipments!")

  override def heal(hp: Int): Entity = {
    if (currentHP + hp >= baseStats.maxHP) copy(currentHP = baseStats.maxHP)
    else copy(currentHP = currentHP + hp)
  }

  /**
   * Consuming a desired item.
   *
   * @param item to be consumed
   * @return added equipment effects to currentEffects vector
   */
  def consume(item: Consumable): Player = {
    item.effects.foldLeft(this)((player, ed) => player.addEffect(ed).asInstanceOf[Player])
  }
  /**
   * Equipping a desired item.
   *
   * @param item to be equipped
   * @return added equipment effects to currentEffects vector and added equipment to equipmentslots
   */
  def equip(item: Equipment): Option[(Entity, Equipment)] = {
    if (equipmentSlots.contains(item)) Some((this, item))
    else {
      Some(item.effects.foldLeft(this)((player, ed) => player.addEffect(ed).asInstanceOf[Player]).copy(equipmentSlots = equipmentSlots + item), null)
    }
  }

  override def takeDamage(hp: Int): Option[Entity] = {
    if (currentHP - hp < 0) None
    else Option(copy(currentHP = currentHP - hp))
  }

  override def addEffect(ed: EffectDuration): Entity = {
    val index = currentEffects.indexWhere(currentEd => currentEd.effect == ed.effect)

    if (index < 0) {
      return copy(currentEffects = currentEffects.appended(ed))
    }

    else {
      val res = DurationOrdering.compare(currentEffects(index).duration, ed.duration)
      if (res >= 0) copy(currentEffects = currentEffects.updated(index, ed))
    }
    this
  }

  override def removeEffects(p: Effect => Boolean): Entity = {
    val newEffects = currentEffects.filterNot(ed => p(ed.effect))
    copy(currentEffects = newEffects)
  }

  override def applyEffects: EntityStats = {
    currentEffects.foldLeft(baseStats)((stat, ed) => ed.effect.apply(stat))
  }

  override def moveTo(pos: Position): Entity = copy(position = pos)

  override def tick: Option[Entity] = {
    if (currentHP <= 0) None

    else {
      val temp = copy(baseStats = this.applyEffects)

      val newCurrentEffect = temp.currentEffects.zipWithIndex.foldLeft(temp.currentEffects)((currentEffects, tupleEd) => {
        tupleEd._1.duration match {
          case left: TicksLeft =>
            val remainingTicks = left.getRemainingTicks
            if (remainingTicks.isEmpty) {
              removeEffects(effect => tupleEd._1.effect == effect).currentEffects
            }
            else {
              temp.currentEffects.updated(tupleEd._2, tupleEd._1.copy(duration = remainingTicks.get))
            }
          case _ => currentEffects
        }
      })

      Some(copy(currentHP = temp.heal(temp.baseStats.regeneration).currentHP, currentEffects = newCurrentEffect))
    }
  }

}

/**
 * The entitystats
 *
 * @param attack       attack damage value
 * @param defense      defense value
 * @param speed        blocks moved per tick
 * @param maxHP        max entity health
 * @param regeneration HP regenerated per tick)
 */
case class EntityStats(
                        attack: Int,
                        defense: Int,
                        speed: Double,
                        maxHP: Int,
                        regeneration: Int
                      )

