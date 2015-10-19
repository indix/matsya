package in.ashwanthkumar.tsdb;

import com.google.common.primitives.Longs;
import com.google.common.primitives.UnsignedBytes;

import java.util.Arrays;
import java.util.Objects;

public class Key implements Comparable<Key> {
    private long timestamp;
    private byte[] key;

    public static Key of(byte[] key) {
        return of(key, System.currentTimeMillis());
    }

    public static Key of(byte[] key, long timestamp) {
        return new Key()
                .withKey(key)
                .withTimestamp(timestamp);
    }

    public Key withTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Key withKey(byte[] key) {
        this.key = key;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getKey() {
        return key;
    }

    public boolean isBefore(long future) {
        return this.timestamp <= future;
    }

    public boolean isAfter(long past) {
        return this.timestamp >= past;
    }

    public boolean prefixMatch(byte[] prefix) {
        if (key.length < prefix.length) return false;
        for (int i = 0; i < prefix.length; i++) {
            if (key[i] != prefix[i]) return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key1 = (Key) o;
        return Objects.equals(timestamp, key1.timestamp) &&
                Arrays.equals(key, key1.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, Arrays.hashCode(key));
    }

    @Override
    public int compareTo(Key o) {
        return UnsignedBytes.lexicographicalComparator().compare(key, o.key) +
                Longs.compare(timestamp, o.timestamp);
    }
}
