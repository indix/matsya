package in.ashwanthkumar.matsya

import java.io.File

import com.google.common.primitives.Longs
import org.rocksdb.{FlushOptions, Options, RocksDB}

class RocksDBStore(input: String) extends TimeSeriesStore with StateStore {

  RocksDB.loadLibrary()

  private lazy val delegate = {
    val dbOptions = new Options()
      .setCreateIfMissing(true)
      .setMaxBackgroundCompactions(2)
      .setWalTtlSeconds(86400)
    new File(input).mkdirs
    RocksDB.open(dbOptions, input)
  }

  override def exists(name: String, az: String): Boolean = {
    delegate.get(bytes(name + az)) != null
  }
  override def exists(name: String): Boolean = {
    delegate.get(bytes(name)) != null
  }
  override def get(name: String, az: String): List[Metric] = {
    val value = delegate.get(bytes(name + az))
    if (value != null) JSONUtil.fromJson[List[Metric]](new String(value))
    else List.empty[Metric]
  }
  override def batchPut(instanceType: String, az: String, metrics: List[Metric]): Unit = {
    delegate.put(bytes(instanceType + az), JSONUtil.toJSON(metrics).getBytes)
  }
  override def get(name: String): State = {
    JSONUtil.fromJson[State](new String(delegate.get(name.getBytes)))
  }
  override def save(name: String, state: State): Unit = {
    delegate.put(bytes(name), JSONUtil.toJSON(state).getBytes)
  }
  override def close(): Unit = {
    delegate.flush(new FlushOptions().setWaitForFlush(true))
    delegate.compactRange()
    delegate.close()
  }

  override def updateLastRun(identifier: String): Unit = {
    delegate.put(bytes(identifier), Longs.toByteArray(System.currentTimeMillis()))
  }
  override def lastRun(identifier: String): Long = {
    val value = delegate.get(bytes(identifier))
    if (value != null) Longs.fromByteArray(value)
    else 0
  }

  private def bytes(str: String) = str.getBytes
}
