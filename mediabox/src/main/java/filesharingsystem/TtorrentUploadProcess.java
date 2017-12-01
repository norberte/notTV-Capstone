package filesharingsystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turn.ttorrent.common.Torrent;

public class TtorrentUploadProcess implements UploadProcess {
    private static final Logger log = LoggerFactory.getLogger(TtorrentUploadProcess.class);
    private final URI announce;
    public TtorrentUploadProcess(URI announce) {
	this.announce = announce;
    }
    
    @Override
    public void upload(File parent, File... files) {
	try {
	    Torrent t = Torrent.create(parent, Arrays.asList(files), announce, "notTV");
	    t.save(new FileOutputStream("toaster.torrent"));
	    // CloseableHttpClient client = HttpClients.createDefault();
	    // File file = new File(textFileName);
	    // HttpPost post = new HttpPost("http://echo.200please.com");
	    // FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
	    // StringBody stringBody1 = new StringBody("Message 1", ContentType.MULTIPART_FORM_DATA);
	    // StringBody stringBody2 = new StringBody("Message 2", ContentType.MULTIPART_FORM_DATA);
	    // //
	    // MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	    // builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
	    // builder.addPart("upfile", fileBody);
	    // builder.addPart("text1", stringBody1);
	    // builder.addPart("text2", stringBody2);
	    // HttpEntity entity = builder.build();
	    // //
	    // post.setEntity(entity);
	    // HttpResponse response = client.execute(post);
	    // client.close();
	} catch (NoSuchAlgorithmException | InterruptedException | IOException e) {
	    log.error("Error creating Torrent file.", e);
	}
    }
    
    @Override
    public void upload(File f) {
	this.upload(new File(""), f);
    }
}
