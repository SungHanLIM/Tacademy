package gcmservice.gcm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    static final private String TAG = "GCM_Example";
    private Handler handler;
    private TextView tokenLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //
        // tokenLabel = (TextView) findViewById(R.id.tokenLabel);
        handler = new Handler();
    }

    // 플레이 서비스 사용 가능 여부 체크
    public void checkPlayService(View v) {
        int resultCode = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        Log.d(TAG, "isGooglePlayServicesAvailable : " + resultCode);
        if (ConnectionResult.SUCCESS == resultCode) {
            // 서비스 가능
            Toast.makeText(this, "플레이 서비스 사용 가능", Toast.LENGTH_SHORT).show();
        } else {
            // 서비스 불가시 - 설치/업데이트 다이얼로그 출력
            GoogleApiAvailability.getInstance().getErrorDialog(this, resultCode, 0).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent : " + intent);
    }

    public void registDevice(View v) {
        // RegistrationIntentService에서 토큰 발급 브로드캐스트 리시버
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String token = intent.getExtras().getString("TOKEN");
                tokenLabel.setText(token);
            }
        }, new IntentFilter(RegistrationIntentService.REGIST_COMPLETE_BROADCAST));

        Log.d(TAG, "start registration service");

        // 토큰 발급 서비스 시작
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
    }

    // 토큰 직접 발급받기 - IntentService로 작성하는 것을 권장
    class RegistThread extends Thread {
        @Override
        public void run() {
            Log.d(TAG, "Trying to regist device");
            try {
                InstanceID instanceID = InstanceID.getInstance(MainActivity.this);
                final String token = instanceID.getToken("104377974250", GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                Log.d(TAG, "Token : " + token);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.this.tokenLabel.setText(token);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "Regist Exception", e);
            }
        }
    }
}