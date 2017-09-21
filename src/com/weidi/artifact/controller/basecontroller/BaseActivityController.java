package com.weidi.artifact.controller.basecontroller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weidi.artifact.activity.MainActivity;
import com.weidi.artifact.controller.ILifeCycle;

/**
 * Created by root on 16-12-13.
 */

public abstract class BaseActivityController {

//    protected Activity mActivity;
    protected MainActivity mMainActivity;
    protected Context mContext;

    public BaseActivityController(Activity activity) {
        if (activity == null) {
            throw new NullPointerException("BaseActivityController's activity is null.");
        }
//        mActivity = activity;
        if (activity instanceof MainActivity) {
            mMainActivity = (MainActivity) activity;
        }
        mContext = activity.getApplicationContext();
    }

    public abstract void onCreate(Bundle savedInstanceState);

    public abstract void onStart();

    public abstract void onRestart();

    public abstract void onResume();

    public abstract void onPause();

    public abstract void onStop();

    public abstract void onDestroy();

    public abstract Object onEvent(int what, Object object);

}
