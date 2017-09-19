package com.weidi.artifact.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weidi.artifact.R;

import static com.weidi.artifact.R.id.tv_title;

public class CheckBoxItemUsbDebug extends RelativeLayout {
    private TextView tv_title = null;
    private TextView tv_content = null;
    private CheckBox cb = null;

    //初始化
    public void init(Context context) {
        View.inflate(context, R.layout.settings_view_item, CheckBoxItemUsbDebug.this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_content = (TextView) findViewById(R.id.tv_content);
        cb = (CheckBox) findViewById(R.id.cb_update);
        tv_title.setText("USB调试设置");
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

    public void setTVColor(int color) {
        tv_content.setTextColor(color);
    }

    public CheckBoxItemUsbDebug(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CheckBoxItemUsbDebug(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckBoxItemUsbDebug(Context context) {
        super(context);
        init(context);
    }

}
