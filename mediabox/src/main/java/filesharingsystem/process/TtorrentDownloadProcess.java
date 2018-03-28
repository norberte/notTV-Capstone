package filesharingsystem.process;

import java.io.*;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;

import springbackend.Config;

import util.storage.StorageService;


public class TtorrentDownloadProcess implements DownloadProcess {
    private static final Logger log = LoggerFactory.getLogger(DownloadProcess.class);
    private final File torrent;
    @Qualifier("VideoStorage")
    @Autowired
    private  StorageService videoStorage;
    private boolean finished;

    @Autowired
    Config config;
    public TtorrentDownloadProcess(File torrent) {
	this.torrent = torrent;
        finished = false;
    }

    @Override
    public Optional<File> download() {
	try {
            SharedTorrent st = SharedTorrent.fromFile(torrent, videoStorage.getBaseDir());
            // If the client isn't finished downloading, finish downloading.
            log.info("Download complete: {}", st.isComplete());
            log.info("{}", st.isInitialized());
            if(!st.isComplete()) {
                Client client = new com.turn.ttorrent.client.Client(
                    // This is the interface the client will listen on (you might need something
                    // else than localhost here).
                    InetAddress.getLocalHost(),
                    st
                );

                client.setMaxDownloadRate(config.bandwidth);
                client.setMaxUploadRate(config.bandwidth);
                //DOWNLOAD SOME JUNK
                client.download();
                client.waitForCompletion();
                //DONE DOWNLOADING
                finished = true;
            }
            // run callback.
            List<String> names = st.getFilenames();
            if(names.size() > 0) // callback if there is a file.
                return Optional.of(videoStorage.get(names.get(0)));
	} catch (NoSuchAlgorithmException | IOException e) {
	    log.error("Error downloading torrent.", e);
	}
        return Optional.empty();
    }

    public boolean isFinished() {
        return finished;
    }
}
