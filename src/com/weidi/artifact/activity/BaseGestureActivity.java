package com.weidi.artifact.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
//杭州市观音桥投递组
//屏幕滑动的父类
public abstract class BaseGestureActivity extends Activity {
	private GestureDetector detector = null;
	protected SharedPreferences sp = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		detector = new GestureDetector(this, new SimpleOnGestureListener() {
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if (e2.getRawX() - e1.getRawX() >= 150) {// 从左向右滑
					goPrevious();
					return true;
				}
				if (e1.getRawX() - e2.getRawX() >= 150) {// 从右向左滑
					goNext();
					return true;
				}
				return false;
			}
		});
	}

	public abstract void goPrevious();

	public abstract void goNext();

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		detector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}

	
}
