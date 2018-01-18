package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import filesharingsystem.process.TtorrentResumeUploadProcess;
import filesharingsystem.process.UploadProcess;


/**
 * Manages the seeding threads, including the startup and shutdown.
 * Doesn't use a threadpool, because the seeding process is long lived.
 * @author
 */
public class SeedManager {
    private static class Pair {
	private Thread t;
	private UploadProcess up;
	Pair(Thread t, UploadProcess up) {
	    this.t = t;
	    this.up = up;
	}
    }
    
    private static final Logger log = LoggerFactory.getLogger(SeedManager.class);
    private static final SeedManager INSTANCE;
    private final Map<String, Pair> currentSeeds;
    private final File memory;

    static {
	INSTANCE = new SeedManager();
	// Runtime.getRuntime().addShutdownHook(new Thread() {
	//     keepRunning = false;
	    
	// });
    }
    
    private SeedManager() {
	this.currentSeeds = new HashMap<>();
	this.memory = Paths.get(System.getProperty("user.home"), "seeds.csv").toFile();

	// Start seeding all saved upload seeds.
	try(Scanner scan = new Scanner(this.memory)) {
	    scan.useDelimiter(",");
	    while(scan.hasNext()) {
		// resume seeding
		String[] vals = scan.next().trim().split(","); // [name, torrentPath]
		UploadProcess up = new TtorrentResumeUploadProcess(vals[0], new File(vals[1]));
		Thread t = new Thread(up);
		t.start();
		INSTANCE.currentSeeds.put(vals[0], new Pair(t, up));
	    }
	} catch (FileNotFoundException e) {
	    log.error("Error reading seeds.", e);
	}
    }

    /**
     * Starts the upload/seeding process in a new thread
     *
     * @param name
     * @param up
     */
    public static void addProcess(UploadProcess up) {
	INSTANCE.currentSeeds.put(up.getName(), new Pair(new Thread(up), up));
    }

    /**
     * Stops the seeding/uploading process given by name
     *
     * @param name
     */
    public static void stopProcess(String name) {
	log.info("Stopping process {}...", name);
	if(INSTANCE.currentSeeds.containsKey(name))
	    INSTANCE.currentSeeds.remove(name).t.interrupt();
    }

    /**
     * Persists the current seeds so they can be started up again.
     *
     */
    public static void saveSeeds() {
	// Very simple persistence.
	try(FileWriter writer = new FileWriter(INSTANCE.memory)){
	    // Write name,torrentPath on each line.
	    for(Pair p : INSTANCE.currentSeeds.values())
		writer.append(p.up.getName())
		    .append(',')
		    .append(p.up.getTorrent().getAbsolutePath())
		    .append('\n');
	    writer.flush();
	} catch (IOException e) {
	    log.error("Error persisting seeds.", e);
	} 
    }
}
