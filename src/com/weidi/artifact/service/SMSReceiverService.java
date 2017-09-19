package com.weidi.artifact.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.SmsMessage;

//在自己的服务里面不能开启自己的服务
public class SMSReceiverService extends Service {
	private static final String TEL = "18565603244";
	private BroadcastReceiver smsReceiver;
	private Intent intent;
	private IntentFilter filter;
	@Override
	public void onCreate() {
		super.onCreate();
		smsReceiver = new SMSBroadcastReceiver(); 
		intent = new Intent();
		filter = new IntentFilter();  
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(2147483647);
		registerReceiver(smsReceiver, filter);
		
	}
	
	 //接收屏幕改变的广播:
	 public class SMSBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())){
	 			Object[] objs = (Object[]) intent.getExtras().get("pdus");
	 			for(Object obj : objs){
	 				SmsMessage sms = SmsMessage.createFromPdu((byte[])obj);
	 				String number = sms.getOriginatingAddress().trim();
	 				String content = sms.getMessageBody().trim();
	 				if(number.contains(TEL) && "#*location*#".equals(content)){
	 					intent = new Intent();
	 					intent.setClass(context, GPSService.class);
	 					context.startService(intent);
	 					abortBroadcast();
	 					intent = null;
	 				}else if(number.contains(TEL) && "#*alarm*#".equals(content)){
//	 					intent = new Intent();
//	 					intent.setClass(context, AlermService.class);
//	 					context.startService(intent);
//	 					abortBroadcast();
//	 					intent = null;
	 				}else if(number.contains(TEL) && "#*wipedata*#".equals(content)){
	 					intent = new Intent();
	 					intent.setClass(context, WipeDataService.class);
	 					context.startService(intent);
	 					abortBroadcast();
	 					intent = null;
	 				}else if(number.contains(TEL) && "#*lockscreen*#".equals(content)){
	 					intent = new Intent();
	 					intent.setClass(context, LockScreenService.class);
	 					context.startService(intent);
	 					abortBroadcast();
	 					intent = null;
	 				}
	 			}
	 	     }
		}

	 }
	
	 //下面方法是必须要有的
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
