package com.example.jianxu.netmeasurement;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends ActionBarActivity
        implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String ID = "IDDDDDD";

    GoogleApiClient mGoogleClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGoogleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleClient.connect();

        Button sendBtn = (Button)findViewById(R.id.notifyBtn);
        sendBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PacketSendTask().execute("abc");
                    }
                }

        );
    }

    @Override
    public void onDestroy() {
        Log.d("MainActivity", "onDestroy");
        mGoogleClient.disconnect();
        super.onDestroy();
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


    @Override
    public void onConnectionSuspended(int cause) {
        Log.d("MainActivity", "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("MainActivity", "onConnectionFailed");
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("MainActivity", "onConnected now");
    }


    private class PacketSendTask extends AsyncTask<String, Integer, Long> {

        private int iteration, interval, pktNum;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            EditText iterationText = (EditText) findViewById(R.id.iteration);
            EditText intervalText = (EditText) findViewById(R.id.interval);
            EditText pktNumText = (EditText) findViewById(R.id.packet_num);
            try {
                iteration = Integer.parseInt(iterationText.getText().toString());
                interval = Integer.parseInt(intervalText.getText().toString());
                pktNum = Integer.parseInt(pktNumText.getText().toString());
            } catch (NumberFormatException e) {
                Log.w("HANDHELD", "Invalid empty input");
                return ;
            }

        }


        @Override
        protected Long doInBackground(String... params) {
            // TODO: this implementation of multi-thread sending,
            // TODO: which might make opponent unable to process packet

            // Prepare Data
            // Create a DataMap object and send it to the data layer
//            String WEARABLE_DATA_PATH = "/wearable_data";
//            DataMap [] dataMap = new DataMap[pktNum];
//            for (int m = 0; m < pktNum; ++m) {
//                dataMap[m] = new DataMap();
//                for (int i = 0; i < iteration; ++i) {
//                    dataMap[m].putLong(Integer.toString(i + 100000 + m * 1000), 10000L);
//                }
//            }
//            // Sending data
//            SendToDataLayerThread [] thread = new SendToDataLayerThread[pktNum];
//            try {
//                for (int i = 0; i < pktNum; ++i) {
//                    dataMap[i].putLong("time", System.nanoTime()); // put the latest timestamp
//                    dataMap[i].putLong(ID, i);
//                    thread[i] = new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap[i], mGoogleClient,
//                            pktNum, iteration, interval);
//                    thread[i].start();
//                    //Log.i("HANDHELD", "execute here.......");
//                    Thread.sleep(interval);
//                }
//                for (int i = 0; i < pktNum; ++i) {
//                    thread[i].join();
//                }
//            } catch (InterruptedException e) {
//                Log.e("HANDHELD", "Thread.sleep interruption:" + e.toString());
//            }


            String WEARABLE_DATA_PATH = "/wearable_data";
            DataMap dataMap = new DataMap();

            // Sending data
            SendToDataLayerThread thread =
                    new SendToDataLayerThread(WEARABLE_DATA_PATH, dataMap,
                            mGoogleClient, pktNum, iteration, interval);
            try {
                thread.start();
                //Log.i("HANDHELD", "execute here.......");
                thread.join();

            } catch (InterruptedException e) {
                Log.e("HANDHELD", "Thread.sleep interruption:" + e.toString());
            }
            return null;
        }


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
        }

    }

}
