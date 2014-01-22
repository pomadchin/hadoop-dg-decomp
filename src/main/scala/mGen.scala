import java.io._
import scala.util.Random

object mGen {
  def main(args: Array[String]) {
    var file = ""
    var rows = 0
    var cols = 0
    var bounds = 0
    var nat = true

    if(args.isDefinedAt(0)) file = args(0)
    if(args.isDefinedAt(1)) rows = args(1).toInt
    if(args.isDefinedAt(2)) cols = args(2).toInt
    if(args.isDefinedAt(3)) bounds = args(3).toInt
    if(args.isDefinedAt(4)) nat = args(4).toBoolean
    println("Generating...")

    if(args.isDefinedAt(0)) {
      val writer = new PrintWriter(new File(file))

      def generate(rows: Int, cols: Int) = {
        def generateRec(r: Int, c: Int): Boolean =
          if(r > 0 && c > 0) {
            if (nat) writer.write((Random.nextInt(bounds)) + "\t")
            else writer.write((2 * Random.nextDouble - 1) * bounds + "\t")
            generateRec(r, c - 1)
          } else if (r > 0 && c == 0) {
            if(nat) writer.write((Random.nextInt(bounds)) + "\n")
            else writer.write((2 * Random.nextDouble - 1) * bounds + "\n")
            generateRec(r - 1, cols)
          } else true

        generateRec(rows, cols)
      }

      generate(rows, cols)

      writer.close()

      println("Complete!")
    } else println("Failed.")

  }
}