package springbackend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DownloadController {

    @RequestMapping(path = "/download", method = RequestMethod.POST)
    public String download(@RequestParam(value="magnetLink") String magnet){
        // Start download process with magnet
        //wait until download completes?
        //return an object with video name, file path, etc.
        return null;
    }
}
