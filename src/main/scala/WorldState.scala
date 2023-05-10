import java.io.{FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream}
import scala.collection.immutable._

trait Request extends Serializable{
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
  override def applyRequest(state: WorldState): WorldState = {

    val allPlayers: Map[String,Player] = state.worldMap.players
    val allMobs: Map[String, Mob] = state.worldMap.mobs

    val newPlayers = allPlayers.foldLeft(allPlayers)( (tempPlayers, currentPlayer) => {
      val updatedPlayer = currentPlayer._2.tick.orNull

      if(updatedPlayer == null){
        tempPlayers - currentPlayer._1
      }
      else {
        tempPlayers.updated(currentPlayer._1, updatedPlayer.asInstanceOf[Player])
      }
    })

    val newMobs = allMobs.foldLeft(allMobs)((tempMobs, currentMob) => {
      val updatedMob = currentMob._2.tick.orNull

      if (updatedMob == null) {
        tempMobs - currentMob._1
      }
      else {
        tempMobs.updated(currentMob._1, updatedMob.asInstanceOf[Mob])
      }
    })

    state.copy(worldMap = state.worldMap.copy(players = newPlayers, mobs = newMobs))
  }
}

/**
 * Join the player to the game.
 *
 * @param player a player to be added to the game
 */
case class Join(player: Player) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    state.copy(worldMap = state.worldMap.copy(players = state.worldMap.players + (player.id -> player)))
  }
}

/**
 * Remove the player from the game.
 *
 * @param id the player to be removed
 */
case class LeavePlayer(id: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    state.copy(worldMap = state.worldMap.copy(players = state.worldMap.players.removed(id)))
  }
}

/**
 * Request to kill the entity.
 *
 * @param id which entity should die
 */
case class Die(id: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = {

    val isPlayer: Boolean = state.worldMap.players.contains(id)
    val isMob: Boolean = state.worldMap.mobs.contains(id)

    if (isPlayer) {
      val player: Player = state.worldMap.players(id).die
      state.copy(worldMap = state.worldMap.copy(players = state.worldMap.players.updated(id, player)))
    }
    else if(!isPlayer && isMob) {
      val mob: Mob = state.worldMap.mobs(id).die.asInstanceOf[Mob]
      state.copy(worldMap = state.worldMap.copy(mobs = state.worldMap.mobs.removed(id)))
    }
    else
      {
        println("[Die]: The requested entity doesn't exist!")
        state
      }
  }
}

/**
 * Request the player to mine the block if is within reaching distance.
 *
 * @param id       which player
 * @param position placable block ot be mined
 */
case class Mine(playerID: String, position: Position) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    val hasPlayer: Boolean = state.worldMap.players.contains(playerID)
    val hasBlock: Boolean = state.worldMap.map(position.x)(position.y).isInstanceOf[Placable]

    if (hasPlayer && hasBlock) {
      val block = state.worldMap.map(position.x)(position.y)
      //TODO: remove this println V
      println("EZ VOLT A BLOCK: " + block.id + "\n")

      if (state.worldMap.players(playerID).inReach(position)) {
        // removing block from map
        val newMap = state.worldMap.map.updated(position.x, state.worldMap.map(position.y).updated(position.y, null))

        // adding new block to inventory
        val newInventory: Chest = (state.worldMap.players(playerID).inventory + ItemStack(block, 1))._1
        val leftOver: Option[ItemStack] = (state.worldMap.players(playerID).inventory + ItemStack(block, 1))._2

        // new player instance with modified inventory
        val newPlayer: Player = state.worldMap.players(playerID).copy(inventory = newInventory)

        if(leftOver.nonEmpty){
          println(s"[Mine]: ${leftOver.get.quantity} ${leftOver.get.item.id} left on ground! \n")
        }

        return state.copy(worldMap = state.worldMap.copy(map = newMap, players = state.worldMap.players.updated(playerID, newPlayer)))
      }
      else {
        println(s"[Mine]: The ${block.id} block is out of reach!\n")
      }
    }
    state
  }
}

/**
 * Request to place block held in hand on the map.
 *
 * @param playerID player
 * @param position destination area where to block needs to be placed
 */
case class Place(playerID: String, position: Position) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    val hasPlayer: Boolean = state.worldMap.players.contains(playerID)
    val hasBlock: Boolean = state.worldMap.map(position.x)(position.y).isInstanceOf[Block]


    if (hasPlayer && !hasBlock) {
      val isPlacable: Boolean = state.worldMap.players(playerID).onCursor.item.isInstanceOf[Placable]
      val player: Player = state.worldMap.players(playerID)

      if (isPlacable && state.worldMap.players(playerID).inReach(position)) {
        val newMap = state.worldMap.map.updated(position.x, state.worldMap.map(position.y).updated(position.y, state.worldMap.players(playerID).onCursor.item.asInstanceOf[Placable]))
        val newPlayer: Player = state.worldMap.players(playerID).copy(onCursor = player.onCursor.copy(quantity = player.onCursor.quantity - 1))

        state.copy(worldMap = state.worldMap.copy(map = newMap, players = state.worldMap.players.updated(playerID, newPlayer)))
      }
      else{
        println(s"[Place]: Action unsuccessful! Block can't be places or position is not in reach.")
        state
      }
    }
    else{
      println(s"[Place]: Action unsuccessful! Position already has a block or player is missing.")
      state
    }
  }
}

/**
 * Request to consume the item held in hand.
 *
 * @param playerID which player should consume the item on cursor
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

/**
 * Request to store item held in the player's hand to the given chest.
 *
 * @param playerID    which player
 * @param chestID     in which chest to be stored
 */
case class StoreItem(playerID: String, chestID: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    val chestIndex = for (
      i <- state.worldMap.map.indices;
      j <- state.worldMap.map(i).indices
      if state.worldMap.map(i)(j) != null && state.worldMap.map(i)(j).id == chestID) yield (i, j)

    if (chestIndex.isEmpty) {
      //TODO comments
      state
    }

    else {
      val chest: Chest = state.worldMap.map(chestIndex(0)._1)(chestIndex(0)._2).asInstanceOf[Chest]
      val newChest: Chest = (chest + state.worldMap.players(playerID).onCursor)._1
      val player: Player = state.worldMap.players(playerID)

      if(player.inReach( Position( chestIndex(0)._1, chestIndex(0)._2 )) && player.onCursor != null) {
        state.copy(worldMap = state.worldMap.copy(
          map = state.worldMap.map
            .updated(
              chestIndex(0)._1, state.worldMap.map(chestIndex(0)._1)
                .updated(chestIndex(0)._2, newChest)
            ),
          players = state.worldMap.players.updated(playerID, state.worldMap.players(playerID).copy(onCursor = null))
        ))
      }
      else {
        println("[StoreItem]: Storing item was unsuccessful!")
        state
      }
    }
  }
}

/**
 *
 * @param playerID
 * @param chestID
 * @param index
 */
case class LootItem(playerID: String, chestID: String, index: Int) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    val chestIndex = for (
      i <- state.worldMap.map.indices;
      j <- state.worldMap.map(i).indices
      if state.worldMap.map(i)(j) != null && state.worldMap.map(i)(j).id == chestID) yield (i, j)

    if (chestIndex.isEmpty) {
      //TODO comments
      state
    }
    else{
      // chest is found
      val chest: Chest = state.worldMap.map(chestIndex(0)._1)(chestIndex(0)._2).asInstanceOf[Chest]
      val player: Player = state.worldMap.players(playerID)

      if(player.inReach( Position( chestIndex(0)._1, chestIndex(0)._2 )) && player.onCursor == null) {
        // chest is in reach and playerhand is empty
        val newPlayer = player.copy(onCursor = chest.items(index))
        val newItems = chest.items.updated(index, null)

        state.copy(worldMap = state.worldMap.copy(
          map = state.worldMap.map.updated(
            chestIndex(0)._1, state.worldMap.map(chestIndex(0)._1).updated(chestIndex(0)._2, chest.copy(items = newItems))),
          players = state.worldMap.players.updated(playerID, newPlayer)
        ))
      }
      else {
        println("[LootItem]: Request unsuccessful!")
        state
      }
    }
  }
}

/**
 * Request to craft the given recipe for the given player.
 *
 * If the player has enough ingredients and has an empty hand, the item is crafted and is placed in his hand.
 * @param playerID    which player
 * @param recipe      recipe, containing vector of inputs and one output
 */
case class CraftRecipe(playerID: String, recipe: Recipe) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    if (state.worldMap.players.contains(playerID)) {
      val selectedPlayer = state.worldMap.players(playerID)

      // check if hand is empty
      if (selectedPlayer.onCursor == null) {
        val crafted = recipe.craftItem(selectedPlayer.inventory.items)

        if (crafted.isEmpty) {
          println(s"[CraftRecipe]: Not enough ingredients to craft '${recipe.output.id}'!")
        }

        else {
          val newPlayer = recipe.inputs.foldLeft(selectedPlayer)((tempPlayer, itemStack) =>{
            val indexWithItemStack = tempPlayer.inventory.items.zipWithIndex.maxBy(stackWithIndex => {
              itemStack == stackWithIndex._1
            })

            if(indexWithItemStack._2 >= 0){
              val foundStack = indexWithItemStack._1
              val newQuantity = foundStack.quantity - itemStack.quantity

              if(newQuantity <= 0){
                tempPlayer.copy(inventory = tempPlayer.inventory.copy(items = tempPlayer.inventory.items.updated(indexWithItemStack._2, null)))
              }
              else
                {
                  tempPlayer.copy(inventory = tempPlayer.inventory.copy(items = tempPlayer.inventory.items.updated(indexWithItemStack._2, indexWithItemStack._1.copy(quantity = newQuantity))))
                }
            }

            else
              {
                println(s"[CraftRecipe]: Ingredient not found in inventory.")
                tempPlayer
              }

          })
          return state.copy(worldMap = state.worldMap.copy(players = state.worldMap.players.updated(playerID, newPlayer.copy(onCursor = ItemStack(crafted.get, 1)))))
        }
      }
      state
    }
    else
      state
  }
}

case class MoveEntity(entityID: String, position: Position) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    val isPlayer: Boolean = state.worldMap.players.contains(entityID)
    val isMob: Boolean = state.worldMap.mobs.contains(entityID)
    val hasBlock: Boolean = state.worldMap.map(position.x)(position.y).isInstanceOf[Block]

    if (!isPlayer && !isMob) {
      state
    }
    else {
      if (isPlayer && !hasBlock) {
        state.copy(worldMap = state.worldMap.copy(players = state.worldMap.players.updated(entityID, state.worldMap.players(entityID).moveTo(position).asInstanceOf[Player])))
      }
      else if (isMob && !hasBlock) {
        state.copy(worldMap = state.worldMap.copy(mobs = state.worldMap.mobs.updated(entityID, state.worldMap.mobs(entityID).moveTo(position).asInstanceOf[Mob])))
      }
      else {
        state
      }
    }
  }
}

case class HitEntity(attackerID: String, defenderID: String) extends Request {
  override def applyRequest(state: WorldState): WorldState = {
    val attacker: Entity =
      if (state.worldMap.players.contains(attackerID)) {
        state.worldMap.players(attackerID)
      }
      else if (state.worldMap.mobs.contains(attackerID))
        state.worldMap.mobs(attackerID)
      else
        null

    val defender: Entity =
      if (state.worldMap.players.contains(defenderID)) {
        state.worldMap.players(defenderID)
      }
      else if (state.worldMap.mobs.contains(defenderID))
        state.worldMap.mobs(defenderID)
      else
        null

    if (attacker == null || defender == null) state

    else {
      if (attacker.inReach(defender.position)) {
        val newDefender: EntityStats = defender.takeDamage(attacker.currentStats.attack - defender.currentStats.defense).get

        if (newDefender.hp <= 0) {
          state.copy(requests = state.requests.appended(Die(defenderID))) // meghalt az entity
        }
        else {
            defender match {
              case player: Player => return state.copy(worldMap = state.worldMap.copy(players = state.worldMap.players.updated(defenderID, player.copy(currentStats = newDefender))))
              case mob: Mob => return state.copy(worldMap = state.worldMap.copy(mobs = state.worldMap.mobs.updated(defenderID, mob.copy(currentStats = newDefender))))
            }
          }
      }
      else{
        state
      }
    }
  }
}






/**
 * A separate class containing information about active rules and entities during the session.
 *
 * @param map       a 2D vector of blocks. This is considered the playable world
 * @param players   players present in the game
 * @param mobs      mobs present in the game
 * @param gameRules rules present in the game
 */
case class WorldMap(
                     map: Vector[Vector[Placable]],
                     players: Map[String, Player],
                     mobs: Map[String, Mob],
                     gameRules: GameRules
                   ) extends Serializable



/**
 * @param worldMap the WorldState is initialized with a previously defined WorldMap and it's data
 * @param requests requests in a sequence waiting to be processed
 */
case class WorldState(worldMap: WorldMap, requests: Seq[Request]) extends Serializable{

  /**
   * Applies the incoming request, modifying the WorldState
   * @param request     incoming request
   * @return            modified WorldState
   */
  def handle(request: Request): WorldState = request.applyRequest(this)


  /**
   * Checks whether the sequence contains requests or not.
   */
  def hasRequests: Boolean = requests.nonEmpty // checks if there are any unhandled requests

  /**
   * Moves to the next requests, removing the current one from the sequence
   *
   * @return modified worldState
   */
  def processNextRequest: WorldState = {
    if (!hasRequests || players.isEmpty) this
    else {
      val nextState = handle(requests.head)
      nextState.copy(requests = requests.tail)
    }
  }

  /**
   * Gets the current players in the active WorldState.
   */
  def players: Vector[Player] = worldMap.players.values.toVector // get the actual players present in the world

  /**
   * Get the block of a given position if exists.
   * @param x coordinate for row
   * @param y coordinate for column
   */
  def apply(x: Int, y: Int): Option[Placable] = Option(this.worldMap.map(x)(y))

  /**
   * Get the block of a given position if exists.
   * @param position x,y bundled into position
   */
  def apply(position: Position): Option[Placable] = Option(this.worldMap.map(position.x)(position.y))

  /**
   * The world map length based on width.
   */
  def width: Int = worldMap.map.length // worldmap width

  /**
   * The world map length based on height.
   */
  def height: Int = worldMap.map(0).length // worldmap height

  /**
   * Saves the game session using java.io.Serializable
   * @param state       given WorldState
   * @param filePath    target file
   */
  def save(state: WorldState, filePath: String): Unit = {
    val fileOut = new FileOutputStream(filePath)
    val objectOut = new ObjectOutputStream(fileOut)

    try {
      objectOut.writeObject(state)
    } finally {
      objectOut.close()
      fileOut.close()
    }
  }

  /**
   * Loads the previously saved session.
   * @param filePath    target file
   */
  def load(filePath: String): Option[WorldState] = {
    val fileIn = new FileInputStream(filePath)
    val objectIn = new ObjectInputStream(fileIn)

    try {
      Some(objectIn.readObject().asInstanceOf[WorldState])
    } catch {
      case e: Exception =>
        e.printStackTrace()
        None
    } finally {
      objectIn.close()
      fileIn.close()
    }
  }
}


