/*
 * Hackas - HACK Assembler
 * Main / Entrypoint
*/
package co.mrzzy.nand2tetris.p1.project6;

object Main extends App {
  val usage = """Usage: hackas [--output OUTPUT] INPUT
  Assemble the HACK Assembly read from INPUT into HACK Machine Language.
  Write the assembled machine language to OUTPUT.

  Options:
    --output  The path of the file to write the assembled machine language to.
              Defaults to 'a.out'
  """
}
