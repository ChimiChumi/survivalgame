import org.scalatest.flatspec.AnyFlatSpec

class ItemTest extends AnyFlatSpec {

  "+ (itemStack)" should "<= mennyiségre" in {
    val testItem = Test("itemName", 5)
    val stack1 = ItemStack(testItem, 2)
    val stack2 = ItemStack(testItem, 2)
    val result = stack1 + stack2
    val expected = (ItemStack(Test("itemName", 5), 4), None)
    assert(result == expected)
  }

  "+ (itemStack)" should  "túlcsorduló mennyiségre" in {
    val testItem = Test("itemName", 5)
    val stack1 = ItemStack(testItem, 3)
    val stack2 = ItemStack(testItem, 4)
    val result = stack1 + stack2
    val expected = (ItemStack(Test("itemName",5),5),Some(ItemStack(Test("itemName",5),2)))
    assert(result == expected)
  }
}
