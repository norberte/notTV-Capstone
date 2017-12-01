package filesharingsystem;

import java.io.File;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.apache.http.HttpEntity;
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
     * @param parent
     * @param files
     */
    @Override
    public void upload(String name, File parent, File... files) throws UploadException{
	try {
	    // Create torrent from announce/files.
	    Torrent t = Torrent.create(parent, Arrays.asList(files), announce, "notTV");

	    // send file to the server.
	    PipedOutputStream filePipe = new PipedOutputStream(); // avoids writing it to a file.
	    t.save(filePipe);

	    // Create request
	    CloseableHttpClient httpClient = HttpClients.createDefault();
	    HttpPost uploadFile = new HttpPost(this.uploadURI);
	    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	    // This attaches the file to the POST:
	    builder.addBinaryBody(
		"file",
		new PipedInputStream(filePipe),
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
