package controller;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import spring.view.AccountForm;
import spring.view.UserTesting;
import spring.view.VideoForm;

public class PostControllersTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();
    
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
                "videoForm=" + URLEncoder.encode(Request2JSON.toString(vidInfo), "UTF-8") + 
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
        
        String url = "http://localhost:1248/upload/accountSubmit";
        List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
        urlParameters.add(new BasicNameValuePair("videoForm", Request2JSON.toString(accountForm)));
    
        int responseCode = Request2JSON.sendPost(url, urlParameters);
        
        // check for changes being made 
        String sql = "Select password, email From nottv_user Where username = 'testUser';";
        List<UserTesting> results = Collections.emptyList();
        try {
            results = jdbcTemplate.query(sql, (rs, row)-> new UserTesting(rs.getString("password"), rs.getString("email")));
        } catch(NullPointerException ex) {
            if(results.size() > 0) {
                UserTesting ut = results.get(0);
                String passwordHash = ut.getHashedPassword();
                String email = ut.getEmail();
                
                assertTrue(encoder.matches("pass", passwordHash));
                assertTrue(email.equals("testing@test.com"));
            } else {
                assertTrue(responseCode == 415);
            }
        }
    }
}
