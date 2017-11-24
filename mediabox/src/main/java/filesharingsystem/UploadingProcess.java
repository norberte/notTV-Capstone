package filesharingsystem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Scanner;

import com.google.inject.Module;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.magnet.MagnetUri;
import bt.magnet.MagnetUri.Builder;
import bt.metainfo.Torrent;
import bt.runtime.BtClient;
import bt.runtime.Config;


public class UploadingProcess {
    public static void main(String[] args) {
	Config config = new Config() {
	    public int getNumOfHashingThreads() {
		return Runtime.getRuntime().availableProcessors() * 2;
	    }
	};

	// enable bootstrapping from public routers
	Module DHT = new DHTModule(new DHTConfig() {
	    public boolean shouldUseRouterBootstrap() {
		return true;
	    }
	});
	
	// create file system based backend for torrent data
	Storage storage = new FileSystemStorage(new File(System.getProperty("user.home")).toPath());

	// BS temporary code to read the damn resource file.
	InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("cat.txt");
	Scanner scan = new Scanner(in);
	File file1 = new File(System.clearProperty("user.home"), "cat.txt");
	try(FileWriter out = new FileWriter(file1)) {
	    while (scan.hasNextLine())
		out.write(scan.nextLine() + "\n");
	    scan.close();
	} catch(IOException e) {
	    e.printStackTrace();
	}
	   
	TorrentAssembler ta = new DefaultTorrentAssembler();
	File torr = ta.makeTorrent(file1);
        
        // create client with a private runtime
	try {
	    BtClient client = Bt.client()
		.config(config)
		.storage(storage)
		.torrent(torr.toURI().toURL())
		.autoLoadModules()
		.module(DHT).afterTorrentFetched((Torrent t) -> {
		    // get Torrent ID from torrent file
		    Builder mlBuider = new MagnetUri.Builder(t.getTorrentId());
		    MagnetUri newMagnet = mlBuider.buildUri();
		    System.out.println("********** " + newMagnet);
		    // String badMagnetLink = "magnet:?" + newMagnet.getTorrentId() + "&" + newMagnet.getDisplayName() +
		    // "&" + newMagnet.getTrackerUrls() + "&" + newMagnet.getPeerAddresses();
		}).build();
	    // launch
	    client.startAsync();
	} catch (MalformedURLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}
