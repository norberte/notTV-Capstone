package filesharing;

import java.net.InetAddress;

import com.google.inject.Binder;
import com.google.inject.Module;

import bt.Bt;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.dht.MldhtService;
import bt.net.InetPeer;
import bt.net.Peer;
import bt.runtime.BtClient;
import bt.runtime.Config;
import bt.service.IRuntimeLifecycleBinder;
import bt.service.RuntimeLifecycleBinder;
import lbms.plugins.mldht.DHTConfiguration;

public class upload {
	public static void main(String[] args) {
		DHTConfiguration dhtConfig;
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
		
		RuntimeLifecycleBinder dhtBinder = new RuntimeLifecycleBinder();
		
		DHT.configure((Binder) dhtBinder); // not sure about this
		
		MldhtService service = new MldhtService(dhtBinder, config, new DHTConfig());
		
		// add yourself as peer
		InetAddress localAddress = config.getAcceptorAddress();
		int localPort = config.getAcceptorPort();
		service.addNode((Peer) new InetPeer(localAddress, localPort));
		
		

	}

}