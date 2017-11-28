package filesharingsystem;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;

import com.google.inject.Module;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.metainfo.Torrent;
import bt.runtime.BtClient;
import bt.runtime.Config;

import filesharingsystem.TorrentAssembler.Node;


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
	File file1 = new File(System.getProperty("user.home"), "cat.txt");
	   
	TorrentAssembler ta = new DefaultTorrentAssembler();
	File torr = ta.makeTorrent(Arrays.asList(new Node(args[0], 6891)), file1);
        
        // create client with a private runtime
	
	try {
	    BtClient client = Bt.client()
		.config(config)
		.storage(storage)
		.torrent(torr.toURI().toURL())
		.autoLoadModules()
		.module(DHT).afterTorrentFetched((Torrent t) -> {
		    // get Torrent ID from torrent file (for magnet link)
		    // TODO: send this to the server.
		    System.out.println(t.getTorrentId());
		}).build();
	    // launch
	    client.startAsync();
	} catch (MalformedURLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
}

