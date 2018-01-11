package springbackend;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class VideoSubmissionController extends WebMvcConfigurerAdapter {
	@Autowired
	private JdbcTemplate jdbc;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/results").setViewName("results");
    }

    @GetMapping("/videoSubmission")
    public String showForm(Model model) {
    	model.addAttribute("form", new VideoForm());
        return "form";
    }

    @PostMapping("/videoSubmission")
    public String checkVideoInfo(@Valid VideoForm videoSubmissionForm, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "form";
        } else {
        	File videoFile = new File("");
        	File thumbnailFile = new File("");
        	
        	String trackerURL = startUploadProcess(videoFile);  // upload-process returns the tracker-filePath
        	String thumbnailURL = storeThumbnailOnServer(thumbnailFile);
        	
        	
        	String fileType = getVideoExtension(videoFile.getPath());
        	
        	int userID = getUserID(videoSubmissionForm.getUserName());
        	videoSubmissionForm.setFileType(fileType);
        	videoSubmissionForm.setAuthor(userID); // get the user id and add it manually to the form
        	videoSubmissionForm.setThumbnailURL(thumbnailURL);
        	videoSubmissionForm.setTrackerURL(trackerURL); // add tracker-filePath manually to the form
        	
        	storeVideoFormIntoDB(videoSubmissionForm);
        }

        return "redirect:/results";
    }
    
    public String startUploadProcess(File video) {
    	// TO DO: call the upload process from here
    	return "";
    }
    
    public String storeThumbnailOnServer(File img) {
    	return "";
    }
    
    public String getVideoExtension(String videoFilePath) {
    	return videoFilePath.substring(videoFilePath.lastIndexOf('.')+1);
    }
    
    public int getUserID(String userName) {
    	// search for userID based on userName
    	ResultSetExtractor<Integer> rse = null;
    	final String SQL_QUERY = "SELECT id FROM nottv_user WHERE username = ?";
    	
    	PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(SQL_QUERY);
                ps.setString(1, userName);
                
                return ps;
            }
        };
    	
    	return this.jdbc.query(psc, rse);
    }
    
    public void storeVideoFormIntoDB(VideoForm vf) {
        // insert statement
    	final String INSERT_SQL = "INSERT INTO video(title, description, version,"
    			+ " fileType, language, city, country, license, "
    			+ "author, thumbnailURL, trackerURL, contentRating) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
    	
    	PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(INSERT_SQL);
                ps.setString(1, vf.getTitle());
                ps.setString(2, vf.getDescription());
                ps.setInt(3, vf.getVersion());
                ps.setString(4, vf.getFileType());
                ps.setString(5, vf.getLanguage());
                ps.setString(6, vf.getCity());
                ps.setString(7, vf.getCountry());
                ps.setString(8, vf.getLicense());
                ps.setInt(9, vf.getAuthor());
                ps.setString(10, vf.getThumbnailURL());
                ps.setString(11, vf.getTrackerURL());
                ps.setString(12, vf.getContentRating());
                return ps;
            }
        };
    	
    	this.jdbc.update(psc);

    }
  
}
