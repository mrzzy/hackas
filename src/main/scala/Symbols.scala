/*
 * Hackas - HACK Assembler
 * Symbol Table
 */
package co.mrzzy.nand2tetris.p1.project6;

object Symbols {

  /** Regex for matching symbols
    *
    *  Symbols cannot start with a number and should contain
    *  only contains number, letters and underscores
    */
  val SymbolPattern = "[a-zA-Z_]\\w*".r

  /** SymbolTable maps string symbols to RAM or ROM addresses */
  type SymbolTable = Map[String, Int]

  /** Defines the builtin defined by the Assembler */
  val BuiltinSymbols: SymbolTable = Map(
    "SP" -> 0,
    "LCL" -> 1,
    "ARG" -> 2,
    "THIS" -> 3,
    "THAT" -> 4,
    "SCREEN" -> 16384,
    "KBD" -> 24576
  ) ++ (
    // add RAM registers R0-R15
    (0 to 15).map((i) => (s"R$i" -> i))
  )

  /** Scan the given HACK Instructions to build a symbol table.
    *
    *  Pass 1: Looks for label declarations in the format (LABEL) and records the line
    *  number of the next instruction as its address in the symbol table.
    *  Pass 2: Looks for variable declarations in the format @VARIABLE that does not
    *  reference an existing label and assigns them an address above 1
    */
  def scan(instructions: Vector[Instruction]): SymbolTable = {
    Map()
  }
}
