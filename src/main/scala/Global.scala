package main.scala

object Global {
  // absolute path to project folder! it a template; rm from vers control
  // fs.default.name port
  // mapred.job.tracker port
  val absPath = "/home/username/projectdir"
  val host = "localhost"
  val fsPort = "54310"
  val jtPort = "54311"

  val buildName = "hadoop-dg-decomp"
  val buildVersion = "1.0.0"
}
