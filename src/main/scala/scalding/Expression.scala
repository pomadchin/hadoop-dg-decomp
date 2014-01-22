import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.util.GenericOptionsParser
import org.apache.hadoop.fs._
import org.apache.hadoop._
import com.twitter.scalding.{Hdfs, Mode, Local, Args}
import java.io.File
import java.net.URLClassLoader
import main.scala.Global

object Expression {
  trait Expr {

    override def toString = this match {
      case Number(n) => n.toString
      case Sum(e1, e2) => "(" + e1.toString + " + " + e2.toString + ")"
      case Diff(e1, e2) => "(" + e1.toString + " - " + e2.toString + ")"
      case Prod(e1, e2) => e1.toString + " * " + e2.toString
    }

    /*def writeToXML(filename: String) {
      val pw = new java.io.PrintWriter(filename)
      def buildXML(expr: Expr):Unit = expr match {
        case Number(filename) => pw.println("<Matrix filename=\"" + filename  + "\" rows=\"1\"  cols=\"1\" sparse=\"true\" />")
        case Matrix(filename,rows, cols, sparse) =>
          pw.println("<Matrix filename=\"" + filename  + "\" rows=\""+rows+"\"  cols=\""+ cols +"\" sparse=\""+sparse+"\" />")
        case Vector(filename,rows, sparse) =>
          pw.println("<Matrix filename=\"" + filename  + "\" rows=\""+rows+"\"  cols=\"1\" sparse=\""+sparse+"\" />")
        case Sum(e1, e2) =>  {
          pw.println("<Sum>")
          buildXML(e1)
          buildXML(e2)
          pw.println("</Sum>")
        }
        case Diff(e1, e2) => {
          pw.println("<Diff>")
          buildXML(e1)
          buildXML(e2)
          pw.println("</Diff>")
        }
        case Prod(e1, e2) => {
          pw.println("<Prod>")
          buildXML(e1)
          buildXML(e2)
          pw.println("</Prod>")
        }
      }
      pw.println("<Expr>")
      buildXML(this)
      pw.println("</Expr>")
      pw.flush
    }*/

    def writeToXML(filename: String) {
      val pw = new java.io.PrintWriter(filename)
      def buildXML(expr: Expr):Unit = expr match {
        case Number(filename) => {
          pw.println("<Matrix filename=\"" + filename  + "\" rows=\"1\"  cols=\"1\" sparse=\"true\" />")
          FsUtils.copyFromLocal(filename, filename)
        }
        case Matrix(filename,rows, cols, sparse) => {
          pw.println("<Matrix filename=\"" + filename  + "\" rows=\""+rows+"\"  cols=\""+ cols +"\" sparse=\""+sparse+"\" />")
          FsUtils.copyFromLocal(filename, filename)
        }
        case Vector(filename,rows, sparse) => {
          pw.println("<Matrix filename=\"" + filename  + "\" rows=\""+rows+"\"  cols=\"1\" sparse=\""+sparse+"\" />")
          FsUtils.copyFromLocal(filename, filename)
        }
        case Sum(e1, e2) =>  {
          pw.println("<Sum>")
          buildXML(e1)
          buildXML(e2)
          pw.println("</Sum>")
        }
        case Diff(e1, e2) => {
          pw.println("<Diff>")
          buildXML(e1)
          buildXML(e2)
          pw.println("</Diff>")
        }
        case Prod(e1, e2) => {
          pw.println("<Prod>")
          buildXML(e1)
          buildXML(e2)
          pw.println("</Prod>")
        }
      }
      pw.println("<Expr>")
      buildXML(this)
      pw.println("</Expr>")
      pw.flush
    }

    def run(fileOutput:String) {
      writeToXML("examples/expr.xml")

      val name = "ExpressionJob"
      val message = "Run expr.xml."
      val mergedOutput = "merged/" + fileOutput

      val master = Global.host // on cluster, internal ip
      val args: Array[String] = Array("--hdfs", s"$master:" + Global.fsPort, "--output", fileOutput)

      val classesDir = new File("target/scala-2.10/classes/main/scala/")

      val loader = classLoaderSetup()

      if (args.length != 0) {
        Run.run(name, classesDir, message, args)

        FsUtils.mergeOnHdfs(fileOutput, mergedOutput)
        FsUtils.copyFromHdfs(mergedOutput, fileOutput)
        FsUtils.delFromHdfs("merged")
      }
    }
  }

  case class Number(path: String) extends Expr
  case class Matrix(path: String,rows:Long, cols:Long, sparse: Boolean) extends Expr {
    def this(path: String,rows:Long, cols:Long) = this(path, rows, cols, true)
  }

  case class Vector(path: String,rows:Long, sparse: Boolean) extends Expr {
    def this(path: String,rows:Long) = this(path, rows, true)
  }

  case class Diff(e1: Expr, e2: Expr) extends Expr

  case class Sum(e1: Expr, e2: Expr) extends Expr
  case class Prod(e1: Expr, e2: Expr) extends Expr

  def classLoaderSetup(): ClassLoader =  {
    val target = new File("target/scala-2.10/classes/")
    val classFiles = target :: Nil

    val jarFiles = List(
      Global.absPath + "/target/scala-2.10/" + Global.buildName + "-" + Global.buildVersion + ".jar"
    ).map(new File(_))

    val allDeps = jarFiles ::: classFiles

    val depUrls = allDeps map { _.toURI.toURL }

    val loader = new URLClassLoader(depUrls.toArray, getClass.getClassLoader)
    Class.forName(classOf[ExpressionJob].getCanonicalName, true, loader)

    Thread.currentThread.setContextClassLoader(loader)

    loader
  }
}

