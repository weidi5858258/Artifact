package com.weidi.artifact.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.weidi.artifact.db.MyInfosSQLiteOpenHelper;
import com.weidi.artifact.db.bean.AppNetworkLimitInfos;

public class NetworkLimitDao {
	private MyInfosSQLiteOpenHelper help;
	private SQLiteDatabase db;
	
	public NetworkLimitDao(Context context){
		help = new MyInfosSQLiteOpenHelper(context);
	}
	
	public void add(String packageName,int uid,int mobile,int wifi){
		String sql = "insert into network (packagename,uid,mobile,wifi) values (?,?,?,?);";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{packageName,String.valueOf(uid),String.valueOf(mobile),String.valueOf(wifi)});
		db.close();
	}
	//应用卸载时要删除
	public void delete(String packageName){
		String sql = "delete from network where packagename=?;";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{packageName});
		db.close();
	}
	
	public void update(String packageName,int mobile,int wifi){
		String sql = "update network set mobile=?,wifi=? where packagename=?;";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{String.valueOf(mobile),String.valueOf(wifi),packageName});
		db.close();
	}
	public void updateMobile(String packageName,int mobile){
		String sql = "update network set mobile=? where packagename=?;";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{String.valueOf(mobile),packageName});
		db.close();
	}
	public void updateWifi(String packageName,int wifi){
		String sql = "update network set wifi=? where packagename=?;";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{String.valueOf(wifi),packageName});
		db.close();
	}
	
	public int queryMobile(String packageName){
		int mobile = 1;
		String sql = "select mobile from network where packagename=?;";
		db = help.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql,new String[]{packageName});
		while(cursor.moveToFirst()){
			mobile = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return mobile;
	}
	
	public int queryWifi(String packageName){
		int wifi = 1;
		String sql = "select wifi from network where packagename=?;";
		db = help.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql,new String[]{packageName});
		while(cursor.moveToFirst()){
			wifi = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return wifi;
	}
	
	public List<AppNetworkLimitInfos> queryAll(){
		List<AppNetworkLimitInfos> list = new ArrayList<AppNetworkLimitInfos>();
		String sql = "select packagename,uid,mobile,wifi from network;";
		db = help.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql,null);
		AppNetworkLimitInfos info = null;
		while(cursor.moveToNext()){
			String packagename = cursor.getString(cursor.getColumnIndex("packagename"));
			int uid = cursor.getInt(cursor.getColumnIndex("uid"));
			int mobile = cursor.getInt(cursor.getColumnIndex("mobile"));
			int wifi = cursor.getInt(cursor.getColumnIndex("wifi"));
			info = new AppNetworkLimitInfos(packagename,uid,mobile,wifi);
			list.add(info);
		}
		cursor.close();
		db.close();
		return list;
	}
	
	public boolean isPackageNameExist(String packageName){
		boolean flag = false;
		String sql = "select * from network where packagename=?;";
		db = help.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, new String[]{packageName});
		if(cursor.moveToNext()){
			flag = true;
		}
		cursor.close();
		db.close();
		return flag;
	}
}
