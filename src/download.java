
public class download {

	public static void main(String[] args) {
		Storage storage = new FileSystemStorage(/* target directory */);

		BtClient client = Bt.client().storage(storage).torrent(/* torrent source */).build();

		client.startAsync().join();

		Storage storage = new FileSystemStorage(/* target directory */);

		BtRuntime sharedRuntime = BtRuntime.defaultRuntime();

		URL url1 = /* torrent file URL #1 */,
		    url2 = /* torrent file URL #2 */;

		BtClient client1 = Bt.client(sharedRuntime).storage(storage).torrent(/* torrent source #1 */).build();
		BtClient client2 = Bt.client(sharedRuntime).storage(storage).torrent(/* torrent source #2 */).build();

		// wait until both clients have finished
		CompletableFuture.allOf(client1.startAsync(), client2.startAsync()).join();
	}
}
