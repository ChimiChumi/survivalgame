trait Item {
  val id: String
  val maxStackSize: Int
}

trait Placable extends Item

/**
 * The chest is a placable item type, allowing the player to place it on the world map.
 * This acts like an itemstack, containing different items.
 *
 * @param id          A unique id differentiating the chests between them
 * @param maxCapacity The maximum capacity of items it can hold.
 * @param items       Currently stored items inside.
 */
case class Chest(id: String, maxCapacity: Int, items: Vector[ItemStack]) extends Placable {
  require(maxCapacity > 0, s"Chest '$id' capacity must be a positive number!")
  require(items.length <= maxCapacity, s"Chest '$id' cannot have more than $maxCapacity items")

  override val maxStackSize: Int = 5

  def isEmpty: Boolean = items.isEmpty

  def capacity: Int = maxCapacity - items.count(_ != null) // number of available slots (i.e: [#] [#] [#] [_] [_])

  // asking for a specific stack on a given slot
  def apply(index: Int): Option[ItemStack] = {
    if (index >= 0 && index < items.length) Some(items(index))
    else None
  }

  def swap(index: Int, stack: ItemStack): (Chest, Option[ItemStack]) = {
    if (index >= maxCapacity || index < 0) {
      (this, Some(stack))
    }
    else {
      val (left, right) = items.splitAt(index)
      val updatedItems = left ++ Vector(stack) ++ right.drop(1)
      val updatedChest = this.copy(items = updatedItems)
      (updatedChest, right.headOption)
    }
  }

  def contains(item: Item): Boolean = items.exists(_.item == item)

  def count(item: Item): Int = items.foldLeft(0) { (sum, current) =>
    if (current != null && item == current.item)
      sum + current.quantity
    else
      sum
  }

  // adding stacks of items to the chest
  def +(stack: ItemStack): (Chest, Option[ItemStack]) = {
    if (this.isEmpty)
      (Chest(id, maxCapacity, items.appended(stack)), None)

    //if chest has items inside
    else {
      var quantity = stack.quantity //original quantity
      var currentItems = items //original item vector

      while (quantity > 0) {
        // index where the same stack is found
        val itemIndex = currentItems.indexWhere(item => {
          item != null && item.item == stack.item && item.quantity < stack.item.maxStackSize
        })

        // found a matching stack
        if (itemIndex >= 0) {
          //temporary stack
          val existingStack = items(itemIndex)

          //new quantity
          val newQuantity = existingStack.quantity + quantity

          //remaining quantity after filling up stack
          quantity = newQuantity - existingStack.item.maxStackSize

          //filling up stack
          val newStack = ItemStack(existingStack.item, math.min(newQuantity, existingStack.item.maxStackSize))

          // updated stack
          currentItems = currentItems.updated(itemIndex, newStack)
        }

        // no matching stack with space found
        else {
          val index = currentItems.indexWhere(_ == null) // checking for empty slot

          if (index >= 0) { // found an empty slot
            val newItems = currentItems.updated(index, ItemStack(stack.item, quantity))
            return (Chest(id, maxCapacity, newItems), None)
          }
          else
          // no empty slot but we still have remaining items
            return (this, Option[ItemStack](ItemStack(stack.item, quantity)))
        }
      }
      // feltoltottuk az osszes stacket
      (Chest(id, maxCapacity, currentItems), None)
    }
  }
}


/**
 * The ItemStack tries to combine two itemstacks.
 *    - if the two can be joined, it will be merged
 *
 *    - if the two overflow, the first stack is filled
 *      and a new stack is created from the remaining amount
 *
 * @param item  item to be stacked
 * @param count amount of the item
 */
case class ItemStack(item: Item, quantity: Int) {
  require(quantity > 0 && quantity <= item.maxStackSize, "The amount has to be greater than 0 and lower than a full stack!")

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

  override def equals(that: Any): Boolean =
    that match {
      case that: ItemStack => that.item.id == this.item.id
      case _ => false
    }
}

/**
 * Placable blocks
 * @param id              name of the block (i.e: wood, stone, brick, etc.)
 * @param maxStackSize    stackable sizes
 */
case class Blocks(id: String, override val maxStackSize: Int) extends Placable


/**
 * Weapon item type providing defense. (i.e: Axe, Sword, etc.)
 * Only one can be equipped at a time. Doesn't stack.
 *
 * @param name   item name
 * @param damage attack damage stat value
 */
case class Weapon(id: String, damage: Int) extends Item {
  override val maxStackSize: Int = 1
  require(damage > 0, "Damage value must be positive!")
  def equipWeapon(stats: EntityStats): EntityStats = stats.copy(attack = stats.attack + damage)
  def unequipWeapon(stats: EntityStats): EntityStats = stats.copy(attack = stats.attack - damage)
}

/**
 * Armor item type providing defense. (i.e: Helmet, ChestPlate, etc.)
 * Only one can be equipped at a time. Doesn't stack.
 *
 * @param name    item name
 * @param defense defense stat value
 */
case class Armor(id: String, defense: Int) extends Item {
  override val maxStackSize: Int = 1
  require(defense > 0, "Defense value must be positive!")
  def equipArmor(stats: EntityStats): EntityStats = stats.copy(defense = stats.defense + defense)
  def unequipArmor(stats: EntityStats): EntityStats = stats.copy(defense = stats.defense - defense)
}

/**
 * Consumable item type providing effects. i.e: Apple, Raw Meat, Potion, etc.
 *
 * @param name    item name
 * @param effects possible effects
 */
case class Consumable(id: String, effects: Vector[EffectDuration]) extends Item {
  override val maxStackSize: Int = 3 // random pre-defined number for consumable stack
}

/**
 * Equipment item type providing effects. i.e: Shield, Enchanted Pickaxe, etc.
 *
 * @param name    item name
 * @param effects possible effects
 */
case class Equipment(id: String, effects: Vector[EffectDuration]) extends Item {
  override val maxStackSize: Int = 1
}

/**
 * A recipe is used to craft new items from materials.
 *
 * @param inputs crafting ingredients (i.e: 2 wood, 3 stone, etc)
 * @param output crafting result (i.e: 1 sword)
 */
case class Recipe(inputs: Vector[ItemStack], output: Item) {
  def craftItem(input: Vector[ItemStack]): Option[Item] = ???
}



