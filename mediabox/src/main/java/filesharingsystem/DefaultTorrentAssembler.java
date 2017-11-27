package filesharingsystem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bt.bencoding.model.BEInteger;
import bt.bencoding.model.BEList;
import bt.bencoding.model.BEMap;
import bt.bencoding.model.BEObject;
import bt.bencoding.model.BEString;

/**
 * Default implementation of TorrentAssembler.
 *
 * @author Levi Miller
 * @version
 */
class DefaultTorrentAssembler implements TorrentAssembler {
    private static Logger log = LoggerFactory.getLogger(TorrentAssembler.class);
    private static final Charset defaultCharset = Charset.forName("UTF-8");
    private static final File torrentDir = new File(System.getProperty("user.home"), "torrents");
    private final int pieceLength;
    private final String announce;
    
    DefaultTorrentAssembler() {
	pieceLength = 262144;
	// Fine for default assembler to hardcode tracker.
	// If other trackers become available in the future, just implement TorrentAssembler.
	URL ann = null;
	try {
	    ann = new URL("http", "35.160.102.229", 2357, "/announce");
	} catch (MalformedURLException e) {
	    log.error("Shouldn't ever happen, this is hardcoded.", e);
	    e.printStackTrace();
	} // http://35.160.102.229:2357/announce
	announce = ann.toString();
	if(!torrentDir.isDirectory())
	    torrentDir.mkdirs();
    }

    public File makeTorrent(List<File> files, String dirname) {
	if(files.size() < 1) 
	    throw new IllegalArgumentException("Must supply at least one file.");

	Map<String, BEObject<?>> torrent = new HashMap<>();
	// Required for mdService.
	torrent.put("announce", new BEString(announce.getBytes()));
	// Private?
	torrent.put("private", new BEInteger(null, BigInteger.valueOf(1)));
	
	// Get info dict for file(s).
	Map<String, BEObject<?>> info = files.size() > 1 ? multiFileInfoDict(files, dirname) : singleFileInfoDict(files.iterator().next());

	// number of bytes per piece. This is commonly 28 KiB = 256 KiB = 262,144 B.
	info.put("piece length", new BEInteger(null, BigInteger.valueOf(pieceLength)));

	// a hash list, i.e., a concatenation of each piece's SHA-1 hash. As SHA-1 returns a 160-bit hash,
	// pieces will be a string whose length is a multiple of 160-bits. If the torrent contains multiple files,
	// the pieces are formed by concatenating the files in the order they appear in the files dictionary
	// (i.e. all pieces in the torrent are the full piece length except for the last piece, which may be shorter).
	BEString hash = getHash(files);
	info.put("pieces", hash);

	torrent.put("info", new BEMap(null, info));
	// Write torrent to file.
	File outFile = new File(torrentDir, Math.abs(hash.hashCode()) + ".torrent");
	try(OutputStream out = new FileOutputStream(outFile)) {
		log.info(torrent.toString());
	    new BEMap(null, torrent).writeTo(out);
	} catch (IOException e) {
	    log.error("Unable to create", e);
	}

	log.info(String.format("Created torrent file: %s", torrent.toString()));
	return outFile;
    }

    public File makeTorrent(File file) {
	// Kind of hacky, but reduces code redundancy.
	return makeTorrent(Arrays.asList(file), null);
    }


    /**
     * Hashes all of the pieces of all of the files.
     *
     * @param files
     * @return
     */
    private BEString getHash(Collection<File> files) {	
	MessageDigest sha1;
	try {
	    sha1 = MessageDigest.getInstance("SHA");
	} catch (NoSuchAlgorithmException e) {
	    log.error("SHA1 not supported");
	    return new BEString("".getBytes());
	}

	try {
	    ByteArrayOutputStream hash = new ByteArrayOutputStream();
	    byte[] curr = new byte[pieceLength];
	    for(File f : files) {
		InputStream in = new FileInputStream(f);
		int byteCount = 0; // used to detect when we hace pieceLength bytes read.
		int readCount = in.read(curr, 0, pieceLength); // bytes read in a single iteration.
		while(readCount != -1) { // While not at end of file.
		    byteCount += readCount;
		    // only read until readCount, because curr may not be full.
		    sha1.update(curr, 0, readCount);
		    if(byteCount == pieceLength) { // we read pieceLength bytes, hash the piece.
			byteCount = 0; // reset.
			hash.write(sha1.digest());
		    }
		    readCount = in.read(curr, 0, pieceLength - byteCount); // keep reading.
		}
		in.close();

		// write remaining bytes if any.
		if(byteCount > 0)
		    hash.write(sha1.digest());
	    }

	    return new BEString(hash.toByteArray());
	} catch(IOException e) {
	    log.error("Error reading the files while getting the file hash.", e);
	    return new BEString("".getBytes());
	}
    }

    /**
     * Helper method to create an info dict with all the single-file specific keys.
     * Note: Doesn't add keys that are always in the info Dict, like piece size. 
     *
     * @param file
     * @return
     */
    private Map<String, BEObject<?>> singleFileInfoDict(File file) {
	Map<String, BEObject<?>> info = new HashMap<>();
	info.put("name", new BEString(file.getName().getBytes()));
	// size of the file in bytes (only when one file is being shared)
	info.put("length", new BEInteger(null, BigInteger.valueOf(file.length())));
	log.debug(String.format("Created single-file info dict: %s", info.toString()));
	return info;
    }

    /**
     * Helper method to create an info Dict with all of the multi-file specific keys.
     * Note: Doesn't add keys that are always in the info Dict, like piece size.
     *
     * @param files - Files to add.
     * @param dirname - suggested directory name.
     * @return
     */
    
    private Map<String, BEObject<?>> multiFileInfoDict(Collection<File> files, String dirname) {
	// Create description of the torrent content. 
	Map<String, BEObject<?>> info = new HashMap<>();
	// A list of dictionaries each corresponding to a file (only when multiple files are being shared). 
	info.put("files", new BEList(null, files.stream().map((File f) -> {
	    // Each dictionary has the following keys:
	    // length: size of the file in bytes.
	    // path: a list of strings corresponding to subdirectory names, the last of which is the actual file name
	    Map<String, BEObject<?>> m = new HashMap<>();
	    m.put("length", new BEInteger(null, BigInteger.valueOf(f.length())));
	    // {'path': ['src', 'test.java'], 'length': 111},
	    List<BEString> path = new ArrayList<>();
	    f.toPath().iterator().forEachRemaining((Path p) -> path.add(null));
	    m.put("path", new BEList(null, path));
	    return new BEMap(null, m);
	}).collect(Collectors.toList())));

	// suggested directory name where the files are to be saved (if multiple files)
	info.put("name", new BEString(dirname.getBytes()));
	log.debug(String.format("Created multi-file info dict: %s", info.toString()));
	return info;
    }
}
