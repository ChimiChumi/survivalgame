/**
 * The global rules of the world.
 * It consists of multiple getters for items, recipes and other things present in the game.
 *
 * @param itemTypes      Items used in the game
 * @param recipeTypes    Crafting recipes
 */
case class GameRules(itemTypes: Vector[Item], recipeTypes:Vector[Recipe]) {
  def getItems(p: Item => Boolean): Option[Vector[Item]] = {
    val items = itemTypes.filter(p)
    if (items.nonEmpty) Some(items) else None
  }

  def getPlaceables: Option[Vector[Placable]] = {
    val placeables = itemTypes.collect { case p: Placable => p }
    if (placeables.nonEmpty) Some(placeables) else None
  }

  def getConsumables: Option[Vector[Consumable]] = {
    val consumables = itemTypes.collect { case c: Consumable => c }
    if (consumables.nonEmpty) Some(consumables) else None
  }

  def getWeapons: Option[Vector[Weapon]] = {
    val weapons = itemTypes.collect { case w: Weapon => w }
    if (weapons.nonEmpty) Some(weapons) else None
  }

  def getArmors: Option[Vector[Armor]] = {
    val armors = itemTypes.collect { case a: Armor => a }
    if (armors.nonEmpty) Some(armors) else None
  }

  def getEquipments: Option[Vector[Equipment]] = {
    val equipments = itemTypes.collect { case e: Equipment => e }
    if (equipments.nonEmpty) Some(equipments) else None
  }

  def materials: Option[Vector[Item]] = {
    val inputItems = recipeTypes.flatMap( item =>  item.inputs.map(_.item))
    val distinctItems = inputItems.distinct
    if (distinctItems.forall(itemTypes.contains)) Some(distinctItems)
    else None
  }

  def craftables: Option[Vector[Item]] = {
    val outputs = recipeTypes.map( item => item.output).distinct
    if (outputs.nonEmpty) Some(outputs)
    else None
  }
}
