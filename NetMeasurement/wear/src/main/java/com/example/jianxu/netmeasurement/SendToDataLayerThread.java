package com.example.jianxu.netmeasurement;

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

    public SendToDataLayerThread(String wearable_data_path, DataMap dm, GoogleApiClient googleApi) {
        path = wearable_data_path;
        dataMap = dm;
        mGoogleApi = googleApi;
    }

    @Override
    public void run() {
        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApi).await();
        for (Node node : nodes.getNodes()) {

            // Construct a DataRequest and send over the data layer
            PutDataMapRequest putDMR = PutDataMapRequest.create(path);
            putDMR.getDataMap().putAll(dataMap);
            PutDataRequest request = putDMR.asPutDataRequest();
            DataApi.DataItemResult result = Wearable.DataApi.putDataItem(mGoogleApi,request).await();
            if (result.getStatus().isSuccess()) {
                //Log.i("WEAR", "DataMap ID=" + dataMap.getLong("ID") + " sent to: " + node.getDisplayName());
            } else {
                // Log an error
                Log.i("WEAR", "ERROR: failed to send DataMap");
            }
        }
    }

}
