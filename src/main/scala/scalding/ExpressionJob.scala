import com.twitter.scalding.mathematics.{SparseHint, Matrix}
import com.twitter.scalding._
import org.slf4j.{LoggerFactory, Logger}

class ExpressionJob(args: Args) extends Job(args) {
  import Matrix._
  @transient private val logger = LoggerFactory.getLogger(classOf[ExpressionJob])
  trait Expr {
    def eval: Matrix[Long,Long,Int] =  {
      logger.info("Starting evaluation")
      this match {
        case EMatrix(p, r, c) => EMatrix(p, r, c).matrix
        case Sum(e1, e2)         => e1.eval + e2.eval
        case Diff(e1, e2)        => e1.eval - e2.eval
        case Prod(e1, e2)        => e1.eval * e2.eval
        case Transpose(e1)       => e1.eval.transpose
      }
    }

    override def toString = this match {
      case EMatrix(p, r, c) => p.toString
      case Sum(e1, e2)         => "(" + e1.toString + " + " + e2.toString + ")"
      case Diff(e1, e2)        => "(" + e1.toString + " - " + e2.toString + ")"
      case Prod(e1, e2)        => e1.toString + " * " + e2.toString
      case Transpose(e1)       => e1.toString + " T "
    }
  }

  case class EMatrix(pipe: RichPipe, rows:Long, cols:Long) extends Expr {
    lazy val matrix = pipe
      .toMatrix[Long,Long,Int]('row, 'col, 'v)
      .withSizeHint(SparseHint(1.0, rows, cols))
  }

  case class Diff(e1: Expr, e2: Expr) extends Expr

  case class Sum(e1: Expr, e2: Expr) extends Expr

  case class Prod(e1: Expr, e2: Expr) extends Expr

  case class Transpose(e1: Expr) extends Expr

  val xml  = scala.xml.XML.loadFile("examples/expr.xml")
  import scala.xml._
  def buildExpr(expr: Node): Expr =
    if (expr.label.equals("Sum")) Sum(buildExpr(expr.child(1)), buildExpr(expr.child(3)))
    else if (expr.label.equals("Prod")) Prod(buildExpr(expr.child(1)), buildExpr(expr.child(3)))
    else {
      val rows =  (expr \ "@rows").toString.toInt
      val cols  = (expr \ "@cols").toString.toInt
      val isSparse = (expr \ "@sparse").toString.toBoolean
      val path = (expr \ "@filename").toString

      if(!isSparse)  {
        logger.info("Dense matrix " + path )
        val out = path.split(".tsv")(0) + "out.tsv"
        val pipe = matrixConvertTsv(path, out)
        logger.info("Converting is finished: " + path )
        EMatrix(pipe, rows, cols)
      } else {
        val pipe = Tsv(path, ('row, 'col, 'v)).read
        EMatrix(pipe, rows, cols)
      }
    }


  def xml2Node(input: Elem) = (input \\ "Expr").head.child(1)

  val expr: Expr = buildExpr(xml2Node(xml))

  expr.eval.write(Tsv(args("output")))

  def matrixConvertTsv(in: String, out: String):RichPipe = {

    logger.info("Start converting to TSV")
    var prev: Long = 0
    var pos: Long = 1

    val zeroInt = 0
    val zeroDouble = 0.0

    TextLine(in)
      .flatMap('line -> 'number)  { line : String => line.split("\\s+") }
      .mapTo(('offset, 'line, 'number) -> ('row, 'col, 'v)) { res : (Long, String, String) =>
      val (offset, line, number) = res
      pos = if(prev == (offset + 1)) pos + 1 else 1
      prev = offset + 1
      (offset + 1, pos, number) }
      .filter('row, 'col, 'v) { line : (Long, String, String) =>
      val (row, col, v) = line
      (v != zeroInt.toString) && (v != zeroDouble.toString) }
      .write(Tsv(out))
  }

  /*override def config(implicit mode: Mode): Map[AnyRef,AnyRef] =
    super.config(mode) /*++ Map("cascading.app.appjar.path" -> "libs/scalding-core-assembly-0.9.0rc1.jar")*/ ++ Map("cascading.app.appjar.class" -> classOf[ExpressionJob])*/
  /*def matrixConvertTxt(in: String, out: String) {
     Tsv(in, ('row, 'col, 'v))
       .read
       .groupBy('row) { _.sortBy('col).mkString('v, "\t") }
       .mapTo(('row, 'v) -> ('c)) { res : (Long, String) =>
         val (row, v) = res
         v }
       .write(Tsv(out))
  } */
}