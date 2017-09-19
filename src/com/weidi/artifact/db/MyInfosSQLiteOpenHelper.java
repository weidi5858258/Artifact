package com.weidi.artifact.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyInfosSQLiteOpenHelper extends SQLiteOpenHelper {

	public MyInfosSQLiteOpenHelper(Context context) {
		super(context, "myinfos.db", null, 4);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "CREATE TABLE Sms (_id integer primary key autoincrement,address varchar(20) not null,date varchar(64) not null,read int(1) not null,type int(1) not null,body varchar(1024) not null);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if(oldVersion == 1 && newVersion == 2){
			String sql = "CREATE TABLE applock (_id integer primary key autoincrement,packagename varchar(100) not null);";
			db.execSQL(sql);
		}
		if(oldVersion == 2 && newVersion == 3){
			String sql = "CREATE TABLE network (_id integer primary key autoincrement,packagename varchar(10) not null,uid int(5) not null,mobile int(1) not null,wifi int(1) not null);";
			db.execSQL(sql);
		}
		if(oldVersion == 3 && newVersion == 4){
			String sql = "CREATE TABLE process (_id integer primary key autoincrement,packagename varchar not null);";
			db.execSQL(sql);
		}
		
		if(oldVersion == 4 && newVersion == 5){
			String sql_user = "CREATE TABLE userapp (_id integer primary key autoincrement,packagename varchar not null);";
			String sql_system = "DROP TABLE if exists systemapp; CREATE TABLE systemapp (_id integer primary key autoincrement,packagename varchar not null);";
			db.execSQL(sql_user);
			db.execSQL(sql_system);
		}
		
	}

}
