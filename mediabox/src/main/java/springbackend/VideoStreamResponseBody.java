package springbackend;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import filesharingsystem.process.DownloadProcess;

import util.storage.StorageService;


public class VideoStreamResponseBody implements StreamingResponseBody {
    private static final Logger log = LoggerFactory.getLogger(VideoStreamResponseBody.class);
    private static final int INTERVAL = 100;
    @Qualifier("VideoStorage")
    @Autowired
    private  StorageService videoStorage;
    
    private final int id;
    private final Optional<DownloadProcess> dp;
    /**
     * Initalizes a StreamingResponseBody for the video specified by videoId.
     * If there is a DownloadProcess specified, it will keep the stream open until the
     * download finishes.
     *
     * @param videoId
     * @param dp
     */
    public VideoStreamResponseBody(int videoId, Optional<DownloadProcess> dp) {
        // it's okay if the video doesn't exist yet, .get() will create a new file
        // which will be written to by the torrent process.
        this.id = videoId;
        this.dp = dp;
    }
    
    @Override
    public void writeTo(OutputStream out) throws IOException {
        File vid = videoStorage.get(String.valueOf(id));
        try(BufferedReader br = new BufferedReader(new FileReader(vid))) {
            int b;
            do {
                b = br.read();
                Thread.sleep(100); // temp for testing.
                if(b != -1) 
                    out.write(b);
                else { // end of file, wait for more to be written.
                    // out.flush(); // flush now because we read as much as possible.
                    Thread.sleep(INTERVAL);
                }
                out.flush(); // temp
                // while download not finished or not at end of File.
            } while(dp.isPresent() && !dp.get().isFinished() || b != -1);
        } catch(InterruptedException e) {
            log.error("Thread interrupted: ", e);
        }
    }
}
