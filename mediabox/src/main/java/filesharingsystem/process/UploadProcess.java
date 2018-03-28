package filesharingsystem.process;

import java.io.File;

public interface UploadProcess extends Runnable {

    /**
     * Returns the id of the process
     *
     * @return
     */
    String getName();

    /**
     * Returns the Torrent File
     * 
     * @return
     */
    File getTorrent();
}
