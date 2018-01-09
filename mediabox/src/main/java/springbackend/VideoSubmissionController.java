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
        	String trackerFPath = startUploadProcess();  // let the upload-process return the tracker-filePath
        	// TO DO: figure out how to get author of the video submission
        	// since the author's user id is not included on the HTML form
        	int userID = 0;
        	videoSubmissionForm.setAuthor(userID); // get the user id and add it manually to the form
        	videoSubmissionForm.setTrackerFilePath(trackerFPath); // add tracker-filePath manually to the form
        	storeVideoFormIntoDB(videoSubmissionForm);
        }

        return "redirect:/results";
    }
    
    public String startUploadProcess() {
    	// TO DO: call the upload process from here
    	return "";
    }
    
    public void storeVideoFormIntoDB(VideoForm vf) {
        // insert statement
    	final String INSERT_SQL = "INSERT INTO video(title, description, version,"
    			+ " fileType, language, city, country, license, tags, "
    			+ "author, trackerFilePath) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
    	
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
                ps.setString(9, vf.getTags());
                ps.setInt(10, vf.getAuthor());
                ps.setString(11, vf.getTrackerFilePath());
                return ps;
            }
        };
    	
    	this.jdbc.update(psc);

    }
  
}
