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
case class Join(player: Player) extends Request {
  override def applyRequest(state: WorldState): WorldState = ???
}
case class LeavePlayer(id: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = ???
}
case class Die(id: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = ???
}
case class Mine(id: String, position: Position) extends Request {
  override def applyRequest(state: WorldState): WorldState = ???
}
case class StoreItem(playerID: String, chestID: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = ???
}
case class Consume(playerID: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = ???
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


trait WorldState {
  def handle(request: Request): WorldState
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
case class WorldStateImpl(worldMap: WorldMap, requests: Seq[Request]) extends WorldState {
  override def handle(request: Request): WorldState = ???
  def hasRequests: Boolean = ???  // checks if there are any unhandled requests
  def processNextRequest: WorldState = ???  // move to the next request
  def players: Vector[Player] = ???  // get the actual players present in the world
  def apply(x: Int, y: Int):Option[Placable] = ???  // get the position of a block if exists
  def apply(position: Position):Option[Placable] = ???  // similar as the previous
  def width:Int = worldMap.map.length   // map width
  def height:Int = worldMap.map(0).length  // map height
  def saveWorldState(worldState: WorldState, filePath: String): Unit = ??? // save WorldState object to JSON file
  def loadWorldState(filePath: String): Option[WorldState] = ??? // load WorldState object from a JSON file
}
