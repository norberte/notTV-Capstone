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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import filesharingsystem.process.DownloadProcess;

import util.SeedManager;
import util.storage.StorageService;

@Controller
@RequestMapping("process")
public class ProcessController {
    private static final Logger log = LoggerFactory.getLogger(ProcessController.class);
    
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
     * Uploads the given video to the network.
     * I.e, creates a .torrent, uploads the .torrent to the server, 
     * and starts seeding.
     *
     * @param video
     * @return
     */
    @PostMapping("upload")
    @ResponseBody
    public String upload(@NotNull MultipartFile video) {
	log.info(video.getOriginalFilename());
	String name = video.getOriginalFilename();
	//TODO: use a hash or something to make a unique name
	File localVideo = videoStorage.get(name);
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
    public ResponseEntity<FileSystemResource> getThumbnail(@PathVariable int id, HttpServletResponse response){
        String thumbnailName = String.valueOf(id);
        File thumbnail = thumbnailStorage.get(thumbnailName);
        log.debug("{}", thumbnail);
        // if it exists, just return it
        if(thumbnailStorage.has(thumbnailName)) // Maybe implement an ImageStorage class that uses a StorageService and can accept id as an int.
            return ResponseEntity.ok().body(new FileSystemResource(thumbnail));

        // else download then return it.
        try {
            Response r = Request.Get(
                String.format("%s/get/thumbnail/%d", config.getServerUrl(), id)
            ).execute();
            HttpResponse httpResponse = r.returnResponse();
            log.debug("{} - {}", id, httpResponse.getStatusLine().getStatusCode());
            // return placeholder if not successful.
            if(httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
                return ResponseEntity.notFound().build();
            // save and return.
            try(
                FileOutputStream out = new FileOutputStream(thumbnail);
                InputStream is = httpResponse.getEntity().getContent();
            ) {
                int inByte;
                while((inByte = is.read()) != -1)
                    out.write(inByte);
            }
            response.setContentType("application/pdf");
            return ResponseEntity.ok().body(new FileSystemResource(thumbnail));
        } catch (IOException e) {
            log.error("Error getting thumbnail file: " + thumbnailName + " from server.", e);
            return ResponseEntity.notFound().build(); // tell client not found.
        }
    }  

    @RequestMapping(path = "download")
    public String download(@RequestParam(value="torrentName") String torrentName, @RequestParam("videoId") int videoId, RedirectAttributes redir){
	// get torrent file.
	File torrentFile = torrentStorage.get(torrentName);
	try {
	    Request.Get(
		String.format("%s/get/torrent/%s", this.config.getServerUrl(), torrentName)
	    ).execute().saveContent(torrentFile);

	    // download file.
	    Optional<File> result = beanFactory.getBean(DownloadProcess.class, torrentFile).download();

            if(result.isPresent()) {
                File f = result.get();
                log.info(f.getName());
                redir.addFlashAttribute("videoId", videoId);
                redir.addFlashAttribute("videoName", f.getName());
                return "redirect:/watch"; // because I can't figure out the bloody forward:
            }
	} catch (IOException e) {
	    log.error("Error getting torrent file from server.", e);
	}

	return "redirect:/";
    }
}
