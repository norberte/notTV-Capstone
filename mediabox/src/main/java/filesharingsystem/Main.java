package filesharingsystem;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.common.Torrent;

public class Main {
    public static void main(String args[])
	throws URISyntaxException, UnknownHostException, NoSuchAlgorithmException, IOException {
	if(args[0].equals("download")) {
	    DownloadProcess dp = new TtorrentDownloadProcess(new File(args[1]));
	    DownloadProcess.Client c = dp.download();
	    c.waitForDownload(); // wait for download to complete.
	    for(File f : c.files()) { // do something with files.
		System.out.println(f);
	    }
	} else if(args[0].equals("upload")) {
	    UploadProcess up = new TtorrentUploadProcess(new URI(
		String.format("http://{}/announce", args.length > 2 ? args[2] : "levimiller.ca")
	    ));
	    up.upload(new File(args[1]));
	} else if (args[0].equals("seed")) {
	    Client client = new Client(
		InetAddress.getLocalHost(),
		new SharedTorrent(
		    Torrent.load(new File(args[1]), true),
		    new File(System.getProperty("user.home"), "downloads"),
		    true
		)
	    );
	    // seed
	    client.share();
	}
    }
}
