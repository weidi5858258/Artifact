package com.weidi.artifact.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.controller.BluetoothFragmentController;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.inject.InjectOnClick;
import com.weidi.inject.InjectView;
import com.weidi.log.MLog;

/**
 * Created by root on 17-1-13.
 */

public class BluetoothFragment extends BaseFragment {

    private static final String TAG = "SettingsFragment";
    private static final boolean DEBUG = true;

    @InjectView(R.id.bt_switch)
    public Switch bt_switch;

    @InjectView(R.id.bt_address_tv)
    public TextView bt_address_tv;

    @InjectView(R.id.as_service_btn)
    public Button as_service_btn;
    @InjectView(R.id.search_device_btn)
    public Button searth_device_btn;
    @InjectView(R.id.be_searched_btn)
    public Button be_searched_btn;
    @InjectView(R.id.cancel_search_btn)
    public Button cancel_search_btn;
    @InjectView(R.id.reset_btn)
    public Button reset_btn;
    @InjectView(R.id.disconnect_btn)
    public Button disconnect_btn;
    @InjectView(R.id.pair_btn)
    public Button pair_btn;
    @InjectView(R.id.disconnect_pair_btn)
    public Button disconnect_pair_btn;
    @InjectView(R.id.cancel_pair_btn)
    public Button cancel_pair_btn;
    @InjectView(R.id.check_state_btn)
    public Button check_state_btn;
    @InjectView(R.id.connect_btn)
    public Button connect_btn;
    @InjectView(R.id.chat_btn)
    public Button chat_btn;
    @InjectView(R.id.input_btn)
    public Button input_btn;
    @InjectView(R.id.cs_btn)
    public Button cs_btn;

    // test
    @InjectView(R.id.test1_btn)
    public Button test1_btn;
    @InjectView(R.id.test2_btn)
    public Button test2_btn;
    @InjectView(R.id.test3_btn)
    public Button test3_btn;
    @InjectView(R.id.test4_btn)
    public Button test4_btn;

    @InjectView(R.id.process_bar)
    public ProgressBar process_bar;
    @InjectView(R.id.bt_status_text)
    public TextView bt_status_text;
    @InjectView(R.id.bluetooth_device_recyclerview)
    public RecyclerView bluetooth_device_recyclerview;

    private BluetoothFragmentController mBluetoothFragmentController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG)
            MLog.d(TAG, "onAttach(): activity = " + activity);
        mBluetoothFragmentController = new BluetoothFragmentController(this);
        mBluetoothFragmentController.beforeInitView();
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
        mBluetoothFragmentController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) MLog.d(TAG, "onPause()");
        mBluetoothFragmentController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG) MLog.d(TAG, "onStop()");
        mBluetoothFragmentController.onStop();
    }

    @Override
    public void onDestroyView() {
        if (DEBUG) MLog.d(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) MLog.d(TAG, "onDestroy()");
        mBluetoothFragmentController.onDestroy();
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


    @InjectOnClick({R.id.as_service_btn, R.id.search_device_btn, R.id.be_searched_btn,
            R.id.cancel_search_btn, R.id.reset_btn, R.id.disconnect_btn,
            R.id.pair_btn, R.id.disconnect_pair_btn, R.id.cancel_pair_btn,
            R.id.check_state_btn, R.id.connect_btn, R.id.chat_btn,
            R.id.input_btn, R.id.cs_btn,
            R.id.test1_btn, R.id.test2_btn, R.id.test3_btn, R.id.test4_btn})
    public void onClick(View view) {
        mBluetoothFragmentController.onClick(view);
    }

    @Override
    protected int provideLayout() {
        return R.layout.fragment_bluetooth;
    }

    @Override
    protected void afterInitView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        mBluetoothFragmentController.afterInitView(inflater, container, savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (DEBUG) MLog.d(TAG, "onHiddenChanged():hidden = " + hidden);
        if (hidden) {
            mBluetoothFragmentController.onPause();
        } else {
            mBluetoothFragmentController.onResume();
        }
        super.onHiddenChanged(hidden);
    }

}
