package com.hackathon.na_lock;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hackathon.na_lock.Util.NAUtils;
import com.hackathon.na_lock.adapter.AppRecyclerAdapter;
import com.hackathon.na_lock.pojo.App;

import java.util.List;

public class AddAppActivity extends AppCompatActivity {

    private static final String TAG = "AddAppActivity";
    private RecyclerView mAppRecyclerView;
    private AppRecyclerAdapter mAdapter;
    private List<App> mAppList;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_app);

        mContext = this;
        mAppList = NAUtils.getInstalledApps(mContext);

        Log.d(TAG, mAppList.size() + "applist " + mAppList.get(0).getAppName());
        mAdapter = new AppRecyclerAdapter(mAppList, mContext);
        mAppRecyclerView = (RecyclerView) findViewById(R.id.app_list_recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mAppRecyclerView.setLayoutManager(mLayoutManager);
        mAppRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAppRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
