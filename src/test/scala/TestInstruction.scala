/*
 * Hackas - HACK Assembler
 * Test HACK Assembly Instructions
 */
package co.mrzzy.nand2tetris.p1.project6;

import org.scalatest.FunSuite
import CInstruction.JumpTruthTable
import CInstruction.ComputeTruthTable
import Symbols.BuiltinSymbols
import javax.naming.NameAlreadyBoundException

class TestAInstruction extends FunSuite {
  test("new AInstruction") {
    val addresses = List(
      "0",
      "3252",
      "R16",
      "LOOP",
      "END_IF",
      "ELSE_IF_3"
    )
    val instructions = addresses.map(AInstruction(_))
    instructions.map(_.address).toSet.equals(addresses.toSet)
  }

  test("new AInstruction: error on non numeric or symbolic address") {
    val badAddresses = List(
      "bad-sym",
      "R@01",
      "has space"
    )

    val instructions = badAddresses.foreach { case badAddr =>
      intercept[IllegalArgumentException] {
        new AInstruction(badAddr)
      }
    }
  }

  test(
    "new AInstruction: error on given numeric address that overflows 15 bit"
  ) {
    intercept[OutOfMemoryError] {
      new AInstruction("32768")
    }
  }

  test("AInstruction.toBinary") {
    val symTable = BuiltinSymbols ++ Map(
      "counter" -> 16,
      "length" -> 21,
      "LOOP" -> 6
    )

    val addresses = List(
      "0",
      "3252",
      "R11",
      "counter",
      "LOOP"
    )
    val binAInstructions = addresses
      .map(AInstruction(_))
      .map(_.toBinary(symTable))

    assert(
      binAInstructions == List(
        "0000000000000000",
        "0000110010110100",
        "0000000000001011",
        "0000000000010000",
        "0000000000000110"
      )
    )
  }
}

class TestCInstruction extends FunSuite {
  test("new CInstruction") {
    // generate valid C instruction test cases
    // permuate valid destinations in C instructions
    val dests = (0 to 3)
      .flatMap("AMD".combinations(_))
      .flatMap(_.permutations)
    val testCases =
      for (
        dest <- dests; compute <- ComputeTruthTable.keySet;
        jmp <- JumpTruthTable.keySet
      )
        yield (dest, compute, jmp)

    val instructions = testCases.map { case (dest, compute, jmp) =>
      new CInstruction(dest, compute, jmp)
    }
    testCases.zip(instructions).foreach {
      case ((dest, compute, jmp), instruction) =>
        assert(
          instruction.compute == compute &&
            instruction.destination == dest &&
            instruction.jump == jmp
        )
    }
  }

  test("new CInstruction: error on unsupported instruction") {
    val badTestCases = List(
      ("Q", "D+1", ""), // bad destination
      ("", "0", "JMQ"), // unknown jump directive
      ("", "A&D", "") // unknown compute operation
    )
    badTestCases.foreach { case (dest, compute, jmp) =>
      intercept[UnsupportedOperationException] {
        new CInstruction(dest, compute, jmp)
      }
    }
  }

  test("CInstruction.toBinary") {
    val binCInstructions = List(
      new CInstruction("", "D+1", ""),
      new CInstruction("", "D&A", ""),
      new CInstruction("D", "A+1", ""),
      new CInstruction("", "D|A", "JGT"),
      new CInstruction("AM", "!D", "JMP")
    ).map(_.toBinary(Map()))

    assert(
      binCInstructions == List(
        "1110011111000000",
        "1110000000000000",
        "1110110111010000",
        "1110010101000001",
        "1110001101101111"
      )
    )
  }

}

class TestLabelDeclaration extends FunSuite {
  test("new LabelDeclaration") {
    val testCases = List(
      "x",
      "X",
      "LOOP",
      "END_IF_2",
      "_PREFIX"
    )

    val instructions = testCases.map(new LabelDeclaration(_))
    instructions.map(_.label).equals(testCases)
  }

  test("new LabelDeclaration: Error on label invalid symbol") {
    val testCases = List(
      "10",
      "1LABEL",
      "bad-label",
      "bad+sign="
    )

    val instructions = testCases.foreach((badLabel) =>
      intercept[IllegalArgumentException] {
        new LabelDeclaration(badLabel)
      }
    )
  }

  test("new LabelDeclaration: Error on label collides with builtin symbol") {
    val builtinNames = BuiltinSymbols.keySet
    val instructions = builtinNames.foreach((builtinName) =>
      intercept[NameAlreadyBoundException] {
        new LabelDeclaration(builtinName)
      }
    )
  }
}
