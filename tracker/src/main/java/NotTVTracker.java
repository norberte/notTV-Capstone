import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.turn.ttorrent.common.Torrent;
import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

    
class NotTVTracker {
    private static Logger log = LoggerFactory.getLogger(NotTVTracker.class);
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
	if(!torrentDir.isFile())
	    throw new IllegalArgumentException("torrentDir cannot be a file.");
	if(!torrentDir.exists())
	    if(!torrentDir.mkdirs())
		throw new IllegalArgumentException("Unable to make directory.");    

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
	    tracker.announce(TrackedTorrent.load(f));
	}

	// watch torrentDir for changes.
	Executors.newSingleThreadExecutor().execute(
	    newWatchTask(torrentDir.toPath())
	);
    }
    

    /**
     * Creates a new Runnable to watch the given path for new files/deleted files.
     * Modification isn't supported yet because I am lazy.
     *
     * @param p
     * @return
     */
    private Runnable newWatchTask(Path p) {
	return new Runnable() {
	    @Override
	    public void run() {
		try (WatchService service = FileSystems.getDefault().newWatchService()) {
		    // register what changes to detect.
		    p.register(
			service,
			StandardWatchEventKinds.ENTRY_CREATE,
			StandardWatchEventKinds.ENTRY_MODIFY,
			StandardWatchEventKinds.ENTRY_DELETE
		    );

		    WatchKey watchKey = null;
		    while (true) {
			watchKey = service.take();
			if(watchKey != null) {
			    watchKey.pollEvents().stream().forEach(event -> {
				Kind<?> kind = event.kind(); // No other way except if statements :/

				// New file, try to announce it.
				if(StandardWatchEventKinds.ENTRY_CREATE == kind) {
				    try {
					Path p = ((WatchEvent<Path>) event).context();
					tracker.announce(
					    TrackedTorrent.load(p.toFile())
					);
				    } catch (NoSuchAlgorithmException | IOException e) {
					log.error("Unable to announce new file: " + p, e);
				    }
				}

				// Deleted file, try to unannounce it.
				if(StandardWatchEventKinds.ENTRY_DELETE == kind) {
				    Path p = ((WatchEvent<Path>) event).context();
				    try {
					tracker.remove(Torrent.load(p.toFile()));
				    } catch (NoSuchAlgorithmException | IOException e) {
					log.error("Unable to remove torrent: " + p, e);
				    }
				}

				// Updated file, try to unannounce it, then announce it.
				if(StandardWatchEventKinds.ENTRY_MODIFY == kind) {
				    Path p = ((WatchEvent<Path>) event).context();
				    log.warn("Modify isn't supported yet, please don't do this. Delete, then Create. Path: " + p);
				}
			    });
			}
			watchKey.reset();
		    }
		} catch (IOException | InterruptedException e) {
		    log.error("Error watching torrentDir.", e);
		}
	    }
	};
    }

    public void start() {
	tracker.start();
    }

    public void stop() {
	tracker.stop();
    }
}
