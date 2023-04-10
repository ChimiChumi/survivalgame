/**
 * The global rules of the world.
 * It consists of multiple getters for items, recipes and other things present in the game.
 *
 * @param itemTypes      Items used in the game
 * @param recipeTypes    Crafting recipes
 */
case class GameRules(itemTypes: Vector[Item], recipeTypes:Vector[Item]) {
  def getItems(p: Item => Boolean):Option[Vector[Item]] = ???
  def getPlaceables(p: Placable => Boolean): Option[Vector[Placable]] = ???
  def getConsumable(p: => Boolean): Option[Vector[Consumable]] = ???
  def getWeapon(p: Weapon => Boolean): Option[Vector[Weapon]] = ???
  def getArmor(p: Armor => Boolean): Option[Vector[Armor]] = ???
  def getEquipment(p: Equipment => Boolean): Option[Vector[Equipment]] = ???
  def materials:Option[Vector[Item]] = ???
  def craftables: Option[Vector[Item]] = ???
}
