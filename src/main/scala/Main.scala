/*
 * Hackas - HACK Assembler
 * Main / Entrypoint
 */
package co.mrzzy.nand2tetris.p1.project6;

import scala.io.Source
import java.io.PrintWriter

object Main {
  val usage = """Usage: hackas [--output OUTPUT] INPUT
  Assemble the HACK Assembly read from INPUT into HACK Machine Language.
  Write the assembled machine language to OUTPUT.

  Options:
    --output  The path of the file to write the assembled machine language to.
              Defaults to 'a.out'
    --help    Show this usage information.
  """
  def main(args: Array[String]): Unit = {
    // parse program options
    val options = Options.fromArgs(args)
    if (options.showUsage == "true") {
      print(usage)
      return
    }

    // parse input hack assembly into instructions
    val hackAsm =
      Source.fromFile(options.inputPath, "UTF-8").getLines.mkString("\n")
    val instructions = Parser.parse(hackAsm)

    // scan instructions for symbols
    val symTable = Symbols.scan(instructions.toList)
    // resolve symbols and assemble HACK assembly to binary HACK machine language
    val hackML = instructions.map(_.toBinary(symTable)).mkString("\n")

    // write assembled HACK ML to output path
    val out = new PrintWriter(options.outputPath)
    out.write(hackML)
    out.close()
  }
}
