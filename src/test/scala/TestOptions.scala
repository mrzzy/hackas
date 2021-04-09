/*
 * Hackas - HACK Assembler
 * Test Program Options
*/
package co.mrzzy.nand2tetris.p1.project6;

import org.scalatest.FunSuite

class TestOptions extends FunSuite {
  // test Options.formArgs
  test("Options.fromArgs"){
    val opts = Options.fromArgs(Array(
      "hackas",
      "--output",
      "out.hack",
      "in.asm"
    ))

    assert(opts.inputPath == "in.asm")
    assert(opts.outputPath == "out.hack")
  }

  test("Options.fromArgs output path unspecified"){
    val opts = Options.fromArgs(Array(
      "hackas",
      "in.asm"
    ))

    assert(opts.inputPath == "in.asm")
    assert(opts.outputPath == "a.out")
  }

  test("Options.fromArgs error missing input path") {
    val exception = intercept[IllegalArgumentException]{
      Options.fromArgs(Array(
        "hackas",
        "--output",
        "in.asm"
      ))
    }
  }
}
