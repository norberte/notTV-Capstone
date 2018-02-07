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
    public boolean unsubscribe(@RequestParam(value="author", required=true) int author,
    @RequestParam(value="subscriber", required=true) int subscriber) {
        log.info("unsibscribe {} from {}", subscriber, author);
        
        // Make the query.
        String query = "Delete From subscribe Where subscriberId = ? AND authorId = ?;";
        log.info(query);
        
        int numberOfRowAffected = jdbc.update(query, aithor, subscriber);
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
}
