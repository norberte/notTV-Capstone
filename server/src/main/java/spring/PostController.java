package spring;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import spring.model.CategoryType;
import spring.model.CategoryValue;
import spring.model.Video;
import spring.view.VideoForm;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

@CrossOrigin
@RestController
@RequestMapping("/upload")
public class PostController {
    private String fileType = "NULL";
    private String downloadURL = "NULL";
    private String thumbnailURL = "NULL";
    private int userID = 0;
    
    private static final Logger log = LoggerFactory.getLogger(PostController.class);
    
    @Autowired
    private JdbcTemplate jdbc;
    
    @PostMapping("/videoSubmission")
    public void processVideoInfo(@RequestParam(value="formData") VideoForm videoUploaded) {
        log.debug("Video Model's toString(): " + videoUploaded.toString());
        fileType = getVideoExtension(videoUploaded);
        userID = getUserID(videoUploaded.getAuthor()); // get the userID, given userName from the DB
        downloadURL = createDownloadURL(videoUploaded.getVideoFile());
        thumbnailURL = getThumbnailURL(videoUploaded.getVideoPoster());
        storeVideoFormIntoDB(videoUploaded);
    }

    private String createDownloadURL(File TorrentFile) {
        if(TorrentFile == null) {
            return "NULL";
        } else {
            return "/download?torrentName=" + TorrentFile.getName();
        }
    }
    
    private String getThumbnailURL(File img) {
        return "TO BE IMPLEMENTED SOON";
    }

    public String getVideoExtension(VideoForm videoUploaded) {
        if(videoUploaded.getVideoFile() == null) {
            return "NULL";
        } else {
            return FilenameUtils.getExtension(videoUploaded.getVideoFile().getPath());
        }
    }

    public int getUserID(String userName) {
        // search for userID based on userName
        final String SQL_QUERY = "SELECT id FROM nottv_user WHERE username = ?";
        int userID = this.jdbc.queryForObject(SQL_QUERY, Integer.class, userName);
        return userID;
    }
    
    public void storeVideoFormIntoDB(VideoForm videoUploaded) {
        // insert statement
        final String INSERT_SQL = "INSERT INTO video(title, description, version,"
                + " fileType, language, license, city, country, userID, thumbnailURL, downloadURL,"
                + " videoRating) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_SQL);
                ps.setString(1, videoUploaded.getVideoTitle());
                ps.setString(2, videoUploaded.getVideoDescription());
                ps.setInt(3, videoUploaded.getVideoVersion());
                ps.setString(4, fileType);
                ps.setString(5, videoUploaded.getVideoLanguage());
                ps.setString(6, videoUploaded.getVideoLicense());
                ps.setString(7, videoUploaded.getCity());
                ps.setString(8, videoUploaded.getCountry());
                ps.setInt(9, userID);
                ps.setString(10, thumbnailURL);
                ps.setString(11, downloadURL);
                ps.setString(12, videoUploaded.getVideoRating());
                return ps;
            }
        };

        this.jdbc.update(psc);
    }
}
