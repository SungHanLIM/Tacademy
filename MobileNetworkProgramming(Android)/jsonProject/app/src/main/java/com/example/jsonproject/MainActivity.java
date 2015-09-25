package com.example.jsonproject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	private String jString = "";

	Melon melon;
	MelonAdapter adapter = null;
	ListView list = null;
	TextView tv;

	ProgressDialog pDialog = null;
	View.OnClickListener bHandler = new View.OnClickListener() {		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.button1 :
				parse();
				break;
			}			
		}
	};	
	//1 분간 미리 듣기 실행 ( 멜론 설치되어 있는 경우 실행)
	AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int position,
				long id) {
			String menuId = melon.getMenuId() + "";			
			String cid = melon.getSongs().get(position).getSongId() + "";			
			String url = "melonapp://play?ctype=1&cid="+cid+"&menuid="+menuId;			
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));  //Melon Intent 연결
			try{
				startActivity(intent);		
			}catch(ActivityNotFoundException e ){
				Log.v(TAG, "Melon 어플 설치 안됨 " + e);
			}
		}		
	};
	class ReadThread extends Thread{
		HttpGet get = null;
		HttpClient httpClient = null;
		HttpResponse response;
		HttpEntity entity;
		StringBuffer sb = new StringBuffer();
		String str = "";
		Message msg = null;
		public void run(){			
			msg = handler.obtainMessage();
			try{
				httpClient = NetManager.getHttpClient();
				get = NetManager.getGet("http://apis.skplanetx.com/melon/charts/realtime?count=10&page=1&version=1");
				get.setHeader("x-skpop-userId", "gsarang100");
				get.setHeader("Accept-Language", "ko_KR");
				get.setHeader("Accept", "application/json");
				get.setHeader("appKey", "2af0c219-62ea-39c6-aba9-b2c629ac4fd8");  // header 데이터로 key 값 설정
				response = httpClient.execute(get);
				int code = response.getStatusLine().getStatusCode();
				Log.v(TAG, "code : " + code);
				switch(code){
				case 200 :
					str = EntityUtils.toString(response.getEntity());
					melon = JSONParser.getParser(str);  //json 파심
					adapter.setData(melon);
					msg.what =100;
					break;
				default :
					msg.what = 800;
					msg.arg1 = code;
					break;
				}				
				Log.v(TAG, str);
			}catch(Exception e){
				msg.what = 999;
				msg.obj = e.toString();
				Log.v(TAG, "로딩 오류 :" + e);
			}
			handler.sendMessage(msg);
		}
	}
	
	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			pDialog.cancel();
			switch(msg.what){
			case 100 :
				String tempStr = melon.getRankDay();
				tempStr = tempStr.substring(0, 4) + "년 "  + tempStr.substring(4,6) + "월" + tempStr.substring(6) + "일  ";
				tempStr +=  melon.getRankHour()  + "시";
				tv.setText(tempStr + "     P " + melon.getPage() +"/" + melon.getTotalPages());
				adapter.notifyDataSetChanged();
				break;
			case 900 :
				showToast("code error : " + msg.arg1);
				break;
			case 999 :
				showToast("오류 ㅣ " + msg.obj.toString());
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	void showToast(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
	void parse(){
		pDialog = ProgressDialog.show(this, "", "검색중...");
		new ReadThread().start();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.button1).setOnClickListener(bHandler);
		list = (ListView)findViewById(R.id.listView1);
		
		tv = (TextView)findViewById(R.id.title);
		melon = new Melon();
		adapter = new MelonAdapter(this, R.layout.melonitem, melon);
		
		list.setOnItemClickListener(itemListener);
		
		list.setAdapter(adapter);
	}
}
