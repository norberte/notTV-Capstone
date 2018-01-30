package filesharingsystem.process;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import springbackend.Config;

import util.storage.StorageService;

public class TtorrentUploadProcessFactory implements UploadProcessFactory {
    @Autowired
    private Config config;
    @Autowired
    @Qualifier("TorrentStorage")
    private StorageService torrentStorage;
    @Autowired
    @Qualifier("VideoStorage")
    private StorageService videoStorage; 

    
    @Override
    public UploadProcess getProcess(String name, File file) throws URISyntaxException {
	
	return new TtorrentUploadProcess(
	    torrentStorage,
	    videoStorage,
	    new URI(config.trackerUrl + "/announce"),
	    new URI(config.serverUrl + "/upload-torrent"),
	    name, file
	);
    }
}
