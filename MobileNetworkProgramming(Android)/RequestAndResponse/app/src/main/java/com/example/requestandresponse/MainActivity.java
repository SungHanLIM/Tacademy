package com.example.requestandresponse;

        import android.os.Bundle;
        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.TextView;

        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.util.List;
        import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Req/Res-Sample";

    private EditText mEditText;
    private TextView mTextView;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = (EditText) findViewById(R.id.editText);
        mTextView = (TextView) findViewById(R.id.textView);
    }

    public void sendRequest(View v) {
        new SendRequestThread().start();
    }

    class SendRequestThread extends Thread {
        @Override
        public void run() {
            try {
                // 연결 생성
                String urlStr = mEditText.getText().toString();
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                // 화면 출력을 위한 스트링 버퍼
                StringBuffer resBuffer = new StringBuffer("== Response ==\n");

                int code = conn.getResponseCode();
                String message = conn.getResponseMessage();
                resBuffer.append("Status Code : " + code + "\n");
                resBuffer.append("Message : " + code + "\n\n");

                // 응답 헤더 항목 얻어오기
                resBuffer.append(" == Header ==\n");


                Map<String, List<String>> requestProperties = conn.getHeaderFields();
                Log.d(TAG, "Header Fields : " + requestProperties.size());

                for (String key : requestProperties.keySet()) {
                    Log.d(TAG, "Header Key : " + key);
                    String value = requestProperties.get(key).toString();
                    resBuffer.append(key + " = " + value + "\n");
                }

                // 컨탠츠 출력
                resBuffer.append("\n== Content ==\n");
                String contentType = conn.getContentType();
                // 글자 기반이면 출력
                if (contentType.startsWith("text")) {
                    // 인풋 스트림 얻기
                    InputStream is = (InputStream) conn.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        resBuffer.append(line);
                    }

                    String content = String.valueOf(conn.getContent());
                    resBuffer.append(content);
                }

                // 화면에 반영
                final String msg = resBuffer.toString();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(msg);
                    }
                });
            } catch (MalformedURLException e) {
                Log.e(TAG, "MalformedURLException", e);
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, "IOException", e);
                e.printStackTrace();
            }
        }
    }
}