package spring;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Autowired
    JdbcTemplate jdbcTemplate;

    @GetMapping("/categories")
    @ResponseBody
    public CategoryType[] getCategories() {
        log.info("categories");
        // test data.
        // CategoryValue(id, name)
        // CategoryType(name, CategoryValue[] values)
        // If arrays are a pain and Lists are more convenient, feel free to
        // change the view object.
        // I'm pretty sure it will Serialize to the same json.
        List<CategoryValue> misc = Arrays.asList(new CategoryValue(1, "In Library"),
                new CategoryValue(2, "Short Videos"), new CategoryValue(3, "Long Videos"));
        List<CategoryValue> city = Arrays.asList(new CategoryValue(1, "Edmonton"), new CategoryValue(2, "Kelowna"));
        List<CategoryType> categories = Arrays.asList(
                new CategoryType("Misc", misc.toArray(new CategoryValue[misc.size()])),
                new CategoryType("City", city.toArray(new CategoryValue[city.size()])));
        return categories.toArray(new CategoryType[categories.size()]);
    }

    @GetMapping("/videos")
    @ResponseBody
    public Video[] getVideos(@RequestParam(value="categories[]", required=false) int[] categories) {
	// Video(title, thumbnail_url, download_url)
	// I don't think we store thumbnails yet, so it's okay to just use the placeholder for now.
	// For the download url, I think we just store the torrent file(?) so just append
	// the name of the torrent to /download?torrentName=
        
    String query = "Select * From Video";
    
    if(categories != null){
        for(int categoryValue :categories){
            query += "Intersect +"
                      + "Select categoryValueId"
                      + "From video_category_value_join Natural Join category_value As cv"
                      + "Where cv.id = " + categoryValue;
        }
    }
    
    query += ";";
            
    List<Video> videos = jdbcTemplate.query(query, 
            (rs, row) -> new Video(rs.getString("title"), 
                        "/img/default-placeholder-300x300.png",
                        "/download?torrentName="+rs.getString("url")) ); 
    /* 
	List<Video> videos = Arrays.asList(
	    new Video(
		"Title",
		"/img/default-placeholder-300x300.png",
		"/download?torrentName=test.torrent"
	    )
	);
	*/
	return videos.toArray(new Video[videos.size()]);
    }
}
