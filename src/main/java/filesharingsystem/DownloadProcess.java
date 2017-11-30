package filesharingsystem;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

public class DownloadProcess {
	private final String magnetURI;
	private final Consumer<List<File>> fileHook;

	public DownloadProcess(String magnetURI) {
		this(magnetURI, f -> {
		});
	}

	/**
	 * 
	 *
	 * @param magnetURI
	 * @param fileHook
	 */
	public DownloadProcess(String magnetURI, Consumer<List<File>> fileHook) {
		this.magnetURI = magnetURI;
		this.fileHook = fileHook;
	}

	// overloaded method
	public BtClient download(String downloadDirectory) {
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
		});

		// get download directory
		Path targetDirectory = new File(downloadDirectory).toPath();

		// create file system based backend for torrent data
		Storage storage = new FileSystemStorage(targetDirectory);

		// create client with a private runtime
		BtClient client = Bt.client().config(config).storage(storage).magnet(this.magnetURI).autoLoadModules()
				.module(dhtModule).stopWhenDownloaded().afterTorrentFetched(t -> {
					List<File> files = new ArrayList<>();
					// Collect files.
					for (TorrentFile tf : t.getFiles()) {
						// append TorentFile's relative path to targetDirectory.
						List<String> relPath = tf.getPathElements();
						files.add(Paths.get(targetDirectory.toAbsolutePath().toString(),
								relPath.toArray(new String[relPath.size()])).toFile());
					}
					System.out.println(files);
					this.fileHook.accept(files);
				}).build();

		// launch
		client.startAsync();
		return client;
	}

	public BtClient download() {
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
		});

		// get download directory
		Path targetDirectory = new File(System.getProperty("user.home")).toPath();

		// create file system based backend for torrent data
		Storage storage = new FileSystemStorage(targetDirectory);

		// create client with a private runtime
		BtClient client = Bt.client().config(config).storage(storage).magnet(this.magnetURI).autoLoadModules()
				.module(dhtModule).stopWhenDownloaded().afterTorrentFetched(t -> {
					List<File> files = new ArrayList<>();
					// Collect files.
					for (TorrentFile tf : t.getFiles()) {
						// append TorentFile's relative path to targetDirectory.
						List<String> relPath = tf.getPathElements();
						files.add(Paths.get(targetDirectory.toAbsolutePath().toString(),
								relPath.toArray(new String[relPath.size()])).toFile());
					}
					System.out.println(files);
					this.fileHook.accept(files);
				}).build();

		// launch
		client.startAsync();
		return client;
	}

	public static void main(String args[]) {
		new DownloadProcess(args[0]);
	}
}