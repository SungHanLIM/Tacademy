package com.tacademy.webdataproject;

import java.nio.channels.Channel;
import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class MyProoductHandler extends DefaultHandler {

	private ArrayList<MyItem> data;
	private MyItem item;
	private String tName;
	boolean flag = false;
	public  ArrayList<MyItem> getData(){
		return data;
	}
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		String str = new String(ch, start, length);
//		Log.v("Handler", "str : " + str);
		if(tName.equals("title")){
			item.setTitle(str);
		}else if(tName.equals("count")){
			item.setCount(str);
		}else if(tName.equals("price")){
			item.setPrice(str);
		}else if(tName.equals("image")){
			item.setImage(str);
		}else if(tName.equals("category")){
			item.setCategory(str);
		}
	}
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if(localName.equals("item")){
			data.add(item);
			Log.v("Handler", "item 추가 " );
		}
		tName = "";
	}
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		if(localName.equals("pList")){
			data = new  ArrayList<MyItem>();
		}else if(localName.equals("item")){
			item = new MyItem();
			Log.v("Handler", "item 삭제 " );
		}
		tName = localName;
	}
}
