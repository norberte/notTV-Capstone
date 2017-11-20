package filesharingsystem;

import java.io.File;
import java.net.InetAddress;
import java.nio.file.Path;
import java.util.Arrays;

//import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Module;

import bt.Bt;
import bt.BtClientBuilder;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.dht.MldhtService;
import bt.dht.MldhtService;
import bt.net.InetPeer;
import bt.net.Peer;
import bt.runtime.BtClient;
import bt.runtime.BtRuntimeBuilder;
import bt.runtime.BtRuntimeBuilder;
import bt.runtime.Config;
import bt.service.RuntimeLifecycleBinder;
import lbms.plugins.mldht.DHTConfiguration;
import bt.magnet.MagnetUri;
import bt.magnet.MagnetUri.Builder;
import bt.metainfo.Torrent;
import bt.metainfo.TorrentId;
import filesharingsystem.TorrentAssembler;
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
		
		// try to come up with something that runs the process
		RuntimeLifecycleBinder dhtBinder = new RuntimeLifecycleBinder();
		DHT.configure((Binder) dhtBinder); 
			
		// create a service object, which could help operate the torrenting process ???
		MldhtService service = new MldhtService(dhtBinder, config, new DHTConfig());
			
		// add yourself as peer
		service.addNode((Peer) new InetPeer(config.getAcceptorAddress(), config.getAcceptorPort()));

		String fileToBeUploaded = "/home/norbert/Alan Walker - The Spectre.mp4";
		String downloadDirectory = "/home/norbert/Downloads/";
		
		// get download directory
		Path targetDirectory = new File(downloadDirectory).toPath();
						
		// create file system based backend for torrent data
		Storage storage = new FileSystemStorage(targetDirectory);

		
		Node peer1 = new Node("23.16.101.147", 8000);
	    File file1 = new File(fileToBeUploaded);
	    String torrentSavingLocation = "/home/norbert/itWorks.torrent";
	    
	   
		TorrentAssembler ta = new DefaultTorrentAssembler();
		Torrent torr = ta.makeTorrent(Arrays.asList(peer1), file1,  torrentSavingLocation);
		// get Torrent ID from torrent file
		TorrentId tid = torr.getTorrentId();
		
        Builder mlBuider = new MagnetUri.Builder(tid);
        MagnetUri newMagnet = mlBuider.buildUri();
        
        // not sure if needed
        BtRuntimeBuilder torrentProcess = new BtRuntimeBuilder(config);
        torrentProcess.disableAutomaticShutdown().autoLoadModules().build().startup();
        
        // create client with a private runtime
     	BtClient client = Bt.client().config(config).storage(storage).magnet(newMagnet).autoLoadModules()
     		        .module(DHT).build();

     	// launch
     	//client.startAsync();
	}
	
	public static MagnetUri createNewMagnetLink(){
		String fileToBeUploaded = "/home/norbert/Alan Walker - The Spectre.mp4";

		Node peer1 = new Node("23.16.101.147", 8000);
	    File file1 = new File(fileToBeUploaded);
	   
		TorrentAssembler ta = new DefaultTorrentAssembler();
		Torrent torr = ta.makeTorrent(Arrays.asList(peer1), file1, "/home/norbert/itWorks.torrent");
		// get Torrent ID from torrent file
		TorrentId tid = torr.getTorrentId();
		
        Builder mlBuider = new MagnetUri.Builder(tid);
        MagnetUri newMagnet = mlBuider.buildUri();
		return newMagnet;
	}

}
