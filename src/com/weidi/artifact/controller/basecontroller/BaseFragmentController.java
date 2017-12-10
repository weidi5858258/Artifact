package com.weidi.artifact.controller.basecontroller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.weidi.activity.base.BaseActivity;
import com.weidi.artifact.activity.MainActivity;

/**
 * Created by root on 16-12-13.
 */

public abstract class BaseFragmentController { // implements ILifeCycle

    //    protected Activity mActivity;
    protected BaseActivity mBaseActivity;
    protected Context mContext;

    public BaseFragmentController(Activity activity) {
        if (activity == null) {
            throw new NullPointerException("BaseActivityController's activity is null.");
        }
        //        mActivity = activity;
        if (activity instanceof BaseActivity) {
            mBaseActivity = (BaseActivity) activity;
            if (mBaseActivity == null) {
                throw new NullPointerException("BaseFragmentController's mBaseActivity is null.");
            }
        }
        mContext = activity.getApplicationContext();
    }

    // 由Fragment的onCreate()方法触发
    public abstract void beforeInitView();

    // 由Fragment的onCreateView()方法触发
    public abstract void afterInitView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState);

    public abstract void onResume();

    public abstract void onPause();

    public abstract void onStop();

    public abstract void onDestroy();

    // 第一次由FragOperManager中的enterFragment()方法中触发,
    // 第二次由FragOperManager中的exitFragment()方法中触发
    // 进入Fragment或者退出一个Fragment进入上一个Fragment时,可以把这个方法当成是onResume()方法
    //    public abstract void onShow();

}
