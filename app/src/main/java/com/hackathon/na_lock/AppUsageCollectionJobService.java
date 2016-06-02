package com.hackathon.na_lock;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.hackathon.na_lock.asynctasks.AppUsageCollectionTask;

import java.util.concurrent.ExecutionException;

public class AppUsageCollectionJobService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    private static final String TAG = "AppUsageCollection";

    public AppUsageCollectionJobService() {
        super("AnalyticIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            callAppUsageCollectionTask();
        }
    }

    private void callAppUsageCollectionTask() {
        try {
            Log.d(TAG, "Starting AppUsageCollectionTask");
            new AppUsageCollectionTask().execute(this).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}