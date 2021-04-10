package co.mrzzy.nand2tetris.p1.project6;
/*
 * Hackas - HACK Assembler
 * HACK Assembly Instructions
 */

/** Represents a HACK Assembly Instruction */
trait Instruction {

  /** Convert this Assembly [[Instruction]] to its binary Machine Language representation
    *
    * @return The binary HACK Machine Language representation of this instruction
    */
  //def toBinary(symTable: Map[Int, f): String
}

/** Represents a A-Kind HACK instruction in the format @ADDRESS */
case class AInstruction(val address: String) extends Instruction {
  // TODO(mrzzy): return human instructions
  override def toString = s"@$address"
}

/** Represents a C-Kind HACK instruction in the format DEST=COMPUTE;JUMP */
case class CInstruction(val dest: String, val compute: String, val jump: String)
    extends Instruction {
  override def toString = {
    dest +
      (if (dest.length >= 1) "=" else "") + compute +
      (if (jump.length >= 1) ";" else "") + jump
  }
}

/** Represents a Label Declaration virtual HACK instruction in form (LABEL)
  *
  *  Virtual Instruction: Has no binary representation as it does not actually
  *  exist in HACK Machine Language.
  */
case class LabelDeclare(val label: String) extends Instruction {}
