/*
 * Hackas - HACK Assembler
 * Symbols
 */
package co.mrzzy.nand2tetris.p1.project6;

object Symbols {

  /** Regex for matching symbols
    *
    *  Symbols cannot start with a number and should contain
    *  only contains number, letters and underscores
    */
  val SymbolPattern = "[a-zA-Z_][\\w.$]*".r

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
    *  Scans for label symbols defined by Label declarations followed by
    *  variable symbols in A instructions. Finally appends the builtin symbols
    *  to complete the symbol table.
    */
  def scan(instructions: List[Instruction]): SymbolTable = {
    val existingSyms = BuiltinSymbols ++ scanLabels(instructions)
    val variableSyms = scanVariables(instructions, existingSyms)
    existingSyms ++ variableSyms
  }

  /** Scan LabelDeclarations to build a symbol table from labels and referenced instructions
    *
    *  Looks for label declarations in the format (LABEL) and records the line
    *  number of the next instruction as its address in the symbol table
    *
    *  @param instructions to scan for LabelDeclaration to build symbol table.
    *  @throws IllegalArgumentException if given a label that does not reference any instruction.
    *  @return SymbolTable with symbols derived from labels.
    */
  def scanLabels(instructions: List[Instruction]): SymbolTable = {
    // scan for label definitions and referenced instructions
    val instructionIndex = instructions.zipWithIndex
    val labelIndexes = instructionIndex
      .collect {
        case (l: LabelDeclaration, labelIdx: Int) => {
          try {
            // compile index of real / non-virtual instruction to:
            // idx: Instructions index among all instructions
            // realIdx: Instruction's index amoung real / non-virtual instructions
            val realInstructionIndex = instructionIndex
              .filter(!_._1.isVirtual)
              .zipWithIndex
              .map { case ((instruction, idx), realIdx) =>
                (instruction, idx, realIdx)
              }

            // label should refer the next non virtual / real HACK instruction
            val referIdx = realInstructionIndex
              .filter { case (_, idx, _) =>
                idx > labelIdx
              }
              // referIdx should reference index of real instructions
              .map(_._3)
              .min
            (l.label, referIdx)
          } catch {
            case e: UnsupportedOperationException =>
              throw new IllegalArgumentException(
                s"Error: Label declaration does not refer to any instruction: $l"
              )
          }
        }
      }

    // construct label symbol table with: label -> referenced instruction index
    labelIndexes.toMap
  }

  /** Scan AInstruction & existing symbols to allocate variables in symbol table
    *
    *  Looks for variable declarations in the format @VARIABLE that does not
    *  reference an existing symbol and assigns them an address 16 and above
    *
    *  @param instructions to scan variable declarations to allocate variables.
    *  @param existingSym table containing the existing non-variable symbols.
    *  @return SymbolTable with variable symbols allocated.
    */
  def scanVariables(
      instructions: List[Instruction],
      existingSyms: SymbolTable
  ) = {
    // collect variable symbols: A-instruction addresses that are not existing symbols
    val variableSymbols = instructions
      .collect { case instruction: AInstruction =>
        instruction.address
      }
      .distinct
      // existing symbols and numeric address cannot be variable symbols
      .filter(address =>
        !(existingSyms.contains(address) || address.forall(_.isDigit))
      )

    // allocate variables symbols with addresses above 16
    variableSymbols.zipWithIndex.map { case (variable, idx) =>
      (variable, idx + 16)
    }.toMap
  }
}
