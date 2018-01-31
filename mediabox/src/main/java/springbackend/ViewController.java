package springbackend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import filesharingsystem.PortMapException;
import filesharingsystem.PortMapper;

import util.storage.StorageService;

@Controller
public class ViewController {
    private static final Logger log = LoggerFactory.getLogger(ViewController.class);
    @Autowired
    @Qualifier("VideoStorage")
    private StorageService videoStorage;

    @Autowired
    public ViewController(PortMapper portMapper) {
	// configure port forwarding.
	try {
	    portMapper.setup();
	} catch (PortMapException e) {
	    log.warn("Unable to setup the port forwarding.", e);
	    // TODO: send notification to UI to inform user
	    // that they need to enable upnp.
	    // Bonus: check portforwarding somehow to allow manual port forwarding.
	}
    }

    @RequestMapping({"/","/home"})
    public String home(Model model){
        String fileList="";
        File torrents;
        try {
            torrents = new ClassPathResource("public/torrents").getFile();
            System.out.println(torrents.exists());
            DirectoryStream<Path> dirStream = Files.newDirectoryStream(torrents.toPath());
            for(Path p: dirStream){
                fileList += "'"+p.getFileName()+"',";
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // e.printStackTrace();
	    log.error("Error getting torrents.", e);
        }

        model.addAttribute("fileList", fileList);
        return "Example";
    }

    @RequestMapping("player")
    public String player(@RequestParam(value="video") String video, Model model) {
	log.info("video {}", video);
	model.addAttribute("source", "/video/" + video);
	return "player";
    }
    
    @RequestMapping("{page}")
    public String defaultPage(@PathVariable String page, Model model) {
	model.addAttribute("title", StringUtils.capitalize(page));
	model.addAttribute("page_name", page);
	return "default_page";
    }

    @RequestMapping("upload")
    public String upload() {
	return "upload";
    }

    @RequestMapping("userProfile/{username}") 
    public String userProfile(@PathVariable("username") String username, Model model) {
    //public String userProfile() {
        model.addAttribute("username", username);
        return "userProfile";
    }
    
    @RequestMapping("video/{videoFile:.+}")
    @ResponseBody
    public void video(@PathVariable(value="videoFile") String source,
    // @RequestParam(value="type", required=false, defaultValue="video/mp4") String type,
    Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
	// Maybe later?
	// http://shazsterblog.blogspot.ca/2016/02/asynchronous-streaming-request.html
	// String type = "video/mp4";
        // model.addAttribute("source", source);
        // model.addAttribute("type", type);
	log.info("********** video *********");
	File videoFile = videoStorage.load(source).toFile();
	log.info("Exists: " + videoFile.isFile());
	log.info("Path: " + videoFile);

	try {
	    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
	    response.setHeader("Content-Disposition", "attachment; filename="+source);
	    InputStream iStream = new FileInputStream(videoFile);
	    IOUtils.copy(iStream, response.getOutputStream());
	    response.flushBuffer();
	} catch (java.nio.file.NoSuchFileException e) {
	    response.setStatus(HttpStatus.NOT_FOUND.value());
	} catch (Exception e) {
	    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}
    }
}
