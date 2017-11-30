package filesharingsystem;

import java.io.File;


public interface UploadProcess {
    
    /**
     * Uploads a list of files into the given directory.
     *
     * @param dirname
     * @param files
     */
    void upload(File parent, File... files);

    
    /**
     * Uploads the single torrent file.
     *
     * @param f
     */
    void upload(File f);
}
