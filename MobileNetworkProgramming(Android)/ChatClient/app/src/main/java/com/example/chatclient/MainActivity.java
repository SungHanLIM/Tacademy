package com.example.chatclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "ChatClient_Example";
    private EditText userInput;
    private TextView resultView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userInput = (EditText) findViewById(R.id.messageInput);
        resultView = (TextView) findViewById(R.id.resultView);

    }

    // 연결 버튼의 이벤트 리스너
    public void connectToServer(View v) {
        if (null != chatThread && chatThread.isConnected()) {
            Toast.makeText(this, "이미 연결돼있습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        EditText ipInput = (EditText) findViewById(R.id.ipInput);
        EditText portInput = (EditText) findViewById(R.id.portInput);

        try {
            String host = ipInput.getText().toString();
            int port = Integer.parseInt(portInput.getText().toString());

            chatThread = new ChatThread(host, port);
            chatThread.start();
        } catch (Exception e) {
            Log.e(TAG, "Connect Error", e);
            e.printStackTrace();

            String msg = resultView.getText() + "\n" + e.getMessage();
            resultView.setText(msg);
        }
    }

    // 전송 버튼의 이벤트 리스너
    public void sendMessage(View v) {
        if (chatThread != null && chatThread.isConnected()) {
            // 사용자가 입력한 문자열을 전송 쓰레드를 통해서 보내기
            String message = userInput.getText().toString();
            chatThread.sendMessage(message);
        } else {
            Toast.makeText(this, "서버와 연결되지 않았습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 종료 버튼 이벤트 리스너
    public void closeConnection(View v) {
        if (chatThread != null) {
            chatThread.closeChat();
            chatThread = null;
        }
    }

    // UI에 컨텐츠 출력을 위한 핸들러
    class ChatMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String str = (String) msg.obj;
            resultView.setText(resultView.getText().toString() + "\n" + str);
        }

        public void printMessage(String str) {
            Message msg = new Message();
            msg.obj = str;
            sendMessage(msg);
        }
    }

    private ChatMessageHandler mHanlder = new ChatMessageHandler();

    // 채팅 쓰레드
    ChatThread chatThread;

    class ChatThread extends Thread {
        private String host;
        private int port;
        private Socket socket;
        boolean quit = false;

        ChatThread(String host, int port) {
            this.host = host;
            this.port = port;
        }

        boolean isConnected() {
            if (socket == null)
                return false;
            return socket.isConnected();
        }

        void sendMessage(String message) {
            try {
                OutputStream os = socket.getOutputStream();
                os.write(message.getBytes());
                os.flush();
            } catch (IOException e) {
                Log.e(TAG, "Exception", e);
                e.printStackTrace();
            }
        }

        void closeChat() {
            if (socket.isConnected()) {
                quit = true;
            }
        }

        @Override
        public void run() {
            try {
                socket = new Socket(host, port);
                Log.d(TAG, "Socket connected? " + socket.isConnected());

                InputStream is = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line;
                while (quit == false) {
                    line = reader.readLine();
                    if (line == null)
                        break;

                    mHanlder.printMessage(line);
                }

                mHanlder.printMessage("Chat service disconnected");

                Log.d(TAG, "closing socket, reader, is");
                reader.close();
                is.close();
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception", e);
            }

        }
    }
}