package filesharingsystem;

import java.io.File;
import java.util.Collection;
import java.util.List;

import bt.metainfo.Torrent;

/**
 * TorrentAssemblers are responsible for creating Torrent objects from a given collection of files.
 *
 * @author Levi Miller
 * @author
 * @version
 */
interface TorrentAssembler {
    /**
     * Creates a Torrent object from the collection of files.
     *
     * @param files
     *            - files in the torrent. 
     *            List instead of Collection because files needs to be ordered, 
     *            since the files need to be hashed in the order they appear in the info Dict.
     * @param dirname
     *            - the suggested directory name if there are multiple files
     *            (ignored if only one file).
     * @param savedTorrentFile
     *            - saves the Torrent file to this path 
     * @return
     */
    File makeTorrent(List<File> files, String dirname);

    /**
     * Creates a Torrent object from a single file.
     *
     * @param file - the file downloaded by the torrent.
     * @param savedTorrentFile
     *            - saves the Torrent file to this path 
     * @return
     */
     File makeTorrent(File file);
}
