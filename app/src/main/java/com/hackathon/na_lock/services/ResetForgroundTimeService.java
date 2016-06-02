package com.hackathon.na_lock.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.hackathon.na_lock.databases.NALockDbHelper;

public class ResetForgroundTimeService extends IntentService {

    public ResetForgroundTimeService() {
        super("ResetForgroundTimeService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            resetForgroundTimeService();
        }
    }

    private void resetForgroundTimeService() {
        NALockDbHelper.getInstance(this).resetTable();
    }
}
