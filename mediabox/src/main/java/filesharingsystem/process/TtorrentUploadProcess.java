package filesharingsystem.process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

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
    @Qualifier("VideoStorage")
    private StorageService videoStorage;
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
	try(Scanner s = new Scanner(new URL(config.getServerUrl() + "/info/public-ip").openStream(), "UTF-8")) {
	    String ip = s.next();
	    log.info(ip);
	    torrentFile = torrentStorage.get(String.format("%s.torrent", this.name));
	    // Create torrent from announce/files.
	    Torrent t = Torrent.create(this.file, new URI(config.getTrackerUrl() + "/announce"), "notTV");

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
        //Begin timing seed for overall bandwidth usage during seed
        long startTime = System.currentTimeMillis();
        // start seeding.
            Pair clientPair = WANClient.newWANClient(
		    InetAddress.getLocalHost(),
		    InetAddress.getByName(ip),
		    new SharedTorrent(t, videoStorage.getBaseDir(), true)
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
	    if(client != null) {
		client.stop();
        //End recording of seed up-time
        long endTime = System.currentTimeMillis();

        //Calculate time spent dseeding in seconds
        double seedTime = (endTime - startTime)/1000;

        //Calculate Bandwidth usage using File Size and time spent seeding.
        //Old Test File Size = 26,415,093 bytes
        long fileSize = 23725150; //panasonic vid is 26415093; //It would be better to get the size of the file programatically.
        //1000 bits per second = 125 Bytes per second
        double bytesPerSecond = fileSize/seedTime;
        double bandwidthUsage = 1000*(bytesPerSecond)/125;

        //Output to File or log with bandwidth usage of download
        PrintWriter out = new PrintWriter(new FileWriter("~/latestUploadData.txt"));
        out.print("Upload Speed Data\n\nBytes Per Second: "+bytesPerSecond+"\nBandwidth Usage:"+bandwidthUsage);
        out.close();

		client = null;
	    }
	}
    }
}
