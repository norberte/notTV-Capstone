package springbackend;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ViewController {
    private static final Logger log = LoggerFactory.getLogger(ViewController.class);
    
    private void defaultSetup(String page, Model model, Map<String,String> paramMap) {
        // Copy model values from any redirects.
        Map<String, ?> modelMap = model.asMap();
        List<String> keys = new ArrayList<>(modelMap.size());
        List<Object> values = new ArrayList<>(modelMap.size());
        // export all model attributes to javascript
        modelMap.entrySet().forEach(e -> {
            keys.add(e.getKey());
            values.add(e.getValue());
        });

        // export all parameters to javascript.
        paramMap.entrySet().forEach(e -> {
            keys.add(e.getKey());
            values.add(e.getValue());
        });     
        model.addAttribute("modelKeys", keys);
        model.addAttribute("modelValues", values);
        model.addAttribute("title", StringUtils.capitalize(page));
        model.addAttribute("page_name", page);
        log.info("Sanity check: {}", model.asMap());
    }

    @RequestMapping({"/","/home"})
    public String home(Model model, @RequestParam Map<String,String> paramMap){
        defaultSetup("browse", model, paramMap);
        return "default_page";
    }

    @RequestMapping("{page}")
    public String defaultPage(@PathVariable String page, Model model, @RequestParam Map<String,String> paramMap) {
        defaultSetup(page, model, paramMap);
	return "default_page";
    }

    @RequestMapping("account")
    public String account() {
        return "account";
    }

    @RequestMapping("userProfile/{username}")
    public String userProfile(@PathVariable("username") String username, Model model) {
        // public String userProfile() {
        model.addAttribute("username", username);
        model.addAttribute("title", "User Profile");
        model.addAttribute("page_name", "userProfile");
        return "default_page";
    }
}
