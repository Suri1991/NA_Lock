package com.hackathon.na_lock.Util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;

import com.hackathon.na_lock.databases.NALockDbHelper;
import com.hackathon.na_lock.pojo.App;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karthik on 27-05-2016.
 */
public class NAUtils {

    public static List<App> getInstalledApps(Context context) {
        List<String> resPackageNames = NALockDbHelper.getInstance(context).getRestrictedAppNames();
        List<App> res = new ArrayList<App>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            if (context.getPackageManager().getLaunchIntentForPackage(p.applicationInfo.packageName) != null && (resPackageNames == null || !resPackageNames.contains(p.packageName))) {
                String appName = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
                String packageName = p.packageName;
                Drawable icon = p.applicationInfo.loadIcon(context.getPackageManager());
                res.add(new App(icon, packageName, appName));
            }
        }
        return res;
    }

    public static String convertToMin(long millisec) {
        if (millisec >= (60 * 60 * 1000)) {
            return "" + millisec / (60 * 60 * 1000) + " hour";
        } else if (millisec >= (60 * 1000)) {
            return "" + millisec / (60 * 1000) + " mins";
        } else if (millisec < (60 * 1000)) {
            return "" + millisec / 1000 + " second";
        }
        return "";
    }
}
