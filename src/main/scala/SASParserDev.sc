import scala.util.matching.Regex
val commentPattern = ".*\\/\\*.*\\*\\/".r.unanchored
val dataPattern = "data .*?run;.*".r
val procSQLPattern = "proc sql .*?quit;".r
val procPattern = "proc .*?(quit;|run;)".r
val input = "lplplpl data _temp(drop = date1-date3); infile unixtemp truncover dlm=' ' lrecl=1000; run;"
input match {
  case commentPattern(_) => println("Comment")
  case dataPattern(_) => println("Data step")
  case procSQLPattern(_) => println("Proc SQL")
  case procPattern(_) => println("Proc")
  case _  => println("Return, no match")
}
val d = (dataPattern findFirstMatchIn(input)).get.start

input.indexOf("data .*?run;.*".r)
//for (body <- input.splitAt(input indexOf dataPattern)){
//  s += new SASStructure("", body)
//}
//  .foreach(new SASStructure(" ", _))
//  .foreach(s += new SASStructure("", _))
//(new SASStructure("Type", _))
