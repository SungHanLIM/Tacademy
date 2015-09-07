package com.example.networkinfo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "NetworkInfo-Sample";
    private TextView infoText;
    private Switch wifiSwitch;
    // 상태 변경 모니터링
    private TextView stateChangeLog;
    private StateChagneMonitor stateChagneMornitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoText = (TextView)findViewById(R.id.infoText);

        wifiSwitch = (Switch)findViewById(R.id.wifiSwitch);
        wifiSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                changeWifiStatus(b);
            }
        });
        stateChangeLog = (TextView)findViewById(R.id.stateChangeLog);
    }

    public void showNetworkInfo(View v) {
        String infoStr = null;

        ConnectivityManager service = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = service.getActiveNetworkInfo();
        if ( info == null ) {
            infoStr = "활성화된 네트워크 없음";
        }
        else {
            infoStr = "연결 타입 : " + info.getTypeName() + "\n";
            Log.d(TAG, "Network Info, name : " + info.getTypeName());

            infoStr += "연결 상태 : ";
            if ( info.isConnectedOrConnecting() ) {
                infoStr += "연결됨(중)\n";
            }
            else {
                infoStr += "연결 안됨\n";
            }
        }

        infoText.setText(infoStr);
    }

    void updateWifiSwitch() {
        // Wifi 상태 반영하기
        ConnectivityManager service = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = service.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if ( wifiInfo != null ) {
            wifiSwitch.setChecked(wifiInfo.isConnected());
        }
    }

    void changeWifiStatus(Boolean b) {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // CHANGE_WIFI_STATE 퍼미션 필요
        wifiManager.setWifiEnabled(b);
    }

    // 상태 변경 모니터링
    class StateChagneMonitor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager service = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            int networkType = intent.getIntExtra(ConnectivityManager.EXTRA_NETWORK_TYPE, 0);
            NetworkInfo eventNetwork = service.getNetworkInfo(networkType);
            stateChangeLog.append("이벤트 발생 장치 : " + eventNetwork.getTypeName());
            if ( eventNetwork.isConnectedOrConnecting() )
                stateChangeLog.append(" 연결됨(중)\n");
            else
                stateChangeLog.append(" 연결 안됨\n");

            boolean b = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
            stateChangeLog.append("FAILOVER : " + b + "\n");

            // 활성 상태의 네트워크 장치
            NetworkInfo activeInfo = service.getActiveNetworkInfo();
            if ( activeInfo != null ) {
                stateChangeLog.append("활성화된 네트워크 : " + activeInfo.getTypeName());
                if ( activeInfo.isConnectedOrConnecting() )
                    stateChangeLog.append(" 연결됨(중)\n");
                else
                    stateChangeLog.append(" 연결 안됨\n");
            }

            String reason = intent.getStringExtra(ConnectivityManager.EXTRA_REASON);
            if ( reason != null )
                stateChangeLog.append("EXTRA REASON : " +  reason + "\n");

            boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if ( noConnectivity == true )
                stateChangeLog.append("가능한 네트워크 없음\n");

            updateWifiSwitch();

            stateChangeLog.append("============================\n");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 상태 변경 모니터링
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        stateChagneMornitor = new StateChagneMonitor();
        registerReceiver(stateChagneMornitor, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(stateChagneMornitor);
    }
}