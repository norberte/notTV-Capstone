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
     * Represents a single node in the bittorrent network.
     * E.g., the seeders or peers of the torrent.
     */
    static class Node {
	private String host;
	private int port;

	public Node(String host, int port) {
	    this.host = host;
	    this.port = port;
	}

	public String getHost() {
	    return host;
	}

	public int getPort() {
	    return port;
	}
    }

    /**
     * Creates a Torrent object from the collection of files.
     *
     * @param nodes
     *            - nodes used to join the network. Should be set to the K closest
     *            nodes in the torrent generating client's routing table.<br>
     *            Alternatively, the key could be set to a known good node such as
     *            one operated by the person generating the torrent.
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
    File makeTorrent(Collection<Node> nodes, List<File> files, String dirname);

    /**
     * Creates a Torrent object from a single file.
     *
     * @param nodes 
     *            - nodes used to join the network. <br>
     *            Should be set to the K closest nodes in the torrent generating 
     *            client's routing table.<br> Alternatively, the key could be set 
     *            to a known good node such as one operated by the person generating the torrent.   
     * @param file - the file downloaded by the torrent.
     * @param savedTorrentFile
     *            - saves the Torrent file to this path 
     * @return
     */
     File makeTorrent(Collection<Node> nodes, File file);
}
