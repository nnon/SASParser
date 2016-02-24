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
    var source = ""
    try {
      source = Source.fromFile(args(0)).getLines.mkString("\u26A4")
    } catch {
      case ex: Exception => println("File exception: "); println(ex)
    }
//    println( commentPattern findFirstIn "/*p0001*/")
    val sasObjs = buffer(source)
    sasObjs.foreach(println(_))

  }

  def buffer(remainder: String): Array[SASStructure] = {

    def patternSplit(input: String, pattern: UnanchoredRegex, structureType: String): Array[SASStructure] = {
      val (unclassified, body): (String, String) = input splitAt ((pattern findFirstMatchIn input).get.start)
      println((pattern findFirstMatchIn input).get.start)
      Array(new SASStructure("Unclassified", unclassified), new SASStructure(structureType, body))
    }

    def buffer0(input: String, remainder: String, steps: Array[SASStructure]): Array[SASStructure] = {
      input.toLowerCase match {
//        case commentPattern() => buffer0(remainder.head.toString, remainder tail,
//          steps :+ new SASStructure("Comment", input))
        case procSQLPattern() => buffer0(remainder.head.toString, remainder tail,
          steps ++ patternSplit(input, procSQLPattern, "proc sql"))
        case procDownloadPattern() => buffer0(remainder.head.toString, remainder tail,
          steps ++ patternSplit(input, procDownloadPattern, "proc download"))
        case procPattern() => buffer0(remainder.head.toString, remainder tail,
          steps ++ patternSplit(input, procPattern, "proc"))
        case dataPattern() => buffer0(remainder.head.toString, remainder tail,
          steps ++ patternSplit(input, dataPattern, "data"))
        case _ if remainder.isEmpty => return steps
        case _ => buffer0(input + remainder.head.toString, remainder tail, steps)
      }
    }

    buffer0(remainder.head.toString, remainder tail, new Array[SASStructure](0))
  }



}
