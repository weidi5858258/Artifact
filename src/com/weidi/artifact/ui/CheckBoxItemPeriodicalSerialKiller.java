package com.weidi.artifact.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weidi.artifact.R;

//周期性的连环杀进程
public class CheckBoxItemPeriodicalSerialKiller extends RelativeLayout {
    private TextView tv_title = null;
    private TextView tv_content = null;
    private CheckBox cb = null;

    //初始化
    public void init(Context context) {
        View.inflate(context, R.layout.settings_view_item, CheckBoxItemPeriodicalSerialKiller.this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        cb = (CheckBox) findViewById(R.id.cb_update);
        tv_title.setText("连环杀进程设置");
    }

    //校验组合控件是否选中
    public boolean isChecked() {
        return cb.isChecked();
    }

    //设置组合控件的状态
    public void setChecked(boolean checked) {
        cb.setChecked(checked);
    }

    public void setTVContent(String text) {
        tv_content.setText(text);
    }

    public CheckBoxItemPeriodicalSerialKiller(Context context, AttributeSet attrs,
                                              int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CheckBoxItemPeriodicalSerialKiller(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckBoxItemPeriodicalSerialKiller(Context context) {
        super(context);
        init(context);
    }


}
