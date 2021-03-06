package com.weidi.artifact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.activity.base.BaseActivity;
import com.weidi.artifact.controller.AppsLockActivityController;
import com.weidi.inject.InjectLayout;
import com.weidi.inject.InjectOnClick;
import com.weidi.inject.InjectView;
import com.weidi.log.MLog;

@InjectLayout(R.layout.activity_appslock)
public class AppsLockActivity extends BaseActivity
        implements OnClickListener {

    private static final String TAG = "AppsLockActivity";
    private static final boolean DBG = true;
    private AppsLockActivityController mAppsLockActivityController;

    @InjectView(R.id.password_layout)
    public LinearLayout password_layout;
    @InjectView(R.id.backdoor_rlayout)
    public RelativeLayout backdoor_rlayout;
    @InjectView(R.id.back_tv)
    public TextView back_tv;
    @InjectView(R.id.appslock_day_tc)
    public android.widget.TextClock appslock_day_tc;
    @InjectView(R.id.appslock_time_tc)
    public android.widget.TextClock appslock_time_tc;
    @InjectView(R.id.appslock_icon_iv)
    public ImageView appslock_icon_iv;
    @InjectView(R.id.appslock_appname_tv)
    public TextView appslock_appname_tv;
    @InjectView(R.id.appslock_password_error_count_tv)
    public TextView appslock_password_error_count_tv;
    @InjectView(R.id.appslock_password_et)
    public EditText appslock_password_et;
    @InjectView(R.id.appslock_entry_ibtn)
    public ImageButton appslock_entry_ibtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DBG) {
            MLog.d(TAG, "onCreate():savedInstanceState = " + savedInstanceState);
        }
        super.onCreate(savedInstanceState);
        mAppsLockActivityController = new AppsLockActivityController(this);
        mAppsLockActivityController.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        if (DBG) {
            MLog.d(TAG, "onStart(): " + this);
        }
        super.onStart();
        mAppsLockActivityController.onStart();
    }

    @Override
    protected void onRestart() {
        if (DBG) {
            MLog.d(TAG, "onRestart(): " + this);
        }
        super.onRestart();
    }

    @Override
    public void onResume() {
        if (DBG) {
            MLog.d(TAG, "onResume(): " + this);
        }
        super.onResume();
        mAppsLockActivityController.onResume();
    }

    @Override
    public void onPause() {
        if (DBG) {
            MLog.d(TAG, "onPause(): " + this);
        }
        super.onPause();
        mAppsLockActivityController.onPause();
    }

    @Override
    public void onStop() {
        if (DBG) {
            MLog.d(TAG, "onStop(): " + this);
        }
        super.onStop();
        mAppsLockActivityController.onStop();
    }

    @Override
    public void onDestroy() {
        if (DBG) {
            MLog.d(TAG, "onDestroy(): " + this);
        }
        mAppsLockActivityController.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (DBG) {
            MLog.d(TAG, "onNewIntent():intent = " + intent + " " + this);
        }
        mAppsLockActivityController.onNewIntent(intent);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (DBG) {
            MLog.d(TAG, "dispatchKeyEvent():event = " + event);
        }
        if (mAppsLockActivityController.dispatchKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    @InjectOnClick({R.id.back_tv, R.id.appslock_entry_ibtn})
    public void onClick(View view) {
        if (DBG) {
            MLog.d(TAG, "onClick():view = " + view);
        }
        mAppsLockActivityController.onClick(view);
    }

    public Object onEvent(int what, Object[] object) {
        return mAppsLockActivityController.onEvent(what, object);
    }

}
