package springbackend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.turn.ttorrent.client.strategy.RequestStrategy;
import com.turn.ttorrent.client.strategy.RequestStrategyImplSequential;

import filesharingsystem.process.DownloadProcess;

import util.SeedManager;
import util.storage.StorageService;

/**
 * Handles requests realated to the upload or download process. (including thumbnail).
 *
 * @author
 */
@Controller
@RequestMapping("process")
public class ProcessController {
    private static final Logger log = LoggerFactory.getLogger(ProcessController.class);
    private static final RequestStrategy SEQUENTIAL = new RequestStrategyImplSequential();
    private static String VIDEO_NAME = "whole-video";
    /*
     * DI attributes.
     */
    @Autowired
    private Config config;
    @Autowired
    @Qualifier("TorrentStorage")
    private StorageService torrentStorage;
    @Autowired
    @Qualifier("VideoStorage")
    private StorageService videoStorage;
    @Autowired
    @Qualifier("ImageStorage")
    private StorageService thumbnailStorage;

    @Autowired
    private SeedManager seedManager;
    @Autowired
    private BeanFactory beanFactory;

    /**
     * Uploads the given video to the network. I.e, creates a .torrent, uploads the .torrent to the server, and starts seeding.
     *
     * @param id
     * @param video
     * @return
     */
    @PostMapping("upload")
    @ResponseBody
    public String upload(int id, @NotNull MultipartFile video) {
        log.info(video.getOriginalFilename());
        String name = String.valueOf(id);
        // use video id to name the file.
	File videoDir = videoStorage.get(name);
        videoDir.mkdir();
	log.info(videoDir.toString());
	try {
            // write mimetype
            Files.write(
                Paths.get(videoDir.getAbsolutePath(), "metadata.txt"),
                Arrays.asList("video/"+FilenameUtils.getExtension(video.getOriginalFilename())),
                Charset.forName("UTF-8")
            );

            // copy uploaded video.
            File videoFile = new File(videoDir, VIDEO_NAME);
	    video.transferTo(videoFile);

            try {
                // Start seeding process.
                File torrent = seedManager.addProcess(name, videoFile);
                return torrent.getName();
            } catch (URISyntaxException e) {
                log.error("Unable to start upload process. Malformed URI's in config.", e);
            }
	} catch (IllegalStateException | IOException e) {
	    log.error("Error saving uploaded file.", e);
	}

        return null;
    }

    /**
     * Gets the thumbnail for a particular video.
     *
     * @param videoId: id of video
     * @return
     */
    @GetMapping("get-thumbnail/{id}")
    @ResponseBody
    public ResponseEntity<FileSystemResource> getThumbnail(@PathVariable int id, HttpServletResponse response) {
        String thumbnailName = String.valueOf(id);
        File thumbnail = thumbnailStorage.get(thumbnailName);
        log.debug("{}", thumbnail);
        // if it exists, just return it
        if (thumbnailStorage.has(thumbnailName)) // Maybe implement an ImageStorage class that uses a StorageService and can accept id as an int.
            return ResponseEntity.ok().body(new FileSystemResource(thumbnail));

        // else download then return it.
        try {
            Response r = Request.Get(String.format("%s/get/thumbnail/%d", config.getServerUrl(), id)).execute();
            HttpResponse httpResponse = r.returnResponse();
            log.debug("{} - {}", id, httpResponse.getStatusLine().getStatusCode());
            // return placeholder if not successful.
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                return ResponseEntity.notFound().build();
            // save and return.
            try (FileOutputStream out = new FileOutputStream(thumbnail); InputStream is = httpResponse.getEntity().getContent();) {
                int inByte;
                while ((inByte = is.read()) != -1)
                    out.write(inByte);
            }
            response.setContentType("application/pdf");
            return ResponseEntity.ok().body(new FileSystemResource(thumbnail));
        } catch (IOException e) {
            log.error("Error getting thumbnail file: " + thumbnailName + " from server.", e);
            return ResponseEntity.notFound().build(); // tell client not found.
        }
    }

    /**
     * Starts the download of the given video with the given strategy (null for default strategy).
     *
     * @param videoId
     * @param strategy
     * @return
     */
    private Optional<DownloadProcess> download(int videoId, RequestStrategy strategy) {
	// get torrent file.
        String torrent = String.format("%d.torrent", videoId);
        String videoName = String.valueOf(videoId);
        Optional<DownloadProcess> dp = Optional.empty();
        // only start download if we don't have the video. Files should be stored in a folder given by the videoId.
        if(!videoStorage.has(videoName)) { 
            File torrentFile = torrentStorage.get(torrent); // create empty torrent file.
            try {
                // download .torrent from server.
                Request.Get(
                    String.format("%s/get/torrent/%s", this.config.getServerUrl(), torrent)
                ).execute().saveContent(torrentFile);

                File baseDir = videoStorage.get(videoName); // create folder for download.
                baseDir.mkdir();
                // download file according to the given strategy (null = default).
                DownloadProcess proc = beanFactory.getBean(DownloadProcess.class, torrentFile, baseDir, false, strategy);
                Optional<File> result = proc.download();
                dp = Optional.of(proc);
                
                // if download actually gave us a file.
                if(result.isPresent()) {
                    File f = result.get();
                    log.info(f.getName());
                }
            } catch (IOException e) {
                log.error("Error getting torrent file from server.", e);
            }
        }
        return dp;
    }
    
    /**
     * Streams the specified video.
     * Starts a sequential download if it doesn't exist already.
     * @param videoId
     * @return
     */
    @RequestMapping(path = "video-stream")
    public void stream(@RequestParam("videoId") int videoId){
        // start sequential download
        download(videoId, SEQUENTIAL);

        // start process for creating hls.
    }

    /**
     * Downloads the file in the background.
     * This method is intended for the use case where the user doesn't
     * expect to watch it until it finishes
     * @param videoId
     */
    @RequestMapping(path = "download")
    public void download(@RequestParam("videoId") int videoId){
        download(videoId);
        // TODO: convert video to hls VOD when complete.
    }
}
