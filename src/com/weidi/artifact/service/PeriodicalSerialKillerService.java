package com.weidi.artifact.service;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.constant.Constant;
//import com.weidi.callsystemmethod.ICallSystemMethod;
import com.weidi.log.MLog;
import com.weidi.service.BaseService;
import com.weidi.threadpool.ThreadPool;
import com.weidi.eventbus.EventBusUtils;
import com.weidi.utils.MyToast;

import java.util.ArrayList;
import java.util.List;

import static com.weidi.utils.MyToast.show;

public class PeriodicalSerialKillerService extends BaseService {

    private static final String TAG = "PeriodicalSerialKillerService";
    private ArrayList<String> mCannotBeKilledPackageNameList = new ArrayList<String>();
    private ArrayList<Intent> mRecentTaskIntentList = new ArrayList<Intent>();
    private ArrayList<String> mRecentTaskPackageNameList = new ArrayList<String>();
    private boolean mIsRunning = true;
    private Handler mHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        MLog.d(TAG, "onCreate(): " + this);

        EventBusUtils.register(this);

        List<ApplicationInfo> mApplicationInfoList = ((MyApplication) getApplicationContext())
                .mPackageManager.getInstalledApplications(0);
        int mApplicationInfoListCount = mApplicationInfoList.size();
        final ArrayList<String> systemApplicationList = new
                ArrayList<String>();
        final ArrayList<ApplicationInfo> userApplicationInfoList = new
                ArrayList<ApplicationInfo>();
        for (int i = 0; i < mApplicationInfoListCount; i++) {
            ApplicationInfo applicationInfo = mApplicationInfoList.get(i);
            if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                userApplicationInfoList.add(applicationInfo);
            } else if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                systemApplicationList.add(applicationInfo.processName);
            }
        }

        /*for (ApplicationInfo applicationInfo : userApplicationInfoList) {
            MLog.d(TAG, "packagename = " + applicationInfo.processName);
        }*/

        ThreadPool.getFixedThreadPool(Constant.FIXEDTHREADPOOLCOUNT).execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                mHandler = new Handler() {
                    public void handleMessage(android.os.Message msg) {
                        serialKiller(systemApplicationList, userApplicationInfoList);
                        if (mIsRunning && mHandler != null) {
                            mHandler.sendEmptyMessageDelayed(0, 1 * 1000);
                        }
                    }
                };
                if (mHandler != null) {
                    mHandler.sendEmptyMessage(0);
                }
                Looper.loop();
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MLog.d(TAG, "onStartCommand():intent = " + intent +
                " flags = " + flags + " startId = " + startId + " " + this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        MLog.d(TAG, "onDestroy(): " + this);
        mIsRunning = false;
        mHandler.removeMessages(0);
        mHandler = null;
        if (mCannotBeKilledPackageNameList != null) {
            mCannotBeKilledPackageNameList.clear();
            mCannotBeKilledPackageNameList = null;
        }
        EventBusUtils.unregister(this);
    }

    public Object onEvent(int what, Object object) {
        switch (what) {
            case Constant.PERIODICALSERIALKILLERSERVICE:
                stopSelf();
                break;

            case Constant.BEKILLEDPROCESSNAME:
                String packageName = (String) object;
                if (mCannotBeKilledPackageNameList != null
                        && mCannotBeKilledPackageNameList.contains(packageName)) {
                    mCannotBeKilledPackageNameList.remove(packageName);

                    for (Intent intent : mRecentTaskIntentList) {
                        String pkgName = intent.getComponent().getPackageName();
                        if (pkgName.equals(packageName)
                                && mRecentTaskIntentList.contains(intent)
                                && mRecentTaskPackageNameList.contains(pkgName)) {
                            mRecentTaskIntentList.remove(intent);
                            mRecentTaskPackageNameList.remove(packageName);
                            break;
                        }
                    }
                    /*for (Intent intent : mRecentTaskIntentList) {
                        ComponentName componentName = intent.getComponent();
                        String className = componentName.getClassName();
                        MLog.d(TAG, "className3 = " + className);
                    }*/
                }
                break;

            case Constant.CHANGEAPP:
                /*for (Intent intent : mRecentTaskIntentList) {
                    // MLog.d(TAG, "intent = " + intent.toString());
                    ComponentName componentName = intent.getComponent();
                    String className = componentName.getClassName();
                    MLog.d(TAG, "className1 = " + className);
                }*/
                if (mRecentTaskIntentList != null
                        && mRecentTaskIntentList.size() > 1) {
                    Intent intent0 = mRecentTaskIntentList.get(0);
                    Intent intent1 = mRecentTaskIntentList.get(1);
                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                    mRecentTaskIntentList.remove(intent0);
                    mRecentTaskIntentList.remove(intent1);
                    mRecentTaskIntentList.add(0, intent0);
                    mRecentTaskIntentList.add(0, intent1);
                    // 超过42个字符就要换行
                    MyToast.show(intent0.getComponent().getClassName());
                }
                /*for (Intent intent : mRecentTaskIntentList) {
                    // MLog.d(TAG, "intent = " + intent.toString());
                    ComponentName componentName = intent.getComponent();
                    String className = componentName.getClassName();
                    MLog.d(TAG, "className2 = " + className);
                }*/
                break;

            default:
        }
        return what;
    }


    // 杀后台，不杀前台
    private void serialKiller(
            ArrayList<String> systemApplicationList,
            ArrayList<ApplicationInfo> userApplicationInfoList) {
        /*ICallSystemMethod call = ((MyApplication) getApplicationContext()).getSystemCall();
        if (call == null) {
            MLog.d(TAG, "call == null " + this);
            return;
        }*/

        List<ActivityManager.RecentTaskInfo> recentTaskInfoList =
                ((MyApplication) getApplication())
                        .mActivityManager.getRecentTasks(
                        5858, ActivityManager.RECENT_IGNORE_UNAVAILABLE);

        String topAppPackageName = null;
        String secondAppPackageName = null;

        List<ActivityManager.RunningTaskInfo> runningTaskInfoList =
                ((MyApplication) getApplicationContext()).mActivityManager.getRunningTasks(100);

        // 白名单
        ArrayList<String> userPackageNameList = ((MyApplication) getApplicationContext()).pkgList;

        int runningTaskInfoListCount = runningTaskInfoList.size();
        // MLog.d(TAG, "runningTaskInfoListCount = " + runningTaskInfoListCount);
        // 当前正在运行的应用
        if (runningTaskInfoListCount > 0) {
            topAppPackageName = runningTaskInfoList.get(0).topActivity.getPackageName();
            if (!userPackageNameList.contains(topAppPackageName)
                    && mCannotBeKilledPackageNameList != null
                    && !mCannotBeKilledPackageNameList.contains(topAppPackageName)
                    && !systemApplicationList.contains(topAppPackageName)) {
                mCannotBeKilledPackageNameList.add(topAppPackageName);
            }
            for (ActivityManager.RecentTaskInfo recentTaskInfo : recentTaskInfoList) {
                String packageName = recentTaskInfo.baseIntent.getComponent().getPackageName();
                if (topAppPackageName.equals(packageName)
                        && !Constant.LAUNCHER.equals(packageName)) {
                    Intent intent = recentTaskInfo.baseIntent;
                    String pkgName = intent.getComponent().getPackageName();
                    if (!mRecentTaskIntentList.contains(intent)
                            && !mRecentTaskPackageNameList.contains(pkgName)
                            && !getPackageName().equals(topAppPackageName)) {
                        /*for (Intent inten : mRecentTaskIntentList) {
                            ComponentName componentName = inten.getComponent();
                            String className = componentName.getClassName();
                            MLog.d(TAG, "className1 = " + className);
                        }*/
                        MLog.d(TAG, "pkgName1 = " + pkgName);
                        // 在添加的时候之前的位置排列有时会反过来,还不知道怎么回事
                        mRecentTaskIntentList.add(intent);
                        mRecentTaskPackageNameList.add(pkgName);
                        /*for (Intent inten : mRecentTaskIntentList) {
                            ComponentName componentName = inten.getComponent();
                            String className = componentName.getClassName();
                            MLog.d(TAG, "className2 = " + className);
                        }*/
                        break;
                    } else {
                        /*Iterator<Intent> iter = mRecentTaskIntentList.iterator();
                        Intent intenT = null;
                        while (iter.hasNext()) {
                            Intent intentTemp = iter.next();
                            String pName = intentTemp.getComponent().getPackageName();
                            if (pName.equals(topAppPackageName)) {
                                intenT = intentTemp;
                                iter.remove();
                                mRecentTaskIntentList.add(0, intenT);
                                break;
                            }
                        }*/
                        for (Intent intenT : mRecentTaskIntentList) {
                            String pName = intenT.getComponent().getPackageName();
                            if (pName.equals(topAppPackageName)) {
                                mRecentTaskIntentList.remove(intenT);
                                mRecentTaskIntentList.add(0, intenT);
                                // MLog.d(TAG, "=====================================");
                                break;
                            }
                        }
                        /*for (Intent inten : mRecentTaskIntentList) {
                            ComponentName componentName = inten.getComponent();
                            String className = componentName.getClassName();
                            MLog.d(TAG, "className = " + className);
                        }*/
                    }
                }
            }

            if (Constant.LAUNCHER.equals(topAppPackageName)) {
                if (runningTaskInfoListCount > 1) {
                    topAppPackageName = runningTaskInfoList.get(1).topActivity.getPackageName();
                    if (!userPackageNameList.contains(topAppPackageName)
                            && mCannotBeKilledPackageNameList != null
                            && !mCannotBeKilledPackageNameList.contains(topAppPackageName)
                            && !systemApplicationList.contains(topAppPackageName)) {
                        mCannotBeKilledPackageNameList.add(topAppPackageName);
                    }
                    for (ActivityManager.RecentTaskInfo recentTaskInfo : recentTaskInfoList) {
                        String packageName = recentTaskInfo.baseIntent.getComponent()
                                .getPackageName();
                        if (topAppPackageName.equals(packageName)
                                && !Constant.LAUNCHER.equals(packageName)) {
                            Intent intent = recentTaskInfo.baseIntent;
                            String pkgName = intent.getComponent().getPackageName();
                            if (!mRecentTaskIntentList.contains(intent)
                                    && !mRecentTaskPackageNameList.contains(pkgName)
                                    && !getPackageName().equals(topAppPackageName)) {
                                MLog.d(TAG, "pkgName2 = " + pkgName);
                                mRecentTaskIntentList.add(intent);
                                mRecentTaskPackageNameList.add(pkgName);
                                break;
                            } else {
                                for (Intent intenT : mRecentTaskIntentList) {
                                    String pName = intenT.getComponent().getPackageName();
                                    if (pName.equals(topAppPackageName)) {
                                        mRecentTaskIntentList.remove(intenT);
                                        mRecentTaskIntentList.add(0, intenT);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (runningTaskInfoListCount > 2) {
                    secondAppPackageName = runningTaskInfoList.get(2).topActivity.getPackageName();
                    if (!userPackageNameList.contains(secondAppPackageName)
                            && mCannotBeKilledPackageNameList != null
                            && !mCannotBeKilledPackageNameList.contains(secondAppPackageName)
                            && !systemApplicationList.contains(secondAppPackageName)) {
                        mCannotBeKilledPackageNameList.add(secondAppPackageName);
                    }
                    for (ActivityManager.RecentTaskInfo recentTaskInfo : recentTaskInfoList) {
                        String packageName = recentTaskInfo.baseIntent.getComponent()
                                .getPackageName();
                        if (secondAppPackageName.equals(packageName)
                                && !Constant.LAUNCHER.equals(packageName)) {
                            Intent intent = recentTaskInfo.baseIntent;
                            String pkgName = intent.getComponent().getPackageName();
                            if (!mRecentTaskIntentList.contains(intent)
                                    && !mRecentTaskPackageNameList.contains(pkgName)
                                    && !getPackageName().equals(secondAppPackageName)) {
                                MLog.d(TAG, "pkgName3 = " + pkgName);
                                mRecentTaskIntentList.add(intent);
                                mRecentTaskPackageNameList.add(pkgName);
                                break;
                            } else {
                                for (Intent intenT : mRecentTaskIntentList) {
                                    String pName = intenT.getComponent().getPackageName();
                                    if (pName.equals(topAppPackageName)) {
                                        mRecentTaskIntentList.remove(intenT);
                                        mRecentTaskIntentList.add(0, intenT);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                if (runningTaskInfoListCount > 1) {
                    // MLog.d(TAG, "runningTaskInfoListCount = " + runningTaskInfoListCount);
                    secondAppPackageName = runningTaskInfoList.get(1).topActivity.getPackageName();
                    if (!userPackageNameList.contains(secondAppPackageName)
                            && mCannotBeKilledPackageNameList != null
                            && !mCannotBeKilledPackageNameList.contains(secondAppPackageName)
                            && !systemApplicationList.contains(secondAppPackageName)) {
                        mCannotBeKilledPackageNameList.add(secondAppPackageName);
                    }
                    for (ActivityManager.RecentTaskInfo recentTaskInfo : recentTaskInfoList) {
                        String packageName = recentTaskInfo.baseIntent.getComponent()
                                .getPackageName();
                        if (secondAppPackageName.equals(packageName)
                                && !Constant.LAUNCHER.equals(packageName)) {
                            Intent intent = recentTaskInfo.baseIntent;
                            String pkgName = intent.getComponent().getPackageName();
                            if (!mRecentTaskIntentList.contains(intent)
                                    && !mRecentTaskPackageNameList.contains(pkgName)
                                    && !getPackageName().equals(secondAppPackageName)) {
                                MLog.d(TAG, "pkgName4 = " + pkgName);
                                mRecentTaskIntentList.add(intent);
                                mRecentTaskPackageNameList.add(pkgName);
                                break;
                            }
                        }
                    }
                }
            }
        }
        /*for (Intent intent : mRecentTaskIntentList) {
            // MLog.d(TAG, "intent = " + intent.toString());
            ComponentName componentName = intent.getComponent();
            String className = componentName.getClassName();
            MLog.d(TAG, "className = " + className);
        }*/

        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList =
                ((MyApplication) getApplicationContext())
                        .mActivityManager.getRunningAppProcesses();

        int runningAppProcessInfoListCount = runningAppProcessInfoList.size();
        int userApplicationInfoListCount = userApplicationInfoList.size();

        for (int i = 0; i < runningAppProcessInfoListCount; i++) {
            // 进程名可能就不单纯是应用的包名了
            String runningAppProcessName = runningAppProcessInfoList.get(i).processName;
            // MLog.d(TAG, "正在运行的进程名: " + runningAppProcessName);

            for (int j = 0; j < userApplicationInfoListCount; j++) {
                // 应用的包名
                String userPackageName = userApplicationInfoList.get(j).processName;
                // 正在运行的进程是用户进程
                if ((runningAppProcessName.equals(userPackageName)
                        || runningAppProcessName.contains(userPackageName))// 可能就是包含":"
                        || runningAppProcessName.startsWith(".")) {
                    // MLog.d(TAG, "正在运行的进程名: " + runningAppProcessName);
                    if (runningAppProcessName.contains(":")) {
                        String processNameTemp = runningAppProcessName.split(":")[0];
                        if (!userPackageNameList.contains(processNameTemp)
                                && mCannotBeKilledPackageNameList != null
                                && !mCannotBeKilledPackageNameList.contains(processNameTemp)) {
                            try {
//                                call.forceStopPackage(processNameTemp);
//                                call.forceStopPackage(runningAppProcessName);
                                // MLog.d(TAG, "被杀的进程名1: " + processNameTemp);
                                // MLog.d(TAG, "被杀的进程名2: " + runningAppProcessName);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    }
                    if (!userPackageNameList.contains(runningAppProcessName)
                            && mCannotBeKilledPackageNameList != null
                            && !mCannotBeKilledPackageNameList.contains(runningAppProcessName)) {
                        try {
//                            call.forceStopPackage(runningAppProcessName);
                            // MLog.d(TAG, "被杀的进程名3: " + runningAppProcessName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }
            }
        }
        // 目的:为了防止像爱奇艺这样的流氓软件杀不掉而执行
        for (ApplicationInfo applicationInfo : userApplicationInfoList) {
            String userPackageName = applicationInfo.processName;
            if (!userPackageNameList.contains(userPackageName)
                    && mCannotBeKilledPackageNameList != null
                    && !mCannotBeKilledPackageNameList.contains(userPackageName)) {
                try {
//                    call.forceStopPackage(userPackageName);
                    // MLog.d(TAG, "被杀的进程名4: " + userPackageName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        /*runningTaskInfoList.clear();
        runningAppProcessInfoList.clear();
        runningTaskInfoList = null;
        runningAppProcessInfoList = null;
        topAppPackageName = null;
        secondAppPackageName = null;*/
    }
}
