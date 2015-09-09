package com.example.basicpost;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    static private final String TAG = "SimplePOST";

    private EditText mUrl;
    private EditText mTitle;
    private EditText mDirector;
    private WebView mWebView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUrl = (EditText)findViewById(R.id.serverUrl);
        mTitle = (EditText)findViewById(R.id.title);
        mDirector = (EditText)findViewById(R.id.director);
        mWebView = (WebView)findViewById(R.id.webView);

        // 웹뷰로 목록 출력
        mWebView.setWebViewClient(new WebViewClient());

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
               String title = mTitle.getText().toString();
               String director = mDirector.getText().toString();
               String urlStr = mUrl.getText().toString();

               // 요청 보내기
               URL url = new URL(urlStr);
               HttpURLConnection conn = (HttpURLConnection) url.openConnection();

               // POST Methcd
               conn.setRequestMethod("POST");

               // Body, URLEncoded
               StringBuffer buffer = new StringBuffer();
               buffer.append("title=").append(URLEncoder.encode(title, "UTF-8"));
               buffer.append("&");
               buffer.append("director=").append(URLEncoder.encode(director, "UTF-8"));
               String bodyStr = buffer.toString();

               // 요청 메세지 헤더
               conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
               conn.setRequestProperty("Content-Length", String.valueOf(bodyStr.length()));
               conn.setDoOutput(true);
               OutputStream os = conn.getOutputStream();
               os.write(bodyStr.getBytes("UTF-8"));
               os.flush();
               os.close();

               // 응답 얻기
               int statusCode = conn.getResponseCode();
               Log.d(TAG, "Status Code : " + statusCode);

               handler.post(new Runnable() {
                   @Override
                   public void run() {
                       refreshList();
                   }
               });



           } catch (Exception e) {
               Log.e(TAG, "Excpetion", e);
               e.printStackTrace();
           }
       }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
