package filesharingsystem.process;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.common.Torrent;

/**
 * Resumes the seeding process.
 *
 * @author
 */
public class TtorrentResumeUploadProcess implements UploadProcess {
    private static final Logger log = LoggerFactory.getLogger(TtorrentUploadProcess.class);
    private String name;
    private File torrentFile;

    public TtorrentResumeUploadProcess(String name, File torrentFile) {
        this.name = name;
        this.torrentFile = torrentFile;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public File getTorrent() {
        return torrentFile;
    }

    @Override
    public void run() {
        try {
            // init client
            Client client = new Client(InetAddress.getLocalHost(), new SharedTorrent(Torrent.load(torrentFile, true), new File(System.getProperty("user.home"), "uploads"), true));
            // seed
            client.share();
        } catch (NoSuchAlgorithmException | IOException e) {
            log.error("", e);
        }
    }
}
