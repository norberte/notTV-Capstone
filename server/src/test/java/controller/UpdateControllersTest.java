package controller;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class UpdateControllersTest {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    public void testSubscribe() throws IOException {
        String url = "http://localhost:1248/update/subscribe";
        
        List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
        urlParameters.add(new BasicNameValuePair("author", "-1"));
        urlParameters.add(new BasicNameValuePair("subscriber", "1"));
        
        boolean success = Request2JSON.sendPost_returnBoolean(url, urlParameters);
        assertTrue(success);
    }
    
    @Test
    public void testUnsubscribe() throws IOException {
        String url = "http://localhost:1248/update/unsubscribe";
        
        List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
        urlParameters.add(new BasicNameValuePair("author", "-1"));
        urlParameters.add(new BasicNameValuePair("subscriber", "1"));
        
        boolean success = Request2JSON.sendPost_returnBoolean(url, urlParameters);
        assertTrue(success);
    }
    
    @Test
    public void testReport() throws IOException {
        String url = "http://localhost:1248/update/report";

        List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
        urlParameters.add(new BasicNameValuePair("videoId", "1"));
        urlParameters.add(new BasicNameValuePair("reportText", "test reporting"));
        
        int responseCode = Request2JSON.sendPost(url, urlParameters);
        
        assertTrue(responseCode == 200);
    }
    
    @Test
    public void testDeleteReport() throws IOException {
        String url = "http://localhost:1248/update/deleteReport";

        List<BasicNameValuePair> urlParameters = new ArrayList<BasicNameValuePair>();
        urlParameters.add(new BasicNameValuePair("videoId", "1"));
        urlParameters.add(new BasicNameValuePair("userId", "1"));
        
        int responseCode = Request2JSON.sendPost(url, urlParameters);
        
        assertTrue(responseCode == 200);
    }
}
