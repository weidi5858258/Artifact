package com.weidi.artifact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.Window;

import com.weidi.artifact.R;
import com.weidi.activity.base.BaseActivity;
import com.weidi.artifact.controller.CameraActivityController;
import com.weidi.inject.InjectLayout;
import com.weidi.inject.InjectView;
import com.weidi.log.MLog;

//做一个没有焦点的弹出框
@InjectLayout(R.layout.activity_camera)
public class CameraActivity extends BaseActivity {

    private static final String TAG = "CameraActivity";
    private static final boolean DEBUG = true;
    private CameraActivityController mCameraActivityController;
    @InjectView(R.id.camera_surfaceview)
    public SurfaceView mCameraSurfaceview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if (DEBUG) MLog.d(TAG, "onCreate():savedInstanceState = " + savedInstanceState);
        mCameraActivityController = new CameraActivityController(this);
        mCameraActivityController.onCreate(savedInstanceState);
        //		// 全屏
        //		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //				             WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG) MLog.d(TAG, "onStart()");
        mCameraActivityController.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) MLog.d(TAG, "onResume()");
        mCameraActivityController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) MLog.d(TAG, "onPause()");
        mCameraActivityController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG) MLog.d(TAG, "onStop()");
        mCameraActivityController.onStop();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) MLog.d(TAG, "onDestroy()");
        mCameraActivityController.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
