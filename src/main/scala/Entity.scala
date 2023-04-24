import scala.collection.immutable._
import DurationOrdering.compare

trait Entity {
  val name: String
  val id: String
  val currentEffects: Vector[EffectDuration] // pair of effects and duration
  val currentHP: Int
  val position: Position

  def baseStats: EntityStats // default stats

  def heal(hp: Int): Entity // increase hp

  def takeDamage(hp: Int): Option[Entity] // lower hp

  def addEffect(ed: EffectDuration): Entity // placing effects on entity

  def removeEffects(p: Effect => Boolean): Entity // removing effects from entity

  def applyEffects: EntityStats // applying awaited effects, changing stats

  def moveTo(position: Position) // change position on the map

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

  override def moveTo(pos: Position): Unit = copy(position = pos)

  override def tick: Option[Entity] = ???
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
                   currentEffects: Vector[EffectDuration],
                   currentHP: Int,
                   position: Position,
                   capacity: Int,
                   inventory: Int,
                   equipmentSlots: Chest,
                   onCursor: ItemStack,
                   respawnPosition: Position,
                   reachingDistance: Double,
                   weaponOnPlayer: Option[Weapon],
                   armorOnPlayer: Option[Armor]
                 ) extends Entity {

  override def baseStats: EntityStats = EntityStats(15, 5, 1, 100, 5) //mobok hp-ja nem regeneralodhat

  override def heal(hp: Int): Entity = {
    if (currentHP + hp >= baseStats.maxHP) copy(currentHP = baseStats.maxHP)
    else copy(currentHP = currentHP + hp)
  }

  /**
   * Consuming a desired item.
   * @param item to be consumed
   * @return added equipment effects to currentEffects vector
   */
  def consume(item: Consumable): Entity = {
    item.effects.foldLeft(this)((player, ed) => player.addEffect(ed).asInstanceOf[Player])
  }

  /**
   * Equipping a desired item.
   * @param item  to be equipped
   * @return      added equipment effects to currentEffects vector and added equipment to equipmentslots
   */
  def equip(item: Equipment): Entity = {
    item.effects
      .foldLeft(this)(
        (player, ed) => player.addEffect(ed).asInstanceOf[Player]
      )
      .copy(equipmentSlots = equipmentSlots + item)
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
    //TODO: befejezni
    currentEffects.foreach(ed => ed.effect.apply(baseStats))
    baseStats
  }

  override def moveTo(pos: Position): Unit = copy(position = pos)

  override def tick: Option[Entity] = ???
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
                        regeneration: Double
                      ) {

  /**
   * A method which applies the effect on the entity.
   *
   * @param effect which particular effect to-be applied on the Entity
   * @return an updated EntityStats
   */

    //TODO: ???
  def applyEffect(effect: Effect): EntityStats = effect match {
    case IncreaseDamage(value) => copy(attack = attack + value)
    case ScaleDefense(percentage) => copy(defense = (defense * percentage).toInt)
    case Poison(value) => copy(regeneration = regeneration - value)
    case _ => this
  }

  // it can be given a vector of effects and iterates through them applying them all
  def applyEffect(effects: Effect*): EntityStats =
    effects.foldLeft(this) {
      (stats, effect) => stats.applyEffect(effect)
    }
}

