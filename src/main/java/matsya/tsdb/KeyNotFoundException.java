package matsya.tsdb;

public class KeyNotFoundException extends Exception {
    public KeyNotFoundException(String message) {
        super(message);
    }

    public KeyNotFoundException(Throwable cause) {
        super(cause);
    }
}
