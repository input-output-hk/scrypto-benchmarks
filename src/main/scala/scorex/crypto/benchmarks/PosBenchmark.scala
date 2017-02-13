package scorex.crypto.benchmarks

import org.h2.mvstore.MVStore
import scorex.crypto.authds.avltree._
import scorex.crypto.authds.avltree.batch.Modification._
import scorex.crypto.authds.avltree.batch.{BatchAVLProver, BatchAVLVerifier, Insert}
import scorex.crypto.encode.Base58
import scorex.crypto.hash.Blake2b256Unsafe

import scala.collection.mutable
import scala.util.{Failure, Success}


object PosBenchmark extends App {

  implicit val hf = new Blake2b256Unsafe()

  val InitSize = 46000000
  val AdditionsPerBlock = 230000
  val Blocks = 200

  def genKey(blockNum: Int, pos: Int) = hf(s"$blockNum--$pos".getBytes)

  def lookupFunction(value: AVLValue) = {
    case None => Failure(new Exception("not found)"))
    case Some(v) => Success(Some(v))
  }: UpdateFunction

  def runFull() = {
    val db = new MVStore.Builder().
      fileName("/tmp/sheetdb").
      cacheSize(128).
      autoCommitDisabled().
      open()

    val sheet = db.openMap[Array[Byte], Long]("sheet")
    val genKeys = mutable.Buffer[Array[Byte]]()

    (0 until InitSize) foreach { p =>
      val k = genKey(0, p)
      sheet.put(k, 0)
      if (p % 200000 == 0) {
        println(s"--Init: $p elements written")
        genKeys.append(k)
      }
    }
    db.commit()

    println("Starting full validation test")

    (1 to Blocks) foreach { block =>

      val time = genKeys.foldLeft(0L){case (t, gk) =>
        val startTime = System.currentTimeMillis()
        sheet.get(gk)
        t + (System.currentTimeMillis() - startTime)
      } / genKeys.size.toFloat

      println(s"Block: $block, time: $time")

      (0 until AdditionsPerBlock).foreach { p =>
        val k = genKey(block, p)
        if (p == 0) genKeys.append(k)
        sheet.put(k, 0)
      }
      db.commit()
    }
  }

  def runLight() = {
    val prover = new BatchAVLProver()
    val genKeys = mutable.Buffer[Array[Byte]]()

    (0 until InitSize) foreach { p =>
      val k = genKey(0, p)
      prover.performOneModification(Insert(k, Array.fill(8)(0: Byte)))
      if (p % 200000 == 0) {
        println(s"--Init: $p elements written")
        genKeys.append(k)
        prover.generateProof
      }
    }

    prover.generateProof

    (1 to Blocks) foreach { block =>

      val rh = prover.rootHash
      println(s"rh: ${Base58.encode(rh)}")

      val time = genKeys.foldLeft(0L){case (t, gk) =>
        prover.performOneModification(gk, (k => Success(k)): UpdateFunction)
        val gp = prover.generateProof
        println(s"ph: ${Base58.encode(prover.rootHash)}")

        val verifier = new BatchAVLVerifier(rh, gp)

        val startTime = System.currentTimeMillis()
        verifier.performOneModification(gk, (k => Success(k)): UpdateFunction).get
        val endTime = System.currentTimeMillis()

        t + (endTime - startTime)
      } / genKeys.size.toFloat


      println(s"Block: $block, time: $time")

      (0 until AdditionsPerBlock).foreach { p =>
        val k = genKey(block, p)
        if (p == 0) genKeys.append(k)
        prover.performOneModification(Insert(k, Array.fill(8)(0: Byte)))
      }
      prover.generateProof
    }
  }

  runLight()
}
