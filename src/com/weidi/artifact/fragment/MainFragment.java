package com.weidi.artifact.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weidi.artifact.R;
import com.weidi.artifact.controller.MainFragmentController;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.inject.InjectView;
import com.weidi.log.MLog;

/**
 * Created by root on 17-1-13.
 */

public class MainFragment extends BaseFragment {

    private static final String TAG = "MainFragment";
    private static final boolean DEBUG = false;

    @InjectView(R.id.recyclerview)
    public RecyclerView factionlist_recycleview;

    private MainFragmentController mMainFragmentController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG)
            MLog.d(TAG, "onAttach(): activity = " + activity);
        mMainFragmentController = new MainFragmentController(this);
        mMainFragmentController.beforeInitView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) MLog.d(TAG, "onCreate():savedInstanceState = " + savedInstanceState);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        if (DEBUG) MLog.d(TAG, "onCreateView():savedInstanceState = " + savedInstanceState);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (DEBUG)
            MLog.d(TAG, "onActivityCreated(): savedInstanceState = " + savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG)
            MLog.d(TAG, "onStart()");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG) MLog.d(TAG, "onResume()");
        mMainFragmentController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) MLog.d(TAG, "onPause()");
        mMainFragmentController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG) MLog.d(TAG, "onStop()");
        mMainFragmentController.onStop();
    }

    @Override
    public void onDestroyView() {
        if (DEBUG) MLog.d(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) MLog.d(TAG, "onDestroy()");
        mMainFragmentController.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if (DEBUG) MLog.d(TAG, "onDetach()");
        super.onDetach();
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    public MainFragmentController getMainFragmentController() {
        return mMainFragmentController;
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
        mMainFragmentController.afterInitView(inflater, container, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (DEBUG) MLog.d(TAG, "onHiddenChanged():hidden = " + hidden);
        if (hidden) {
            mMainFragmentController.onPause();
        } else {
            mMainFragmentController.onResume();
        }
        super.onHiddenChanged(hidden);
    }


}
