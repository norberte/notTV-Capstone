package filesharingsystem;
import java.io.File;
import java.util.List;
import java.util.function.Consumer;

public interface DownloadProcess {
    public interface Client {
	
	/**
	 * Waits for the download to complete.
	 *
	 */
	void waitForDownload();
	
	/**
	 * Returns the downloaded files.
	 *
	 * @return
	 */
	List<File> files();
    }
    
    /**
     * Starts a download of the magnetURI, and calls the fileHook
     * with the files in the torrent as soon as it loads the torrent (files not completed.)
     * Returns the client used. Call client.waitForCompletion(); to wait for it to complete.
     *
     * @param fileHook
     */
    Client download(Consumer<List<File>> fileHook);

    default Client download() {
	return download(lf->{});
    }
}
