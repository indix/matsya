package in.ashwanthkumar.matsya

import java.nio.ByteBuffer

import com.google.common.primitives.Ints
import org.rocksdb.{FlushOptions, RocksDB}

case class State(name: String,
                 currentAZ: Byte,
                 nrOfTimes: Int,
                 spotCount: Int,
                 odCount: Int) {

  def toBytes = State.toBytes(this)
}

object State {
  def toBytes(state: State) = {
    val buffer = ByteBuffer.allocate(1 + (3 * Ints.BYTES))
    buffer
      .put(state.currentAZ)
      .putInt(state.nrOfTimes)
      .putInt(state.spotCount)
      .putInt(state.odCount)
      .array()
  }

  def fromBytes(bytes: Array[Byte]) = {
    val buffer = ByteBuffer.wrap(bytes)
    State(
      name = "", /* Name is stored as key, which is populated later */
      currentAZ = buffer.get(),
      nrOfTimes = buffer.getInt,
      spotCount = buffer.getInt,
      odCount = buffer.getInt
    )
  }
}

class MatsyaStateStore(delegate: RocksDB) extends AutoCloseable {

  def get(name: String) = {
    State.fromBytes(delegate.get(name.getBytes)).copy(name = name)
  }

  def save(state: State, name: String): Unit = {
    delegate.put(name.getBytes, State.toBytes(state))
    delegate.flush(new FlushOptions)
  }

  def close(): Unit = {
    delegate.close()
  }
}
