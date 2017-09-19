package com.weidi.artifact.service;

import java.util.ArrayList;
import java.util.List;


import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.weidi.artifact.R;

public class TopWindowService extends Service implements OnClickListener{
	
	public static final String OPERATION = "operation";
	public static final int OPERATION_SHOW = 100;
	public static final int OPERATION_HIDE = 101;

	private static final int HANDLE_CHECK_ACTIVITY = 200;

	private boolean isAdded = false; // 是否已增加悬浮窗
	private static WindowManager wm;
	private static WindowManager.LayoutParams params;
	private View view;
	
	private List<String> homeList; // 桌面应用程序包名列表
	private ActivityManager am;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate()
	{
		wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
		homeList = getHomes();
		createFloatView();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int operation = intent.getIntExtra(OPERATION, OPERATION_SHOW);
		switch (operation){
			case OPERATION_SHOW:
				mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
				mHandler.sendEmptyMessage(HANDLE_CHECK_ACTIVITY);
				break;
			case OPERATION_HIDE:
				mHandler.removeMessages(HANDLE_CHECK_ACTIVITY);
				break;
		}
		return super.onStartCommand(intent, flags, startId);
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what){
				case HANDLE_CHECK_ACTIVITY:
					if (isHome()){
						if (!isAdded)
						{
							wm.addView(view, params);
							isAdded = true;
						}
					} else {
						if (isAdded)
						{
							wm.removeView(view);
							isAdded = false;
						}
					}
					mHandler.sendEmptyMessageDelayed(HANDLE_CHECK_ACTIVITY, 1000);
					break;
			}
		}
	};
	
	/**
	 * 创建悬浮窗
	 */
	private void createFloatView(){
		view = View.inflate(getApplicationContext(), R.layout.querytools_view, null);
		Button bt_querytools_sure = (Button) view.findViewById(R.id.bt_querytools_sure);
		Button bt_querytools_cancel = (Button) view.findViewById(R.id.bt_querytools_cancel);
		bt_querytools_sure.setOnClickListener(this);
		bt_querytools_cancel.setOnClickListener(this);
	    params = new WindowManager.LayoutParams();
		params.width = (int)(wm.getDefaultDisplay().getWidth() / 3 * 2);
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
//		FLAG_NOT_TOUCH_MODAL--只有这个属性时，特点：能输入，但是输入的内容看起来不好（输入体验很差），有点击事件，但是不能后退，按Home键也没有用。
//		FLAG_NOT_FOCUSABLE----只有这个属性时，特点：能后退，能操作其他动作，但是不能输入，有点击事件（点击Button时没有被按下的效果，但是有响应事件）。
//		FLAG_NOT_TOUCHABLE----不能用，也不要用，不用再去测试
		params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT|
			          WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
//		TYPE_TOAST-----------有点击事件，不能输入内容，不影响其他操作，适合看视频
//		TYPE_SYSTEM_OVERLAY--不影响界面的操作，但是自己不能移动，失去点击事件
		params.alpha = 30;
		params.gravity = Gravity.TOP + Gravity.LEFT;
		params.x = (int)(wm.getDefaultDisplay().getWidth() / 2 - params.width / 2);//在X轴的中间的位置上
		params.y = 200;
		view.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				return true;
			}
		});
		view.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;
			int newX;
			int newY;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch(event.getAction()){
					case MotionEvent.ACTION_UP:{
						
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
		});
		wm.addView(view, params);
		isAdded = true;
	}
	
	/**
	 * 获得属于桌面的应用的应用包名称
	 * 
	 * @return 返回包含所有包名的字符串列表
	 */
	private List<String> getHomes(){
		List<String> names = new ArrayList<String>();
		PackageManager packageManager = this.getPackageManager();
		// 属性
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		for (ResolveInfo ri : resolveInfo){
			names.add(ri.activityInfo.packageName);
		}
		return names;
	}

	/**
	 * 判断当前界面是否是桌面
	 */
	public boolean isHome(){
		if (am == null)
		{
			am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		}
		List<RunningTaskInfo> rti = am.getRunningTasks(1);
		return true;
		// return homeList.contains(rti.get(0).topActivity.getPackageName());
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch(v.getId()){
			case R.id.bt_querytools_sure:{
				System.out.println("sure");
				break;
			}
			case R.id.bt_querytools_cancel:{
				wm.removeView(view);
				stopSelf();
				break;
			}
		}
	}
	
}
