package filesharingsystem.process;

import java.io.File;
import java.util.Optional;

public interface DownloadProcess {

    /**
     * Starts a download of the magnetURI, and may block until it is done,
     * depending on the implementation.
     * @return the file if it was successfully downloaded.
     */
    Optional<File> download();

    /**
     * True if the video is finished downloading.
     *
     * @return
     */
    boolean isFinished();
}
