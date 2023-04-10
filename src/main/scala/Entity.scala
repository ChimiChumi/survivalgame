trait Entity {
  val name: String
  val id: String
  val currentEffects: Vector[EffectDuration]
  val currentHP: Int
  val position: Position

  //TODO: baseStat
  def baseStats: EntityStats
  def heal(hp: Int): Entity
  def takeDamage(hp: Int): Option[Entity]
  def addEffect(effect: Effect, duration: Duration): Entity
  def removeEffects(p: Effect => Boolean): Entity
  def applyEffects: EntityStats
  def moveTo(position: Position)
  def tick: Option[Entity]
}

case class Mob(
                name: String,
                id: String,
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
 * Az entitásoknak a statjai
 *
 * @param attack       támadás
 * @param defense      védelem
 * @param speed        sebesség
 * @param maxHP        maximális életerő
 * @param regeneration regenerálódás (HP per tick)
 */
case class EntityStats(
                        attack: Int,
                        defense: Int,
                        speed: Double,
                        maxHP: Int,
                        regeneration: Double
                      ) {

  /**
   *
   * @param effect alkalmazandó effekt
   * @return
   */
  private def applyEffect(effect: Effect): EntityStats = effect match {
    case IncreaseDamage(value) => copy(attack = attack + value)
    case ScaleDefense(percentage) => copy(defense = (defense * percentage).toInt)
    case Poison(value) => copy(regeneration = regeneration - value)
    case _ => this // ha az effekt nem tartozik egyik altípusba sem, akkor nem változtatjuk meg az állapotot
  }

  private def applyEffect(effects: Effect*): EntityStats =
    effects.foldLeft(this) {
      (stats, effect) => stats.applyEffect(effect)
    }
}

