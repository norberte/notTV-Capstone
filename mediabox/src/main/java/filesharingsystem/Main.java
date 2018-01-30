package filesharingsystem;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.io.FilenameUtils;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;
import com.turn.ttorrent.common.Torrent;

import filesharingsystem.process.DownloadProcess;
import filesharingsystem.process.TtorrentDownloadProcess;
import filesharingsystem.process.TtorrentUploadProcess;
import filesharingsystem.process.UploadException;
import filesharingsystem.process.UploadProcess;

import util.storage.StorageProperties;
import util.storage.FileSystemStorageService;


public class Main {
    public static void main(String args[])
	throws URISyntaxException, UnknownHostException, NoSuchAlgorithmException, IOException, UploadException {
	if(args[0].equals("download")) {
	    DownloadProcess dp = new TtorrentDownloadProcess(
		new File(args[1]),
		new FileSystemStorageService(new StorageProperties("torrents"))
	    );
	    DownloadProcess.Client c = dp.download();
	    c.waitForDownload(); // wait for download to complete.
	    for(File f : c.files()) { // do something with files.
		System.out.println(f);
	    }
	} else if(args[0].equals("upload")) {
	    UploadProcess up = new TtorrentUploadProcess(
		new FileSystemStorageService(new StorageProperties("torrents")),
		new FileSystemStorageService(new StorageProperties("uploads")),
		new URI(args.length > 2 ? args[2] : "http://levimiller.ca:6969/announce"),
		new URI(args.length > 3 ? args[3] : "http://notTV.levimiller.ca/upload-torrent"),
		FilenameUtils.getBaseName(args[1]),
		new File(args[1])
	    );
	    up.run();
	} else if (args[0].equals("seed")) {
	    Client client = new Client(
		InetAddress.getLocalHost(),
		new SharedTorrent(
		    Torrent.load(new File(args[1]), true),
		    new File(System.getProperty("user.home"), "uploads"),
		    true
		)
	    );
	    // seed
	    client.share();
	}
    }
}
