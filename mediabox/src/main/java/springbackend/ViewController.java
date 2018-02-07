package springbackend;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import util.storage.StorageService;

@Controller
public class ViewController {
    private static final Logger log = LoggerFactory.getLogger(ViewController.class);
    @Autowired
    @Qualifier("VideoStorage")
    private StorageService videoStorage;

    private void defaultSetup(String page, Model model) {
        // Copy model values from any redirects.
        Map<String, ?> modelMap = model.asMap();
        List<String> keys = new ArrayList<>(modelMap.size());
        List<Object> values = new ArrayList<>(modelMap.size());
        modelMap.entrySet().forEach(e -> {
            keys.add(e.getKey());
            values.add(e.getValue());
        });
        model.addAttribute("modelKeys", keys);
        model.addAttribute("modelValues", values);
	model.addAttribute("title", StringUtils.capitalize(page));
	model.addAttribute("page_name", page);
        log.info("Sanity check: {}", model.asMap());
    }

    @RequestMapping({"/","/home"})
    public String home(Model model){
        defaultSetup("browse", model);
        return "default_page";
    }
    
    @RequestMapping("{page}")
    public String defaultPage(@PathVariable String page, Model model) {
        defaultSetup(page, model);
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
        model.addAttribute("title", "User Profile");
        model.addAttribute("page_name", "userProfile");
        return "default_page";
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
	File videoFile = videoStorage.get(source);
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
