package springbackend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

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
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.turn.ttorrent.client.strategy.RequestStrategy;
import com.turn.ttorrent.client.strategy.RequestStrategyImplSequential;

import filesharingsystem.process.DownloadProcess;

import util.SeedManager;
import util.storage.StorageService;

@Controller
@RequestMapping("process")
public class ProcessController {
    private static final Logger log = LoggerFactory.getLogger(ProcessController.class);
    private static final RequestStrategy SEQUENTIAL = new RequestStrategyImplSequential();
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
     * @param video
     * @return
     */
    @PostMapping("upload")
    @ResponseBody
    public String upload(int id, @NotNull MultipartFile video) {
        log.info(video.getOriginalFilename());
        String name = String.valueOf(id);
        // use video id to name the file.
        File localVideo = videoStorage.get(name); // , FilenameUtils.getExtension(video.getOriginalFilename())));
        log.info(localVideo.toString());
        try {
            video.transferTo(localVideo);
        } catch (IllegalStateException | IOException e) {
            log.error("Error saving uploaded file.", e);
        }

        try {
            // Start seeding process.
            File torrent = seedManager.addProcess(name, localVideo);
            return torrent.getName();
        } catch (URISyntaxException e) {
            log.error("Unable to start upload process. Malformed URI's in config.", e);
            return null; // hardcoded urls, so should never happen.
        }
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
     * Streams the specified video.
     * Starts a sequential download if it doesn't exist already.
     * @param videoId
     * @return
     */
    @RequestMapping(path = "video-stream")
    public StreamingResponseBody stream(@RequestParam("videoId") int videoId){
	// get torrent file.
        String torrent = String.format("%d.torrent", videoId);
        String videoName = String.valueOf(videoId);
        Optional<DownloadProcess> dp = Optional.empty();
        // only start download if we don't have the video.
        if(!videoStorage.has(videoName)) {
            File torrentFile = torrentStorage.get(torrent); // create empty torrent file.
            try {
                Request.Get(
                    String.format("%s/get/torrent/%s", this.config.getServerUrl(), torrent)
                ).execute().saveContent(torrentFile);
                
                // download file sequentially so it is in order, and can be streamed immediately.
                DownloadProcess proc = beanFactory.getBean(DownloadProcess.class, torrentFile, false, SEQUENTIAL);
                Optional<File> result = proc.download();
                dp = Optional.of(proc);
                
                // if download actually gave us a file.
                if(result.isPresent()) {
                    File f = result.get();
                    log.info(f.getName());
                    videoName = f.getName();
                }
            } catch (IOException e) {
                log.error("Error getting torrent file from server.", e);
            }
        }
        // return the stream.
        return beanFactory.getBean(StreamingResponseBody.class, videoId, dp);
    }

    /**
     * Downloads the file in the background.
     * This method is intended for the use case where the user doesn't
     * expect to watch it until it finishes
     * @param videoId
     */
    @RequestMapping(path = "download")
    public void download(@RequestParam("videoId") int videoId){
	// get torrent file.
        String torrent = String.format("%d.torrent", videoId);
        String videoName = String.valueOf(videoId);
        // only start download if we don't have the video.
        if(!videoStorage.has(videoName)) {
            File torrentFile = torrentStorage.get(torrent); // create empty torrent file.
            try {
                Request.Get(
                    String.format("%s/get/torrent/%s", this.config.getServerUrl(), torrent)
                ).execute().saveContent(torrentFile);
                
                // download file.
                Optional<File> result = beanFactory.getBean(DownloadProcess.class, torrentFile).download();

                // if download actually gave us a file.
                if(result.isPresent()) {
                    File f = result.get();
                    log.info(f.getName());
                    videoName = f.getName();
                }
            } catch (IOException e) {
                log.error("Error getting torrent file from server.", e);
            }
        }
    }
}
