package filesharingsystem.process;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
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

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.common.Torrent;

import util.storage.StorageService;

public class TtorrentUploadProcess implements UploadProcess {
    private static final Logger log = LoggerFactory.getLogger(TtorrentUploadProcess.class);
    private final URI announce, uploadURI;
    private Client client;
    private String name;
    private File file, torrentFile;
    private StorageService torrentStorage, uploadStorage;

    
    /**
     * Creates a new UploadProcess
     *
     * @param announce - http://tracker.url:port/announce
     * @param uploadURI - http://server.url:port/uploat-torrent
     * @param name - name of the torrent
     * @param file - video file.
     */
    public TtorrentUploadProcess(StorageService torrentStorage, StorageService uploadStorage, URI announce, URI uploadURI, String name, File file) {
	this.announce = announce;
	this.uploadURI = uploadURI;
	this.name = FilenameUtils.getBaseName(name);
	this.file = file;
	this.torrentStorage = torrentStorage;
	this.uploadStorage = uploadStorage;
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
	try (java.util.Scanner s = new java.util.Scanner(new java.net.URL("https://api.ipify.org").openStream(), "UTF-8").useDelimiter("\\A")) {
	    torrentFile = torrentStorage.get(String.format("%s.torrent", this.name));
	    // Create torrent from announce/files.
	    Torrent t = Torrent.create(this.file, announce, "notTV");

	    t.save(new FileOutputStream(torrentFile));
	    // send file to the server.
	    // PipedOutputStream filePipe = new PipedOutputStream(); // avoids writing it to a file.

	    String ip = s.next();
	    log.info("My current IP address is " + ip);
	    
	    // Create request
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    HttpPost uploadFile = new HttpPost(this.uploadURI);
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
		client = new Client(
		    // InetAddress.getByName(ip),
		    InetAddress.getLocalHost(),
		    new SharedTorrent(t, uploadStorage.getBaseDir(), true)
		);
		// Should block
		client.share();
	    } else {
		throw new UploadException("Unable to upload torrent to server. Got status code: " + code);
	    }
	} catch (NoSuchAlgorithmException | IOException e) {
	    log.error("Error creating Torrent file.", e);
	} catch (InterruptedException e) {
	    // shutdown
	    log.info("Stopping seeding of {}...", this.name);
	    if(client != null) {
		client.stop();
		client = null;
	    }
	}
    }
}
