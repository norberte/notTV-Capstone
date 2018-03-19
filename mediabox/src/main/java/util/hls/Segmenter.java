package util.hls;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Segmenter {
    private static final Logger log = LoggerFactory.getLogger(Segmenter.class);
    // https://www.ffmpeg.org/ffmpeg-formats.html#hls-2
    // -c:v h264: converts video to H.264
    // +cgop -g 30: GOP=Group Of Pictures, deals with compression.
    // Open - needs to look at next and previous, closed = only previous. We specify closed.
    // -g 30: Keyframe interval, the maximum distance between I-frames. high GOP lengths =  more efficient compression, but will make seeking more difficult.
    //-hls_time, // time per segment, ffmpeg does segments based on time, we do it by size.                                              
    private static final String cmd = String.format(
        "ffmpeg -i %%s -c:v h264 -flags +cgop -g 30 -hls_time %d  -hls_playlist_type vod %s -hls_segment_filename %s%%%%d%s",
        Constants.SEGMENT_TIME,
        Constants.INDEX_NAME,
        Constants.SEGMENT_NAME,
        Constants.SEGMENT_EXT
    );

    
    /**
     * Segments the given file to prepare it for hls
     *
     * @param f
     */
    public void segment(File f) {
        try {
            String s = String.format(cmd, f.getAbsolutePath());
            Process p = Runtime.getRuntime().exec(s, null, f.getParentFile());
            if(p.waitFor() != 0)
                log.warn("Unable to convert to hls with command: {}", cmd);
        } catch (IOException e) {
            log.error("Error converting file.", e);
        } catch (InterruptedException e) {
            log.error("Interrupted.", e);
        }
    }
}
