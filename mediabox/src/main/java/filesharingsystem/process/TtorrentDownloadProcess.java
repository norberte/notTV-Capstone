package filesharingsystem.process;

import java.io.*;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.turn.ttorrent.client.Client;
import com.turn.ttorrent.client.SharedTorrent;

import util.storage.StorageService;


public class TtorrentDownloadProcess implements DownloadProcess {
    private static final Logger log = LoggerFactory.getLogger(DownloadProcess.class);
    private final File torrent;
    @Qualifier("VideoStorage")
    @Autowired
    private  StorageService videoStorage;
    public TtorrentDownloadProcess(File torrent) {
	this.torrent = torrent;
    }

    @Override
    public Optional<File> download() {
	try {
            SharedTorrent st = SharedTorrent.fromFile(torrent, videoStorage.getBaseDir());
            // If the client isn't finished downloading, finish downloading.
            log.info("Download complete: {}", st.isComplete());
            log.info("{}", st.isInitialized());
            if(!st.isComplete()) {
                Client client = new com.turn.ttorrent.client.Client(
                    // This is the interface the client will listen on (you might need something
                    // else than localhost here).
                    InetAddress.getLocalHost(),
                    st
                );

                client.setMaxDownloadRate(50.0);
                client.setMaxUploadRate(50.0);

                //Start recording time to download time to download here
                long startTime = System.currentTimeMillis();

                //DOWNLOAD SOME JUNK
                client.download();
                client.waitForCompletion();
                //DONE DOWNLOADING

                //End recording of download time
                long endTime = System.currentTimeMillis();

                //Calculate time spent downloading in seconds
                double downloadTime = (endTime - startTime)/1000;

                //Calculate Bandwidth usage using File Size and time to download.
                //Test File Size = 26,415,093 bytes
                long fileSize = 23725150 //panasonic vid is 26415093; //It would be better to get the size of the file programatically.
                //1000 bits per second = 125 Bytes per second
                double bytesPerSecond = fileSize/downloadTime;
                double bandwidthUsage = 1000*(bytesPerSecond)/125;

                //CPU Usage during mediabox runtime is handled by a python program

                //Output to File or log with bandwidth usage of download
                PrintWriter out = new PrintWriter(new FileWriter("~/latestDownloadData.txt"));
                out.print("Download Speed Data\n\nBytes Per Second: "+bytesPerSecond+"\nBandwidth Usage:"+bandwidthUsage+"\n\nCPU Usage: "+cpuUsage);
                out.close();
            }
            // run callback.
            List<String> names = st.getFilenames();
            if(names.size() > 0) // callback if there is a file.
                return Optional.of(videoStorage.get(names.get(0)));
	} catch (NoSuchAlgorithmException | IOException e) {
	    log.error("Error downloading torrent.", e);
	}
        return Optional.empty();
    }
}
