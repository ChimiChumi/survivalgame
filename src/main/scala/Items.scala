trait Item {
  val name: String
  val maxStackSize: Int
}

trait Placable

/**
 * The chest is a placable item type, allowing the player to place it on the world map.
 * This acts like an itemstack, containing different items.
 * @param id              A unique id differentiating the chests between them
 * @param maxCapacity     The maximum capacity of items it can hold.
 * @param items           Currently stored items inside.
 */
case class Chest(id: String, maxCapacity: Int, items: Vector[ItemStack]) extends Placable{
  def isEmpty: Boolean = ???  // returns if chest is empty or not
  def capacity: Int = ???  // available slots in chest
  def apply(index: Int): Option[ItemStack] = ??? //gets the itemstack from the chest
  def +(stack: ItemStack): (Chest, Option[ItemStack]) = ??? // adding items to the chest
  def swap(index: Int, stack:ItemStack): (Chest, Option[ItemStack]) = ??? // tries to swaps items
                                                                          // with a stack currently inside
  def contains(item: Item): Boolean = ???  // returns whether the item is inside the chest
  def count(item: Item): Int = ???  // returns the amount of a given item stored in chest
}


/**
 * The ItemStack tries to combine two itemstacks.
 * - if the two can be joined, it will be merged
 * - if the two overflows, fills the first and a new stack is created from the remaining amount
 *
 * @param item  item to be stacked
 * @param count amount of the item
 */
case class ItemStack(item: Item, quantity: Int) {
  require(quantity > 0, "Amount must be positive!")
  require(quantity >= 0 && quantity <= item.maxStackSize, "Invalid count")

  def +(that: ItemStack): (ItemStack, Option[ItemStack]) = {
    if (this.item != that.item) {
      (this, Some(that)) // if the two are not the same items, we return them both
    }

    else {
      val totalQuant = this.quantity + that.quantity // checks what would be the total quantity
      if (totalQuant <= item.maxStackSize) {
        // if it fits, we merge them
        (ItemStack(this.item, totalQuant), None)
      }

      else {
        // if it overflows, we top up the first then create a new with the rest
        (ItemStack(this.item, item.maxStackSize), Some(ItemStack(this.item, totalQuant - item.maxStackSize)))
      }
    }
  }
}


//TODO ezek szerintem nem jók. Player EntityStats-jánál kell módosítani ? Consumable, Armor effektek ?

/**
 * Weapon item type providing defense. i.e: Axe, Sword, etc.
 * Only one can be equipped at a time. Doesn't stack.
 *
 * @param name      item name
 * @param damage    attack damage stat value
 */
case class Weapon(name: String, damage: Int) extends Item {
  override val maxStackSize: Int = 1
  require(damage > 0, "Damage value must be positive!")
  def applyDamage(entity: Entity): Entity = ???
}


/**
 * Armor item type providing defense. i.e: Helmet, ChestPlate, etc.
 * Only one can be equipped at a time. Doesn't stack.
 *
 * @param name      item name
 * @param defense   defense stat value
 */
case class Armor(name: String, defense: Int) extends Item {
  override val maxStackSize: Int = 1
  require(defense > 0, "Defense value must be positive!")
  def applyDefense(entity: Entity): Entity = ???
}

/**
 * Consumable item type providing effects. i.e: Apple, Raw Meat, Potion, etc.
 *
 * @param name      item name
 * @param effects   possible effects
 */
case class Consumable(name: String, effects: Vector[Effect]) extends Item {
  override val maxStackSize: Int = 3 // random pre defined number for consumable stack
  def applyEffects(entity: Entity): Entity = ???
}

/**
 * Equipment item type providing effects. i.e: Shield, Enchanted Pickaxe, etc.
 *
 * @param name      item name
 * @param effects   possible effects
 */
case class Equipment(name: String, effects: Vector[Effect]) extends Item {
  override val maxStackSize: Int = 1
  def applyEffects(entity: Entity): Entity = ???
}

/**
 * A recipe is used to craft new items from materials.
 * @param inputs    crafting ingredients (i.e: 2 wood, 3 stone, etc)
 * @param output    crafting result (i.e: 1 sword)
 */
case class Recipe(inputs: Vector[ItemStack], output: Item) {
  def craftItem(input: Vector[ItemStack]):Option[Item] = ???
}



