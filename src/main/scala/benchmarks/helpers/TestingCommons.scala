package scorex.crypto

import java.io.File

import scala.util.Random

trait TestingCommons {
  val dirName = "/tmp/scorex-test/test/"
  val treeDir = new File(dirName)
  treeDir.mkdirs()

  def genElements(howMany: Int, seed: Long, size: Int = 32): Seq[Array[Byte]] = {
    val r = Random
    r.setSeed(seed)
    (0 until howMany).map { l =>
      r.nextString(16).getBytes.take(size)
    }
  }

  def time[R](block: => R): (Float, R) = {
    val t0 = System.nanoTime()
    val result = block // call-by-name
    val t1 = System.nanoTime()
    ((t1 - t0).toFloat / 1000000, result)
  }
}
