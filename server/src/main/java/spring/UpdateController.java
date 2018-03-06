package spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import spring.view.CategoryUpdate;
import spring.view.CategoryUpdateList;
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
    
    private static final int NEW_CATEGORY_TYPE = 1;
    private static final int NEW_CATEGORY_VALUE = 2;
    private static final int EDIT_CATEGORY_VALUE = 3;
    private static final int EDIT_CATEGORY_TYPE = 4;
    private static final int DELETE_CATEGORY_VALUE = 5;
    private static final int DELETE_CATEGORY_TYPE = 6;

    @Autowired
    JdbcTemplate jdbc;

    @PostMapping("/unsubscribe")
    @ResponseBody
    public boolean unsubscribe(@RequestParam(value="author", required=true) int author,
    @RequestParam(value="subscriber", required=true) int subscriber) {
        log.info("unsubscribe {} from {}", subscriber, author);
        
        // Make the query.
        String query = "Delete From subscribe Where subscriberId = ? AND authorId = ?;";
        log.info(query);
        
        int numberOfRowAffected = jdbc.update(query, subscriber, author);
        if(numberOfRowAffected > 0) {
            return true; // successfully unsubscribe
        } else {
            //TODO: does returning false return an error Response to the client?
            // Do we want an error to be thrown if they weren't even subscribed?
            return false; // did not unsubscribe, or could not even unsubscribe, since it was not subscribed before
        }        
    }

    @PostMapping("/subscribe")
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
    public boolean report(@RequestParam("videoId") int videoId, @RequestParam("reportText") String reportText) {
        log.info("update subscriptions table");
        String query = "Insert Into flag (userid, videoid, message) Values (?,?,?);"; 
        log.info(query);
        jdbc.update(query, 1, videoId, reportText); //userid is hard-coded as 1 for now
        return true;
    }

    @PostMapping("/videoSubmission")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void processVideoInfo(@RequestBody(required=true) VideoForm videoForm) {
        // insert statement
        final String INSERT_SQL = "INSERT INTO video (title, description, version, license, userID, downloadURL) VALUES(?,?,?,?,?,?)";
        
        PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_SQL);
                ps.setString(1, videoForm.getTitle());
                ps.setString(2, videoForm.getDescription());
                ps.setInt(3, videoForm.getVersion());
                ps.setString(4, videoForm.getLicense());
                ps.setInt(5, videoForm.getUserid());
                ps.setString(6, videoForm.getDownloadurl());
                return ps;
            }
        };

        this.jdbc.update(psc);
    }
    @PostMapping("/categories")
    @ResponseBody
    public void updateCategories(@RequestBody CategoryUpdateList jsonString) {
        
        List<CategoryUpdate> updateList = jsonString.getUpdateList();
        //updateList.sort((a, b) -> a.action - b.action); // sort update list by action: delete first, then insert, then update
        
        String sql = null;

        for(CategoryUpdate update: updateList){
            switch(update.action){
                case DELETE_CATEGORY_VALUE:
                    sql = "Delete From category_value Where id = ?;";
                    jdbc.update(sql, update.categoryValueId);
                    break;
                case DELETE_CATEGORY_TYPE:
                    sql = "Delete From category_value Where categoryTypeId = ?; Delete From category_type Where id = ?;";
                    jdbc.update(sql, update.categoryTypeId, update.categoryTypeId);
                    break;
                case NEW_CATEGORY_TYPE:
                    sql = "Insert Into category_type (id, name) Values (?, ?);";
                    jdbc.update(sql, update.categoryTypeId, update.value);
                    break;
                case NEW_CATEGORY_VALUE:
                    sql = "Insert Into category_value (id, categoryTypeId, name) Values (?, ?, ?);";
                    jdbc.update(sql, update.categoryValueId, update.categoryTypeId, update.value);
                    break;
                case EDIT_CATEGORY_VALUE:
                    sql = "Update category_value Set name = ? Where id = ?;";
                    jdbc.update(sql, update.value, update.categoryValueId);
                    break;
                case EDIT_CATEGORY_TYPE:
                    sql = "Update category_type Set name = ? Where id = ?;";
                    jdbc.update(sql, update.value, update.categoryTypeId);
                    break;
                default:
                    log.info("shit");
                    break;
            }
            log.info(sql);
        }     
    }
}
