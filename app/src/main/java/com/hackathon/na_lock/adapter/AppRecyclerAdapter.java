package com.hackathon.na_lock.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.hackathon.na_lock.R;
import com.hackathon.na_lock.Util.NAUtils;
import com.hackathon.na_lock.Utils;
import com.hackathon.na_lock.databases.NALockDbHelper;
import com.hackathon.na_lock.pojo.App;

import java.util.List;

/**
 * Created by karthik on 26-05-2016.
 */
public class AppRecyclerAdapter extends RecyclerView.Adapter<AppRecyclerAdapter.MyViewHolder> {

    private static final String TAG = "AppRecyclerAdapter";
    private List<App> mAppList;
    private Context mContext;
    private OnItemClickListener mItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView icon;
        public TextView appName, duration;
        public Switch mSwitch;


        public MyViewHolder(View view) {
            super(view);

            icon = (ImageView) view.findViewById(R.id.appIcon);
            appName = (TextView) view.findViewById(R.id.appName);
            mSwitch = (Switch) view.findViewById(R.id.toggle);
            duration = (TextView) view.findViewById(R.id.duration);
            view.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if (mItemClickListener != null)
                mItemClickListener.onItemClick(v, getPosition());
        }
    }


    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public AppRecyclerAdapter(List<App> appList, Context context) {
        this.mAppList = appList;
        this.mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.app_list_row, parent, false);
        /*itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"itemClicked",Toast.LENGTH_LONG).show();
            }
        });*/

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final App app = mAppList.get(position);

        holder.appName.setText(app.getAppName());
        if(app.getAppIcon() != null)
            holder.icon.setImageDrawable(app.getAppIcon());
        else
            holder.icon.setVisibility(View.GONE);
        if (app.isRestricted()) {
            holder.mSwitch.setChecked(true);
            holder.duration.setText("Usage Limit: " + NAUtils.convertToMin(app.getRestrictionTime()));
        } else {
            holder.mSwitch.setChecked(false);
        }

        holder.mSwitch.setTag(mAppList.get(position));

        holder.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App appToInsert = (App) v.getTag();
                appToInsert.setRestricted(((Switch) v).isChecked());


                if (appToInsert.isRestricted()) {
                    appToInsert.setRestrictionTime(30 * Utils.MIN_IN_MILLSEC);
                    appToInsert.setForegroundTime(0);
                    NALockDbHelper.getInstance(mContext).insertAppForRestriction(appToInsert, mContext);
                } else
                    NALockDbHelper.getInstance(mContext).deleteApp(appToInsert.getPackageName());
            }
        });


    }

    @Override
    public int getItemCount() {

        if (mAppList != null)
            return mAppList.size();
        else
            return 0;
    }
}