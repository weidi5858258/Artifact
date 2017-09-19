package com.weidi.artifact.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlacklistSQLiteOpenHelper extends SQLiteOpenHelper {

	public BlacklistSQLiteOpenHelper(Context context) {
		super(context, "blacklist.db", null, 6);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create TABLE blacklist (_id integer primary key autoincrement,name varchar(20),number varchar(30) not null,mode varchar(1) not null);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = null;
		if(oldVersion == 2 && newVersion == 3){
			sql = "CREATE TABLE blacklist_phone (_id INTEGER PRIMARY KEY AUTOINCREMENT,number TEXT,date INTEGER,duration INTEGER,type INTEGER,new INTEGER,name TEXT);";
			db.execSQL(sql);
			sql = "CREATE TABLE blacklist_sms (_id integer primary key autoincrement,address varchar not null,date varchar not null,read int not null,type int not null,body varchar not null);";
			db.execSQL(sql);
		}
		if(oldVersion == 3 && newVersion == 4){
			sql = "CREATE TABLE blacklist_count (_id integer primary key autoincrement,blacklist_phone integer,blacklist_sms integer);";
			db.execSQL(sql);
			sql = "drop TABLE blacklist_sms";
			db.execSQL(sql);
			sql = "CREATE TABLE blacklist_sms (_id integer primary key autoincrement,number varchar,address varchar,date long,time varchar,read int,type int,body varchar);";
			db.execSQL(sql);
		}
		if(oldVersion == 4 && newVersion == 5){
			sql = "drop TABLE blacklist_phone";
			db.execSQL(sql);
			sql = "CREATE TABLE blacklist_phone (_id integer primary key autoincrement,number varchar,address varchar,date long,duration long,time varchar,type int,new int);";
			db.execSQL(sql);
		}
		if(oldVersion == 5 && newVersion == 6){
			sql = "drop TABLE blacklist_phone";
			db.execSQL(sql);
			sql = "CREATE TABLE blacklist_phone (_id integer primary key autoincrement,number varchar,address varchar,date long,duration long,time varchar,type int,new int,flag int);";
			db.execSQL(sql);
		}
		
	}

}
