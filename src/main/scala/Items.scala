trait Item {
  val name: String
  val maxStackSize: Int
}

trait Placable

case class Chest(id: String, maxCapacity: Int, items: Vector[ItemStack]) extends Placable{
  def isEmpty: Boolean = ???
  def capacity: Int = ???
  def apply(index: Int): Option[ItemStack] = ???
  def +(stack: ItemStack): (Chest, Option[ItemStack]) = ???
  def swap(index: Int, stack:ItemStack): (Chest, Option[ItemStack]) = ???
  def contains(item: Item): Boolean = ???
  def count(item: Item): Int = ???
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

case class Weapon(name: String, damage: Int = 5) extends Item {
  val maxStackSize: Int = 1
  def applyDamage(entity: Entity): Entity = ???
}

case class Armor(name: String, defense: Int = 5) extends Item {
  val maxStackSize: Int = 1
  def applyDefense(entity: Entity): Entity = ???
}

case class Consumable(name: String = "Potion", effects: Vector[Effect]) extends Item {
  val maxStackSize: Int = 4
  def applyEffects(entity: Entity): Entity = ???
}

case class Equipment(name: String, effects: List[Effect]) extends Item {
  val maxStackSize: Int = 1
  def applyEffects(entity: Entity): Entity = ???
}

/**
 *
 * @param inputs    crafting ingredients
 * @param output    crafting result
 */
case class Recipe(inputs: Vector[ItemStack], output: Item) {
  def craftItem(input: Vector[ItemStack]):Option[Item] = ???
}



