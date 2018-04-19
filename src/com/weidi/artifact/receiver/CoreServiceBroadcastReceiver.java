package com.weidi.artifact.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.service.AppsLockService;
import com.weidi.artifact.service.CoreService;
import com.weidi.artifact.service.PeriodicalSerialKillerService;
import com.weidi.log.MLog;
import com.weidi.utils.MyUtils;

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
public class CoreServiceBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "CoreServiceBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        MLog.d(TAG, "onReceive():intent = " + intent);
        // 开启核心服务
        if (!MyUtils.isSpecificServiceAlive(
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
        }
    }

}
