import org.scalatest.flatspec.AnyFlatSpec

class ItemTest extends AnyFlatSpec {

  "+ (itemStack)" should "<= mennyiségre" in {
    val wood = Blocks("wood", 5)
    val stack1 = ItemStack(wood, 2)
    val stack2 = ItemStack(wood, 2)
    val result = stack1 + stack2
    val expected = (ItemStack(Blocks("wood", 5), 4), None)
    assert(result == expected)
  }

  "+ (itemStack)" should  "túlcsorduló mennyiségre" in {
    val wood = Blocks("wood", 5)
    val stack1 = ItemStack(wood, 3)
    val stack2 = ItemStack(wood, 4)
    val result = stack1 + stack2
    val expected = (ItemStack(Blocks("wood",5),5),Some(ItemStack(Blocks("wood",5),2)))
    assert(result == expected)
  }
}
