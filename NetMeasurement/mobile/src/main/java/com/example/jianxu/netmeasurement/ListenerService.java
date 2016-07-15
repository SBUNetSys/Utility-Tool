package com.example.jianxu.netmeasurement;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Date;

/**
 * Created by jianxu on 8/17/15.
 */
public class ListenerService extends WearableListenerService {


    private static final String WEARABLE_DATA_PATH = "/wearable_data";
    private static final String HANDHELD_DATA_PATH = "/handheld_data";
    private static final String ID = "IDDDDDD";
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        //Log.d("DEBUG", "execute here....1");

        DataMap dataMap;
        for (DataEvent event : dataEvents) {

            String path1 = event.getDataItem().getUri().getPath();
            //Log.d("DEBUG", "path: " + path1);

            // Check the data type
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // Check the data path
                String path = event.getDataItem().getUri().getPath();
                //Log.d("DEBUG", "Path: " + path);

                if (path.equals(HANDHELD_DATA_PATH)) {

                    dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
                    Long curTime = System.nanoTime();
                    Long prevTime = dataMap.getLong("time");
                    Long id = dataMap.getLong(ID);
                    Log.i("HANDHELD_STATISTIC", "The duration is: " + (curTime - prevTime)/1000000L + " ms"
                            + " ID: " + id + " size: " + dataMap.size());
                    Log.i("HANDHELD", "DataMap size received on watch.: " + dataMap.size() + " ID=" + id);
                }
            }
        }
    }


}
