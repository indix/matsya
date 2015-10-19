package matsya.tsdb;

public class Metric {
    private Key key;
    private byte[] value;

    public Key getKey() {
        return key;
    }

    public Metric setKey(Key key) {
        this.key = key;
        return this;
    }

    public byte[] getValue() {
        return value;
    }

    public Metric setValue(byte[] value) {
        this.value = value;
        return this;
    }
}
