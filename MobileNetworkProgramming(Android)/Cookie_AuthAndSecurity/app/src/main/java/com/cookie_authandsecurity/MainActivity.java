package com.cookie_authandsecurity;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "Cookie-Sample";
    RequestQueue requestQueue;

    private TextView visitInfoView;
    private WebView webview;
    private EditText address;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        visitInfoView = (TextView) findViewById(R.id.visitInfoView);
        address = (EditText) findViewById(R.id.serverAddress);

        // cookie manager
        CookieManager manager = new CookieManager();
        CookieHandler.setDefault(manager);

        // 발리 요청 큐
        requestQueue = Volley.newRequestQueue(this);

        // 웹뷰와 웹뷰 클라이언트
        webview = (WebView) findViewById(R.id.webview);
        WebViewClient client = new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                // 메모리 상의 쿠키를 저장소로 강제 동기화
                android.webkit.CookieManager webCookieManager = android.webkit.CookieManager.getInstance();
                String cookie = webCookieManager.getCookie(url);
                CookieSyncManager.getInstance().sync();

                // 웹뷰 요청에서 얻은 쿠키를 네이티브 쿠키와 동기화
                syncWebViewCookieToNative(url);
            }
        };
        webview.setWebViewClient(client);
    }

    // 웹뷰의 쿠키를 네이티브 쿠키로 동기화
    void syncWebViewCookieToNative(String url) {
        try {
            Log.d(TAG, "== syncWebViewCookieToNative ==");
            // 웹뷰의 쿠키 가져오기
            android.webkit.CookieManager webCookieManager = android.webkit.CookieManager.getInstance();
            String cookieHeader = webCookieManager.getCookie(url);

            Log.d(TAG, "cookieHeader : " + cookieHeader);
            String[] cookies = cookieHeader.split(";");

            URI uri = new URI(url);

            // 네이티브 쿠키 스토어에 쿠기 저장
            CookieManager cookieManager = (CookieManager)CookieManager.getDefault();
            CookieStore store = cookieManager.getCookieStore();

            for( int i = 0 ; i < cookies.length ; i++ ) {
                List<HttpCookie> parsed = HttpCookie.parse(cookies[i]);
                HttpCookie cookie = parsed.get(0);
                cookie.setDomain(uri.getHost());
                cookie.setPath("/");
                Log.d(TAG, "HttpCookie parse : " + cookie.toString());

                store.add(uri, cookie);
            }

            showNativeCookies(url);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }



    // 네이티브 쿠키를 웹뷰의 쿠키로 동기화
    void syncNativeCookieToWebView(String url) {
        try {
            CookieManager cookieManager = (CookieManager)CookieManager.getDefault();
            URI uri = new URI(url);
            List<HttpCookie> cookieList = cookieManager.getCookieStore().get(uri);

            String cookieStr = "";
            android.webkit.CookieManager webCookieManager = android.webkit.CookieManager.getInstance();
            for ( int i = 0 ; i < cookieList.size() ; i++ ) {
                HttpCookie cookie = cookieList.get(i);
                cookieStr += cookie.getName() + "=" + cookie.getValue() + "; ";
            }

            webCookieManager.setCookie(url, cookieStr);
            Log.d(TAG, "Setting new Cookie to WebView : " + cookieStr);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Error", e);
            e.printStackTrace();
        }
    }

    // 웹뷰로 로딩
    public void sendWebRequest(View v) {
        String urlStr = address.getText().toString();
        syncNativeCookieToWebView(urlStr);
        webview.loadUrl(urlStr);
    }

    // HttpUrlRequest를 이용한 요청
    public void sendBasicRequest(View v) {
        CookieThread cookieThread = new CookieThread();
        cookieThread.start();
    }

    // Volley를 이용한 요청
    public void sendVolleyRequest(View v) {
        String urlStr = address.getText().toString();
        // 응답 메세지는 JSON 형태
        JsonObjectRequest request = new JsonObjectRequest(urlStr, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    int visit = response.getInt("visit");
                    String since = response.getString("since");
                    String last = response.getString("last");
                    String info = "vist : " + visit + " last : " + last + " since : " + since;
                    visitInfoView.setText(info);
                    Log.d(TAG, info);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception", e);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Request Error", error);
            }
        });
        requestQueue.add(request);
    }

    class CookieThread extends Thread {
        @Override
        public void run() {
            try {
                String urlStr = address.getText().toString();
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // 응답 메세지의 쿠키를 콘솔에 출력
                String cookie = null;
                Map<String, List<String>> headers = conn.getHeaderFields();
                for ( String key : headers.keySet() ) {
                    if ( "set-cookie".equalsIgnoreCase(key)) { // 주의 - statusLine의 key는 null
                        cookie = headers.get("set-cookie").toString();
                        Log.d(TAG, "Response Cookie : " + cookie);
                    }
                }

                // JSON 분석
                InputStream is = conn.getInputStream();
                JsonReader reader = new JsonReader(new InputStreamReader(is));

                String visit = null, since = null, last = null;
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("visit")) {
                        visit = reader.nextString();
                    } else if (name.equals("since")) {
                        since = reader.nextString();
                    } else if (name.equals("last")) {
                        last = reader.nextString();
                    }
                }

                // 화면에 출력
                final String info = "vist : " + visit + " last : " + last + " since : " + since;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        visitInfoView.setText(info);
                    }
                });

                showNativeCookies(urlStr);
            } catch (Exception e) {
                Log.e(TAG, "Exception", e);
                e.printStackTrace();
            }
        }
    }

    // 쿠키 모두 지우기
    public void clearCookie(View v) {
        CookieManager manager = (CookieManager) CookieManager.getDefault();
        manager.getCookieStore().removeAll();

        android.webkit.CookieManager.getInstance().removeAllCookies(null);
    }


    // For Debugging
    public void showNativeCookies(String urlStr) {
        Log.d(TAG, "== showNativeCookies : " + urlStr);
        try {
            CookieManager manager = (CookieManager) CookieManager.getDefault();
            CookieStore store = manager.getCookieStore();
            Log.d(TAG, "Cookie store : " + store);
            URI uri = new URI(urlStr);
            Log.d(TAG, "URI : " + uri);
            List<URI> uriList = store.getURIs();
            for(int i = 0 ; i < uriList.size() ; i++) {
                Log.d(TAG, "URI : " + uriList.get(i));
            }

            List<HttpCookie> cookies = store.get(uri);
            for ( int i = 0 ; i < cookies.size() ; i++) {
                HttpCookie item = cookies.get(i);
                Log.d(TAG, "Cookie : " + item);
            }
        }
        catch (Exception e) {
            Log.e(TAG, "Exception", e);
        }

    }
}