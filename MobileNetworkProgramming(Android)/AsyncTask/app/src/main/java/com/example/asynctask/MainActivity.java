package com.example.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

        private static final String TAG = "AsyncTask-Sample";
        private ImageView imageView;
        private TextView textView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);

                textView = (TextView) findViewById(R.id.textView);
                imageView = (ImageView) findViewById(R.id.imageView);
        }

        public void showImage(View v) {
                // Random Placeholder Image
                String urlStr = "http://lorempixel.com/720/1080/cats/";
                new NetworkTask().execute(urlStr);
        }

        class NetworkTask extends AsyncTask<String, Integer, Bitmap> {
                @Override
                protected void onPreExecute() {
                        // 준비 동작 : 비동기 동작 전에 실행, 메인 쓰레드에서 실행
                        textView.setText("0 byte received");
                        imageView.setImageBitmap(null);
                }

                // 필수
                @Override
                protected Bitmap doInBackground(String... params) {
                        try {
                                String urlStr = params[0];
                                URL url = new URL(urlStr);

                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                                int length = conn.getContentLength();
                                InputStream is = conn.getInputStream();

                                byte buffer[] = new byte[1024];
                                ByteArrayOutputStream os = new ByteArrayOutputStream();

                                int count = 0;
                                int received = 0;
                                while ( ( count = is.read(buffer) ) != -1 ) {
                                        os.write(buffer, 0, count);

                                        // 진행률 계산
                                        received += count;
                                        publishProgress(received);
                                }

                                Bitmap bitmap = BitmapFactory.decodeByteArray(os.toByteArray(), 0, os.size());
                                return bitmap;
                        } catch (Exception e) {
                                Log.e(TAG, "Exception", e);
                                e.printStackTrace();
                        }
                        return null;
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                        int progress = values[0];
                        textView.setText(progress + " byte received.");
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                        // 백그라운드 동작 완료 : 메인 쓰레드에서 실행
                        imageView.setImageBitmap(bitmap);
                }
        }
}