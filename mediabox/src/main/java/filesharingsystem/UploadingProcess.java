package filesharingsystem;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;

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


public class UploadingProcess {
	private static Logger log;
    private Config config;
	private Module DHT;
	private Storage storage;
	
	public UploadingProcess() {
		// logger system
		log = LoggerFactory.getLogger(UploadingProcess.class);
		
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
		
		// create file system based backend for torrent data
		storage = new FileSystemStorage(new File(System.getProperty("user.home")).toPath());
	}
    
	
	
	public BtClient upload(File fileToBeUploaded) {
		// create .torrent file using notTV's own tracker link
		TorrentAssembler ta = new DefaultTorrentAssembler();
		File torr = ta.makeTorrent(fileToBeUploaded);

		// create client with a private runtime
		BtClient client = null;
		try {
			client = Bt.client()
					.config(config)
					.storage(storage)
					.torrent(torr.toURI().toURL())
					.autoLoadModules()
					.module(DHT)
					.build();
			// launch
			client.startAsync();
		} catch (MalformedURLException e) {
			log.error("Malformed URL not supported.");
			e.printStackTrace();
			return null;
		}
		return client;
	}
	
	public BtClient upload(List<File> files, String dirname){
		// create .torrent file using notTV's own tracker link
		TorrentAssembler ta = new DefaultTorrentAssembler();
		File torr = ta.makeTorrent(files, dirname);

		// create client with a private runtime
		BtClient client = null;
		try {
			client = Bt.client()
					.config(config)
					.storage(storage)
					.torrent(torr.toURI().toURL())
					.autoLoadModules()
					.module(DHT)
					.build();
			// launch
			client.startAsync();
		} catch (MalformedURLException e) {
			log.error("Malformed URL not supported.");
			e.printStackTrace();
			return null;
		}
		return client;
	}
}
