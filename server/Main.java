public class Main {
    public static void main(String args[]) {
	Config config = new Config() {
	    @Override
	    public int getAcceptorAddress() {
		return 0;
	    }

	    @Override
	    public int getAcceptorPort() {
		return 0;
	    }

	    @Override
	    public int getNumberOfPeersToRequestFromTracker() {
		return 0;
	    }
	};


	NotTVTracker track = new NotTVTracker(config, "");
    }
}
