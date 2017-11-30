package filesharingsystem;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import bt.runtime.BtClient;

public class TestDownloadProcess {
	private String magnetLink = "magnet:?xt=urn:btih:e5bf947fe6820b4678a3e57e3785f38f9cd87aaf"; // cat.txt magnetlink
	private File downloadDirectory = new File(this.getClass().getResource("/downloadDirectory").getFile());

	@Test
	public void testIfDownloadProcessEnded() {
		DownloadProcess down = new DownloadProcess(magnetLink);
		BtClient client = down.download();

		Assert.assertFalse(client.isStarted()); // check if client stopped running, because it ended the downloading
												// process
	}

	@Test
	public void testIfNewFileGotDownloaded() {
		File[] beforeFiles = downloadDirectory.listFiles();

		DownloadProcess down = new DownloadProcess(magnetLink);
		BtClient client = down.download(downloadDirectory.getPath());

		File[] afterFiles = downloadDirectory.listFiles();

		Assert.assertNotEquals(beforeFiles.length, afterFiles.length);
	}
}
