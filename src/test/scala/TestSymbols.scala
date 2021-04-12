/*
 * Hackas - HACK Assembler
 * Test Symbols
 */
package co.mrzzy.nand2tetris.p1.project6;

import org.scalatest.FunSuite
import Symbols.BuiltinSymbols

class TestSymbols extends FunSuite {
  test("Symbols.scanLabels") {
    val instructions = List(
      new AInstruction("END.IF"),
      new LabelDeclaration("LOOP"),
      new CInstruction("M", "M+1", ""),
      new LabelDeclaration("END_LOOP"),
      new LabelDeclaration("END.IF"),
      new CInstruction("", "M-D", "JLT")
    )

    assert(
      Symbols.scanLabels(instructions) == Map(
        "LOOP" -> 1,
        "END_LOOP" -> 2,
        "END.IF" -> 2
      )
    )
  }

  test("Symbols.scanLabels: error on dangling label definitions") {
    // check that label declarations that do not reference any instructions cause errorf
    val instructions = List(
      new AInstruction("SCREEN"),
      new LabelDeclaration("LOOP")
    )

    intercept[IllegalArgumentException] {
      Symbols.scanLabels(instructions)
    }
  }

  test("Symbols.scanVariables") {
    val instructions = List(
      new AInstruction("KBD"),
      new CInstruction("D", "M", ""),
      new AInstruction("forward_label"),
      new LabelDeclaration("LOOP"),
      new AInstruction("index"),
      new CInstruction("M", "M+1", ""),
      new LabelDeclaration("forward_label"),
      new AInstruction("index"),
      new CInstruction("D", "M", ""),
      new AInstruction("length"),
      new CInstruction("M", "D", "")
    )

    val existingSyms = BuiltinSymbols ++ Symbols.scanLabels(instructions)
    assert(
      Symbols.scanVariables(instructions, existingSyms) == Map(
        "index" -> 16,
        "length" -> 17
      )
    )
  }

  test("Symbols.scan") {
    val instructions = List(
      new AInstruction("KBD"),
      new CInstruction("D", "M+1", ""),
      new AInstruction("END"),
      new CInstruction("", "D", "JGT"),
      new AInstruction("counter"),
      new CInstruction("M", "0", "JMP"),
      new LabelDeclaration("END"),
      new AInstruction("END"),
      new CInstruction("", "0", "JMP")
    )

    assert(
      Symbols.scan(instructions) == BuiltinSymbols ++ Map(
        // label symbol
        "END" -> 6,
        // variable symbols
        "counter" -> 16
      )
    )
  }
}
