package controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Request2JSON {
   // this class is full with all kinds of helper functions needed for testing
    
    public static String toString(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }
    
    public static int sendPost(String url,List<BasicNameValuePair> urlParameters) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", "Mozilla/5.0");
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " + 
                                    response.getStatusLine().getStatusCode());
        /*
        JSONArray json = null;
        try {
            BufferedReader streamReader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            
            json = new JSONArray(responseStrBuilder.toString());
            streamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
        */
        return response.getStatusLine().getStatusCode();
    }
    
    public static boolean sendPost_returnBoolean(String url,List<BasicNameValuePair> urlParameters) throws IOException {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);

        // add header
        post.setHeader("User-Agent", "Mozilla/5.0");
        post.setEntity(new UrlEncodedFormEntity(urlParameters));

        HttpResponse response = client.execute(post);
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + post.getEntity());
        System.out.println("Response Code : " + 
                                    response.getStatusLine().getStatusCode());
        
        boolean success = false;
        try {
            BufferedReader streamReader = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            
            success = Boolean.parseBoolean(responseStrBuilder.toString());
            streamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return success;
    }

    public static boolean getJSON_returnBoolean(URL url) {
        URLConnection conn;
        InputStream is  = null;
        try {
            conn = url.openConnection();
            is = conn.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        boolean subscribed = false;
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            
            subscribed = Boolean.parseBoolean(responseStrBuilder.toString());
            streamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return subscribed;
    }
    
    public static JSONObject getJSONObject(URL url) {
        URLConnection conn;
        InputStream is  = null;
        try {
            conn = url.openConnection();
            is = conn.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        JSONObject json = null;
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            
            json = new JSONObject(responseStrBuilder.toString());
            streamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static String getJSON_String(URL url) {
        URLConnection conn;
        InputStream is  = null;
        try {
            conn = url.openConnection();
            is = conn.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        String result = "";
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            
            result = responseStrBuilder.toString();
            streamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static JSONArray getJSON(URL url) {
        URLConnection conn;
        InputStream is  = null;
        try {
            conn = url.openConnection();
            is = conn.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        JSONArray json = null;
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            
            json = new JSONArray(responseStrBuilder.toString());
            streamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    
    public static JSONArray sendGet(URL url, String urlParameters) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", 
            "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", 
            Integer.toString(urlParameters.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");  

        connection.setUseCaches(false);
        connection.setDoOutput(true);

        //Send request
        DataOutputStream wr;
        try {
            wr = new DataOutputStream (
                connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        InputStream is = connection.getInputStream();
        JSONArray json = null;
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            
            json = new JSONArray(responseStrBuilder.toString());
            streamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }
    
    public static int getInt(URL url, String urlParameters, String requestType, boolean flag) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestType);
        connection.setRequestProperty("Content-Type", 
            "application/x-www-form-urlencoded");
        connection.setRequestProperty("Content-Length", 
            Integer.toString(urlParameters.getBytes().length));
        connection.setRequestProperty("Content-Language", "en-US");  

        connection.setUseCaches(false);
        connection.setDoOutput(true);

        //Send request
        DataOutputStream wr;
        try {
            wr = new DataOutputStream (
                connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        File f = new File("emptyFile.png");
        f.createNewFile();
        InputStream is = new FileInputStream(f);
        int jsonValue = 0;
        
        if(flag)
            is = connection.getInputStream();
        
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            if(flag)
                jsonValue = Integer.parseInt(responseStrBuilder.toString());
            streamReader.close();
        } catch (IOException e) {
          
        }
        
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        } 
        return jsonValue;
    }
    
    public static int getInt(URL url) {
        URLConnection conn;
        InputStream is  = null;
        try {
            conn = url.openConnection();
            is = conn.getInputStream();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        int jsonValue = 0;
        try {
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            
            jsonValue = Integer.parseInt(responseStrBuilder.toString());
            streamReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonValue;
    }
}
