package filesharingsystem;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;

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
	private Config config;
	private Module DHT;
	private Storage storage;
	private String magnetLink;

	public UploadProcess() {
		DHT = new DHTModule(new DHTConfig() {
			public boolean shouldUseRouterBootstrap() {
				return true;
			}
		});

		// enable bootstrapping from public routers
		config = new Config() {
			public int getNumOfHashingThreads() {
				return Runtime.getRuntime().availableProcessors() * 2;
			}
		};

		// create file system based backend for torrent data
		storage = new FileSystemStorage(new File(System.getProperty("user.home")).toPath());
		magnetLink = null;
	}

	public String getMagnetLink() {
		return magnetLink;
	}

	private void setMagentLink(String magnet) {
		this.magnetLink = magnet;
	}

	public BtClient startUpload(String host, File file) {
		TorrentAssembler ta = new DefaultTorrentAssembler();
		File torr = ta.makeTorrent(Arrays.asList(new Node(host, 6891)), file);

		// create client with a private runtime
		BtClient client = null;
		try {
			client = Bt.client().config(config).storage(storage).torrent(torr.toURI().toURL()).autoLoadModules()
					.module(DHT).afterTorrentFetched((Torrent t) -> {
						// get Torrent ID from torrent file (for magnet link)
						setMagentLink("magnet:?xt=urn:btih:" + t.getTorrentId().toString());
						System.out.println("magnet:?xt=urn:btih:" + t.getTorrentId().toString());
						// TODO: send this to the server.
					}).build();
			// launch
			client.startAsync();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return client;
	}

	public static void main(String[] args) {
		File file1 = new File(System.getProperty("user.home"), "cat.txt");

		UploadProcess up = new UploadProcess();
		BtClient cli = up.startUpload(args[0], file1);
	}
}
