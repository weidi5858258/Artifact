package com.weidi.artifact.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weidi.artifact.R;

public class CheckBoxItemBlacklistPhone extends RelativeLayout {
    public TextView tv_blacklistphone_number;
    public TextView tv_blacklistphone_address;
    public TextView tv_blacklistphone_time;
    public TextView tv_blacklistphone_duration;
    //	public CheckBox cb_blacklistphone_check;

    //初始化
    public void init(Context context) {
        View view = View.inflate(context, R.layout.blacklist_phone_view_item,
                CheckBoxItemBlacklistPhone.this);
        tv_blacklistphone_number = (TextView) view.findViewById(R.id.tv_blacklistphone_number);
        tv_blacklistphone_address = (TextView) view.findViewById(R.id.tv_blacklistphone_address);
        tv_blacklistphone_time = (TextView) view.findViewById(R.id.tv_blacklistphone_time);
        tv_blacklistphone_duration = (TextView) view.findViewById(R.id.tv_blacklistphone_duration);
        //		cb_blacklistphone_check = (CheckBox) view.findViewById(R.id
		// .cb_blacklistphone_check);
    }

    //	public void hideCheckBox(){
    //		cb_blacklistphone_check.setVisibility(CheckBox.GONE);
    //	}
    //
    //	public void showCheckBox(){
    //		cb_blacklistphone_check.setVisibility(CheckBox.VISIBLE);
    //	}

    //校验组合控件是否选中
    //	public boolean isChecked(){
    //		return cb_blacklistphone_check.isChecked();
    //	}
    //
    //	//设置组合控件的状态
    //	public void setChecked(boolean checked){
    //		cb_blacklistphone_check.setChecked(checked);
    //	}

    public CheckBoxItemBlacklistPhone(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CheckBoxItemBlacklistPhone(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckBoxItemBlacklistPhone(Context context) {
        super(context);
        init(context);
    }

}
