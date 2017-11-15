package com.weidi.artifact.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.weidi.artifact.activity.AppsLockActivity;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.modle.Event;
import com.weidi.artifact.service.AppsLockService;
import com.weidi.artifact.service.CoreService;
import com.weidi.artifact.service.PeriodicalSerialKillerService;
import com.weidi.dbutil.SimpleDao;
import com.weidi.utils.MyUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 权限<uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 * <p>
 * 安装应用到你的手机后，启动一次该应用，那么下次开机就能收到BOOT_COMPLETED广播。
 * <p>
 * 做了个测试：到设置——>应用程序，找到刚才安装的应用，点击“强行停止”，
 * 那么重启手机后，就收不到BOOT_COMPLETED广播了。
 * 如果该应用被有些三方安全软件强制杀掉进程后，
 * 重启手机也会收不到BOOT_COMPLETED广播。
 */
//开机完成的系统广播
public class BootCompleteBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "BootCompleteBroadcastReceiver";
    private SimpleDateFormat mSimpleDateFormat =
            new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

    @Override
    public void onReceive(Context context, Intent intent) {
        // Log.d(TAG, "onReceive():context = " + context + " intent = " + intent);
        TelephonyManager telphonyManager = (TelephonyManager) context.getSystemService(
                Context.TELEPHONY_SERVICE);
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constant.APP_CONFIG, context.MODE_PRIVATE);
        // 在SecurityPhoneSetup2Activity
        String old_sim = sharedPreferences.getString(Constant.SIM, null);
        // 在java中设置的
        String new_sim = telphonyManager.getLine1Number();
        // Line1Number = null Line1Number = +8618565603244
        // 手机真正重启完成的话,Line1Number = +8618565603244
        // Log.d(TAG, "onReceive():Line1Number = " + new_sim);
        int state = telphonyManager.getSimState();
        // Log.d(TAG, "onReceive():state = " + state);// state = 0 state = 1 state = 5
        // 手机真正重启完成的话,state = 5
        //如果手机丢了，别人拿去用了，但是别人不插入sim卡，他只是想在有网络的时候可以上网就行了。
        //那么，此时得不到这个人的手机号。只有等这人联上网络后，把他的地理位置发送给我。
        //还有一个问题：别人安装了这款软件后，没有运行，因此也没有绑定SIM卡，然后手机重启了，如果没有相应的处理，则可能会抛异常。
        if (state != 1) {//如果别人拿到手机后开启了“飞行模式”就没用了
            if (!TextUtils.isEmpty(old_sim)
                    && !TextUtils.isEmpty(new_sim)
                    && new_sim.equals(old_sim)) {
                // MyUtils.showToast(context, "sim卡没有更换", 1);
            } else {
                // MyUtils.showToast(context, "sim已经更换", 1);
                protectMyPhone(context, sharedPreferences);
            }
        } else {
            // MyUtils.showToast(context, "没有插卡", 1);
            protectMyPhone(context, sharedPreferences);
        }

        // 开启核心服务
        /*if (!MyUtils.isSpecificServiceAlive(
                context,
                Constant.CLASS_CORESERVICE)) {
            intent = new Intent(context, CoreService.class);
            context.startService(intent);
        }

        if (!MyUtils.isSpecificServiceAlive(
                context,
                Constant.CLASS_PERIODICALSERIALKILLERSERVICE)) {
            intent = new Intent(context, PeriodicalSerialKillerService.class);
            context.startService(intent);
        }

        if (!MyUtils.isSpecificServiceAlive(
                context,
                Constant.CLASS_APPSLOCKSERVICE)) {
            intent = new Intent(context, AppsLockService.class);
            context.startService(intent);
        }*/

        int safe_exit = sharedPreferences.getInt(Constant.SAFE_EXIT, 0);
        if (safe_exit != 0) {
            protectMyPhone(context, sharedPreferences);
        }
    }

    private void protectMyPhone(Context context, SharedPreferences sharedPreferences) {
        sharedPreferences.edit().putBoolean(Constant.USB_DEBUG, false).commit();
        // sharedPreferences.edit().putBoolean(Constant.SDCARD_USBDISK, false).commit();

        ActivityManager activityManager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> mRunningTaskInfoList =
                activityManager.getRunningTasks(1);
        if (mRunningTaskInfoList == null) {
            return;
        }
        String packageName = mRunningTaskInfoList.get(0).topActivity.getPackageName();

        if (!context.getPackageName().equals(packageName)) {
            Intent intent = new Intent(context, AppsLockActivity.class);
            // 服务中开启Activity必须要加上这句
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Constant.APP_PACKAGE_NAME, Constant.LAUNCHER);
            context.startActivity(intent);

            String currentTime = mSimpleDateFormat.format(new Date());
            Event event = new Event();
            event.time = currentTime;
            event.event1 = "com.weidi.artifact.receiver.BootCompleteBroadcastReceiver";
            SimpleDao.getInstance().add(Event.class, event);
        }
    }

}
