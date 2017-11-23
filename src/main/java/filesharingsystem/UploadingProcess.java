package filesharingsystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.magnet.MagnetUri;
import bt.magnet.MagnetUri.Builder;
import bt.metainfo.MetadataService;
import bt.metainfo.Torrent;
import bt.metainfo.TorrentId;
import bt.net.InetPeerAddress;
import bt.runtime.BtClient;
import bt.runtime.Config;
import filesharingsystem.TorrentAssembler.Node;

public class UploadingProcess {

	private static Logger log = LoggerFactory.getLogger(UploadingProcess.class);
	private static String fileToBeUploaded = "/home/norbert/Alan Walker - The Spectre.mp4";
	private static String downloadDirectory = "/home/norbert/Downloads/";
	private static String IP = "23.16.101.147"; // enter your ID
	private static int port = 8080;
	
	public static void main(String[] args) {
		uploadingProcess();
	}
	
	public static void uploadingProcess() {
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
		
		// get download directory
		Path targetDirectory = new File(downloadDirectory).toPath();
								
		// create file system based backend for torrent data
		Storage storage = new FileSystemStorage(targetDirectory);
		
		MagnetUri newMagnetURI = createMagnetURI(fileToBeUploaded, IP, port);
		
		
		
		String badMagnetLink = "magnet:?" + newMagnetURI.getTorrentId() + "&" + newMagnetURI.getDisplayName() +
				"&" + newMagnetURI.getTrackerUrls() + "&" + newMagnetURI.getPeerAddresses();
		System.out.println(badMagnetLink);	// check this out to see the problem
		
		
		String magnetLink = "magnet:?" + newMagnetURI.getTorrentId();
		System.out.println(magnetLink);

		
		 // create client with a private runtime
     	BtClient client = Bt.client().config(config).storage(storage).magnet(newMagnetURI).autoLoadModules()
     		        .module(DHT).build();

     	// launch
     	client.startAsync();

     	//DownloadingProcess.downloadFiles(newMagnetURI, downloadDirectory);
	}
	
	
	public static MagnetUri createMagnetURI(String fileToBeUploaded, String IP, int port) {
		Node peer1 = new Node(IP, port);
	    File file1 = new File(fileToBeUploaded);
	   
		TorrentAssembler ta = new DefaultTorrentAssembler(); 
		File torr = ta.makeTorrent(Arrays.asList(peer1), file1);
		
		byte[] torrentByteArray;
		TorrentId tid = null;
		try {
			torrentByteArray = Files.readAllBytes(torr.toPath()); 	// make byte array from .torrent file
			Torrent t = new MetadataService().fromByteArray(torrentByteArray);   //  make Torrent object from byte array
			tid = t.getTorrentId();  		// get Torrent ID from Torrent object 
		} catch (IOException e) {
			log.error("Error reading the torrent file into a Byte array. IOException occured.", e);
			e.printStackTrace();
		}
		
		InetPeerAddress myself = new InetPeerAddress(IP, port);
		
        Builder mlBuider = new MagnetUri.Builder(tid).peer(myself);
        MagnetUri newMagnet = mlBuider.buildUri();
        
     	return newMagnet;
	}
}
