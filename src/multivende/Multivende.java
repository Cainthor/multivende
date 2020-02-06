/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multivende;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author cfuen
 */
public class Multivende {

    private static String email, password;
    private static String token, merchant_id;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        getProperties();

        try {
            token = getToken();
            merchant_id = getMerchantid(token);
            System.out.println("Token :" + token);
            System.out.println("merchant_id :" + merchant_id);
            getProducts();
            getWareHouses();

        } catch (Exception e) {
            System.out.println("Error" + e);
        }

    }

    public static String getToken() throws UnirestException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.post("http://app.multivende.com/auth/local")
                .header("cache-control", "no-cache")
                .header("Content-Type", "application/json")
                .body("{\r\n  \"email\": \"" + email + "\",\r\n  \"password\": \"" + password + "\"\r\n}")
                .asString();
        JSONObject obj = new JSONObject(response.getBody());
        //System.out.println(obj.get("token"));
        return obj.get("token").toString();
    }

    public static void getProperties() {
        try {
            Properties prop = new Properties();
            FileInputStream fis = new FileInputStream("./config.properties");
            prop.load(fis);
            email = prop.getProperty("email");
            password = prop.getProperty("password");
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    public static String getMerchantid(String token) throws UnirestException, IOException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("http://app.multivende.com/api/users/me")
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .asString();
        try {
            JSONObject jo = new JSONObject(response.getBody());
            JSONArray jsonMainArr = jo.getJSONArray("Merchants");
            for (int i = 0; i < jsonMainArr.length(); i++) {
                merchant_id = jsonMainArr.getJSONObject(i).get("_id").toString();
            }
            return merchant_id;
        } catch (Exception e) {
            System.out.println("Error " + e);
        }
        return merchant_id;

    }

    public static String getProducts() throws UnirestException, IOException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("http://app.multivende.com/api/m/" + merchant_id + "/products/p/{{page}}")
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .asString();
        try {
            JSONObject jo = new JSONObject(response.getBody());
            //System.out.println(jo.toString(4));
            JSONArray jsonMainArr = jo.getJSONArray("entries");
            for (int i = 0; i < jsonMainArr.length(); i++) {
                System.out.println(jsonMainArr.getJSONObject(i).get("_id").toString());
                System.out.println(jsonMainArr.getJSONObject(i).get("code").toString());
            }
        } catch (Exception e) {
            System.out.println("Error " + e);
        }
        return merchant_id;

    }
        public static String getWareHouses() throws UnirestException, IOException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("http://app.multivende.com/api/m/" + merchant_id + "/stores-and-warehouses/p/{{page}}")
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .asString();
        try {
            JSONObject jo = new JSONObject(response.getBody());
            System.out.println(jo.toString(4));
            /*JSONArray jsonMainArr = jo.getJSONArray("entries");
            for (int i = 0; i < jsonMainArr.length(); i++) {
                System.out.println(jsonMainArr.getJSONObject(i).get("_id").toString());
                System.out.println(jsonMainArr.getJSONObject(i).get("code").toString());
            }*/
        } catch (Exception e) {
            System.out.println("Error " + e);
        }
        return merchant_id;

    }
       

}
