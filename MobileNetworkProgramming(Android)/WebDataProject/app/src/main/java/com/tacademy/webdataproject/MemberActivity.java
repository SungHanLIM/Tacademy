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

import com.tacademy.webdataproject.LoginActivity.LoginThread;

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

public class MemberActivity extends Activity {
	private static final String TAG= "MainActivity";
	EditText etMemberId, etMemberPw, etMemberName, etMemberTel, etMemberAddress, etMemberComment;
	ProgressDialog dialog;
	
	boolean isIDCheck = false;
	View.OnClickListener bHandler = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnInsert :
				if(isIDCheck){
					dialog = ProgressDialog.show(MemberActivity.this, "", "회원가입중....");
					new InsertThread().start();
				}else{
					showToast("ID 중복 체크 검사 후 회원가입을 해주세요");
				}
				break;
			case R.id.btnCheck :
				if(etMemberId.toString().length() >= 1){
					dialog = ProgressDialog.show(MemberActivity.this, "", "ID 중복 체크중....");
					new IDCheckThread().start();
				}else{
					showToast("ID를 입력하신후 ID체크 버튼을 눌러 주세요");
				}
				break;
			}
			
		}
		
	};
	
	String data = "";
	class IDCheckThread extends Thread{
		public void run(){
			String url = ServerUtil.SERVER_URL + "member";
			
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>() ;   
			nameValue.add( new BasicNameValuePair( "action", "check" ) ) ; 
			nameValue.add( new BasicNameValuePair( "id", etMemberId.getText().toString() ) ) ;   
			
			
			HttpClient client = NetManager.getHttpClient();
			
			HttpPost post = NetManager.getPost(url);
			
			HttpResponse response = null;
			BufferedReader br = null;
			StringBuffer sb = null;
			
			String line = "";
			try{
				Log.v(TAG, "process");
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValue, "UTF-8");
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
						sb.append(line + "\n");
					}
					Log.v(TAG, "process msg : " + sb);
					data = sb.toString();
					handler.sendEmptyMessage(9);
					break;
				default :
					handler.sendEmptyMessage(1);
				}
				
			}catch(Exception e){
				Log.v(TAG, "google error : " + e);
				handler.sendEmptyMessage(2);
			}finally{
				try{
					br.close();
				}catch(Exception e){}
			}
		}
	}
	class InsertThread extends Thread{
		public void run(){
			String url = ServerUtil.SERVER_URL + "member";
			
			List<NameValuePair> nameValue = new ArrayList<NameValuePair>() ;   
			nameValue.add( new BasicNameValuePair( "action", "insert" ) ) ; 
			nameValue.add( new BasicNameValuePair( "id", etMemberId.getText().toString() ) ) ;   
			nameValue.add( new BasicNameValuePair( "pw", etMemberPw.getText().toString() ) ) ;   
			nameValue.add( new BasicNameValuePair( "name", etMemberName.getText().toString() ) ) ;   
			nameValue.add( new BasicNameValuePair( "tel", etMemberTel.getText().toString() ) ) ;   
			nameValue.add( new BasicNameValuePair( "address", etMemberAddress.getText().toString() ) ) ;   
			nameValue.add( new BasicNameValuePair( "comment", etMemberComment.getText().toString() ) ) ;   
			
			
			HttpClient client = NetManager.getHttpClient();
			
			HttpPost post = NetManager.getPost(url);
			
			HttpResponse response = null;
			BufferedReader br = null;
			StringBuffer sb = null;
			
			String line = "";
			try{
				Log.v(TAG, "process");
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValue, "UTF-8");
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
						sb.append(line + "\n");
					}
					Log.v(TAG, "process msg : " + sb);
					data = sb.toString();
					handler.sendEmptyMessage(0);
					break;
				default :
					handler.sendEmptyMessage(1);
				}
				
			}catch(Exception e){
				Log.v(TAG, "google error : " + e);
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
					showToast(data + "님 회원 가입을 환영함");
					Intent intent = new Intent(MemberActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
				}else{
					showToast("회원 가입 다시해주세요");
				}
				break;
			case 9 :
				if(doCheck()){
					isIDCheck = true;
					showToast("ID 사용이 가능합니다.");
				}else{
					isIDCheck = false;
					showToast("이미 사용중인 ID입니다. 다른 ID를 사용하세요");
					etMemberId.requestFocus();
				}
			}
		}		
	};
	void showToast(String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
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
	
	    setContentView(R.layout.member);
	    etMemberId = (EditText)findViewById(R.id.etMemId);
	    etMemberPw = (EditText)findViewById(R.id.etMemPw);
	    etMemberName = (EditText)findViewById(R.id.etMemberName);
	    etMemberTel = (EditText)findViewById(R.id.etMemTel);
	    etMemberAddress = (EditText)findViewById(R.id.etMemAddress);
	    etMemberComment = (EditText)findViewById(R.id.etMemComment);
	    
	    findViewById(R.id.btnInsert).setOnClickListener(bHandler);
	    findViewById(R.id.btnCheck).setOnClickListener(bHandler);
	}

}
