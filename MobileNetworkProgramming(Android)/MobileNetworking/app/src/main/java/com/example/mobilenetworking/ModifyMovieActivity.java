package com.example.mobilenetworking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class ModifyMovieActivity extends AppCompatActivity {
    private static final String TAG = "수정하기";
    private RequestQueue mQueue;

    private EditText mTitle;
    private EditText mDirector;
    private EditText mYear;
    private EditText mSynopsis;
    private EditText mReview;
    private static String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_movie);

        mTitle = (EditText)findViewById(R.id.movie);
        mDirector = (EditText)findViewById(R.id.director);
        mYear = (EditText)findViewById(R.id.year);
        mSynopsis = (EditText)findViewById(R.id.synopsis);
        mReview = (EditText)findViewById(R.id.review);

        mQueue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Server Address : " + MainActivity.SERVER_ADDRESS);
        resolveMovieDetail();
    }

    // 영화 상세정보
    void resolveMovieDetail() {
        try {
            String movieId = getIntent().getStringExtra("movieId");
            // 공백 문자가 +가 된다. %20으로 변환
            String encoded = URLEncoder.encode(movieId, "UTF-8").replace("+", "%20");
            url = MainActivity.SERVER_ADDRESS + "/movies/" + encoded;

            Log.d(TAG, "MovieDetail : " + url);

            JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        JSONObject content = response.getJSONObject("movie");

                        String title =  content.getString("title");
                        String director = content.getString("director");
                        String year = content.getString("year");
                        String synopsis = content.getString("synopsis");
                        String reviews = content.getString("reviews");

                        mTitle.setText(title);
                        mDirector.setText(director);
                        mYear.setText(year);
                        mSynopsis.setText(synopsis);
                        mReview.setText(reviews);

                    } catch (JSONException e) {
                        Log.e(TAG, "JSONException", e);
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            mQueue.add(request);
        } catch (Exception e) {
            Log.e(TAG, "Exception", e);
            e.printStackTrace();
        }
    }

    // 요청 보내기 :: 완료 버튼 이벤트 리스너
    public void sendRequest(View v) {

        //String url = MainActivity.SERVER_ADDRESS + "/movies/";
        Log.d(TAG, "MovieDetail : " + url);
        StringRequest request = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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
                String year = mYear.getText().toString();
                String synopsis = mSynopsis.getText().toString();
                String reviews = mReview.getText().toString();

                Map<String, String> params = new HashMap<>();

                params.put("title", title);
                params.put("director", director);
                params.put("year", year);
                params.put("synopsis", synopsis);
                params.put("reviews", reviews);

                return params;
            }

            @Override
            public String getBodyContentType() {
                // 컨텐트 타입
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        mQueue.add(request);

        // 완료 버튼이 눌리면 메인화면으로 이동
        finish();       // Activity 이동하면서 이전의 Activity를 종료한다.
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
