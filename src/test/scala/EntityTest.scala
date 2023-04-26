import org.scalatest.flatspec.AnyFlatSpec

class EntityTest extends AnyFlatSpec {

  val player: Player = Player(
    "player",
    "player1",
    EntityStats(5, 0, 1, 100, 5),
    Vector(EffectDuration(null, null)),
    100,
    Position(0,0),
    10,
    Chest("inventory", 10, Vector[ItemStack]()),
    Chest("equipments", 4, Vector[ItemStack]()),
    null,
    Position(0,0),
    2,
    None,
    None
  )

  val zombie = Mob(
    "zombie",
    "mob1",
    EntityStats(10, 5, 2, 75, 0),
    Vector(EffectDuration(null, null)),
    100,
    Position(0, 0),
  )

  "methods for players" should "heal" in {
    val test = player.copy(currentHP = 93)
    // test.heal(5)
    // test.heal(5)
    // print(test.currentHP) TODO miert nem jo igy? nem frissul
    print(test.heal(5).heal(5).currentHP)
  }

  it should "consume" in { //TODO nem megy addEffect miatt
    val consumable1: Consumable = Consumable("rotten_meat", Vector(EffectDuration(Poison(10), TicksLeft(5))))
    val consumable2: Consumable = Consumable("spinach", Vector(EffectDuration(IncreaseDamage(10), Permanent)))
    print(player.consume(consumable1).consume(consumable2).currentEffects)
  }

  it should "equip" in {  //TODO nem megy

  }

  it should "takeDamage" in {
    print(player.takeDamage(25))
  }

  it should "addEffect" in {  //TODO nem megy
    val effect = EffectDuration(IncreaseDamage(5), Permanent)
    print(player.addEffect(effect).currentEffects)
  }

  it should "removeEffect" in {
    val effect1 = EffectDuration(IncreaseDamage(5), Permanent)
    val effect2 = EffectDuration(Poison(5), Permanent)
    val test = player.copy(currentEffects = Vector[EffectDuration](effect1, effect2))
    print(test.removeEffects(_.isInstanceOf[IncreaseDamage]).currentEffects)
  }

  it should "moveTo" in {
    print(player.moveTo(Position(1,2)).position)
  }
}
