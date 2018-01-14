package spring;

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
    public void videoData(@RequestParam("filter") String filter){
        
        String query = "Select id From Video";
        for(String categoryValue :filter.split("+")){
            query += "Intersect +"
                    + "Select categoryValueId"
                    + "From video_category_value_join Natural Join category_value As cv"
                    + "Where cv.name = " + categoryValue;
        }
        query += ";";
        
        jdbcTemplate.query(query, rse)
        
    }
}