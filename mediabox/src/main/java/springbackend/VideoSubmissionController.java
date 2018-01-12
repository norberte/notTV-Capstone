package springbackend;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import filesharingsystem.TtorrentUploadProcess;
import filesharingsystem.UploadProcess;
import filesharingsystem.UploadProcess.UploadException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

import org.apache.commons.io.FilenameUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class VideoSubmissionController extends WebMvcConfigurerAdapter {
    @Autowired
    private JdbcTemplate jdbc;
    private static final Logger log = LoggerFactory.getLogger(VideoSubmissionController.class);

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
            File videoFile = videoSubmissionForm.getVideoFile();
            File thumbnailFile = videoSubmissionForm.getThumbnailFile();
            String trackerURL = "NULL";
            try {
                trackerURL = startUploadProcess(videoFile); // upload-process returns the tracker-filePath
            } catch (UploadException | URISyntaxException e) {
                log.error("Error while starting the upload process.", e);
            } 
            
            String thumbnailURL = "NULL";
            try {
                thumbnailURL = storeThumbnailOnServer(thumbnailFile);
            } catch (URISyntaxException | UploadException e) {
                log.error("Error while uploading video thumbnail to server.", e);
            }

            String fileType = getVideoExtension(videoFile.getPath());

            int userID = getUserID(videoSubmissionForm.getUserName()); // get the userID, given username from the DB
            videoSubmissionForm.setFileType(fileType);
            videoSubmissionForm.setAuthor(userID); // add userID manually to the VideoForm object
            videoSubmissionForm.setThumbnailURL(thumbnailURL);  // set the thumb-nail URL manually
            videoSubmissionForm.setTrackerURL(trackerURL); // add tracker-URL manually to the form

            storeVideoFormIntoDB(videoSubmissionForm);
            storeVideoCategoriesIntoDB(videoSubmissionForm); // TO DO
        }

        return "redirect:/results";
    }

    public String startUploadProcess(File video) throws UploadException, URISyntaxException {
        URI trackerURI = new URI("http://35.160.102.229/torrents");
        String filename = FilenameUtils.getBaseName(video.getPath());
        UploadProcess up = new TtorrentUploadProcess(
                new URI("http://35.160.102.229:6969/announce"),trackerURI);
        up.upload(filename, video);
        return trackerURI.toString() + "/" + String.format("%s.torrent", filename);
    }

    public String storeThumbnailOnServer(File img) throws UploadException, URISyntaxException {
        // send file to the server.
        // Create request
        URI thumbnailUploadURI = new URI("http://35.160.102.229/video_thumbnails");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(thumbnailUploadURI);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();

        // Play around with a random number to make the file name sort of unique
        Random r = new Random();
        int randomNum = r.nextInt(100000);
        String fileExtension = FilenameUtils.getExtension(img.getPath());
        String newFileName = img.getName() + "_" + randomNum + "." + fileExtension;

        // This attaches the file to the POST:
        builder.addBinaryBody("file", img, ContentType.APPLICATION_OCTET_STREAM, newFileName);

        uploadFile.setEntity(builder.build());
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(uploadFile);
        } catch (IOException e) {
            log.error("IO Exception has occured while uploading video thumbnail to server.", e);
        }
        int code = response.getStatusLine().getStatusCode();
        if (code == 200) {
            log.info("Successfully uploaded torrent to the server, seeding..."); 
            return thumbnailUploadURI.toString() + "/" + newFileName;
        } else {
            throw new UploadException("Unable to upload thumbnail to server. Got status code: " + code);
        }
    }

    public String getVideoExtension(String videoFilePath) {
        return FilenameUtils.getExtension(videoFilePath);
    }

    public int getUserID(String userName) {
        // search for userID based on userName
        final String SQL_QUERY = "SELECT id FROM nottv_user WHERE username = ?";
        int userID = this.jdbc.queryForObject(SQL_QUERY, Integer.class, userName);
        return userID;
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
    
    public void storeVideoCategoriesIntoDB(VideoForm vf) {
        
    }
}
