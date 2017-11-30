package filesharingsystem;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import bt.runtime.BtClient;

public class TestUploadingProcess {
    // private File singleFile = new File(this.getClass().getResource("/cat.txt").getFile());

    // private File dir = new File(this.getClass().getResource("/directory").getFile());
    // private String dirName = dir.getName();
    // private File[] listOfFiles = dir.listFiles();

    // @Test
    // public void testSingleFileUpload() {
    //     UploadProcess up = new UploadProcess();
    //     BtClient client = up.upload(singleFile);

    //     Assert.assertTrue(client.isStarted());
    //     client.stop();
    // }

    // @Test
    // public void testMultipleFileUpload() {
    //     UploadProcess up = new UploadProcess();
    //     BtClient client = up.upload(dirName, listOfFiles);

    //     Assert.assertTrue(client.isStarted());
    //     client.stop();
    // }

    // @Test
    // public void testIfSingleUploadingProcessFailed() {
    //     UploadProcess up = new UploadProcess();
    //     BtClient client = up.upload(singleFile);

    //     Assert.assertTrue(client != null);
    //     client.stop();
    // }

    // @Test
    // public void testIfMultipleUploadingProcessFailed() {
    //     UploadProcess up = new UploadProcess();
    //     BtClient client = up.upload(dirName, listOfFiles);

    //     Assert.assertTrue(client != null);
    //     client.stop();
    // }
}
