package com.weidi.artifact.controller;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.os.StatFs;
import android.os.SystemClock;
//import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.activity.MainActivity;
import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.basecontroller.BaseFragmentController;
import com.weidi.artifact.db.bean.AppInfos;
import com.weidi.artifact.db.dao.AppLockDao;
import com.weidi.artifact.fragment.AppsManagerFragment;
import com.weidi.artifact.fragment.AppsOperationDialogFragment;
import com.weidi.artifact.listener.OnResultListener;
import com.weidi.callsystemmethod.ICallSystemMethod;
import com.weidi.customadapter.CustomRecyclerViewAdapter;
import com.weidi.customadapter.CustomViewHolder;
import com.weidi.customadapter.listener.OnItemClickListener;
import com.weidi.log.Log;
import com.weidi.threadpool.CustomRunnable;
import com.weidi.threadpool.ThreadPool;
import com.weidi.utils.MyToast;
import com.weidi.utils.MyUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 17-1-13.
 */

public class AppsManagerFragmentController extends BaseFragmentController {

    private static final String TAG = "AppsManagerFragmentController";
    private AppsManagerFragment mAppsManagerFragment;
    private AppsManagerAdapter mAppsManagerAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private List<AppInfos> appsList;
    private List<AppInfos> userList;
    private List<AppInfos> systemList;

    private WindowManager mWindowManager;
    private PackageManager mPackageManager;
    private ActivityManager mActivityManager;
    private PopupWindow mPopupWindow;
    private AppLockDao mAppLockDao;
    private int firstVisibleItemPosition;
    private int visibleItemCount;
    private int totalItemCount;
    private String mExternalStorageDirectory;

    public AppsManagerFragmentController(Fragment fragment) {
        super(fragment.getActivity());
        mAppsManagerFragment = (AppsManagerFragment) fragment;
    }

    @Override
    public void beforeInitView() {
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mPackageManager = mContext.getPackageManager();
        mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        mAppLockDao = new AppLockDao(mContext);
    }

    @Override
    public void afterInitView(LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        mAppsManagerFragment.appsinfo_recyclerview.addOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                    }

                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        dismissPopupWindow();

                        firstVisibleItemPosition = mLinearLayoutManager
                                .findFirstVisibleItemPosition();
                        visibleItemCount = mLinearLayoutManager.getChildCount();
                        totalItemCount = mLinearLayoutManager.getItemCount();

                        if (userList != null && systemList != null) {
                            mAppsManagerFragment.user_system_app_counts_tv.setVisibility(
                                    TextView.VISIBLE);
                            if (firstVisibleItemPosition < userList.size()) {
                                mAppsManagerFragment.user_system_app_counts_tv.setText(
                                        "用户程序： " + userList.size() + " 个");
                            } else {
                                mAppsManagerFragment.user_system_app_counts_tv.setText(
                                        "系统程序： " + systemList.size() + " 个");
                            }
                        }
                    }

                });

        mExternalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();
        loadData();
    }

    @Override
    public void onResume() {
        ((MainActivity) mAppsManagerFragment.getActivity()).title.setText("我的软件");
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

    private class AppsManagerAdapter extends CustomRecyclerViewAdapter<AppInfos> {

        public AppsManagerAdapter(Context context, List items, int layoutResId) {
            super(context, items, layoutResId);
        }

        @Override
        public void onBind(CustomViewHolder customViewHolder,
                           int viewType,
                           int layoutPosition,
                           AppInfos item) {
            AppInfos info = appsList.get(layoutPosition);

            customViewHolder.setImageDrawable(R.id.iv_appinfos_icon, info.getIcon());
            customViewHolder.setText(R.id.tv_appinfos_appname, info.getAppName());
            customViewHolder.setText(R.id.tv_appinfos_uid, "uid: " + info.getUid());
            customViewHolder.setText(R.id.tv_appinfos_spaceusage,
                    "占用总空间： " + info.getSpaceUsage());
            customViewHolder.setText(R.id.tv_appinfos_packagename, info.getPackageName());

            //设置程序锁的图标
            String packageName = info.getPackageName();
            if (mAppLockDao.query(packageName)) {
                customViewHolder.setImageResource(R.id.iv_applock_icon, R.drawable.lock);
            } else {
                customViewHolder.setImageResource(R.id.iv_applock_icon, R.drawable.unlock);
            }
        }
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View itemView, int viewType, int position) {

            AppInfos info = appsList.get(position);

            AppsOperationDialogFragment mAppsOperationDialogFragment =
                    new AppsOperationDialogFragment();
            Bundle bundle = new Bundle();
            bundle.putString(Constant.APP_NAME, info.getAppName());
            bundle.putString(Constant.APP_PACKAGE_NAME, info.getPackageName());

            mAppsOperationDialogFragment.setOnResultListener(mOnResultListener);
            mAppsOperationDialogFragment.setArguments(bundle);
            mAppsOperationDialogFragment.show(
                    mMainActivity.getFragmentManager(), Constant.APPSOPERATIONDIALOGFRAGMENT);


            //            dismissPopupWindow();
            //
            //            AppInfos info = appsList.get(position);
            //
            //            View v = View.inflate(mContext, R.layout.appinfos_oper_view_item, null);
            //            LinearLayout ll_run = (LinearLayout) v.findViewById(R.id.ll_run);
            //            LinearLayout ll_uninstall = (LinearLayout) v.findViewById(R.id
            // .ll_uninstall);
            //            LinearLayout ll_show = (LinearLayout) v.findViewById(R.id.ll_show);
            //            AppsInfoOnClick ocl = new AppsInfoOnClick(info);
            //            ll_run.setOnClickListener(ocl);
            //            ll_uninstall.setOnClickListener(ocl);
            //            ll_show.setOnClickListener(ocl);
            //
            //            mPopupWindow = new PopupWindow(v, -2, -2);//-2为包裹内容
            //            mPopupWindow.setContentView(v);
            //            mPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //            int[] location = new int[2];
            //            itemView.getLocationInWindow(location);
            //            int x = 25;
            //            int y = 5;
            //            mPopupWindow.showAtLocation(
            //                    mAppsManagerFragment.appsinfo_recyclerview,
            //                    Gravity.TOP | Gravity.LEFT,
            //                    location[0] + MyUtils.dip2px(mContext, x),
            //                    location[1] - itemView.getHeight() - MyUtils.dip2px(mContext, 5));
        }

    };

    // 得到外置存储卡的路径
    private String getSDPath() {
        File innerFile = Environment.getExternalStorageDirectory();
        File parentFile = innerFile.getParentFile();
        if (parentFile != null) {
            File[] files = parentFile.listFiles();
            for (File file : files) {
                if (file != null
                        && file.length() > 0
                        && !innerFile.getAbsolutePath().equals(file.getAbsolutePath())) {
                    return file.getAbsolutePath();
                }
            }
        }
        return null;
    }

    // 得到可用的存储空间
    private String getAvailableSize(String path) {
        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSize();
        long availableBlocks = sf.getAvailableBlocks();
        long availableSize = blockSize * availableBlocks;
        return Formatter.formatFileSize(mContext, availableSize);
        //		long freeBlocks = sf.getFreeBlocks();
        //		long freeSize = blockSize * freeBlocks;//结果与availableSize一样
    }

    // 得到可用的手机运行内存大小
    private String getRAMAvailableSize() {
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        mActivityManager.getMemoryInfo(outInfo);
        long availMem = outInfo.availMem;
        //		long totalMem = outInfo.totalMem;
        return Formatter.formatFileSize(mContext, availMem);
    }

    private void showLoading() {
        mAppsManagerFragment.loading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mAppsManagerFragment.loading.setVisibility(View.GONE);
    }

    private void dismissPopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
    }

    // 下面两个是连在一起的
    // 一调用这个方法，就会执行下面类中的回调方法
    public void getPackageSizeInfo(Context context, String packageName) {
        try {
            PackageManager pm = ((MyApplication) context.getApplicationContext()).mPackageManager;
            Class<PackageManager> clazz = PackageManager.class;
            Method method = clazz.getMethod(
                    "getPackageSizeInfo",
                    String.class,
                    IPackageStatsObserver.class);
            method.invoke(pm, packageName, new MyPackageStatsObserver());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyPackageStatsObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                throws RemoteException {
            //			long cache = pStats.cacheSize;
            long code = pStats.codeSize;
            long data = pStats.dataSize;
            for (AppInfos info : appsList) {
                if (info.getPackageName().equals(pStats.packageName)) {
                    info.setSpaceUsage(
                            Formatter.formatFileSize(mContext, code + data));
                }
            }
        }

    }

    //点击事件：启动程序、卸载程序、分享程序
    private class AppsInfoOnClick implements View.OnClickListener {
        private AppInfos info;

        public AppsInfoOnClick(AppInfos info) {
            this.info = info;
        }

        @Override
        public void onClick(View v) {
            dismissPopupWindow();
            Intent intent;
            switch (v.getId()) {
                case R.id.ll_run: {
                    if (info != null) {
                        intent = mPackageManager.getLaunchIntentForPackage(info.getPackageName());
                        if (intent != null
                                && !mContext.getPackageName().equals(info.getPackageName())) {
                            mMainActivity.startActivity(intent);
                        } else if (mContext.getPackageName().equals(info.getPackageName())) {
                            return;
                        } else {
                            MyToast.show("我无能为力，启动不了这个东西");
                        }
                    }
                    break;
                }
                case R.id.ll_uninstall: {
                    //系统程序是卸载不了的，如果用户点击了系统程序，那么就应该判断一下当前系统是否已经root过了，
                    //如果已经root了，则可以卸载，否则是卸载不了的，应该提示一下。
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

                        ICallSystemMethod call = ((MyApplication) mContext.getApplicationContext())
                                .getSystemCall();
                        if (call != null) {
                            try {
                                showLoading();
                                call.deletePackage(
                                        info.getPackageName(),
                                        new android.content.pm.IPackageDeleteObserver.Stub() {
                                            @Override
                                            public void packageDeleted(String s, int i)
                                                    throws RemoteException {
                                                Log.d(TAG, "s = " + s + " i = " + i);
                                                loadData();
                                            }
                                        });
                            } catch (RemoteException e) {
                                hideLoading();
                                e.printStackTrace();
                            }
                        }
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
                        mMainActivity.startActivity(intent);
                    }
                    break;
                }
            }
        }
    }

    private void loadData() {
        final CustomRunnable mCustomRunnable = new CustomRunnable();
        mCustomRunnable.setCallBack(
                new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {
                        showLoading();
                        mAppsManagerFragment.rom_available_tv.setVisibility(View.INVISIBLE);
                        mAppsManagerFragment.sd_available_tv.setVisibility(View.INVISIBLE);
                        mAppsManagerFragment.user_system_app_counts_tv.setVisibility(
                                TextView.INVISIBLE);
                    }

                    @Override
                    public Object running() {
                        appsList = MyUtils.getInstalledApplicationInfos(mContext);
                        userList = new ArrayList<AppInfos>();
                        systemList = new ArrayList<AppInfos>();
                        for (AppInfos appInfos : appsList) {
                            getPackageSizeInfo(mContext, appInfos.getPackageName());
                            boolean isUserApp = appInfos.isUserApp();
                            if (isUserApp) {
                                userList.add(appInfos);
                            } else {
                                systemList.add(appInfos);
                            }
                        }
                        appsList = new ArrayList<AppInfos>();
                        appsList.addAll(userList);
                        appsList.addAll(systemList);
                        SystemClock.sleep(500);
                        return null;
                    }

                    @Override
                    public void onProgressUpdate(Object object) {

                    }

                    @Override
                    public void runAfter(Object object) {
                        hideLoading();
                        mAppsManagerFragment.rom_available_tv.setVisibility(View.VISIBLE);
                        mAppsManagerFragment.sd_available_tv.setVisibility(View.VISIBLE);
                        mAppsManagerFragment.user_system_app_counts_tv.setVisibility(
                                TextView.VISIBLE);
                        mAppsManagerFragment.rom_available_tv.setText(
                                "ROM Available：" + getAvailableSize(mExternalStorageDirectory));
                        if (getSDPath() != null) {
                            mAppsManagerFragment.sd_available_tv.setText(
                                    "SD Available：" + getAvailableSize(getSDPath()));
                        } else {
                            mAppsManagerFragment.sd_available_tv.setText("找不到外置存储卡");
                        }

                        firstVisibleItemPosition = mLinearLayoutManager
                                .findFirstVisibleItemPosition();

                        if (userList != null && systemList != null) {
                            if (firstVisibleItemPosition < userList.size()) {
                                mAppsManagerFragment.user_system_app_counts_tv.setText(
                                        "用户程序： " + userList.size() + " 个");
                            } else {
                                mAppsManagerFragment.user_system_app_counts_tv.setText(
                                        "系统程序： " + systemList.size() + " 个");
                            }
                        }

                        if (mAppsManagerAdapter == null) {
                            mAppsManagerAdapter = new AppsManagerAdapter(
                                    mContext, appsList, R.layout.appinfos_view_item);
                            mAppsManagerAdapter.setOnItemClickListener(mOnItemClickListener);
                            mAppsManagerFragment.appsinfo_recyclerview.setLayoutManager(
                                    mLinearLayoutManager);
                            mAppsManagerFragment.appsinfo_recyclerview
                                    .setAdapter(mAppsManagerAdapter);
                        } else {
                            mAppsManagerAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void runError() {
                        hideLoading();
                    }

                });
        ThreadPool.getCachedThreadPool().execute(mCustomRunnable);
    }

    private OnResultListener mOnResultListener = new OnResultListener() {

        @Override
        public void onResult(int requestCode, int resultCode, Object object) {
            loadData();
        }

    };


}
