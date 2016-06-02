package com.hackathon.na_lock;

import android.app.AlarmManager;
import android.app.AppOpsManager;
import android.app.PendingIntent;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.hackathon.na_lock.databases.NALockDbHelper;
import com.hackathon.na_lock.pojo.App;

import java.util.Calendar;
import java.util.List;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    //Unit of time
    public static final int SECONDS_IN_MILLISECONDS = 1000;
    public static final int MINUTES_IN_MILLISECONDS = SECONDS_IN_MILLISECONDS * 60;
    public static final int HOUR_IN_MILLISECONDS = MINUTES_IN_MILLISECONDS * 60;
    public static final int DAY_IN_MILLISECONDS = HOUR_IN_MILLISECONDS * 24;

    public static final String IGNORE_SYSTEM_APPLICATIONS = "ignore_system_app";
    public static final String PREF_FILE_NAME = "analytics_pref_file";

    public static final long MIN_IN_MILLSEC = 60 * 1000;

    /**
     * gives json array string of usage stats for each application within given time frame
     *
     * @param mContext
     * @return
     */
    public static void getAppUsageStats(Context mContext) {
        long endTime = 0;
        long beginTime = 0;
        UsageStatsManager usageStatsManager = (UsageStatsManager) mContext.getSystemService(Context.USAGE_STATS_SERVICE);

        endTime = System.currentTimeMillis();
        beginTime = endTime - (1500 * Utils.HOUR_IN_MILLISECONDS);

        if (!isUsageStatsPermissionEnabled(mContext)) {
            Log.d(TAG, "isUsageStatsPermissionEnabled : false");
            return;
        }

        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                beginTime, endTime);//0, System.currentTimeMillis());

        if (queryUsageStats.size() == 0) {
            Log.d(TAG, "UsageStats size is 0");
            return;
        }
        getUsageArray(mContext, beginTime, endTime, queryUsageStats);

        return;
    }

    /**
     * A method to check if permission for collecting UsageStats is enabled in Security Settings
     *
     * @param context
     * @return
     */
    public static boolean isUsageStatsPermissionEnabled(Context context) {
        ApplicationInfo applicationInfo = getApplicationInfo(context, context.getPackageName());
        if (applicationInfo != null) {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid,
                    applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        }
        return false;
    }

    /**
     * Returns ApplicationInfo object for a given packageName
     *
     * @param context
     * @param packageName
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return applicationInfo;
        }
    }

    private static void getUsageArray(Context mContext, long beginTime, long endTime, List<UsageStats> queryUsageStats) {
        for (UsageStats usageStatObj : queryUsageStats) {
            String packageName = usageStatObj.getPackageName();
            if (isAppLaunchable(packageName, mContext) && isRestrictedPackage(mContext, packageName)) {
                // if (usageStatObj.getFirstTimeStamp() > beginTime && usageStatObj.getFirstTimeStamp() < endTime)
                getUsageObject(usageStatObj, mContext);
            }
        }
    }

    private static boolean isRestrictedPackage(Context context, String packageName) {

        try {
            List<String> restrictedAppNames = NALockDbHelper.getInstance(context).getRestrictedAppNames();

            if (restrictedAppNames.contains(packageName))
                return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private static void getUsageObject(UsageStats usageStatObj, Context mContext) {
        String packageName = usageStatObj.getPackageName();
        try {
            long previousForeGroundTime = NALockDbHelper.getInstance(mContext).getForegroundTime(packageName);

            /*if (previousForeGroundTime > usageStatObj.getTotalTimeInForeground())
                NALockDbHelper.getInstance(mContext).updateAppUsage(previousForeGroundTime + usageStatObj.getTotalTimeInForeground(), packageName);
            else*/
            NALockDbHelper.getInstance(mContext).updateAppUsage(usageStatObj.getTotalTimeInForeground(), packageName);

            Log.i(TAG, "usageStatObj.getPackageName() is : " + packageName + " Foregroundtime : " + usageStatObj.getTotalTimeInForeground() + " FirstTimeStamp : " + usageStatObj.getFirstTimeStamp() + " LastTimeUsed :  " + usageStatObj.getLastTimeUsed());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAppLaunchable(String packageName, Context ctx) {
        PackageManager packageManager = ctx.getPackageManager();
        return null != packageManager.getLaunchIntentForPackage(packageName);
    }

    public static String getFromPreference(Context context, String key, String defaultValue) {
        return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE).getString(key, defaultValue);
    }

    public static boolean isSystemApplication(Context context, String packageName) {
        return !getApplicationInfo(context, packageName).sourceDir.contains("/data");
    }

}