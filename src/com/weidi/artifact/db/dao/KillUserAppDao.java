package com.weidi.artifact.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.weidi.artifact.db.MyInfosSQLiteOpenHelper;

public class KillUserAppDao {
	private MyInfosSQLiteOpenHelper help;
	private SQLiteDatabase db;
	private Context context;
	private Intent intent;
	
	public KillUserAppDao(Context context){
		help = new MyInfosSQLiteOpenHelper(context);
		this.context = context;
	}
	
	public void add(String packageName){
		String sql = "insert into userapp (packagename) values (?);";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{packageName});
		db.close();
		//为了使程序锁能够更快地响应动作，先把数据库里的内容加载到内存中，所以在增加或者删除操作后要通知数据更新一下
		intent = new Intent();
		intent.setAction("com.aowin.mobilesafe.package");
		context.sendBroadcast(intent);
	}
	
	public void delete(String packageName){
		String sql = "delete from userapp where packagename=?;";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{packageName});
		db.close();
		intent = new Intent();
		intent.setAction("com.aowin.mobilesafe.package");
		context.sendBroadcast(intent);
	}
	
//	public boolean query(String packageName){
//		boolean result = false;
//		String sql = "select packagename from applock where packagename=?;";
//		db = help.getReadableDatabase();
//		Cursor cursor = db.rawQuery(sql, new String[]{packageName});
//		if(cursor.moveToNext()){
//			result = true;
//		}
//		cursor.close();
//		db.close();
//		return result;
//	}
	
//	public List<String> query(){
//		List<String> list = new ArrayList<String>();
//		String sql = "select packagename from userapp;";
//		db = help.getReadableDatabase();
//		Cursor cursor = db.rawQuery(sql, null);
//		String packagename = null;
//		while(cursor.moveToNext()){
//			packagename = cursor.getString(0);
//			list.add(packagename);
//		}
//		cursor.close();
//		db.close();
//		return list;
//	}
	
	public List<String> queryUserApp(){
		List<String> list = new ArrayList<String>();
		String sql = "select packagename from systemapp;";
		db = help.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		String packagename = null;
		while(cursor.moveToNext()){
			packagename = cursor.getString(0);
			list.add(packagename);
		}
		cursor.close();
		db.close();
		return list;
	}
	
}
