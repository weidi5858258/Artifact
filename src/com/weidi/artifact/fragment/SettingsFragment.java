package com.weidi.artifact.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.controller.SettingsFragmentController;
import com.weidi.artifact.ui.CheckBoxItemAppLock;
import com.weidi.artifact.ui.CheckBoxItemBlacklist;
import com.weidi.artifact.ui.CheckBoxItemInstallPackage;
import com.weidi.artifact.ui.CheckBoxItemPeriodicalSerialKiller;
import com.weidi.artifact.ui.CheckBoxItemSdCardAndUsbDisk;
import com.weidi.artifact.ui.CheckBoxItemShowAttribution;
import com.weidi.artifact.ui.CheckBoxItemUninstallPackage;
import com.weidi.artifact.ui.CheckBoxItemUpdate;
import com.weidi.artifact.ui.CheckBoxItemUsbDebug;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.inject.InjectOnClick;
import com.weidi.inject.InjectView;
import com.weidi.log.Log;

/**
 * Created by root on 17-1-13.
 */

public class SettingsFragment extends BaseFragment {

    private static final String TAG = "SettingsFragment";
    private static final boolean DEBUG = true;

    @InjectView(R.id.settings_update)
    public CheckBoxItemUpdate settings_update;

    @InjectView(R.id.settings_showattribution)
    public CheckBoxItemShowAttribution settings_showattribution;

    @InjectView(R.id.settings_showinstallpackage)
    public CheckBoxItemInstallPackage settings_showinstallpackage;

    @InjectView(R.id.settings_showuninstallpackage)
    public CheckBoxItemUninstallPackage settings_showuninstallpackage;

    @InjectView(R.id.settings_showusbdebug)
    public CheckBoxItemUsbDebug settings_showusbdebug;

    @InjectView(R.id.settings_showsdcardandusbdisk)
    public CheckBoxItemSdCardAndUsbDisk settings_showsdcardandusbdisk;

    @InjectView(R.id.settings_blacklist)
    public CheckBoxItemBlacklist settings_blacklist;

    @InjectView(R.id.settings_periodicalserialkiller)
    public CheckBoxItemPeriodicalSerialKiller settings_periodicalserialkiller;

    @InjectView(R.id.settings_applock)
    public CheckBoxItemAppLock settings_applock;

    @InjectView(R.id.settings_applock_password)
    public TextView settings_applock_password;

    private SettingsFragmentController mSettingsFragmentController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG)
            Log.d(TAG, "onAttach(): activity = " + activity);
        mSettingsFragmentController = new SettingsFragmentController(this);
        mSettingsFragmentController.beforeInitView();
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
        mSettingsFragmentController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause()");
        mSettingsFragmentController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG) Log.d(TAG, "onStop()");
        mSettingsFragmentController.onStop();
    }

    @Override
    public void onDestroyView() {
        if (DEBUG) Log.d(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");
        mSettingsFragmentController.onDestroy();
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

    public SettingsFragmentController getSettingsFragmentController() {
        return mSettingsFragmentController;
    }

    @InjectOnClick({R.id.settings_update, R.id.settings_showattribution,
            R.id.settings_showinstallpackage,R.id.settings_showuninstallpackage,
            R.id.settings_showusbdebug, R.id.settings_showsdcardandusbdisk,
            R.id.settings_blacklist, R.id.settings_periodicalserialkiller,
            R.id.settings_applock, R.id.settings_applock_password})
    public void onClick(View view) {
        mSettingsFragmentController.onClick(view);
    }

    @Override
    protected int provideLayout() {
        return R.layout.settings_view;
    }

    @Override
    protected void afterInitView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        mSettingsFragmentController.afterInitView(inflater, container, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (DEBUG) Log.d(TAG, "onHiddenChanged():hidden = " + hidden);
        if (hidden) {
            mSettingsFragmentController.onPause();
        } else {
            mSettingsFragmentController.onResume();
        }
        super.onHiddenChanged(hidden);
    }

}
