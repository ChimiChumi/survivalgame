sealed trait Request {
  def applyRequest(state: WorldState): WorldState
  // similar to switch-case request matching, but more optimized. The requests can directly call themselves.
}

/**
 * To avoid case-match check for numerous request types, these will have their own applyRequest method,
 * resulting in their direct call.
 *
 * Join:          requests new player to join
 *
 * LeavePlayer:   requests to disconnect player
 *
 * Die:           requests to kill entity
 *
 * Mine:          requests player to mine block
 *
 * StoreItem:     requests item to be stored in chest
 *
 * Consume:       requests consumable to be consumed
 *
 * MoveEntity:    requests entity to change position
 *
 * HitEntity:     requests entity to hit other entity
 *
 * LootItem:      requests item to be moved to be stored
 */

case object Tick extends Request {
  override def applyRequest(state: WorldState): WorldState = ???
}

/**
 * Join the player to the game.
 * @param player    a player to be added to the game
 */
case class Join(player: Player) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    state.copy(worldMap = state.worldMap.copy(players = state.worldMap.players + (player.id -> player)))
  }
}

/**
 * Remove the player from the game.
 * @param id     the player to be removed
 */
case class LeavePlayer(id: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    state.copy(worldMap = state.worldMap.copy(players = state.worldMap.players.removed(id)))
  }
}

/**
 * Request to kill the entity.
 * @param id    which entity should die
 */
case class Die(id: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    val hasPlayer: Boolean = state.worldMap.players.contains(id)

    if(hasPlayer){
      val player: Player = state.worldMap.players(id).die
      state.copy(worldMap = state.worldMap.copy(players = state.worldMap.players.updated(id, player)))
    }
    else {
      val mob = state.worldMap.mobs(id).die
      state.copy(worldMap = state.worldMap.copy(mobs = state.worldMap.mobs.removed(id)))
    }
  }
}

/**
 * Request the player to mine the block if is below reachingdistance.
 * @param id          which player
 * @param position    placable block ot be mined
 */
case class Mine(id: String, position: Position) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    val hasPlayer: Boolean = state.worldMap.players.contains(id)
    val hasBlock: Boolean = state.worldMap.map(position.x)(position.y).isInstanceOf[Block]

    if(hasPlayer && hasBlock) {
      val block = state.worldMap.map(position.x)(position.y)

      if(state.worldMap.players(id).inReach(position)){
          state.worldMap.players(id).inventory + ItemStack(block, 1)  // adding item to inventory
      }
      else {
          println(s"The ${block.id} block is out of reach!")
        }
    }
    state
  }
}


case class StoreItem(playerID: String, chestID: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    val chestIndex = for (i <- state.worldMap.map.indices; j <- state.worldMap.map(i).indices if state.worldMap.map(i)(j).id == chestID)
    yield (i, j)

    if(chestIndex.isEmpty) return state

    val chest: Chest = state.worldMap.map(chestIndex(0)._1)(chestIndex(0)._2).asInstanceOf[Chest]
    val newChest: Chest = (chest + state.worldMap.players(playerID).onCursor)._1

    state.copy(worldMap = state.worldMap.copy(
      map = state.worldMap.map
        .updated(
          chestIndex(0)._1, state.worldMap.map(chestIndex(0)._1)
            .updated(chestIndex(0)._2, newChest)
        )

    ))
  }
}

/**
 * Request to consume the item held in hand.
 * @param playerID    which player should consume the item on cursor
 */
case class Consume(playerID: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    val hasPlayer: Boolean = state.worldMap.players.contains(playerID)

    if (hasPlayer) {
      val player: Player = state.worldMap.players(playerID)
      val newPlayer: Player = player.consume
      state.copy(worldMap = state.worldMap.copy(players = state.worldMap.players.updated(playerID, newPlayer)))
    }
    else
      state
  }
}
case class MoveEntity(entityID: String, position: Position) extends Request {
  override def applyRequest(state: WorldState): WorldState = ???
}
case class HitEntity(attackerID: String, defenderID: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = ???
}
case class LootItem(playerID: String, chestID: String, index: Int) extends Request {
  override def applyRequest(state: WorldState): WorldState = ???
}


//todo finish docs
/**
 * A separate class containing information about active rules and entities during the session.
 * @param map           a 2D vector of blocks. This is considered the playable world
 * @param players       players present in the game
 * @param mobs          mobs present in the game
 * @param gameRules     rules present in the game
 */

case class WorldMap(
                     map: Vector[Vector[Placable]],
                     players: Map[String, Player],
                     mobs: Map[String, Mob],
                     gameRules: GameRules
                   ) {}

/**
 * @param worldMap    the WorldState is initialized with a previously defined WorldMap and it's data
 * @param requests    requests in a sequence waiting to be processed
 */
case class WorldState(worldMap: WorldMap, requests: Seq[Request]){
  def handle(request: Request): WorldState = ???
  def hasRequests: Boolean = requests.nonEmpty  // checks if there are any unhandled requests
  def processNextRequest: WorldState = ???  // move to the next request
  def players: Vector[Player] = worldMap.players.values.toVector  // get the actual players present in the world
  def apply(x: Int, y: Int):Option[Placable] = ??? // get the position of a block if exists
  def apply(position: Position):Option[Placable] = ???  // similar as the previous
  def width:Int = worldMap.map.length   // map width
  def height:Int = worldMap.map(0).length  // map height
  def saveWorldState(worldState: WorldState, filePath: String): Unit = ??? // save WorldState object to JSON file
  def loadWorldState(filePath: String): Option[WorldState] = ??? // load WorldState object from a JSON file
}




/**
 * Consuming a desired item.
 * @param item to be consumed
 * @return added equipment effects to currentEffects vector
 */
/*
TODO ezt a playerhez irtam kozbe ide kell.

   Ugy kene mukodjon, hogy az effektek hossza osszeadodjon ha mar van rajta olyan
   (pl ticksLeft(5) v ticksleft(10) -> ticksLeft(10) )

def consume(item: Consumable): Player = {
item.effects.foldLeft(this)((player, ed) => player.addEffect(ed).asInstanceOf[Player])

 */
