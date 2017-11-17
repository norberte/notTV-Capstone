package filesharing;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.Mac;

import bt.metainfo.Torrent;
import bt.bencoding.model.BEObject;


/**
 *
 *
 * @author
 */
class TorrentAssembler {
    /**
     * Creates a Torrent object from the collection of files.
     * 
     * @param files
     * @return
    */
    public static Torrent makeTorrent(Collection<File> files) {
	if(files.size() < 1)
	    throw new IllegalArgumentException("Must supply at least one file."));
	Map<String, BEObject<?>> torrent = new HashMap<>();

	// The key could be set to a known good node such as one operated by the person generating the torrent.
	List<BEList<BEObject<?>>> nodes = new ArrayList<>();

        // Get info dict for file(s).
        Map<String, BEObject<?>> info = files.size() > 1 ? multiFileInfoDict(files) : singleFileInfoDict(files.iterator().next());

        // number of bytes per piece. This is commonly 28 KiB = 256 KiB = 262,144 B.
        info.put("piece length", new BEInteger(null, BigInteger.valueOf(262144)))

	// a hash list, i.e., a concatenation of each piece's SHA-1 hash. As SHA-1 returns a 160-bit hash,
        // pieces will be a string whose length is a multiple of 160-bits. If the torrent contains multiple files,
        // the pieces are formed by concatenating the files in the order they appear in the files dictionary
        // (i.e. all pieces in the torrent are the full piece length except for the last piece, which may be shorter).
	info.put("pieces", getHash(files));
    }
    
    /**
     * Creates a Torrent object from a single file.
     *
     * @param file
     * @return
     */
    public static Torrent makeTorrent(File file) {

    }

    private BEString getHash(Collection<File> files) {
    }

    private static Map<String, BEObject<?>> singleFileInfoDict(File file) {
	Map<String, BEObject<?>> info = new HashMap<>();
	info.put("name", file.getName());
	// size of the file in bytes (only when one file is being shared)
	info.put("length", new BEInteger(null, BigInteger.valueOf(file.length()));
    }

    private static Map<String, BEObject<?>> multiFileInfoDict(Collection<File> files) {
	// Create description of the torrent content. 
	Map<String, BEObject<?>> info = new HashMap<>();
	info.put("files", files.stream().map((File f) -> {
	    Map<String, BEObject<?>> h = new HashMap<>();
	    h.put("length", new BEInteger(null, BigInteger.valueOf(f.length())));
	    h.put("path", new BEString(f.getAbsolutePath().getBytes()));
	    return h;
	}).collect(Collectors.toList()));
    }
}
