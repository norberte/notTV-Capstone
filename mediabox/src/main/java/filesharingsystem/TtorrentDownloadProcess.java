package filesharingsystem;

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


public class TtorrentDownloadProcess implements DownloadProcess {
    private static final Logger log = LoggerFactory.getLogger(DownloadProcess.class);
    private final File torrent, downloadDir;
    public TtorrentDownloadProcess(File torrent, File downloadDir) {
	this.torrent = torrent;
	this.downloadDir = downloadDir;
    }

    public TtorrentDownloadProcess(File torrent) {
	this.torrent = torrent;
	this.downloadDir = new File(System.getProperty("user.home"));
    }
    
    @Override
    public DownloadProcess.Client download(Consumer<List<File>> fileHook) {
	try {
	    com.turn.ttorrent.client.Client client = new com.turn.ttorrent.client.Client(
		// This is the interface the client will listen on (you might need something
		// else than localhost here).
		InetAddress.getLocalHost(),		
		SharedTorrent.fromFile(torrent, downloadDir));
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
			return new File(downloadDir, fn);
		    }).collect(Collectors.toList());
		}

	    };
	} catch (NoSuchAlgorithmException | IOException e) {
	    log.error("Error downloading torrent.", e);
	}
	return null;
    }
    
}
