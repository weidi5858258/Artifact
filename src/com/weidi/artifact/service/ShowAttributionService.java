package com.weidi.artifact.service;

import com.weidi.artifact.R;
import com.weidi.artifact.db.dao.PhoneNumberAddressQueryUtils;
import com.weidi.utils.MyUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class ShowAttributionService extends Service {
	private WindowManager wm;
	private TelephonyManager tm;
	private View view;
	private TextView tv;
	private WindowManager.LayoutParams params;
	private IntentFilter filter;
	private SharedPreferences sp;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		filter = new IntentFilter("android.intent.action.NEW_OUTGOING_CALL");
		ShowAttributionReceiver receiver = new ShowAttributionReceiver();
		getApplicationContext().registerReceiver(receiver , filter);
		sp = getApplicationContext().getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
	}

	private class ShowAttributionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean flag = MyUtils.isSpecificServiceAlive(context, "ShowAttributionService");
			if(flag){
				String phoneNumber = getResultData();
				String address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(phoneNumber);
				myToast(address);
			}
		}
	}
	
	private PhoneStateListener phoneStateListener = new PhoneStateListener(){
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch(state){
				case TelephonyManager.CALL_STATE_IDLE:{
					if(view != null){
						wm.removeView(view);
					}
					break;
					}
				case TelephonyManager.CALL_STATE_RINGING:{
					boolean flag = MyUtils.isSpecificServiceAlive(getApplicationContext(), "ShowAttributionService");
					if(flag){
						String address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(incomingNumber);
						myToast(address);
					}
					break;
					}
				case TelephonyManager.CALL_STATE_OFFHOOK:{
					break;
					}
			}
		}
	};
	
	@SuppressLint("NewApi")
	private void myToast(String content){
		view = View.inflate(getApplicationContext(), R.layout.mytoast_view, null);
//		view.setBackgroundDrawable(new BitmapDrawable());
//		view.setBackgroundColor(Color.TRANSPARENT);
		view.setOnTouchListener(l);
		view.setOnClickListener(ocl);
		tv = (TextView) view.findViewById(R.id.mytoast_address);
		tv.setText(content);
		params = new WindowManager.LayoutParams();
		params.alpha = 30;//放在服务里面设置这个透明度好像没有效果
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.TOP + Gravity.LEFT;
		params.x = sp.getInt("lastX", (wm.getDefaultDisplay().getWidth() / 2 - view.getWidth() / 2));
		params.y = sp.getInt("lastY", (wm.getDefaultDisplay().getHeight() / 2 - view.getHeight() / 2));
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
				       WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		params.format = 1;
		wm.addView(view, params);
	}
	
	private OnTouchListener l = new OnTouchListener() {
		int startX;
		int startY;
		int newX;
		int newY;
		@SuppressLint("NewApi")
		@TargetApi(Build.VERSION_CODES.HONEYCOMB)
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()){
				case MotionEvent.ACTION_UP:{
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
					break;
					}
				case MotionEvent.ACTION_DOWN:{
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
					}
				case MotionEvent.ACTION_MOVE:{
					newX = (int) event.getRawX();
					newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					params.x += dx;
					params.y += dy;
					if(params.x < 0){
						params.x = 0;
					}
					if(params.y < 0){
						params.y = 0;
					}
					if(params.x > (wm.getDefaultDisplay().getWidth() - view.getWidth())){
						params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
					}
					if(params.y > (wm.getDefaultDisplay().getHeight() - view.getHeight())){
						params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
					}
					wm.updateViewLayout(view, params);
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
					}
			}
			return false;
		}
	};
	
	private OnClickListener ocl = new OnClickListener() {
		long firstClickTime;
		@Override
		public void onClick(View v) {//双击事件
			if(firstClickTime > 0){
				long secondClickTime = SystemClock.uptimeMillis();
				long dtime = secondClickTime - firstClickTime;
				if(dtime <= 500){
					params.x = wm.getDefaultDisplay().getWidth() / 2 - view.getWidth() / 2;
					params.y = wm.getDefaultDisplay().getHeight() / 2 - view.getHeight() / 2;
					wm.updateViewLayout(view, params);
					Editor editor = sp.edit();
					editor.putInt("lastX", params.x);
					editor.putInt("lastY", params.y);
					editor.commit();
				}else{
					firstClickTime = 0;
				}
				return;
			}
			firstClickTime = SystemClock.uptimeMillis();
			new Thread(new Runnable() {
				@Override
				public void run() {
					SystemClock.sleep(500);
					firstClickTime = 0;
				}
			}).start();
		}
	};
	/*private void myToast(String content){
		tv = new TextView(getApplicationContext());
		tv.setText(content);
		tv.setTextColor(Color.RED);
		tv.setTextSize(25);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		wm.addView(tv, params);
	}*/
	
}
