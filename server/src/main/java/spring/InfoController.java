package spring;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public List<CategoryType> getCategories() {
	log.info("categories");
	// CategoryValue(id, name)
	// CategoryType(name, CategoryValue[] values)
	return jdbcTemplate.query("Select * From category_type;", (rs, rowNum) -> new CategoryType(
	    rs.getString("name"),
	    jdbcTemplate.query(
		"Select id, name From category_value Where categorytypeid=?;",
		(cvRs, cvRowNum) -> new CategoryValue(cvRs.getInt("id"), cvRs.getString("name")),
		rs.getInt("id")
	    )
	));
    }

    @GetMapping("/videos")
    @ResponseBody
    public List<Video> getVideos(@RequestParam(value="categories[]", required=false) int[] categories) {
	// Video(title, thumbnail_url, download_url)
	log.info("videos");

	// Make the query. It looks terrible, but it should be pretty efficient since
	// the Intersect tables will be small, and the filters on the id can be pushed up before the joins.
	// Also, the intersects can be used to filter subsequent results
	StringBuilder queryBuilder = new StringBuilder("Select title, downloadurl, thumbnailurl From Video ");
	
	if(categories != null && categories.length > 0) { // Only filter results if categories are specified.
	    queryBuilder.append("Where id in (");
	    for(int i=0; i<categories.length;i++) {
		if(i != 0) // No intersect on first one.
		    queryBuilder.append("Intersect ");
		queryBuilder.append("Select videoid ");
		queryBuilder.append("From video_category_value_join As vcvj Join category_value As cv On cv.id=vcvj.categoryvalueid ");
		queryBuilder.append("Where cv.id = ");
		queryBuilder.append(categories[i]);
	    }
	    queryBuilder.append(')');
	}
	queryBuilder.append(';');
	String query = queryBuilder.toString();
	log.info(query);
	
	return jdbcTemplate.query(query, (rs, row) -> new Video(
	    rs.getString("title"), 
	    "/img/default-placeholder-300x300.png", //TODO: link up thumbnails
	    "/download?torrentName="+rs.getString("downloadurl"))
	); 
    }
}
