package com.weidi.artifact.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weidi.artifact.R;
import com.weidi.artifact.controller.SmsFragmentController;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.inject.InjectOnClick;
import com.weidi.inject.InjectView;
import com.weidi.log.Log;

/**
 * Created by root on 17-1-13.
 */

public class SmsFragment extends BaseFragment {

    private static final String TAG = "SmsFragment";
    private static final boolean DEBUG = true;

    @InjectView(R.id.recyclerview)
    public RecyclerView smslist_recycleview;

    @InjectView(R.id.loading)
    public View loading;

    private SmsFragmentController mSmsFragmentController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG)
            Log.d(TAG, "onAttach(): activity = " + activity);
        mSmsFragmentController = new SmsFragmentController(this);
        mSmsFragmentController.beforeInitView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.d(TAG, "onCreate():savedInstanceState = " + savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "onCreateView():savedInstanceState = " + savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (DEBUG)
            Log.d(TAG, "onActivityCreated(): savedInstanceState = " + savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG)
            Log.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) Log.d(TAG, "onResume()");
        mSmsFragmentController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause()");
        mSmsFragmentController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG) Log.d(TAG, "onStop()");
        mSmsFragmentController.onStop();
    }

    @Override
    public void onDestroyView() {
        if (DEBUG) Log.d(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");
        mSmsFragmentController.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if (DEBUG) Log.d(TAG, "onDetach()");
        super.onDetach();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


    public SmsFragmentController getSmsFragmentController() {
        return mSmsFragmentController;
    }

    @InjectOnClick({R.id.data_sms_backup_btn, R.id.data_sms_restore_btn})
    public void onClick(View view) {
        mSmsFragmentController.onClick(view);
    }

    @Override
    protected int provideLayout() {
        return R.layout.recycler_view;
    }

    @Override
    protected void afterInitView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        mSmsFragmentController.afterInitView(inflater, container, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (DEBUG) Log.d(TAG, "onHiddenChanged():hidden = " + hidden);
        if (hidden) {
            mSmsFragmentController.onPause();
        } else {
            mSmsFragmentController.onResume();
        }
        super.onHiddenChanged(hidden);
    }

}
