package com.weidi.artifact.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RecentTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.db.bean.AppInfos;
import com.weidi.artifact.db.bean.RecentApp;

public class RecentTaskActivity extends Activity {
    private Context mContext;
    private GridView lv_recenttask;
    private List<RecentTaskInfo> list;
    private List<RecentApp> taskList;
    private List<AppInfos> appList;
    private Intent intent;
    private RecentTaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.activity_recenttask);
        mContext = RecentTaskActivity.this;

        list = ((MyApplication) getApplication()).mActivityManager.getRecentTasks(5858,
                ActivityManager.RECENT_IGNORE_UNAVAILABLE);
        appList = ((MyApplication) getApplication()).appList;
        RecentApp app = null;
        String packageName = null;
        if (list != null) {
            taskList = new ArrayList<RecentApp>();
            for (RecentTaskInfo li : list) {
                packageName = li.baseIntent.getComponent().getPackageName();
                for (AppInfos info : appList) {
                    if (packageName.equals(info.getPackageName())) {
                        //创建一个能够被执行的应用对象，包括：包名、应用名、图标、Intent
                        app = new RecentApp();
                        app.packageName = packageName;
                        app.appName = info.getAppName();
                        app.icon = info.getIcon();
                        app.intent = li.baseIntent;

                        if ("com.tencent.mobileqq".equals(packageName)) {
                            taskList.add(0, app);
                        } else if ("com.tencent.mm".equals(packageName)) {
                            taskList.add(0, app);
                        } else if (getPackageName().equals(packageName)
                                || (Constant.LAUNCHER).equals(packageName)) {
                            taskList.remove(app);
                        } else {
                            taskList.add(app);
                        }
                        break;
                    }
                }
            }

            if (taskList.size() > 15) {
                do {
                    taskList.remove(taskList.size() - 1);
                } while (taskList.size() > 15);
            }
        }

        lv_recenttask = (GridView) this.findViewById(R.id.lv_recenttask);
        adapter = new RecentTaskAdapter();
        lv_recenttask.setAdapter(adapter);

        //点击后启动那个应用
        lv_recenttask.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = taskList.get(position).intent;
                if (intent != null) {
                    String packageName = intent.getComponent().getPackageName();
                    if (getPackageName().equals(packageName)) {
                        intent = new Intent(mContext, MainActivity.class);
                        mContext.startActivity(intent);
                    } else {
                        mContext.startActivity(intent);
                    }
                }
                RecentTaskActivity.this.finish();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        list = null;
        appList = null;
        taskList = null;
    }

    private class RecentTaskAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (taskList.size() > 0) {
                return taskList.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            RecentApp app = null;
            if (convertView == null) {
                holder = new ViewHolder();
                view = View.inflate(mContext, R.layout.recenttask_view_item, null);
                holder.iv_recenttask_icon = (ImageView) view.findViewById(R.id.iv_recenttask_icon);
                holder.tv_recenttask_appname = (TextView) view.findViewById(R.id
                        .tv_recenttask_appname);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }
            app = taskList.get(position);
            holder.iv_recenttask_icon.setImageDrawable(app.icon);
            holder.tv_recenttask_appname.setText(app.appName);
            return view;
        }

    }

    class ViewHolder {
        ImageView iv_recenttask_icon;
        TextView tv_recenttask_appname;
    }

}
