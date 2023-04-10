import org.json4s.JsonDSL._
import org.json4s._
import org.json4s.native.JsonMethods._

implicit val formats = DefaultFormats
sealed trait Request {
  def applyRequest(state: WorldState): WorldState
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

/**
 * A separate class containing information about
 * @param map
 * @param players
 * @param mobs
 * @param gameRules
 */

//TODO: kerdes mobokkal, stb
case class WorldMap(
                     map: Vector[Vector[Placable]],
                     players: Vector[Player],
                     mobs: Vector[Mob],
                     gameRules: GameRules
                   ) {}

/**
 * @param worldMap    WorldState receives previously defined WorldMap and it's data
 * @param requests    //TODO handle request nem ugyan az??
 */
case class WorldStateImpl(worldMap: WorldMap, requests: Seq[Request]) extends WorldState {
  override def handle(request: Request): WorldState = ???
  def hasRequests: Boolean = ???
  def processNextRequest: WorldState = ???
  def players: Vector[Player] = ???
  def apply(x: Int, y: Int):Option[Placable] = ???
  def apply(position: Position):Option[Placable] = ???
  def width:Int = ???
  def height:Int = ???
  def saveWorldState(worldState: WorldState, filePath: String): String = ??? // save WorldState object to JSON file
  def loadWorldState(filePath: String): WorldState = ??? // load WorldState object from a JSON file
}


//TODO megirni
case class WorldStateJson(worldMap: WorldMap, requests: Seq[Request])
object WorldStateJson {
  implicit val formats: DefaultFormats.type = DefaultFormats
}
