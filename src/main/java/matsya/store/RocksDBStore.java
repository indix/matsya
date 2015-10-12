package matsya.store;

import matsya.tsdb.Key;
import matsya.tsdb.KeyNotFoundException;
import matsya.tsdb.TSDB;
import org.rocksdb.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class RocksDBStore extends TSDB {
    static {
        RocksDB.loadLibrary();
    }

    private RocksDB db;
    private ReadOptions tail = new ReadOptions().setFillCache(true).setTailing(true);

    public RocksDBStore(String input) {
        try {
            Options dbOptions = new Options()
                    .setCreateIfMissing(true)
                    .setMaxBackgroundCompactions(2);
            new File(input).mkdirs();
            db = RocksDB.open(dbOptions, input);
        } catch (RocksDBException e) {
            throw new RuntimeException(e);
        }
    }

    // For Testing purposes only
    public RocksDBStore(RocksDB db) {
        this.db = db;
    }

    @Override
    public void put(long timestamp, byte[] key, byte[] value) throws Exception {
        byte[] keyInBytes = toKeyBytes(timestamp, key);
        db.put(keyInBytes, value);
    }

    @Override
    public byte[] get(long timestamp, byte[] key) throws IOException, KeyNotFoundException {
        try {
            return db.get(toKeyBytes(timestamp, key));
        } catch (RocksDBException e) {
            throw new KeyNotFoundException(e);
        }
    }

    @Override
    public byte[][] scan(long start, long end, byte[] keyPrefix) throws Exception {
        RocksIterator rocksIterator = db.newIterator(tail);
        rocksIterator.seek(toKeyBytes(start, keyPrefix));
        Key key = toKey(rocksIterator.key());
        ArrayList<byte[]> values = new ArrayList<>();
        while (rocksIterator.isValid() &&
                key.prefixMatch(keyPrefix) &&
                key.isAfter(start) && key.isBefore(end)) {
            values.add(rocksIterator.value());
            rocksIterator.next();
        }
        rocksIterator.dispose();
        // FIXME - Not sure if there are any perf issues doing List.toArray
        return values.toArray(new byte[0][]);
    }

    @Override
    public void flush() throws Exception {
        db.flush(new FlushOptions());
    }

    @Override
    public void close() throws Exception {
        db.close();
    }

    private byte[] toKeyBytes(long timestamp, byte[] key) {
        return ByteBuffer.allocate(8 + key.length)
                .putLong(timestamp)
                .put(key)
                .array();
    }

    private Key toKey(byte[] keyInBytes) {
        ByteBuffer buffer = ByteBuffer.wrap(keyInBytes);
        long timestamp = buffer.getLong(0);
        byte[] key = buffer.slice().array();
        return Key.of(key, timestamp);
    }
}
