package com.weidi.artifact.receiver;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

//桌面小控件
public class MSAppWidgetProvider extends AppWidgetProvider {
    //每接收到一次广播就调用一下，使用频繁
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        System.out.println("MSAppWidgetProvider:onReceive");
    }

    //每次更新都调用一次该方法，使用频繁
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        System.out.println("MSAppWidgetProvider:onUpdate");
    }

    @SuppressLint("NewApi")
    @Override
    public void onAppWidgetOptionsChanged(Context context,
                                          AppWidgetManager appWidgetManager, int appWidgetId,
                                          Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        System.out.println("MSAppWidgetProvider:onAppWidgetOptionsChanged");
    }

    //每删除一个就调用一次
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        System.out.println("MSAppWidgetProvider:onDeleted");
    }

    //当该Widget第一次添加到桌面是调用该方法，可添加多次但只是第一次添加时调用
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        System.out.println("MSAppWidgetProvider:onEnabled");
    }

    //当最后一个该Widget删除时调用该方法，注意是最后一个
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        System.out.println("MSAppWidgetProvider:onDisabled");
    }

    @SuppressLint("NewApi")
    public void onRestored(Context context,
                           int[] oldWidgetIds,
                           int[] newWidgetIds) {
//        super.onRestored(context, oldWidgetIds, newWidgetIds);
        System.out.println("MSAppWidgetProvider:onRestored");
    }

	
	/*

	        桌面上还没有这个应用的控件时，添加控件到桌面
       04-13 13:52:05.240: I/System.out(6048): MSAppWidgetProvider:onEnabled
       04-13 13:52:05.240: I/System.out(6048): MSAppWidgetProvider:onReceive
       04-13 13:52:05.255: I/System.out(6048): MSAppWidgetProvider:onUpdate
       04-13 13:52:05.255: I/System.out(6048): MSAppWidgetProvider:onReceive
       04-13 13:52:05.385: I/System.out(6048): MSAppWidgetProvider:onAppWidgetOptionsChanged
       04-13 13:52:05.385: I/System.out(6048): MSAppWidgetProvider:onReceive
                  接下去再添加此应用的控件时，都是发生下面这些方法
       04-13 13:53:05.195: I/System.out(6048): MSAppWidgetProvider:onUpdate
       04-13 13:53:05.195: I/System.out(6048): MSAppWidgetProvider:onReceive
       04-13 13:53:05.360: I/System.out(6048): MSAppWidgetProvider:onAppWidgetOptionsChanged
       04-13 13:53:05.360: I/System.out(6048): MSAppWidgetProvider:onReceive
                  每次删除一个控件时
       04-13 13:55:53.785: I/System.out(6048): MSAppWidgetProvider:onDeleted
       04-13 13:55:53.785: I/System.out(6048): MSAppWidgetProvider:onReceive
                  删除最后一个控件时
       04-13 14:01:43.560: I/System.out(6048): MSAppWidgetProvider:onDeleted
       04-13 14:01:43.560: I/System.out(6048): MSAppWidgetProvider:onReceive
       04-13 14:01:43.590: I/System.out(6048): MSAppWidgetProvider:onDisabled
       04-13 14:01:43.590: I/System.out(6048): MSAppWidgetProvider:onReceive

	 */
}
