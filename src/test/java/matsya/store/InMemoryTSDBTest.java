package matsya.store;

public class InMemoryTSDBTest extends BaseTSDBTest {
    public InMemoryTSDBTest() {
        super(new InMemoryTSDB());
    }
}
