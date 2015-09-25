package com.tacademy.webdataproject;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class JSONProductParser {
	private static final String TAG = "MainActivity";
	public static ArrayList<MyItem> parser(String jsonStr){
		 ArrayList<MyItem> data = new ArrayList<MyItem>();
		JSONArray arr = null;
		JSONObject json = null;
		MyItem item = null;
		JSONObject temp = null;
		try{
			json = new JSONObject(jsonStr);
			arr = json.getJSONArray("pList");
			for(int i = 0; i < arr.length(); i++){
				item = new MyItem();
				temp = arr.getJSONObject(i);
				item.setCategory(temp.getString("category"));
				item.setCount(temp.getString("count"));
				item.setImage(temp.getString("image"));
				item.setPrice(temp.getInt("price") + "");
				item.setTitle(temp.getString("title"));
				data.add(item);
			}
		}catch(Exception e){
			Log.v(TAG, "json error : " + e);
		}
		return data;
	}
}
