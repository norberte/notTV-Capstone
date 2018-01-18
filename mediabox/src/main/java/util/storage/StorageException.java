package util.storage;

public class StorageException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = -3559292316156206376L;

    public StorageException(String message) {
	super(message);
    }

    public StorageException(String message, Throwable cause) {
	super(message, cause);
    }
}
