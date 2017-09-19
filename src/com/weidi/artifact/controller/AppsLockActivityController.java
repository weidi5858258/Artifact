package com.weidi.artifact.controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.weidi.artifact.R;
import com.weidi.artifact.activity.AppsLockActivity;
import com.weidi.artifact.activity.CameraActivity;
import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.basecontroller.BaseActivityController;
import com.weidi.artifact.service.CoreService;
import com.weidi.callsystemmethod.ICallSystemMethod;
import com.weidi.eventbus.EventBus;
import com.weidi.log.Log;
import com.weidi.utils.MyUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by root on 17-1-13.
 */
//┏┓　　　┏┓
//┏┛┻━━━┛┻┓
//┃　　　　　　　┃
//┃　　　━　　　┃
//┃　┳┛　┗┳　┃
//┃　　　　　　　┃
//┃　　　┻　　　┃
//┃　　　　　　　┃
//┗━┓　　　┏━┛
//┃　　　┃  神兽保佑
//┃　　　┃  代码无BUG！
//┃　　　┗━━━┓
//┃　　　　　　　┣┓
//┃　　　　　　　┏┛
//┗┓┓┏━┳┓┏┛
// ┃┫┫　┃┫┫
//

/**
 * 登录系统用多长时间,到了那个时间点,就把系统给锁了
 * 首先要保证自己的服务绝对不能被杀死,万一杀死了,马上就要起来
 * 还要保证自己的应用不能被卸载(对安装应用进行控制)
 */

public class AppsLockActivityController extends BaseActivityController {

    private static final String TAG = "AppsLockActivityController";
    private static final boolean DBG = false;
    private AppsLockActivity mAppsLockActivity;
    private PackageManager mPackageManager;
    private InputMethodManager mInputMethodManager;
    private SharedPreferences mSharedPreferences;
    private String mPackageName;
    private int mScreenOffTime;
    private MyCountDownTimer mMyCountDownTimer;
    private int mPasswordErrorCount = Constant.PASSWORDERRORCOUNT;
    private static int[] mInterceptKeyCode = new int[]{
            //            KeyEvent.KEYCODE_POWER,
            KeyEvent.KEYCODE_BACK,
            KeyEvent.KEYCODE_HOME,
            KeyEvent.KEYCODE_MENU,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN
    };

    public AppsLockActivityController(Activity activity) {
        super(activity);
        mAppsLockActivity = (AppsLockActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DBG) Log.d(TAG, "onCreate():savedInstanceState = " + savedInstanceState);

        init();

        // 下面三句一起的效果是默认得到焦点
        /*mAppsLockActivity.appslock_password_et.setFocusable(true);
        mAppsLockActivity.appslock_password_et.setFocusableInTouchMode(true);
        mAppsLockActivity.appslock_password_et.requestFocus();
        //延迟弹出键盘 必须要有上面三句代码的支持（已经测试过了）
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager =
                        (InputMethodManager) mContext.getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(mAppsLockActivity.appslock_password_et, 0);
            }
        }, 100);*/
    }

    @Override
    public void onStart() {
        if (DBG) Log.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        if (DBG) Log.d(TAG, "onResume()");
        // 必须要放在这里
        sendBroadcastToInputManagerService(mContext, true);

        boolean isStartedUsbDebug = mSharedPreferences.getBoolean(
                Constant.USB_DEBUG, true);
        if (!isStartedUsbDebug) {
            if (((MyApplication) mAppsLockActivity.getApplication()).getSystemCall() != null) {
                try {
                    // 关闭USB调试
                    ((MyApplication) mAppsLockActivity.getApplication())
                            .getSystemCall().usbDebug(false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        boolean hadOpenSdCardAndUsbDisk = mSharedPreferences.getBoolean(
                Constant.SDCARD_USBDISK, true);
        if (!hadOpenSdCardAndUsbDisk) {
            if (((MyApplication) mAppsLockActivity.getApplication()).getSystemCall() != null) {
                try {
                    ((MyApplication) mAppsLockActivity.getApplication())
                            .getSystemCall().unmountVolume(Constant.SDCARD1, false, false);
                    ((MyApplication) mAppsLockActivity.getApplication())
                            .getSystemCall().unmountVolume(Constant.USBDISK0, false, false);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            sendBroadcastToMountService(mContext, true);
        }
    }

    @Override
    public void onPause() {
        if (DBG) Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        if (DBG) Log.d(TAG, "onStop()");
    }

    @Override
    public void onDestroy() {
        if (DBG) Log.d(TAG, "onDestroy()");
        EventBus.getDefault().unregister(mAppsLockActivity);
        // 开启核心服务
        if (!MyUtils.isSpecificServiceAlive(
                mContext,
                Constant.CLASS_CORESERVICE)) {
            Intent intent = new Intent(mAppsLockActivity, CoreService.class);
            mAppsLockActivity.startService(intent);
        }
        if (mMyCountDownTimer != null) {
            mMyCountDownTimer.cancel();
            mMyCountDownTimer = null;
        }
    }

    public void onNewIntent(Intent intent) {

    }

    // 事件已经被我在InputManagerService中拦截掉了
    public boolean dispatchKeyEvent(KeyEvent event) {
        // repeatCount == 0按一下时,长按不等于0
        int keyCode = event.getKeyCode();
        int repeatCount = event.getRepeatCount();
        if (keyCode == KeyEvent.KEYCODE_BACK
                || keyCode == KeyEvent.KEYCODE_MENU
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP
                || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //具体的操作代码
            return true;
        }
        return false;
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_tv:
                MyUtils.hideKeyboard(mAppsLockActivity);
                Intent intent = new Intent();
                intent.setAction("android.intent.action.MAIN");
                intent.addCategory("android.intent.category.HOME");
                intent.addCategory("android.intent.category.DEFAULT");
                intent.addCategory("android.intent.category.MONKEY");
                mAppsLockActivity.startActivity(intent);
                mAppsLockActivity.finish();
                mAppsLockActivity.exitActivity();
                break;
            case R.id.appslock_entry_ibtn:
                String password = mAppsLockActivity.appslock_password_et
                        .getText().toString().trim();
                if (!TextUtils.isEmpty(password) && password.length() <= 100) {
                    String pw = mSharedPreferences.getString(Constant.APPSLOCK_PASSWORD, "");
                    try {
                        if (pw.equals(MyUtils.md5AddKey(password))) {
                            passworkOK();

                        } else {
                            --mPasswordErrorCount;
                            if (mPasswordErrorCount < 1) {
                                passwordErrorCountOverTopDoSomething();
                                return;
                            }
                            if (mPasswordErrorCount == 1) {
                                showPasswordErrorInfo(
                                        "下一次再输错密码,就要被锁1小时,好自为之.");
                                return;
                            }
                            showPasswordErrorInfo(
                                    "密码错误,您还有 " + mPasswordErrorCount + " 次输入机会.");

                        }
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void onEvent(int what, Object object) {
        switch (what) {
            case Constant.SCREEN_OFF:
                if (DBG) Log.d(TAG, "SCREEN_OFF");
                sendBroadcastToInputManagerService(mContext, false);
                break;

            case Constant.UNLOCK_MY_PHONE_TAG:
                passworkOK();
                break;

            default:
        }
    }

    private void init() {
        /***
         需要做的事有：
         1.应用是否可以被安装
         2.应用是否可以被卸载
         3.是否允许USB调试
         4.外置存储卡是否可用
         */

        // 全屏
        mAppsLockActivity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EventBus.getDefault().register(mAppsLockActivity);

        mPackageManager = mContext.getPackageManager();
        mInputMethodManager = (InputMethodManager) mContext.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        mSharedPreferences = mContext.getSharedPreferences(
                Constant.APP_CONFIG, Context.MODE_PRIVATE);
        mPackageName = mAppsLockActivity.getIntent().getStringExtra(Constant.APP_PACKAGE_NAME);

        mSharedPreferences.edit().putInt(Constant.SAFE_EXIT, 1).commit();

        setScreenOffTime(30 * 1000);

        try {
            ApplicationInfo info = mPackageManager.getApplicationInfo(mPackageName, 0);
            String appName = info.loadLabel(mPackageManager).toString();
            Drawable icon = mPackageManager.getApplicationIcon(mPackageName);
            mAppsLockActivity.appslock_icon_iv.setImageDrawable(icon);
            mAppsLockActivity.appslock_appname_tv.setText(appName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mAppsLockActivity.backdoor_rlayout.setOnTouchListener(mOnTouchListener);
        mAppsLockActivity.password_layout.setOnTouchListener(mOnTouchListener);

        EventBus.getDefault().post(Constant.HIDEGESTUREVIEW, null);
        EventBus.getDefault().post(Constant.ILLEGALUNLOCK_ENTER, null);
    }

    /**
     * 统一一下，false表示不允许做的事，true表示允许做的事
     *
     * com.android.server.input.InputManagerService
     * true表示拦截
     * false表示不拦截
     *
     * @param isIntercept
     */
    public static void sendBroadcastToInputManagerService(Context context, boolean isIntercept) {
        Intent intent = new Intent();
        intent.setAction(Constant.INPUTMANAGERSERVICE);
        intent.putExtra(Constant.INTERCEPTKEYCODE, mInterceptKeyCode);
        if (isIntercept) {
            intent.putExtra(Constant.ISINTERCEPT, true);
        } else {
            intent.putExtra(Constant.ISINTERCEPT, false);
        }
        context.sendBroadcastAsUser(intent, UserHandle.OWNER);
    }

    /**
     * com.android.server.MountService
     * true表示拦截
     * false表示不拦截
     *
     * @param isIntercept
     */
    public static void sendBroadcastToMountService(Context context, boolean isIntercept) {
        Intent intent = new Intent();
        intent.setAction(Constant.MOUNTSERVICE);
        if (isIntercept) {
            intent.putExtra(Constant.ISINTERCEPT, true);
        } else {
            intent.putExtra(Constant.ISINTERCEPT, false);
        }
        context.sendBroadcastAsUser(intent, UserHandle.OWNER);
    }

    public static void sendBroadcastToPackageManagerService(Context context, boolean isIntercept) {
        Intent intent = new Intent();
        intent.setAction(Constant.PACKAGEMANAGERSERVICE);
        if (isIntercept) {
            // 允许安装,卸载
            intent.putExtra(Constant.ISINTERCEPTINSTALL, true);
            intent.putExtra(Constant.ISINTERCEPTUNINSTALL, true);
        } else {
            // 不允许安装,卸载
            intent.putExtra(Constant.ISINTERCEPTINSTALL, false);
            intent.putExtra(Constant.ISINTERCEPTUNINSTALL, false);
        }
        context.sendBroadcastAsUser(intent, UserHandle.OWNER);
    }

    // com.android.server.power.PowerManagerService
    public static void sendBroadcastToPowerManagerService(Context context, boolean isIntercept) {
        Intent intent = new Intent();
        intent.setAction(Constant.POWERMANAGERSERVICE);
        if (isIntercept) {
            intent.putExtra(Constant.ISINTERCEPT, true);
        } else {
            intent.putExtra(Constant.ISINTERCEPT, false);
        }
        context.sendBroadcastAsUser(intent, UserHandle.OWNER);
    }

    private void startSelf() {
        mAppsLockActivity.finish();
        Intent mIntent = new Intent(mContext, AppsLockActivity.class);
        // 服务中开启Activity必须要加上这句
        mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mIntent.putExtra(Constant.APP_PACKAGE_NAME, mPackageName);
        mAppsLockActivity.startActivity(mIntent);
    }

    /**
     *
     */
    private void passworkOK() {
        mAppsLockActivity.appslock_entry_ibtn.setEnabled(false);

        sendBroadcastToInputManagerService(mContext, false);
        sendBroadcastToMountService(mContext, false);
        sendBroadcastToPackageManagerService(mContext, true);

        mSharedPreferences.edit().putBoolean(Constant.USB_DEBUG, true).commit();
        mSharedPreferences.edit().putBoolean(Constant.SDCARD_USBDISK, true).commit();
        mSharedPreferences.edit().putInt(Constant.SAFE_EXIT, 0).commit();

        ICallSystemMethod iCallSystemMethod = ((MyApplication) mAppsLockActivity.getApplication())
                .getSystemCall();
        if (iCallSystemMethod != null) {
            try {
                // 开启USB调试
                iCallSystemMethod.usbDebug(true);
                // 挂载SD卡,USB存储设备
                iCallSystemMethod.mountVolume(Constant.SDCARD1);
                iCallSystemMethod.mountVolume(Constant.USBDISK0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        MyUtils.hideKeyboard(mAppsLockActivity);

        setScreenOffTime(120 * 1000);

        // 密码正确通知一下AppsLockService
        EventBus.getDefault().post(Constant.PASSWORD_CORRECT, mPackageName);

        EventBus.getDefault().post(Constant.SHOWGESTUREVIEW, null);

        EventBus.getDefault().post(Constant.ILLEGALUNLOCK_EXIT, null);

        mAppsLockActivity.finish();
        mAppsLockActivity.exitActivity();
    }

    private void showPasswordErrorInfo(String msg) {
        mAppsLockActivity.appslock_password_error_count_tv.setVisibility(View.VISIBLE);
        mAppsLockActivity.appslock_password_error_count_tv.setText(msg);
    }

    private void passwordErrorCountOverTopDoSomething() {
        mAppsLockActivity.password_layout.setOnTouchListener(null);
        MyUtils.hideKeyboard(mAppsLockActivity);
        mAppsLockActivity.appslock_password_et.setText("");
        mAppsLockActivity.appslock_entry_ibtn.setEnabled(false);
        mAppsLockActivity.appslock_password_et.setFocusable(false);
        mAppsLockActivity.appslock_password_et.setFocusableInTouchMode(false);
        if (mMyCountDownTimer == null) {
            mMyCountDownTimer = new MyCountDownTimer(3600 * 1000, 1000);
        }
        mMyCountDownTimer.start();

        Intent cameraActivity = new Intent(mAppsLockActivity, CameraActivity.class);
        mAppsLockActivity.startActivity(cameraActivity);
    }

    /**
     * 获得锁屏时间  毫秒
     */
    private int getScreenOffTime() {
        int screenOffTime = 0;
        try {
            screenOffTime = Settings.System.getInt(
                    mContext.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Exception localException) {
        }
        return screenOffTime;
    }

    /**
     * 设置背光时间  毫秒
     */
    private void setScreenOffTime(int paramInt) {
        try {
            Settings.System.putInt(
                    mContext.getContentResolver(),
                    Settings.System.SCREEN_OFF_TIMEOUT,
                    paramInt);
        } catch (Exception localException) {
        }
    }

    /*long[] mHits = new long[2];
    private void click2(View view) {
        //每点击一次 实现左移一格数据
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //给数组的最后赋当前时钟值
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        //当0出的值大于当前时间-500时  证明在500秒内点击了2次
        if (mHits[0] > SystemClock.uptimeMillis() - 500) {

        }
    }*/

    // 设置一个3位数组。需要点击几次，就设置一个几位的数组
    long[] mHits100 = new long[100];

    private void click100() {
        // 每点击一次 实现左移一格数据
        // 复制数组的元素从第1个位置开始，目标地址是第0个位置，复制的长度为数组长度-1
        System.arraycopy(mHits100, 1, mHits100, 0, mHits100.length - 1);
        // 给数组的最后赋当前时钟值
        // 给数组最后一个位置赋值
        mHits100[mHits100.length - 1] = SystemClock.uptimeMillis();
        // 当0出的值大于当前时间-500时  证明在500秒内点击了2次
        // 判断数组第一个位置的时间与当前时间的差是否小于500毫秒，假如小于的话，就认为是多次点击事件。
        // 在15秒内点完50次
        if (mHits100[0] > SystemClock.uptimeMillis() - 25000) {
            mAppsLockActivity.backdoor_rlayout.setOnTouchListener(null);
            passworkOK();
            int count = mHits100.length;
            for (int i = 0; i < count; i++) {
                mHits100[i] = 0;
            }
            mHits100 = null;
        }
    }

    private void showSoftInputFromWindow() {
        mAppsLockActivity.appslock_password_et.setFocusable(true);
        mAppsLockActivity.appslock_password_et.setFocusableInTouchMode(true);
        mAppsLockActivity.appslock_password_et.requestFocus();
        //延迟弹出键盘 必须要有上面三句代码的支持（已经测试过了）
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mInputMethodManager.showSoftInput(
                        mAppsLockActivity.appslock_password_et, 0);
            }
        }, 100);
    }

    /**
     * onTouch():view = android.widget.LinearLayout
     * {4184f338 V.E..... ........ 0,0-720,1280 #7f090007 app:id/password_layout}
     * event = MotionEvent
     * { action=ACTION_DOWN, id[0]=0, x[0]=308.0, y[0]=1016.0,
     * toolType[0]=TOOL_TYPE_FINGER, buttonState=0, metaState=0,
     * flags=0x0, edgeFlags=0x0, pointerCount=1, historySize=0,
     * eventTime=46865415, downTime=46865415, deviceId=14, source=0x1002 }
     */
    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //            Log.d(TAG, "onTouch():view = " + v + " event = " + event);

            if (v instanceof android.widget.LinearLayout
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                if (mInputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0)) {
                    // 软键盘已弹出则隐藏

                } else {
                    // 软键盘未弹出则弹出
                    showSoftInputFromWindow();

                }
                return true;

            } else if (v instanceof android.widget.RelativeLayout
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                click100();
                return true;
            }
            return false;
        }

    };

    /**
     * 重写 父类的方法 onTick() 、 onFinish()
     */

    private class MyCountDownTimer extends CountDownTimer {

        /**
         * @param millisInFuture    表示以毫秒为单位 倒计时的总数
         *                          <p>
         *                          例如 millisInFuture=1000 表示1秒
         * @param countDownInterval 表示 间隔 多少毫秒 调用一次 onTick 方法
         *                          <p>
         *                          例如: countDownInterval =1000 ; 表示每1000毫秒调用一次onTick()
         */
        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            mAppsLockActivity.password_layout.setOnTouchListener(mOnTouchListener);
            mPasswordErrorCount = Constant.PASSWORDERRORCOUNT;
            mAppsLockActivity.appslock_entry_ibtn.setEnabled(true);
            showSoftInputFromWindow();
            showPasswordErrorInfo("再接再励");
        }

        @Override
        public void onTick(long millisUntilFinished) {
            showPasswordErrorInfo("倒计时: " + (millisUntilFinished / 1000) + " 秒");
        }

    }

    //*********************下面的代码不用,作为以后的参考代码*********************//

    // 现在事件已经被我在InputManagerService中拦截掉了,因此不会再收到相应的广播了
    private static class HomeWatcherReceiver extends BroadcastReceiver {

        private static final String LOG_TAG = "HomeReceiver";
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
        private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

        //        IntentFilter mIntentFilter = new IntentFilter();
        //        mIntentFilter.setPriority(2147483647);
        //        mIntentFilter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        //        mAppsLockActivity.registerReceiver(mHomeWatcherReceiver, intentFilter);

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            Log.i(LOG_TAG, "onReceive():intent: " + intent);
            String action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                // android.intent.action.CLOSE_SYSTEM_DIALOGS
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);

                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    // 短按Home键
                    Log.i(LOG_TAG, "Home");

                } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                    // startSelf();
                    // 长按Menu键 或者 activity切换键
                    Log.i(LOG_TAG, "long press Menu key or switch Activity");
                }
                // 下面两个接收不到
                else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {

                    // 锁屏
                    Log.i(LOG_TAG, "lock");
                } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {

                    // samsung 长按Home键
                    Log.i(LOG_TAG, "assist");
                }

            }
        }

    }

}
