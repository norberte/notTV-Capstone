package spring;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import spring.view.CategoryType;
import spring.view.CategoryValue;
import spring.view.Video;

@CrossOrigin
@RestController
@RequestMapping("/info")
public class InfoController {
    private static final Logger log = LoggerFactory.getLogger(InfoController.class);
    
    @GetMapping("/categories")
    @ResponseBody
    public CategoryType[] getCategories() {
	log.info("categories");
	// test data.
	List<CategoryValue> misc = Arrays.asList(
	    new CategoryValue(1, "In Library"),
	    new CategoryValue(2, "Short Videos"),
	    new CategoryValue(3, "Long Videos")
	);
	List<CategoryValue> city = Arrays.asList(
	    new CategoryValue(1, "Edmonton"),
	    new CategoryValue(2, "Kelowna")
	); 
	List<CategoryType> categories = Arrays.asList(
	    new CategoryType("Misc", misc.toArray(new CategoryValue[misc.size()])),
	    new CategoryType("City", city.toArray(new CategoryValue[city.size()]))
	);
	return categories.toArray(new CategoryType[categories.size()]);
    }

    @GetMapping("/videos")
    @ResponseBody
    public Video[] getVideos() {
	List<Video> videos = Arrays.asList(
	    new Video(
		"Title",
		"/img/default-placeholder-300x300.png",
		"/test/url"
	    )
	);
	return videos.toArray(new Video[videos.size()]);
    }
}
