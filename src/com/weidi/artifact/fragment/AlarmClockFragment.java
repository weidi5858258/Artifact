package com.weidi.artifact.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.controller.AlarmClockFragmentController;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.inject.InjectOnClick;
import com.weidi.inject.InjectView;
import com.weidi.log.Log;
import com.weidi.timepicker.TimePickerDialog;
import com.weidi.timepicker.listener.OnDateSetListener;

/**
 * Created by root on 17-1-13.
 */

public class AlarmClockFragment extends BaseFragment
        implements
        View.OnClickListener,
        OnDateSetListener {

    private static final String TAG = "AlarmClockFragment";
    private static final boolean DEBUG = true;

    @InjectView(R.id.alarm_time_tv)
    public TextView alarm_time_tv;

    @InjectView(R.id.alarm_past_times_tc)
    public TextView alarm_past_times_tc;

    @InjectView(R.id.time_picker_btn)
    public Button time_picker_btn;

    @InjectView(R.id.start_stop_btn)
    public Button start_stop_btn;

    private AlarmClockFragmentController mAlarmClockFragmentController;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (DEBUG)
            Log.d(TAG, "onAttach(): activity = " + activity);
        mAlarmClockFragmentController = new AlarmClockFragmentController(this);
        mAlarmClockFragmentController.beforeInitView();
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
        mAlarmClockFragmentController.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (DEBUG) Log.d(TAG, "onPause()");
        mAlarmClockFragmentController.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (DEBUG) Log.d(TAG, "onStop()");
        mAlarmClockFragmentController.onStop();
    }

    @Override
    public void onDestroyView() {
        if (DEBUG) Log.d(TAG, "onDestroyView()");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");
        mAlarmClockFragmentController.onDestroy();
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

    public AlarmClockFragmentController getAlarmClockFragmentController() {
        return mAlarmClockFragmentController;
    }

    @InjectOnClick({R.id.time_picker_btn, R.id.start_stop_btn})
    public void onClick(View view) {
        mAlarmClockFragmentController.onClick(view);
    }

    @Override
    public int provideLayout() {
        return R.layout.fragment_alarmclock;
    }

    @Override
    public void afterInitView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        mAlarmClockFragmentController.afterInitView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        mAlarmClockFragmentController.onDateSet(timePickerView, millseconds);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (DEBUG) Log.d(TAG, "onHiddenChanged():hidden = " + hidden);
        if (hidden) {
            mAlarmClockFragmentController.onPause();
        } else {
            mAlarmClockFragmentController.onResume();
        }
        super.onHiddenChanged(hidden);
    }

}
