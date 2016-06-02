package com.hackathon.na_lock.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hackathon.na_lock.Utils;

/**
 * Created by user on 11/20/2015.
 */
public class AppUsageCollectionTask extends AsyncTask<Object, Void, Boolean> {

    private static final String TAG = "AppUsageCollectionTask";

    private Context mContext;

    public AppUsageCollectionTask() {
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        mContext = (Context) params[0];
        if (mContext != null) {

            Utils.getAppUsageStats(mContext);
        } else {
            return false;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        Log.d(TAG, "Inside onPostExecute");
    }
}
