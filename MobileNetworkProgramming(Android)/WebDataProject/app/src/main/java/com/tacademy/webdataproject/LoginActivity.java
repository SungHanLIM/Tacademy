package com.tacademy.webdataproject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private static final String TAG= "MainActivity";
	EditText etId, etPw;
	ProgressDialog dialog;
	View.OnClickListener bHandler = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnLogin :
				dialog = ProgressDialog.show(LoginActivity.this, "", "로그인중.....");
				new LoginThread().start();
				break;
			case R.id.btnMember :
				Intent intent = new Intent(LoginActivity.this, MemberActivity.class);
				startActivity(intent);
				finish();
				break;
			}
			
		}
		
	};
	String data = "";
	class LoginThread extends Thread{
		public void run(){
			String url = ServerUtil.SERVER_URL + "member";
			
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>() ;   
			nameValue.add( new BasicNameValuePair( "action", "login" ) ) ; 
			nameValue.add( new BasicNameValuePair( "id", etId.getText().toString() ) ) ;   
			nameValue.add( new BasicNameValuePair( "pw", etPw.getText().toString() ) ) ;   
			
			
			HttpClient client = NetManager1.getHttpClient();
			
			HttpPost post = NetManager1.getPost(url);
			
			HttpResponse response = null;
			BufferedReader br = null;
			StringBuffer sb = null;
			
			String line = "";
			try{
				Log.v(TAG, "process");
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValue);
				post.setEntity(entity);
				response = client.execute(post);
				int code = response.getStatusLine().getStatusCode();
				Log.v(TAG, "process code : " + code);
				switch(code){
				case 200 :
//					br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"euc-kr"));
					br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					sb = new StringBuffer();
					while((line = br.readLine())!= null){
						sb.append(line);
					}
					Log.v(TAG, "process msg : " + sb);
					data = sb.toString();
					handler.sendEmptyMessage(0);
					break;
				default :
					handler.sendEmptyMessage(1);
				}
				
			}catch(Exception e){
				Log.v(TAG, "login parser error : " + e);
				handler.sendEmptyMessage(2);
			}finally{
				try{
					br.close();
				}catch(Exception e){}
			}
		}
	}
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			dialog.dismiss();
			switch(msg.what){
			case 0 :
				if(doCheck()){
					Intent intent = new Intent(LoginActivity.this, MainActivity.class);
					intent.putExtra("name", data);
					startActivity(intent);
					finish();
				}else{
					showToast("아이디 암호가 다릅니다 다시 시도하세요");
				}
				break;
			}
		}		
	};
	void showToast(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
		etId.setText("");
		etPw.setText("");
	}
	boolean doCheck(){
		if(data.indexOf("fail") >-1){
			return false;
		}else{
			return true;
		}
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.login);
	    etPw = (EditText)findViewById(R.id.etPw);
	    etId = (EditText)findViewById(R.id.etId);
	    findViewById(R.id.btnLogin).setOnClickListener(bHandler);
	    findViewById(R.id.btnMember).setOnClickListener(bHandler);
	}

}
