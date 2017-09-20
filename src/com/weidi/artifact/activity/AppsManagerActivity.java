package com.weidi.artifact.activity;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.Intent;
//import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.db.dao.AppLockDao;
import com.weidi.artifact.db.bean.AppInfos;
//import com.weidi.callsystemmethod.ICallSystemMethod;
import com.weidi.log.Log;
import com.weidi.utils.MyToast;
import com.weidi.utils.MyUtils;

public class AppsManagerActivity extends Activity {
    private static final String TAG = "AppsManagerActivity";
    private Context mContext;
    private WindowManager wm;
    private PackageManager pm;
    private ActivityManager am;
    private PopupWindow pw;
    private ListView appsmanager_ll_appsinfo;
    private TextView appsmanager_tv_phonememoryinfo;
    private TextView appsmanager_tv_sdinfo;
    private TextView tv_appsmanager_appcounts;
    private AppsManagerInfoAdapter adapter;
    private List<AppInfos> list;
    private List<AppInfos> userList;
    private List<AppInfos> systemList;
    private ProgressBar pb_appsmanager_progress;
    private TextView pb_appsmanager_alert;
    private StatFs sf;
    private ViewHolder holder;
    private AppLockDao dao;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appsmanager);
        mContext = AppsManagerActivity.this;
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        pm = mContext.getPackageManager();
        am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        appsmanager_ll_appsinfo = (ListView) findViewById(R.id.appsmanager_ll_appsinfo);
        appsmanager_tv_phonememoryinfo = (TextView) findViewById(R.id
                .appsmanager_tv_phonememoryinfo);
        appsmanager_tv_sdinfo = (TextView) findViewById(R.id.appsmanager_tv_sdinfo);
        tv_appsmanager_appcounts = (TextView) findViewById(R.id.tv_appsmanager_appcounts);
        pb_appsmanager_progress = (ProgressBar) findViewById(R.id.pb_appsmanager_progress);
        pb_appsmanager_alert = (TextView) findViewById(R.id.pb_appsmanager_alert);

        dao = new AppLockDao(mContext);

        loadData();

        appsmanager_ll_appsinfo.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                AppInfos info;
                dismissPopupWindow();
                if (position == 0) {
                    return;
                } else if (position == userList.size() + 1) {
                    return;
                } else if (position <= userList.size() + 1) {
                    info = userList.get(position - 1);
                } else {
                    info = systemList.get(position - userList.size() - 2);
                }
                View v = View.inflate(mContext, R.layout.appinfos_oper_view_item, null);
                LinearLayout ll_run = (LinearLayout) v.findViewById(R.id.ll_run);
                LinearLayout ll_uninstall = (LinearLayout) v.findViewById(R.id.ll_uninstall);
                LinearLayout ll_show = (LinearLayout) v.findViewById(R.id.ll_show);
                MyOnClick ocl = new MyOnClick(info);
                ll_run.setOnClickListener(ocl);
                ll_uninstall.setOnClickListener(ocl);
                ll_show.setOnClickListener(ocl);

                pw = new PopupWindow(v, -2, -2);//-2为包裹内容
                pw.setContentView(v);
                pw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                int[] location = new int[2];
                view.getLocationInWindow(location);
                int x = 25;
                int y = 5;
                pw.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, location[0] + MyUtils
                        .dip2px(mContext, x), location[1] - view.getHeight() - MyUtils.dip2px
                        (mContext, 5));
                //动画效果
                //				AnimationSet set = new AnimationSet(false);
                //参数不知道什么意思
                //				ScaleAnimation sa = new ScaleAnimation(1.0f, 0.3f, 10.f, 0.3f,
                // Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
                //				AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
                //				set.addAnimation(sa);
                //				set.addAnimation(aa);
                //				view.startAnimation(set);
            }
        });

        appsmanager_ll_appsinfo.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                dismissPopupWindow();
                if (userList != null && systemList != null) {
                    if (firstVisibleItem < userList.size() + 1) {
                        tv_appsmanager_appcounts.setVisibility(TextView.VISIBLE);
                        tv_appsmanager_appcounts.setText("用户程序：" + userList.size() + "个");
                    } else {
                        tv_appsmanager_appcounts.setVisibility(TextView.VISIBLE);
                        tv_appsmanager_appcounts.setText("系统程序：" + systemList.size() + "个");
                    }
                }
            }
        });

        //		appsmanager_ll_appsinfo.setOnScrollListener(new PauseOnScrollListener(taskHandler,
        // pauseOnScroll, pauseOnFling));

        //长点击事件
        appsmanager_ll_appsinfo.setOnItemLongClickListener(new AdapterView
                .OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                String packageName = null;
                if (position == 0) {
                    return true;
                } else if (position == userList.size() + 1) {
                    return true;
                } else if (position < userList.size() + 1) {
                    int newPosition = position - 1;
                    packageName = userList.get(newPosition).getPackageName();
                } else {
                    int newPosition = position - userList.size() - 2;
                    packageName = systemList.get(newPosition).getPackageName();
                }
                holder = (ViewHolder) view.getTag();
                if (dao.query(packageName)) {
                    dao.delete(packageName);
                    holder.icon_applock.setImageResource(R.drawable.unlock);
                } else {
                    dao.add(packageName);
                    holder.icon_applock.setImageResource(R.drawable.lock);
                }
                return true;
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissPopupWindow();
    }

    private void dismissPopupWindow() {
        if (pw != null && pw.isShowing()) {
            pw.dismiss();
            pw = null;
        }
    }

    private void loadData() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        appsmanager_tv_phonememoryinfo.setText("ROM Available：" + getAvailableSize(path));
        if (getSDPath() != null) {
            appsmanager_tv_sdinfo.setText("SD Available：" + getAvailableSize(getSDPath()));
        } else {
            appsmanager_tv_sdinfo.setText("找不到外置存储卡");
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        pb_appsmanager_progress.setVisibility(ProgressBar.VISIBLE);
                        pb_appsmanager_alert.setVisibility(TextView.VISIBLE);
                    }
                });
                list = MyUtils.getInstalledApplicationInfos(mContext);
                userList = new ArrayList<AppInfos>();
                systemList = new ArrayList<AppInfos>();
                for (AppInfos l : list) {
                    getPackageSizeInfo(mContext, l.getPackageName());
                    boolean isUserApp = l.isUserApp();
                    if (isUserApp) {
                        userList.add(l);
                    } else {
                        systemList.add(l);
                    }
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (adapter == null) {
                            adapter = new AppsManagerInfoAdapter();
                            appsmanager_ll_appsinfo.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                        pb_appsmanager_progress.setVisibility(ProgressBar.INVISIBLE);
                        pb_appsmanager_alert.setVisibility(TextView.INVISIBLE);
                    }
                });
            }
        }).start();
    }

    //得到可用的手机运行内存大小
    private String getRAMAvailableSize() {
        MemoryInfo outInfo = new MemoryInfo();
        am.getMemoryInfo(outInfo);
        long availMem = outInfo.availMem;
        //		long totalMem = outInfo.totalMem;
        return Formatter.formatFileSize(mContext, availMem);
    }

    //得到外置存储卡的路径n
    private String getSDPath() {
        File innerFile = Environment.getExternalStorageDirectory();
        File parentFile = innerFile.getParentFile();
        if (parentFile != null) {
            File[] files = parentFile.listFiles();
            for (File file : files) {
                if (file != null && file.length() > 0 && !innerFile.getAbsolutePath().equals(file
                        .getAbsolutePath())) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    //得到可用的存储空间
    private String getAvailableSize(String path) {
        sf = new StatFs(path);
        long blockSize = sf.getBlockSize();
        long availableBlocks = sf.getAvailableBlocks();
        long availableSize = blockSize * availableBlocks;
        return Formatter.formatFileSize(mContext, availableSize);
        //		long freeBlocks = sf.getFreeBlocks();
        //		long freeSize = blockSize * freeBlocks;//结果与availableSize一样
    }

    //点击事件：启动程序、卸载程序、分享程序
    private class MyOnClick implements OnClickListener {
        private AppInfos info;

        public MyOnClick(AppInfos info) {
            this.info = info;
        }

        @Override
        public void onClick(View v) {
            dismissPopupWindow();
            Intent intent;
            switch (v.getId()) {
                case R.id.ll_run: {
                    if (info != null) {
                        intent = pm.getLaunchIntentForPackage(info.getPackageName());
                        if (intent != null && !"com.aowin.mobilesafe".equals(info.getPackageName
                                ())) {
                            startActivity(intent);
                        } else if ("com.aowin.mobilesafe".equals(info.getPackageName())) {
                            return;
                        } else {
                            MyToast.show("我无能为力，启动不了这个东西");
                        }
                    }
                    break;
                }
                case R.id.ll_uninstall: {
                    //系统程序是卸载不了的，如果用户点击了系统程序，那么就应该判断一下当前系统是否已经root过了，
                    //如果已经root了，刚可以卸载，否则是卸载不了的，应该提示一下。
                    if (info != null) {
                        //                        intent = new Intent();
                        //                        intent.setAction("android.intent.action.VIEW");
                        //                        intent.setAction("android.intent.action.DELETE");
                        //                        intent.addCategory("android.intent.category
                        // .DEFAULT");
                        //                        intent.setData(Uri.parse("package:" + info
                        // .getPackageName()));
                        //                        startActivityForResult(intent, 5858);
                        // 5858为返回码，随便定义一个大于零的整数就好了

                        /*ICallSystemMethod call = ((MyApplication) getApplicationContext())
                                .getSystemCall();
                        if (call != null) {
                            try {
                                boolean result = call.deletePackage(
                                        info.getPackageName(),
                                        new android.content.pm.IPackageDeleteObserver.Stub() {
                                            @Override
                                            public void packageDeleted(String s, int i)
                                                    throws RemoteException {
                                                Log.d(TAG, "s = " + s + " i = " + i);
//                                                loadData();
                                            }
                                        });
//                                if (result) {
//                                    loadData();
//                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }*/
                    }
                    break;
                }
                case R.id.ll_show: {
                    if (info != null) {
                        intent = new Intent();
                        intent.setAction("android.intent.action.SEND");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_TEXT, "分享一款软件：" + info.getAppName());
                        startActivity(intent);
                    }
                    break;
                }
            }
        }
    }

    ;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //刷新界面
        loadData();
    }

    private class AppsManagerInfoAdapter extends BaseAdapter {

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
        public View getView(int position, View convertView, final ViewGroup parent) {
            View view;
            AppInfos info = null;
            if (position == 0) {
                TextView tv_user = new TextView(mContext);
                tv_user.setText("用户程序" + userList.size() + "个");
                return tv_user;
            } else if (position == (userList.size() + 1)) {
                TextView tv_system = new TextView(mContext);
                tv_system.setText("系统程序" + systemList.size() + "个");
                return tv_system;
            } else if (position <= userList.size() + 1) {
                info = userList.get(position - 1);
            } else {
                info = systemList.get(position - userList.size() - 2);
            }

            //convertView instanceof RelativeLayout这步很重要，没有这句则会抛空指针异常或者达不到缓存的效果
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(mContext, R.layout.appinfos_view_item, null);
                holder = new ViewHolder();
                holder.icon_app = (ImageView) view.findViewById(R.id.iv_appinfos_icon);
                holder.appName = (TextView) view.findViewById(R.id.tv_appinfos_appname);
                holder.uid = (TextView) view.findViewById(R.id.tv_appinfos_uid);
                holder.packageName = (TextView) view.findViewById(R.id.tv_appinfos_packagename);
                holder.spaceUsage = (TextView) view.findViewById(R.id.tv_appinfos_spaceusage);
                holder.icon_applock = (ImageView) view.findViewById(R.id.iv_applock_icon);
                view.setTag(holder);
            }

            holder.icon_app.setImageDrawable(info.getIcon());
            holder.appName.setText(info.getAppName());
            holder.uid.setText("uid:" + info.getUid());
            holder.spaceUsage.setText("占用总空间：" + info.getSpaceUsage());
            holder.packageName.setText(info.getPackageName());

            //设置程序锁的图标
            String packageName = info.getPackageName();
            if (dao.query(packageName)) {
                holder.icon_applock.setImageResource(R.drawable.lock);
            } else {
                holder.icon_applock.setImageResource(R.drawable.unlock);
            }

            return view;
        }

    }

    private static class ViewHolder {
        ImageView icon_app;
        TextView appName;
        TextView uid;
        TextView packageName;
        TextView spaceUsage;
        ImageView icon_applock;

    }

    //下面两个是连在一起的
    public void getPackageSizeInfo(Context context, String packageName) {//一调用这个方法，就会执行下面类中的回调方法
        /*try {
            PackageManager pm = ((MyApplication) context.getApplicationContext()).mPackageManager;
            Class<PackageManager> c = PackageManager.class;
            Method method = c.getMethod("getPackageSizeInfo", String.class,
                    IPackageStatsObserver.class);
            method.invoke(pm, packageName, new MyPackageStatsObserver());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /*private class MyPackageStatsObserver extends IPackageStatsObserver.Stub {
        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            //			long cache = pStats.cacheSize;
            long code = pStats.codeSize;
            long data = pStats.dataSize;
            for (AppInfos info : list) {
                if (info.getPackageName().equals(pStats.packageName)) {
                    info.setSpaceUsage(Formatter.formatFileSize(getApplicationContext(), code +
                            data));
                }
            }
        }
    }*/


}
