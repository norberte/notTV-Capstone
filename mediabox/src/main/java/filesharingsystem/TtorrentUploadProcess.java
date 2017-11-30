package filesharingsystem;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turn.ttorrent.common.Torrent;

public class TtorrentUploadProcess implements UploadProcess {
    private static final Logger log = LoggerFactory.getLogger(TtorrentUploadProcess.class);
    private final URI announce;
    public TtorrentUploadProcess(URI announce) {
	this.announce = announce;
    }
    
    @Override
    public void upload(File parent, File... files) {
	try {
	    Torrent t = Torrent.create(parent, Arrays.asList(files), announce, "notTV");
	} catch (NoSuchAlgorithmException | InterruptedException | IOException e) {
	    log.error("Error creating Torrent file.", e);
	}
    }
    
    @Override
    public void upload(File f) {
	this.upload(new File(""), f);
    }
}
