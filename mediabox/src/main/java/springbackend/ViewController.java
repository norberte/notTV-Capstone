package springbackend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import filesharingsystem.PortMapException;
import filesharingsystem.PortMapper;

import util.storage.StorageService;

@Controller
public class ViewController {
    private static final Logger log = LoggerFactory.getLogger(ViewController.class);
    private final File videoDir;
    @Qualifier("VideoStorage")
    private StorageService videoStorage;

    @Autowired
    public ViewController(PortMapper portMapper) {
	// Set up directories to store files
	// TODO: Replace with FileStorageService
	videoDir = new File(System.getProperty("user.home"), "videos");
	if(!videoDir.isDirectory())
	    videoDir.mkdir();

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

    @RequestMapping("browse")
    public String browse(Model model) {
	return "browse";
    }

    @RequestMapping("upload")
    public String upload() {
	return "upload";
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
	File video = new File(videoDir, source);
	log.info("Exists: " + video.isFile());
	log.info("Path: " + video);

	try {
	    response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
	    response.setHeader("Content-Disposition", "attachment; filename="+source);
	    InputStream iStream = new FileInputStream(video);
	    IOUtils.copy(iStream, response.getOutputStream());
	    response.flushBuffer();
	} catch (java.nio.file.NoSuchFileException e) {
	    response.setStatus(HttpStatus.NOT_FOUND.value());
	} catch (Exception e) {
	    response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}
    }
}
