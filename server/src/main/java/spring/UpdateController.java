package spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import spring.view.VideoForm;


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
    JdbcTemplate jdbc;

    @PutMapping("/unsubscribe")
    @ResponseBody
    public boolean unsubscribe(@RequestParam(value="author", required=true) int author,
    @RequestParam(value="subscriber", required=true) int subscriber) {
        log.info("unsubscribe {} from {}", subscriber, author);
        
        // Make the query.
        String query = "Delete From subscribe Where subscriberId = ? AND authorId = ?;";
        log.info(query);
        
        int numberOfRowAffected = jdbc.update(query, author, subscriber);
        if(numberOfRowAffected > 0) {
            return true; // successfully unsubscribe
        } else {
            //TODO: does returning false return an error Response to the client?
            // Do we want an error to be thrown if they weren't even subscribed?
            return false; // did not unsubscribe, or could not even unsubscribe, since it was not subscribed before
        }        
    }

    @PutMapping("/subscribe")
    @ResponseBody
    public boolean subscribe(@RequestParam(value="subscriber", required=true) int subscriber,
    @RequestParam(value="author", required=true) int author ) {
        log.info("subscribe {} from {}", subscriber, author);

        // Make the query.
        String query = "Insert into subscribe(subscriberId,authorId) Values(?,?);";
        log.info(query);
        int numberOfRowAffected = jdbc.update(query, subscriber, author);
        if(numberOfRowAffected > 0) {
            return true; // successfully subscribed
        } else {
            return false; // did not subscribe, since some error happened or it was already subscribed in the beginning
        }
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
        jdbc.update(query, 1, videoId); //userid is hard-coded as 1 for now
        return true;
    }

    @PostMapping("/videoSubmission")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void processVideoInfo(@RequestBody(required=true) VideoForm videoForm) {
        // insert statement
        final String INSERT_SQL = "INSERT INTO video (title, description, version, fileType, license, userID, thumbnailURL, downloadURL) VALUES(?,?,?,?,?,?,?,?)";

        PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_SQL);
                ps.setString(1, videoForm.getTitle());
                ps.setString(2, videoForm.getDescription());
                ps.setInt(3, videoForm.getVersion());
                ps.setString(4, videoForm.getFiletype());
                ps.setString(5, videoForm.getLicense());
                ps.setInt(6, videoForm.getUserid());
                ps.setString(7, videoForm.getThumbnailurl());
                ps.setString(8, videoForm.getDownloadurl());
                return ps;
            }
        };

        this.jdbc.update(psc);
    }
}
