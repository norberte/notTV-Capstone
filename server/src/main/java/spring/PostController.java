package spring;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
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
import org.springframework.web.multipart.MultipartFile;

import spring.view.VideoForm;
import spring.storage.StorageService;
import spring.view.AccountForm;

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
    
    @PostMapping("/videoSubmission")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void processVideoInfo(@RequestBody(required=true) VideoForm videoForm) {
        log.info("Adding video {}...", videoForm.getTitle());
        // insert statement
        final String INSERT_SQL = "INSERT INTO video (title, description, version, fileType, license, userID, thumbnailURL, downloadURL) VALUES(?,?,?,?,?,?,?,?)";
        
        log.info("Video's thumbnail URL is {} ...", videoForm.getThumbnailurl());
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
    
    @PostMapping("/thumbnailSubmission")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public String processThumnailFile(@RequestBody(required=true) MultipartFile image) {
        // if thumbnail was uploaded, store it on the server using the methods from ThumbnailUploadController
        Resource thumbnailURL = null;
        if(image != null) {
            log.info("Thubnail image name is: " + image.getOriginalFilename());
            ThumbnailUploadController thumbnailUploader = new ThumbnailUploadController(thumbnailStorage);
            try {
                thumbnailUploader.storeThumbnailOnServer(image);
                log.info("Successfully uploaded thumbnail file.");
            } catch (IllegalStateException e) {
                log.error("Error saving uploaded file.", e);
            }
            
            // if successfully uploaded inside the try-catch, then the following line should return the thumbnailURL
            thumbnailURL = thumbnailUploader.getUploadPath(image.getOriginalFilename());
        } else {
             log.error("image parameter is NULL");
        }
        
        // if a new ThumbnailURL was returned by the thumbnailUploader, then overwrite the default thumbnailURL
        if(thumbnailURL != null) {
            try {
                log.info("thumbnailURL.getURL().toExternalForm() = " + thumbnailURL.getURL().toExternalForm());
                return thumbnailURL.getURL().toExternalForm();
            } catch (IOException e) {
                log.error("Error getting thumbnailURL. IO Exception", e);
                return "";
            }
        } else {
            log.error("Error getting thumbnailURL: thumbnailURL is NULL");
            return ""; // empty string returned will signal the post request that the thumbnail was not uploaded
            // and it should stick with the default thumnailURL .. I think throwing an exception is an overkill
            // I just don't want to handle exceptions or anything else inside a post request's response
        }
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
