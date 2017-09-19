package com.weidi.artifact.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;

//在自己的服务里面不能开启自己的服务
public class TestService extends Service {
	private BroadcastReceiver screenEvent;
	private Intent intent;
	private IntentFilter filter;
	
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("服务已经开启......");
		screenEvent = new ScreenEventBroadcastReceiver(); 
		intent = new Intent();
		filter = new IntentFilter();  
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(2147483647);
		filter.addAction(Intent.ACTION_SCREEN_ON);  
		filter.addAction(Intent.ACTION_SCREEN_OFF); 
		registerReceiver(screenEvent, filter);
	}
	
	 //接收屏幕改变的广播:
	 public class ScreenEventBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())){
	 	          //收到屏幕开启的通知
	 			System.out.println("收到屏幕开启的广播");
	 	     }else if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
	 	          //收到屏幕关闭的通知
	 	    	System.out.println("收到屏幕关闭的广播!");
	 	     }else if("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())){
	 			Object[] objs = (Object[]) intent.getExtras().get("pdus");
	 			for(Object obj : objs){
	 				SmsMessage sms = SmsMessage.createFromPdu((byte[])obj);
	 				String number = sms.getOriginatingAddress().trim();
	 				String content = sms.getMessageBody().trim();
	 				System.out.println(number+" "+content);
	 			}
//	 			abortBroadcast();
	 	     }
		}

	 }
	
	 //下面方法是必须要有的
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
