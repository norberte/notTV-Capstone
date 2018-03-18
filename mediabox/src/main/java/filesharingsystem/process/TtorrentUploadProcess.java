package filesharingsystem.process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.common.Torrent;

import filesharingsystem.PortMapper;
import filesharingsystem.process.WANClient.ClientInitializationException;
import filesharingsystem.process.WANClient.Pair;

import springbackend.Config;

import util.storage.StorageService;

public class TtorrentUploadProcess implements UploadProcess {
    private static final Logger log = LoggerFactory.getLogger(TtorrentUploadProcess.class);
    private Client client;
    private String name;
    private File file, torrentFile;
    @Autowired
    @Qualifier("TorrentStorage")
    private StorageService torrentStorage;
    @Autowired
    private Config config;
    @Autowired
    private PortMapper portMapper;

    /**
     * Creates a new UploadProcess
     *
     * @param announce - http://tracker.url:port/announce
     * @param uploadURI - http://server.url:port/uploat-torrent
     * @param name - name of the torrent
     * @param file - video file.
     */
    public TtorrentUploadProcess(String name, File file) {
        this.name = FilenameUtils.getBaseName(name);
        this.file = file;
        client = null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public File getTorrent() {
        return torrentFile;
    }

    /**
     * @param name - name of the torrent. Can't be a path.
     * @param parent
     * @param files
     */
    @Override
    public void run() {
        // Get public ip.
        try {
            torrentFile = torrentStorage.get(String.format("%s.torrent", this.name));
            // Create torrent from announce/files.
            // http://tracker.url:port/announce
            URI trackerURI = new URI(String.format("%s:%d/%s", config.getTrackerUrl(), config.getTrackerPort(), "announce"));
            Torrent t = Torrent.create(this.file, trackerURI, "notTV");

            t.save(new FileOutputStream(torrentFile));
            // send file to the server.
            // PipedOutputStream filePipe = new PipedOutputStream(); // avoids writing it to a file.

	    // Create request
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    HttpPost uploadFile = new HttpPost(new URI(config.getServerUrl() + "/upload/torrent"));
	    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	    // This attaches the file to the POST:
	    builder.addBinaryBody(
		"file",
		torrentFile, 
		ContentType.APPLICATION_OCTET_STREAM,
		torrentFile.getName()
	    );
	    
	    uploadFile.setEntity(builder.build());
	    CloseableHttpResponse response = httpClient.execute(uploadFile);
	    int code = response.getStatusLine().getStatusCode();
	    if(code == 200) {
		log.info("Successfully uploaded torrent to the server, seeding...");
		// start seeding.
                Pair clientPair = WANClient.newWANClient(
		    InetAddress.getLocalHost(),
		    config.getPublicIp(),
		    new SharedTorrent(t, this.file.getParentFile(), true)
		);
                
                // forward ports:
                portMapper.add(clientPair.address.getPort());
                client = clientPair.client;
                // Should block
                client.share();
            } else {
                throw new UploadException("Unable to upload torrent to server. Got status code: " + code);
            }
        } catch (NoSuchAlgorithmException | IOException | URISyntaxException e) {
            log.error("Error creating Torrent file.", e);
        } catch (ClientInitializationException e) {
            log.error("Error creating the client, couldn't start upload process.", e);
        } catch (InterruptedException e) {
            // shutdown
            log.info("Stopping seeding of {}...", this.name);
            if (client != null) {
                client.stop();
                client = null;
            }
        }
    }
}
