import main.scala.Global
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileUtil, Path, FileSystem}

object FsUtils {
  def copyFromLocal (source: String, dest: String) = {

    val conf = new Configuration()
    conf.set("fs.default.name", "hdfs://" + Global.host + ":" + Global.fsPort)
    conf.set("mapred.job.tracker", Global.host + ":" + Global.jtPort)
    //disable caching to avoid java.io.IOException: Filesystem closed
    conf.setBoolean("fs.hdfs.impl.disable.cache", true);

    val fileSystem = FileSystem.get(conf)
    val srcPath = new Path(source)

    val dstPath = new Path(dest)
    // Check if the file already exists
    if (fileSystem.exists(dstPath)) {
      println(dstPath + "exists!") // console log
      delFromHdfs(source)
      //return
    }

    // Get the filename out of the file path
    val filename = source.substring(source.lastIndexOf('/') + 1, source.length())

    fileSystem.copyFromLocalFile(srcPath, dstPath)
    println("File " + filename + " (local) " + "copied to " + dest + " (hdfs)")
    fileSystem.close()

  }

  def copyFromHdfs (source: String, dest: String) = {

    val conf = new Configuration()
    conf.set("fs.default.name", "hdfs://" + Global.host + ":" + Global.fsPort)
    conf.set("mapred.job.tracker", Global.host + ":" + Global.jtPort)

    val fileSystem = FileSystem.get(conf)
    val srcPath = new Path(source)

    val dstPath = new Path(dest)
    // Check if the file already exists
    if (fileSystem.exists(dstPath)) {
      println(dstPath + " exists!")
      // return
    }

    // Get the filename out of the file path
    val filename = source.substring(source.lastIndexOf('/') + 1, source.length())

    fileSystem.copyToLocalFile(srcPath, dstPath)
    println("File " + filename + " (hdfs) " + "copied to " + dest + " (local)")
    fileSystem.close()

  }

  def mergeOnHdfs(source: String, dest: String) = {

    val conf = new Configuration()
    conf.set("fs.default.name", "hdfs://" + Global.host + ":" + Global.fsPort)
    conf.set("mapred.job.tracker", Global.host + ":" + Global.jtPort)

    val fileSystem = FileSystem.get(conf)
    val srcPath = new Path(source)

    val dstPath = new Path(dest)
    // Check if the file already exists
    if (fileSystem.exists(dstPath)) {
      println(dstPath + " exists!")
      // return
    }

    // Get the filename out of the file path
    val filename = source.substring(source.lastIndexOf('/') + 1, source.length())

    FileUtil.copyMerge(fileSystem, srcPath, fileSystem, dstPath, false, conf, null)

    println("File " + filename + " (hdfs) " + "copied to " + dest + " (hdfs)")
    fileSystem.close()
  }

  def delFromHdfs(path: String) = {
    val conf = new Configuration()
    conf.set("fs.default.name", "hdfs://" + Global.host + ":" + Global.fsPort)
    conf.set("mapred.job.tracker", Global.host + ":" + Global.jtPort)

    val fileSystem = FileSystem.get(conf)
    fileSystem.delete(new Path(path), true)

    println("File " + path + " (hdfs) " + "deleted")
    fileSystem.close()
  }

}
