import java.io.Serializable

/**
 * Getting the exact position value on the map using a 2D coordinate pair.
 * Useful for handling things inside the world map
 *
 * @param x   coordinate
 * @param y   coordinate
 */
case class Position(x: Int, y: Int) extends Serializable

