package com.example.photoupload_apachelibrary;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "PhotoUpload-Sample";


    // 서버 주소
    private EditText mAddress;
    // 제목
    private EditText mTitle;
    // 이미지 뷰
    private ImageView mImageView;
    // 선택된 이미지
    private Uri mImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = (EditText)findViewById(R.id.title);
        mImageView = (ImageView)findViewById(R.id.imageView);
        mAddress = (EditText) findViewById(R.id.serverAddress);
    }


    // 사진 고르기
    private static final int PICK_IMAGE_REQUEST = 1;
    public void selectImage(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "result Code : " + resultCode);

        if ( resultCode != Activity.RESULT_OK ) {
            Toast.makeText(this, "file choose cancelled", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "file choose cancelled");
            return;
        }

        if ( PICK_IMAGE_REQUEST == requestCode ) {
            try {
                Log.d(TAG, "data : " + data);
                mImageUri = data.getData();
                // 화면에 이미지 출력
                mImageView.setImageURI(mImageUri);
            } catch (Exception e) {
                Log.e(TAG, "URISyntaxException", e);
                e.printStackTrace();
            }
        }
    }

    Handler mHandler = new Handler();

    public void showList(View v) {
        String url = mAddress.getText().toString();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    public void uploadContents(View v) {
        String title = mTitle.getText().toString();
        if ( title.length() > 1 ) {
            new FileUploadThread().start();
        }
        else {
            Toast.makeText(this, "글을 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }

    class FileUploadThread extends Thread {
        @Override
        public void run() {

            try {
                String title = mTitle.getText().toString();

                String host = mAddress.getText().toString();
                Log.d(TAG, "Send Post message to " + host);

                HttpPost post = new HttpPost(host);

                // 기본 HTTP 모듈에는 없음.
                MultipartEntity entity = new MultipartEntity();
                entity.addPart("title", new StringBody(title, Charset.forName("utf-8")));

                if ( mImageUri != null ) {
                    // Drawable에서 올리기
                    // InputStream drawableIs = getResources().openRawResource(R.drawable.one);

                    InputStream is =  getContentResolver().openInputStream(mImageUri);
                    String fileName = mImageUri.getLastPathSegment();
                    Log.d(TAG, "file Name : " + fileName);

                    InputStreamBody isBody = new InputStreamBody(is, fileName);
                    Log.d(TAG, "getSchemeSpecificPart : " + mImageUri.getSchemeSpecificPart());

                    entity.addPart("poster", isBody);
                }

                post.setEntity(entity);

                HttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(post);
                Log.d(TAG, "status : " + response.getStatusLine().getStatusCode());
                final StatusLine statusLine = response.getStatusLine();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if ( statusLine.getStatusCode() == 200 ) {
                            Toast.makeText(MainActivity.this, "성공", Toast.LENGTH_SHORT).show();
                            mImageView.setImageDrawable(null);
                            mTitle.setText("");
                        }
                        else {
                            Toast.makeText(MainActivity.this, "실패 : " + statusLine.getReasonPhrase(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                e.printStackTrace();
            }
        }
    }

}