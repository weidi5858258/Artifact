package com.weidi.artifact.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weidi.artifact.R;
import com.weidi.artifact.controller.ProcessManagerFragmentController;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.log.Log;

/**
 * Created by root on 17-1-13.
 */

public class ProcessManagerFragment extends BaseFragment {

    private ProcessManagerFragmentController mProcessManagerFragmentController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mProcessManagerFragmentController = new ProcessManagerFragmentController(this);
        mProcessManagerFragmentController.beforeInitView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        mProcessManagerFragmentController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mProcessManagerFragmentController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
//        mProcessManagerFragmentController.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mProcessManagerFragmentController.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public Object onEvent(int what, Object object) {
        return null;
    }

    public ProcessManagerFragmentController getProcessManagerFragmentController(){
        return mProcessManagerFragmentController;
    }

    @Override
    protected int provideLayout() {
        return R.layout.activity_processesmanager;
    }

    @Override
    protected void afterInitView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        mProcessManagerFragmentController.afterInitView(inflater, container, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        // if (DEBUG) Log.d(TAG, "onHiddenChanged():hidden = " + hidden);
        if (hidden) {
            mProcessManagerFragmentController.onPause();
        } else {
            mProcessManagerFragmentController.onResume();
        }
        super.onHiddenChanged(hidden);
    }

}
