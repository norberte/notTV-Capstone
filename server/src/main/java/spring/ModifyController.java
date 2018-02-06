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

@CrossOrigin
@RestController
@RequestMapping("/set")
public class ModifyController {
    private static final Logger log = LoggerFactory.getLogger(ModifyController.class);
    
    @Autowired
    private JdbcTemplate jdbc;

    
    @PutMapping("/unsubscribe")
    @ResponseBody
    public boolean unsubscribe(@RequestParam(value="userID1", required=true) int userID1,
                        @RequestParam(value="userID2", required=true) int userID2 ) {
    log.info("Given userID1 and userID2, unsubscribe userid1 from userId2's profile");

    // Make the query.
    String query = "Delete From subscribe Where subscriberId = ? AND authorId = ?;";
    log.info(query);
    
    PreparedStatementCreator psc = new PreparedStatementCreator() {
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userID1);  // user1, the loggedIn user, is the subscriber
            ps.setInt(2, userID2);  // user2, the user who's profile is being unsubscribed, is the author
            return ps;
        }
    };
    
    
        int numberOfRowAffected = jdbc.update(psc);
        if(numberOfRowAffected > 0) {
            return true; // successfully unsubscribe
        } else {
            return false; // did not unsubscribe, or could not even unsubscribe, since it was not subscribed before
        }        
    }
    
    
    @PutMapping("/subscribe")
    @ResponseBody
    public boolean subscribe(@RequestParam(value="userID1", required=true) int userID1,
                        @RequestParam(value="userID2", required=true) int userID2 ) {
    log.info("Given userID1 and userID2, subscribe userid1 to userId2's profile");

    // Make the query.
    String query = "Insert into subscribe(subscriberId,authorId) Values(?,?);";
    log.info(query);
    
    PreparedStatementCreator psc = new PreparedStatementCreator() {
        public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userID1);  // user1, the loggedIn user, is the subscriber
            ps.setInt(2, userID2);  // user2, the user who's profile is being unsubscribed, is the author
            return ps;
        }
    };
    
        int numberOfRowAffected = jdbc.update(psc);
        if(numberOfRowAffected > 0) {
            return true; // successfully subscribed
        } else {
            return false; // did not subscribe, since some error happened or it was already subscribed in the beginning
        }
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
