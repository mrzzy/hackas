package co.mrzzy.nand2tetris.p1.project6;
/*
 * Hackas - HACK Assembler
 * Test HACK Assembly Parser
 */

import org.scalatest.FunSuite
import Parser.Line

class TestParser extends FunSuite {
  test("Parser.strip") {
    val strippedLines = Parser.strip(
      Vector(
        Line("", 1),
        Line("// comment", 2),
        Line("// another comment", 3),
        Line("@END // in line comment ", 4),
        Line("M = D + 1", 5),
        Line("", 6),
        Line("", 7),
        Line("A=D -1 ; JMP ", 8),
        Line("(END)", 9),
        Line("    @END", 10),
        Line("    0;JMP", 11)
      )
    )
    assert(
      strippedLines.toVector == Vector(
        Line("@END", 4),
        Line("M=D+1", 5),
        Line("A=D-1;JMP", 8),
        Line("(END)", 9),
        Line("@END", 10),
        Line("0;JMP", 11)
      )
    )
  }

  test("Parser.parseLine") {
    // generate C kind test cases
    val dests = List("", "M=", "D=", "MD=", "A=", "AM=", "AD=", "AMD=")
    val computes = List(
      "0",
      "1",
      "-1",
      "D",
      "A",
      "!D",
      "!A",
      "-D",
      "-A",
      "D+1",
      "A+1",
      "D-1",
      "A-1",
      "D+1",
      "D-A",
      "A-D",
      "D&A",
      "D|A"
    )
    val jumps = List(
      "",
      ";JGT",
      ";JEQ",
      ";JGE",
      ";JLT",
      ";JNE",
      ";JLE",
      ";JMP"
    )
    val cTestCases =
      for (dest <- dests; compute <- computes; jump <- jumps)
        yield (dest + compute + jump -> new CInstruction(dest, compute, jump))

    // A instruction test cases
    val aTestCases = List(
      "@ENDIF" -> new AInstruction("ENDIF"),
      "@LOOP" -> new AInstruction("LOOP"),
      "@R16" -> new AInstruction("R16"),
      "@3325" -> new AInstruction("3325")
    )
    val testCases = aTestCases ++ cTestCases
    val expectedInstructions = testCases.map(_._2)
    val actualInstructions = testCases
      .map { case (text, _) => Line(text, 1) }
      .map(Parser.parseLine(_))
    actualInstructions.zip(expectedInstructions).foreach {
      case (actual, expected) => {
        assert(actual == expected)
      }
    }
  }
}
