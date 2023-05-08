import org.scalatest.flatspec.AnyFlatSpec

class GameRulesTest extends AnyFlatSpec {
  val item1: Armor          = Armor("shield", 5)
  val item2: Weapon         = Weapon("sword", 10)
  val item3: Weapon         = Weapon("axe", 3)
  val item4: Block          = Block("stone")
  val item5: Block          = Block("wood")
  val item6: Equipment      = Equipment("laser-goggles", Vector(EffectDuration(ScaleDefense(5), UntilDeath)))

  val loot1: Loot           = Loot("stick")
  val loot2: Loot           = Loot("iron")

  val door: Block           = Block("door")
  val sword: Weapon         = item2

  val doorRecipe:  Recipe   = Recipe(Vector[ItemStack](ItemStack(item5, 4), ItemStack(item4, 2)), door)
  val swordRecipe: Recipe   = Recipe(Vector[ItemStack](ItemStack(loot1, 1), ItemStack(loot2, 2)), sword)


  val rules: GameRules = GameRules(
    Vector[Item](item1, item2, item3, item4, item5, item6, loot1, loot2),
    Vector[Recipe](doorRecipe, swordRecipe)
  )

  "methods for gamerules" should "getItems" in {
    val res = rules.getItems(_.isInstanceOf[Item])
    val expected = Some(
      Vector(
        Armor("shield",5),
        Weapon("sword",10),
        Weapon("axe",3),
        Block("stone"),
        Block("wood"),
        Equipment("laser-goggles",Vector(EffectDuration(ScaleDefense(5.0),UntilDeath))),
        Loot("stick"), Loot("iron")
      ))

    assert(res == expected)
  }

  it should "getPlacable" in {
    val res = rules.getPlaceables
    val expected = Some(Vector(Block("stone"), Block("wood")))
    assert(res == expected)
  }

  it should "getConsumable" in {
    val res = rules.getConsumables
    val expected = None
    assert(res == expected)
  }

  it should "getWeapon" in {
    val res = rules.getWeapons
    val expected = Some(Vector(Weapon("sword", 10), Weapon("axe", 3)))
    assert(expected == res)
  }

  it should "getArmor" in {
    val res = rules.getArmors
    val expected = Some(Vector(Armor("shield", 5)))
    assert(res == expected)
  }

  it should "getEquipments" in {
    val res = rules.getEquipments
    val expected = Some(Vector(Equipment("laser-goggles",Vector(EffectDuration(ScaleDefense(5.0),UntilDeath)))))
    assert(res == expected)
  }


  it should "crafting ingredients" in {
    val res = rules.ingredients
    val expected = Some(Vector(Block("wood"), Block("stone"), Loot("stick"), Loot("iron")))
    assert(res == expected)
  }

  it should "crafting results" in {
    val res = rules.craftables
    val expected = Some(Vector(Block("door"), Weapon("sword",10)))
    assert(res == expected)
  }
}
