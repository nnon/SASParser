/**
  * Created by nnon on 23/02/16.
  */
class SASStructure(sType: String, sBody: String) {
  var structureType: String = sType
  var structureBody: String = sBody

  override def toString(): String = {
    structureType + ": \n" + structureBody.replaceAll("\u26A4", "\n") + "\n"
  }
}
