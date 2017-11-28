package filesharingsystem;

import java.io.File;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;


public class TestDefaultTorrentAssembler {
    private final File file1 = new File(System.getProperty("user.home"), "cat.txt");
    
    @Test
    public void testSingleFileTorrentAssembler() {
	TorrentAssembler ta = new DefaultTorrentAssembler();
	File torr = ta.makeTorrent(Arrays.asList(), file1);
	
	//Testing stuff, I'm done for tonight lol.
	Assert.assertTrue(true);
    }
}
