import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

    
/**
 *
 *
 * @author
 */
class NotTVTracker {
    private static Logger log = LoggerFactory.getLogger(NotTVTracker.class);
    private Map<String, Torrent> announceMap;
    private Tracker tracker;
    
    /**
     * Creates a tracker for notTV clients.
     * Config should have the following properties:
     * getAcceptorAddress(), 
     * getAcceptorPort(),
     * getNumberOfPeersToRequestFromTracker()
     *
     * @param config
     * @param trackerUrl
     * @throws IOException
     * @throws NoSuchAlgorithmException
     */
    NotTVTracker(File torrentDir) throws NoSuchAlgorithmException, IOException {
	// Sanity check - Check torrentDir is a folder.
	if(torrentDir.isFile())
	    throw new IllegalArgumentException("torrentDir cannot be a file.");
	if(!torrentDir.exists())
	    if(!torrentDir.mkdirs())
		throw new IllegalArgumentException("Unable to make directory.");    

	announceMap = new HashMap<>();
	// First, instantiate a Tracker object with the port you want it to listen on.
	// The default tracker port recommended by the BitTorrent protocol is 6969.
	tracker = new Tracker(new InetSocketAddress(6969));
	System.out.println(tracker.getAnnounceUrl());
	// Then, for each torrent you wish to announce on this tracker, simply created
	// a TrackedTorrent object and pass it to the tracker.announce() method:
	FilenameFilter filter = new FilenameFilter() {
	    @Override
	    public boolean accept(File dir, String name) {
		return name.endsWith(".torrent");
	    }
	};

	// announce all existing files.
	for (File f : torrentDir.listFiles(filter)) {
	    // save Torrent object so it can be removed later. (detects after file is deleted.)
	    announceMap.put(f.getAbsolutePath(), Torrent.load(f));
	    tracker.announce(TrackedTorrent.load(f));
	}

	// watch torrentDir for changes.
	Executors.newSingleThreadExecutor().execute(
	    newWatchTask(torrentDir.getAbsoluteFile().toPath())
	);
    }
    

    /**
     * Creates a new Runnable to watch the given path for new files/deleted files.
     * Modification isn't supported yet because I am lazy.
     *
     * @param p
     * @return
     */
    private Runnable newWatchTask(Path torrentDir) {
	return new Runnable() {
	    @SuppressWarnings("unchecked")
	    @Override
	    public void run() {
		log.info(torrentDir.toString());
		try (WatchService service = FileSystems.getDefault().newWatchService()) {
		    // register what changes to detect.
		    torrentDir.register(
			service,
			StandardWatchEventKinds.ENTRY_CREATE,
			StandardWatchEventKinds.ENTRY_MODIFY,
			StandardWatchEventKinds.ENTRY_DELETE
		    );
		    String dir = torrentDir.toAbsolutePath().toString();

		    WatchKey watchKey = null;
		    while (true) {
			watchKey = service.take();
			if(watchKey != null) {
			    watchKey.pollEvents().stream().forEach(event -> {
				Kind<?> kind = event.kind(); // No other way except if statements :/
				log.info("{}", kind);
				//No need to listen for ENTRY_CREATE because ENTRY_MODIFY is fired when copying a file.

				// Deleted file, try to unannounce it.
				if(StandardWatchEventKinds.ENTRY_DELETE == kind) {
				    delete(Paths.get(dir, ((WatchEvent<Path>) event).context().toString()));
				}

				// Updated file, try to unannounce it, then announce it.
				if(StandardWatchEventKinds.ENTRY_MODIFY == kind) {
				    Path p = Paths.get(dir, ((WatchEvent<Path>) event).context().toString());
				    delete(p);
				    add(p);
				    log.warn("Modified Path: " + p);
				}
			    });
			    log.debug(announceMap.entrySet().toString());
			}
			watchKey.reset();
		    }
		} catch (IOException | InterruptedException e) {
		    log.error("Error watching torrentDir.", e);
		}
	    }
	};
    }

    /**
     * Utility method to announce a new file.
     *
     * @param p
     */
    private void add(Path p) {
	try {
	    File f = p.toFile();
	    log.info("{} : {}",p.getParent(), f);
	    announceMap.put(f.getAbsolutePath(), Torrent.load(f));
	    tracker.announce(
		TrackedTorrent.load(f)
	    );
	    log.info("File added: {}", p);
	} catch (NoSuchAlgorithmException | IOException e) {
	    log.error("Unable to announce new file.", e);
	}
    }

    /**
     * Utility method to stop announcing a file.
     *
     * @param p
     */
    public void delete(Path p) {
	Torrent t;
	if((t = announceMap.remove(p.toString())) != null) {
	    tracker.remove(t);
	    log.info("File deleted: {}", p);
	} else {
	    log.warn("File not being announced.");
	}
    }

    public void start() {
	tracker.start();
    }

    public void stop() {
	tracker.stop();
    }
}
