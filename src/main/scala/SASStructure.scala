/**
  * Created by nnon on 23/02/16.
  */
class SASStructure(sSource: String, sType: String, sBody: String) {
  val structureSource: String = sSource
  val structureType: String = sType
  val structureBody: String = sBody

  override def toString(): String = {
    structureType + ": \n" + structureBody.replaceAll("\u26A4", "\n") + "\n"
  }
}
