sealed trait Request {
  def applyRequest(state: WorldState): WorldState
}

//case-match elkerülése érdekében, hatékonyságot növelve így írjuk
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

case class WorldMap(
                     map: Vector[Vector[Placable]],
                     players: Vector[Player],
                     mobs: Vector[Mob],
                     gameRules: GameRules
                   ) {}

case class WorldStateImpl(worldMap: WorldMap, requests: Seq[Request]) extends WorldState {
  override def handle(request: Request): WorldState = ???
  def hasRequests: Boolean = ???
  def processNextRequest: WorldState = ???
  def players: Vector[Player] = ???
  def apply(x: Int, y: Int):Option[Placable] = ???
  def apply(position: Position):Option[Placable] = ???
  def width:Int = ???
  def height:Int = ???
}
