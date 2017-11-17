package filesharing;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.runtime.BtClient;
import bt.runtime.Config;
import com.google.inject.Module;

import java.io.File;
import java.nio.file.Path;


public class Downloading {
	public static void main(String[] args) {
		// enable multithreaded verification of torrent data
		Config config = new Config() {
		    @Override
		    public int getNumOfHashingThreads() {
		        return Runtime.getRuntime().availableProcessors() * 2;
		    }
		};

		// enable bootstrapping from public routers
		Module dhtModule = new DHTModule(new DHTConfig() {
		    @Override
		    public boolean shouldUseRouterBootstrap() {
		        return true;
		    }
		});

		// get download directory
		Path targetDirectory = new File("/home/norbert/Downloads").toPath();

		// magnet link
		String magnetUri = "magnet:?xt=urn:btih:c2e359c772ad61c7094dd0a73d6ea90ef67eb466&dn=Alan+Walker+-+The+Spectre.mp4";
		
		// create file system based backend for torrent data
		Storage storage = new FileSystemStorage(targetDirectory);

		// create client with a private runtime
		BtClient client = Bt.client()
		        .config(config)
		        .storage(storage)
		        .magnet(magnetUri)
		        .autoLoadModules()
		        .module(dhtModule)
		        .stopWhenDownloaded()
		        .build();

		// launch
		client.startAsync(state -> {
			if(state.getPiecesRemaining() == 0) {
				client.stop();
			}
		}, 1000).join();
	}
}
