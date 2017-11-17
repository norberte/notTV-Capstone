package filesharingsystem;

import java.net.InetAddress;
import com.google.inject.Binder;
import com.google.inject.Module;

import bt.Bt;
import bt.BtClientBuilder;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.dht.MldhtService;
import bt.net.InetPeer;
import bt.net.Peer;
import bt.runtime.BtClient;
import bt.runtime.BtRuntimeBuilder;
import bt.runtime.Config;
import bt.service.RuntimeLifecycleBinder;
import lbms.plugins.mldht.DHTConfiguration;
import bt.magnet.MagnetUri;
import bt.magnet.MagnetUri.Builder;
import bt.metainfo.TorrentId;
import filesharingsystem.TorrentAssembler;

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
		
		/*
		 * not sure about these runtime and service objects
		 * 
		    // try to come up with something that runs the process
			RuntimeLifecycleBinder dhtBinder = new RuntimeLifecycleBinder();
			DHT.configure((Binder) dhtBinder); 
			
			// create a service object, which could help operate the torrenting process ???
			MldhtService service = new MldhtService(dhtBinder, config, new DHTConfig());
			
			// add yourself as peer
			service.addNode((Peer) new InetPeer(config.getAcceptorAddress(), config.getAcceptorPort();));
		 * 
		 */
		
		
		TorrentId tid = null;
		// get Torrent ID after the torrent assembler finished its job
        Builder mlBuider = new MagnetUri.Builder(tid);
        MagnetUri newMagnet = mlBuider.buildUri();
        
        // not sure if needed
        BtRuntimeBuilder torrentProcess = new BtRuntimeBuilder(config);
        torrentProcess.disableAutomaticShutdown().autoLoadModules().build().startup();
        
        // create client with a private runtime
     	BtClient client = Bt.client().config(config).magnet(newMagnet).autoLoadModules()
     		        .module(DHT).build();

     	// launch
     	client.startAsync();
	}

}
