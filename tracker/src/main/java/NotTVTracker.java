import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.NoSuchAlgorithmException;

import com.turn.ttorrent.tracker.TrackedTorrent;
import com.turn.ttorrent.tracker.Tracker;

    
class NotTVTracker {
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

	for (File f : torrentDir.listFiles(filter)) {
	    tracker.announce(TrackedTorrent.load(f));
	}
    }

    public void start() {
	tracker.start();
    }

    public void stop() {
	tracker.stop();
    }
}
