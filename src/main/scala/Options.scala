/*
 * Hackas - HACK Assembler
 * Program Options
 */
package co.mrzzy.nand2tetris.p1.project6;

/** Stores program options
  *
  *  inputPath: Path to obtain input HAC Assembly to assemble
  *  outputPath: Path to write output HACK machine code to.
  *  showUsage: Whther to show usage information and exit.
  */
case class Options(
    inputPath: String = "in.asm",
    outputPath: String = "out.hack",
    showUsage: String = "false"
)

object Options {
  val OutputOpt = "--output"
  val HelpOpt = "--help"
  val OptionDefaults = Map(
    OutputOpt -> "out.hack",
    HelpOpt -> "false"
  )

  /** Contruct a [[Options]] instance from commmand line arguments
    *
    *  Parses commmand line arguments into program options in [[Options]]
    *  @param args The commmand line arguments to parse.
    *  @return Contructed [[Options]] instance.
    */
  def fromArgs(args: Array[String]): Options = {
    if (args.length < 1) {
      throw new IndexOutOfBoundsException(
        "Expected passed args to have 1 or more elements"
      )
    }
    // filter options from given args
    // .tail to skip program name in args
    val options = args.filter { _(0) == '-' }

    // parse options and their arguments
    val givenOptions = options.collect {
      case OutputOpt => OutputOpt -> args(args.indexOf(OutputOpt) + 1)
      case HelpOpt   => HelpOpt -> "true"
      case badOpt =>
        throw new IllegalArgumentException(s"Error: Bad option: ${badOpt}")
    }.toMap
    val optionMap = OptionDefaults ++ givenOptions

    if (optionMap(HelpOpt) == "true") {
      return Options(showUsage = "true")
    }

    // filter out arguments consumed for options to obtain program args
    // .tail required to skip out program name in args.
    val optionArgs = givenOptions.keySet | givenOptions.values.toSet
    val programArgs = args.tail.filter(!optionArgs.contains(_))
    if (programArgs.length != 1) {
      throw new IllegalArgumentException(
        s"Error: Missing required INPUT path argument"
      )
    }

    Options(
      inputPath = programArgs(0),
      outputPath = optionMap(OutputOpt),
      showUsage = optionMap(HelpOpt)
    )
  }
}
