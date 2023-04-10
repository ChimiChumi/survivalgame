/**
 * Class for file handling
 * @param filePath     JSON file destination
 */
case class File(filePath: String) {
  /**
   * Method to write to file
   * @param content   string to be written to file
   * @return          whether file write was successful or not
   */
  def write(content: String): Boolean = ???
  def read(): String = ???  // reads file content
}
