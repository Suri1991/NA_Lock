package com.hackathon.na_lock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hackathon.na_lock.services.AppMonitorService;

public class StartServiceAtBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Intent i = new Intent(context, AppMonitorService.class);
            try {
                context.stopService(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            context.startService(i);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("On BOOt", e.getMessage());
        }
    }
}
