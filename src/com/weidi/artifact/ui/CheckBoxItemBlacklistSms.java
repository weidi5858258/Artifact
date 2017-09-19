package com.weidi.artifact.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weidi.artifact.R;

public class CheckBoxItemBlacklistSms extends RelativeLayout {
    public TextView tv_blacklistsms_number;
    public TextView tv_blacklistsms_address;
    public TextView tv_blacklistsms_time;
    public TextView tv_blacklistsms_body;
    public CheckBox cb_blacklistsms_check;

    //初始化
    public void init(Context context) {
        View view = View.inflate(context, R.layout.blacklist_sms_view_item,
                CheckBoxItemBlacklistSms.this);
        tv_blacklistsms_number = (TextView) view.findViewById(R.id.tv_blacklistsms_number);
        tv_blacklistsms_address = (TextView) view.findViewById(R.id.tv_blacklistsms_address);
        tv_blacklistsms_time = (TextView) view.findViewById(R.id.tv_blacklistsms_time);
        tv_blacklistsms_body = (TextView) view.findViewById(R.id.tv_blacklistsms_body);
        cb_blacklistsms_check = (CheckBox) view.findViewById(R.id.cb_blacklistsms_check);
    }

    public void hideCheckBox() {
        cb_blacklistsms_check.setVisibility(CheckBox.GONE);
    }

    public void showCheckBox() {
        cb_blacklistsms_check.setVisibility(CheckBox.VISIBLE);
    }

    //校验组合控件是否选中
    public boolean isChecked() {
        return cb_blacklistsms_check.isChecked();
    }

    //设置组合控件的状态
    public void setChecked(boolean checked) {
        cb_blacklistsms_check.setChecked(checked);
    }

    public CheckBoxItemBlacklistSms(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CheckBoxItemBlacklistSms(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckBoxItemBlacklistSms(Context context) {
        super(context);
        init(context);
    }

}
