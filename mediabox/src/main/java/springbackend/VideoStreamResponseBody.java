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
        BufferedReader br = new BufferedReader(new FileReader(videoStorage.get(String.valueOf(id))));
        try {
            do {
                while(true) {
                    int b = br.read();
                    log.info("FILE BYTE: {}", b);
                    if(b != -1)
                        out.write(b);
                    else // end of file, wait for more to be written.
                        Thread.sleep(INTERVAL);
                }
            } while((dp.isPresent() && !dp.get().isFinished()));
        }catch(InterruptedException e) {
            log.error("Thread interrupted: ", e);
        }
    }
}
