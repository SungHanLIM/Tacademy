package com.example.mobilenetworking;

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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class ComposeMovieActivity extends AppCompatActivity {
    static private final String TAG = "POST";

    //private static String mUrl;
    private EditText mTitle;
    private EditText mDirector;
    private EditText mYear;
    private EditText mSynopsis;
    private EditText mReview;

    private RequestQueue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_movie);

        mTitle = (EditText)findViewById(R.id.movie);
        mDirector = (EditText)findViewById(R.id.direcor);
        mYear = (EditText)findViewById(R.id.year);
        mSynopsis = (EditText)findViewById(R.id.synopsis);
        mReview = (EditText)findViewById(R.id.review);

        queue = Volley.newRequestQueue(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Server Address : " + MainActivity.SERVER_ADDRESS);
        //sendRequest(null);
    }

    // 요청 보내기
    public void sendRequest(View v) {
        finish();

        String url = MainActivity.SERVER_ADDRESS + "/movies/";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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
                //String movie_id = mTitle.getText().toString();
                String title = mTitle.getText().toString();
                String director = mDirector.getText().toString();
                String year = mYear.getText().toString();
                String synopsis = mSynopsis.getText().toString();
                String reviews = mReview.getText().toString();
                // Log.d(TAG, "!!!!!!!!!!!!!!!!!!!!!!!!!! title : " + title);

                Map<String, String> params = new HashMap<>();
               // params.put("movie_id", movie_id);
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
        queue.add(request);
    }

}
