package com.weidi.artifact.ui;


import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.weidi.artifact.R;

public class MyClickView extends View {
	private Context mContext;
	private TextView tv_fun;
	
	public MyClickView(Context context) {
		super(context);
		mContext = context;
		init();
	}
	public MyClickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}
	public MyClickView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		init();
	}

	private void init(){
		View view = View.inflate(mContext, R.layout.myview, null);
		tv_fun = (TextView) view.findViewById(R.id.tv_fun);
	}
	
	public void setText(String text){
		tv_fun.setText(text);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
	private long one;
	private long two;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch(event.getAction()){
			case MotionEvent.ACTION_UP:
				two = SystemClock.uptimeMillis();
				if(two - one < 300){//多练习，凭感觉
					System.out.println("单击");
				}else if(two - one >= 300 && two - one <600){
					System.out.println("双击");
				}else{
					System.out.println("长击");
				}
				break;
			case MotionEvent.ACTION_DOWN:
				one = SystemClock.uptimeMillis();
				
				break;
			case MotionEvent.ACTION_MOVE:
				
				break;
		}
		return true;
	}
	
	
	
}
