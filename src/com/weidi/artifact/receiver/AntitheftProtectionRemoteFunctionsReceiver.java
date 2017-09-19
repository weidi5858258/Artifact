package com.weidi.artifact.receiver;

import com.weidi.artifact.service.GPSService;
import com.weidi.artifact.service.LockScreenService;
import com.weidi.artifact.service.WipeDataService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class AntitheftProtectionRemoteFunctionsReceiver extends
		BroadcastReceiver {
	private static final String TEL = "18565603244";
	@Override
	public void onReceive(Context context, Intent intent) {
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
//				intent = new Intent();
//				intent.setClass(context, AlermService.class);
//				context.startService(intent);
//				abortBroadcast();
//				intent = null;
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
