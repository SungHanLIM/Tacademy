package com.example.restjson;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private static final String APP_KEY = "8f0b96f0-1914-3a41-874e-89a2bae6cdb2";
    EditText mEditText;

    public searchProduct(View v) {
        String keyword = mEditText.getText().toString();
        String encoded = URLEncoder.encode(keyword, "UTF-8");
        String url = "http://apis.skplanetx.com/11st/common/products?version=1&page=&count=&searchKeyword=아이패드&sortCode=&option=&appKey=8f0b96f0-1914-3a41-874e-89a2bae6cdb2&format=json";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
