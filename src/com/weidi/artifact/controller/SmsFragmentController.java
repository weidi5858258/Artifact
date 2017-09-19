package com.weidi.artifact.controller;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weidi.artifact.R;
import com.weidi.artifact.activity.MainActivity;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.basecontroller.BaseFragmentController;
import com.weidi.artifact.fragment.SmsFragment;
import com.weidi.artifact.modle.Sms;
import com.weidi.customadapter.CustomRecyclerViewAdapter;
import com.weidi.customadapter.CustomViewHolder;
import com.weidi.customadapter.listener.OnItemClickListener;
import com.weidi.dbutil.SimpleDao;
import com.weidi.log.Log;
import com.weidi.threadpool.CustomRunnable;
import com.weidi.threadpool.ThreadPool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by root on 17-1-13.
 */

public class SmsFragmentController extends BaseFragmentController {

    private static final String TAG = "SmsFragmentController";
    private static final boolean DEBUG = false;
    private SmsFragment mSmsFragment;
    private ArrayList<Sms> mSmsList = new ArrayList<Sms>();
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");

    public SmsFragmentController(Fragment fragment) {
        super(fragment.getActivity());
        mSmsFragment = (SmsFragment) fragment;
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
    }

    @Override
    public void beforeInitView() {
        if (DEBUG) Log.d(TAG, "beforeInitView()");
        mSmsList.clear();
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
        ((MainActivity) mSmsFragment.getActivity()).title.setText("短信");
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
    }

    public void onClick(View view) {

    }

    //初始化
    private void init() {
        ThreadPool.getCachedThreadPool().execute(
                new CustomRunnable().setCallBack(new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {
                        showLoading();
                    }

                    @Override
                    public Object running() {
                        return SimpleDao.getInstance().queryAll(Sms.class, "date", true);
                    }

                    @Override
                    public void onProgressUpdate(Object object) {

                    }

                    @Override
                    public void runAfter(Object object) {
                        hideLoading();
                        if (object == null) {
                            return;
                        }
                        mSmsList = (ArrayList<Sms>) object;
                        SmsFragmentAdapter smsFragmentAdapter = new SmsFragmentAdapter(
                                mContext, mSmsList, R.layout.sms_view_item);
                        smsFragmentAdapter.setOnItemClickListener(mOnItemClickListener);
                        mSmsFragment.smslist_recycleview.setLayoutManager(
                                new LinearLayoutManager(mContext));
                        mSmsFragment.smslist_recycleview.setAdapter(smsFragmentAdapter);
                    }

                    @Override
                    public void runError() {
                        hideLoading();
                    }
                }));
    }

    private void showLoading() {
        mSmsFragment.loading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mSmsFragment.loading.setVisibility(View.GONE);
    }

    private class SmsFragmentAdapter extends CustomRecyclerViewAdapter<Sms> {

        public SmsFragmentAdapter(Context context, List items, int layoutResId) {
            super(context, items, layoutResId);
        }

        @Override
        public void onBind(CustomViewHolder customViewHolder,
                           int viewType,
                           int layoutPosition,
                           Sms sms) {
            customViewHolder.setText(R.id.tv_sms_phone, sms.address);
            customViewHolder.setText(
                    R.id.tv_sms_time, mSimpleDateFormat.format(new Date(sms.date)));
            customViewHolder.setText(R.id.tv_sms_body, sms.body);
        }
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View itemView, int viewType, int position) {

        }
    };

}
