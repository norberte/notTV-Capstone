package controller;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import spring.InfoController;
import spring.view.CategoryType;

import org.codehaus.jackson.JsonParser;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import controller.Request2JSON;

public class InfoControllersTest {
    private static final Logger log = LoggerFactory.getLogger(InfoControllersTest.class);

    @Test
    public void testGettingCategories() throws IOException {
        // check to see if Genre is could be returned as a Category
        
        URL url = new URL("http://localhost:1248/info/categories");
        JSONArray json = Request2JSON.getJSON(url);
        
        String categoryValue = "";
        for (int i = 0; i < json.length(); i++) {
            JSONObject childJSONObject;
            try {
                childJSONObject = json.getJSONObject(i);
                int id = childJSONObject.getInt("id");
                if(id == 2) { 
                    categoryValue = childJSONObject.getString("name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
        }

        assertTrue(categoryValue.equals("Genre"));
    }
    
    @Test
    public void testGettingCategoryTypeId() throws IOException {
        // check to see if a new unique id is returned each time when making a get request to categoryTypeId
        URL url = new URL("http://localhost:1248/info/category-type-id");
        int childJSON1 = Request2JSON.getInt(url);
        int childJSON2 = Request2JSON.getInt(url);
        assertNotSame(childJSON1,childJSON2);
    }
    
    @Test
    public void testGettingCategoryValueId() throws IOException {
        // check to see if a new unique id is returned each time when making a get request to categoryValueId
        URL url = new URL("http://localhost:1248/info/category-value-id");
        int childJSON1 = Request2JSON.getInt(url);
        int childJSON2 = Request2JSON.getInt(url);
        assertNotSame(childJSON1,childJSON2);
    }
    /*
    @Test
    public void testGettingPlaylists() throws IOException {
        // check to see if Genre is could be returned as a Category
        
        URL url = new URL("http://localhost:1248/info/playlists");
        String urlParameters =
                "userid=" + URLEncoder.encode("1", "UTF-8");
        JSONArray json = Request2JSON.getJSON(url, urlParameters, "GET");
        
        String playlistName = "";
        for (int i = 0; i < json.length(); i++) {
            JSONObject childJSONObject;
            try {
                childJSONObject = json.getJSONObject(i);
                int id = childJSONObject.getInt("id");
                playlistName = childJSONObject.getString("name");
               
            } catch (JSONException e) {
                e.printStackTrace();
            }
            
        }

        assertTrue(playlistName.equals("Genre"));
    }
    */
}

