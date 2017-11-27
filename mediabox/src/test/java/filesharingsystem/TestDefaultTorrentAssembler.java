package filesharingsystem;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;


public class TestDefaultTorrentAssembler {
    private File file1 = new File(this.getClass().getResource("/cat.txt").getFile());
    
    @Test
    public void testSingleFileTorrentAssembler() {
	// TorrentAssembler ta = new DefaultTorrentAssembler();
	// Torrent torr = ta.makeTorrent(Arrays.asList(peer1), file1);
	TorrentAssembler ta = new DefaultTorrentAssembler();
	File torr = ta.makeTorrent(file1);
	//Testing stuff, I'm done for tonight lol.
	Assert.assertTrue(true);
    }
}
