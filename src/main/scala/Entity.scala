import scala.collection.immutable._
import DurationOrdering.compare

trait Entity {
  val name: String
  val id: String
  val currentEffects: Vector[EffectDuration] // pair of effects and duration
  val currentStats: EntityStats
  val baseStats: EntityStats
  val position: Position

  def heal(hp: Int): EntityStats // increase hp
  def takeDamage(hp: Int): Option[EntityStats] // lower hp
  def addEffect(ed: EffectDuration): Entity // placing effects on entity
  def removeEffects(p: EffectDuration => Boolean): Entity // removing effects from entity
  def applyEffects: EntityStats // applying awaited effects, changing stats
  def moveTo(position: Position): Entity // change position on the map
  def tick: Option[Entity] // clock, needed to check entity status after each tick
}

/**
 * Represents the hostile entities in-game
 * @param name              mob's name
 * @param id                unique ID
 * @param baseStats         the default stats
 * @param currentStats      the actively changing stats throughout a game session
 * @param currentEffects    contains a Vector of EffectDurations currently on the mob
 * @param position          locates the mob in a 2D matrix
 */
case class Mob(
                name: String,
                id: String,
                baseStats: EntityStats,
                currentStats: EntityStats,
                currentEffects: Vector[EffectDuration],
                position: Position
              ) extends Entity {

  /**
   * Heals the mob by the given amount.
   * @param hp    for mobs, this corresponds with the regeneration value. Can be negative too.
   * @return      modified currentStats
   */
  override def heal(hp: Int): EntityStats = {
    if (currentStats.hp + hp >= baseStats.hp) currentStats.copy(hp = baseStats.hp)
    else currentStats.copy(hp = currentStats.hp + hp)
  }

  /**
   * Causes damage for the entity by the given amount.
   * @param hp    amount of damage inflicted
   * @return      modified currentStats in an Option. If the entity dies -> None
   */
  override def takeDamage(hp: Int): Option[EntityStats] = {
    if (currentStats.hp - hp < 0) None
    else Option(currentStats.copy(hp = currentStats.hp - hp))
  }

  /**
   * Adds the input effect to the currentEffects Vector.
   * @param ed    EffectDuration type, consists of an Effect and Duration
   * @return      updated currentEffects Vector
   */
  override def addEffect(ed: EffectDuration): Entity = {
    val index = currentEffects.indexWhere(currentEd => currentEd.effect == ed.effect)

    if (index < 0) {
      return copy(currentEffects = currentEffects.appended(ed))
    }

    else {
      val res = DurationOrdering.compare(currentEffects(index).duration, ed.duration)
      if (res < 0) {
        return copy(currentEffects = currentEffects.updated(index, ed))
      }
    }
    this
  }

  /**
   * Removes the effect from the currentEffects Vector.
   * @param p    predicate to be truthy
   * @return     updated currentEffects Vector, with the effect removed
   */
  override def removeEffects(p: EffectDuration => Boolean): Entity = {
    val newEffects = currentEffects.filterNot(ed => p(ed))
    copy(currentEffects = newEffects)
  }

  /**
   * Applies all the effects present in the curerntEffects Vector.
   * @return    the Updated EntityStats
   */
  override def applyEffects: EntityStats = {
    currentEffects.foldLeft(baseStats)((stat, ed) => ed.effect.apply(stat))
  }

  /**
   * Move entity to the given position
   * @param pos   x,y coordinates
   * @return      New position
   */
  override def moveTo(pos: Position): Entity = copy(position = pos)

  /**
   * This represents the lifecycle of the game.
   * - each tick, the entity's effects time should decrease
   * - the entity should regenerate health
   * @return    An Option of Entity, representing if the Entity is still present in the game or not. (alive or death)
   */
  override def tick: Option[Entity] = {
    if (currentStats.hp <= 0) {
      None
    }
    else {
      val (remainingEffects, indicesToRemove) = currentEffects.zipWithIndex.foldLeft((currentEffects, Seq.empty[Int])) {
        case ((effects, toRemove), (effect, index)) =>
          effect.duration match {
            case left: TicksLeft =>
              val remainingTicks = left.getRemainingTicks
              if (remainingTicks.isEmpty) {
                (effects, toRemove :+ index)
              } else {
                (effects.updated(index, effect.copy(duration = remainingTicks.get)), toRemove)
              }
            case _ => (effects, toRemove)
          }
      }

      val newCurrentEffect = remainingEffects.filterNot(effect => indicesToRemove.contains(currentEffects.indexOf(effect)))
      val temp = copy(currentEffects = newCurrentEffect)
      val stats = temp.applyEffects
      val healed = temp.heal(stats.regeneration)

      Some(copy(currentStats = stats.copy(hp = healed.hp), currentEffects = newCurrentEffect))
    }
  }
}

/**
 *
 * @param name                mob's name
 * @param id                  unique ID
 * @param baseStats           the default stats
 * @param currentStats        actively changing stats throughout a game session
 * @param currentEffects      a Vector of EffectDurations currently on the mob
 * @param position            the entity in a 2D matrix
 * @param capacity            inventory slots size
 * @param inventory           the inventory itself of the player
 * @param equipmentSlots      predefined slots for equipments
 * @param onCursor            a single item slot for holding consumable or swapping items from Chest
 * @param respawnPosition     x,y coordinates for the default spawn position
 * @param reachingDistance    valid length to perform specific action
 * @param weaponOnPlayer      a single predefined slot for weapon
 * @param armorOnPlayer       a single predefined slot for armor
 */
case class Player(
                   name: String,
                   id: String,
                   baseStats: EntityStats,
                   currentStats: EntityStats,
                   currentEffects: Vector[EffectDuration],
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

  /**
   * Heals the mob by the given amount.
   *
   * @param hp      for mobs, this corresponds with the regeneration value. Can be negative too.
   * @return        modified currentStats
   */
  override def heal(hp: Int): EntityStats = {
    if (currentStats.hp + hp >= baseStats.hp) currentStats.copy(hp = baseStats.hp)
    else currentStats.copy(hp = currentStats.hp + hp)
  }
  /**
   * Stores an Equipment in the according slots and applies its effects.
   *
   * @param item     to be equipped
   * @return         Some(unchanged entity, equipment) if the equipment type is present on player already
   * @return         Some(modified stats, null), equips the item and applies it's effects.
   *
   */
  def equip(item: Equipment): Option[(Entity, Equipment)] = {
    if (equipmentSlots.contains(item)) Some((this, item))
    else {
      Some(item.effects.foldLeft(this)((player, ed) => player.addEffect(ed).asInstanceOf[Player]).copy(equipmentSlots = equipmentSlots + item), null)
    }
  }

  /**
   * Causes damage for the entity by the given amount.
   *
   * @param hp amount of damage inflicted
   * @return modified currentStats in an Option. If the entity dies -> None
   */
  override def takeDamage(hp: Int): Option[EntityStats] = {
    if (currentStats.hp - hp < 0) None
    else Option(currentStats.copy(hp = currentStats.hp - hp))
  }


  /**
   * Adds the input effect to the currentEffects Vector.
   *
   * @param ed EffectDuration type, consists of an Effect and Duration
   * @return updated currentEffects Vector
   */
  override def addEffect(ed: EffectDuration): Entity = {
    val index = currentEffects.indexWhere(currentEd => currentEd.effect == ed.effect)

    if (index < 0) {
      return copy(currentEffects = currentEffects.appended(ed))
    }

    else {
      val res = DurationOrdering.compare(currentEffects(index).duration, ed.duration)
      if (res < 0) {
        return copy(currentEffects = currentEffects.updated(index, ed))
      }
    }
    this
  }

  /**
   * Removes the effect from the currentEffects Vector.
   *
   * @param p predicate to be truthy
   * @return updated currentEffects Vector, with the effect removed
   */
  override def removeEffects(p: EffectDuration => Boolean): Entity = {
    val newEffects = currentEffects.filterNot(ed => p(ed))
    copy(currentEffects = newEffects)
  }

  /**
   * Applies all the effects present in the curerntEffects Vector.
   *
   * @return the Updated EntityStats
   */
  override def applyEffects: EntityStats = {
    currentEffects.foldLeft(baseStats)((stat, ed) => ed.effect.apply(stat))
  }

  /**
   * Move entity to the given position
   *
   * @param pos x,y coordinates
   * @return New position
   */
  override def moveTo(pos: Position): Entity = copy(position = pos)

  /**
   * This represents the lifecycle of the game.
   * - each tick, the entity's effects time should decrease
   * - the entity should regenerate health
   *
   * @return An Option of Entity, representing if the Entity is still present in the game or not. (alive or death)
   */
  override def tick: Option[Entity] = {
    if (currentStats.hp <= 0) {
      None
    }
    else {
      val (remainingEffects, indicesToRemove) = currentEffects.zipWithIndex.foldLeft((currentEffects, Seq.empty[Int])) {
        case ((effects, toRemove), (effect, index)) =>
          effect.duration match {
            case left: TicksLeft =>
              val remainingTicks = left.getRemainingTicks
              if (remainingTicks.isEmpty) {
                (effects, toRemove :+ index)
              } else {
                (effects.updated(index, effect.copy(duration = remainingTicks.get)), toRemove)
              }
            case _ => (effects, toRemove)
          }
      }

      val newCurrentEffect = remainingEffects.filterNot(effect => indicesToRemove.contains(currentEffects.indexOf(effect)))
      val temp = copy(currentEffects = newCurrentEffect)
      val stats = temp.applyEffects
      val healed = temp.heal(stats.regeneration)

      Some(copy(currentStats = stats.copy(hp = healed.hp), currentEffects = newCurrentEffect))
    }
  }
}

/**
 * Stats of the entities
 *
 * @param attack          attack damage value
 * @param defense         defense value
 * @param speed           blocks moved per tick
 * @param hp              entity health
 * @param regeneration    HP regenerated per tick)
 */
case class EntityStats(
                        attack: Int,
                        defense: Int,
                        speed: Double,
                        hp: Int,
                        regeneration: Int
                      )

