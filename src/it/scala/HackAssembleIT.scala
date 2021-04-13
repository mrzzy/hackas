package co.mrzzy.nand2tetris.p1.project6;
/*
 * Hackas - HACK Assembler
 * Hack Assembly Intergration Test
 */

import org.scalatest.FunSuite
import scala.io.Source
import java.io.File

class HackAssembleIT extends FunSuite {
  test("Assemble Add.asm, Max.asm, Rect.asm, Pong.asm") {
    val testCases = List("Add", "Max", "Rect", "Pong")

    testCases.foreach(testCase => {
      val asmPath = getClass().getResource(s"/$testCase.asm").getPath()
      // create temp file for Hackas to writeto
      val outFile = File.createTempFile("out", ".hack")

      // use hackas to assemble Hack Assembly and write to temp file
      Main.main(
        Array(
          "--output",
          outFile.getPath(),
          asmPath
        )
      )

      // verify assembled HACK machine language same as expected
      val actualHack = Source.fromFile(outFile, "UTF-8").getLines.mkString("\n")
      val expectedHack = Source
        .fromResource(s"${testCase}Expected.hack")
        .getLines
        .mkString("\n")
      assert(actualHack == expectedHack)
    })
  }
}
