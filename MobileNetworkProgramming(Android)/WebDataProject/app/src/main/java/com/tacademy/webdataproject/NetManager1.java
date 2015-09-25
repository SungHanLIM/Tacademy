package com.tacademy.webdataproject;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class NetManager1 {


	public static HttpClient getHttpClient(){
		HttpClient httpclient = null;
		HttpParams hp = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(hp,10000);
		HttpConnectionParams.setSoTimeout(hp,10000) ;    
		httpclient = new DefaultHttpClient(hp);

		return httpclient ;
	}
	public static HttpGet getGet(String url){ 	//HttpGet

		HttpGet httpGet =  new HttpGet( url );
		httpGet.setHeader("Connection","Keep-Alive"); 
		httpGet.setHeader("Accept","application/xml"); 
		httpGet.setHeader("Content-Type","application/xml"); 
		httpGet.setHeader("User-Agent","ANDROID");
		httpGet.setHeader("Pragma","no-cache"); 
		httpGet.setHeader("Cache-Control","no-cache,must-reval!idate"); 
		httpGet.setHeader("Expires","0"); 
		return httpGet;
	}
	//HttpPost xml 
	public static HttpPost getPost1(String url){

		HttpPost post =  new HttpPost( url );
		post.setHeader("Connection","Keep-Alive"); 
		post.setHeader("Accept","application/xml"); 
		post.setHeader("Content-Type","application/xml"); 
		post.setHeader("User-Agent","ANDROID"); 
		return post;
	}
	//HttpPost
	public static HttpPost getPost(String url){

		HttpPost post =  new HttpPost( url );
		post.setHeader("User-Agent","ANDROID"); 
		return post;
	}

}
