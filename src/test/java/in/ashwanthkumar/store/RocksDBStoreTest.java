package in.ashwanthkumar.store;

public class RocksDBStoreTest extends BaseTSDBTest {

    public RocksDBStoreTest() {
        super(new RocksDBStore("/tmp/test-rocksdb"));
    }

}