package controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import spring.view.AccountForm;
import spring.view.VideoForm;

public class Request2JSON {
   // this class is full with all kinds of helper functions needed for testing
    public static String videoFormToString(VideoForm object) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter stringEmp = new StringWriter();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.writeValue(stringEmp, object);
        return stringEmp.toString();
    }
    
    public static String accountFormToString(AccountForm object) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter stringEmp = new StringWriter();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.writeValue(stringEmp, object);
        return stringEmp.toString();
    }
    
    public static JSONArray getJSON(URL url, String urlParameters, String requestType) throws IOException {
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
