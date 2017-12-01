package spring.storage;

public class StorageFileNotFoundException extends StorageException {

    /**
     *
     */
    private static final long serialVersionUID = 6737529429182421424L;

    public StorageFileNotFoundException(String message) {
	super(message);
    }

    public StorageFileNotFoundException(String message, Throwable cause) {
	super(message, cause);
    }
}
