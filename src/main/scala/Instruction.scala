/*
 * Hackas - HACK Assembler
 * HACK Assembly Instructions
 */
package co.mrzzy.nand2tetris.p1.project6;

import Symbols.SymbolPattern;
import Symbols.BuiltinSymbols;
import javax.naming.NameAlreadyBoundException

/** Represents a HACK Assembly Instruction.
  *
  *  Can either represent a real Hack Assembly assemable to HACK Machine Language Instruction
  *  or a Virtual Instruction.
  */
sealed trait Instruction {

  /** Convert this Assembly [[Instruction]] to its binary Machine Language representation
    *
    * Since Virtual Instructions have no binary representation this method will
    * return empty string.
    *
    * @return The binary HACK Machine Language representation of this instruction
    */
  def toBinary(symTable: Symbols.SymbolTable): String = ""

  /** Whether this [[Instruction]] is a Virtual Instruction
    *  Virtual Instruction: Has no binary representation as it does not actually
    *  exist in HACK Machine Language.
    */
  def isVirtual: Boolean
}

/** Represents a A-Kind HACK instruction in the format @ADDRESS
  *
  * The A instruction assigns the given address to the A register.
  * ADDRESS must be an non empty numeric or symbolic addresss.
  *
  * @throws IllegalArgumentException if addr given is not a valid numeric or symbolic address
  */
case class AInstruction(private val addr: String) extends Instruction {
  // check that address is symbolic or numeric (made of digits)
  val address = if (!(SymbolPattern.matches(addr) || addr.forall(_.isDigit))) {
    throw new IllegalArgumentException(
      s"A Instruction expected symbol or numeric addresss but got: @$addr"
    )
  } else addr

  override def toString = s"@$address"
  override def isVirtual: Boolean = false
}

/** Represents a C-Kind HACK instruction in the format DEST=COMPUTE;JUMP
  *
  *  COMPUTE defines the computation to be done and must be one of the
  *  predefined computations in the compute truth table.
  *
  *  The JUMP directive optionally jumps the instruction addressed by
  *  the A register and must be one of jump directives in the jump truth table.
  *
  *  DEST optionally defines the destination registers (A, M and/or D) to store
  *  result of COMPUTE. DEST is position invariant ie specifying 'AD' is equavilent
  *  to specifying 'DA'.
  *
  *  @throws UnsupportedOperationException if CInstruction constructed is unsupported
  */
case class CInstruction(
    private val dest: String,
    private val comp: String,
    private val jmp: String
) extends Instruction {
  // check that destination is supported
  val destination = if ((dest.toSet &~ "AMD".toSet).size > 0) {
    throw new UnsupportedOperationException(
      s"C Instruction only supports A,M and/or D as destination(s), got: ${dest}"
    )
  } else dest

  // check that the compute operation are supported
  val computeTable = CInstruction.ComputeTruthTable
  val compute = if (!computeTable.contains(comp)) {
    val supportedOps = computeTable.keySet.mkString(", ")
    throw new UnsupportedOperationException(
      s"Given compute operation in C instruction is unsupported: ${comp}" +
        s".\n Supported computations: ${supportedOps}"
    )
  } else comp

  // check that the compute operation
  val jumpTable = CInstruction.JumpTruthTable
  val jump = if (!jumpTable.contains(jmp)) {
    val supportedJumps = jumpTable.keySet.mkString(", ")
    throw new UnsupportedOperationException(
      s"Unsupported jump directive in C instruction used: ${jmp}" +
        s".\n Supported jump directives: ${supportedJumps}"
    )
  } else jmp

  override def toString = {
    (if (destination.length > 0) s"$destination=" else "") + compute +
      (if (jump.length > 0) s";$jump" else "")
  }

  override def isVirtual: Boolean = false
}

/* [[CInstruction]] companion object */
object CInstruction {

  /** Compute bit truth table mapping compute operation to HACK Machine Language
    *
    * Maps compute operation to binary bits in the format: a c1 c2 c3 c4 c5 c6
    */
  private val computeTruthTable = Map(
    "0" -> "0101010",
    "1" -> "0111111",
    "-1" -> "0111010",
    "D" -> "0001100",
    "A" -> "0110000",
    "!D" -> "0001101",
    "!A" -> "0110001",
    "-D" -> "0001111",
    "-A" -> "0110011",
    "D+1" -> "0111111",
    "A+1" -> "0110111",
    "D-1" -> "0001110",
    "A-1" -> "0110010",
    "D+A" -> "0000010",
    "D-A" -> "0010011",
    "A-D" -> "0000111",
    "D&A" -> "0000000",
    "D|A" -> "0010101"
  )
  // expand compute truth table with 'a' bit to use M instead of A register
  val ComputeTruthTable = computeTruthTable ++ (
    computeTruthTable
      .filter(_._1.contains('A'))
      .map { case (op, bin) => (op.replace('A', 'M'), "1" + bin.tail) }
  )

  /* Compute bit truth table mapping jump instruction to HACK Machine Language
   *
   * Maps jump instruction to binary bits in the format: j1 j2 j3
   */
  val JumpTruthTable = Map(
    "" -> "000",
    "JGT" -> "001",
    "JEQ" -> "010",
    "JGE" -> "011",
    "JLT" -> "100",
    "JNE" -> "101",
    "JLE" -> "110",
    "JMP" -> "111"
  )
}

/** Represents a Label Declaration virtual HACK instruction in form (LABEL)
  *
  * Labels the real HACK instruction with LABEL. LABEL must be a valid symbol
  * that does not collide with any of the builtin symbols
  *
  * @throws IllegalArgumentException if label given is not a valid symbol
  * @throws NameAlreadyBoundException if label given collides with builtin symbol
  */
case class LabelDeclaration(val label: String) extends Instruction {
  // check that label is valid symbol
  if (!SymbolPattern.matches(label)) {
    throw new IllegalArgumentException(
      s"Cannot declare label with invalid symbol: $label"
    )
  }
  // check that label dofest not collide with builtin symbols
  if (BuiltinSymbols.keySet.contains(label)) {
    val builtinNames = BuiltinSymbols.keySet.mkString(", ")
    throw new NameAlreadyBoundException(
      s"Declared label collides with builtin symbol: $label" +
        s"Builtin symbols cannot be used as label names: $builtinNames"
    )
  }
  override def isVirtual: Boolean = true

  override def toString = s"($label)"
}
