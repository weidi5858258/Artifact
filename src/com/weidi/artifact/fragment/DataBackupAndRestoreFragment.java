package com.weidi.artifact.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.weidi.artifact.R;
import com.weidi.artifact.controller.DataBackupAndRestoreFragmentController;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.inject.InjectOnClick;
import com.weidi.inject.InjectView;
import com.weidi.log.MLog;

/**
 * Created by root on 17-1-13.
 */

public class DataBackupAndRestoreFragment extends BaseFragment {

    private static final String TAG = "DataBackupAndRestoreFragment";
    private static final boolean DEBUG = true;

    @InjectView(R.id.data_phone_backup_btn)
    public Button data_phone_backup_btn;

    @InjectView(R.id.data_phone_restore_btn)
    public Button data_phone_restore_btn;

    @InjectView(R.id.data_sms_backup_btn)
    public Button data_sms_backup_btn;

    @InjectView(R.id.data_sms_restore_btn)
    public Button data_sms_restore_btn;

    @InjectView(R.id.loading)
    public View loading;

    private DataBackupAndRestoreFragmentController mDataBackupAndRestoreFragmentController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG)
            MLog.d(TAG, "onAttach(): activity = " + activity);
        mDataBackupAndRestoreFragmentController = new DataBackupAndRestoreFragmentController(this);
        mDataBackupAndRestoreFragmentController.beforeInitView();
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
        mDataBackupAndRestoreFragmentController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) MLog.d(TAG, "onPause()");
        mDataBackupAndRestoreFragmentController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG) MLog.d(TAG, "onStop()");
        mDataBackupAndRestoreFragmentController.onStop();
    }

    @Override
    public void onDestroyView() {
        if (DEBUG) MLog.d(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) MLog.d(TAG, "onDestroy()");
        mDataBackupAndRestoreFragmentController.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        if (DEBUG) MLog.d(TAG, "onDetach()");
        super.onDetach();
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    public Object onEvent(int what, Object object) {
        return null;
    }

    public DataBackupAndRestoreFragmentController getDataBackupAndRestoreFragmentController() {
        return mDataBackupAndRestoreFragmentController;
    }

    @InjectOnClick({R.id.data_phone_backup_btn, R.id.data_phone_restore_btn,
            R.id.data_sms_backup_btn, R.id.data_sms_restore_btn,
            R.id.settings_showusbdebug})
    public void onClick(View view) {
        mDataBackupAndRestoreFragmentController.onClick(view);
    }

    @Override
    protected int provideLayout() {
        return R.layout.data_backup_restore_view;
    }

    @Override
    protected void afterInitView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        mDataBackupAndRestoreFragmentController.afterInitView(inflater, container,
                savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (DEBUG) MLog.d(TAG, "onHiddenChanged():hidden = " + hidden);
        if (hidden) {
            mDataBackupAndRestoreFragmentController.onPause();
        } else {
            mDataBackupAndRestoreFragmentController.onResume();
        }
        super.onHiddenChanged(hidden);
    }

}
