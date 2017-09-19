package com.weidi.artifact.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.weidi.artifact.db.BlacklistSQLiteOpenHelper;
import com.weidi.artifact.db.bean.BlacklistInfo;
import com.weidi.artifact.db.bean.BlacklistPhone;
import com.weidi.artifact.db.bean.BlacklistSms;

public class BlacklistDao {
	private BlacklistSQLiteOpenHelper help;
	private SQLiteDatabase db;
	
	public BlacklistDao(Context context){
		help = new BlacklistSQLiteOpenHelper(context);
	}
	
//------------------------------下面是blacklist表------------------------------------------	
	public void add(String number,String mode){
		String sql = "insert into blacklist (number,mode) values (?,?);";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{number,mode});
		db.close();
	}
	public void add(BlacklistInfo info){
		String sql = "insert into blacklist (name,number,mode) values (?,?,?);";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{info.getName(),info.getNumber(),info.getMode()});
		db.close();
	}
	
	public void delete(String number){
		String sql = "delete from blacklist where number=?;";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{number});
		db.close();
	}
	
	public void update(String name,String number,String mode){
		String sql = "update blacklist set name=?,mode=? where number=?;";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{name,mode,number});
		db.close();
	}
	
	public BlacklistInfo query(String number){
		BlacklistInfo info = null;
		String sql = "select name,number,mode from blacklist where number=?;";
		db = help.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, new String[]{number});
		if(cursor != null){
			while(cursor.moveToNext()){
				String name = cursor.getString(0);
				String num = cursor.getString(1);
				String mode = cursor.getString(2);
				info = new BlacklistInfo(name,num,mode);
			}
		}
		cursor.close();
		db.close();
		return info;
	}
	
	public List<BlacklistInfo> query(){
		List<BlacklistInfo> list = new ArrayList<BlacklistInfo>();
		String sql = "select name,number,mode from blacklist order by _id desc;";
		BlacklistInfo info;
		db = help.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor != null){
			while(cursor.moveToNext()){
				String name = cursor.getString(0);
				String number = cursor.getString(1);
				String mode = cursor.getString(2);
				info = new BlacklistInfo(name, number, mode);
				list.add(info);
			}
		}
		cursor.close();
		db.close();
		return list;
	}
	
	public boolean isNumberExist(String number){
		boolean flag = false;
		String sql = "select * from blacklist where number=?;";
		db = help.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, new String[]{number});
		if(cursor != null){
			if(cursor.moveToNext()){
				flag = true;
			}
		}
		cursor.close();
		db.close();
		return flag;
	}
	
	//------------------------------下面是blacklist_phone表------------------------------------------
	public void addBlacklistPhone(String number,String address,long date,long duration,String time,int type,int news,int flag){
		String sql = "insert into blacklist_phone (number,address,date,duration,time,type,new,flag) values (?,?,?,?,?,?,?,?);";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{number,address,String.valueOf(date),String.valueOf(duration),time,String.valueOf(type),String.valueOf(news),String.valueOf(flag)});
		db.close();
	}
	
	public void addBlacklistPhone(BlacklistPhone phone){
		String sql = "insert into blacklist_phone (number,address,date,duration,time,type,new,flag) values (?,?,?,?,?,?,?,?);";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{phone.getNumber(),phone.getAddress(),String.valueOf(phone.getDate()),String.valueOf(phone.getDuration()),phone.getTime(),String.valueOf(phone.getType()),String.valueOf(phone.getNews()),String.valueOf(phone.getFlag())});
		db.close();
	}
	
	public void deleteBlacklistPhone(String date){
		String sql = "delete from blacklist_phone where date=?;";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{date});
		db.close();
	}
	
	public List<BlacklistPhone> queryBlacklistPhone(){
		List<BlacklistPhone> list = new ArrayList<BlacklistPhone>();
		String sql = "select number,address,date,duration,time,type,new,flag from blacklist_phone order by _id desc;";
		BlacklistPhone info;
		db = help.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor != null){
			while(cursor.moveToNext()){
				String number = cursor.getString(0);
				String address = cursor.getString(1);
				long date = cursor.getLong(2);
				long duration = cursor.getLong(3);
				String time = cursor.getString(4);
				int type = cursor.getInt(5);
				int news = cursor.getInt(6);
				int flag = cursor.getInt(7);
				info = new BlacklistPhone(number,address,date,duration,time,type,news,flag);
				list.add(info);
			}
		}
		cursor.close();
		db.close();
		return list;
	}
	
	//------------------------------下面是blacklist_sms表------------------------------------------
//	address varchar,date varchar,read int,type int,body varchar
	public void addBlacklistSms(String number,String address,long date,String time,int read,int type,String body){
		String sql = "insert into blacklist_sms (number,address,date,time,read,type,body) values (?,?,?,?,?,?,?);";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{number,address,String.valueOf(date),time,String.valueOf(read),String.valueOf(type),body});
		db.close();
	}
	
	public void addBlacklistSms(BlacklistSms sms){
		String sql = "insert into blacklist_sms (number,address,date,time,read,type,body) values (?,?,?,?,?,?,?);";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{sms.getNumber(),sms.getAddress(),String.valueOf(sms.getDate()),sms.getTime(),String.valueOf(sms.getRead()),String.valueOf(sms.getType()),sms.getBody()});
		db.close();
	}
	
	public void deleteBlacklistSms(String date){
		String sql = "delete from blacklist_sms where date=?;";
		db = help.getWritableDatabase();
		db.execSQL(sql,new String[]{date});
		db.close();
	}
	
	public List<BlacklistSms> queryBlacklistSms(){
		List<BlacklistSms> list = new ArrayList<BlacklistSms>();
		String sql = "select number,address,date,time,read,type,body from blacklist_sms order by _id desc;";
		BlacklistSms info;
		db = help.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		if(cursor != null){
			while(cursor.moveToNext()){
				String number = cursor.getString(0);
				String address = cursor.getString(1);
				long date = cursor.getLong(2);
				String time = cursor.getString(3);
				int read = cursor.getInt(4);
				int type = cursor.getInt(5);
				String body = cursor.getString(6);
				info = new BlacklistSms(number,address,date,time,read,type,body);
				list.add(info);
			}
		}
		cursor.close();
		db.close();
		return list;
	}
}
