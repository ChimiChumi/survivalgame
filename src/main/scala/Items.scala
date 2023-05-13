import scala.collection.immutable._

trait Item extends Serializable{
  val id: String
  val maxStackSize: Int

  /**
   * Checking if two ItemStacks are the same
   * @param that    other ItemStack
   */
  override def equals(that: Any): Boolean = {
    that match {
      case that: Item => that.id == this.id && that.maxStackSize == this.maxStackSize
      case _ => false
    }
  }
}

trait Placable extends Item

/**
 * The chest is a placable item type, allowing the player to place it on the world map.
 * This acts like an itemstack, containing different items.
 *
 * @param id          A unique id differentiating the chests between them
 * @param maxSlots    The maximum capacity of items it can hold.
 * @param items       Currently stored items inside.
 */
case class Chest(id: String, maxSlots: Int, items: Vector[ItemStack]) extends Placable {
  require(maxSlots > 0, s"The capacity for '$id' Chest must be a positive number! (given: $maxSlots)")
  require(items.length <= maxSlots, s"The '$id' Chest cannot contain more than it's capacity: $maxSlots !")

  override val maxStackSize: Int = 5

  def isEmpty: Boolean = items.isEmpty

  def capacity: Int = maxSlots - items.count(_ != null) // number of available slots (i.e: [#] [#] [#] [_] [_])

  // asking for a specific stack on a given slot
  def apply(index: Int): Option[ItemStack] = {
    if (index >= 0 && index < items.length) Some(items(index))
    else None
  }

  def swap(index: Int, stack: ItemStack): (Chest, Option[ItemStack]) = {
    if (index >= maxSlots || index < 0) {
      (this, Some(stack))
    }
    else {
      val stackInChest = this.items(index)
      val newVector = this.items.updated(index, stack)

      if(stackInChest == null) (this.copy(items = newVector), None)
      else (this.copy(items = newVector), Some(stackInChest))
    }
  }

  def contains(item: Item): Boolean = items.exists(_.item == item)

  // counts how many of the given item is present in the chest
  def count(item: Item): Int = items.foldLeft(0) { (sum, current) =>
    if (current != null && item == current.item)
      sum + current.quantity
    else
      sum
  }

  // adding stacks of items to the chest
  def vectorItemStackRec(chest: Chest, toPutInChestStack: ItemStack): (Chest, Option[ItemStack]) = {
    if (toPutInChestStack == null) {
      (chest, None)
    } else {
      val itemIndex = chest.items.indexWhere(itemStack => {
        itemStack != null && itemStack.item == toPutInChestStack.item && itemStack.quantity < toPutInChestStack.item.maxStackSize
      })
      if (itemIndex <= 0) {

        val index = chest.items.indexWhere(_ == null) // checking for empty slot

        if (index >= 0) { // found an empty slot
          val newItems = chest.items.updated(index, toPutInChestStack)
          (chest.copy(items = newItems), None)
        }
        else
        // no empty slot but we still have remaining items
          (chest, Option[ItemStack](toPutInChestStack))

      }
      else {
        //temporary stack
        val existingStack = chest.items(itemIndex)

        //new quantity
        val newQuantity = existingStack.quantity + toPutInChestStack.quantity

        //remaining quantity after filling up stack

        val remainingItemStack = if (newQuantity > existingStack.item.maxStackSize) toPutInChestStack.copy(
          quantity = newQuantity - existingStack.item.maxStackSize) else null

        //filling up stack
        val newStack = ItemStack(existingStack.item, math.min(newQuantity, existingStack.item.maxStackSize))

        // updated stack
        val updatedItems = chest.items.updated(itemIndex, newStack)

        vectorItemStackRec(chest.copy(items = updatedItems), remainingItemStack)

      }
    }
  }

  def +(stack: ItemStack): (Chest, Option[ItemStack]) = {
    if (this.isEmpty)
      (Chest(id, maxSlots, items.appended(stack)), None)

    //if chest has items inside
    else {
      vectorItemStackRec(this, stack)
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
 * @param item        item to be stacked
 * @param quantity    amount of the item
 */
case class ItemStack(item: Item, quantity: Int) {
  require(quantity > 0 && quantity <= item.maxStackSize, s"The quantity for ${item.id} has to be greater than 0 and lower than ${item.maxStackSize}!")

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
      case that: ItemStack => that.item.equals(this.item) && that.quantity == this.quantity
      case _ => false
    }
}

/**
 * Placable blocks
 * @param id     name of the block (i.e: wood, stone, brick, etc.)
 */
case class Block(id: String) extends Placable{
  override val maxStackSize: Int = 16
}

/**
 * Loot dropped by dead entity. It should get in the player's inventory.
 * @param id    name of the item
 */
case class Loot(id: String) extends Item{
  override val maxStackSize: Int = 12
}


/**
 * Weapon item type providing defense. (i.e: Axe, Sword, etc.)
 * Only one can be equipped at a time. Doesn't stack.
 *
 * @param id   item name
 * @param damage attack damage stat value
 */
case class Weapon(id: String, damage: Int) extends Item {
  override val maxStackSize: Int = 1
  require(damage > 0, s"Weapon ($id) damage value must be positive! (current: $damage")
  def equipWeapon(stats: EntityStats): EntityStats = stats.copy(attack = stats.attack + damage)
  def unequipWeapon(stats: EntityStats): EntityStats = stats.copy(attack = stats.attack - damage)
}

/**
 * Armor item type providing defense. (i.e: Helmet, ChestPlate, etc.)
 * Only one can be equipped at a time. Doesn't stack.
 *
 * @param id    item name
 * @param defense defense stat value
 */
case class Armor(id: String, defense: Int) extends Item {
  override val maxStackSize: Int = 1
  require(defense > 0, s"Armor ($id) defense value must be positive! (current: $defense")
  def equipArmor(stats: EntityStats): EntityStats = stats.copy(defense = stats.defense + defense)
  def unequipArmor(stats: EntityStats): EntityStats = stats.copy(defense = stats.defense - defense)
}

/**
 * Consumable item type providing effects. i.e: Apple, Raw Meat, Potion, etc.
 *
 * @param id          item name
 * @param effects     possible effects
 */
case class Consumable(id: String, effects: Vector[EffectDuration]) extends Item {
  override val maxStackSize: Int = 3 // random pre-defined number for consumable stack
}

/**
 * Equipment item type providing effects. i.e: Shield, Enchanted Pickaxe, etc.
 *
 * @param id        item name
 * @param effects   possible effects
 */
case class Equipment(id: String, effects: Vector[EffectDuration]) extends Item {
  override val maxStackSize: Int = 1

  override def equals(that: Any): Boolean =
    that match {
      case that: Equipment => that.id == this.id
      case _ => false
    }
}

/**
 * A recipe is used to craft new items from materials.
 *
 * @param inputs crafting ingredients (i.e: 2 wood, 3 stone, etc)
 * @param output crafting result (i.e: 1 sword)
 */
case class Recipe(inputs: Vector[ItemStack], output: Item) extends Serializable {
  def craftItem(input: Vector[ItemStack]): Option[Item] = {

    val inputSet = inputs.toSet
    val expectedSet = input.toSet  // method parameter

    val res = inputSet.diff(expectedSet)

    if(res.isEmpty) Some(output)
    else None
  }
}



