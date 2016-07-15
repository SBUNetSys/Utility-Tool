package com.example.jianxu.netmeasurement;

import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by jianxu on 8/17/15.
 */
public class SendToDataLayerThread extends Thread {

    private String path;

    private DataMap dataMap;

    private GoogleApiClient mGoogleApi;
    private static final String ID = "IDDDDDD";
    private static final int BYTES_PER_ITEM = 20;

    private int mPktNum;
    private int mPktSize;
    private int mInterval;
    DataMap mDataMap;
    public SendToDataLayerThread(String wearable_data_path,
                                 DataMap dm,
                                 GoogleApiClient googleApi,
                                 int pktNum,
                                 int pktSize,
                                 int interval) {
        path = wearable_data_path;
        mDataMap = dm;
        mGoogleApi = googleApi;
        mPktNum = pktNum;
        mPktSize = pktSize;
        mInterval = interval;
    }

    @Override
    public void run() {

        String WEARABLE_DATA_PATH = "/wearable_data";
        DataMap [] dataMap = new DataMap[mPktNum];
        for (int m = 0; m < mPktNum; ++m) {
            dataMap[m] = new DataMap();
            for (long i = 0; i < mPktSize; ++i) {
                // Each item's size is: 12 Chars + 1 Long = 12B + 8B = 20 Bytes
                dataMap[m].putLong(Long.toString(i + 100000000000L + m * 1000L), 10000L);
            }
        }
        Log.i("DataSize", "DataMap size: " + dataMap.length);
        Log.i("DataSize", "DataMap bytes count: " + dataMap[0].toByteArray().length);
        Log.i("DataSize", "DataMap to string length: " + dataMap);

        // Sending data
        for (int i = 0; i < mPktNum; ++i) {

            try {
                Thread.sleep(mInterval);

                dataMap[i].putLong("time", System.nanoTime()); // put the latest timestamp
                dataMap[i].putLong(ID, i);
                sendMsg(dataMap[i]);
                // Log.i("TAG", "Sending packets");
            } catch(InterruptedException e) {
                Log.e("Error", "Thread.sleep failed");
            }
        }
//        Log.i("TAG", "HERE");
//        sendMsg(mDataMap);
    }

    private void sendMsg(DataMap dataMap) {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApi).await();
        for (Node node : nodes.getNodes()) {
            // Construct a DataRequest and send over the data layer
            PutDataMapRequest putDMR = PutDataMapRequest.create(path);
            putDMR.getDataMap().putAll(dataMap);
            PutDataRequest request = putDMR.asPutDataRequest();
            Log.i("HANDHELD", "For script: sent packet size= " + ((dataMap.size() - 2)*BYTES_PER_ITEM + 27)
                            + " ,timestamp= " + System.currentTimeMillis());

            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleApi,request).await();
            if (result.getStatus().isSuccess()) {
                //Log.d("HANDHELD", "DataMap: " + dataMap + " sent to: " + node.getDisplayName());
            } else {
                // Log an error
                Log.i("HANDHELD", "ERROR: failed to send DataMap");
            }
        }
    }

}
