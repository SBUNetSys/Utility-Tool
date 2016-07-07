package com.cscao.apps.ntplib;

import android.content.Context;
import android.util.Log;

import com.chrisplus.rootmanager.RootManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by qqcao on 7/4/16.
 * <p/>
 * Utils for ntp operations
 */
public class NtpUtil {

    private static long offset=0;
    private static String details;

    private static boolean gainAccess() {
        return RootManager.getInstance().hasRooted()
                && RootManager.getInstance().obtainPermission();
    }


    public static boolean prepare(String server) {
        NTPClient ntpClient = new NTPClient(server);
        offset = ntpClient.getOffset();
        details = ntpClient.getDetails();
        return offset != 0;
    }

    public static boolean prepare() {
        NTPClient ntpClient = new NTPClient();
        offset = ntpClient.getOffset();
        details = ntpClient.getDetails();
        return offset != 0;
    }

    public static String getDetails() {
        return details;
    }

    public static long getOffset() {
        return offset;
    }


    public static void calibrate(Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT, Locale.ENGLISH);
       String time= simpleDateFormat.format(new Date(offset + System.currentTimeMillis()));
        Log.d("time", "time is: "+time);
                if (ShellInterface.isSuAvailable()) {
            ShellInterface.runCommand("date "+time);

        }
//        if (ShellInterface.isSuAvailable()) {
//            ShellInterface.runCommand("chmod 666 /dev/rtc0");
//            SystemClock.setCurrentTimeMillis(offset + System.currentTimeMillis());
//            ShellInterface.runCommand("chmod 664 /dev/rtc0");
//        }

//        if (gainAccess()) {
//            Result result = RootManager.getInstance().runCommand("chmod 777 /dev/alarm");
//            if (result.getResult()) {
//                long currentTime = System.currentTimeMillis();
//                Log.d("lib","cur time:" + currentTime);
////                AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
////                am.setTime(offset + currentTime);
//
//                SystemClock.setCurrentTimeMillis(offset + currentTime);
////                RootManager.getInstance().runCommand("chmod 664 /dev/alarm");
//                return true;
//
//            } else {
//                return false;
//            }
//
//        } else {
//            return false;
//        }
    }
}
