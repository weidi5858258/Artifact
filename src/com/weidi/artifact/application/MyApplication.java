package com.weidi.artifact.application;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;

import com.weidi.application.WeidiApplication;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.db.bean.AppInfos;
import com.weidi.artifact.db.dao.ProcessDao;
import com.weidi.artifact.modle.BTDevice;
import com.weidi.artifact.modle.Contacts;
import com.weidi.artifact.modle.Data;
import com.weidi.artifact.modle.Event;
import com.weidi.artifact.modle.MimeTypes;
import com.weidi.artifact.modle.RawContacts;
import com.weidi.artifact.modle.Sms;
//import com.weidi.callsystemmethod.ICallSystemMethod;
import com.weidi.dbutil.DbUtils;
import com.weidi.log.MLog;
import com.weidi.threadpool.CustomRunnable;
import com.weidi.threadpool.ThreadPool;
import com.weidi.utils.MyUtils;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends WeidiApplication implements ServiceConnection {

    private static final String TAG = "WeidiApplication";
    //    private static ICallSystemMethod mICallSystemMethod;
//    private ICallSystemMethod mICallSystemMethod;
    public PackageManager mPackageManager;
    public ActivityManager mActivityManager;
    public AlarmManager mAlarmManager;
    public Runtime mRuntime;
    public Instrumentation mInstrumentation;
    public ArrayList<String> pkgList;
    public List<AppInfos> appList;
    public List<String> userList;
    public List<String> systemList;
    private ProcessDao dao;
    private Class[] tableNameClass = new Class[]{
            BTDevice.class, Event.class, Sms.class,
            Contacts.class, RawContacts.class, Data.class, MimeTypes.class
    };

    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //        MultiDex.install(this);
    }

    /*public ICallSystemMethod getSystemCall() {
        if (mICallSystemMethod == null) {
            bindRemoteService();
        }
        return mICallSystemMethod;
    }*/

    //*********************************ServiceConnection*********************************//

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
//        mICallSystemMethod = ICallSystemMethod.Stub.asInterface(service);
//        MLog.d(TAG, "mICallSystemMethod = " + mICallSystemMethod);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        //        mICallSystemMethod = null;
    }

    //*********************************ServiceConnection*********************************//

    private class ProcessBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 用于某个进程被加入白名单或者黑名单后能够及时更新数据
            // 在ProcessDao这个类中发送的
            pkgList = dao.query();
        }
    }

    private class PackageAddOrRemoveBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //在应用增加或者被卸载时收到通知
            new Thread(new Runnable() {
                @Override
                public void run() {
                    appList = MyUtils.getInstalledApplicationInfos(getApplicationContext());
                    for (AppInfos user : appList) {
                        if (user.isUserApp()) {
                            userList.add(user.getPackageName());
                        }
                    }


                }
            }).start();
        }
    }

    private void init() {
        bindRemoteService();
        mPackageManager = getPackageManager();
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mRuntime = Runtime.getRuntime();
        mInstrumentation = new Instrumentation();
        dao = new ProcessDao(this);
        pkgList = dao.query();
        appList = MyUtils.getInstalledApplicationInfos(this);
        userList = new ArrayList<String>();
        for (AppInfos user : appList) {
            if (user.isUserApp() && !userList.contains(user.getPackageName())) {
                userList.add(user.getPackageName());
            }
        }
        ProcessBroadcastReceiver processBR = new ProcessBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.aowin.mobilesafe.process");
        registerReceiver(processBR, filter);

        PackageAddOrRemoveBroadcastReceiver packageARBR = new PackageAddOrRemoveBroadcastReceiver();
        filter = new IntentFilter();
        filter.addAction("com.aowin.mobilesafe.package");
        registerReceiver(packageARBR, filter);

        /*// 数据库
        SimpleDao.setContext(this);
        // 先调用一下,把对象给创建好
        SimpleDao.getInstance();*/

        ThreadPool.getFixedThreadPool(Constant.FIXEDTHREADPOOLCOUNT).execute(
                new CustomRunnable().setCallBack(new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {
                        /*DbUtils.getInstance().setInitializationState(
                                DbUtils.INITIALIZING);*/
                        DbUtils.getInstance().setInitializationState(
                                DbUtils.INITIALIZATION_COMPLETE);
                    }

                    @Override
                    public Object running() {
                        DbUtils.getInstance().createOrUpdateDBWithVersion(
                                getApplicationContext(),
                                tableNameClass);
                        Runtime rt = Runtime.getRuntime();
                        long maxMemory = rt.maxMemory();
                        MLog.d("maxMemory:", Long.toString(maxMemory / (1024 * 1024)));
                        return null;
                    }

                    @Override
                    public void onProgressUpdate(Object object) {

                    }

                    @Override
                    public void runAfter(Object object) {
                        DbUtils.getInstance().setInitializationState(
                                DbUtils.INITIALIZATION_COMPLETE);
                    }

                    @Override
                    public void runError() {
                        DbUtils.getInstance().setInitializationState(
                                DbUtils.INITIALIZATION_FAILED);
                    }
                }));

        /*MyToast.setContext(this);
        MyToast.getInstance();
        EventBus.getDefault();

        MLog.init();*/

        /*// 初始化sdk
        JPushInterface.setDebugMode(true);//正式版的时候设置false，关闭调试
        JPushInterface.init(this);
        // 建议添加tag标签，发送消息的之后就可以指定tag标签来发送了
        Set<String> set = new HashSet<String>();
        set.add("artifact");// 名字任意，可多添加几个
        set.add("weidi5858258");
        JPushInterface.setTags(this, set, null);// 设置标签*/
    }

    /**
     * startServiceAsUser(mIntent, UserHandle.OWNER);
     * bindService(mIntent, this, Context.BIND_AUTO_CREATE, UserHandle.getUserId(Process.myUid()));
     */
    private void bindRemoteService() {
        /*try {
            Intent intent = new Intent();
            intent.setClassName(Constant.REMOTE_PACKAGE, Constant.REMOTE_PACKAGE_CLASS);
            if (!MyUtils.isSpecificServiceAlive(
                    getApplicationContext(), Constant.REMOTE_PACKAGE_CLASS)) {
                startServiceAsUser(intent, UserHandle.OWNER);
            }
            bindService(intent, this,
                    Context.BIND_AUTO_CREATE, UserHandle.getUserId(Process.myUid()));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

}
