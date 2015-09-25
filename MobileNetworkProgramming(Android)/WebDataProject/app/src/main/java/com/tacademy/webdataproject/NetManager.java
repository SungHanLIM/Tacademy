package com.tacademy.webdataproject;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class NetManager {
	static HttpClient httpclient = null;
	//HttpClien
	public static HttpClient getHttpClient(){
		if(httpclient == null){
			//timeout 
			HttpParams hp = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(hp,10000);
			HttpConnectionParams.setSoTimeout(hp,10000) ;    
			httpclient = new DefaultHttpClient(hp);
		}
		return httpclient ;
	}
	public static HttpGet getGet(String url){ 	//HttpGet ��������޼ҵ�
		if(httpclient == null){	httpclient =  getHttpClient();}
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
	//HttpPost xml ���� ��ü �Ѱ��ִ� �޼ҵ�
	public static HttpPost getPost1(String url){
		if(httpclient == null){
			httpclient =  getHttpClient();
		}
		HttpPost post =  new HttpPost( url );
		post.setHeader("Connection","Keep-Alive"); 
		post.setHeader("Accept","application/xml"); 
		post.setHeader("Content-Type","application/xml"); 
		post.setHeader("User-Agent","ANDROID"); 
		return post;
	}
	//HttpPost ��ü �Ѱ��ִ� �޼ҵ�
	public static HttpPost getPost(String url){
		if(httpclient == null){
			httpclient =  getHttpClient();
		}
		HttpPost post =  new HttpPost( url );
		post.setHeader("User-Agent","ANDROID"); 
		return post;
	}

}
