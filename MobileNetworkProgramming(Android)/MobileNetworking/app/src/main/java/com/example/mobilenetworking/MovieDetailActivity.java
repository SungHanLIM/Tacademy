package com.example.mobilenetworking;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = "MovieDetail";
    private RequestQueue mQueue;

    private EditText mTitle;
    private EditText mDirector;
    private EditText mYear;
    private EditText mSynopsis;
    private EditText mReview;

    private TextView mMovieInfo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        mTitle = (EditText)findViewById(R.id.movie);
        mDirector = (EditText)findViewById(R.id.direcor);
        mYear = (EditText)findViewById(R.id.year);
        mSynopsis = (EditText)findViewById(R.id.synopsis);
        mReview = (EditText)findViewById(R.id.review);

        mMovieInfo = (TextView) findViewById(R.id.movieInfo);

        mQueue = RequestQueueSingleton.getInstance(this).getRequestQueue();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Server Address : " + MainActivity.SERVER_ADDRESS);
        resolveMovieDetail();
    }

    // 영화정보 상세 보기
    void resolveMovieDetail() {
        try {
            String movieId = getIntent().getStringExtra("movieId");
            // 공백 문자가 +가 된다. %20으로 변환
            String encoded = URLEncoder.encode(movieId, "UTF-8").replace("+", "%20");
            String url = MainActivity.SERVER_ADDRESS + "/movies/" + encoded;
            //String url = MainActivity.SERVER_ADDRESS + "/movies/" + "test";
            Log.d(TAG, "MovieDetail : " + url);

            JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    String contents = new String("");

                    try {
                        JSONObject content = response.getJSONObject("movie");
                        contents =   "❖ 영화 제목 : " + content.getString("title") + "\n"
                                   + "❖ 감독 이름 : " + content.getString("director") + "\n"
                                   + "❖ 제작 년도 : " + content.getString("year") + "\n"
                                   + "❖ 줄 거 리 : " + content.getString("synopsis") + "\n"
                                   + "❖ 리    뷰 : " + content.getString("reviews");

                        Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!"+ content.getString("title"));

                        mMovieInfo.setText(contents);
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

    // 영화정보 수정 버튼이 클릭되면 ModifyMovieActivity로 이동
    public void modifyMovie(View v) {
        Intent intent = new Intent(MovieDetailActivity.this, ModifyMovieActivity.class);

        String movieId = getIntent().getStringExtra("movieId");
        intent.putExtra("movieId", movieId);
        startActivity(intent);

        finish();       // Activity 이동하면서 이전의 Activity를 종료한다.
    }

    // 삭제 버튼이 클릭되면 삭제 기능 실행
    public void deletewMovie(View v) throws UnsupportedEncodingException {

        finish();       // Activity 이동하면서 이전의 Activity를 종료한다.

        String movieId = getIntent().getStringExtra("movieId");
        // 공백 문자가 +가 된다. %20으로 변환
        String encoded = URLEncoder.encode(movieId, "UTF-8").replace("+", "%20");
        String url = MainActivity.SERVER_ADDRESS + "/movies/" + encoded;

        StringRequest request = new StringRequest(Request.Method.DELETE, url, new Response.Listener<String>() {
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
                return null;
            }

            @Override
            public String getBodyContentType() {
                // 컨텐트 타입
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };
        mQueue.add(request);
    }
}
