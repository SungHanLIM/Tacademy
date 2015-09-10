package com.example.mobilenetworking;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Moviest-Sample";
    private ArrayAdapter<String> adapter;
    private List<String> movieList = new ArrayList<>();
    private RequestQueue mQueue;
    private EditText mAddress;

    public static String SERVER_ADDRESS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAddress = (EditText)findViewById(R.id.serverAddress);

        ListView mListView = (ListView) findViewById(R.id.movieListView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, movieList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);

                // 영화 제목과 영화 ID가 같다.
                String movieId = movieList.get(position);
                intent.putExtra("movieId", movieId);

                startActivity(intent);
            }
        });

        // Singleton 사용
        // https://developer.android.com/training/volley/requestqueue.html
//      mQueue = Volley.newRequestQueue(this);
        mQueue = RequestQueueSingleton.getInstance(this).getRequestQueue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        resolveMovieList();
    }

    public void composeNewMovie(View v) {
        Intent intent = new Intent(MainActivity.this, ComposeMovieActivity.class);
        startActivity(intent);
    }

    public void refresh(View v) {
        resolveMovieList();
    }

    void resolveMovieList() {
        movieList.clear();

        String url = mAddress.getText().toString();
        MainActivity.SERVER_ADDRESS = url;

        JsonObjectRequest request = new JsonObjectRequest(url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d(TAG, "Movie List : " + response);
                    JSONArray movies = response.getJSONArray("movies");

                    for ( int i = 0 ; i < movies.length() ; i++) {
                        JSONObject item = (JSONObject) movies.get(i);
                        String title = item.getString("title");
                        movieList.add(title);
                    }

                    adapter.notifyDataSetChanged();

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

    }
}