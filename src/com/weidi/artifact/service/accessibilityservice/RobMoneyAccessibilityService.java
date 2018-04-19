package com.weidi.artifact.service.accessibilityservice;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.modle.Event;
import com.weidi.dbutil.SimpleDao;
import com.weidi.log.MLog;
import com.weidi.threadpool.ThreadPool;
import com.weidi.utils.EventBusUtils;
import com.weidi.utils.MyToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ex-wangliwei on 2015/12/23.
 */
public class RobMoneyAccessibilityService extends AccessibilityService {

    private static final String TAG = "RobMoneyAccessibilityService";
    private SimpleDateFormat mSimpleDateFormat =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
    private static List<AccessibilityNodeInfo> mAccessibilityNodeInfoList =
            new ArrayList<AccessibilityNodeInfo>();
    private static int hongBaoCount = 0;
    private boolean mIsScreenOn = true;
    private boolean mIsHongBaoInfo = false;

    private ActivityManager mActivityManager;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private KeyguardManager mKeyguardManager;
    private KeyguardManager.KeyguardLock mKeyguardLock;
    private SensorManager mSensorManager;
    private DevicePolicyManager mDevicePolicyManager;
    private Vibrator mVibretor;// 振动

    private int timeOut = 1000 * 45;
    private boolean timeOutFlag = true;
    private boolean startThread = false;

    @Override
    public void onServiceConnected() {
        super.onServiceConnected();
        init();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                MLog.d(TAG, "AccessibilityEvent.TYPE_ANNOUNCEMENT");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                MLog.d(TAG, "AccessibilityEvent.TYPE_GESTURE_DETECTION_END");
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                MLog.d(TAG, "AccessibilityEvent.TYPE_GESTURE_DETECTION_START");
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                MLog.d(TAG, "AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END");
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                MLog.d(TAG, "AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START");
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                MLog.d(TAG, "AccessibilityEvent.TYPE_TOUCH_INTERACTION_END");
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                MLog.d(TAG, "AccessibilityEvent.TYPE_TOUCH_INTERACTION_START");
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                MLog.d(TAG, "AccessibilityEvent.TYPE_VIEW_CLICKED");
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                MLog.d(TAG, "AccessibilityEvent.TYPE_VIEW_LONG_CLICKED");
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                MLog.d(TAG, "AccessibilityEvent.TYPE_VIEW_SELECTED");
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                MLog.d(TAG, "AccessibilityEvent.TYPE_VIEW_FOCUSED");
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                MLog.d(TAG, "AccessibilityEvent.TYPE_ANNOUNCEMENT");
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                MLog.d(TAG, "AccessibilityEvent.TYPE_VIEW_HOVER_ENTER");
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                MLog.d(TAG, "AccessibilityEvent.TYPE_VIEW_HOVER_EXIT");
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                MLog.d(TAG, "AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                MLog.d(TAG, "AccessibilityEvent.TYPE_VIEW_SCROLLED");
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                MLog.d(TAG, "AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                MLog.d(TAG, "AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED");
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
                MLog.d(TAG, "AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED");
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                MLog.d(TAG, "AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY");
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                // 通知栏事件
                type_notification_state_changed(event);
                break;

            // 能不能监听到键盘弹出或者隐藏的这种状态
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                type_window_state_changed(event);
                break;
            
            /*case AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT:
                MLog.d(TAG,"AccessibilityEvent.TYPE_ASSIST_READING_CONTEXT");
                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                MLog.d(TAG,"AccessibilityEvent.TYPE_WINDOWS_CHANGED");
                break;
            case AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED:
                MLog.d(TAG,"AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED");
                break;*/

            default:
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public boolean onUnbind(Intent intent) {
        EventBusUtils.unregister(this);
        return super.onUnbind(intent);
    }

    public Object onEvent(int what, Object object) {
        switch (what) {
            case Constant.SCREEN_ON:
                mIsScreenOn = true;
                break;

            case Constant.SCREEN_OFF:
                mIsScreenOn = false;
                break;

            default:
        }
        return what;
    }

    private void init() {
        mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        mVibretor = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        mDevicePolicyManager = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //                onSensorChangedWeidi(event);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        EventBusUtils.register(this);

        MLog.d(TAG, "成功连接上AccessibilityService服务");
    }

    private void type_notification_state_changed(AccessibilityEvent event) {
        MLog.d(TAG, "有通知来了...");
        List<CharSequence> texts = event.getText();
        if (texts == null || texts.isEmpty()) {
            return;
        }
        /**
         event.getBeforeText() = null
         event.getPackageName() = com.tencent.mm
         event.getClassName() = android.app.Notification
         张    丹: 可口可乐
         */
        MLog.d(TAG, "event.getBeforeText() = " + event.getBeforeText());
        MLog.d(TAG, "event.getPackageName() = " + event.getPackageName());
        MLog.d(TAG, "event.getClassName() = " + event.getClassName());
        for (CharSequence text : texts) {
            String content = text.toString();
            MLog.d(TAG, content);
            if (content.contains("[微信红包]")) {
                if (!mIsScreenOn) {
                    openScreen();
                }
                vibrate(500);
                // 监听到微信红包的notification，打开通知
                if (event.getParcelableData() != null
                        && event.getParcelableData() instanceof Notification) {
                    Notification notification = (Notification) event.getParcelableData();
                    PendingIntent pendingIntent = notification.contentIntent;
                    try {
                        pendingIntent.send();
                        startThread = true;
                        mIsHongBaoInfo = true;
                        hongBaoCount++;
                        MLog.d(TAG, "红包到来...");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            }
        }
    }

    /**
     * 每打开一个Activity都会回调
     *
     * @param event
     */
    private void type_window_state_changed(AccessibilityEvent event) {
        String className = event.getClassName().toString();
        MLog.d(TAG, className);
        if ("com.tencent.mm.ui.LauncherUI".equals(className)) {

            if (mIsHongBaoInfo) {
                getPacket();// 领取红包
            }

        } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI".equals(className)
                || "com.tencent.mm.ui.base.o".equals(className)) {

            openPacket();// 打开红包

        } else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(className)) {

            goBack();

        }

        String currentTime = mSimpleDateFormat.format(new Date());
        Event mEvent = new Event();
        mEvent.time = currentTime;
        mEvent.event1 = className;
        SimpleDao.getInstance().add(Event.class, mEvent);
    }

    private void onSensorChangedWeidi(SensorEvent event) {
        int sensorType = event.sensor.getType();
        //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;

        float x = values[0];
        float y = values[1];
        float z = values[2];

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            int value = 15;//摇一摇阀值,不同手机能达到的最大值不同,如某品牌手机只能达到20
            if (x >= value || x <= -value || y >= value || y <= -value || z >= value || z <=
                    -value) {
                if (mIsScreenOn) {
                    if (mDevicePolicyManager != null) {
                        vibrate(20);
                        mDevicePolicyManager.lockNow();// 锁屏
                    }
                }
            }
        }
    }

    private void vibrate(long milliseconds) {
        if (mVibretor != null) {
            mVibretor.vibrate(milliseconds);
        }
    }

    private ComponentName mComponentName;

    private void goHomeRunnable() {
        int count = 0;
        while (timeOutFlag) {
            SystemClock.sleep(1000);
            mComponentName = mActivityManager.getRunningTasks(1).get(0).topActivity;
            if (mComponentName != null
                    && "com.tencent.mm.ui.LauncherUI".equals(mComponentName.getClassName())) {
                count++;
            } else {
                count = 0;
            }
            if (count >= 10) {
                timeOutFlag = false;
                mIsHongBaoInfo = false;
                goHome();
                return;
            }
        }
    }

    private static Rect boundsInParent = new Rect();
    private static Rect boundsInScreen = new Rect();
    private static int childCount = 0;

    private boolean flag = false;

    private void getPacket() {
        MLog.d(TAG, "开始抢红包啦");
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) {
            MLog.d(TAG, "AccessibilityNodeInfo对象为null");
            return;
        }

        childCount = rootNode.getChildCount();
        MLog.d(TAG, "rootNode.getChildCount() = " + childCount);

        if (childCount == 6) {
            flag = true;
            for (int i = rootNode.getChildCount() - 1; i > 0; i--) {
                AccessibilityNodeInfo child = rootNode.getChild(i);
                if (child == null) {
                    continue;
                }
                recycle(child);
            }
        } else if (childCount == 7) {
            flag = false;
            if (mIsHongBaoInfo) {// false startThread
                timeOutFlag = true;
                startThread = false;
                ThreadPool.getCachedThreadPool().execute(new Runnable() {

                    @Override
                    public void run() {
                        goHomeRunnable();
                    }
                });
            }

            //            for(int i=0;i<rootNode.getChildCount();i++){
            //                recycle(rootNode.getChild(i));
            //            }

            AccessibilityNodeInfo child = rootNode.getChild(1);
            if (child == null) {
                return;
            }
            MLog.d(TAG, child.toString() + " " + child.getChildCount());
            recycle(child);

            //            AccessibilityNodeInfo child = null;
            //            for (int i = rootNode.getChildCount() - 1; i > 0; i--) {
            //                child = rootNode.getChild(i);
            //                if (boundsInParent == null) {
            //                    boundsInParent = new Rect();
            //                }
            //                if (boundsInScreen == null) {
            //                    boundsInScreen = new Rect();
            //                }
            //                child.getBoundsInParent(boundsInParent);
            //                child.getBoundsInScreen(boundsInScreen);
            ////              不同的机器可能判断的参数也不一样
            //                if (boundsInParent.right == 720 &&
            //                        boundsInParent.bottom == 1134 &&
            //                        boundsInScreen.top == 146 &&
            //                        boundsInScreen.right == 720 &&
            //                        boundsInScreen.bottom == 1280) {
            //                    MLog.d(TAG,"-" + child.toString() + " " +
            // child.getChildCount() + " i = " + i);
            //                    recycle(child);
            //                }
            //                MLog.d(TAG,"=========>" + child.toString() + " " + child
            // .getChildCount());
            //                recycle(child);
            //            }

            if (mAccessibilityNodeInfoList != null && mAccessibilityNodeInfoList.size() > 0) {
                for (AccessibilityNodeInfo info : mAccessibilityNodeInfoList) {
                    onClick(info);
                    //                    if (hongBaoCount > 0) {
                    //                        onClick(info);
                    //                    }else {
                    //                    }
                    if (hongBaoCount == 0) {
                        if (mIsHongBaoInfo) {
                            goHome();
                        }
                        timeOutFlag = false;
                        mIsHongBaoInfo = false;
                        return;
                    }
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void recycle(AccessibilityNodeInfo rootNode) {
        if (rootNode.getChildCount() == 0) {
            doSomething(rootNode);
        } else {
            // 循环找出有多少个红包
            for (int i = 0; i < rootNode.getChildCount(); i++) {
                if (rootNode.getChild(i) != null) {
                    recycle(rootNode.getChild(i));
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void doSomething(AccessibilityNodeInfo rootNode) {
        if (childCount == 6) {

            if (rootNode.getContentDescription() == null) {
                return;
            }
            String contentDescription = rootNode.getContentDescription().toString();
            if (contentDescription == null) {
                return;
            }
            if (contentDescription.contains("微信红包")) {
                rootNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                startThread = true;
                getPacket();
            }

        } else if (childCount == 7) {

            if (boundsInParent == null) {
                boundsInParent = new Rect();
            }

            //            if(rootNode.isClickable()){
            //                MLog.d(TAG,"----------------" + rootNode.toString());
            //                if (!mAccessibilityNodeInfoList.contains(rootNode)) {
            //                    mAccessibilityNodeInfoList.add(rootNode);
            //                }
            //            }

            rootNode.getBoundsInParent(boundsInParent);
            if (boundsInParent.right == 446 && boundsInParent.bottom == 202) {
                // 这块内容要着重研究
                //                if (rootNode.isClickable()) {}
                if (!mAccessibilityNodeInfoList.contains(rootNode)) {
                    mAccessibilityNodeInfoList.add(rootNode);
                }
            } else {
                if (rootNode.getParent() != null) {
                    //                    MLog.d(TAG,"----"+rootNode.toString());
                    //                    MLog.d(TAG,"----------------"+rootNode
                    // .getParent().toString());
                    doSomething(rootNode.getParent());
                }
            }

        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void onClick(AccessibilityNodeInfo nodeInfo) {
        if (mIsHongBaoInfo) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void setHongBaoCount() {
        if (hongBaoCount > 0) {
            hongBaoCount--;
        } else {
            hongBaoCount = 0;
        }
    }

    @SuppressLint("NewApi")
    private void openPacket() {
        try {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                return;
            }
            List<AccessibilityNodeInfo> list2 = nodeInfo.findAccessibilityNodeInfosByText("红包派完了");
            if (list2 != null && list2.size() == 1) {
                MyToast.show("手慢了，红包派完了");
                setHongBaoCount();
                goBack();
                list2 = null;
                return;
            }
            List<AccessibilityNodeInfo> list3 = nodeInfo.findAccessibilityNodeInfosByText("红包已失效");
            if (list3 != null && list3.size() == 1) {
                MyToast.show("超过1天未领取，红包已失效");
                setHongBaoCount();
                goBack();
                list3 = null;
                return;
            }
            if (nodeInfo.getChildCount() < 4) {
                return;
            }
            AccessibilityNodeInfo clickedNode = nodeInfo.getChild(3);
            if (clickedNode == null) {
                return;
            }
            MyToast.show("恭喜，抢到一个红包");
            setHongBaoCount();
            clickedNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void goBack() {
        try {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void goHome() {
        try {
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openScreen() {
        // 取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        mWakeLock = mPowerManager.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP
                        | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        mKeyguardLock = mKeyguardManager.newKeyguardLock("unLock");
        if (mWakeLock != null) {
            // 点亮屏幕
            mWakeLock.acquire();
        }
        if (mKeyguardLock != null) {
            // 解锁
            mKeyguardLock.disableKeyguard();
        }
    }


    // 只是保存一份代码
    @SuppressLint("NewApi")
    private void openPacket1() {
        try {
            AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
            if (nodeInfo == null) {
                return;
            }

            // boundsInParent: Rect(0, 0 - 180, 180); boundsInScreen: Rect(270, 700 - 450, 880);
            //            int count = nodeInfo.getChildCount();// count = 5
            //            for(int i=0;i<count;i++){
            //                AccessibilityNodeInfo node = nodeInfo.getChild(i);
            //                MLog.d(TAG,"--------- i = "+i+" "+node.toString());
            //            }

            if (nodeInfo.getChildCount() < 4) {
                return;
            }
            AccessibilityNodeInfo clickedNode = nodeInfo.getChild(3);
            if (clickedNode != null) {
                MLog.d(TAG, "恭喜，抢到一个红包");
                setHongBaoCount();
                clickedNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return;
            }


            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("拆红包");
            List<AccessibilityNodeInfo> list2 = nodeInfo.findAccessibilityNodeInfosByText("红包派完了");
            List<AccessibilityNodeInfo> list3 = nodeInfo.findAccessibilityNodeInfosByText("红包已失效");
            if (list == null || list2 == null || list3 == null) {
                MLog.d(TAG, list == null ? "list = null" : "list != null");
                MLog.d(TAG, list2 == null ? "list2 = null" : "list2 != null");
                MLog.d(TAG, list3 == null ? "list3 = null" : "list3 != null");
                return;
            }
            if (list.size() == 1) {
                MLog.d(TAG, "恭喜，抢到一个红包");
                setHongBaoCount();
                list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else if (list2.size() == 1) {
                MLog.d(TAG, "手慢了，红包派完了");
                setHongBaoCount();
                goBack();
                list2 = null;
            } else if (list3.size() == 1) {
                MLog.d(TAG, "超过1天未领取，红包已失效");
                setHongBaoCount();
                goBack();
                list3 = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
