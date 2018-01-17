package filesharingsystem;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import filesharingsystem.process.DownloadProcess;
import filesharingsystem.process.DownloadProcess.Client;
import filesharingsystem.process.TtorrentDownloadProcess;
import filesharingsystem.process.TtorrentUploadProcess;
import filesharingsystem.process.UploadProcess;
import filesharingsystem.process.UploadProcess.UploadException;

public class TestDownloadingProcess {
    private File contentFile, torrFile;
    private UploadProcess up;
    
    @Before
    public void setup() {
	torrFile = new File(System.getProperty("user.home"), "cat.torrent");  
	contentFile = new File(System.getProperty("user.home"), "cat.txt");
	try(PrintWriter out = new PrintWriter( contentFile )){
	    out.println("  )\\._.,--....,'``.");
	    out.println(" /,   _.. \\   _\\  (`._ ,.");
	    out.println("`._.-(,_..'--(,_..'`-.;.'");
	    out.flush();

	    //Test if a .torrent file can be created during the upload process
	    up = new TtorrentUploadProcess(
		new URI("http://levimiller.ca:6969/announce"),
		new URI("http://notTV.levimiller.ca/upload-torrent")
	    );
	    up.upload("cat", contentFile);
	} catch (FileNotFoundException | URISyntaxException | UploadException e) {
	    e.printStackTrace();
	}
    }

    @After
    public void tearDown() {
	up.stop();
	torrFile.delete();
    }

    @Test
    public void testDownload() {
	// Test if the download process can download video content using the file sharing system
	DownloadProcess dp = new TtorrentDownloadProcess(torrFile);
	Client c = dp.download();
	c.waitForDownload();
	assertTrue(new File(System.getProperty("user.home"), "cat.txt").exists());
    }
}
