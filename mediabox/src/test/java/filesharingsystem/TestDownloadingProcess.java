package filesharingsystem;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import bt.runtime.BtClient;

public class TestDownloadingProcess {
		private File torrentFile = new File(this.getClass().getResource("/957580847.torrent").getFile());
		private File downloadDirectory = new File(this.getClass().getResource("/directory").getFile()); 

		@Test
		public void testIfDownloadProcessEnded() {
			DownloadingProcess down = new DownloadingProcess(downloadDirectory.getPath());
			BtClient client = down.download(torrentFile);
			
			Assert.assertFalse(client.isStarted()); // check if client stopped running, because it ended the downloading process
		}
		
		@Test
		public void testIfNewFileGotDownloaded() {
			File[] beforeFiles = downloadDirectory.listFiles();
			
			DownloadingProcess down = new DownloadingProcess(downloadDirectory.getPath());
			BtClient client = down.download(torrentFile);
			
			File[] afterFiles = downloadDirectory.listFiles();
			
			Assert.assertNotEquals(beforeFiles.length, afterFiles.length);
		}
		
}

