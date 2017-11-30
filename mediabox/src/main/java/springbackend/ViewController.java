package springbackend;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
}
