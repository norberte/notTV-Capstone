package springbackend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DownloadController {

    @RequestMapping({"/","/home"})
    public String download(){
        return "react-gui";
    }
}
