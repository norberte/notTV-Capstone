package filesharingsystem;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

import bt.Bt;
import bt.data.Storage;
import bt.data.file.FileSystemStorage;
import bt.runtime.BtClient;
import bt.runtime.BtRuntime;

public class download {

	public static void main(String[] args) {
		// get download directory
		Path targetDirectory = new File("/home/norbert/Downloads").toPath();
		Storage storage = new FileSystemStorage(targetDirectory);

		// BtClient client = Bt.client().storage(storage).torrent(/* torrent source */).build();

		// client.startAsync().join();

		// BtRuntime sharedRuntime = BtRuntime.defaultRuntime();

		// URL url1 = /* torrent file URL #1 */;

		// BtClient client1 = Bt.client(sharedRuntime).storage(storage).torrent(/* torrent source #1 */).build();

		// wait until both clients have finished
		// CompletableFuture.allOf(client1.startAsync(), client2.startAsync()).join();
	}
}
