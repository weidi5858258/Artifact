package com.weidi.artifact.db.dao;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.weidi.artifact.db.MyInfosSQLiteOpenHelper;


//com.google.android.gms
//com.google.android.gsf
//com.google.android.syncadapters.calendar
//com.sec.android.app.clockpackage
public class ProcessDao {
    private MyInfosSQLiteOpenHelper help;
    private SQLiteDatabase db;
    private Context context;
    private Intent intent;

    public ProcessDao(Context context) {
        help = new MyInfosSQLiteOpenHelper(context);
        this.context = context;
    }

    public void add(String packageName) {
        String sql = "insert into process (packagename) values (?);";
        db = help.getWritableDatabase();
        db.execSQL(sql, new String[]{packageName});
        db.close();
        intent = new Intent();
        intent.setAction("com.aowin.mobilesafe.process");
        context.sendBroadcast(intent);
    }

    public void delete(String packageName) {
        String sql = "delete from process where packagename=?;";
        db = help.getWritableDatabase();
        db.execSQL(sql, new String[]{packageName});
        db.close();
        intent = new Intent();
        intent.setAction("com.aowin.mobilesafe.process");
        context.sendBroadcast(intent);
    }

    public boolean query(String packageName) {
        boolean result = false;
        String sql = "select packagename from process where packagename=?;";
        db = help.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, new String[]{packageName});
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();
        return result;
    }

    public ArrayList<String> query() {
        ArrayList<String> list = new ArrayList<String>();
        String sql = "select packagename from process;";
        db = help.getReadableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        String packagename = null;
        while (cursor.moveToNext()) {
            packagename = cursor.getString(0);
            if (!list.contains(packagename)) {
                list.add(packagename);
            }
        }
        cursor.close();
        db.close();
        return list;
    }

}
