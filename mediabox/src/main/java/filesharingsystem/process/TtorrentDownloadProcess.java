package filesharingsystem.process;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turn.ttorrent.client.SharedTorrent;

import util.storage.StorageService;


public class TtorrentDownloadProcess implements DownloadProcess {
    private static final Logger log = LoggerFactory.getLogger(DownloadProcess.class);
    private final File torrent;
    private final StorageService torrentStorage;
    public TtorrentDownloadProcess(File torrent, StorageService torrentStorage) {
	this.torrent = torrent;
	this.torrentStorage = torrentStorage;
    }
    
    @Override
    public DownloadProcess.Client download(Consumer<List<File>> fileHook) {
	try {
	    com.turn.ttorrent.client.Client client = new com.turn.ttorrent.client.Client(
		// This is the interface the client will listen on (you might need something
		// else than localhost here).
		InetAddress.getLocalHost(),		
		SharedTorrent.fromFile(torrent, torrentStorage.getBaseDir()));
	    // client.setMaxDownloadRate(50.0);
	    // client.setMaxUploadRate(50.0);
	    client.download();

	    return new Client() {
		
		@Override
		public void waitForDownload() {
		    client.waitForCompletion();
		}
		
		@Override
		public List<File> files() {
		    return client.getTorrent().getFilenames().stream().map(fn -> {
			return torrentStorage.get(fn);
		    }).collect(Collectors.toList());
		}

	    };
	} catch (NoSuchAlgorithmException | IOException e) {
	    log.error("Error downloading torrent.", e);
	}
	return null;
    }
    
}
