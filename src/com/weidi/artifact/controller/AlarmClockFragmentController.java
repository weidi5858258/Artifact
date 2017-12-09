package com.weidi.artifact.controller;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weidi.artifact.R;
import com.weidi.artifact.activity.MainActivity;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.basecontroller.BaseFragmentController;
import com.weidi.artifact.fragment.AlarmClockFragment;
import com.weidi.artifact.service.AlarmClockService;
import com.weidi.log.Log;
import com.weidi.timepicker.TimePickerDialog;
import com.weidi.timepicker.data.Type;
import com.weidi.utils.EventBusUtils;
import com.weidi.utils.MyToast;
import com.weidi.utils.MyUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by root on 17-1-13.
 */

public class AlarmClockFragmentController extends BaseFragmentController {

    private static final String TAG = "AlarmClockFragmentController";
    private static final boolean DEBUG = false;
    private AlarmClockFragment mAlarmClockFragment;
    private TimePickerDialog mTimePickerDialog;
    private SimpleDateFormat mSimpleDateFormat1 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    // private SimpleDateFormat mSimpleDateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private long mDifferTime;
    private SharedPreferences mSharedPreferences;

    public AlarmClockFragmentController(Fragment fragment) {
        super(fragment.getActivity());
        mAlarmClockFragment = (AlarmClockFragment) fragment;
        mSharedPreferences = mContext.getSharedPreferences(
                Constant.APP_CONFIG, Context.MODE_PRIVATE);
    }

    @Override
    public void beforeInitView() {
        if (DEBUG) Log.d(TAG, "beforeInitView()");
    }

    public void afterInitView(LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "afterInitView():savedInstanceState = " + savedInstanceState);
        if (savedInstanceState == null) {
            init();
        }
    }

    @Override
    public void onResume() {
        if (DEBUG) Log.d(TAG, "onResume()");
        ((MainActivity) mAlarmClockFragment.getActivity()).title.setText("设置闹钟");
        if (MyUtils.isSpecificServiceAlive(
                mContext, "com.weidi.artifact.service.AlarmClockService")) {
            mAlarmClockFragment.start_stop_btn.setText("停\t\t\t\t\t\t止");
            String alarmTime = mSharedPreferences.getString(Constant.ALARMTIME, "");
            showAlarmTime(alarmTime);
        } else {
            hideAlarmTime();
            hidePastTimes();
            mAlarmClockFragment.start_stop_btn.setText("开\t\t\t\t\t\t始");
        }
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        if (DEBUG) Log.d(TAG, "onStop()");
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");
        EventBusUtils.unregister(mAlarmClockFragment);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_picker_btn:
                mTimePickerDialog.show(mMainActivity.getFragmentManager(), "all");
                break;

            case R.id.start_stop_btn:
                if (MyUtils.isSpecificServiceAlive(
                        mContext, "com.weidi.artifact.service.AlarmClockService")) {
                    EventBusUtils.postSync(
                            AlarmClockService.class, Constant.STOP_ALARMCLOCKSERVICE, null);
                    if (!MyUtils.isSpecificServiceAlive(
                            mContext, "com.weidi.artifact.service.AlarmClockService")) {
                        mAlarmClockFragment.start_stop_btn.setText("开\t\t\t\t\t\t始");
                    }

                } else {
                    if (mDifferTime <= 0) {
                        MyToast.show("设置的时间无效");
                        return;
                    }
                    Intent intent = new Intent(mMainActivity, AlarmClockService.class);
                    intent.putExtra(Constant.DIFFERTIME, mDifferTime);
                    mMainActivity.startService(intent);
                    if (MyUtils.isSpecificServiceAlive(
                            mContext, "com.weidi.artifact.service.AlarmClockService")) {
                        mAlarmClockFragment.start_stop_btn.setText("停\t\t\t\t\t\t止");
                    }

                }
                break;

            default:
        }
    }

    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        long currentTime = System.currentTimeMillis();
        Date date = new Date(millseconds);
        String alarmTime = mSimpleDateFormat1.format(date);
        showAlarmTime(alarmTime);
        mSharedPreferences.edit().putString(Constant.ALARMTIME, alarmTime).commit();
        // mDifferTime = millseconds - currentTime;
        mDifferTime = millseconds;
    }

    public Object onEvent(int what, Object object) {
        switch (what) {
            case Constant.ALARMCLOCKSERVICE_IS_STOPPED:
                hideAlarmTime();
                hidePastTimes();
                mAlarmClockFragment.start_stop_btn.setText("开\t\t\t\t\t\t始");
                mDifferTime = 0;
                break;

            case Constant.TRANSPORT_TIME:
                showPastTimes((String) object);
                break;

            case Constant.TRANSPORT_TIME_COMPLETE:
                hidePastTimes();
                break;

            default:
        }
        return null;
    }

    //初始化
    private void init() {
        EventBusUtils.register(mAlarmClockFragment);
        long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
        mTimePickerDialog = new TimePickerDialog.Builder()
                .setCallBack(mAlarmClockFragment)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("选择时间")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(mContext.getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.ALL)
                .setWheelItemTextNormalColor(
                        mContext.getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(
                        mContext.getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(12)
                .build();

    }

    private void showPastTimes(String remainingTime) {
        mAlarmClockFragment.alarm_past_times_tc.setVisibility(View.VISIBLE);
        mAlarmClockFragment.alarm_past_times_tc.setText(remainingTime);
    }

    private void hidePastTimes() {
        mAlarmClockFragment.alarm_past_times_tc.setVisibility(View.GONE);
    }

    private void showAlarmTime(String alarmTime) {
        mAlarmClockFragment.alarm_time_tv.setVisibility(View.VISIBLE);
        mAlarmClockFragment.alarm_time_tv.setText(alarmTime);
    }

    private void hideAlarmTime() {
        mAlarmClockFragment.alarm_time_tv.setVisibility(View.GONE);
    }

}
