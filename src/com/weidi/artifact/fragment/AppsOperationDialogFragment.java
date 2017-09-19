package com.weidi.artifact.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.controller.AppsOperationDialogFragmentController;
import com.weidi.artifact.fragment.base.BaseDialogFragment;
import com.weidi.inject.InjectOnClick;
import com.weidi.inject.InjectView;
import com.weidi.log.Log;

/**
 * Created by root on 17-1-13.
 */

public class AppsOperationDialogFragment extends BaseDialogFragment {

    private static final String TAG = "AppsOperationDialogFragment";
    private static final boolean DEBUG = true;
    private AppsOperationDialogFragmentController mAppsOperationDialogFragmentController;
    @InjectView(R.id.app_name_tv)
    public TextView app_name_tv;
    @InjectView(R.id.app_start_btn)
    public Button app_start_btn;
    @InjectView(R.id.app_delete_btn)
    public Button app_delete_btn;
    @InjectView(R.id.app_share_btn)
    public Button app_share_btn;
    @InjectView(R.id.app_lock_btn)
    public Button app_lock_btn;
    @InjectView(R.id.app_kill_btn)
    public Button app_kill_btn;
    @InjectView(R.id.app_close_btn)
    public Button app_close_btn;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG)
            Log.d(TAG, "onAttach(): activity = " + activity);
        mAppsOperationDialogFragmentController = new AppsOperationDialogFragmentController(this);
        mAppsOperationDialogFragmentController.beforeInitView();
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
        mAppsOperationDialogFragmentController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mAppsOperationDialogFragmentController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mAppsOperationDialogFragmentController.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAppsOperationDialogFragmentController.onDestroy();
    }

    @InjectOnClick({R.id.app_start_btn, R.id.app_delete_btn,
            R.id.app_share_btn, R.id.app_lock_btn,
            R.id.app_kill_btn, R.id.app_close_btn})
    public void onClick(View view) {
        mAppsOperationDialogFragmentController.onClick(view);
    }

    @Override
    protected int provideStyle() {
        return DialogFragment.STYLE_NO_TITLE;
    }

    @Override
    protected int provideLayout() {
        return R.layout.apps_operation_dialog;
    }

    @Override
    protected void afterInitView(LayoutInflater inflater,
                                 ViewGroup container,
                                 Bundle savedInstanceState) {
        mAppsOperationDialogFragmentController.afterInitView(
                inflater, container, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (DEBUG) Log.d(TAG, "onHiddenChanged():hidden = " + hidden);
        if (hidden) {
            mAppsOperationDialogFragmentController.onPause();
        } else {
            mAppsOperationDialogFragmentController.onResume();
        }
        super.onHiddenChanged(hidden);
    }

}
