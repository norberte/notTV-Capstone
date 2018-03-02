package springbackend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
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
    
    private HttpServletRequest request;


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
    
    // because I am casting Request.Get(...).execute().returnContent() to List<String>
    @SuppressWarnings("unchecked")
    @PostMapping("downloadThumbnail")
    @ResponseBody
    public void thumbnailDownload(){
        // get a list of all thumbnail names.
        List<String> thumbnailNames = Collections.EMPTY_LIST;
        try {
            thumbnailNames = (List<String>) Request.Get(
            String.format("%s/list-thumbnails", this.config.getServerUrl())
            ).execute().returnContent();
        } catch (IOException e) {
            log.error("Error getting thumbnail list from server.", e);
        }
        
        // there could be multiple videos with the same thumbnail... so only download a unique set of thumbnails
        Set<String> uniqueSetOfThumbnails = new HashSet<>(thumbnailNames);
        File[] thumbnailFiles = new File[uniqueSetOfThumbnails.size()];
        int idx = 0;
        
        // iterate though each unique thumbnail, download them, then store them in a File array
        Iterator<String> itr = uniqueSetOfThumbnails.iterator();
        String temp;
        while(itr.hasNext()) {
            temp = itr.next();
            try {
                Request.Get(
                        String.format("%s/get-thumbnail/%s", this.config.getServerUrl(), temp)
                        ).execute().saveContent(thumbnailFiles[idx++]);
            } catch (IOException e) {
                log.error("Error getting the thumbnail file from server.", e);
            }
        }
        
        // go through each thumbnail file and upload it to the Browse page
        for (int i = 0; i < thumbnailFiles.length; i++) {
            MultipartFile file = (MultipartFile) thumbnailFiles[i];
            if (!file.isEmpty()) {
                String uploadsDir = "/img/";
                // need to get the servlet context
                String realPathtoUploads =  request.getServletContext().getRealPath(uploadsDir);
                if(! new File(realPathtoUploads).exists()){
                    new File(realPathtoUploads).mkdir();
                }

                log.info("realPathtoUploads = {}", realPathtoUploads);

                String orgName = file.getOriginalFilename();
                String filePath = realPathtoUploads + orgName;
                File dest = new File(filePath);
                try {
                    file.transferTo(dest);
                } catch (IllegalStateException e) {
                    log.error("Error getting thumbnail file to front end.", e);
                } catch (IOException e) {
                    log.error("Error getting thumbnail file to front end.", e);
                }
            }
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
