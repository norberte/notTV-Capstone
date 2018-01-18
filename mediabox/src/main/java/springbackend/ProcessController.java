package springbackend;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import filesharingsystem.process.DownloadProcess;
import filesharingsystem.process.TtorrentDownloadProcess;

@Controller
public class ProcessController {
    private static final Logger log = LoggerFactory.getLogger(ProcessController.class);
    @Autowired
    private Config config;
    private File torrentDir;

    public ProcessController() {
	torrentDir = new File(System.getProperty("user.home"), "torrents");
	if(!torrentDir.isDirectory())
	    torrentDir.mkdir();
    }
    
    @RequestMapping("upload")
    public void upload() {
	
    }
    
    @RequestMapping(path = "download")
    public String download(@RequestParam(value="torrentName") String torrentName, Model model){
	// get torrent file.
	File torrentFile = new File(torrentDir, torrentName);
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
	    model.addAttribute("source", "video/"+filename);
	    return "player";
	} catch (IOException e) {
	    log.error("Error getting torrent file from server.", e);
	}

	return "redirect:/";
    }
}
