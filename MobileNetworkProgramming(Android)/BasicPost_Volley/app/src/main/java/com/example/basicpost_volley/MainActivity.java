package com.example.basicpost_volley;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    static private final String TAG = "SimplePOST-Sample";

    private EditText mUrl;
    private EditText mTitle;
    private EditText mDirector;
    private WebView mWebView;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUrl = (EditText)findViewById(R.id.serverUrl);
        mTitle = (EditText)findViewById(R.id.title);
        mDirector = (EditText)findViewById(R.id.director);
        mWebView = (WebView)findViewById(R.id.webView);

        queue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshList();
    }

    // 웹뷰에 목록 출력
    void refreshList() {
        Log.d(TAG, "Refresh WebView");
        String urlStr = mUrl.getText().toString();
        mWebView.loadUrl(urlStr);
    }

    // 요청 보내기
    public void sendRequest(View v) {
        String url = mUrl.getText().toString();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                refreshList();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error", error);
                NetworkResponse response = error.networkResponse;
                if ( response != null )
                    Log.e(TAG, "Error Response : " + response.statusCode);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // 바디 작성
                String title = mTitle.getText().toString();
                String director = mDirector.getText().toString();

                Map<String, String> params = new HashMap<>();
                params.put("title", title);
                params.put("director", director);

                return params;
            }

            @Override
            public String getBodyContentType() {
                // 컨텐트 타입
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        queue.add(request);
    }


}