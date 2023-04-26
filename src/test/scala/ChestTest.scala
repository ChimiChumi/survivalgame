import org.scalatest.flatspec.AnyFlatSpec

class ChestTest extends AnyFlatSpec {

  val item1 = Blocks("brick")
  val item2 = Blocks("wood")
  val item3 = Blocks("stone")

  val stack1 = ItemStack(item1, 3)
  //  val stack2 = ItemStack(item2, 5)

  val chest1 = Chest("chest1", 16, Vector(ItemStack(item1, 3), ItemStack(item2, 5)))
  val chest2 = Chest("chest2", 16, Vector())
  val chest3 = Chest("chest3", 16, Vector(
      ItemStack(item1, 3),
      ItemStack(item2, 12),
      null, // empty slot
      null, // empty slot
      ItemStack(item1, 1),
      null, // empty slot
      null, // empty slot
      ItemStack(item2, 14),
      null, // empty slot
      null, // empty slot
  ))

  "methods for non-empty chest or slot" should "isEmpty" in {
    assert(!chest1.isEmpty)
  }

  it should "capacity" in {
    assert(chest3.capacity == 14)
  }

  it should "contains" in {
    assert(chest1.contains(item1))
  }

  it should "apply when index in range" in {
    val expected = Some(ItemStack(Blocks("wood"), 5))
    assert(chest1.apply(1) == expected)
  }

  it should "apply when index out of range" in {
    assert(chest1.apply(3) == None)
  }

  it should "count" in {
    assert(chest1.count(item1) == 3)
  }

  it should "swap with present stack" in {
    val stack3 = ItemStack(Blocks("SWAP_ME!"), 3)
    val expected = (Chest("chest1", 16, Vector(ItemStack(Blocks("brick"), 3), ItemStack(Blocks("SWAP_ME!"), 3))), Some(ItemStack(Blocks("wood"), 5)))
    val result = chest1.swap(1, stack3)
    assert(result == expected)
  }

  it should "operator +, same stack BELOW capacity" in {
    val testStack = ItemStack(Blocks("wood"), 2)
    val result = chest3 + testStack
    val expected = (Chest("chest3", 16,
      Vector(
        ItemStack(Blocks("brick"), 3),
        ItemStack(Blocks("wood"), 14),
        null,
        null,
        ItemStack(Blocks("brick"), 1),
        null,
        null,
        ItemStack(Blocks("wood"), 14),
        null,
        null)), None)
    assert(result == expected)
  }

  it should "operator + same stack ABOVE capacity" in {
    val testStack = ItemStack(Blocks("wood"), 10)
    val result = chest3 + testStack
    val expected = (Chest("chest3",16,
      Vector(
        ItemStack(Blocks("brick"),3),
        ItemStack(Blocks("wood"),16),
        ItemStack(Blocks("wood"),4),
        null,
        ItemStack(Blocks("brick"),1),
        null,
        null,
        ItemStack(Blocks("wood"),16),
        null,
        null)),None)

    assert(result == expected)
  }

  /** ----------------------------------------------------------------------------------------- */

  "methods for empty chest or slot" should "isEmpty" in {
    assert(chest2.isEmpty)
  }


  it should "capacity for empty chest" in {
    assert(chest2.capacity == 16)
  }

  it should "capacity with empty slots" in {
    assert(chest3.capacity == 12)
  }

  it should "contains" in {
    assert(!chest2.contains(item1))
  }

  it should "apply when vector is empty" in {
    assert(chest2.apply(0) == None)
  }

  it should "count" in {
    assert(chest2.count(item1) == 0)
  }

  it should "count with empty slots" in {
    assert(chest3.count(item2) == 26)
  }

  it should "swap, when empty slot" in {
    val test = ItemStack(Blocks("SWAP_ME!"), 3)
    val expected = (Chest("chest3", 16,
      Vector(
        ItemStack(Blocks("brick"),3),
        ItemStack(Blocks("wood"),5),
        null,
        ItemStack(Blocks("SWAP_ME!"),3),
        ItemStack(Blocks("brick"),1),
        null,
        null,
        ItemStack(Blocks("wood"),9),
        null,
        null)), Some(null))
    val result = chest3.swap(3, test)
    assert(result == expected)
  }

  it should "operator + and empty chest" in {
    val testStack = ItemStack(Blocks("stone"), 3)
    val result = chest2 + testStack
    val expected = (Chest("chest2",16,Vector(ItemStack(Blocks("stone"),3))),None)
    assert(result == expected)
  }

   it should "operator + no prev appearance, and empty slots" in {
    val testStack = ItemStack(Blocks("stone"), 3)
    val result = chest3 + testStack
    val expected = (Chest("chest3",16,
      Vector(
        ItemStack(Blocks("brick"),3),
        ItemStack(Blocks("wood"),5),
        ItemStack(Blocks("stone"),3),
        null,
        ItemStack(Blocks("brick"),1),
        null,
        null,
        ItemStack(Blocks("wood"),9),
        null,
        null)),None)
     assert(result == expected)
  }
}
