package com.weidi.artifact.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkUtils;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.weidi.activity.ScanCodeActivity;
import com.weidi.artifact.R;
import com.weidi.artifact.activity.AppsLockActivity;
import com.weidi.artifact.activity.CameraActivity;
import com.weidi.artifact.activity.ReceiveSMSsActivity;
import com.weidi.artifact.activity.RecentTaskActivity;
import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.AppsLockActivityController;
import com.weidi.artifact.db.bean.AppInfos;
import com.weidi.artifact.db.bean.BlacklistInfo;
import com.weidi.artifact.db.bean.BlacklistPhone;
import com.weidi.artifact.db.bean.BlacklistSms;
import com.weidi.artifact.db.dao.BlacklistDao;
import com.weidi.artifact.db.dao.PhoneNumberAddressQueryUtils;
import com.weidi.artifact.modle.Event;
import com.weidi.artifact.modle.Sms;
import com.weidi.callsystemmethod.ICallSystemMethod;
import com.weidi.dbutil.SimpleDao;
import com.weidi.eventbus.EventBus;
import com.weidi.log.Log;
import com.weidi.threadpool.CustomRunnable;
import com.weidi.threadpool.ThreadPool;
import com.weidi.utils.MyToast;
import com.weidi.utils.MyUtils;
import com.weidi.utils.Recorder;
import com.weidi.utils.MyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jackpal.androidterm.Term;

import static com.weidi.artifact.R.id.bt_querytools_cancel;
import static com.weidi.artifact.R.id.bt_querytools_sure;
import static com.weidi.artifact.R.id.fun7_btn;

//SIMIsChangedReceiver在这个广播中启动 还有StartActivity
public class CoreService extends Service implements
        EventBus.EventListener,
        OnClickListener,
        View.OnLongClickListener,
        OnTouchListener {

    private static final String TAG = "CoreService";
    private static final boolean DEBUG = true;
    private Context mContext;
    private TelephonyManager mTelephonyManager;
    private InputMethodManager mInputMethodManager;
    private ConnectivityManager mConnectivityManager;
    private AnnoyingBroadcastReceiver mAnnoyingBroadcastReceiver;
    private SharedPreferences mSharedPreferences;
    //    private IntentFilter mIntentFilter;
    private BlacklistDao mBlacklistDao;
    //    private BlacklistInfo mBlacklistInfo;
    private WindowManager mCallWindowManager;
    private WindowManager.LayoutParams mCallLayoutParams;

    private Uri uri = Uri.parse("content://sms");
    // private SmsMessage mSmsMessage;
    private ContentResolver mContentResolver;
    private ContentValues mContentValues;

    public static boolean isScreenOnOrOff = true;// 用于判断屏幕是否亮着 true为点亮状态
    // 用于测试
    private PendingIntent securityPhonePendingIntent;

    private List<RunningAppProcessInfo> mRunningAppProcessInfoList;// 正在运行的进程的集合
    private boolean notifyChangeProcessCount_flag = true;

    private SDCardFileObserver mSdCardFileObserver;

    private Vibrator mVibretor;// 振动

    //    private Intent mGoHomeIntent;

    private Recorder mRecorder;

    private Handler mUiHandler;

    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;

    public static final String[] commandsBack = new String[]{"keyevent", "KEYCODE_BACK"};
    public static final String[] commandsHome = new String[]{"keyevent", "KEYCODE_HOME"};
    public static final String[] commandsPower = new String[]{"keyevent", "KEYCODE_POWER"};

    private SimpleDateFormat mPhoneSimpleDateFormat =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

    private SimpleDateFormat mSimpleDateFormat =
            new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate()");

        init();

        myBroadcastReceiver();

        if (null == mSdCardFileObserver) {
            String path = "/storage/sdcard0/Download/app/";
            mSdCardFileObserver = new SDCardFileObserver(path);
            mSdCardFileObserver.startWatching(); // 开始监听
        }

        initCallView();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand():intent = " + intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy()");
        if (mPhoneStateListener != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
            mPhoneStateListener = null;
        }
        if (mAnnoyingBroadcastReceiver != null) {
            mContext.unregisterReceiver(mAnnoyingBroadcastReceiver);
            mAnnoyingBroadcastReceiver = null;
        }

        if (securityPhonePendingIntent != null) {
            ((MyApplication) getApplication()).mAlarmManager.cancel(securityPhonePendingIntent);
            securityPhonePendingIntent = null;
        }

        notifyChangeProcessCount_flag = false;

        if (null != mSdCardFileObserver) {
            mSdCardFileObserver.stopWatching(); // 停止监听
            mSdCardFileObserver = null;
        }

        if (mStartCallBroadcastReceiver != null) {
            unregisterReceiver(mStartCallBroadcastReceiver);
            mStartCallBroadcastReceiver = null;
        }

        removeGestureView();

        if (operationView != null) {
            mWindowManager.removeView(operationView);
            operationView = null;
        }

        if (mSystemDatabasesChangedContentObserver != null) {
            mContext.getContentResolver().unregisterContentObserver(
                    mSystemDatabasesChangedContentObserver);
        }

        /*if(mRecorder != null){
            mRecorder.stopRecording();
            mRecorder = null;
        }*/

        EventBus.getDefault().unregister(this);
    }

    /*
     * 点击事件
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call: {// 打电话按钮
                try {
                    String myNumber = number.getText().toString().trim();
                    if (((MyApplication) mContext.getApplicationContext())
                            .getSystemCall() != null && !TextUtils.isEmpty(myNumber)) {
                        mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        ((MyApplication) mContext.getApplicationContext())
                                .getSystemCall().call(myNumber);
                        if (callView != null && callView.isShown()) {
                            mCallWindowManager.removeView(callView);
                            // callView = null;
                            // mCallLayoutParams = null;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case R.id.cancel: {// 打电话取消按钮
                if (callView != null && callView.isShown()) {
                    mCallWindowManager.removeView(callView);
                }
                break;
            }
            case R.id.serial_killer_back:
                goBack();
                break;
            case R.id.serial_killer_home:
                goHome();
                break;
            case R.id.serial_killer_foreground:
                killForegroundApp();
                break;
            case R.id.serial_killer_background:
                killBackgroundApp();
                break;
            case R.id.serial_killer_turnoff:
                lockScreen();
                break;
            default:
        }
    }

    public boolean onLongClick(View v) {

        return true;
    }

    /*
     * 触摸事件
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                try {
                    x0 = (int) event.getX(0);
                    y0 = (int) event.getY(0);
                    // Log.d(TAG, "x0 = "+x0+" y0 = "+y0);
                    x1 = (int) event.getX(1);// 不要注释掉
                    y1 = (int) event.getY(1);// 不要注释掉
                    time0 = System.currentTimeMillis();
                } catch (Exception e1) {
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (Configuration.ORIENTATION_PORTRAIT == mContext.getResources()
                        .getConfiguration().orientation) {

                    dx = MyUtils.getScreenWidth(mContext);
                    dy = MyUtils.getScreenHeight(mContext);
                    vertical(v, event);

                } else {

                    dx = MyUtils.getScreenWidth(mContext);
                    dy = MyUtils.getScreenHeight(mContext);
                    horizontal(v, event);

                }

                break;
            case MotionEvent.ACTION_UP:
                x0 = 0;
                y0 = 0;
                x1 = 0;
                y1 = 0;
                time0 = 0;
                break;
        }
        return true;
    }

    // 连环杀手---杀进程
    boolean wait = true;// 防止多次点击

    public void serialKiller(Context context) {// 全杀，包括前台
        if (wait) {
            wait = false;
            // 把集合存到内存中，这样速度更快---把安装的每个应用给杀一遍
            List<AppInfos> appList = ((MyApplication) getApplication()).appList;
            // List<AppInfos> userList =
            // ((WeidiApplication)getApplication()).userList;
            String pkg = null;
            for (AppInfos ai : appList) {
                pkg = ai.getPackageName();
                if (pkg.contains(":")) {
                    int index = pkg.indexOf(":");
                    String newPkg = pkg.substring(0, index);
                    if (!((MyApplication) context.getApplicationContext()).pkgList.contains
                            (newPkg)) {
                        MyUtils.forceStopPackage(context, pkg);
                    }
                } else {// packageName也有可能是com.lbe.security:service这种进程，所以用包含
                    if (!((MyApplication) context.getApplicationContext()).pkgList.contains(pkg)) {
                        MyUtils.forceStopPackage(context, pkg);
                    }
                }
            }
            wait = true;
        }
    }

    /**
     * EventListener
     *
     * @param what
     * @param object
     */
    @Override
    public void onEvent(int what, final Object object) {
        // Log.d(TAG, "onEvent():what = " + what);
        switch (what) {
            case Constant.CORESERVICE:
                stopSelf();
                break;

            case Constant.SHOWGESTUREVIEW:
                showGestureView();
                break;

            case Constant.HIDEGESTUREVIEW:
                hideGestureView();
                break;

            default:
        }
    }

    private Dialog mFunctionAlertDialog;

    private void showFunctionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(getApplicationContext(), R.layout.functions_dialog, null);
        Button fun1_btn = (Button) view.findViewById(R.id.fun1_btn);
        Button fun2_btn = (Button) view.findViewById(R.id.fun2_btn);
        Button fun3_btn = (Button) view.findViewById(R.id.fun3_btn);
        Button fun4_btn = (Button) view.findViewById(R.id.fun4_btn);
        Button fun5_btn = (Button) view.findViewById(R.id.fun5_btn);
        Button fun6_btn = (Button) view.findViewById(R.id.fun6_btn);
        Button fun7_btn = (Button) view.findViewById(R.id.fun7_btn);
        Button fun8_btn = (Button) view.findViewById(R.id.fun8_btn);
        Button fun9_btn = (Button) view.findViewById(R.id.fun9_btn);
        Button fun10_btn = (Button) view.findViewById(R.id.fun10_btn);
        Button fun11_btn = (Button) view.findViewById(R.id.fun11_btn);
        Button fun12_btn = (Button) view.findViewById(R.id.fun12_btn);
        Button fun13_btn = (Button) view.findViewById(R.id.fun13_btn);
        fun1_btn.setText("关机");
        fun2_btn.setText("重启");
        fun3_btn.setText("电话");
        fun4_btn.setText("关门");
        fun5_btn.setText("打开常亮");
        fun6_btn.setText("关闭常亮");
        try {
            int enabled = 0;
            if (((MyApplication) mContext.getApplicationContext())
                    .getSystemCall() != null) {
                try {
                    enabled = ((MyApplication) mContext.getApplicationContext())
                            .getSystemCall().getWifiEnabledState();
                    Log.d(TAG, "enabled = " + enabled);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            if (enabled == 3) {
                fun7_btn.setText("关闭Wi-Fi");
            } else {
                fun7_btn.setText("打开Wi-Fi");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mConnectivityManager != null) {
            if (mConnectivityManager.getMobileDataEnabled()) {
                fun8_btn.setText("关闭移动数据");
            } else {
                fun8_btn.setText("打开移动数据");
            }
        }
        fun9_btn.setText("二维码");
        fun10_btn.setText("连续拔打电话");
        fun11_btn.setText("Terminal");
        fun1_btn.setOnClickListener(new FunctionOnClickListener());
        fun2_btn.setOnClickListener(new FunctionOnClickListener());
        fun3_btn.setOnClickListener(new FunctionOnClickListener());
        fun4_btn.setOnClickListener(new FunctionOnClickListener());
        fun5_btn.setOnClickListener(new FunctionOnClickListener());
        fun6_btn.setOnClickListener(new FunctionOnClickListener());
        fun7_btn.setOnClickListener(new FunctionOnClickListener());
        fun8_btn.setOnClickListener(new FunctionOnClickListener());
        fun9_btn.setOnClickListener(new FunctionOnClickListener());
        fun10_btn.setOnClickListener(new FunctionOnClickListener());
        fun11_btn.setOnClickListener(new FunctionOnClickListener());
        fun12_btn.setOnClickListener(new FunctionOnClickListener());
        fun13_btn.setOnClickListener(new FunctionOnClickListener());
        builder.setView(view);
        mFunctionAlertDialog = builder.create();
        mFunctionAlertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mFunctionAlertDialog.show();
    }

    private class FunctionOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (mFunctionAlertDialog != null) {
                mFunctionAlertDialog.dismiss();
                mFunctionAlertDialog = null;
            }
            switch (view.getId()) {
                case R.id.fun1_btn:
                    shutdown();
                    break;

                case R.id.fun2_btn:
                    reboot();
                    break;

                case R.id.fun3_btn:
                    call();
                    break;

                case R.id.fun4_btn:
                    protectMyPhone();
                    break;

                case R.id.fun5_btn:
                    enableScreenOn();
                    break;

                case R.id.fun6_btn:
                    disableScreenOn();
                    break;

                case fun7_btn:
                    try {
                        int enabled = 0;
                        if (((MyApplication) mContext.getApplicationContext())
                                .getSystemCall() != null) {
                            try {
                                enabled = ((MyApplication) mContext.getApplicationContext())
                                        .getSystemCall().getWifiEnabledState();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                        if (enabled == 3) {
                            setWifiDataEnabled(false);
                        } else {
                            setWifiDataEnabled(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                case R.id.fun8_btn:
                    if (mConnectivityManager != null) {
                        if (mConnectivityManager.getMobileDataEnabled()) {
                            setMobileDataEnabled(false);
                        } else {
                            setMobileDataEnabled(true);
                        }
                    }
                    break;

                case R.id.fun9_btn:
                    Intent intent = new Intent(mContext, ScanCodeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    break;

                case R.id.fun10_btn:
                    addPhoneNumberForCall();
                    break;

                case R.id.fun11_btn:
                    intent = new Intent(mContext, Term.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                    break;

                case R.id.fun12_btn:
                    WifiInfo wifiInfo = null;
                    ICallSystemMethod call =
                            ((MyApplication) mContext.getApplicationContext()).getSystemCall();
                    if (call != null) {
                        try {
                            wifiInfo = call.getConnectionInfo();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                        if (wifiInfo != null) {
                            String hostAddress = NetworkUtils.intToInetAddress(
                                    wifiInfo.getIpAddress()).getHostAddress();
                            if (!TextUtils.isEmpty(hostAddress)) {
                                MyToast.show(hostAddress);
                            }
                        }
                    }
                    break;

                default:
            }
        }
    }

    /**
     * 正规的做法就是先判断一下设备是否支持移动数据网络,支持才允许操作
     *
     * @param enabled
     */
    private void setMobileDataEnabled(final boolean enabled) {
        if (((MyApplication) mContext.getApplicationContext()).getSystemCall() != null) {
            ThreadPool.getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((MyApplication) mContext.getApplicationContext())
                                .getSystemCall().setMobileDataEnabled(enabled);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void setWifiDataEnabled(final boolean enabled) {
        if (((MyApplication) mContext.getApplicationContext()).getSystemCall() != null) {
            ThreadPool.getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        ((MyApplication) mContext.getApplicationContext())
                                .getSystemCall().setWifiEnabled(enabled);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void enableScreenOn() {
        if (mWakeLock != null) {
            try {
                // 是否需计算锁的数量
                mWakeLock.setReferenceCounted(false);
                // 请求屏幕常亮,一般在onResume()中设置
                mWakeLock.acquire();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void disableScreenOn() {
        if (mWakeLock != null) {
            try {
                // 是否需计算锁的数量
                mWakeLock.setReferenceCounted(false);
                // 取消屏幕常亮，一般在onPause()中设置
                mWakeLock.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void shutdown() {
        if (((MyApplication) mContext.getApplicationContext()).getSystemCall() != null) {
            try {
                ((MyApplication) mContext.getApplicationContext())
                        .getSystemCall().shutdown(false, false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void reboot() {
        if (((MyApplication) mContext.getApplicationContext()).getSystemCall() != null) {
            try {
                ((MyApplication) mContext.getApplicationContext())
                        .getSystemCall().reboot(false, "reboot", false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 程序包新增加或者被删除时发一个通知给MyApplication，让它更新一下数据
     */
    private void notifyPackageAddOrRemove(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.aowin.mobilesafe.package");
        context.sendBroadcast(intent);
    }

    /**
     * 检测文件的增加、删除
     */
    private class SDCardFileObserver extends FileObserver {
        private Thread installAppThread;

        public SDCardFileObserver(String path) {
            super(path);
        }

        // mask:指定要监听的事件类型，默认为FileObserver.ALL_EVENTS
        public SDCardFileObserver(String path, int mask) {
            super(path, mask);
        }

        @Override
        public void onEvent(int event, final String path) {
            final int action = event & FileObserver.ALL_EVENTS;
            switch (action) {
                case FileObserver.CREATE:// .jpg .png .xml .mp3 .mp4 .avi .txt
                    // .apk
                    installAppThread = new Thread() {
                        @Override
                        public void run() {
                            if (path != null) {
                                String ph = "/storage/sdcard0/Download/app/";
                                File file = new File(ph);// file是否存在也不判断了，因为这是给自己用的，所以本人确实这个文件是存在的
                                File[] files = file.listFiles();
                                for (File f : files) {
                                    if (f.getName().endsWith(".apk")) {// f.getAbsolutePath()
                                        // == ph
                                        // +
                                        // path;
                                        String packageName = MyUtils.getAppPackageName(mContext,
                                                ph, path);
                                        MyUtils.installPackage(mContext, Uri.parse(f
                                                .getAbsolutePath()), packageName, f
                                                .getAbsolutePath());
                                    }
                                }
                                ph = null;
                                file = null;
                                files = null;
                            }
                            installAppThread = null;
                        }
                    };
                    installAppThread.start();
                    break;
                // case FileObserver.OPEN:
                // if(path != null)Log.d(TAG, "event: 文件或目录被打开, path: "
                // + path);
                // break;
                // case FileObserver.CLOSE_WRITE:
                // if(path != null)Log.d(TAG, "event: 文件或目录被关闭, path: "
                // + path);
                // break;
                // case FileObserver.ACCESS:
                // if(path != null)Log.d(TAG, "event: 文件或目录被访问, path: "
                // + path);
                // break;
                // case FileObserver.MODIFY:
                // if(path != null)Log.d(TAG, "event: 文件或目录被修改, path: "
                // + path);
                // break;
                // case FileObserver.DELETE:
                // if(path != null)Log.d(TAG, "event: 文件或目录被删除, path: "
                // + path);
                // break;
            }
        }

    }

    /**
     * 广播的处理
     */
    private class AnnoyingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if ("android.intent.action.ACTION_POWER_CONNECTED".equals(intent.getAction())) {
                    // if(!MyUtils.isSpecificServiceAlive(mContext,
                    // "PeriodicalSerialKillerService")){
                    // periodicalSerialKillerServiceIntent = new
                    // Intent(mContext, PeriodicalSerialKillerService.class);
                    // mContext.startService(periodicalSerialKillerServiceIntent);
                    // }
                } else if ("android.intent.action.ACTION_POWER_DISCONNECTED".equals(
                        intent.getAction())) {
                    // if(MyUtils.isSpecificServiceAlive(mContext,
                    // "PeriodicalSerialKillerService")){
                    // stopService(periodicalSerialKillerServiceIntent);
                    // periodicalSerialKillerServiceIntent = null;
                    // }
                } else if ("android.intent.action.NEW_OUTGOING_CALL".equals(intent.getAction())) {
                    // 有电话打出时的操作
                    outNumber = getResultData();
                    outCallTime = System.currentTimeMillis();
                    outRing = true;
                    Log.d(TAG, "NEW_OUTGOING_CALL---outNumber = " + outNumber);
                    MyToast.show("NEW_OUTGOING_CALL");
                } else if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                    EventBus.getDefault().post(Constant.SCREEN_OFF, null);
                    String currentTime = mPhoneSimpleDateFormat.format(new Date());
                    Event event = new Event();
                    event.time = currentTime;
                    event.event1 = "android.intent.action.SCREEN_OFF";
                    SimpleDao.getInstance().add(Event.class, event);
                    isScreenOnOrOff = false;
                } else if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                    EventBus.getDefault().post(Constant.SCREEN_ON, null);
                    String currentTime = mPhoneSimpleDateFormat.format(new Date());
                    Event event = new Event();
                    event.time = currentTime;
                    event.event1 = "android.intent.action.SCREEN_ON";
                    SimpleDao.getInstance().add(Event.class, event);
                    isScreenOnOrOff = true;
                } else if ("android.intent.action.TIME_TICK".equals(intent.getAction())) {
                    // 每分钟广播一次
                } else if ("android.intent.action.UID_REMOVED".equals(intent.getAction())) {
                    notifyPackageAddOrRemove(context);
                    MyToast.show("UID_REMOVED");
                } else if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
                    receiverSMSsService(context, intent);
                    abortBroadcast();
                }
            }

        }

    }

    // 已经能够截全屏了，只是图片大小有点大。
    //    private Thread screenShotThread;

    /**
     * 截屏
     */
    @SuppressLint("NewApi")
    private void screenShot() {// 截好屏幕后接下去要实现能够编辑功能，现在还没有实现
        final CustomRunnable mCustomRunnable = new CustomRunnable();
        mCustomRunnable.setCallBack(
                new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {

                    }

                    @Override
                    public Object running() {
                        Date date = new Date();
                        String imagePath = Environment.getExternalStorageDirectory() +
                                "/Pictures/Screenshots/" + mSimpleDateFormat.format(date) + ".png";
                        Bitmap mScreenBitmap;
                        DisplayMetrics mDisplayMetrics;
                        Display mDisplay;

                        mDisplay = mCallWindowManager.getDefaultDisplay();
                        mDisplayMetrics = new DisplayMetrics();
                        mDisplay.getRealMetrics(mDisplayMetrics);

                        float[] dims = {mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels};
                        // screenshot利用反射得到的，所以会费点时间
                        mScreenBitmap = MyUtils.screenshot((int) dims[0], (int) dims[1]);
                        if (mScreenBitmap != null) {
                            try {
                                FileOutputStream out = new FileOutputStream(imagePath);
                                mScreenBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                                out.flush();
                                out.close();
                                out = null;
                                mScreenBitmap = null;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        mDisplay = null;
                        mDisplayMetrics = null;
                        date = null;
                        //                        if (screenShotThread != null) {
                        //                            screenShotThread.interrupt();
                        //                            screenShotThread = null;
                        //                        }
                        Runtime.getRuntime().gc();
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
        ThreadPool.getCachedThreadPool().execute(mCustomRunnable);


        //        if (screenShotThread == null) {
        //            screenShotThread = new Thread() {
        //                public void run() {
        //                    Date date = new Date();
        //                    String imagePath = Environment.getExternalStorageDirectory() +
        //                            "/Pictures/Screenshots/" + mPhoneSimpleDateFormat.format
        // (date) +
        // ".png";
        //                    Bitmap mScreenBitmap;
        //                    DisplayMetrics mDisplayMetrics;
        //                    Display mDisplay;
        //
        //                    mDisplay = mCallWindowManager.getDefaultDisplay();
        //                    mDisplayMetrics = new DisplayMetrics();
        //                    mDisplay.getRealMetrics(mDisplayMetrics);
        //
        //                    float[] dims = {mDisplayMetrics.widthPixels, mDisplayMetrics
        // .heightPixels};
        //                    // screenshot利用反射得到的，所以会费点时间
        //                    mScreenBitmap = MyUtils.screenshot((int) dims[0], (int) dims[1]);
        //                    if (mScreenBitmap != null) {
        //                        try {
        //                            FileOutputStream out = new FileOutputStream(imagePath);
        //                            mScreenBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        //                            out.flush();
        //                            out.close();
        //                            out = null;
        //                            mScreenBitmap = null;
        //                        } catch (Exception e) {
        //                            e.printStackTrace();
        //                        }
        //                    }
        //                    mDisplay = null;
        //                    mDisplayMetrics = null;
        //                    date = null;
        //                    if (screenShotThread != null) {
        //                        screenShotThread.interrupt();
        //                        screenShotThread = null;
        //                    }
        //                    Runtime.getRuntime().gc();
        //                }
        //
        //                ;
        //            };
        //        }
        //        screenShotThread.start();
    }

    /*************************************************************************/

    private WindowManager mWindowManager;
    private android.view.WindowManager.LayoutParams mLayoutParams;
    private int x0, y0, x1, y1;
    private int dx, dy;
    private long time0;
    private View viewLeft, viewTop, viewRight, viewBottom;// 透明窗体
    private int PX = 70;
    private View operationView;

    /**
     * 初始化
     */
    private void init() {
        EventBus.getDefault().register(this);
        mContext = getApplicationContext();
        mWindowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        mCallWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        // 监听电话的状态
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mInputMethodManager = (InputMethodManager) mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        mConnectivityManager = ConnectivityManager.from(this);
        mVibretor = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mSharedPreferences = mContext.getSharedPreferences(
                Constant.APP_CONFIG, Context.MODE_PRIVATE);
        mPowerManager = (PowerManager) this.getSystemService(Service.POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "Keep Screen On");

        mContentResolver = mContext.getContentResolver();
        mBlacklistDao = new BlacklistDao(mContext);

        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        addGestureView();

        //        // 后退到桌面并结束当前Activity
        //        mGoHomeIntent = new Intent();
        //        mGoHomeIntent.setAction("android.intent.action.MAIN");
        //        mGoHomeIntent.addCategory("android.intent.category.HOME");
        //        // 下面两句可能不需要指定 没去测试
        //        mGoHomeIntent.addCategory("android.intent.category.DEFAULT");
        //        mGoHomeIntent.addCategory("android.intent.category.MONKEY");
        //        // 在服务中开启一个Activity这句少不了
        //        mGoHomeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.STARTING_A_PHONE_CALL");
        registerReceiver(mStartCallBroadcastReceiver, intentFilter);

        //        initOperationView();

        monitoringRemoteService();

        if (!MyUtils.isSpecificServiceAlive(
                this,
                Constant.CLASS_PERIODICALSERIALKILLERSERVICE)) {
            Intent intent = new Intent(this, PeriodicalSerialKillerService.class);
            startService(intent);
        }

        if (!MyUtils.isSpecificServiceAlive(
                this,
                Constant.CLASS_APPSLOCKSERVICE)) {
            Intent intent = new Intent(this, AppsLockService.class);
            startService(intent);
        }

        mContext.getContentResolver().registerContentObserver(
                Uri.parse(Constant.SMS_URI), true, mSystemDatabasesChangedContentObserver);
    }

    private void monitoringRemoteService() {
        ThreadPool.getFixedThreadPool(Constant.FIXEDTHREADPOOLCOUNT).execute(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (mUiHandler == null) {
                    mUiHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            startCoreService();
                            mUiHandler.sendEmptyMessageDelayed(0, 1 * 1000);
                        }
                    };

                    mUiHandler.sendEmptyMessage(0);
                }
                Looper.loop();
            }
        });
    }

    private void startCoreService() {
        // 开启远程核心服务
        if (!MyUtils.isSpecificServiceAlive(this, Constant.REMOTESERVICENAME)) {
            Intent intent = new Intent();
            intent.setClassName(Constant.REMOTEPACKAGENAME, Constant.REMOTESERVICENAME);
            startServiceAsUser(intent, UserHandle.OWNER);
        }
    }

    private void addGestureView() {
        // 滑动手势
        viewLeft = LayoutInflater.from(mContext).inflate(
                R.layout.layout_weidi_left, null);
        viewBottom = LayoutInflater.from(mContext).inflate(
                R.layout.layout_weidi_bottom, null);

        viewLeft.setOnTouchListener(this);
        viewBottom.setOnTouchListener(this);

        mWindowManager.addView(
                viewLeft,
                setLayoutParams(
                        MyUtils.px2dip(mContext, PX),
                        WindowManager.LayoutParams.MATCH_PARENT,
                        Gravity.TOP,
                        Gravity.LEFT));
        mWindowManager.addView(
                viewBottom,
                setLayoutParams(
                        WindowManager.LayoutParams.MATCH_PARENT,
                        MyUtils.px2dip(mContext, PX),
                        Gravity.BOTTOM,
                        Gravity.LEFT));
    }

    private void removeGestureView() {
        if (viewLeft != null) {
            mWindowManager.removeView(viewLeft);
            viewLeft = null;
        }

        if (viewBottom != null) {
            mWindowManager.removeView(viewBottom);
            viewBottom = null;
        }
    }

    private void showGestureView() {
        if (viewLeft != null) {
            viewLeft.setVisibility(View.VISIBLE);
        }

        if (viewBottom != null) {
            viewBottom.setVisibility(View.VISIBLE);
        }
    }

    private void hideGestureView() {
        if (viewLeft != null) {
            viewLeft.setVisibility(View.GONE);
        }

        if (viewBottom != null) {
            viewBottom.setVisibility(View.GONE);
        }
    }

    private void initOperationView() {
        operationView = LayoutInflater.from(mContext).inflate(R.layout.serial_killer_view, null);
        operationView.findViewById(R.id.serial_killer_back).setOnClickListener(this);
        operationView.findViewById(R.id.serial_killer_home).setOnClickListener(this);
        operationView.findViewById(R.id.serial_killer_foreground).setOnClickListener(this);
        operationView.findViewById(R.id.serial_killer_background).setOnClickListener(this);
        operationView.findViewById(R.id.serial_killer_turnoff).setOnClickListener(this);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.alpha = 30;
        params.format = 1;//很重要 按钮按几下后，状态不恢复到原来那样子，设置这个后就好了
        params.gravity = Gravity.TOP + Gravity.LEFT;
        params.x = mWindowManager.getDefaultDisplay().getWidth() / 2 - operationView.getWidth() / 2;
        params.y = mWindowManager.getDefaultDisplay().getHeight() / 2 -
                operationView.getHeight() / 2;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        operationView.setOnTouchListener(new OnTouchListener() {

            int startX;
            int startY;
            int newX;
            int newY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        newX = (int) event.getRawX();
                        newY = (int) event.getRawY();
                        int dx = newX - startX;
                        int dy = newY - startY;
                        params.x += dx;
                        params.y += dy;
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > (mWindowManager.getDefaultDisplay().getWidth() -
                                operationView.getWidth())) {
                            params.x = mWindowManager.getDefaultDisplay().getWidth() -
                                    operationView.getWidth();
                        }
                        if (params.y > (mWindowManager.getDefaultDisplay().getHeight() -
                                operationView.getHeight())) {
                            params.y = mWindowManager.getDefaultDisplay().getHeight() -
                                    operationView.getHeight();
                        }
                        mWindowManager.updateViewLayout(operationView, params);//更新view
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    }
                }
                return false;//如果返回true的话，就没有点击事件了
            }
        });

        mWindowManager.addView(operationView, params);
    }

    /**
     * 定义的广播
     */
    private void myBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);

        intentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");//
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");//
        intentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");//
        intentFilter.addAction("android.intent.action.SCREEN_OFF");//
        intentFilter.addAction("android.intent.action.SCREEN_ON");//
        intentFilter.addAction("android.intent.action.TIME_TICK");//
        intentFilter.addAction("android.intent.action.UID_REMOVED");//
        intentFilter.addAction("android.media.RINGER_MODE_CHANGED");// 改变声音状态（有声变静音）时广播
        intentFilter.addAction("android.media.VIBRATE_SETTING_CHANGED");// 颤动变为不颤动时广播
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");//

        mAnnoyingBroadcastReceiver = new AnnoyingBroadcastReceiver();
        mContext.registerReceiver(mAnnoyingBroadcastReceiver, intentFilter);
    }

    /**
     * String LOCK_MY_PHONE = "@@@@@weidi5858258@@@@@";
     * String UNLOCK_MY_PHONE = "######weidi5858258######";
     * String UNLOCK_MY_PHONE_COMPLETE = "$$$$$$$weidi5858258$$$$$$$";
     * String SHUTDOWN = "%%%%%%%%weidi5858258%%%%%%%%";
     * String REBOOT = "*********weidi5858258*********";
     */
    private void sendSMS() {

    }

    private void showMyInfo(String msg) {
        new AlertDialog.Builder(mContext)
                .setTitle("您好!")
                .setMessage(msg)
                .setPositiveButton("取消", null)
                .show();
    }

    // /对于好多应用，会在程序中杀死 进程，这样会导致我们统计不到此时Activity结束的信息，
    // /对于这种情况需要调用 'MobclickAgent.onKillProcess( Context )'
    // /方法，保存一些页面调用的数据。正常的应用是不需要调用此方法的。
    private void Hook() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setPositiveButton("退出应用", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);
            }
        });
        builder.setNeutralButton("后退一下", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.setNegativeButton("点错了", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.show();
    }

    /**
     * 接收短信的业务逻辑
     */
    private void receiverSMSsService(Context context, Intent intent) {
        // 收到短信的广播 短信长度：中文：70 英文：160
        // （只要有一个中方输入，不包括中方状态下的标点符号，一条短信就只能是70个字符）
        if (intent == null) {
            return;
        }
        String address = "";
        String body = "";
        long receiveTime = 0;
        String time = "";

        StringBuffer stringBuffer = new StringBuffer();

        Object[] objects = (Object[]) intent.getExtras().get(Constant.PDUS);
        if (objects == null) {
            return;
        }
        // int counts = objects.length;
        for (Object object : objects) {
            SmsMessage mSmsMessage = SmsMessage.createFromPdu((byte[]) object);
            address = mSmsMessage.getOriginatingAddress();// 发件人号码
            body = mSmsMessage.getMessageBody().trim();// 短信内容
            stringBuffer.append(body);
            receiveTime = mSmsMessage.getTimestampMillis();
            time = mPhoneSimpleDateFormat.format(receiveTime);// 收到短信时的时间 存入数据库的数据则是long类型的时间值
        }

        String bodyContent = stringBuffer.toString();

        if (Constant.LOCK_MY_PHONE.equalsIgnoreCase(bodyContent)) {
            // 如果手机被偷了,发个短信把手机给锁了
            protectMyPhone();
            return;

        } else if (Constant.UNLOCK_MY_PHONE.equalsIgnoreCase(bodyContent)) {
            EventBus.getDefault().post(Constant.UNLOCK_MY_PHONE_TAG, null);
            return;

        } else if (Constant.UNLOCK_MY_PHONE_COMPLETE.equalsIgnoreCase(bodyContent)) {
            EventBus.getDefault().post(Constant.APPSLOCKSERVICE, null);
            EventBus.getDefault().post(Constant.UNLOCK_MY_PHONE_TAG, null);
            return;

        } else if (Constant.SHUTDOWN.equalsIgnoreCase(bodyContent)) {
            shutdown();
            return;

        } else if (Constant.REBOOT.equalsIgnoreCase(bodyContent)) {
            reboot();
            return;

        } else if (Constant.TAKEPICTURE.equalsIgnoreCase(bodyContent)) {
            goCameraActivity();
            return;

        } else if (bodyContent.contains(Constant.SHOWMYINFO)) {
            String[] bodyContents = bodyContent.split("@@@");
            if (bodyContents != null && bodyContents.length >= 2) {
                showMyInfo(bodyContents[1]);
                return;
            }

        }

        MyToast.show("SMS_RECEIVED");

        // 我没有判断拦截黑名单的服务是否在运行，我想让这个服务一直在运行
        if (mBlacklistDao.isNumberExist(address)) {// 黑名单
            BlacklistInfo info = mBlacklistDao.query(address);
            String mode = info.getMode();
            if ("2".equals(mode) || "3".equals(mode)) {
                String place = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(address);
                mBlacklistDao.addBlacklistSms(address, place, receiveTime,
                        time, 1, 1, bodyContent);
                // 更新黑名单中的短信拦截界面数据
                // CommunicationGuardBlacklistSmsFragment中定义的广播
                BlacklistSms sms = new BlacklistSms(address, place, receiveTime,
                        time, 1, 1, bodyContent);
                intent = new Intent();
                intent.setAction("com.aowin.mobilesafe.updateadapter.Sms");
                intent.putExtra("Sms", sms);
                context.sendBroadcast(intent);
                return;
            } else {
                receiveSMSs(context, intent, address, receiveTime, bodyContent, time);
            }
        } else {
            receiveSMSs(context, intent, address, receiveTime, bodyContent, time);
        }
    }

    // 抽取的方法
    private void receiveSMSs(final Context context, final Intent intent, final String address,
                             final long receiveTime, final String bodyContent, final String time) {
        ThreadPool.getCachedThreadPool().execute(
                new CustomRunnable().setCallBack(new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {

                    }

                    @Override
                    public Object running() {
                        MyUtils.ring(context);
                        return null;
                    }

                    @Override
                    public void onProgressUpdate(Object object) {

                    }

                    @Override
                    public void runAfter(Object object) {
                        // //系统声音 权限：android.permission.WRITE_SETTINGS
                        // Uri notification =
                        // RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        // Ringtone ring = RingtoneManager.getRingtone(mContext, notification);
                        // ring.play();

                        Uri uriContacts = Uri.parse("content://com.android.contacts/data");
                        mContentValues = new ContentValues();
                        mContentValues.put("address", address);
                        mContentValues.put("date", receiveTime);
                        mContentValues.put("read", 1);// 1为已读 0为未读
                        mContentValues.put("type", 1);// 1为接收到的短信 2为发送的短信
                        mContentValues.put("body", bodyContent);
                        mContentResolver.insert(uri, mContentValues);

                        String name = "";
                        // 根据号码查找在本地通讯录中是否有联系人，如果有，则把这个人的姓名给取出并显示，否则显示发件人的号码。
                        Cursor cursor_raw_contact_id = mContentResolver.query(uriContacts, new
                                String[]{"raw_contact_id"}, "data1=?", new String[]{address}, null);
                        Cursor cursor_name = null;
                        while (cursor_raw_contact_id.moveToNext()) {
                            String raw_contact_id = cursor_raw_contact_id.getString
                                    (cursor_raw_contact_id
                                            .getColumnIndex("raw_contact_id"));
                            cursor_name = mContentResolver.query(uriContacts, new String[]{"data1"},
                                    "mimetype_id=? and raw_contact_id=?", new String[]{"7",
                                            raw_contact_id}, null);
                            while (cursor_name.moveToNext()) {
                                // 得到联系人的姓名
                                name = cursor_name.getString(cursor_name.getColumnIndex("data1"));
                            }
                        }
                        if (cursor_name != null) {
                            cursor_name.close();
                            cursor_name = null;
                        }
                        if (cursor_raw_contact_id != null) {
                            cursor_raw_contact_id.close();
                            cursor_raw_contact_id = null;
                        }
                        if (TextUtils.isEmpty(name)) {
                            name = address;// 没有联系人就显示号码
                        }

                        // 发到ReceiveSMSsActivity去的
                        Intent intent = new Intent();
                        intent.setAction("com.aowin.mobilesafe.Sms");
                        intent.putExtra("name", name);
                        intent.putExtra("address", address);
                        intent.putExtra("body", bodyContent);
                        intent.putExtra("time", time);
                        intent.putExtra("receiveTime", String.valueOf(receiveTime));
                        context.sendBroadcast(intent);

                        Runtime.getRuntime().gc();

                        intent = new Intent();
                        intent.setClass(mContext, ReceiveSMSsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("name", name);
                        intent.putExtra("address", address);
                        intent.putExtra("body", bodyContent);
                        intent.putExtra("time", time);
                        context.startActivity(intent);
                    }

                    @Override
                    public void runError() {

                    }
                }));
    }

    @SuppressLint("InlinedApi")
    private WindowManager.LayoutParams setLayoutParams(int _width, int _height,
                                                       int position1, int position2) {
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.gravity = position1 + position2;
        // mLayoutParams.alpha = 30;
        mLayoutParams.width = _width;
        mLayoutParams.height = _height;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 特别注意在这里设置等级为系统警告
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
        // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        return mLayoutParams;
    }

    /**
     * 竖屏
     */
    private void vertical(View v, MotionEvent event) {
        try {

            if (event.getX() >= 0 && event.getX() <= PX) {
                // 手指靠左边缘划动
                if (y0 >= 0 && y0 <= (int) (dy / 3.0f) && y0 - event.getY(0) >= PX && System
                        .currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "左边缘上---》向上滑动---》");

                    vibrate(20);
                    if (MyUtils.isSpecificServiceAlive(
                            mContext,
                            "com.weidi.artifact.service.PeriodicalSerialKillerService")) {
                        EventBus.getDefault().post(
                                Constant.PERIODICALSERIALKILLERSERVICE, null);
                        MyToast.show("PeriodicalSerialKillerService is Shutdown");
                        //                        if (periodicalSerialKillerServiceIntent != null) {
                        //                            mContext.stopService
                        // (periodicalSerialKillerServiceIntent);
                        //                            periodicalSerialKillerServiceIntent = null;
                        //                        }
                    }

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= 0 && y0 <= (int) (dy / 3.0f) && event.getY(0) - y0 >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "左边缘上---》向下滑动---》");

                    vibrate(20);
                    if (!MyUtils.isSpecificServiceAlive(
                            mContext,
                            "com.weidi.artifact.service.PeriodicalSerialKillerService")) {
                        Intent periodicalSerialKillerServiceIntent = new Intent(
                                mContext,
                                PeriodicalSerialKillerService.class);
                        mContext.startService(periodicalSerialKillerServiceIntent);
                        MyToast.show("PeriodicalSerialKillerService is Start");
                    }

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= (int) (dy / 3.0f) && y0 <= (int) (2 * dy / 3.0f) && y0 - event
                        .getY(0) >= PX && System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "左边缘中---》向上滑动---》");

                    vibrate(20);
                    lockScreen();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= (int) (dy / 3.0f) && y0 <= (int) (2 * dy / 3.0f) && event.getY
                        (0) - y0 >= PX && System.currentTimeMillis() - time0 >= 500) {
                    Log.d(TAG, "左边缘中---》向下滑动---》");

                    vibrate(20);
                    goBack();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= (int) (2 * dy / 3.0f) && y0 - event.getY(0) >= PX && System
                        .currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "左边缘下---》向上滑动---》");

                    vibrate(20);
                    lockScreen();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= (int) (2 * dy / 3.0f) && event.getY(0) - y0 >= PX && System
                        .currentTimeMillis() - time0 >= 500) {
                    Log.d(TAG, "左边缘下---》向下滑动---》");

                    vibrate(20);
                    goBack();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                }
                return;
            }

            if (event.getY(0) >= 0 && event.getY(0) <= PX) {
                // 手指靠下边缘划动
                if (x0 >= 0 && x0 <= (int) (dx / 2.0) && event.getX(0) - x0 >= PX && System
                        .currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "下边缘左---》向右滑动---》");

                    vibrate(20);
                    killForegroundApp();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= 0 && x0 <= (int) (dx / 2.0) && x0 - event.getX(0) >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "下边缘左---》向左滑动---》");

                    vibrate(20);
                    goRecentTaskActivity();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= (int) (dx / 2.0) && x0 <= dx && event.getX(0) - x0 >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "下边缘右---》向右滑动---》");

                    time0 = System.currentTimeMillis();
                    vibrate(20);
                    killForegroundApp();

                    x0 = (int) event.getX(0);
                } else if (x0 >= (int) (dx / 2.0) && x0 <= dx && x0 - event.getX(0) >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "下边缘右---》向左滑动---》");

                    vibrate(20);
                    goRecentTaskActivity();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                }
                return;
            }

            if (event.getX(0) - x0 >= PX && System.currentTimeMillis() - time0 >= 500) {
                if (event.getY(0) >= 0 && event.getY(0) < (int) (dy / 3.0f)) {
                    Log.d(TAG, "上");

                    vibrate(20);
                    takeScreenshot();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (event.getY(0) >= (int) (dy / 3.0f) && event.getY(0) < (int) (2 * dy /
                        3.0f)) {
                    Log.d(TAG, "中");

                    vibrate(20);
                    showFunctionDialog();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (event.getY(0) >= (int) (2 * dy / 3.0f)) {
                    Log.d(TAG, "下");

                    vibrate(20);
                    changeApp();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                }
                return;
            }

            if (y0 - event.getY(0) >= PX && System.currentTimeMillis() - time0 >= 500) {
                time0 = System.currentTimeMillis();
                y0 = (int) event.getY(0);
                if (event.getX(0) >= 0 && event.getX(0) < (int) (dx / 2.0f)) {
                    // Log.d(TAG, "左");

                    vibrate(20);
                    goHome();

                } else if (event.getX(0) >= (int) (dx / 2.0f)) {
                    // Log.d(TAG, "右 ");

                    vibrate(20);
                    goHome();

                }
                return;
            }

        } catch (Exception e) {
        }
    }

    /**
     * 横屏
     */
    private void horizontal(View v, MotionEvent event) {
        try {

            if (event.getX() >= 0 && event.getX() <= PX) {
                // 手指靠左边缘划动
                if (y0 >= 0 && y0 <= (int) (dy / 2.0f) && y0 - event.getY(0) >= PX && System
                        .currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "左边缘上---》向上滑动---》");

                    vibrate(20);

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= 0 && y0 <= (int) (dy / 2.0f) && event.getY(0) - y0 >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "左边缘上---》向下滑动---》");

                    vibrate(20);
                    goHome();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= (int) (dy / 2.0f) && y0 <= dy && y0 - event.getY(0) >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "左边缘下---》向上滑动---》");

                    vibrate(20);
                    lockScreen();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= (int) (dy / 2.0f) && y0 <= dy && event.getY(0) - y0 >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "左边缘下---》向下滑动---》");

                    vibrate(20);
                    goBack();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                }
                return;
            }

            if (event.getY(0) >= 0 && event.getY(0) <= PX) {
                // 手指靠下边缘划动
                if (x0 >= 0 && x0 <= (int) (dx / 3.0f) && event.getX(0) - x0 >= PX && System
                        .currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "下边缘左---》向右滑动---》");

                    vibrate(20);
                    killForegroundApp();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= 0 && x0 <= (int) (dx / 3.0f) && x0 - event.getX(0) >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "下边缘左---》向左滑动---》");

                    vibrate(20);
                    goRecentTaskActivity();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= (int) (dx / 3.0f) && x0 <= (int) (2 * dx / 3.0f) && event.getX
                        (0) - x0 >= PX && System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "下边缘中---》向右滑动---》");

                    vibrate(20);

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= (int) (dx / 3.0f) && x0 <= (int) (2 * dx / 3.0f) && x0 - event
                        .getX(0) >= PX && System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "下边缘中---》向左滑动---》");

                    vibrate(20);

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= (int) (2 * dx / 3.0f) && x0 <= dx && event.getX(0) - x0 >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "下边缘右---》向右滑动---》");

                    vibrate(20);

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= (int) (2 * dx / 3.0f) && x0 <= dx && x0 - event.getX(0) >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // Log.d(TAG, "下边缘右---》向左滑动---》");

                    vibrate(20);

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                }
                return;
            }

            if (event.getX(0) - x0 >= PX && System.currentTimeMillis() - time0 >= 500) {
                time0 = System.currentTimeMillis();
                x0 = (int) event.getX(0);
                if (event.getY(0) >= 0 && event.getY(0) < (int) (dy / 2.0f)) {
                    // Log.d(TAG, "上");

                    vibrate(20);

                } else {
                    // Log.d(TAG, "下");

                    vibrate(20);

                }
                return;
            }

            if (y0 - event.getY(0) >= PX && System.currentTimeMillis() - time0 >= 500) {
                time0 = System.currentTimeMillis();
                y0 = (int) event.getY(0);
                if (event.getX(0) >= 0 && event.getX(0) < (int) (dx / 3.0f)) {
                    // Log.d(TAG, "左");

                    vibrate(20);

                } else if (event.getX(0) >= (int) (dx / 3.0f) && event.getX(0) < (int) (2 * dx /
                        3.0f)) {
                    // Log.d(TAG, "中");

                    vibrate(20);

                } else {
                    // Log.d(TAG, "右");

                    vibrate(20);

                }
                return;
            }

        } catch (Exception e) {
        }
    }

    /******************************************************************************************/

    private View callView;
    private EditText number;

    /**
     * 打电话的
     */
    private void call() {
        if (callView != null && mCallLayoutParams != null) {
            if (number != null) {
                number.setText("");
            }
            mCallWindowManager.addView(callView, mCallLayoutParams);
        }
    }

    private void initCallView() {
        callView = View.inflate(mContext, R.layout.activity_call, null);
        number = (EditText) callView.findViewById(R.id.number);
        number.setFocusable(true);
        number.setFocusableInTouchMode(true);
        number.requestFocus();
        TextView call = (TextView) callView.findViewById(R.id.call);
        TextView cancel = (TextView) callView.findViewById(R.id.cancel);
        call.setOnClickListener(this);
        cancel.setOnClickListener(this);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(number, 0);
                imm = null;
            }
        };
        timer.schedule(task, 100);
        task = null;
        timer = null;

        mCallLayoutParams = new WindowManager.LayoutParams();
        mCallLayoutParams.alpha = 30;
        mCallLayoutParams.gravity = Gravity.TOP + Gravity.LEFT;
        mCallLayoutParams.width = (int) (mCallWindowManager.getDefaultDisplay().getWidth() * 4 / 5);
        mCallLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 要想弹出软键盘，就不能设置这个属性
        // 不能设置之个属性,那么这个窗口下面的内容不能点击,只有先关闭这个窗口后,才可以点击其他内容.
        // mCallLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mCallLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        // 很重要 按钮按几下后，状态不恢复到原来那样子，设置这个后就好了
        mCallLayoutParams.format = 1;

        callView.setOnTouchListener(new OnTouchListener() {
            int startX_call;
            int startY_call;
            int newX_call;
            int newY_call;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        startX_call = (int) event.getRawX();
                        startY_call = (int) event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        newX_call = (int) event.getRawX();
                        newY_call = (int) event.getRawY();
                        int dx = newX_call - startX_call;
                        int dy = newY_call - startY_call;
                        mCallLayoutParams.x += dx;
                        mCallLayoutParams.y += dy;
                        if (mCallLayoutParams.x < 0) {
                            mCallLayoutParams.x = 0;
                        }
                        if (mCallLayoutParams.y < 0) {
                            mCallLayoutParams.y = 0;
                        }
                        if (mCallLayoutParams.x > (mCallWindowManager.getDefaultDisplay().getWidth
                                () - callView.getWidth())) {
                            mCallLayoutParams.x = mCallWindowManager.getDefaultDisplay().getWidth()
                                    - callView.getWidth();
                        }
                        if (mCallLayoutParams.y > (mCallWindowManager.getDefaultDisplay().getHeight
                                () - callView.getHeight())) {
                            mCallLayoutParams.y = mCallWindowManager.getDefaultDisplay().getHeight
                                    () - callView.getHeight();
                        }
                        mCallWindowManager.updateViewLayout(callView, mCallLayoutParams);// 更新view
                        startX_call = (int) event.getRawX();
                        startY_call = (int) event.getRawY();
                        break;
                    }
                }
                return false;
            }
        });
    }

    // Sdcard0Mapping
    private final int SERIAL_KILLER_PROCESS_COUNT = 0;
    private Handler handler;

    private void killRunningProcessIcon() {

        mRunningAppProcessInfoList = MyUtils.getRunningAppProcessInfo(mContext);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                handler = new Handler() {
                    public void handleMessage(android.os.Message msg) {
                        if (msg.what == SERIAL_KILLER_PROCESS_COUNT) {
                            // list = MyUtils.getAllRunningProcesses(mContext);
                            mRunningAppProcessInfoList = MyUtils.getRunningAppProcessInfo(mContext);
                            if (notifyChangeProcessCount_flag) {
                                handler.sendEmptyMessageDelayed(SERIAL_KILLER_PROCESS_COUNT, 2 *
                                        1000);
                            }
                        }
                    }

                    ;
                };
                // 放的位置要注意，不然要抛空指针异常。还有就是不能发送消息。
                handler.sendEmptyMessage(SERIAL_KILLER_PROCESS_COUNT);
                Looper.loop();
            }
        }).start();

        // view.setOnClickListener(new OnClickListener() {
        // long firstClickTime;
        // @Override
        // public void onClick(View v) {//双击事件
        // if(firstClickTime > 0){
        // long secondClickTime = SystemClock.uptimeMillis();
        // long dtime = secondClickTime - firstClickTime;
        // if(dtime <= 500){
        // serialKiller(mContext);
        // }else{
        // firstClickTime = 0;
        // }
        // return;
        // }
        // firstClickTime = SystemClock.uptimeMillis();
        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        // SystemClock.sleep(500);
        // firstClickTime = 0;
        // }
        // }).start();
        // }
        // });

        //        params = new WindowManager.LayoutParams();
        //        params.alpha = 30;
        //        params.format = 1;//很重要 按钮按几下后，状态不恢复到原来那样子，设置这个后就好了
        //        params.gravity = Gravity.TOP + Gravity.LEFT;
        //        params.x = wm.getDefaultDisplay().getWidth() / 2 - view.getWidth() / 2;
        //        params.y
        //                = wm.getDefaultDisplay().getHeight() / 2 - view.getHeight() / 2;
        //        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        //        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //        params.height
        //                = WindowManager.LayoutParams.WRAP_CONTENT;
        //
        //
        //        view.setOnTouchListener(new OnTouchListener() {
        //            int startX;
        //            int
        //                    startY;
        //            int newX;
        //            int newY;
        //
        //            @Override
        //            public boolean onTouch(View v, MotionEvent event) {
        //                switch (event.getAction()) {
        //                    case MotionEvent.ACTION_UP: {
        //                        break;
        //                    }
        //                    case MotionEvent.ACTION_DOWN: {
        //                        startX = (int) event.getRawX();
        //                        startY =
        //                                (int) event.getRawY();
        //                        break;
        //                    }
        //                    case MotionEvent.ACTION_MOVE: {
        //                        newX =
        //                                (int) event.getRawX();
        //                        newY = (int) event.getRawY();
        //                        int dx = newX -
        //                                startX;
        //                        int dy = newY - startY;
        //                        params.x += dx;
        //                        params.y += dy;
        //                        if (params.x < 0) {
        //                            params.x = 0;
        //                        }
        //                        if (params.y < 0) {
        //                            params.y = 0;
        //                        }
        //                        if (params.x > (wm.getDefaultDisplay().getWidth() - view
        // .getWidth())) {
        //                            params.x = wm.getDefaultDisplay().getWidth() - view
        // .getWidth();
        //                        }
        //                        if (params.y > (wm.getDefaultDisplay().getHeight() -
        //                                view.getHeight())) {
        //                            params.y = wm.getDefaultDisplay().getHeight() -
        //                                    view.getHeight();
        //                        }
        //                        wm.updateViewLayout(view, params);//更新view
        //                        startX =(int) event.getRawX();
        //                        startY = (int) event.getRawY();
        //                        break;
        //                    }
        //                }
        //                return false;//如果返回true的话，就没有点击事件了
        //                } });
        //        wm.addView(view, params);
    }

    private void changeApp() {
        EventBus.getDefault().post(Constant.CHANGEAPP, null);
    }

    /**
     * 后退
     */
    private void goBack() {
        // 经测试必须要开启线程才能达到后退效果
        //        if (((WeidiApplication) mContext.getApplicationContext()).getSystemCall() != null) {
        //            ThreadPool.getCachedThreadPool().execute(new Runnable() {
        //                @Override
        //                public void run() {
        //                    try {
        //                        ((WeidiApplication) mContext.getApplicationContext())
        // .getSystemCall().goBack();
        //                    } catch (Exception e) {
        //                        e.printStackTrace();
        //                    }
        //                }
        //            });
        //        }

        if (((MyApplication) mContext.getApplicationContext()).getSystemCall() != null) {
            ThreadPool.getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        try {
                            ((MyApplication) mContext.getApplicationContext())
                                    .getSystemCall().input(commandsBack);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * Home
     */
    private void goHome() {
        //        if (mGoHomeIntent != null) {
        //            mContext.startActivity(mGoHomeIntent);
        //        }

        if (((MyApplication) mContext.getApplicationContext()).getSystemCall() != null) {
            try {
                ((MyApplication) mContext.getApplicationContext())
                        .getSystemCall().input(commandsHome);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 锁屏
     */
    private void lockScreen() {
        //        if (((WeidiApplication) mContext.getApplicationContext()).getSystemCall() != null) {
        //            try {
        //                ((WeidiApplication) mContext.getApplicationContext()).getSystemCall()
        // .lockScreen();
        //            } catch (RemoteException e) {
        //                e.printStackTrace();
        //            }
        //        }

        if (((MyApplication) mContext.getApplicationContext()).getSystemCall() != null) {
            try {
                ((MyApplication) mContext.getApplicationContext())
                        .getSystemCall().input(commandsPower);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void protectMyPhone() {
        AppsLockActivityController.sendBroadcastToInputManagerService(mContext, true);
        AppsLockActivityController.sendBroadcastToMountService(mContext, true);
        AppsLockActivityController.sendBroadcastToPackageManagerService(mContext, false);

        mSharedPreferences.edit().putBoolean(Constant.USB_DEBUG, false).commit();
        mSharedPreferences.edit().putBoolean(Constant.SDCARD_USBDISK, false).commit();

        lockScreen();

        Intent intent = new Intent(this, AppsLockActivity.class);
        // 服务中开启Activity必须要加上这句
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Constant.APP_PACKAGE_NAME, Constant.LAUNCHER);
        startActivity(intent);

        if (!MyUtils.isSpecificServiceAlive(this, "com.weidi.artifact.service.AppsLockService")) {
            intent = new Intent(this, AppsLockService.class);
            startService(intent);
        }
    }

    /**
     * 截屏
     */
    private void takeScreenshot() {
        if (((MyApplication) mContext.getApplicationContext()).getSystemCall() != null) {
            try {
                ((MyApplication) mContext.getApplicationContext())
                        .getSystemCall().takeScreenshot();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭应用
     */
    private void killForegroundApp() {
        List<RunningTaskInfo> runningTaskInfoList =
                ((MyApplication) mContext.getApplicationContext()).mActivityManager
                        .getRunningTasks(1);
        if (runningTaskInfoList != null && runningTaskInfoList.size() > 0) {
            String packageName = runningTaskInfoList.get(0).topActivity.getPackageName();
            if (!Constant.LAUNCHER.equals(packageName)
                    && !mContext.getPackageName().equals(packageName)) {
                if (((MyApplication) mContext.getApplicationContext()).getSystemCall() != null) {
                    try {
                        ((MyApplication) mContext.getApplicationContext())
                                .getSystemCall().forceStopPackage(packageName);
                        EventBus.getDefault().post(Constant.BEKILLEDPROCESSNAME, packageName);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            runningTaskInfoList.clear();
            runningTaskInfoList = null;
            packageName = null;
        }
    }

    private void killBackgroundApp() {
        if (MyUtils.isSpecificServiceAlive(
                mContext,
                "com.weidi.artifact.service.PeriodicalSerialKillerService")) {
            EventBus.getDefault().post(Constant.PERIODICALSERIALKILLERSERVICE, null);
            MyToast.show("PeriodicalSerialKillerService is Shutdown");
        } else {
            Intent periodicalSerialKillerServiceIntent = new Intent(
                    mContext,
                    PeriodicalSerialKillerService.class);
            mContext.startService(periodicalSerialKillerServiceIntent);
            MyToast.show("PeriodicalSerialKillerService is Start");
        }
    }

    /**
     * 杀后台，不杀前台
     */
    //    private void killBackgroundApp() {
    //        MyUtils.serialKiller(mContext);// 执行一次大概花费2秒左右的时间
    //    }

    /**
     * 列出运行过的应用
     */
    private void goRecentTaskActivity() {
        Intent recentTaskActivity = new Intent(mContext, RecentTaskActivity.class);
        recentTaskActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(recentTaskActivity);
    }

    /**
     * 拍照
     */
    private void goCameraActivity() {
        Intent cameraActivity = new Intent(mContext, CameraActivity.class);
        cameraActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(cameraActivity);
    }

    private void vibrate(long milliseconds) {
        mVibretor.vibrate(milliseconds);
    }

    /*******************************添加电话号码用于打电话*******************************/

    private Dialog mAddPhoneNumberForCallAlertDialog;
    private String mPhoneNumberForCall;
    private boolean mIsConnectedForCall;
    private EditText mPhoneNumberEditText;

    private void addPhoneNumberForCall() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.querytools_view, null);
        // view.findViewById(R.id.tv_querytools_title).setVisibility(View.GONE);
        view.findViewById(R.id.et_querytools_number).setVisibility(View.GONE);
        mPhoneNumberEditText = (EditText) view.findViewById(R.id.et_querytools_name);
        mPhoneNumberEditText.setInputType(InputType.TYPE_CLASS_PHONE);

        ClipboardManager clipboardManager =
                (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        mPhoneNumberEditText.setText(clipboardManager.getText().toString().trim());

        Button sureBtn = (Button) view.findViewById(bt_querytools_sure);
        Button cancelBtn = (Button) view.findViewById(bt_querytools_cancel);
        sureBtn.setOnClickListener(new AddPhoneNumberForCallClickListener());
        cancelBtn.setOnClickListener(new AddPhoneNumberForCallClickListener());

        builder.setView(view);
        mAddPhoneNumberForCallAlertDialog = builder.create();
        mAddPhoneNumberForCallAlertDialog.getWindow().setType(
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        mAddPhoneNumberForCallAlertDialog.show();
    }

    private class AddPhoneNumberForCallClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_querytools_sure:
                    mPhoneNumberForCall = mPhoneNumberEditText.getText().toString().trim();
                    if (TextUtils.isEmpty(mPhoneNumberForCall)) {
                        MyToast.show("请输入电话号码");
                        return;
                    }
                    mIsConnectedForCall = false;
                    Log.d(TAG, "mPhoneNumberForCall = " + mPhoneNumberForCall);
                    break;

                case R.id.bt_querytools_cancel:
                    mIsConnectedForCall = true;
                    mPhoneNumberForCall = null;
                    break;

                default:
            }
            if (mAddPhoneNumberForCallAlertDialog != null) {
                mAddPhoneNumberForCallAlertDialog.dismiss();
                mAddPhoneNumberForCallAlertDialog = null;
            }
        }
    }

    /*******************************添加电话号码用于打电话*******************************/

    /************************************************************************/

    /**
     * CallManager->getFirstActiveRingingCall()===>Call
     * Call->getState()===>State
     * State == Call.State.ACTIVE 电话接通的时候
     */
    private long callStateIdleTime;
    private long callStateRingingTime;
    private long callStateOffHookTime;
    private long outCallTime;
    private long outHookTime;
    private long outIdleTime;
    private boolean inRing = false;
    private boolean inBlackRing = false;
    private boolean inHook = false;
    private boolean outRing = false;
    private boolean outHook = false;
    private String outNumber;
    private String incomingNumber;
    //    private String address;// 打出去的电话接通与没有接通这两个状态还没有搞好

    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            /**
             onStartCommand():intent = Intent { cmp=com.weidi.artifact/.service.CoreService }
             onCallStateChanged(): state = 1 incomingNumber = 13764926846
             CALL_STATE_RINGING---callStateRingingTime = 1488242107119
             CALL_STATE_RINGING---callStateRingingTime = 1488242107119
             onSaveInstanceState():outState = Bundle[{}]
             onCallStateChanged(): state = 2 incomingNumber =
             CALL_STATE_OFFHOOK---outHookTime = 1488242114807
             mStartCallBroadcastReceiver onReceive():intent = Intent { act=android
             .intent.action.STARTING_A_PHONE_CALL flg=0x10 (has extras) }
             */
            // 电话打出去时incomingNumber为null
            Log.d(TAG, "onCallStateChanged(): state = " + state +
                    " incomingNumber = " + incomingNumber);
            if (state == 1) {
                // 此时才有号码
                CoreService.this.incomingNumber = incomingNumber;
            }
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: {// 空闲(电话一挂断就执行这里)
                    callStateIdle(CoreService.this.incomingNumber);
                    break;
                }
                case TelephonyManager.CALL_STATE_RINGING: {// 响铃
                    callStateRinging(CoreService.this.incomingNumber);
                    break;
                }
                case TelephonyManager.CALL_STATE_OFFHOOK: {// 通话(电话一打出去就执行这里的代码)
                    // 此时号码为null
                    callStateOffHook();
                    break;
                }
            }
            super.onCallStateChanged(state, incomingNumber);
        }

    };

    /************************************************************************/

    private void callStateIdle(String incomingNumber) {
        callStateIdleTime = System.currentTimeMillis();
        Log.d(TAG, "CALL_STATE_IDLE---callStateIdleTime = " + callStateIdleTime);

        BlacklistPhone phone = null;
        String address = null;
        // 打进来的电话
        if (inRing && inHook) {// 表示打进来的电话已经接通过
            address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(incomingNumber);
            inRing = false;
            inHook = false;
            callStateOffHookTime = callStateIdleTime - callStateOffHookTime;// 通话时间，转换成秒
            String time = mPhoneSimpleDateFormat.format(new Date(callStateRingingTime));
            // 打进来的电话有时没号码怎么回事？
            phone = new BlacklistPhone(incomingNumber, address, callStateRingingTime, (int)
                    (callStateOffHookTime /
                            1000), time, 1, 1, 1);
            Log.d(TAG, "flag:1" + " inNumber:" + incomingNumber + " " + address);
        } else if (inRing && !inBlackRing && !inHook) {
            address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(incomingNumber);
            inRing = false;
            String time = mPhoneSimpleDateFormat.format(new Date(callStateRingingTime));
            callStateRingingTime = callStateIdleTime - callStateRingingTime;// 响铃时间
            phone = new BlacklistPhone(incomingNumber, address, callStateRingingTime, (int)
                    (callStateRingingTime /
                            1000), time, 3, 1, 0);
            Log.d(TAG, "flag:0" + " inNumber:" + incomingNumber + " " + address);
        }

        // http://192.168.1.158:8080/UploadFileServer/upload.jsp
        // update blacklist_phone set number='18918366438' where
        // date='1429785760219';
        // 打出去的电话 怎样才能知道打出去的电话没有被接通过？？？
        // 打出去的电话执行过程：
        // 1、先执行广播那里
        // 2、在执行TelephonyManager.CALL_STATE_OFFHOOK（紧接着上一步）
        // 因此outCallTime与outHookTime的数值几乎相等
        // 3、在执行TelephonyManager.CALL_STATE_IDLE（不管打出去的电话有没有接通过）
        outIdleTime = System.currentTimeMillis();
        if (outRing && outHook) {// 拨打出去的电话已经接通过
            address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(outNumber);
            outRing = false;
            outHook = false;
            outHookTime = outIdleTime - outHookTime;
            String time = mPhoneSimpleDateFormat.format(new Date(outCallTime));
            phone = new BlacklistPhone(outNumber, address, outCallTime, (int)
                    (outHookTime / 1000), time, 2, 1, 4);
            Log.d(TAG, "flag:4");
        } else if (outRing && !outHook) {// 拨打出去的电话没有接通（根本执行不到）
            address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(outNumber);
            outRing = false;
            String time = mPhoneSimpleDateFormat.format(new Date(outCallTime));
            outCallTime = outIdleTime - outCallTime;
            phone = new BlacklistPhone(outNumber, address, outCallTime, (int)
                    (outCallTime / 1000), time, 2, 1, 3);
            Log.d(TAG, "flag:3");
        }

        if (phone != null) {
            mBlacklistDao.addBlacklistPhone(phone);
            Intent intent = new Intent();
            intent.setAction("com.aowin.mobilesafe.updateadapter.phone");
            intent.putExtra("phone", phone);
            mContext.sendBroadcast(intent);
            phone = null;
        }

        outNumber = null;
        address = null;
        Log.d(TAG, "CALL_STATE_IDLE---outIdleTime = " + outIdleTime);
    }

    private void callStateRinging(String incomingNumber) {
        BlacklistPhone phone = null;
        String address = null;
        inRing = true;
        inBlackRing = false;
        callStateRingingTime = System.currentTimeMillis();
        Log.d(TAG, "CALL_STATE_RINGING---callStateRingingTime = " + callStateRingingTime);
        // 此号码是黑名单
        if (mBlacklistDao.isNumberExist(incomingNumber)) {
            BlacklistInfo blacklistInfo = mBlacklistDao.query(incomingNumber);
            String mode = blacklistInfo.getMode();
            if ("1".equals(mode) || "3".equals(mode)) {
                inBlackRing = true;
                if (((MyApplication) mContext.getApplicationContext()).getSystemCall() != null) {
                    try {
                        // 静音
                        ((MyApplication) mContext.getApplicationContext())
                                .getSystemCall().silenceRinger();
                        // 挂断
                        ((MyApplication) mContext.getApplicationContext())
                                .getSystemCall().endCall();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                String time = mPhoneSimpleDateFormat.format(new Date(callStateRingingTime));
                address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery
                        (incomingNumber);
                phone = new BlacklistPhone(incomingNumber, address, callStateRingingTime,
                        0, time,
                        1, 1, 2);
                mBlacklistDao.addBlacklistPhone(phone);

                Intent intent = new Intent();
                intent.setAction("com.aowin.mobilesafe.updateadapter.phone");
                intent.putExtra("phone", phone);
                mContext.sendBroadcast(intent);

                // Log.d(TAG, "flag:2");
                // iTelephony.answerRingingCall();
                // iTelephony.setRadio(false);
                // iTelephony.setRadioPower(false);
                // iTelephony.toggleRadioOnOff();
                // iTelephony.updateServiceLocation();
                // iTelephony.setDataEnabled(false);
                // iTelephony.silenceRinger();//静音
                // 虽然被挂断了，但还是要把这次通话给记录下来
            }
        }
        address = null;
        Log.d(TAG, "CALL_STATE_RINGING---callStateRingingTime = " + callStateRingingTime);
    }

    private void callStateOffHook() {
        callStateOffHookTime = System.currentTimeMillis();
        outHookTime = System.currentTimeMillis();
        inHook = true;
        outHook = true;
        Log.d(TAG, "CALL_STATE_OFFHOOK---outHookTime = " + outHookTime);
    }

    private static final String mediaRecorderPath = "/storage/sdcard1/Sounds/";

    // 这个广播接收的消息是从Phone应用中发出的(CallCard:sendBroadcastStartCall(Call.State state))
    private BroadcastReceiver mStartCallBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            Log.d(TAG, "mStartCallBroadcastReceiver onReceive():intent = " + intent);
            vibrate(1000);
            String state = intent.getStringExtra("Call.State");// mPhoneSimpleDateFormat
            if ("ACTIVE".equals(state)) {// 接通
                Log.d(TAG, "mStartCallBroadcastReceiver onReceive():ACTIVE");
                mIsConnectedForCall = true;
                mPhoneNumberForCall = null;
                /*String currentTime = mSimpleDateFormat.format(new Date());
                File mOutputFile = new File(mediaRecorderPath, currentTime + ".amr");// .amr
                mRecorder = new Recorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);// AMR_NB
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                // CHANNEL_CONFIGURATION_MONO为单声道
                mRecorder.setAudioChannels(AudioFormat.CHANNEL_IN_STEREO);
                mRecorder.setAudioSamplingRate(44100);
                mRecorder.setAudioEncodingBitRate(AudioFormat.ENCODING_PCM_16BIT);
                mRecorder.setOutputFile(mOutputFile);
                mRecorder.startRecording();*/
            } else if ("DISCONNECTING".equals(state)) {// 挂断
                Log.d(TAG, "mStartCallBroadcastReceiver onReceive():DISCONNECTING");
                mIsConnectedForCall = false;
                /*mRecorder.stopRecording();
                mRecorder = null;*/
            }
            if ("android.intent.action.STARTING_A_PHONE_CALL".equals(intent.getAction())) {
                if (!mIsConnectedForCall && !TextUtils.isEmpty(mPhoneNumberForCall)) {
                    if (mUiHandler != null) {
                        mUiHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ICallSystemMethod call =
                                        ((MyApplication) mContext.getApplicationContext())
                                                .getSystemCall();
                                if (call != null) {
                                    try {
                                        call.call(mPhoneNumberForCall);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }, 3 * 1000);
                    }
                }
            }
        }
    };


    private long mId;
    private ContentObserver mSystemDatabasesChangedContentObserver =
            new ContentObserver(mUiHandler) {

                @Override
                public void onChange(boolean selfChange, final Uri uri) {
                    super.onChange(selfChange, uri);
                    // onChange():selfChange = false,uri = content://sms/raw
                    // onChange():selfChange = false,uri = content://sms/4861
                    try {
                        Log.d(TAG, "onChange():selfChange = " + selfChange + ",uri = " + uri);
                        String path = uri.getLastPathSegment();
                        Pattern pattern = Pattern.compile("[0-9]+");
                        Matcher matcher = pattern.matcher(path);
                        if (matcher.matches()) {
                            // 数字
                            final long pathId = Long.parseLong(path);
                            if (mId == pathId) {
                                return;
                            }
                            Long id = SimpleDao.getInstance().getLastId(Sms.class);
                            if (id >= pathId) {
                                return;
                            }
                            mId = pathId;
                            ThreadPool.getFixedThreadPool(Constant.FIXEDTHREADPOOLCOUNT).execute(
                                    new Runnable() {

                                        @Override
                                        public void run() {
                                            Cursor cursor = mContext.getContentResolver().query(
                                                    uri, null, null, null, null);
                                            boolean result = SimpleDao.getInstance().copyData(
                                                    Sms.class, cursor);
                                            if (result) {
                                                MyToast.show("备份成功: " + pathId);
                                            } else {
                                                MyToast.show("备份失败: " + pathId);
                                            }
                                            mId = 0;
                                        }
                                    });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

}
