object Example extends App {

  import Expression._
  import FsUtils._

  //copyFromLocal("data", "data")

  /*copyFromLocal("examples/inputMatrixout.tsv", "examples/inputMatrix.tsv")
  copyFromLocal("examples/inputMatrix2out.tsv", "examples/inputMatrix2.tsv")*/
  //delFromHdfs("examples")
  val x = Sum(Matrix("examples/mat1_50.tsv",50,50,false),
    Matrix("examples/mat2_50.tsv",50,50,false))
  x.run("examples/answer.tsv")
  //val x = Sum(Number("examples/1.tsv"), Number("examples/2.tsv"))
  //x.run("examples/answer.tsv")
  //val y = Sum(Number("examples/1.tsv"), Number("examples/2.tsv"))
  //y.run



}
