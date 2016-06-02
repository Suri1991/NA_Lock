package com.hackathon.na_lock.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hackathon.na_lock.pojo.App;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by karthik on 26-05-2016.
 */

public class NALockDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "NALockDbHelper";
    private final Context context;

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NALock.db";

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";


    private static final String SQL_CREATE_APP_MONITOR =
            "CREATE TABLE IF NOT EXISTS " + NALockDbContract.AppUsageMonitor.TABLE_NAME + " ("
                    + NALockDbContract.AppUsageMonitor._ID + INTEGER_TYPE + " PRIMARY KEY"
                    + COMMA_SEP
                    + NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_NAME + TEXT_TYPE
                    + COMMA_SEP
                    + NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_PACKAGE_NAME + TEXT_TYPE
                    + COMMA_SEP
                    + NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_FOREGROUND_TIME
                    + INTEGER_TYPE + COMMA_SEP
                    + NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_RESTRICTION_TIME
                    + INTEGER_TYPE + " )";

    private static final String SQL_DELETE_APP_MONITOR =
            "DROP TABLE IF EXISTS " + NALockDbContract.AppUsageMonitor.TABLE_NAME;

    static Object ob = new Object();
    private static NALockDbHelper instance;

    private NALockDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;


    }

    public static NALockDbHelper getInstance(Context context) {

        synchronized (ob) {

            if (instance == null) {
                instance = new NALockDbHelper(context);
            }

            return instance;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_APP_MONITOR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(SQL_DELETE_APP_MONITOR);

        onCreate(db);
    }

    public void insertAppForRestriction(App app, Context context) {

        Log.d(TAG, "inserting " + app.getPackageName());
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();

            values.put(NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_NAME, app.getAppName());
            values.put(NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_PACKAGE_NAME, app.getPackageName());
            values.put(NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_FOREGROUND_TIME, app.getForegroundTime());
            values.put(NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_RESTRICTION_TIME, app.getRestrictionTime());


            db.insert(NALockDbContract.AppUsageMonitor.TABLE_NAME, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<App> getRestrictedApps() {
        List<App> restrictedApps = new ArrayList<App>();
        Cursor cursor = null;
        try (SQLiteDatabase db = this.getWritableDatabase()) {

            cursor = db.rawQuery("SELECT * FROM " + NALockDbContract.AppUsageMonitor.TABLE_NAME, null);

            if (cursor != null && cursor.moveToFirst()) {

                restrictedApps = new ArrayList<App>();
                do {

                    App app = new App();

                    String appName = cursor.getString(cursor.getColumnIndex(
                            NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_NAME));
                    app.setAppName(appName);

                    String packageName = cursor.getString(cursor.getColumnIndex(
                            NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_PACKAGE_NAME));
                    app.setPackageName(packageName);

                    Long restrictionTime = cursor.getLong(cursor.getColumnIndex(
                            NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_RESTRICTION_TIME));
                    app.setRestrictionTime(restrictionTime);

                    Long foregroundTime = cursor.getLong(cursor.getColumnIndex(
                            NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_FOREGROUND_TIME));
                    app.setForegroundTime(foregroundTime);

                    app.setRestricted(true);

                    restrictedApps.add(app);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return restrictedApps;
    }

    public List<String> getRestrictedAppNames() {
        List<String> restrictedApps = null;
        Cursor cursor = null;
        try (SQLiteDatabase db = this.getWritableDatabase()) {

            cursor = db.rawQuery("SELECT * FROM " + NALockDbContract.AppUsageMonitor.TABLE_NAME, null);

            if (cursor != null && cursor.moveToFirst()) {

                restrictedApps = new ArrayList<String>();
                do {


                    String packageName = cursor.getString(cursor.getColumnIndex(
                            NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_PACKAGE_NAME));


                    restrictedApps.add(packageName);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return restrictedApps;
    }


    public long getForegroundTime(String packageName) {
        Cursor cursor = null;
        try (SQLiteDatabase db = this.getWritableDatabase()) {

            cursor = db.rawQuery("SELECT " + NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_FOREGROUND_TIME +
                    " FROM " + NALockDbContract.AppUsageMonitor.TABLE_NAME + " WHERE " + NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_PACKAGE_NAME + " = '" + packageName + "'", null);

            if (cursor != null && cursor.moveToFirst()) {

                do {
                    return cursor.getLong(cursor.getColumnIndex(
                            NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_FOREGROUND_TIME));
                }
                while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return 0L;
    }

    public void updateAppUsage(long foregroundTime, String packageName) {

        Log.d(TAG, "Update usage time");
        try (SQLiteDatabase db = this.getWritableDatabase()) {

            ContentValues values = new ContentValues();
            values.put(NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_FOREGROUND_TIME, foregroundTime);

            int res = db.update(NALockDbContract.AppUsageMonitor.TABLE_NAME, values, NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_PACKAGE_NAME + "=?", new String[]{packageName});

            Log.d(TAG, "update usage time " + res);
        } catch (Exception e) {
            Log.d(TAG, "Exception ");
            e.printStackTrace();
        }

    }


    public void deleteApp(String packageName) {
        try (SQLiteDatabase db = this.getWritableDatabase()) {

            db.delete(NALockDbContract.AppUsageMonitor.TABLE_NAME, NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_PACKAGE_NAME + "=?", new String[]{packageName});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetTable() {
        Log.d(TAG, "resting table");
        try (SQLiteDatabase db = this.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_FOREGROUND_TIME, 0);
            db.update(NALockDbContract.AppUsageMonitor.TABLE_NAME, values, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAppRestriction(long restrictionTime, String packageName) {

        Log.d(TAG, "Update usage time");
        try (SQLiteDatabase db = this.getWritableDatabase()) {

            ContentValues values = new ContentValues();
            values.put(NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_RESTRICTION_TIME, restrictionTime);

            db.update(NALockDbContract.AppUsageMonitor.TABLE_NAME, values, NALockDbContract.AppUsageMonitor.COLUMN_NAME_APP_PACKAGE_NAME + "=?", new String[]{packageName});

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
