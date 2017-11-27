package filesharingsystem;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Module;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.runtime.BtClient;
import bt.runtime.Config;

public class DownloadingProcess {	
	private static Logger log;
    private static Config config;
	private static Module DHT;
	private static Storage storage;
	private static BtClient client;
	private static Path targetDirectory;
	
	// default constructor
	public DownloadingProcess() {
		// logger system
		log = LoggerFactory.getLogger(DownloadingProcess.class);
		
	    // enable multi-threaded verification of torrent data
	    config = new Config() {
			public int getNumOfHashingThreads() {
				return Runtime.getRuntime().availableProcessors() * 2;
			}
		};
		
		// enable bootstrapping from public routers
		DHT = new DHTModule(new DHTConfig() { 
			public boolean shouldUseRouterBootstrap() {
				return true;
			}
		});
		
		targetDirectory = new File("~/Downloads").toPath();    // get download directory
		storage = new FileSystemStorage(targetDirectory); // create file system based back-end for torrent data
		client = null; // initialize object, just for safety
	}
	
	// 1 argument constructor: String downloadDirectory path
	public DownloadingProcess(String downloadDirectory) {
		// logger system
		log = LoggerFactory.getLogger(DownloadingProcess.class);
		
	    // enable multi-threaded verification of torrent data
	    config = new Config() {
			public int getNumOfHashingThreads() {
				return Runtime.getRuntime().availableProcessors() * 2;
			}
		};
		
		// enable bootstrapping from public routers
		DHT = new DHTModule(new DHTConfig() { 
			public boolean shouldUseRouterBootstrap() {
				return true;
			}
		});
		
		targetDirectory = new File(downloadDirectory).toPath();    // get download directory
		storage = new FileSystemStorage(targetDirectory); // create file system based back-end for torrent data
		client = null; // initialize object, just for safety
	}
	
	// create client with a private runtime
	public BtClient download(File torrentFile) {
		try {
			client = Bt.client()
			    .config(config)
			    .storage(storage)
			    .torrent(torrentFile.toURI().toURL())
			    .autoLoadModules()
			    .module(DHT)
			    .stopWhenDownloaded()
			    .build();
		} catch (MalformedURLException e) {
			log.error("Malformed URL not supported.");
			e.printStackTrace();
			return null;
		}

		// launch
		client.startAsync().join();
		return client;
	}
}
