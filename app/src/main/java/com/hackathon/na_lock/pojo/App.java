package com.hackathon.na_lock.pojo;

import android.graphics.drawable.Drawable;

/**
 * Created by karthik on 26-05-2016.
 */
public class App {

    private String packageName;
    private String appName;
    private Drawable appIcon;
    private long foregroundTime;
    private long restrictionTime;
    private boolean isRestricted;

    public App() {
    }

    public App(Drawable appIcon, String packageName, String appName) {
        this.appIcon = appIcon;
        this.packageName = packageName;
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public long getForegroundTime() {
        return foregroundTime;
    }

    public void setForegroundTime(long foregroundTime) {
        this.foregroundTime = foregroundTime;
    }

    public long getRestrictionTime() {
        return restrictionTime;
    }

    public void setRestrictionTime(long restrictionTime) {
        this.restrictionTime = restrictionTime;
    }

    public boolean isRestricted() {
        return isRestricted;
    }

    public void setRestricted(boolean isRestricted) {
        this.isRestricted = isRestricted;
    }
}
