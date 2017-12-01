package springbackend;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import filesharingsystem.DownloadProcess;
import filesharingsystem.TrivialDownloadProcess;

@Controller
public class ViewController {

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
            e.printStackTrace();
        }
       
        model.addAttribute("fileList", fileList);
        return "Example";
    }
    @RequestMapping("player")
    public String playVideo(@RequestParam(value="torrentFile") String source,
                            @RequestParam(value="type", required=false, defaultValue="video/mp4") String type, 
                            Model model){
        model.addAttribute("source", source);
        model.addAttribute("type", type);
        return "player";
    }
    @RequestMapping(path = "download", method = RequestMethod.POST)
    public String download(@RequestParam(value="torrentFile") String torrentName, Model model){
        
        String source;
        File torrentFile = new File("public/torrents/"+torrentName);
        
        DownloadProcess dp = new TrivialDownloadProcess(torrentFile);
        filesharingsystem.DownloadProcess.Client client = dp.download();
        client.waitForDownload();
        String filename = client.files().get(0).getName();
        //removes ".torrent" from delete this if not using TrivialDownloadProcess
        filename = filename.substring(0, filename.length()-8);
        
        //this assumes the torrent contains a single video file. I don't know how we want to handle other cases, if at all -Daniel
        source = "videos/"+filename;
        
        model.addAttribute("source", source);
        return "player";
    }
}
