package com.example.jianxu.bluetoothwear;

import android.bluetooth.BluetoothAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    public final String TAG = "MainActivity";

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.w(TAG, "Default Bluetooth Adapter is null.");
        }

        Log.i(TAG, "Default Blueooth Adapter is set");
        if (mBluetoothAdapter.isEnabled()) {
            Log.i(TAG, "Bluetooth is enabled");
            String addr = mBluetoothAdapter.getAddress();
            String deviceName = mBluetoothAdapter.getName();
            Log.i(TAG, "Bluetooth is enabled, " + deviceName + ":" + addr);

        } else {
            Log.w(TAG, "Bluetooth is not Enabled");
        }
    }




}
