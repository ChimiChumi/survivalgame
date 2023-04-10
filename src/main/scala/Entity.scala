trait Entity {
  val name: String
  val id: String
  val currentEffects: Vector[EffectDuration] // pair of effects and duration
  val currentHP: Int
  val position: Position

  //TODO: baseStat ???
  def baseStats: EntityStats    // default stats
  def heal(hp: Int): Entity     // increase hp
  def takeDamage(hp: Int): Option[Entity]  // lower hp
  def addEffect(effect: Effect, duration: Duration): Entity  // placing effects on entity
  def removeEffects(p: Effect => Boolean): Entity   // removing effects from entity
  def applyEffects: EntityStats // applying awaited effects, changing stats
  def moveTo(position: Position)  // change position on the map
  def tick: Option[Entity]  // clock, needed to check entity status after each tick
}

//todo:basestats kerdes
case class Mob(
                name: String,
                id: String,
                stats: EntityStats,
                currentEffects: Vector[EffectDuration],
                currentHP: Int,
                position: Position
              ) extends Entity {
  override def baseStats: EntityStats = ???
  override def heal(hp: Int): Entity = ???
  override def takeDamage(hp: Int): Option[Entity] = ???
  override def addEffect(effect: Effect, duration: Duration): Entity = ???
  override def removeEffects(p: Effect => Boolean): Entity = ???
  override def applyEffects: EntityStats = ???
  override def moveTo(position: Position): Unit = ???
  override def tick: Option[Entity] = ???
}

/**
 *
 * @param name                player name
 * @param id                  unique player id
 * @param currentEffects      current effects on the player
 * @param currentHP           current hp of the player
 * @param position            current position of the player
 * @param capacity            inventory size of the player
 * @param inventory           current empty inventory
 * @param equipmentSlots      dedicated slots for equipment
 * @param weapon              dedicated slot for weapon
 * @param armor               dedicated slot for armor
 * @param onCursor            temporary storage of items currently being moved around between player and Chest
 * @param respawnPosition     Coordinates where player will respawn after death
 * @param reachingDistance    Blocks between the player and interaction
 */
case class Player(
                name: String,
                id: String,
                currentEffects: Vector[EffectDuration],
                currentHP: Int,
                position: Position,
                capacity: Int,
                inventory: Chest,
                equipmentSlots: Chest,
                weapon: Weapon,
                armor: Armor,
                onCursor: ItemStack,
                respawnPosition: Position,
                reachingDistance: Double
              ) extends Entity {

  override def baseStats: EntityStats = ???
  override def heal(hp: Int): Entity = ???
  override def takeDamage(hp: Int): Option[Entity] = ???
  override def addEffect(effect: Effect, duration: Duration): Entity = ???
  override def removeEffects(p: Effect => Boolean): Entity = ???
  override def applyEffects: EntityStats = ???
  override def moveTo(position: Position): Unit = ???
  override def tick: Option[Entity] = ???
}

/**
 * The entitystats
 *
 * @param attack          attack damage value
 * @param defense         defense value
 * @param speed           blocks moved per tick
 * @param maxHP           max entity health
 * @param regeneration    HP regenerated per tick)
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
   * @param effect which particular effect to-be applied on the Entity
   * @return an updated EntityStats
   */
  private def applyEffect(effect: Effect): EntityStats = effect match {
    case IncreaseDamage(value) => copy(attack = attack + value)
    case ScaleDefense(percentage) => copy(defense = (defense * percentage).toInt)
    case Poison(value) => copy(regeneration = regeneration - value)
    case _ => this //
  }

  // it can be given a vector of effects and iterates through them applying them all
  private def applyEffect(effects: Effect*): EntityStats =
    effects.foldLeft(this) {
      (stats, effect) => stats.applyEffect(effect)
    }
}

