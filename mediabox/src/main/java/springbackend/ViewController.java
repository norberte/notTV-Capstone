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
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import filesharingsystem.DownloadProcess;
import filesharingsystem.TtorrentDownloadProcess;

@Controller
public class ViewController {
    private static final Logger log = LoggerFactory.getLogger(ViewController.class);
    private final File torrentDir, videoDir;
    
    public ViewController() {
	torrentDir = new File(System.getProperty("user.home"), "torrents");
	videoDir = new File(System.getProperty("user.home"), "videos");
	if(!torrentDir.isDirectory())
	    torrentDir.mkdir();
	if(!videoDir.isDirectory())
	    videoDir.mkdir();
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
        }
       
        model.addAttribute("fileList", fileList);
        return "Example";
    }
    
    @RequestMapping("/browse")
    public String browse(){
        return "Browse";
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
    
    @RequestMapping(path = "download")
    public String download(@RequestParam(value="torrentName") String torrentName, Model model){
	// get torrent file.
	File torrentFile = new File(torrentDir, torrentName);
	try {
	    Request.Get(
		String.format("http://nottv.levimiller.ca/get-torrent/%s", torrentName)
	    ).execute().saveContent(torrentFile);

	    // download file.
	    DownloadProcess dp = new TtorrentDownloadProcess(
		torrentFile, new File(System.getProperty("user.home"), "videos"));
	    filesharingsystem.DownloadProcess.Client client = dp.download();
	    client.waitForDownload();
	    String filename = client.files().get(0).getName();
	    //removes ".torrent" from delete this if not using TrivialDownloadProcess
	    // filename = filename.substring(0, filename.length()-8);
	    
	    //this assumes the torrent contains a single video file. I don't know how we want to handle other cases, if at all -Daniel
	    log.info(filename);
	    model.addAttribute("source", "video/"+filename);
	    return "player";
	} catch (IOException e) {
	    log.error("Error getting torrent file from server.", e);
	}
	
	return "redirect:/";
    }
}
