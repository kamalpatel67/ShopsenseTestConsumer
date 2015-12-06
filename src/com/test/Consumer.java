package com.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Consumer {
	
	public static void main(String args[]) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("Beans.xml");
		DAO dao = (DAO) context.getBean("dao");
		DefaultHttpClient df = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://localhost:8080/ShopsenseTestProducer/restwebservice/data/items");
		httpGet.addHeader("accept", "application/json");
		HttpResponse response;
		JSONArray jsonArray = null;
		try {
			response = df.execute(httpGet);
			// System.out.println(response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 200) {
				BufferedReader bf = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line = null;
				while ((line = bf.readLine()) != null) {
					System.out.println(line);
					jsonArray = new JSONArray(line);
				}				
				
				if(jsonArray != null){
					System.out.println(jsonArray.length());
					String sql = "insert into shopsense_test(item_name,shade_name,size,quantity,mrp) values(?,?,?,?,?)";
                    dao.prepare(sql);
                    for (int i = 0; i < jsonArray.length(); i++) {
	                    JSONObject items = jsonArray.getJSONObject(i);
	                    String item_name=items.optString("item_name");
	                    System.out.println(item_name);
	                    String shade_name=items.optString("shade_name");
	                    System.out.println(shade_name);
	                    String mrp=items.optString("mrp");
	                    System.out.println(mrp);
	                    String size_quantity=items.optString("size_quantity");
	                    System.out.println(size_quantity);
	                    JSONArray jsonArray_size_quantity=new JSONArray(size_quantity);
	                    System.out.println(jsonArray_size_quantity.length());
	                    dao.setString(1, item_name);
	                    dao.setString(2, shade_name);
	                    dao.setString(5, mrp);
	                    for (int j = 0; j < jsonArray_size_quantity.length(); j++) {
	                    	JSONObject items_size_quantity = jsonArray_size_quantity.getJSONObject(j);
	                    	String quantity=items_size_quantity.optString("quantity");
	                    	System.out.println(quantity);
	                    	String size=items_size_quantity.optString("size");
	                    	System.out.println(size);
	                    	dao.setString(3, size);
		                    dao.setString(4, quantity);
		                    dao.insert();
	                    }
					}
				}
				dao.close();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
