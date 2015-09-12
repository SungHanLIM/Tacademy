package com.example.mobilenetworking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ModifyMovieActivity extends AppCompatActivity {
    private static final String TAG = "수정하기";
    private RequestQueue mQueue;

    private EditText mTitle;
    private EditText mDirector;
    private EditText mYear;
    private EditText mSynopsis;
    private EditText mReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_movie);



        mQueue = Volley.newRequestQueue(this);

        resolveMovieDetail();
    }

    // 완료 버튼이 눌리면 메인화면으로 이동
    public void sendRequest(View v) {

        finish();       // Activity 이동하면서 이전의 Activity를 종료한다.
        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);
        Log.d(TAG, "ttt" + null);
    }

    void resolveMovieDetail() {

       /* try {
            String movieId = getIntent().getStringExtra("movieId");
            // 공백 문자가 +가 된다. %20으로 변환
            String encoded = URLEncoder.encode(movieId, "UTF-8").replace("+", "%20");
            String url = MainActivity.SERVER_ADDRESS + "/movies/" + encoded;
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
        }*/
    }
}
