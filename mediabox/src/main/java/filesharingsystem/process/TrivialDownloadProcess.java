package filesharingsystem.process;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;



/**
 * Uses the DownloadProcess interface to just
 * return the file given to the constructor.
 *
 * @author
 */
public class TrivialDownloadProcess implements DownloadProcess {
    private final File file;
    public TrivialDownloadProcess(File file) {
	this.file = file;
    }

    @Override
    public DownloadProcess.Client download(Consumer<List<File>> fileHook) {
	return new Client() {
	    @Override
	    public void waitForDownload() {
		// Done, so do nothing.
	    }
	    
	    @Override
	    public List<File> files() {
		return Arrays.asList(file);
	    }
	    
	};
    }
    
}
