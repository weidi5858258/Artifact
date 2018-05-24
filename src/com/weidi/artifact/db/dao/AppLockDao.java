package com.weidi.artifact.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.db.MyInfosSQLiteOpenHelper;
import com.weidi.artifact.service.AppsLockService;
import com.weidi.eventbus.EventBusUtils;

public class AppLockDao {
    private MyInfosSQLiteOpenHelper help;
    private SQLiteDatabase db;
    private Context context;
    private Intent intent;

    public AppLockDao(Context context) {
        help = new MyInfosSQLiteOpenHelper(context);
        this.context = context;
    }

    public void add(String packageName) {
        String sql = "insert into applock (packagename) values (?);";
        db = help.getWritableDatabase();
        db.execSQL(sql, new String[]{packageName});
        db.close();
        //为了使程序锁能够更快地响应动作，先把数据库里的内容加载到内存中，所以在增加或者删除操作后要通知数据更新一下
        //		intent = new Intent();
        //		intent.setAction("com.aowin.mobilesafe.update");
        //		context.sendBroadcast(intent);
        EventBusUtils.postAsync(AppsLockService.class,Constant.UPDATEAPPSLOCKLIST, null);
    }

    public void delete(String packageName) {
        String sql = "delete from applock where packagename=?;";
        db = help.getWritableDatabase();
        db.execSQL(sql, new String[]{packageName});
        db.close();
        //		intent = new Intent();
        //		intent.setAction("com.aowin.mobilesafe.update");
        //		context.sendBroadcast(intent);
        EventBusUtils.postAsync(AppsLockService.class,Constant.UPDATEAPPSLOCKLIST, null);
    }

    public boolean query(String packageName) {
        boolean result = false;
        String sql = "select packagename from applock where packagename=?;";
        db = help.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{packageName});
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    public List<String> query() {
        List<String> list = new ArrayList<String>();
        String sql = "select packagename from applock;";
        db = help.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        String packagename = null;
        while (cursor.moveToNext()) {
            packagename = cursor.getString(0);
            list.add(packagename);
        }
        cursor.close();
        db.close();
        return list;
    }

}
