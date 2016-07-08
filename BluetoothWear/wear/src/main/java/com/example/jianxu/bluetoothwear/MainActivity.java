package com.example.jianxu.bluetoothwear;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends Activity {
    public final String TAG = "MainActivity";

    private BluetoothAdapter mBluetoothAdapter;

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "Default Bluetooth Adapter is null.");
        }

        Log.i(TAG, "Default Blueooth Adapter is set");
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
}
