package com.hackathon.na_lock.services;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.hackathon.na_lock.LockerActivity;
import com.hackathon.na_lock.Util.NAUtils;
import com.hackathon.na_lock.databases.NALockDbHelper;
import com.hackathon.na_lock.pojo.App;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

public class AppMonitorService extends Service {
    private static final String TAG = "AppMonitorService";
    public Timer timer;
    public static boolean locker = false;
    private ContentResolver contentResolver;
    private PackageManager mPackageManager;
    private ActivityManager activityManager;
    String packageName;
    int counter;

    private void monitorApp() {

        try {
            activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            mPackageManager = getPackageManager();
            contentResolver = AppMonitorService.this.getContentResolver();

        } catch (Exception e) {
            Log.e(TAG, "Getting the shared preference Error " + e.toString());
        }

        timer = new Timer();

        timer.schedule(new TimerTask() {
            public void run() {

                Log.i(TAG, "Moniter App TimerTask started.... 1... !");

                /** PACKAGE */
                // this line is to get top activity's package name
                packageName = "";
                counter++;

                packageName = getForegroundTask();

                if (packageName != null && !packageName.equalsIgnoreCase(getApplicationContext().getPackageName())) {
                    if (isSelectedAppAndTimeExceeded(AppMonitorService.this, packageName)) {
                        Intent i = new Intent(AppMonitorService.this,
                                LockerActivity.class);

                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }
                }

//                if (counter == 10) {
//                    counter = 0;
//                    Intent appUsageCollectionIntent = new Intent(AppMonitorService.this, AppUsageCollectionJobService.class);
//                    startService(appUsageCollectionIntent);
//                }
            }
        }, 0, 1000);
    }

    private String getForegroundTask() {
        String currentApp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService("usagestats");
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        Log.e("adapter", "Current App in foreground is: " + currentApp);
        return currentApp;
    }

    private boolean isSelectedAppAndTimeExceeded(final Context context, String pkgNme) {
        List<App> restrictedAppList = NALockDbHelper.getInstance(context).getRestrictedApps();

        for (int i = 0; i < restrictedAppList.size(); i++) {
            App app = restrictedAppList.get(i);
            Log.d(TAG, " PackageName is " + app.getPackageName() + " ForegroundTime is " + app.getForegroundTime() + " RestrictionTime is " + app.getRestrictionTime());

            if (app.getPackageName().equals(pkgNme)) {
                if (app.getForegroundTime() >= app.getRestrictionTime()) {
                    return true;
                } else if (app.getForegroundTime() == (0.8 * app.getRestrictionTime())) {
                    final long time = app.getForegroundTime();
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Your usage time is crossed " + NAUtils.convertToMin(time), Toast.LENGTH_LONG).show();
                        }
                    });
                }
                NALockDbHelper.getInstance(context).updateAppUsage(app.getForegroundTime() + 1000, pkgNme);
            }
        }
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            if (timer != null)
                timer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "onStartCommand");

        try {

            monitorApp();

//            Notification note = new Notification(android.R.drawable.ic_lock_idle_lock,
//                    "NA_LOCK App", System.currentTimeMillis());
//
//            Intent i = new Intent(Intent.ACTION_MAIN);
//            i.putExtra("autoLuanch", false);
//            if (MDM.equals("error"))
//                i.setClassName("com.dfoeindia.one.master.student",
//                        "com.dfoeindia.one.master.student.LoginActivity");
//            else
//                i.setClassName("com.dfoeindia.one.master.student",
//                        "com.dfoeindia.one.master.student.HomeScreen");
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//            PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
//
//            note.setLatestEventInfo(this, "OneSteadyState", "App is Running",
//                    pi);
//            note.flags |= Notification.FLAG_NO_CLEAR;
//
//            startForeground(1337, note);

            Log.i(TAG, "AppMonitorService :  SERVICE STARTTED ");
        } catch (NullPointerException ex) {

            Log.e(TAG,
                    "AppMonitorService : in onStartCommand, NullPointerException is "
                            + ex.toString());
        } catch (Exception e) {

            Log.e(TAG, "AppMonitorService : in onStartCommand, exception is "
                    + e.toString());
        }
        return START_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        monitorApp();
        return null;
    }

    public String getTime(int inMinutesOrSeconds) {
        DateFormat dateFormat = null;

        if (inMinutesOrSeconds == 0) // return time including seconds
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        else if (inMinutesOrSeconds == 1) // return time excluding seconds
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date date = new Date();
        return dateFormat.format(date);
    }
}