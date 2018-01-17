package spring;

import java.sql.ResultSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/*
 *  Queries the database and returns data based on queries
 */
@CrossOrigin
@RestController
public class DataRequestController {

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @RequestMapping("/video-data")
    public List<VideoData> videoData(@RequestParam(value="filter", required=false) String filter){
        
        String query = "Select * From Video";
        if(filter != null){
            for(String categoryValue :filter.split("+")){
                query += "Intersect +"
                        + "Select categoryValueId"
                        + "From video_category_value_join Natural Join category_value As cv"
                        + "Where cv.name = " + categoryValue;
            }
        }
        query += ";";
        
        List<VideoData> videos = jdbcTemplate.query(query, (rs, row) -> new VideoData(rs.getLong("id"), rs.getString("title")) );
        
        return videos;
    }
}