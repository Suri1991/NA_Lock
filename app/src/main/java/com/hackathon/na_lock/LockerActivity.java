package com.hackathon.na_lock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hackathon.na_lock.services.AppMonitorService;

public class LockerActivity extends Activity {

    long time;
    public  void setTimer(long time1){
        this.time=time1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("LockerActvity", "Oncreat");

    }

    @Override
    public void onBackPressed() {
        //do nothing
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("LockerActvity", "OnPause");
        finishAndRemoveTask();


    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("LockerActvity", "OnStop");
        finishAndRemoveTask();
    }


}
