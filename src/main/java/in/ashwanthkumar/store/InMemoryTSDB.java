package in.ashwanthkumar.store;

import in.ashwanthkumar.tsdb.Key;
import in.ashwanthkumar.tsdb.KeyNotFoundException;
import in.ashwanthkumar.tsdb.Metric;
import in.ashwanthkumar.tsdb.TSDB;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

/**
 * InMemory implementation of TimeSeries Database for testing purposes.
 */
public class InMemoryTSDB extends TSDB {
    Map<Key, ByteBuffer> points = new TreeMap<>();

    @Override
    public void put(long timestamp, byte[] key, byte[] value) throws IOException {
        points.put(Key.of(key, timestamp), ByteBuffer.wrap(value));
    }

    @Override
    public byte[] get(long timestamp, byte[] key) throws IOException, KeyNotFoundException {
        ByteBuffer value = points.get(Key.of(key, timestamp));
        if (value == null)
            throw new KeyNotFoundException("timestamp=" + timestamp + ", bytes=" + Arrays.toString(key) + " is not found.");

        return value.array();
    }

    @Override
    public void batchPut(Iterable<Metric> points) throws Exception {
        for (Metric point : points) {
            put(point.getKey(), point.getValue());
        }
    }

    @Override
    public byte[][] scan(long start, long end, byte[] keyPrefix) throws Exception {
        ArrayList<byte[]> values = new ArrayList<>();
        for (Key key : points.keySet()) {
            if (key.isAfter(start) && key.isBefore(end) && key.prefixMatch(keyPrefix))
                values.add(points.get(key).array());
        }
        return values.toArray(new byte[0][]);
    }

    @Override
    public void flush() throws Exception { /* No Op */}

    @Override
    public void close() throws Exception { /* No Op */}
}
