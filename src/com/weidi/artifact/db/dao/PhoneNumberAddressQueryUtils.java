package com.weidi.artifact.db.dao;

import java.io.File;

import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.weidi.utils.MyUtils;


public class PhoneNumberAddressQueryUtils {
	
	public static String phoneNumberAddressQuery(String number){
		String address = number;
		String sql;
		String path = "/data/data/com.aowin.mobilesafe/databases/address.db";
		File file = new File(path);
		if(file.exists() && file.length() > 0){//有数据库时用数据库查
			SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.aowin.mobilesafe/databases/address.db",null,SQLiteDatabase.OPEN_READONLY);
			if(number.startsWith("0")){//number3代表3位数的区号
				String number3 = number.substring(1, 3);
				sql = "select location from data2 where area=?;";
				Cursor cursor = db.rawQuery(sql, new String[]{number3});
				//即使查不到数据cursor也不会为null（cursor不需要判断是否为null）
				if(cursor.moveToNext()){//查不到数据时不会走这里
					address = cursor.getString(cursor.getColumnIndex("location")).trim();
					if(address.endsWith("电信") || address.endsWith("移动") || address.endsWith("联通")){
						address = address.substring(0,address.length() - 2);
					}
					return address;
				}else{
					if(number.length() >= 4){//位数不够的话要抛异常
						String number4 = number.substring(1, 4);
						cursor = db.rawQuery(sql, new String[]{number4});
						if(cursor.moveToNext()){
							address = cursor.getString(cursor.getColumnIndex("location")).trim();
							if(address.endsWith("电信") || address.endsWith("移动") || address.endsWith("联通")){
								address = address.substring(0,address.length() - 2);
							}
							return address;
						}else{
							if(number.length() >= 5){
								String number5 = number.substring(1, 5);
								cursor = db.rawQuery(sql, new String[]{number5});
								if(cursor.moveToNext()){
									address = cursor.getString(cursor.getColumnIndex("location")).trim();
									if(address.endsWith("电信") || address.endsWith("移动") || address.endsWith("联通")){
										address = address.substring(0,address.length() - 2);
									}
									return address;
								}
							}
						}
					}
				}
				if(!address.equals(number)){
					address = address.substring(0, address.length()-2);
					return address;
				}
				cursor.close();
				db.close();
			}else if(number.startsWith("1")){//要考虑特殊号码，如：110
				String number3 = number.substring(1, 3);
				if(number.length() >= 7){
					String number7 = number.substring(0, 7);
					sql = "select location from data2 where id=(select outkey from data1 where id=?);";
					Cursor cursor = db.rawQuery(sql, new String[]{number7});
					if(cursor.moveToNext()){//查不到数据时不会走这里
						address = cursor.getString(cursor.getColumnIndex("location"));
						return address;
					}
					cursor.close();
				}
				db.close();
			}else{//拨打本地的座机时不需要加区号，当然该机也不会是以“1”开头，除了特殊号码之外的座机
				
			}
		}else{//没有数据库时用网络查
			
		}
		return address;
	}
	
	public static String phoneNumberAddressHttpQuery(String number){
		String address = "没有信息";
		String jsonResult;
		String msg;
		JSONObject obj;
		try {
			if(number.length() == 3 && number.startsWith("0")){//number3代表3位数的区号
				jsonResult = MyUtils.httpClientGet(number);
				obj = new JSONObject(jsonResult);
				msg = (String) obj.get("msg");
				address = printResult(msg);
				return address;
			}else if(number.length() == 4 && number.startsWith("0")){//位数不够的话要抛异常
				jsonResult = MyUtils.httpClientGet(number);
				obj = new JSONObject(jsonResult);
				msg = (String) obj.get("msg");
				address = printResult(msg);
				return address;
			}else if(number.length() == 5 && number.startsWith("0")){
				jsonResult = MyUtils.httpClientGet(number);
				obj = new JSONObject(jsonResult);
				msg = (String) obj.get("msg");
				address = printResult(msg);
				return address;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return address;
	}
	
	public static String printResult(String msg){
		String s;
		StringBuffer sb = new StringBuffer();
		String[] str = msg.split("区域: ");
		for(int i=1;i<str.length;i++){
			if(i == 1){
				s = str[i].subSequence(1, str[i].length()-6).toString();
			}else{
				s = str[i].subSequence(0, str[i].length()-6).toString();
			}
			sb.append(s).append("\r\n");
		}
		return sb.toString();
	}
	
	//下面方法没用
	public static String phoneNumberAddressQuery1(String number){
		String address = number;
		if(number.length() < 7){
			return address;
		}
		String path = "/data/data/com.aowin.mobilesafe/databases/address.db";
		File file = new File(path);
		if(file.exists() && file.length() > 0){
			SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.aowin.mobilesafe/databases/address.db",null,SQLiteDatabase.OPEN_READONLY);
			String sql = "select location from data2 where id=(select outkey from data1 where id=?)";
			Cursor cursor = db.rawQuery(sql, new String[]{number.subSequence(0, 7).toString()});
			if(cursor != null){
				while(cursor.moveToNext()){
					address = cursor.getString(0);
				}
				cursor.close();
			}
			db.close();
		}
		path = null;
		file = null;
		return address;
	}
	
}
