package controller;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;

import controller.Request2JSON;
import spring.view.AccountForm;
import spring.view.Playlist;
import spring.view.VideoForm;

public class PostControllersTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Test
    public void testProcesingVideoInfo() throws IOException {
        // test if video data can be processed properly
        
        // create video object
        VideoForm vidInfo = new VideoForm();
        vidInfo.setUserid(1);
        vidInfo.setTitle("TestVideo");
        vidInfo.setDescription("Mock Video File");
        
        // create multipartFile for thumbnail
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);

        // This attaches the file to the POST:
        File f = new File("emptyFile.png");   // could not get it take a file from the resource folder...
        f.createNewFile();
        
        URL url = new URL("http://localhost:1248/upload/add-video");
        String urlParameters =
                "videoForm=" + URLEncoder.encode(Request2JSON.videoFormToString(vidInfo), "UTF-8") + 
                "&thumbnail=" + URLEncoder.encode("", "UTF-8");
        
        int response = Request2JSON.getInt(url, urlParameters, "POST", false);
        assertTrue(response != -1);
    }
    
    @Test
    public void testAccountSubmit() throws IOException, SQLException {
        // test if the account submission works, when a user changes information to their account
        AccountForm accountForm = new AccountForm();
        accountForm.setCurrentUsername("testUser");
        accountForm.setNewPass("pass");
        accountForm.setConfirmNewPass("pass");
        accountForm.setNewEmail("testing@test.com");
        
        URL url = new URL("http://localhost:1248/upload/accountSubmit");
        String urlParameters =
                "videoForm=" + URLEncoder.encode(Request2JSON.accountFormToString(accountForm), "UTF-8");
        
        Request2JSON.getJSON(url, urlParameters, "POST");
        
        // check for changes being made 
        String query = "Select password, email From nottv_user Where username = ?;";

        PreparedStatementCreator psc = new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement(query);
                ps.setString(1, accountForm.getCurrentUsername()); // json of length 1 is sent, with only one userid inside the json object
                return ps;
            }
        };

        ResultSet results = (ResultSet) jdbcTemplate.query(psc, new RowMapper() {
           public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
               return rs;
           }
        }).get(0);
        
        String passwordHash = results.getString("password");
        String email = results.getString("email");
        
    }
}
