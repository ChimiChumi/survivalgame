import org.scalatest.flatspec.AnyFlatSpec

class GameRulesTest extends AnyFlatSpec {
  val item1 = Armor("shield", 5)
  val item2 = Weapon("sword", 10)
  val item3 = Weapon("axe", 3)
  val item4 = Blocks("stone", 4)
  val item5 = Blocks("wood", 4)
  val item6 = Blocks("door", 3)


  val door = Recipe(Vector[ItemStack](ItemStack(item5, 4), ItemStack(item4, 2)), item6)


  val rules = GameRules(
    Vector[Item](item1, item2, item3, item4, item5, item6),
    Vector[Recipe](door)
  )

  "methods for gamerules" should "getPlacable" in {
    print(rules.getPlaceables)
  }

  it should "getWeapon" in {
    val expected = Some(Vector(Weapon("sword", 10), Weapon("axe", 3)))
    val res = rules.getWeapons
    assert(expected == res)
  }

  it should "getArmor" in {
    print(rules.getArmors)
  }

  it should "getConsumable" in {
    print(rules.getConsumables)
  }

  it should "getItems" in {
    print(rules.getItems(_.isInstanceOf[Item]))
  }

  it should "getConsumables" in {
    print(rules.getConsumables)
  }

  it should "getEquipments" in {
    print(rules.getEquipments)
  }

  it should "materials" in {
    print(rules.materials)
  }

  it should "craftables" in {
    print(rules.craftables)
  }
}
