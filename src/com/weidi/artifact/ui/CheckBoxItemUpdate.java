package com.weidi.artifact.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weidi.artifact.R;

public class CheckBoxItemUpdate extends RelativeLayout {
	private TextView tv_title = null;
	private TextView tv_content = null;
	private CheckBox cb = null;
	
	//初始化
	public void init(Context context){
		View.inflate(context, R.layout.settings_view_item, CheckBoxItemUpdate.this);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_content = (TextView) findViewById(R.id.tv_content);
		cb = (CheckBox) findViewById(R.id.cb_update);
		tv_title.setText("设置是否自动更新");
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
		tv_content.setText(text);
	}
	
	public CheckBoxItemUpdate(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	public CheckBoxItemUpdate(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public CheckBoxItemUpdate(Context context) {
		super(context);
		init(context);
	}
	
}
