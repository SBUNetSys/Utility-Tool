package com.example.jianxu.bluetoothwear;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Set;
import com.example.jianxu.commonlib.BluetoothChatService;
import com.example.jianxu.commonlib.Constants;
import com.google.android.gms.wearable.DataMap;

public class MainActivity extends AppCompatActivity {
    public final String TAG = "MainActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothChatService mBluetoothService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            Log.i(TAG, "Bluetooth is enabled, " + deviceName + ":" + addr);

        } else {
            Log.w(TAG, "Bluetooth is not Enabled");
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices) {

                Log.i(TAG, "Found device, " + device.getName() + ":" + device.getAddress());
            }

        } else {
            Log.i(TAG, "No devices have been paired yet.");
        }

        Button connectBtn = (Button) findViewById(R.id.connectWatch);
        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Connect the smartwatch
                connectDevice("44:D4:E0:F9:AF:D1");
            }
        });

        Button sendBtn = (Button) findViewById(R.id.sendMsg);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Hello, I'm Nexus 6P " + System.currentTimeMillis() + " .\n");
            }
        });

        // Sending 1KB data
        Button send1KBtn = (Button) findViewById(R.id.send1KB);
        send1KBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Should fillin 1K/2 chars
                int length = 1024;
                String sendStr = constructKBMsg(length);
                sendMessage(sendStr);
            }
        });



        Button sendBatch = (Button) findViewById(R.id.sendMsgBatch);
        sendBatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PacketSendTask().execute("abc");
            }
        });
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

                        Log.i(TAG, "For script: received timestamp= " + System.currentTimeMillis());
                        //Log.i(TAG, "Received packet, " + readStr);
                        break;
                    case Constants.MESSAGE_WRITE:
                        byte[] writeBuf = (byte[]) msg.obj;
                        String sentStr = new String(writeBuf);
                        //Log.i(TAG, "Sent packet, " + sentStr);
                        Log.i(TAG, "For script: sent packet size= " + sentStr.length()
                                + " ,timestamp= " + System.currentTimeMillis());
                        break;
                }
            } catch (Exception e) {
                Log.w(TAG, "Exception caught " + e.toString());
            }
        }
    };


    private void sendMessage(String message) {
        if (mBluetoothService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "You are not connected to a device", Toast.LENGTH_SHORT).show();
        }
        message += "#";
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

    private String constructKBMsg(int size) {
        char[] array = new char[size];
        Arrays.fill(array, 'a');
        String msg = new String(array);
        return msg;
    }


    private class PacketSendTask extends AsyncTask<String, Integer, Long> {
        private int mDataSize;
        private int mPacketNum;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                EditText sizeText = (EditText) findViewById(R.id.packetSize);
                mDataSize = Integer.parseInt(sizeText.getText().toString());
                EditText numText = (EditText) findViewById(R.id.packetNum);
                mPacketNum = Integer.parseInt(numText.getText().toString());
            } catch (Exception e) {
                Log.w(TAG, "Invalid empty input");
                return;
            }
        }

        @Override
        protected Long doInBackground(String... params) {
            // TODO: this implementation of multi-thread sending,
            // TODO: which might make opponent unable to process packet

            String msg = constructKBMsg(mDataSize);
            try {
                for (int i = 0; i < mPacketNum; ++i) {
                    sendMessage(msg);
                    Thread.sleep(5000);
                }
            } catch(Exception e) {
                Log.w(TAG, "Exception occurred.." + e.toString());
            }
            return null;
        }
    }
}