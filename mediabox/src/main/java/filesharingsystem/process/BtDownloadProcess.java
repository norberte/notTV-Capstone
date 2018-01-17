package filesharingsystem.process;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import com.google.inject.Module;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.dht.DHTConfig;
import bt.dht.DHTModule;
import bt.metainfo.TorrentFile;
import bt.runtime.BtClient;
import bt.runtime.Config;


public class BtDownloadProcess implements DownloadProcess{   
    private String magnetURI;
    public BtDownloadProcess(String magnetURI) {
	this.magnetURI = magnetURI;
    }

    /**
     * {@inheritDoc}
     *
     * @see DownloadProcess#download(String,Consumer<List<File>>)
     */
    public Client download(Consumer<List<File>> fileHook) {
	// enable multi-threaded verification of torrent data
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

	    // // Temporary code (I know, I'm a hypocrite).
	    // // home.levimiller.ca goes to the raspberry Pis.
	    // @Override
	    // public Collection<InetPeerAddress> getBootstrapNodes() {
	    // 	return Collections.singletonList(new InetPeerAddress("home.levimiller.ca", 6891));
	    // }
	});

	// get download directory
	Path targetDirectory = new File(System.getProperty("user.home")).toPath();
				
	// create file system based backend for torrent data
	Storage storage = new FileSystemStorage(targetDirectory);

	// create client with a private runtime
	List<File> files = new ArrayList<>(); 
	BtClient client = Bt.client()
	    .config(config)
	    .storage(storage)
	    .magnet(magnetURI)
	    .autoLoadModules()
	    .module(dhtModule)
	    .stopWhenDownloaded()
	    .afterTorrentFetched(t -> {
		// Collect files.
		for(TorrentFile tf : t.getFiles()) {
		    // append TorentFile's relative path to targetDirectory.
		    List<String> relPath = tf.getPathElements();
		    files.add(Paths.get(
			targetDirectory.toAbsolutePath().toString(),
			relPath.toArray(new String[relPath.size()])
		    ).toFile());
		}
		fileHook.accept(files);
	    })
	    .build();

	// launch
	CompletableFuture<?> proc = client.startAsync();
	return new Client() {
	    @Override
	    public void waitForDownload() {
		proc.join();
	    }
	    
	    @Override
	    public List<File> files() {
		return files;
	    }
	};
    }
    

    public static void main(String args[]) {
	new BtDownloadProcess(args[0]).download();
    }
}
