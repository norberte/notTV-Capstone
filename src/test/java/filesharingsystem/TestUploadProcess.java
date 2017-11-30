package filesharingsystem;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;

import bt.runtime.BtClient;

public class TestUploadProcess {
	private File singleFile = new File(this.getClass().getResource("/cat.txt").getFile());
	private String host = "23.16.101.147"; // change IP address

	@Test
	public void testSingleFileUpload() {
		UploadProcess up = new UploadProcess();
		BtClient client = up.startUpload(host, singleFile);

		Assert.assertTrue(client.isStarted());
		client.stop();
	}

	@Test
	public void testIfSingleUploadingProcessFailed() {
		UploadProcess up = new UploadProcess();
		BtClient client = up.startUpload(host, singleFile);

		Assert.assertTrue(client != null);
		client.stop();
	}
}
