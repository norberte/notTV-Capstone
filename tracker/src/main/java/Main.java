import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String args[]) {
	try {
	    NotTVTracker track = new NotTVTracker(new File(
		args.length > 0 ? args[0] : "torrents"
	    ));
	    track.start();
	} catch (NoSuchAlgorithmException | IOException e) {
       	    e.printStackTrace();
	}
    }
}
