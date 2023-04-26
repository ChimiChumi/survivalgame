import org.scalatest.flatspec.AnyFlatSpec

class ItemTest extends AnyFlatSpec {

  "+ (itemStack)" should "<= mennyiségre" in {
    val wood = Blocks("wood")
    val stack1 = ItemStack(wood, 2)
    val stack2 = ItemStack(wood, 2)
    val result = stack1 + stack2
    val expected = (ItemStack(Blocks("wood"), 4), None)
    assert(result == expected)
  }

  "+ (itemStack)" should  "túlcsorduló mennyiségre" in {
    val wood = Blocks("wood")
    val stack1 = ItemStack(wood, 10)
    val stack2 = ItemStack(wood, 8)
    val result = stack1 + stack2
    val expected = (ItemStack(Blocks("wood"),16),Some(ItemStack(Blocks("wood"),2)))
    assert(result == expected)
  }
}
