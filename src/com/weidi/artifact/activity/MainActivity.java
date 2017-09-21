package com.weidi.artifact.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.activity.base.BaseActivity;
import com.weidi.artifact.controller.MainActivityController;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.inject.InjectLayout;
import com.weidi.inject.InjectView;
import com.weidi.log.Log;

@InjectLayout(R.layout.activity_main)
public class MainActivity extends BaseActivity implements BaseFragment.BackHandlerInterface {

    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = true;

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
        if (DEBUG) Log.d(TAG, "onCreate():savedInstanceState = " + savedInstanceState);
        mMainActivityController = new MainActivityController(this);
        super.onCreate(savedInstanceState);
        mMainActivityController.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG) Log.d(TAG, "onStart()");
        mMainActivityController.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (DEBUG) Log.d(TAG, "onRestart()");
        mMainActivityController.onRestart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) Log.d(TAG, "onResume()");
        mMainActivityController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause()");
        mMainActivityController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG) Log.d(TAG, "onStop()");
        mMainActivityController.onStop();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");
        mMainActivityController.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (DEBUG) Log.d(TAG, "onBackPressed()");
        mMainActivityController.onBackPressed();
        // super.onBackPressed();
    }

    public Object onEvent(int what, Object object) {
        return mMainActivityController.onEvent(what, object);
    }

    @Override
    public String[] getRequiredPermissions() {
        return new String[0];
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DEBUG)
            Log.d(TAG, "onActivityResult():requestCode = " + requestCode +
                    " resultCode = " + resultCode +
                    " data = " + data);
        // super.onActivityResult(requestCode, resultCode, data);
        mMainActivityController.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (DEBUG)
            Log.d(TAG, "onSaveInstanceState():outState = " + outState);
        mMainActivityController.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (DEBUG)
            Log.d(TAG, "onRestoreInstanceState():savedInstanceState = " + savedInstanceState);
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
            Log.d(TAG, "onConfigurationChanged():newConfig = " + newConfig);
        mMainActivityController.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void setSelectedFragment(BaseFragment selectedFragment, String fragmentTag) {
        mMainActivityController.setSelectedFragment(selectedFragment, fragmentTag);
    }

    public MainActivityController getMainActivityController() {
        return mMainActivityController;
    }

}
