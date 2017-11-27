package springbackend;

@Controller
public class DownloadController {

    @RequestMapping("/")
    public String download(){
        return "react-gui";
    }
}
