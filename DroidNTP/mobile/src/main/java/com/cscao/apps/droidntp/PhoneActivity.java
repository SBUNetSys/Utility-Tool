package com.cscao.apps.droidntp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cscao.apps.ntplib.NtpUtil;

public class PhoneActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button getTime = (Button) findViewById(R.id.button_get_time);
        getTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QueryTask().execute();
            }
        });


        Button calibrate = (Button) findViewById(R.id.button_calibrate);
        calibrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CalibrateTask().execute();
//                if (RootManager.getInstance().hasRooted()) {
//                   if (RootManager.getInstance().obtainPermission()){
//                       Toast.makeText(getApplicationContext(), "success request root ", Toast.LENGTH_SHORT).show();
//
//                   }else {
//                       Toast.makeText(getApplicationContext(), "failed to request root", Toast.LENGTH_SHORT).show();
//
//                   }
//
//                } else {
//                    Toast.makeText(getApplicationContext(), "not rooted", Toast.LENGTH_SHORT).show();
//                }

            }
        });
//pool.ntp.org
//        SntpClient client = new SntpClient();
//        if (client.requestTime("clock.psu.edu", 10000)) {
//            long now = client.getNtpTime() + SystemClock.elapsedRealtime() - client.getNtpTimeReference();
//            Log.d("time", "" + now);
//        } else {
//            Log.d("time", "failed");
//
//        }

    }

    class QueryTask extends AsyncTask<Void, Void, Long> {
        @Override
        protected Long doInBackground(Void... params) {
            NtpUtil.prepare();
            return NtpUtil.getOffset();
        }

        @Override
        protected void onPostExecute(Long offset) {
            Toast.makeText(getApplicationContext(), "offset is: " + offset,Toast.LENGTH_SHORT ).show();

        }

    }

    class CalibrateTask extends AsyncTask<Void, Void, Long> {
        @Override
        protected Long doInBackground(Void... params) {
            NtpUtil.prepare();
            NtpUtil.calibrate(getApplicationContext());
            return NtpUtil.getOffset();
        }

        @Override
        protected void onPostExecute(Long offset) {
            Toast.makeText(getApplicationContext(), "offset is: " + offset,Toast.LENGTH_SHORT ).show();

        }

    }
}
