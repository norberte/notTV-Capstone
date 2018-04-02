package filesharingsystem.process;

import java.io.File;
import java.util.Optional;

public interface DownloadProcess {

    /**
     * Starts a download of the magnetURI, and BLOCKS until it is done.
     * @return the file if it was successfully downloaded.
     */
    Optional<File> download();
}
