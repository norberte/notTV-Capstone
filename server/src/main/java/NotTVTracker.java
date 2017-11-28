
import com.google.inject.Guice;
import com.google.inject.Injector;

import bt.module.ServiceModule;
import bt.runtime.Config;
import bt.tracker.Tracker;
import bt.tracker.udp.UdpTrackerFactory;


class NotTVTracker {
    Tracker tracker;

    /**
     * Creates a tracker for notTV clients.
     * Config should have the following properties:
     * getAcceptorAddress(), 
     * getAcceptorPort(),
     * getNumberOfPeersToRequestFromTracker()
     *
     * @param config
     * @param trackerUrl
     */
    NotTVTracker(Config config, String trackerUrl) {
	Injector i = Guice.createInjector(new ServiceModule(config));
	tracker = i.getInstance(UdpTrackerFactory.class).getTracker(trackerUrl);
    }
}
