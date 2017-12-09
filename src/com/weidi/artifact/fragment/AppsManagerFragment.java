package com.weidi.artifact.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.controller.AppsManagerFragmentController;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.inject.InjectView;
import com.weidi.log.Log;

/**
 * Created by root on 17-1-13.
 */

public class AppsManagerFragment extends BaseFragment {

    private static final String TAG = "AppsManagerFragment";
    private static final boolean DEBUG = true;
    private AppsManagerFragmentController mAppsManagerFragmentController;
    @InjectView(R.id.rom_available_tv)
    public TextView rom_available_tv;
    @InjectView(R.id.sd_available_tv)
    public TextView sd_available_tv;
    @InjectView(R.id.user_system_app_counts_tv)
    public TextView user_system_app_counts_tv;
    @InjectView(R.id.recyclerview)
    public RecyclerView appsinfo_recyclerview;
    @InjectView(R.id.loading)
    public View loading;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG)
            Log.d(TAG, "onAttach(): activity = " + activity);
        mAppsManagerFragmentController = new AppsManagerFragmentController(this);
        mAppsManagerFragmentController.beforeInitView();
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
        mAppsManagerFragmentController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause()");
        mAppsManagerFragmentController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG) Log.d(TAG, "onStop()");
        mAppsManagerFragmentController.onStop();
    }

    @Override
    public void onDestroyView() {
        if (DEBUG) Log.d(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");
        mAppsManagerFragmentController.onDestroy();
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


    public AppsManagerFragmentController getAppsManagerFragmentController() {
        return mAppsManagerFragmentController;
    }

    @Override
    protected int provideLayout() {
        return R.layout.fragment_appsmanager;
    }

    @Override
    protected void afterInitView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        mAppsManagerFragmentController.afterInitView(inflater, container, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (DEBUG) Log.d(TAG, "onHiddenChanged():hidden = " + hidden);
        if (hidden) {
            mAppsManagerFragmentController.onPause();
        } else {
            mAppsManagerFragmentController.onResume();
        }
        super.onHiddenChanged(hidden);
    }

}
