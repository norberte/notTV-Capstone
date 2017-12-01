package filesharingsystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.common.Torrent;

public class TtorrentUploadProcess implements UploadProcess {
    private static final Logger log = LoggerFactory.getLogger(TtorrentUploadProcess.class);
    private final URI announce, uploadURI;
    private Client client;
    public TtorrentUploadProcess(URI announce, URI uploadURI) {
	this.announce = announce;
	this.uploadURI = uploadURI;
	client = null;
    }
    
    /**
     * @param name - name of the torrent. Can't be a path. 
     * @param parent
     * @param files
     */
    @Override
    public void upload(String name, File parent, File... files) throws UploadException {
	File tempFile = null;
	try {
	    tempFile = File.createTempFile(name, ".tmp");
	    // Create torrent from announce/files.
	    Torrent t = Torrent.create(parent, Arrays.asList(files), announce, "notTV");
	    t.save(new FileOutputStream(tempFile));
	    // send file to the server.
	    // PipedOutputStream filePipe = new PipedOutputStream(); // avoids writing it to a file.
	    
	    // Create request
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    HttpPost uploadFile = new HttpPost(this.uploadURI);
	    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	    // This attaches the file to the POST:
	    builder.addBinaryBody(
		"file",
		tempFile, 
		ContentType.APPLICATION_OCTET_STREAM,
		name
	    );
	    
	    uploadFile.setEntity(builder.build());
	    CloseableHttpResponse response = httpClient.execute(uploadFile);
	    int code = response.getStatusLine().getStatusCode();
	    if(code == 200) {
		System.out.println("Success!");
		// start seeding.
		// client = new Client(
		// 	InetAddress.getLocalHost(),
		// 	new SharedTorrent(t, parent, true)
		// );
		// client.share();
	    } else {
		throw new UploadException("Unable to upload torrent to server. Got status code: " + code);
	    }
	} catch (NoSuchAlgorithmException | InterruptedException | IOException e) {
	    log.error("Error creating Torrent file.", e);
	} finally {
	    if(tempFile != null)
		tempFile.delete();
	}
    }
    
    @Override
    public void upload(String name, File f) throws UploadException {
	this.upload(name, new File(""), f);
    }

    @Override
    public void stop() {
	if(client != null) {
	    client.stop();
	    client = null;
	}
    }
}
