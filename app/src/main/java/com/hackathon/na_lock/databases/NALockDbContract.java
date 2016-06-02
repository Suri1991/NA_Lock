package com.hackathon.na_lock.databases;

import android.provider.BaseColumns;

/**
 * Created by karthik on 26-05-2016.
 */
public class NALockDbContract {

    public NALockDbContract() {
    }

    /* Inner class that defines the AppUsage table contents */
    public static abstract class AppUsageMonitor implements BaseColumns {

        public static final String TABLE_NAME = "app_usage_monitor";
        public static final String COLUMN_NAME_APP_NAME = "app_name";
        public static final String COLUMN_NAME_APP_PACKAGE_NAME = "package_name";
        public static final String COLUMN_NAME_APP_FOREGROUND_TIME = "app_foreground_time";
        public static final String COLUMN_NAME_APP_RESTRICTION_TIME = "app_restriction_time";

    }
}
