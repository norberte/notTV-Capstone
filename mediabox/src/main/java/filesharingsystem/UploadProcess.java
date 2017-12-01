package filesharingsystem;

import java.io.File;


public interface UploadProcess {
    public class UploadException extends Exception {
	public UploadException(String string) {
	    super(string);
	}
	private static final long serialVersionUID = 5135331814835498558L;
    }
    /**
     * Uploads a list of files into the given directory.
     *
     * @param dirname
     * @param files
     */
    void upload(String name, File parent, File... files) throws UploadException;

    
    /**
     * Uploads the single torrent file.
     *
     * @param f
     */
    void upload(String name, File f) throws UploadException;

    
    /**
     * Stops the seeding process.
     *
     */
    void stop();
}
