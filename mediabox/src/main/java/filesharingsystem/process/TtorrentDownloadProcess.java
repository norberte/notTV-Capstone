package filesharingsystem.process;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.turn.ttorrent.client.SharedTorrent;

import filesharingsystem.PortMapper;
import filesharingsystem.process.WANClient.ClientInitializationException;
import filesharingsystem.process.WANClient.Pair;

import springbackend.Config;

import util.storage.StorageService;

public class TtorrentDownloadProcess implements DownloadProcess {
    private static final Logger log = LoggerFactory.getLogger(DownloadProcess.class);
    private final File torrent;
    @Qualifier("VideoStorage")
    @Autowired
    private StorageService videoStorage;

    @Autowired
    private Config config;
    @Autowired
    private PortMapper portMapper;

    public TtorrentDownloadProcess(File torrent) {
        this.torrent = torrent;
    }

    @Override
    public Optional<File> download() {
        try {
            SharedTorrent st = SharedTorrent.fromFile(torrent, videoStorage.getBaseDir());
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
}
