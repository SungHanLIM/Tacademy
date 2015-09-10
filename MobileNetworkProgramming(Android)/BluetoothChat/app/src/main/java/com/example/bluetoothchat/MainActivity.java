package com.example.bluetoothchat;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    // UUID
    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    // 서비스 이름
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    private static final String TAG = "BluetoothChat-App";

    private BluetoothAdapter bluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 1;
    private TextView textView;
    private EditText editText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        textView = (TextView) findViewById(R.id.textView);
        editText = (EditText)findViewById(R.id.editText);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mReceiver, filter);
    }

    private Handler handler = new Handler();
    void showMessage(String msg) {
        final String text = msg;
        handler.post(new Runnable() {
            @Override
            public void run() {
                textView.append(text + "\n");
            }
        });
    }

   /*
   * 블루투스 채팅 서버
   * */

    // 검색 가능하게 하기
    public void makeDiscoverable(View v) {
        if (bluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            showMessage("검색 가능 상태 변경 시작");

            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = null;
            if ( BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                msg = "상태 변경  : ";

                int state = intent.getExtras().getInt(BluetoothAdapter.EXTRA_STATE);
                int prevState = intent.getExtras().getInt(BluetoothAdapter.EXTRA_PREVIOUS_STATE);

                switch (state) {
                    case BluetoothAdapter.STATE_CONNECTED:
                        msg += "STATE_CONNECTED";
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        msg += "STATE_CONNECTING";
                        break;
                    case BluetoothAdapter.STATE_DISCONNECTED:
                        msg += "STATE_DISCONNECTED";
                        break;
                    case BluetoothAdapter.STATE_DISCONNECTING:
                        msg += "STATE_DISCONNECTING";
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        msg += "STATE_OFF";
                        break;
                    case BluetoothAdapter.STATE_ON:
                        msg += "STATE_ON";
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        msg += "STATE_TURNING_ON";
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        msg += "STATE_TURNING_OFF";
                        break;
                }
            }
            else if ( BluetoothAdapter.ACTION_SCAN_MODE_CHANGED.equals(intent.getAction())) {
                msg = "스캔 모드 변경 : ";
                switch (bluetoothAdapter.getScanMode()) {
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        msg += "SCAN_MODE_NONE";
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        msg += "SCAN_MODE_CONNECTABLE";
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        msg += "SCAN_MODE_CONNECTABLE_DISCOVERABLE";
                        break;
                }
            }
            showMessage(msg);

        }
    };

    public void startServer(View v) {
        ServerThread server = new ServerThread();
        server.start();
    }

    private class ServerThread extends Thread {
        BluetoothServerSocket serverSocket;

        public ServerThread() {
            try {
                serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME_INSECURE, MY_UUID_INSECURE);
                if ( serverSocket != null ) {
                    showMessage("서버 소켓 생성, 리스닝");
                }
            } catch (IOException e) {
                showMessage("Listening Error \n" + e.getMessage());
                Log.e(TAG, "Listening Error", e);
                serverSocket = null;
            }
        }

        @Override
        public void run() {
            Log.d(TAG, "Echo Server socket started");
            BluetoothSocket socket = null;

            try {
                socket = serverSocket.accept();
                showMessage("Socket accepted");

            } catch (IOException e) {
                showMessage("Socket accept error\n" + e.getMessage());
                Log.e(TAG, "Socket accept Failed", e);
            }

            if ( socket == null) {
                showMessage("Socket is null");
                return;
            }

            chatThread = new ChatThread(socket);
            chatThread.start();

            // 서버 소켓 닫기
            try {
                serverSocket.close();
                showMessage("Server socket closed");
            } catch (IOException e) {
                Log.e(TAG, "Socket Close Error", e);
                e.printStackTrace();
            }
        }
    }

   /*
   * 블루투스 채팅 클라이언트
   * */

    private ClientThread clientThread;

    // 기기 검색 액티비티
    public void startDiscover(View v) {
        Intent intent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intent, 0);
    }

    // 기기 목록에서 선택
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == Activity.RESULT_OK ) {
            String address = data.getExtras().getString(DeviceListActivity.SELECTED_DEVICE_ADDRESS);
            Log.d(TAG, "Trying to pair " + address);

            // 클라이언트 쓰레드 동작

            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            showMessage("선택된 기기 : " + device.getName() + "(" + device.getAddress() + ")");

            clientThread = new ClientThread(device);
            clientThread.start();
        }
    }

    private class ClientThread extends Thread {
        private BluetoothSocket mSocket;
        private BluetoothDevice mDevice;

        InputStream is = null;
        OutputStream os = null;

        ClientThread(BluetoothDevice device) {
            mDevice = device;
        }

        @Override
        public void run() {

            try {
                showMessage("소켓 생성");
                mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            } catch (IOException e) {
                showMessage("소켓 생성 실패");
                Log.e(TAG, "Socket Create Error", e);
                return;
            }

            // 연결이 느려지지 않도록 검색을 중지
            bluetoothAdapter.cancelDiscovery();

            try {
                showMessage("소겟 연결 시도");
                mSocket.connect();
            } catch (IOException e) {
                showMessage("소켓 연결 에러\n" + e.getMessage());
                Log.e(TAG, "Socket connect Error", e);
                return;
            }

            chatThread = new ChatThread(mSocket);
            chatThread.start();


        }

        public void sendMessage(String msg) {
            if ( os == null || mSocket == null ) {
                showMessage("연결 안됨");
                return;
            }
            try {
                Log.d(TAG, "Sending : " + msg);
                os.write(msg.getBytes());
//                os.flush();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                e.printStackTrace();
            }
        }
    }

   /*
   * 채팅 쓰레드 - 서버, 클라이언트 공용
   * */

    public void sendMessage(View v) {
        if ( chatThread == null ) {
            showMessage("연결 안됨");
            return;
        }

        String message = editText.getText().toString();
        chatThread.sendMessage(message + "\r\n");
    }
    private ChatThread chatThread;
    class ChatThread extends Thread {
        private DataOutputStream bos = null;
        private BluetoothSocket mSocket;

        ChatThread(BluetoothSocket socket) {
            mSocket = socket;
        }

        public void sendMessage(String msg) {
            if ( bos == null || mSocket == null ) {
                showMessage("연결 안됨");
                return;
            }
            try {
                Log.d(TAG, "Sending : " + msg);
                showMessage(">> " + msg);
                bos.write(msg.getBytes());
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                e.printStackTrace();
            }
        }


        @Override
        public void run() {
            showMessage("채팅 쓰레드 시작");
            InputStream is = null;

            try {
                is = mSocket.getInputStream();
                OutputStream os = mSocket.getOutputStream();
                bos = new DataOutputStream(os);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line = null;
                while ( true ) {
                    line = reader.readLine();
                    Log.d(TAG, "Read.. : " + line);
                    showMessage("<< " + line);
                    if ( line == null ) {
                        break;
                    }
                }

                mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                showMessage("IO Error\n"+e.getMessage());
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}