package tracker;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Main {
    public static void main(String args[]) {
	try {
	    NotTVTracker track = new NotTVTracker(new File(args[0]));
	    track.start();
	} catch (NoSuchAlgorithmException | IOException e) {
       	    e.printStackTrace();
	}
    }
}
