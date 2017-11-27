package filesharingsystem;

import java.io.File;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import bt.runtime.BtClient;

public class TestUploadingProcess {
	private File singleFile = new File(this.getClass().getResource("/cat.txt").getFile());
	
	private File dir = new File(this.getClass().getResource("/directory").getFile()); 
	private String dirName = dir.getName();
	private File[] listOfFiles = dir.listFiles();

	@Test
	public void testSingleFileUpload() {
		UploadingProcess up = new UploadingProcess();
		BtClient client = up.upload(singleFile);
		
		Assert.assertTrue(client.isStarted());
		client.stop();
	}
	
	@Test
	public void testMultipleFileUpload() {
		UploadingProcess up = new UploadingProcess();
		BtClient client = up.upload(Arrays.asList(listOfFiles), dirName);
		
		Assert.assertTrue(client.isStarted());
		client.stop();
	}
	
	@Test
	public void testIfSingleUploadingProcessFailed() {
		UploadingProcess up = new UploadingProcess();
		BtClient client = up.upload(singleFile);
		
		Assert.assertTrue(client != null);
		client.stop();
	}
	
	@Test
	public void testIfMultipleUploadingProcessFailed() {
		UploadingProcess up = new UploadingProcess();
		BtClient client = up.upload(Arrays.asList(listOfFiles), dirName);
		
		Assert.assertTrue(client != null);
		client.stop();
	}
}