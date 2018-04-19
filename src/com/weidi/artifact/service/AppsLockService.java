package com.weidi.artifact.service;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.weidi.artifact.activity.AppsLockActivity;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.db.dao.AppLockDao;
import com.weidi.log.MLog;
import com.weidi.service.BaseService;
import com.weidi.threadpool.CustomRunnable;
import com.weidi.threadpool.ThreadPool;
import com.weidi.utils.EventBusUtils;

import java.util.ArrayList;
import java.util.List;

import static com.weidi.artifact.constant.Constant.FIXEDTHREADPOOLCOUNT;

public class AppsLockService extends BaseService {

    private static final String TAG = "AppsLockService";
    private List<String> mAppsLockList;
    private AppLockDao mAppLockDao;
    private boolean isRunning = true;
    private List<String> mPackageNameList;
    private String packageName;
    private Handler mHandler;
    // true表示非法解锁
    private boolean mIllegalUnlock = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 在服务中开启子线程，如果服务停止了，但是进程还在，子线程是否会停止？
    @Override
    public void onCreate() {
        super.onCreate();
        final ActivityManager activityManager = (ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE);
        mAppLockDao = new AppLockDao(getApplicationContext());
        mAppsLockList = mAppLockDao.query();
        mPackageNameList = new ArrayList<String>();

        EventBusUtils.register(this);

        final CustomRunnable mCustomRunnable = new CustomRunnable();
        mCustomRunnable.setCallBack(
                new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {

                    }

                    @Override
                    public Object running() {
                        /**
                         * Looper.prepare();和Looper.loop();的作用相当于while (true).
                         * 只不过前者可以操作界面元素,后者不行.
                         */
                        Looper.prepare();
                        MLog.d(TAG, "prepare()");
                        // 里面就是主线程,不能执行耗时任务
                        mHandler = new Handler() {
                            public void handleMessage(android.os.Message msg) {
                                protectApps(activityManager);
                                if (isRunning) {
                                    // 使用mHandler发送消息给Handler的handleMessage()方法进行处理,
                                    // 发送一个消息,loop循环就会从消息队列里取出一个消息给
                                    // handleMessage()方法进行处理.这样就达到一种循环不断地效果.
                                    // 没有消息到来时,loop就等待着.
                                    mHandler.sendEmptyMessageDelayed(0, 20);
                                }
                            }
                        };
                        mHandler.sendEmptyMessage(0);
                        MLog.d(TAG, "loop()");
                        Looper.loop();

                        /*while (isRunning) {
                            SystemClock.sleep(20);
                            List<RunningTaskInfo> mRunningTaskInfoList =
                                    activityManager.getRunningTasks(1);
                            String packageName = mRunningTaskInfoList.get(0).topActivity
                                    .getPackageName();
                            // 程序被锁
                            if (mAppsLockList.contains(packageName)) {
                                if (!mPackageNameList.contains(packageName)) {
                                    Intent intent = new Intent(getApplicationContext(),
                                            AppsLockActivity.class);
                                    // 服务中开启Activity必须要加上这句
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.putExtra(Constant.APP_PACKAGE_NAME, packageName);
                                    startActivity(intent);
                                }
                            }
                        }*/
                        return null;
                    }

                    @Override
                    public void onProgressUpdate(Object object) {

                    }

                    @Override
                    public void runAfter(Object object) {

                    }

                    @Override
                    public void runError() {

                    }

                });
        ThreadPool.getFixedThreadPool(FIXEDTHREADPOOLCOUNT).execute(mCustomRunnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        mHandler.removeMessages(0);
        mHandler = null;
        EventBusUtils.unregister(this);
    }

    public Object onEvent(int what, Object object) {
        switch (what) {
            case Constant.SCREEN_OFF:
                mPackageNameList.clear();//锁屏后全部清空
                break;

            case Constant.PASSWORD_CORRECT:
                // 密码输入正确后要干的事
                if (object instanceof String) {
                    packageName = (String) object;
                    mPackageNameList.add(packageName);//用户记录不同的程序有没有解锁
                }
                break;

            case Constant.APPSLOCKSERVICE:
                stopSelf();
                break;

            case Constant.UPDATEAPPSLOCKLIST:
                mAppsLockList.clear();
                mAppsLockList = mAppLockDao.query();//更新数据
                break;

            case Constant.ILLEGALUNLOCK_ENTER:
                mIllegalUnlock = true;
                break;

            case Constant.ILLEGALUNLOCK_EXIT:
                mIllegalUnlock = false;
                break;

            default:
        }
        return what;
    }

    private void protectApps(ActivityManager activityManager) {
        List<RunningTaskInfo> mRunningTaskInfoList =
                activityManager.getRunningTasks(1);
        ComponentName componentName = mRunningTaskInfoList.get(0).topActivity;
        String packageName = componentName.getPackageName();
        String topActivityClassName = componentName.getClassName();

        // 程序被锁
        if (mAppsLockList.contains(packageName)) {
            // normal case
            if (!mPackageNameList.contains(packageName)) {
                if (!packageName.equals(getApplicationContext().getPackageName())) {
                    Intent intent = new Intent(
                            getApplicationContext(),
                            AppsLockActivity.class);
                    // 服务中开启Activity必须要加上这句
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(Constant.APP_PACKAGE_NAME, packageName);
                    startActivity(intent);
                }
            }
            // com.android.phone.InCallScreen
        } else if (mIllegalUnlock) {
            if (Constant.INCALLSCREEN.equals(topActivityClassName)) {
                return;
            }
            if (!packageName.equals(getApplicationContext().getPackageName())) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        AppsLockActivity.class);
                // 服务中开启Activity必须要加上这句
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(Constant.APP_PACKAGE_NAME, packageName);
                startActivity(intent);
            }
        }
        if (mRunningTaskInfoList != null) {
            mRunningTaskInfoList.clear();
            mRunningTaskInfoList = null;
        }

        componentName = null;
        packageName = null;
    }

}
