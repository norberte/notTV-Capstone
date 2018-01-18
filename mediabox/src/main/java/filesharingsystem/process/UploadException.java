package filesharingsystem.process;

public class UploadException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 2519819932347245555L;

    public UploadException(String message) {
	super(message);
    }

    public UploadException(String message, Throwable cause) {
	super(message, cause);
    }
}
