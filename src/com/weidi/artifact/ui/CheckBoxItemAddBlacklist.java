package com.weidi.artifact.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weidi.artifact.R;

public class CheckBoxItemAddBlacklist extends RelativeLayout {
	private CheckBox cb;
	private TextView tv;
	
	//初始化
	public void init(Context context){
		View.inflate(context, R.layout.addblacklist_view_item_mode, CheckBoxItemAddBlacklist.this);
		cb = (CheckBox) findViewById(R.id.cb_blacklist_checked);
		tv = (TextView) findViewById(R.id.tv_blacklist_mode);
	}
	
	//校验组合控件是否选中
	public boolean isChecked(){
		return cb.isChecked();
	}
	
	//设置组合控件的状态
	public void setChecked(boolean checked){
		cb.setChecked(checked);
	}
	
	public void setTVContent(String text){
		tv.setText(text);
	}
	
	public void setTVColor(int color){
		tv.setTextColor(color);
	}
	
	public CheckBoxItemAddBlacklist(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public CheckBoxItemAddBlacklist(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CheckBoxItemAddBlacklist(Context context) {
		super(context);
		init(context);
	}
	
}
