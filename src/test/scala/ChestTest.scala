import org.scalatest.flatspec.AnyFlatSpec

class ChestTest extends AnyFlatSpec {

  val item1 = Test("item1", 5)
  val item2 = Test("item2", 10)
  val item3 = Test("item3", 4)

  //  val stack1 = ItemStack(item1, 3)
  //  val stack2 = ItemStack(item2, 5)

  val chest1 = Chest("chest1", 16, Vector(ItemStack(item1, 3), ItemStack(item2, 5)))
  val chest2 = Chest("chest2", 16, Vector())
  val chest3 = Chest("chest3", 16, Vector(
      ItemStack(item1, 3),
      ItemStack(item2, 5),
      null, // empty slot
      null, // empty slot
      ItemStack(item1, 1),
      null, // empty slot
      null, // empty slot
      ItemStack(item2, 9),
      null, // empty slot
  ))

  "methods for non-empty chest or slot" should "isEmpty" in {
    assert(!chest1.isEmpty)
  }

  it should "capacity" in {
    assert(chest1.capacity == 14)
  }

  it should "contains" in {
    assert(chest1.contains(item1))
  }

  it should "apply when index in range" in {
    val expected = Some(ItemStack(Test("item2", 10), 5))
    assert(chest1.apply(1) == expected)
  }

  it should "apply when index out of range" in {
    assert(chest1.apply(3) == None)
  }

  it should "count" in {
    assert(chest1.count(item1) == 3)
  }

  it should "swap with present stack" in {
    val stack3 = ItemStack(Test("SWAPME", 5), 3)
    val expected = (Chest("chest1", 16, Vector(ItemStack(Test("item1", 5), 3), ItemStack(Test("SWAPME", 5), 3))), Some(ItemStack(Test("item2", 10), 5)))
    val result = chest1.swap(1, stack3)
    assert(result == expected)
  }

  it should "operator +, same stack BELOW capacity" in {
    val testStack = ItemStack(Test("item2", 10), 2)
    print(chest3 + testStack)
  }

  //TODO: csak az első előfordulást tölti fel jól és alakul maradék. a maradék nem kerül be
  it should "operator + same stack ABOVE capacity" in {
    val testStack = ItemStack(Test("item2", 10), 5)
    val result = chest3 + testStack
    val expected = (Chest("chest2", 16, Vector(ItemStack(Test("item2", 5), 5))), None)
    //assert(result == expected)
    //assert(chest2.capacity - 1 == result._1.capacity)
    print(chest3 + testStack)
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
    assert(chest3.count(item2) == 14)
  }

  it should "swap, when empty slot" in {
    val test = ItemStack(Test("SWAPME", 5), 3)
    val expected = (Chest("chest3", 16,
      Vector(
        ItemStack(Test("item1",5),3),
        ItemStack(Test("item2",10),5),
        null,
        ItemStack(Test("SWAPME",5),3),
        ItemStack(Test("item1",5),1),
        null,
        null,
        ItemStack(Test("item2",10),9),
        null)), Some(null))
    val result = chest3.swap(3, test)
    assert(result == expected)
  }

  it should "operator + and empty chest" in {
    val testStack = ItemStack(Test("test", 5), 3)
    val result = chest2 + testStack
    val expected = (Chest("chest2",16,Vector(ItemStack(Test("test",5),3))),None)
    assert(result == expected)
    //assert(chest2.capacity - 1 == result._1.capacity)
  }

   it should "operator + no prev appearance, and empty slots" in {
    val testStack = ItemStack(Test("test", 5), 3)
    val result = chest3 + testStack
    val expected = (Chest("chest3",16,
      Vector(
        ItemStack(Test("item1",5),3),
        ItemStack(Test("item2",10),5),
        ItemStack(Test("test",5),3),
        null,
        ItemStack(Test("item1",5),1),
        null,
        null,
        ItemStack(Test("item2",10),10),
        null)),None)
     //assert(result == expected)
    //assert(chest3.capacity - 1 == result._1.capacity)
     print(chest3 + testStack)
  }
}
