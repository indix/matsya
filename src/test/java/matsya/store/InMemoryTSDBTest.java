package matsya.store;

import matsya.tsdb.Key;
import matsya.tsdb.KeyNotFoundException;
import matsya.tsdb.TSDB;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InMemoryTSDBTest {
    TSDB store = new InMemoryTSDB();

    @Test
    public void shouldPutInAKV() throws IOException {
        store.put(1l, toBytes("key"), toBytes("value"));
    }

    @Test
    public void shouldGetValueForAKey() throws IOException, KeyNotFoundException {
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

        byte[][] values = store.scan(1l, 5l, toBytes("k"));
        for (int i = 0; i < values.length; i++) {
            assertThat("Failed at index " + i, fromBytes(values[i]), is("value" + i));
        }
    }


    public byte[] toBytes(String value) {
        return value.getBytes();
    }

    public String fromBytes(byte[] bytes) {
        return new String(bytes);
    }

}
