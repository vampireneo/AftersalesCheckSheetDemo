package hk.com.inchcape.aftersaleschecksheetdemo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by neochoi on 12/8/14.
 */
public class Json {
    public static JSONObject getJson(String url, String type){

        InputStream is = null;
        String result = "";
        JSONObject jsonObject = null;

        // HTTP
        try {
            HttpResponse response;
            HttpClient httpclient = new DefaultHttpClient(); // for port 80 requests!
            if (type.equals("POST")) {
                HttpPost httppost = new HttpPost(url);
                response = httpclient.execute(httppost);
            }
            else {
                HttpGet httpget = new HttpGet(url);
                response = httpclient.execute(httpget);
            }
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch(Exception e) {
            return null;
        }

        // Read response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch(Exception e) {
            return null;
        }

        // Convert string to object
        try {
            jsonObject = new JSONObject(result);
        } catch(JSONException e) {
            return null;
        }

        return jsonObject;

    }

    public static String getString(String url, String type){

        InputStream is = null;
        String result = "";
        JSONObject jsonObject = null;

        // HTTP
        try {
            HttpResponse response;
            HttpClient httpclient = new DefaultHttpClient(); // for port 80 requests!
            if (type.equals("POST")) {
                HttpPost httppost = new HttpPost(url);
                response = httpclient.execute(httppost);
            }
            else {
                HttpGet httpget = new HttpGet(url);
                response = httpclient.execute(httpget);
            }
            HttpEntity entity = response.getEntity();
            is = entity.getContent();
        } catch(Exception e) {
            return null;
        }

        // Read response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is,"utf-8"),8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch(Exception e) {
            return null;
        }

        return result;

    }
}
