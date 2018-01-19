package filesharingsystem;


public class PortMapException extends Exception {
    private static final long serialVersionUID = 8231030470900947796L;

    public PortMapException(String message, Throwable cause) {
	super(message, cause);
    }

    public PortMapException(String message) {
	super(message);
    }
}
