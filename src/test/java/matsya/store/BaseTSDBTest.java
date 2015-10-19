package matsya.store;

import com.google.common.collect.ImmutableList;
import matsya.tsdb.Key;
import matsya.tsdb.Metric;
import matsya.tsdb.TSDB;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

abstract public class BaseTSDBTest {
    protected TSDB store;

    public BaseTSDBTest(TSDB store) {
        this.store = store;
    }

    @After
    public void tearDown() throws Exception {
        store.flush();
        store.close();
    }

    @Test
    public void shouldPutInAKV() throws Exception {
        store.put(1l, toBytes("key"), toBytes("value"));
    }

    @Test
    public void shouldGetValueForAKey() throws Exception {
        store.put(1l, toBytes("key"), toBytes("value"));
        byte[] value = store.get(Key.of(toBytes("key"), 1l));
        assertThat(value, is(toBytes("value")));
    }

    @Test
    public void shouldGetRangeOfValuesForAPrefix() throws Exception {
        store.put(1l, toBytes("key"), toBytes("value0"));
        store.put(2l, toBytes("key"), toBytes("value1"));
        store.put(3l, toBytes("key"), toBytes("value2"));
        store.put(4l, toBytes("key"), toBytes("value3"));
        store.put(5l, toBytes("key"), toBytes("value4"));

        byte[][] values = store.scan(1l, 5l, toBytes("key"));
        for (int i = 0; i < values.length; i++) {
            assertThat("Failed at index " + i, fromBytes(values[i]), is("value" + i));
        }
    }

    @Test
    public void shouldBatchPutData() throws Exception {
        store.batchPut(ImmutableList.of(
                metric(1l, "key2", "value0"),
                metric(2l, "key2", "value1"),
                metric(3l, "key2", "value2"),
                metric(4l, "key2", "value3"),
                metric(5l, "key2", "value4")
        ));

        byte[][] values = store.scan(1l, 5l, toBytes("key2"));
        for (int i = 0; i < values.length; i++) {
            assertThat("Failed at index " + i, fromBytes(values[i]), is("value" + i));
        }
    }

    private Metric metric(long ts, String key, String value) {
        return new Metric().setKey(Key.of(toBytes(key), ts)).setValue(toBytes(value));
    }

    public byte[] toBytes(String value) {
        return value.getBytes();
    }

    public String fromBytes(byte[] bytes) {
        return new String(bytes);
    }

}
