package filesharingsystem;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


public class UploadProcess {
    private static final Logger log = LoggerFactory.getLogger(UploadProcess.class);
    private Config config;
    private String host;
    private int port;
    private Module DHT;
    private Storage storage;

    public UploadProcess() {
	config = new Config() {
	    public int getNumOfHashingThreads() {
		return Runtime.getRuntime().availableProcessors() * 2;
	    }
	};

	this.host = config.getAcceptorAddress().getHostAddress();
	this.port = config.getAcceptorPort();
	log.info("{}:{}", host, port);
	// enable bootstrapping from public routers
	DHT = new DHTModule(new DHTConfig() {
	    public boolean shouldUseRouterBootstrap() {
		return true;
	    }
	});
	
	// create file system based backend for torrent data
	storage = new FileSystemStorage(new File(System.getProperty("user.home")).toPath());
    }

    public UploadProcess(String host, int port) {
	this();
	this.host = host;
	this.port = port;
    }

    public BtClient upload(String dirname, File... files) {
	TorrentAssembler ta = new DefaultTorrentAssembler();
	return uploadHelper(ta.makeTorrent(
	    Arrays.asList(new Node(host, port)),
	    Arrays.asList(files),
	    dirname
	));
    }
    
    public BtClient upload(File f) {
	TorrentAssembler ta = new DefaultTorrentAssembler();
	return uploadHelper(ta.makeTorrent(Arrays.asList(new Node(host, port)), f));
    }

    private BtClient uploadHelper(File torr) {
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
	    return client;
	} catch (MalformedURLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return null;
	}
    }

    public static void main(String args[]) {
	UploadProcess up;
	String host = args.length > 0 ? args[0] : null;
	int port = args.length > 1 ?
	    Integer.parseInt(args[1]) :
	    6891;
	if(host != null)
	    up = new UploadProcess(host, port);
	else
	    up = new UploadProcess();
	up.upload(new File(System.getProperty("user.home"), "cat.txt"));
    }
}

