import scala.io.Source
import scala.util.matching.{UnanchoredRegex, Regex}

/**
  * Created by nnon on 23/02/16.
  */
object SASParser {

  //    val commentPattern = "\\/\\*.*\\*\\/".r.unanchored
  val procSQLPattern = "proc sql .*?quit;".r.unanchored
  val procDownloadPattern = "proc download .*?run;".r.unanchored
  val procPattern = "proc .*?(quit;|run;)".r.unanchored
  val dataPattern = "data .*?run;".r.unanchored

  def main(args: Array[String]) {
    if (args.length != 1) {
      println("File parameter required")
      System.exit(1)
    }
    val sourceFile = args(0)
    var source = ""
    try {
      source = Source.fromFile(sourceFile).getLines.mkString("\u26A4")
    } catch {
      case ex: Exception => println("File exception: "); println(ex)
    }
//    println( commentPattern findFirstIn "/*p0001*/")
    val sasObjs = buffer(sourceFile, source)
    sasObjs.foreach(sasobj => println(sasobj.structureSource + " " + sasobj.structureType))

  }

  /*regex objects - type and pattern
  * reduce case steps to match one of list of patterns, remainder empty, next recursion*/

  def buffer(sourceFile: String, remainder: String): Array[SASStructure] = {

    def patternSplit(sourceFile: String, input: String, pattern: UnanchoredRegex, structureType: String): Array[SASStructure] = {
      val (unclassified, body): (String, String) = input splitAt ((pattern findFirstMatchIn input).get.start)
      Array(new SASStructure(sourceFile, "Unclassified", unclassified), new SASStructure(sourceFile, structureType, body))
    }

    def buffer0(sourceFile: String, input: String, remainder: String, steps: Array[SASStructure]): Array[SASStructure] = {
      input.toLowerCase match {
//        case commentPattern() => buffer0(remainder.head.toString, remainder tail,
//          steps :+ new SASStructure("Comment", input))
        case procSQLPattern() => buffer0(sourceFile, remainder.head.toString, remainder tail,
          steps ++ patternSplit(sourceFile, input, procSQLPattern, "proc sql"))
        case procDownloadPattern() => buffer0(sourceFile, remainder.head.toString, remainder tail,
          steps ++ patternSplit(sourceFile, input, procDownloadPattern, "proc download"))
        case procPattern() => buffer0(sourceFile, remainder.head.toString, remainder tail,
          steps ++ patternSplit(sourceFile, input, procPattern, "proc"))
        case dataPattern() => buffer0(sourceFile, remainder.head.toString, remainder tail,
          steps ++ patternSplit(sourceFile, input, dataPattern, "data"))
        case _ if remainder.isEmpty => return steps
        case _ => buffer0(sourceFile, input + remainder.head.toString, remainder tail, steps)
      }
    }

    buffer0(sourceFile, remainder.head.toString, remainder tail, new Array[SASStructure](0))
  }



}
