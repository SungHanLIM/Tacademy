package com.tacademy.webdataproject;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.xml.sax.InputSource;

import android.util.Log;

public class MyProductParser {
	private static final String TAG = "MainActivity";
	static SAXParserFactory sFactory;
	static SAXParser parser;
	public static ArrayList<MyItem> parser(HttpEntity entity){
		 ArrayList<MyItem> data = null;
		InputSource is = null;
		MyProoductHandler handler = new MyProoductHandler();
		try{
			sFactory = SAXParserFactory.newInstance();
			parser = sFactory.newSAXParser();
			is = new InputSource(entity.getContent());
			parser.parse(is, handler);
			data = handler.getData();
		}catch(Exception e){
			Log.v(TAG, "parser error : " + e);
		}
		return data;
	}
}
