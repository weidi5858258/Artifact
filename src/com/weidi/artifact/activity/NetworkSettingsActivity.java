package com.weidi.artifact.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.db.dao.NetworkLimitDao;
import com.weidi.artifact.db.bean.AppInfos;
import com.weidi.artifact.db.bean.AppNetworkLimitInfos;
import com.weidi.utils.Api;
import com.weidi.utils.MyToast;
import com.weidi.utils.MyUtils;


//主要干两件事：
//一、列出每个程序上传下载的流量大小。
//二、对没有选中的程序进行网络控制。
public class NetworkSettingsActivity extends Activity implements OnClickListener {
    private Context mContext;
    private Intent intent;
    private ListView lv_networksettings;
    private NetworkAdapter adapter;
    private List<AppInfos> list;
    private List<AppInfos> userList;
    private List<AppInfos> systemList;
    private List<AppNetworkLimitInfos> netList;
    private List<Integer> uids3g;
    private List<Integer> uidsWifi;
    private NetworkLimitDao dao;
    private SharedPreferences sp;
    private View view;
    private ViewHolder holder;
    //	private ProgressBar pb_networksettings_progress;
    //	private TextView pb_networksettings_alert;
    private Button apply;
    private boolean hasroot = false;
    private boolean result;//运用规则返回的结果
    private boolean flag;//运用规则返回的结果
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networksettings);
        mContext = NetworkSettingsActivity.this;
        userList = new ArrayList<AppInfos>();
        systemList = new ArrayList<AppInfos>();
        dao = new NetworkLimitDao(mContext);
        netList = dao.queryAll();
        hasroot = Api.hasRootAccess(mContext, true);//检查系统是否已root，true为已root
        sp = mContext.getSharedPreferences("config", Context.MODE_PRIVATE);
        lv_networksettings = (ListView) this.findViewById(R.id.lv_networksettings);
        //		pb_networksettings_progress = (ProgressBar) this.findViewById(R.id
        // .pb_networksettings_progress);
        //		pb_networksettings_alert = (TextView) this.findViewById(R.id
        // .pb_networksettings_alert);
        apply = (Button) this.findViewById(R.id.apply);
        apply.setOnClickListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        apply.setVisibility(Button.INVISIBLE);
                        //						pb_networksettings_progress.setVisibility
                        // (ProgressBar.VISIBLE);
                        //						pb_networksettings_alert.setVisibility(TextView
                        // .VISIBLE);
                    }
                });
                list = MyUtils.getInstalledApplicationInfos(mContext);
                int mobile;
                int wifi;
                for (AppInfos info : list) {
                    if (!dao.isPackageNameExist(info.getPackageName())) {
                        //默认为程序可以联网
                        dao.add(info.getPackageName(), info.getUid(), 0, 0);
                    }
                    for (AppNetworkLimitInfos netInfo : netList) {
                        if (info.getPackageName().equals(netInfo.getPackageName())) {
                            if (info.isUserApp()) {
                                info.setMobile(netInfo.getMobile());
                                info.setWifi(netInfo.getWifi());
                                userList.add(info);
                            } else {
                                info.setMobile(netInfo.getMobile());
                                info.setWifi(netInfo.getWifi());
                                systemList.add(info);
                            }
                            break;
                        }
                    }

                }
                if (hasroot) {
                    result = applyIptables();//运用规则
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        adapter = new NetworkAdapter();
                        lv_networksettings.setAdapter(adapter);
                        if (result) {
                            apply.setVisibility(Button.VISIBLE);
                        } else {
                            apply.setVisibility(Button.INVISIBLE);
                        }
                        //						pb_networksettings_progress.setVisibility
                        // (ProgressBar.INVISIBLE);
                        //						pb_networksettings_alert.setVisibility(TextView
                        // .INVISIBLE);
                    }
                });
            }
        }).start();

    }//onCreate()

    private class NetworkAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userList.size() + systemList.size() + 2;
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
            final AppInfos info;
            if (position == 0) {
                TextView userTV = new TextView(mContext);
                userTV.setText("用户程序：" + userList.size() + "个");
                return userTV;
            } else if (position == userList.size() + 1) {
                TextView systemTV = new TextView(mContext);
                systemTV.setText("系统程序：" + systemList.size() + "个");
                return systemTV;
            } else if (position <= userList.size() + 1) {
                info = userList.get(position - 1);
            } else {
                info = systemList.get(position - userList.size() - 2);
            }
            if (convertView != null && convertView instanceof LinearLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(mContext, R.layout.networksettings_view_item, null);
                holder = new ViewHolder();
                holder.iv_networksettings_icon = (ImageView) view.findViewById(R.id
                        .iv_networksettings_icon);
                holder.tv_networksettings_appname = (TextView) view.findViewById(R.id
                        .tv_networksettings_appname);
                holder.tv_networksettings_datainfo = (TextView) view.findViewById(R.id
                        .tv_networksettings_datainfo);
                holder.cb_networksettings_g23 = (CheckBox) view.findViewById(R.id
                        .cb_networksettings_g23);
                holder.cb_networksettings_wifi = (CheckBox) view.findViewById(R.id
                        .cb_networksettings_wifi);
                view.setTag(holder);
            }

            holder.iv_networksettings_icon.setImageDrawable(info.getIcon());
            holder.tv_networksettings_appname.setText(info.getAppName());
            //-1.00B
            long tx = TrafficStats.getUidTxBytes(info.getUid());
            long rx = TrafficStats.getUidRxBytes(info.getUid());
            long oldtx = sp.getLong("tx" + info.getUid(), -1);
            long oldrx = sp.getLong("rx" + info.getUid(), -1);
            holder.tv_networksettings_datainfo.setText("上传：" + Formatter.formatFileSize(mContext,
                    (tx - oldtx)) + " 下载：" + Formatter.formatFileSize(mContext, (rx - oldrx)));

            if (hasroot) {//手机已经root
                if (info.getMobile() == 1) {
                    holder.cb_networksettings_g23.setChecked(true);
                } else {
                    holder.cb_networksettings_g23.setChecked(false);
                }
                if (info.getWifi() == 1) {
                    holder.cb_networksettings_wifi.setChecked(true);
                } else {
                    holder.cb_networksettings_wifi.setChecked(false);
                }
            } else {
                holder.cb_networksettings_g23.setVisibility(CheckBox.INVISIBLE);
                holder.cb_networksettings_wifi.setVisibility(CheckBox.INVISIBLE);
            }

            holder.cb_networksettings_g23.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        dao.updateMobile(info.getPackageName(), 1);
                    } else {
                        dao.updateMobile(info.getPackageName(), 0);
                    }
                }
            });
            holder.cb_networksettings_wifi.setOnCheckedChangeListener(new OnCheckedChangeListener
                    () {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        dao.updateWifi(info.getPackageName(), 1);
                    } else {
                        dao.updateWifi(info.getPackageName(), 0);
                    }
                }
            });

            return view;
        }

    }//NetworkAdapter

    private static class ViewHolder {
        ImageView iv_networksettings_icon;
        TextView tv_networksettings_appname;
        TextView tv_networksettings_datainfo;
        CheckBox cb_networksettings_g23;
        CheckBox cb_networksettings_wifi;
    }

    private boolean run = true;

    @Override
    public void onClick(View v) {
        if (run) {
            run = false;
            switch (v.getId()) {
                case R.id.apply: {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            flag = applyIptables();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if (flag) {
                                        MyToast.show("亲，运用规则成功");
                                    } else {
                                        MyToast.show("亲，运用规则失败");
                                    }
                                }
                            });
                        }
                    }).start();
                    break;
                }
            }
            run = true;
        }

    }

    public boolean applyIptables() {//黑名单模式：被选中的应用不能联网（1为选中，0为不选中）
        uids3g = new ArrayList<Integer>();
        uidsWifi = new ArrayList<Integer>();
        netList = dao.queryAll();
        for (AppNetworkLimitInfos info : netList) {
            if (info.getMobile() == 1) {
                uids3g.add(info.getUid());
            }
            if (info.getWifi() == 1) {
                uidsWifi.add(info.getUid());
            }
        }
        Api.purgeIptables(mContext, false);
        return Api.applyIptablesRulesImpl(mContext, uidsWifi, uids3g, false);
    }

}
