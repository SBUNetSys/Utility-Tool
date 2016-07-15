package com.example.jianxu.netmeasurement;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;


public class MainActivity extends Activity
        implements
        DataApi.DataListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private TextView mTextView;
    GoogleApiClient mGoogleClient;
    private static final String WEARABLE_DATA_PATH = "/wearable_data";
    private static final String HANDHELD_DATA_PATH = "/handheld_data";
    private static final String ID = "IDDDDDD";
    private static final int BYTES_PER_ITEM = 20;


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
        stub.setKeepScreenOn(true);

        mGoogleClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleClient.connect();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        //super.onDataChanged(dataEvents);
        System.gc();
        DataMap dataMap;

        for (DataEvent event : dataEvents) {
//            Log.d("DEBUG", "Data Changed......... " + event.getType()
//                    + " " + event.getDataItem().getUri().getPath());
            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                if (path.equals(WEARABLE_DATA_PATH)) {

                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();

                    Log.i("HANDHELD", "For script: received packet size= " + ((dataMap.size() - 2)*BYTES_PER_ITEM + 27)
                            + " ,timestamp= " + System.currentTimeMillis());

                    Log.i("WATCH", "DataMap received ......ID=" + dataMap.getLong(ID));
                    sendMsg(dataMap);
                    //new SendToDataLayerThread(HANDHELD_DATA_PATH, dataMap, mGoogleClient).start();
                }
            }
        }
    }


    private void sendMsg(DataMap dataMap) {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleClient).await();
        for (Node node : nodes.getNodes()) {

            // Construct a DataRequest and send over the data layer
            PutDataMapRequest putDMR = PutDataMapRequest.create(HANDHELD_DATA_PATH);
            putDMR.getDataMap().putAll(dataMap);
            PutDataRequest request = putDMR.asPutDataRequest();
            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleClient,request).await();
            if (result.getStatus().isSuccess()) {
                //Log.i("WEAR", "DataMap ID=" + dataMap.getLong("ID") + " sent to: " + node.getDisplayName());
            } else {
                // Log an error
                Log.i("WEAR", "ERROR: failed to send DataMap");
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) { }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("WEAR", "Listener added");
        Wearable.DataApi.addListener(mGoogleClient, this);
    }

}
