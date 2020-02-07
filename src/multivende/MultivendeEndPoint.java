/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multivende;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author cfuen
 */
@WebService(endpointInterface = "multivende.MultivendeWS")
public class MultivendeEndPoint {

    private static String email, password;
    private static String token, merchant_id;

    public String cargaStock(@WebParam(name = "productos") ArrayList<String> productos, @WebParam(name = "cantidad") ArrayList<String> cantidad) throws UnirestException, IOException {

        try {
            getProperties();
            token = getToken();
            merchant_id = getMerchantid(token);
            String warehouse = getWareHousesID("TEST");
            String[] productosArray = productos.toArray(new String[productos.size()]);
            String[] cantidadArray = cantidad.toArray(new String[cantidad.size()]);
            System.out.println(productosArray[0]);
            System.out.println(cantidadArray[0]);
            
            String[][] productosMatriz = {
                productosArray,
                cantidadArray
            };
            System.out.println(productosMatriz[0][0]);
            System.out.println(productosMatriz[0][1]);
            //String[][] productos = {{"TEST_SKU", "2"}, {"TEST_SKU_2", "4"}, {"222", "4"}};

            //System.out.println("warehouse_id :" + warehouse);
            //String[] sku = {"4627254789206", "4265249487760", "5531295136143"};
            //String[][] array = {{"1","1"},{"1","1"}};
            //String[][] productsCode = getProductsCode(productos);
            return setUpdateStock(productosMatriz, warehouse);
        } catch (Exception e) {
            System.out.println("Error cargaStock" + e);
        }
        return "OK";
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
            System.out.println("Error getProperties: " + e);
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
            System.out.println("Error getMerchantid" + e);
        }
        return merchant_id;

    }

    public static String[][] getProductsCode(String[][] productos) throws UnirestException, IOException {
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
                JSONObject job = jsonMainArr.getJSONObject(i);
                JSONArray jsonProductVersions = job.getJSONArray("ProductVersions");
                for (int j = 0; j < jsonProductVersions.length(); j++) {
                    for (int k = 0; k < productos.length; k++) {
                        if (jsonMainArr.getJSONObject(i).get("code").toString().equals(productos[k][0])) {
                            productos[k][0] = jsonProductVersions.getJSONObject(j).get("code").toString();
                        }
                    }
                }
            }
            return productos;
        } catch (Exception e) {
            System.out.println("Error  getProductsCode  : " + e);
        }
        return null;
    }

    public static String getWareHousesID(String name) throws UnirestException, IOException {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest.get("http://app.multivende.com/api/m/" + merchant_id + "/stores-and-warehouses/p/{{page}}")
                .header("accept", "application/json")
                .header("content-type", "application/json")
                .header("Authorization", "Bearer " + token)
                .asString();
        try {
            JSONObject jo = new JSONObject(response.getBody());
            //System.out.println(jo.toString(4));
            JSONArray jsonMainArr = jo.getJSONArray("entries");
            for (int i = 0; i < jsonMainArr.length(); i++) {
                if (jsonMainArr.getJSONObject(i).get("name").toString().equals(name)) {
                    return jsonMainArr.getJSONObject(i).get("_id").toString();
                }
            }
        } catch (Exception e) {
            System.out.println("Error getWareHousesID" + e);
        }
        return null;
    }

    public static String setUpdateStock(String[][] productos, String warehouse_id) throws UnirestException {
        Unirest.setTimeouts(0, 0);
        StringBuilder body = new StringBuilder();
        StringBuilder sb = new StringBuilder();
        body.append("[");
        for (int i = 0; i < productos.length; i++) {
            if (productos.length - 1 == i) {
                body.append("\r\n  {\r\n    \"code\": \"" + productos[i][0] + "\",\r\n     \"amount\": " + productos[i][1] + "\r\n  }");
            } else {
                body.append("\r\n  {\r\n    \"code\": \"" + productos[i][0] + "\",\r\n     \"amount\": " + productos[i][1] + "\r\n  },");
            }

        }
        body.append("\r\n]");
        System.out.println(body);
        HttpResponse<String> response = Unirest.post("http://app.multivende.com/api/product-stocks/stores-and-warehouses/" + warehouse_id + "/bulk-set")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .body(body.toString())
                .asString();
        try {
            JSONObject jo = new JSONObject(response);
            System.out.println(response.getStatus());
            //System.out.println(jo.toString(5));
            JSONArray jsonMainArr = new JSONArray(response.getBody().toString());
            for (int i = 0; i < jsonMainArr.length(); i++) {
                //System.out.println(Boolean.valueOf(jsonMainArr.getJSONObject(i).get("success").toString()));
                if (Boolean.valueOf(jsonMainArr.getJSONObject(i).get("success").toString())) {
                    System.out.println("Sincronizado: " + jsonMainArr.getJSONObject(i).get("code"));
                    System.out.println("Estado: " + jsonMainArr.getJSONObject(i).get("success"));
                    sb.append("Sincronizado: " + jsonMainArr.getJSONObject(i).get("code"));
                    sb.append("Sincronizado: " + jsonMainArr.getJSONObject(i).get("success"));
                } else {
                    System.out.println("Sincronizado: " + jsonMainArr.getJSONObject(i).get("code"));
                    System.out.println("Estado: " + jsonMainArr.getJSONObject(i).get("error"));
                    sb.append("Sincronizado: " + jsonMainArr.getJSONObject(i).get("code"));
                    sb.append("Sincronizado: " + jsonMainArr.getJSONObject(i).get("error"));
                }
            }
            return sb.toString();
        } catch (Exception e) {
            System.out.println("Error setUpdateStock" + e);
        }
        return null;
    }
}
