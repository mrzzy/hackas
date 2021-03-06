/*
 * Hackas - HACK Assembler
 * HACK Assembly Parser
 */
package co.mrzzy.nand2tetris.p1.project6;

import scala.util.Properties
import scala.util.matching.Regex

import Symbols.SymbolPattern
import java.nio.charset.MalformedInputException

/** Parser that parses HACK Assembly into Instructions */
object Parser {

  /** Represents a line of HACK Assembly code */
  case class Line(val text: String, val number: Int)

  /** Parse the given HACK assembly to [[Instruction]]s
    *
    * @param hackAsm The input HACK assembly to parse
    * @return A list of parsed HACK A / C kind Instruction
    */
  def parse(hackAsm: String): Iterable[Instruction] = {
    // read lines from full assembly string
    val asmLines = hackAsm
      .split(Properties.lineSeparator)
      .zipWithIndex
      // compile list of lines: text and line numbers
      .map { case (text, idx) => Line(text, idx + 1) }

    // strip whitespace and comments & parse instructions
    strip(asmLines).map(parseLine(_))
  }

  /** Parse a single assembly line into an [[Instruction]]
    *
    *  @param line to parse into an [[Instruction]]
    *  @return The parsed [[Instruction]] from the Assembly line
    */
  def parseLine(line: Line): Instruction = {
    // match instruction kind using regex & parse instruction
    val aKindPattern = s"@(\\d+|${SymbolPattern})".r
    val cKindPattern = "(?:(\\w+)=)?([-!+&|\\w01]+)(?:;(\\w+))?".r
    val labelDeclarePattern = s"\\((${SymbolPattern})\\)".r

    try {
      line.text match {
        // some optional regex groups on no match results in null variables.
        // ie 'jump' may be null when the user does specify the jump part of a c instruction.
        // Use Option to convert nulls to empty string
        case aKindPattern(address) => new AInstruction(address)
        case cKindPattern(dest, compute, jump) =>
          new CInstruction(
            Option(dest).getOrElse(""),
            compute,
            Option(jump).getOrElse("")
          )
        case labelDeclarePattern(label) =>
          new LabelDeclaration(Option(label).getOrElse(""))
        case badInstruction => {
          throw new IllegalArgumentException(
            s"Error: Line ${line.number}: Malformed Instruction not a " +
              s"A, C Instruction or Label Declaration: ${line.text}"
          )
        }
      }
    } catch {
      case e: IllegalArgumentException =>
        throw new IllegalArgumentException(
          s"Error: Line ${line.number}: ${e.getMessage()}"
        )
    }
  }

  /** Strip the whitespace and comments from the given lines of Hack Assembly
    *
    *  Strips comments which start with '//' and end at the end of line and
    *  any whitespace in line text.  Discards any line with empty texts.
    *  @param asmLines List of HACK assembly lines to strip.
    *  @return List of stripped Hack assembly lines
    */
  def strip(asmLines: Iterable[Line]): Iterable[Line] = {
    asmLines
      .map((line) => {
        // clip / drop comments delimited by // and newline
        val delimPos = line.text.indexOf("//")
        val clipIdx = if (delimPos != -1) delimPos else line.text.length
        val clipText = line.text.substring(0, clipIdx)
        Line(clipText, line.number)
      })
      .map((line) => {
        // remove all whitespace in line
        val compactText = "\\s".r.replaceAllIn(line.text, "")
        Line(compactText, line.number)
      })
      .filter(_.text.length > 0)
  }
}
