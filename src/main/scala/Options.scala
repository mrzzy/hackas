/*
 * Hackas - HACK Assembler
 * Program Options
*/
package co.mrzzy.nand2tetris.p1.project6;


// stores assembler options
case class Options(inputPath: String, outputPath:String)

object Options {
  val OutputOpt = "--output"
  val DefaultOutputPath = "a.out"
  
  /** Contruct a [[Options]] instance from commmand line arguments
   *  
   *  Parses commmand line arguments into program options in [[Options]]
   *  @param args The commmand line arguments to parse.
   *  @return Contructed [[Options]] instance.
   */
  def fromArgs(args: Array[String]): Options = {
    // partition options (and option args) against program args
    // .tail to skip program name in args
    val (optionArgs, programArgsGrouped) = args.tail.grouped(2)
      .partition {
      // options come in (option, option arg) pair & start with "-"
      case Array(first, second) if first(0) == '-' => true
      case _ => false
    }

    // extract input path from program args
    val programArgs = programArgsGrouped.flatten.toVector
    if(programArgs.length != 1) {
      throw new IllegalArgumentException("Missing expected INPUT file path")
    }
    val inputPath = programArgs.head

    // extract output path from options
    val optionMap = optionArgs.map{ case Array(first, second) => (first, second) }.toMap
    val outputPath = optionMap.getOrElse(OutputOpt, DefaultOutputPath)
    
    Options(inputPath, outputPath)
  }
}

