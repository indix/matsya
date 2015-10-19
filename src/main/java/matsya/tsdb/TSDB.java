package matsya.tsdb;

import java.io.IOException;
import java.util.Map;

/**
 * Assumed to be Not Thread safe
 */
abstract public class TSDB {
    /**
     * Put the value against the key at the specified timestamp
     *
     * @param timestamp
     * @param key
     * @param value
     */
    public abstract void put(long timestamp, byte[] key, byte[] value) throws Exception;

    /**
     * Exactly same as {@link #put(long, byte[], byte[])} but assumes timestamp to be current milliseconds
     *
     * @param key
     * @param value
     */
    public void put(byte[] key, byte[] value) throws Exception {
        put(System.currentTimeMillis(), key, value);
    }

    /**
     * Exactly same as {@link #put(long, byte[], byte[])} but the timestamp and key collected into an object
     *
     * @param key
     * @param value
     */
    public void put(Key key, byte[] value) throws Exception {
        put(key.getTimestamp(), key.getKey(), value);
    }

    /**
     * Get the value of the key at the given timestamp
     *
     * @param timestamp
     * @param key
     * @return
     */
    public abstract byte[] get(long timestamp, byte[] key) throws IOException, KeyNotFoundException;

    /**
     * Exactly same as {@link #get(long, byte[])} with timestamp and key collected into an object
     *
     * @param key
     * @return
     */
    public byte[] get(Key key) throws IOException, KeyNotFoundException {
        return get(key.getTimestamp(), key.getKey());
    }

    /**
     * Add data points in bulk
     */
    abstract public void batchPut(Iterable<Metric> points) throws Exception;

    /**
     * Both Start and End inclusive scan with the given key prefix.
     *
     * @param start
     * @param end
     * @param keyPrefix
     * @return
     */
    abstract public byte[][] scan(long start, long end, byte[] keyPrefix) throws Exception;

    /**
     * Triggers a flush on the underlying datastore
     * @throws Exception
     */
    abstract public void flush() throws Exception;

    /**
     * Close the handle / connection to the underlying datastore and release all necessary resources.
     * @throws Exception
     */
    abstract public void close() throws Exception;
}
