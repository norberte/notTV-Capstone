package springbackend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import filesharingsystem.DownloadProcess;

@Controller
public class ViewController {

    @RequestMapping({"/","/home"})
    public String home(){
        return "Example";
    }
    @RequestMapping("player")
    public String playVideo(@RequestParam(value="source") String source,
            @RequestParam(value="type", required=false, defaultValue="video/mp4") String type, Model model){
        model.addAttribute("source", source);
        model.addAttribute("type", type);
        return "player";
    }
    @RequestMapping(path = "download", method = RequestMethod.POST)
    public String download(@RequestParam(value="submit") String magnet, Model model){
        
        String source;
        String type = "video/mp4";
        
        Client client = new DownloadProcess(magnet).download();
        client.waitForCompletion();
        String filename = client.getTorrent().getName();
        
        //this assumes the torrent contains a single video file. I don't know how we want to handle other cases, if at all -Daniel
        source = filename;
        
        model.addAttribute("source", source);
        model.addAttribute("type", type);
        return "player";
    }
}
