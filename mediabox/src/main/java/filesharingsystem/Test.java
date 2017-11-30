package filesharingsystem;

import java.io.File;

import filesharingsystem.DownloadProcess.Client;

public class Test {
    public static void main(String args[]) {
	DownloadProcess dp = new TtorrentDownloadProcess(new File(args[0]));
	Client c = dp.download();
	c.waitForDownload(); // wait for download to complete.
	for(File f : c.files()) { // do something with files.
	    System.out.println(f);
	}
    }
}
