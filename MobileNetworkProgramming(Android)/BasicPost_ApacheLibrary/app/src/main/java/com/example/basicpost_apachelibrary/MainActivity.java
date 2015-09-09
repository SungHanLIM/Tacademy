package com.example.basicpost_apachelibrary;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static private final String TAG = "SimplePOST-Sample";

    private EditText mUrl;
    private EditText mTitle;
    private EditText mDirector;
    private WebView mWebView;

    private HttpClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUrl = (EditText)findViewById(R.id.serverUrl);
        mTitle = (EditText)findViewById(R.id.title);
        mDirector = (EditText)findViewById(R.id.director);
        mWebView = (WebView)findViewById(R.id.webView);

        mClient = new DefaultHttpClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    // 요청 보내기
    public void sendRequest(View v) {
        new NetworkThread().start();
    }

    // 웹뷰에 목록 출력
    void refreshList() {
        Log.d(TAG, "Refresh WebView");
        String urlStr = mUrl.getText().toString();
        mWebView.loadUrl(urlStr);
    }

    Handler handler = new Handler();

    class NetworkThread extends Thread {
        @Override
        public void run() {
            try {
                // POST 요청 메세지 객체 생성
                String url = mUrl.getText().toString();
                HttpPost post = new HttpPost(url);

                // 헤더 작성
                post.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                // Post 메소드는 매세지 엔티티로 데이터 전송
                String title = mTitle.getText().toString();
                String director = mDirector.getText().toString();

                // 메세지 바디
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("title", title));
                params.add(new BasicNameValuePair("director", director));
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
                post.setEntity(entity);

                StringEntity stringEntity = new StringEntity("Hello");

                // 요청 실행
                HttpResponse response = mClient.execute(post);

                // 응답
                if ( response.getStatusLine().getStatusCode() == 200 ) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            refreshList();
                        }
                    });
                }
                else {
                    Log.e(TAG, "Response : " + response.getStatusLine().toString());
                }
            } catch (IOException e) {
                Log.e(TAG, "Exception", e);
                e.printStackTrace();
            }
        }
    }
}