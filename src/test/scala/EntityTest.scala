import org.scalatest.flatspec.AnyFlatSpec

class EntityTest extends AnyFlatSpec {

  val player: Player = Player(
    "player",
    "player1",
    EntityStats(5, 0, 1, 100, 5),  //eredeti stat
    EntityStats(5, 0, 1, 91, 5),   //jelenlegi stat
    Vector(),
    Position(0, 0),
    10,
    Chest("inventory", 10, Vector[ItemStack]()),
    Set(),
    null,
    Position(0, 0),
    2,
    None,
    None
  )

  val zombie = Mob(
    "zombie",
    "mob1",
    EntityStats(10, 5, 2, 75, 0),  // eredeti
    EntityStats(10, 5, 2, 75, 0),  // jelenlegi
    Vector(EffectDuration(null, null)),
    Position(0, 0),
  )

  "methods for players" should "heal" in {
    print(player.heal(5).hp)
  }

  it should "consume" in {
    val consumable1: Consumable = Consumable("rotten_meat", Vector(EffectDuration(Poison(10), TicksLeft(5))))
    val consumable2: Consumable = Consumable("spinach", Vector(EffectDuration(IncreaseDamage(10), Permanent)))
    print(player.consume(consumable1).consume(consumable2).currentEffects)
  }

  it should "equip" in {
    val equipment: Equipment = Equipment("teszt", Vector(EffectDuration(IncreaseDamage(10), Permanent)))
    print(player.equip(equipment))
  }

  it should "takeDamage" in {
    print(player.takeDamage(25))
  }

  it should "addEffect" in {
    val effect = EffectDuration(IncreaseDamage(5), TicksLeft(10))
    val effect2 = EffectDuration(IncreaseDamage(5), UntilDeath)
    val effect3 = EffectDuration(IncreaseDamage(5), TicksLeft(5))

    print(player.addEffect(effect).addEffect(effect3).currentEffects)
  }

  it should "applyEffect" in {
    val effect = EffectDuration(Poison(20), Permanent)

    print(player.addEffect(effect).applyEffects)
  }

  it should "removeEffect" in {
    val effect1 = EffectDuration(IncreaseDamage(5), Permanent)
    val effect2 = EffectDuration(Poison(5), Permanent)
    val test = player.copy(currentEffects = Vector[EffectDuration](effect1, effect2))
    print(test.removeEffects(_.isInstanceOf[IncreaseDamage]).currentEffects)
  }

  it should "moveTo" in {
    print(player.moveTo(Position(1, 2)).position)
  }

  it should "tick" in {
    val effect = EffectDuration(Poison(20), TicksLeft(10))
    val test = player.addEffect(effect)
    println(test.tick.get.tick.get.tick.get.tick)
  }
}
