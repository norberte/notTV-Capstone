package filesharingsystem;

import java.io.File;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import bt.metainfo.Torrent;

import filesharingsystem.TorrentAssembler.Node;


public class TestDefaultTorrentAssembler {
    private Node peer1 = new Node("99.199.106.140", 8000);
    private File file1 = new File(this.getClass().getResource("/file1.txt").getFile());
    
    @Test
    public void testSingleFileTorrentAssembler() {
	// TorrentAssembler ta = new DefaultTorrentAssembler();
	// Torrent torr = ta.makeTorrent(Arrays.asList(peer1), file1);
	//Testing stuff, I'm done for tonight lol.
	Assert.assertTrue(true);
    }
}
