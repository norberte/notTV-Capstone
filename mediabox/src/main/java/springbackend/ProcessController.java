package springbackend;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    public String upload(MultipartFile video) {
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
    @GetMapping("get-thumbnail/<int:id>")
    @ResponseBody
    public File getThumbnail(@PathVariable int id, HttpServletResponse response){
        String thumbnailName = String.valueOf(id);
        File thumbnail = thumbnailStorage.get(thumbnailName);

        // if it exists, just return it
        if(thumbnailStorage.has(thumbnailName)) // Maybe implement an ImageStorage class that uses a StorageService and can accept id as an int.
            return thumbnail;

        // else download then return it.
        try {
            Request.Get(
                String.format("%s/thumbnail/%d", config.getServerUrl(), id)
            ).execute().saveContent(thumbnail);
            return thumbnail;
        } catch (IOException e) {
            log.error("Error getting thumbnail file: " + thumbnailName + " from server.", e);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND); // tell client file not found.
            return thumbnailStorage.get("placeholder");
        }
    }  

    @RequestMapping(path = "download")
    public String download(@RequestParam(value="torrentName") String torrentName, @RequestParam("videoId") int videoId, RedirectAttributes redir){
	// get torrent file.
	File torrentFile = torrentStorage.get(torrentName);
	try {
	    Request.Get(
		String.format("%s/get-torrent/%s", this.config.getServerUrl(), torrentName)
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
