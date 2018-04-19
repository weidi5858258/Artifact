package com.weidi.artifact.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weidi.activity.base.BaseActivity;
import com.weidi.artifact.R;
import com.weidi.artifact.controller.MainActivityController;
import com.weidi.inject.InjectLayout;
import com.weidi.inject.InjectView;
import com.weidi.log.MLog;

@InjectLayout(R.layout.activity_main)
public class MainActivity extends BaseActivity
//        implements BaseFragment.BackHandlerInterface
{

    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = false;

    @InjectView(R.id.drawerlayout)
    public DrawerLayout mDrawerLayout;
    @InjectView(R.id.main_activity_layout)
    public LinearLayout mMainActivityLayout;
    @InjectView(R.id.title)
    public TextView title;
    /*@InjectView(R.id.container)
    public View fragment_container;*/

    public MainActivityController mMainActivityController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) MLog.d(TAG, "onCreate():savedInstanceState = " + savedInstanceState);
        mMainActivityController = new MainActivityController(this);
        super.onCreate(savedInstanceState);
        mMainActivityController.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG) MLog.d(TAG, "onStart()");
        mMainActivityController.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (DEBUG) MLog.d(TAG, "onRestart()");
        mMainActivityController.onRestart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) MLog.d(TAG, "onResume()");
        mMainActivityController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) MLog.d(TAG, "onPause()");
        mMainActivityController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG) MLog.d(TAG, "onStop()");
        mMainActivityController.onStop();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) MLog.d(TAG, "onDestroy()");
        mMainActivityController.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (DEBUG) MLog.d(TAG, "onBackPressed()");
        // super.onBackPressed();
    }

    public Object onEvent(int what, Object[] object) {
        return mMainActivityController.onEvent(what, object);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DEBUG)
            MLog.d(TAG, "onActivityResult():requestCode = " + requestCode +
                    " resultCode = " + resultCode +
                    " data = " + data);
        // super.onActivityResult(requestCode, resultCode, data);
        mMainActivityController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (DEBUG)
            MLog.d(TAG, "onSaveInstanceState():outState = " + outState);
        mMainActivityController.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (DEBUG)
            MLog.d(TAG, "onRestoreInstanceState():savedInstanceState = " + savedInstanceState);
        mMainActivityController.onRestoreInstanceState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /***
     * 当配置发生变化时，不会重新启动Activity
     * 但是会回调此方法，用户自行进行对屏幕旋转后进行处理
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (DEBUG)
            MLog.d(TAG, "onConfigurationChanged():newConfig = " + newConfig);
        mMainActivityController.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    public MainActivityController getMainActivityController() {
        return mMainActivityController;
    }

}
