package spring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import spring.storage.StorageService;
import spring.view.AccountForm;
import spring.view.VideoForm;

@CrossOrigin
@RestController
@RequestMapping("/upload")
public class PostController {
    private static final Logger log = LoggerFactory.getLogger(PostController.class);
    
    @Autowired
    private JdbcTemplate jdbc;
    
    @Autowired
    @Qualifier("ImageStorage")
    private StorageService thumbnailStorage;
    
    @PostMapping("/add-video")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void processVideoInfo(@RequestBody(required=true) VideoForm videoForm) {
        log.info("Adding video {}...", videoForm.getTitle());
        // insert statement
        final String INSERT_SQL = "INSERT INTO video (title, description, version, fileType, license, userID, downloadURL) VALUES(?,?,?,?,?,?,?)";
        
        PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_SQL);
                ps.setString(1, videoForm.getTitle());
                ps.setString(2, videoForm.getDescription());
                ps.setInt(3, videoForm.getVersion());
                ps.setString(4, ""); // TODO: remove this from the db.
                ps.setString(5, videoForm.getLicense());
                ps.setInt(6, videoForm.getUserid());
                ps.setString(7, videoForm.getDownloadurl());
                return ps;
            }
        };

        this.jdbc.update(psc);
    }
    
    private int getUserID(String username) {
        // Make the query.
        StringBuilder queryBuilder = new StringBuilder("Select id From nottv_user Where username = ?;");
        String query = queryBuilder.toString();
        
        PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, username); // json of length 1 is sent, with only one username inside the json object
                return ps;
            }
        };
    
        List<Integer> id = this.jdbc.query(psc, (rs, row) -> new Integer(rs.getInt("id")));
        if(id.size() > 0) {
            return id.get(0);
        } else {
            return -10;
        }
    }
    
    private int getUserSettingsPK(int user_id) {
        // Make the query.
        StringBuilder queryBuilder = new StringBuilder("Select id From settings Where userId = ?;");
        String query = queryBuilder.toString();
        
        PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setInt(1, user_id); // json of length 1 is sent, with only one username inside the json object
                return ps;
            }
        };
    
        List<Integer> id = this.jdbc.query(psc, (rs, row) -> new Integer(rs.getInt("id")));
        if(id.size() > 0) {
            return id.get(0);
        } else {
            return -1;
        }
    }
    
    @PostMapping("/accountSubmit")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void processAccountInfoChanges(@RequestBody(required=true) AccountForm accountInfo) {
        // update statements
        final String emailUpdate_query = "UPDATE nottv_user SET email = ? WHERE id = ?;";
        final String passwordUpdate_query = "UPDATE nottv_user SET password = ? WHERE id = ?;";
        final String autoDownload_update = "UPDATE settings SET autodownload = ? WHERE id = ?;";
        
        // insert statement
        final String autoDownload_insert = "INSERT INTO settings(userId, alwaysDownload) VALUES(?,?);";
   
        String username = accountInfo.getCurrentUsername();
        int user_id = getUserID(username);
        if(user_id == -10) {    // username does not belong to a userid... non-existent account
            return;
        }
        
        String newPass = accountInfo.getNewPass();
        String confirmedNewPass = accountInfo.getConfirmNewPass();
        
        // change password, if the 2 passwords match
        if(newPass.equals(confirmedNewPass)) {
            PreparedStatementCreator psc = new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(passwordUpdate_query);
                    ps.setString(1, newPass);
                    ps.setInt(2, user_id);
                    return ps;
                }
            };
            this.jdbc.update(psc);
        }
        
        // change e-mail address
        String newEmail = accountInfo.getNewEmail();
        PreparedStatementCreator psc2 = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(emailUpdate_query);
                ps.setString(1, newEmail);
                ps.setInt(2, user_id);
                return ps;
            }
        };
        this.jdbc.update(psc2);
        
        // change auto-download 
        boolean autoDownloading;
        String autoDownload = accountInfo.getAutoDownload(); // take care of this
        if(autoDownload.equals("T")){
            autoDownloading = true;
        } else {
            autoDownloading =  false;
        }
        
        int settings_id = getUserSettingsPK(user_id);
        if(settings_id == -1) { // if user does not have settings yet, insert into settings table
            PreparedStatementCreator psc = new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(autoDownload_insert);
                    ps.setInt(1, user_id);
                    ps.setBoolean(2, autoDownloading);
                    return ps;
                }
            };

            this.jdbc.update(psc);
        } else {    // if user_id is already present in the settings table, just update auto-download
            PreparedStatementCreator psc = new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(autoDownload_update);
                    ps.setBoolean(1, autoDownloading);
                    ps.setInt(2, settings_id);
                    return ps;
                }
            };
            this.jdbc.update(psc);
        }
    }
}
