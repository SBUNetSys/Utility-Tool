package com.example.jianxu.bluetoothwear;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jianxu.commonlib.BluetoothChatService;
import com.example.jianxu.commonlib.Constants;

public class MainActivity extends Activity {
    public final String TAG = "MainActivity";

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mBluetoothService;

    private TextView mTextView;
    private Button mConnectBtn;
    private Button mSendBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
                mConnectBtn = (Button) stub.findViewById(R.id.connectBtn);
                if (mConnectBtn == null) {
                    Log.e(TAG, "Button is NULL!!");
                    return;
                }
                mConnectBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        connectDevice("02:00:00:00:00:00");
                    }
                });

                mSendBtn = (Button) stub.findViewById(R.id.sendHelloBtn);
                mSendBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendMsg("Hello World!! I'm sending from Smartwatch");
                    }
                });
            }

        });
        stub.setKeepScreenOn(true);

        // start the BluetoothChatService
        mBluetoothService = new BluetoothChatService(this, mHandler);
        mBluetoothService.start();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "Default Bluetooth Adapter is null.");
        }

        Log.i(TAG, "Default Bluetooth Adapter is set");
        if (mBluetoothAdapter.isEnabled()) {
            Log.i(TAG, "Bluetooth is enabled");
            String addr = mBluetoothAdapter.getAddress();
            String deviceName = mBluetoothAdapter.getName();
            int state = mBluetoothAdapter.getState();
            Log.i(TAG, "Bluetooth is enabled, " + deviceName + ":" + addr);

        } else {
            Log.w(TAG, "Bluetooth is not Enabled");
        }

    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            try {
                switch (msg.what) {
                    case Constants.MESSAGE_STATE_CHANGE:
                        switch (msg.arg1) {
                            case BluetoothChatService.STATE_CONNECTED:
                                Log.i(TAG, "Connected.");
                                break;
                            case BluetoothChatService.STATE_CONNECTING:
                                Log.i(TAG, "Connecting....");
                                break;
                            case BluetoothChatService.STATE_LISTEN:
                            case BluetoothChatService.STATE_NONE:
                                Log.i(TAG, "Not connected");
                                break;
                        }
                        break;

                    case Constants.MESSAGE_READ:
                        byte[] readBuf = (byte[]) msg.obj;
                        String readStr = new String(readBuf, 0, msg.arg1);

                        Log.i(TAG, "Received packet, " + readStr);
                        Log.i(TAG, "For script: received packet size= " + readStr.length()
                                + " ,timestamp= " + System.currentTimeMillis());
                        sendMsg(readStr);
                        break;
                    case Constants.MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        String sentStr = new String(writeBuf);
                        Log.i(TAG, "Sent packet, " + sentStr);
                        break;
                }
            } catch (Exception e) {
                Log.w(TAG, "Exception caught " + e.toString());
            }
        }
    };

    private void sendMsg(String message) {
        if (mBluetoothService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Log.w(TAG, "You are not connected to a device");
        }

        try {
            if (message.length() > 0) {
                byte[] send = message.getBytes();
                mBluetoothService.write(send);

            }
        } catch (Exception e) {
            Log.w(TAG, "Exception catched " + e.toString());
        }
    }

    private void connectDevice(String addr) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(addr);
        mBluetoothService.connect(device, false);
    }

}
