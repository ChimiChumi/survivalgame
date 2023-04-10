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
 * Az ItemStack metódus megpróbálja egy stackbe rakni a két itemstacket.
 * - ha elfér a kezünkben, egyesíti ugyanazzal az itemmel
 * - ha túlcsordul akkor új stackre bomlik
 *
 * @param item  stackelni kívánt item
 * @param count mennyi van belőle
 */
case class ItemStack(item: Item, quantity: Int) {
  require(quantity > 0, "Amount must be positive!")
  require(quantity >= 0 && quantity <= item.maxStackSize, "Invalid count")

  def +(that: ItemStack): (ItemStack, Option[ItemStack]) = {
    if (this.item != that.item) {
      (this, Some(that)) // két itemstack nem egyezik, visszaadjuk őket külön
    }

    else {
      val totalQuant = this.quantity + that.quantity //megnezi hogy a stackelendő item összesen mennyit tesz ki
      if (totalQuant <= item.maxStackSize) {
        //ha belefér, belerakjuk a bal koordinátára és stackmérete nő
        (ItemStack(this.item, totalQuant), None)
      }

      else {
        // ha túlcsordul, visszaad egy teljes stacket és a maradékot új stackben
        (ItemStack(this.item, item.maxStackSize), Some(ItemStack(this.item, totalQuant - item.maxStackSize)))
      }
    }
  }
}

/**
 * Lehetséges itemtípusok
 */
//TODO ezek szerintem nem jók. Player EntityStats-jánál kell módosítani ? Consumable, Armor effektek ?

// Weapon item type. Only 1 can be equipped at a time.
case class Weapon(name: String, damage: Int) extends Item {
  override val maxStackSize: Int = 1
  require(damage > 0, "Damage value must be positive!")
  def applyDamage(entity: Entity): Entity = ???
}

// Armor item type. Only 1 can be equipped at a time.
case class Armor(name: String, defense: Int) extends Item {
  override val maxStackSize: Int = 1
  require(defense > 0, "Defense value must be positive!")
  def applyDefense(entity: Entity): Entity = ???
}

// Consumable item type providing effects. i.e: Apple, Meat, Potion, etc.
case class Consumable(name: String, effects: Vector[Effect]) extends Item {
  override val maxStackSize: Int = 4
  def applyEffects(entity: Entity): Entity = ???
}

// Equipment item type providing effects. i.e: Shield, Enchanted Pickaxe, etc.
case class Equipment(name: String, effects: List[Effect]) extends Item {
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



