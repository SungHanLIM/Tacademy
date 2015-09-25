package com.tacademy.webdataproject;



import java.net.URLEncoder;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static final String TAG = "MainActivity";
	
    ListView list;
    ArrayList<MyItem> data = null;
    MyItem itme = null;
    ProgressDialog dialog;
    MyAdapter adapter = null;
    boolean flag;
    String url1 = "";
    String url2 = "";
    String url;
    String category = "0";
    String type = "0";
    String key ="";
    
    EditText etKey = null;
    TextView txtName = null;
	View.OnClickListener bHandler = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnSearch :
				dialog = ProgressDialog.show(MainActivity.this, "", "상품검색중....");
				new ReadThread().start();
				break;
			case R.id.radio0 :
				category = "0";
				break;
			case R.id.radio1 :
				category = "1";
				break;
			case R.id.radio2 :
				category = "2";
				break;
			case R.id.radio3 :
				type = "0";
				break;
			case R.id.radio4 :
				type = "1";
				break;
			}
			
		}
	};
	class ReadThread extends Thread{
		public void run(){
			HttpGet get = null;
			HttpClient client = null;
			HttpResponse response = null;
			HttpEntity entity = null;
			url1 = ServerUtil.SERVER_URL +  "product?key=";
			try{
				
				url2 = "&category="+category +"&type="+type;
				key = etKey.getText().toString();
				if(key.length() == 0){
					key = "empty";
				}
				url = url1 + URLEncoder.encode(key, "UTF-8") + url2;
				client = NetManager.getHttpClient();
				Log.v(TAG, "url : " + url);
				get = NetManager1.getGet(url);
				response = client.execute(get);
				int code = response.getStatusLine().getStatusCode();
				switch(code){
				case 200 :
					if(type.equals("1")){ // XML 파싱
						data = MyProductParser.parser(response.getEntity());
					}else{
						// JSON 파싱
						String json = EntityUtils.toString(response.getEntity());
						data = JSONProductParser.parser(json);
					}
					adapter.setData(data);
					Log.v(TAG, "data size :  " + data.size());
					break;
				}
			}catch(Exception e){
				Log.v(TAG, "로딩에러 : " + e);
			}
			handler.sendEmptyMessage(0);
		}
	}
	
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			dialog.dismiss();
			adapter.notifyDataSetChanged();
		}		
	};
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        etKey = (EditText)findViewById(R.id.etKey);
        
        txtName = (TextView)findViewById(R.id.txtName);
        		
        
        findViewById(R.id.btnSearch).setOnClickListener(bHandler);
        findViewById(R.id.radio0).setOnClickListener(bHandler);
        findViewById(R.id.radio1).setOnClickListener(bHandler);
        findViewById(R.id.radio2).setOnClickListener(bHandler);
        
        list = (ListView)findViewById(R.id.wList);
        data = new ArrayList<MyItem>();
        adapter = new MyAdapter(this, R.layout.item, data);
        list.setAdapter(adapter);
        
        Intent intent = getIntent();
        txtName.setText(intent.getStringExtra("name") + "님 계정");
    }

}
