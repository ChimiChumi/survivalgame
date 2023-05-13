import org.scalatest.flatspec.AnyFlatSpec

class WorldStateTest extends AnyFlatSpec {

  // blocks
  val stone: Block = Block("stone")
  val wood: Block = Block("wood")
  val dirt: Block = Block("dirt")
  val sand: Block = Block("sand")
  val scrapMetal: Block = Block("scrapMetal")

  // weapons
  val sword: Weapon = Weapon("sword", 10)
  val axe: Weapon = Weapon("axe", 5)
  val stick: Weapon = Weapon("stick", 3)

  // armors
  val helmet: Armor = Armor("helmet", 3)
  val chestPlate: Armor = Armor("chestPlate", 5)
  val boots: Armor = Armor("boots", 2)

  // equipment
  val shield: Equipment = Equipment("shield", Vector(EffectDuration(ScaleDefense(5), Permanent)))
  val tactical_gloves: Equipment = Equipment("tactical_gloves", Vector(EffectDuration(IncreaseDamage(5), Permanent)))

  // consumable
  val rottenMeat: Consumable = Consumable("rottenMeat", Vector(EffectDuration(Poison(10), TicksLeft(5))))
  val goldenApple: Consumable = Consumable("goldenApple", Vector(EffectDuration(ScaleDefense(25), TicksLeft(15))))

  // crafting recipes
  val chestPlate_recipe: Recipe = Recipe(Vector[ItemStack](ItemStack(scrapMetal, 3), ItemStack(stone, 2)), chestPlate)
  val helmet_recipe: Recipe = Recipe(Vector[ItemStack](ItemStack(scrapMetal, 4)), helmet)
  val sword_recipe: Recipe = Recipe(Vector[ItemStack](ItemStack(scrapMetal, 3), ItemStack(wood, 2)), sword)
  val axe_recipe: Recipe = Recipe(Vector[ItemStack](ItemStack(stone, 3), ItemStack(wood, 2)), axe)
  val shield_recipe: Recipe = Recipe(Vector[ItemStack](ItemStack(scrapMetal, 6), ItemStack(stone, 4)), shield)
  val stick_recipe: Recipe = Recipe(Vector[ItemStack](ItemStack(wood, 2)), stick)

  // chests
  val chest1: Chest = Chest("chest1", 4, Vector(null, null, null, null))
  val chest2: Chest = Chest("chest2", 10, Vector(
    ItemStack(stone, 3),
    ItemStack(wood, 12),
    null, // empty slot
    null, // empty slot
    ItemStack(stone, 1),
    null, // empty slot
    null, // empty slot
    ItemStack(wood, 14),
    null, // empty slot
    null, // empty slot
  ))

  val rules: GameRules = GameRules(
    Vector[Item](stone, wood, dirt, sand, scrapMetal, sword, axe, stick, helmet, chestPlate, boots, shield, tactical_gloves, rottenMeat, goldenApple, chest1, chest2),
    Vector[Recipe](chestPlate_recipe, helmet_recipe, sword_recipe, axe_recipe, shield_recipe, stick_recipe)
  )

// players
  val player1: Player = Player(
    "Player1",
    "p1",
    EntityStats(35, 0, 1, 100, 5), //eredeti stat
    EntityStats(35, 0, 1, 100, 5), //aktuális stat
    Vector(),
    Position(0, 0),
    10,
    Chest("inventory1", 10, Vector(ItemStack(scrapMetal, 3), ItemStack(stone, 2), null, null, null, null, null, null, null, null)),
    Set(),
    null,
    Position(0, 0),
    2,
    None,
    None
  )
  val player2: Player = Player(
    "Player2",
    "p2",
    EntityStats(5, 0, 2, 100, 5), //eredeti stat
    EntityStats(10, 0, 2, 80, 5), //aktuális stat
    Vector(EffectDuration(IncreaseDamage(5), Permanent)),
    Position(0, 0),
    10,
    Chest("inventory2", 10, Vector[ItemStack]()),
    Set(),
    ItemStack(stone, 5),
    Position(0, 0),
    2,
    Some(axe),
    Some(helmet)
  )
  val player3: Player = Player(
    "Player3",
    "p3",
    EntityStats(5, 0, 3, 100, 5), //eredeti stat
    EntityStats(10, 0, 3, 75, 5), //aktuális stat
    Vector(EffectDuration(IncreaseDamage(5), Permanent)),
    Position(0, 0),
    10,
    Chest("inventory3", 10, Vector[ItemStack]()),
    Set(tactical_gloves),
    ItemStack(goldenApple, 2),
    Position(0, 0),
    2,
    Some(stick),
    None
  )

  // mobs
  val zombie: Mob = Mob(
    "zombie",
    "m1",
    EntityStats(10, 5, 2, 100, 0), // eredeti
    EntityStats(10, 5, 2, 100, 0), // aktuális
    Vector(),
    Position(0, 1),
    2
  )
  val skeleton: Mob = Mob(
    "skeleton",
    "m2",
    EntityStats(20, 5, 2, 50, 0), // eredeti
    EntityStats(20, 5, 2, 50, 0), // aktuális
    Vector(EffectDuration(IncreaseDamage(3), TicksLeft(5))),
    Position(3, 3),
    3
  )
  val creeper: Mob = Mob(
    "creeper",
    "m3",
    EntityStats(45, 5, 1, 75, 0), // eredeti
    EntityStats(45, 5, 1, 75, 0), // aktuális
    Vector(),
    Position(3, 4),
    1
  )

  val players: Map[String, Player] = Map(player1.id -> player1) // for testing purposes, only one
  val mobs: Map[String, Mob] = Map(zombie.id -> zombie, skeleton.id -> skeleton, creeper.id -> creeper)

  val map: Vector[Vector[Placable]] = Vector(
    Vector(null, chest1, stone, null, null, wood, null, null, null, sand),
    Vector(null, stone, stone, null, null, wood, null, null, null, sand),
    Vector(null, null, null, null, null, wood, null, null, null, sand),
    Vector(null, null, null, null, null, null, null, null, null, sand),
    Vector(null, null, null, null, null, null, null, null, null, sand),
    Vector(null, null, null, null, null, null, scrapMetal, null, null, sand),
    Vector(null, scrapMetal, null, null, null, null, null, scrapMetal, null, sand),
    Vector(null, null, scrapMetal, null, null, null, scrapMetal, null, null, sand),
    Vector(null, null, null, null, null, null, null, null, null, sand),
    Vector(dirt, dirt, dirt, dirt, dirt, dirt, dirt, dirt, dirt, sand)
  )

  val worldMap: WorldMap = WorldMap(map, players, mobs, rules)
  val requests: Seq[Request] = Seq(Tick, Die(player1.id), Tick)
  val worldState: WorldState = WorldState(worldMap, requests)


  "Testing methods for Request" should "tick entities" in {
    val newState = Join(player2).applyRequest(worldState)
    println(Tick.applyRequest(newState).worldMap.players)
    println(Tick.applyRequest(newState).worldMap.mobs)
  }

  it should "join player" in {
    val newState = Join(player2).applyRequest(worldState)
    val res = newState.players.map(_.name).mkString(", ")
    val expected = "Player1, Player2"
    assert(res == expected)
  }

  it should "leave player" in {
    val prevState = Join(player2).applyRequest(worldState)
    val newState = LeavePlayer(player1.id).applyRequest(prevState)
    val res = newState.players.map(_.name).mkString(", ")
    val expected = "Player2"
    assert(res == expected)
  }

  it should "die" in {
    val newState = Die(player1.id).applyRequest(worldState)
    val res = newState.worldMap.players(player1.id)
    val expected = Player("Player1",player1.id,EntityStats(35,0,1.0,100,5),EntityStats(35,0,1.0,50,5),Vector(),Position(0,0),10,Chest("inventory1",10,Vector()),Set(),null,Position(0,0),2,None,None)
    assert(res == expected)
  }

  it should "mine" in {
    val newState = Mine(player1.id, Position(0,2)).applyRequest(worldState)
    val nextState = Mine(player1.id, Position(0,2)).applyRequest(newState)
    val res = nextState.worldMap.players(player1.id).inventory.items
    val expected = Vector(ItemStack(scrapMetal, 3), ItemStack(stone, 3), null, null, null, null, null, null, null, null)
    assert(res == expected)
  }

  it should "place" in {
    val testState = Join(player2).applyRequest(worldState)
    val nextState = Place(player2.id, Position(0, 0)).applyRequest(testState)
    assert(nextState.worldMap.map(0)(0).id == "stone" && nextState.worldMap.players(player2.id).onCursor.quantity == testState.worldMap.players(player2.id).onCursor.quantity - 1)
  }

  it should "consume" in {
    val testState = Join(player3).applyRequest(worldState)
    val res = Consume(player3.id).applyRequest(testState).worldMap.players(player3.id).onCursor
    assert(res.quantity == testState.worldMap.players(player3.id).onCursor.quantity - 1)
  }

  it should "store item" in {
    val testState = Join(player2).applyRequest(worldState)
    val nextState = StoreItem(player2.id, "chest1").applyRequest(testState)
    val res = nextState.worldMap.map(0)(1)
    assert(res == Chest("chest1",4,Vector(ItemStack(Block("stone"),5), null, null, null)) && nextState.worldMap.players(player2.id).onCursor == null)
    // block placed from hand
  }

  it should "loot item" in {
    val initial = Join(player2).applyRequest(worldState)
    val testState = StoreItem(player2.id, "chest1").applyRequest(initial)
    val nextState = LootItem(player2.id, "chest1", 0).applyRequest(testState)
    assert(testState.worldMap.players(player2.id).onCursor == null && nextState.worldMap.players(player2.id).onCursor.item == stone)
    // initially empty hand, later filled hand
  }

  it should "craft item" in {
    val testState = CraftRecipe(player1.id, chestPlate_recipe).applyRequest(worldState)

    assert(testState.worldMap.players(player1.id).onCursor.item == chestPlate && testState.worldMap.players(player1.id).inventory.capacity == 10)
    // item crafted, is in hand and ingredients are removed from inventory
  }

  it should "move entity" in {
    assert(MoveEntity(player1.id, Position(6,0)).applyRequest(worldState).worldMap.players(player1.id).position == Position(6, 0))
  }

  it should "hit entity" in {
    val nextState = HitEntity( player1.id, zombie.id).applyRequest(worldState)
    val attacker = nextState.worldMap.players(player1.id)
    val defender = nextState.worldMap.mobs(zombie.id)

    assert(defender.currentStats.hp == defender.baseStats.hp - attacker.currentStats.attack + defender.baseStats.defense)
  }


  /**##################################################################################################**/

  "Testing methods for WorldState" should "handle" in {
    //println(worldState.handle(Tick))
  }

  it should "hasRequests" in {
    assert(worldState.hasRequests)
  }

  it should "processNextRequest" in {
    val res = worldState.processNextRequest.requests
    assert(res.size == 2)
  }

  it should "players connected" in {
    val testState = Join(player2).applyRequest(worldState)
    assert(testState.players.length == 2)
  }

  it should "game map width and height" in {
    assert(worldState.width == 10 && worldState.height == 10)
  }

  it should "apply with x,y" in {
    val res1 = worldState.apply(0,2).get.id
    val res2 = worldState.apply(0,0)
    val expected1 = "stone"
    val expected2 = None

    assert(res1 == expected1 && res2 == expected2)
  }

  it should "apply with position" in {
    val pos1 = Position(0,9)
    val pos2 = Position(0,8)
    val res1 = worldState.apply(pos1).get.id
    val res2 = worldState.apply(pos2)
    val expected1 = "sand"
    val expected2 = None

    assert(res1 == expected1 && res2 == expected2)
  }

  it should "save and load" in {
    worldState.save(worldState, "gamesaves/last-save")
    val emptyState = WorldState(null, null)
    val loadedState = emptyState.load("gamesaves/last-save")
    assert(loadedState.nonEmpty)
  }

}
