package filesharingsystem.process;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.client.strategy.RequestStrategy;

import filesharingsystem.PortMapper;
import filesharingsystem.process.WANClient.ClientInitializationException;
import filesharingsystem.process.WANClient.Pair;

import springbackend.Config;

import util.storage.StorageService;

public class TtorrentDownloadProcess implements DownloadProcess {
    private static final Logger log = LoggerFactory.getLogger(DownloadProcess.class);
    private final File torrent;
    private final RequestStrategy strategy;
    private final boolean wait;
    
    @Qualifier("VideoStorage")
    @Autowired
    private StorageService videoStorage;

    @Autowired
    private Config config;
    @Autowired
    private PortMapper portMapper;

    public TtorrentDownloadProcess(File torrent) {
        this(torrent, false, null);
    }

    /**
     * Starts the download process using the given RequestStrategy (sequential or rarest-first).
     *
     * @param torrent
     * @param requestStrategy
     */
    public TtorrentDownloadProcess(File torrent, boolean wait, RequestStrategy requestStrategy) {
	this.torrent = torrent;
        this.strategy = requestStrategy;
        this.wait = wait;
    }

    @Override
    public Optional<File> download() {
        try {
            // load the torrent with the appripriate strategy (sequential vs rarest-first).
            SharedTorrent st;
            if(this.strategy == null) // fromFile() defaults to rarest-first
                st = SharedTorrent.fromFile(torrent, videoStorage.getBaseDir());
            else {
                byte[] data = FileUtils.readFileToByteArray(torrent);
                st = new SharedTorrent(data, videoStorage.getBaseDir(), false, strategy);
            }
            
            // If the client isn't finished downloading, finish downloading.
            log.info("Download complete: {}", st.isComplete());
            log.info("{}", st.isInitialized());
            if (!st.isComplete()) {
                Pair clientPair = WANClient.newWANClient(InetAddress.getLocalHost(), config.getPublicIp(), st);
                // client.setMaxDownloadRate(50.0);
                // client.setMaxUploadRate(50.0);

                // add port for peers to respond to.
                portMapper.add(clientPair.address.getPort());
                clientPair.client.download();

                clientPair.client.download();
                if(this.wait)
                    clientPair.client.waitForCompletion();
            }
            // run callback.
            List<String> names = st.getFilenames();
            if (names.size() > 0) // callback if there is a file.
                return Optional.of(videoStorage.get(names.get(0)));
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error("Error downloading torrent.", e);
        } catch (ClientInitializationException e) {
            log.error("Error creating WAN client,", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean isFinished() {
        throw new UnsupportedOperationException("Not implemented yet.");
    }
}
