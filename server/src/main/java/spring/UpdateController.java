package spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Daniel
 *
 * Controller class for handling incoming database updates
 */

@CrossOrigin
@RestController
@RequestMapping("/update")
public class UpdateController {
    private static final Logger log = LoggerFactory.getLogger(InfoController.class);

    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @PostMapping("/subscribe")
    @ResponseBody
    public boolean subscribe(@RequestParam("authorId") int authorId, @RequestParam("unsub") boolean unsub) {
        log.info("update subscriptions table");
        String query;
        if(unsub)
            query = new String("Delete From subscribe Where authorid = ? And subscriberid = ?;");
        else
            query = new String("Insert Into subscribe (authorid, subscriberid) Values (?,?);");

        log.info(query);
        jdbcTemplate.update(query, authorId, 1);
        return true;
    }
    
    /**
     * Update the flag table with an new notTV standards violation report
     * @param videoId - id of reported video
     * @param reportText - message describing the reason for reporting the video/nature of the violation
     * @return
     */
    @PostMapping("/report")
    @ResponseBody
    public boolean report(@RequestParam("videoId") int videoId, @RequestParam("report_text") String reportText) {
        log.info("update subscriptions table");
        //TODO: add reportText to query after we add that column to the database 
        String query = new String("Insert Into flag (userid, videoid) Values (?,?);"); 
        log.info(query);
        jdbcTemplate.update(query, 1, videoId); //userid is hard-coded as 1 for now
        return true;
    }
}
