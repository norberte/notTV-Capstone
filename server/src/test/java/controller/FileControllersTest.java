package controller;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import org.json.JSONArray;
import org.junit.Test;

public class FileControllersTest {
    @Test
    public void testListingFiles() throws IOException {
        URL url = new URL("http://localhost:1248/list/torrent");
        JSONArray json = Request2JSON.getJSON(url);
        
        assertTrue(json.length() > 0);
    }
    /*
    @Test
    public void testFetchingTorrentFile() throws IOException {
        URL url = new URL("http://localhost:1248/get/torrent/75.torrent");
        JSONArray json = Request2JSON.getJSON(url);
        
        assertTrue(json.length() > 0);
    }
    */
}
