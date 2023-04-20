import org.scalatest.flatspec.AnyFlatSpec

class ChestTest extends AnyFlatSpec {
  val item1 = Test("item1", 5)
  val item2 = Test("item2", 10)

  val stack1 = ItemStack(item1, 3)
  val stack2 = ItemStack(item2, 5)

  val chest1 = Chest("chest1", 16, Vector[ItemStack](stack1, stack2))
  val chest2 = Chest("chest2", 16, Vector[ItemStack]())

  "chest.isEmpty" should "non-empty chest" in {
    assert(!chest1.isEmpty)
  }

  "chest.capacity" should "non-empty chest" in {
    assert(chest1.capacity == 14)
  }

  "chest.contains" should "non-empty chest" in {
    assert(chest1.contains(item1))
  }

  "chest.apply" should "non-empty chest" in {
    val expected = Some(ItemStack(Test("item2",10),5))
    assert(chest1.apply(1) == expected)
  }

  "chest.apply ha rossz index" should "non-empty chest" in {
    assert(chest1.apply(3) == None)
  }

  "chest.count" should "non-empty chest" in {
    assert(chest1.count(item1) == 3)
  }

  "chest.swap" should "occupied slot" in {
    val stack3 = ItemStack(Test("SWAPME", 5), 3)
    val expected = (Chest("chest1",16,Vector(ItemStack(Test("item1",5),3), ItemStack(Test("SWAPME",5),3))),Some(ItemStack(Test("item2",10),5)))
    val result = chest1.swap(1, stack3)
    assert(result == expected)
  }

  "chest.swap" should "empty slot" in {
    val stack3 = ItemStack(Test("SWAPME", 5), 3)
    val expected = (Chest("chest2",16,Vector(ItemStack(Test("SWAPME",5),3))),None)
    val result = chest2.swap(0, stack3)
    assert(result == expected)
  }

  "chest + " should "empty chest" in {
    val testStack = ItemStack(Test("test", 5), 3)
    val expected = (Chest("chest2",16,Vector(ItemStack(Test("test",5),3))),None)
    assert(chest2 + testStack == expected)
  }

  "chest + " should "non-empty chest" in {
    val testStack = ItemStack(Test("test", 5), 3)
    val result = chest1 + testStack
    val expected = (Chest("chest1",16,Vector(ItemStack(Test("item1",5),3), ItemStack(Test("item2",10),5), ItemStack(Test("test",5),3))),None)
    assert(result == expected)
    //assert(chest1.capacity - 1 == result._1.capacity)
  }

  "chest + " should "same stack" in {
    //val stack1 = ItemStack(item1, 3)
    //val stack2 = ItemStack(item2, 5)
    //val chest1 = Chest("chest1", 16, Vector[ItemStack](stack1, stack2))

    val testStack = ItemStack(Test("item2", 10), 5)
    print(chest1 + testStack)
  }
}
