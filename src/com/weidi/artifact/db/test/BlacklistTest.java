package com.weidi.artifact.db.test;

import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;

import com.weidi.artifact.db.dao.BlacklistDao;
import com.weidi.artifact.db.dao.PhoneNumberAddressQueryUtils;
import com.weidi.artifact.db.bean.BlacklistInfo;
import com.weidi.utils.MyUtils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

public class BlacklistTest extends AndroidTestCase {
	private BlacklistDao dao;
	public void testAdd(){
		dao = new BlacklistDao(getContext());
//		Random random = new Random();
//		for(int i=100;i<=200;i++){
//			dao.add("13800000"+i,String.valueOf(random.nextInt(3)+1));
//		}
		BlacklistInfo info = new BlacklistInfo("坏蛋", "10019", "3");
		dao.add(info);
		
	}
	
	public void testDelete(){
		dao = new BlacklistDao(getContext());
		dao.delete("10010");
	}
	
	public void testUpdate(){
		dao = new BlacklistDao(getContext());
		dao.update("","13800000200","1");
	}
	
	public void testQuery(){
		dao = new BlacklistDao(getContext());
		List<BlacklistInfo> list = dao.query();
		Iterator<BlacklistInfo> iter = list.iterator();
		while(iter.hasNext()){
			BlacklistInfo info = iter.next();
			System.out.println(info.toString());
		}
	}

	public void testQuery1(){
		dao = new BlacklistDao(getContext());
		BlacklistInfo info = dao.query("10010");
		System.out.println(info.toString());
	}

	public void testPhoneNumberAddressQuery1(){
		PhoneNumberAddressQueryUtils.phoneNumberAddressQuery("15584121670");
	}
	
	public void testHttpGet() throws Exception{
		String address = MyUtils.httpClientGet("0575");
		System.out.println(address);
	}
	
	public void testPhoneNumberAddressHttpQuery() throws Exception{
		String temp = MyUtils.httpClientGet("0575");
		String address = PhoneNumberAddressQueryUtils.printResult(temp);
//		System.out.println(address);
	}
	
	public void testAddBlacklistSms(){
		Uri uri = Uri.parse("content://Sms");
		BlacklistDao dao = new BlacklistDao(getContext());
		ContentResolver cr = getContext().getContentResolver();
		Cursor cursor = cr.query(uri, new String[]{"address","date","read","type","body"}, "_id <= 100", null, null);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while(cursor.moveToNext()){
			String address = cursor.getString(0);
			long date = cursor.getLong(1);
			int read = cursor.getInt(2);
			int type = cursor.getInt(3);
			String body = cursor.getString(4);
			String number = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(address);
			String time = sdf.format(date);
			dao.addBlacklistSms(address, number, date, time, read, type, body);
		}
		cursor.close();
	}
	
}