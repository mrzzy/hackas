/*
 * Hackas - HACK Assembler
 * Test Program Options
 */
package co.mrzzy.nand2tetris.p1.project6;

import org.scalatest.FunSuite

class TestOptions extends FunSuite {
  // test Options.formArgs
  test("Options.fromArgs") {
    val opts = Options.fromArgs(
      Array("--output", "out.hack", "in.asm")
    )
    assert(opts == Options("in.asm", "out.hack", "false"))
  }

  test("Options.fromArgs: Output path unspecified") {
    val opts = Options.fromArgs(
      Array("add.asm")
    )
    assert(opts == Options("add.asm", "out.hack", "false"))
  }

  test("Options.fromArgs: Only help option") {
    val opts = Options.fromArgs(
      Array("--help")
    )
    assert(opts == Options("in.asm", "out.hack", "true"))
  }

  test("Options.fromArgs: Error missing input path") {
    val exception = intercept[IllegalArgumentException] {
      Options.fromArgs(
        Array("--output", "out.hack")
      )
    }
  }

  test("Options.fromArgs: Error bad option") {
    val exception = intercept[IllegalArgumentException] {
      Options.fromArgs(
        Array("-o", "out.hack", "in.asm")
      )
    }
  }

  test("Options.fromArgs: Error no args given") {
    val exception = intercept[IndexOutOfBoundsException] {
      Options.fromArgs(
        Array()
      )
    }
  }
}
