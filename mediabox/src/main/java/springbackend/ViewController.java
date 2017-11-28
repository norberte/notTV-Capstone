package springbackend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ViewController {

    @RequestMapping({"/","/home"})
    public String home(){
        return "react-gui";
    }
    @RequestMapping("single")
    public String singleFile(){
        return "single-file-example";
    }
}
