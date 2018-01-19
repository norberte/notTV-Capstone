package springbackend;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import filesharingsystem.process.DownloadProcess;
import filesharingsystem.process.TtorrentDownloadProcess;
import filesharingsystem.process.TtorrentUploadProcess;

import util.SeedManager;
import util.storage.StorageService;

@Controller
@RequestMapping("process")
public class ProcessController {
    private static final Logger log = LoggerFactory.getLogger(ProcessController.class);
    
    @Autowired
    private Config config;
    @Autowired
    @Qualifier("TorrentStorage")
    private StorageService torrentStorage;
    @Autowired
    @Qualifier("VideoStorage")
    private StorageService videoStorage; 
    
    @PostMapping("upload")
    @ResponseBody
    public String upload(MultipartFile video) {
	log.info(video.getOriginalFilename());
	String name = video.getOriginalFilename();
	//TODO: use a hash or something to make a unique name
	File localVideo = videoStorage.newFile(name);
	log.info(localVideo.toString());
	try {
	    video.transferTo(localVideo);
	} catch (IllegalStateException | IOException e) {
	    log.error("Error saving uploaded file.", e);
	}

	try {
	    // Start seeding process.
	    File torrent = SeedManager.addProcess(new TtorrentUploadProcess(
		new URI(config.trackerUrl + "/announce"),
		new URI(config.serverUrl + "/upload-torrent"),
		name, localVideo
	    ));
	    return torrent.getName();
	} catch (URISyntaxException e) {
	    log.error("Unable to start upload process. Malformed URI's in config.", e);
	    return null; // hardcoded urls, so should never happen.
	}
    }
    
    @RequestMapping(path = "download")
    public String download(@RequestParam(value="torrentName") String torrentName, Model model){
	// get torrent file.
	File torrentFile = torrentStorage.newFile(torrentName);
	try {
	    Request.Get(
		String.format("%s/get-torrent/%s", this.config.getServerUrl(), torrentName)
	    ).execute().saveContent(torrentFile);

	    // download file.
	    DownloadProcess dp = new TtorrentDownloadProcess(
		torrentFile, new File(System.getProperty("user.home"), "videos"));
	    filesharingsystem.process.DownloadProcess.Client client = dp.download();
	    client.waitForDownload();
	    String filename = client.files().get(0).getName();
	    //removes ".torrent" from delete this if not using TrivialDownloadProcess
	    // filename = filename.substring(0, filename.length()-8);

	    //this assumes the torrent contains a single video file. I don't know how we want to handle other cases, if at all -Daniel
	    //torrents with multiple files are a bonus feature :P -Levi
	    log.info(filename);
	    model.addAttribute("source", "/video/"+filename);
	    return "player";
	} catch (IOException e) {
	    log.error("Error getting torrent file from server.", e);
	}

	return "redirect:/";
    }
}
