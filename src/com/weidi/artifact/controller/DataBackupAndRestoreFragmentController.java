package com.weidi.artifact.controller;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weidi.artifact.R;
import com.weidi.artifact.activity.MainActivity;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.basecontroller.BaseFragmentController;
import com.weidi.artifact.fragment.DataBackupAndRestoreFragment;
import com.weidi.artifact.fragment.QrCodeFragment;
import com.weidi.artifact.modle.Contacts;
import com.weidi.artifact.modle.Data;
import com.weidi.artifact.modle.MimeTypes;
import com.weidi.artifact.modle.RawContacts;
import com.weidi.artifact.modle.Sms;
import com.weidi.dbutil.SimpleDao;
import com.weidi.fragment.FragOperManager;
import com.weidi.log.Log;
import com.weidi.threadpool.CustomRunnable;
import com.weidi.threadpool.ThreadPool;

/**
 * Created by root on 17-1-13.
 */

public class DataBackupAndRestoreFragmentController extends BaseFragmentController {

    private static final String TAG = "DataBackupAndRestoreFragmentController";
    private static final boolean DEBUG = false;
    private DataBackupAndRestoreFragment mDataBackupAndRestoreFragment;

    public DataBackupAndRestoreFragmentController(Fragment fragment) {
        super(fragment.getActivity());
        mDataBackupAndRestoreFragment = (DataBackupAndRestoreFragment) fragment;
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
        // ((MainActivity) mDataBackupAndRestoreFragment.getActivity()).title.setText("数据备份与恢复");
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
        switch (view.getId()) {
            case R.id.data_phone_backup_btn:
                dataPhoneBackup();
                break;

            case R.id.data_phone_restore_btn:
                dataPhoneRestore();
                break;

            case R.id.data_sms_backup_btn:
                dataSmsBackup();
                break;

            case R.id.data_sms_restore_btn:
                dataSmsRestore();
                break;

            case R.id.settings_showusbdebug:
                FragOperManager.getInstance().enter(mBaseActivity, new QrCodeFragment(), null);
                break;

            default:
                break;
        }
    }

    //初始化
    private void init() {

    }

    private void showLoading() {
        mDataBackupAndRestoreFragment.loading.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        mDataBackupAndRestoreFragment.loading.setVisibility(View.GONE);
    }

    private void dataPhoneBackup() {
        ThreadPool.getFixedThreadPool().execute(
                new CustomRunnable().setCallBack(new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {
                        mDataBackupAndRestoreFragment.data_phone_backup_btn
                                .setClickable(false);
                        showLoading();
                    }

                    @Override
                    public Object running() {
                        long startTime = SystemClock.uptimeMillis();
                        Log.d(TAG, "start time : " + startTime);
                        Cursor phoneCursor = mContext.getContentResolver().query(
                                Uri.parse(Constant.PHONE_URI), null, null, null, null);
                        Cursor rawContactsCursor = mContext.getContentResolver().query(
                                Uri.parse(Constant.RAW_CONTACTS_URI), null, null, null, null);
                        Cursor dataCursor = mContext.getContentResolver().query(
                                Uri.parse(Constant.DATA_URI), null, null, null, null);
//                        Cursor mimeTypesCursor = mContext.getContentResolver().query(
//                                Uri.parse(Constant.MIMETYPES_URI), null, null, null, null);
//                        Cursor mimeTypesCursor = mContext.getContentResolver().query(
//                                Uri.parse(ContactsContract.Data.CONTENT_TYPE), null, null, null, null);
//                        SimpleDao.getInstance().copyData(Contacts.class, phoneCursor);
//                        SimpleDao.getInstance().copyData(RawContacts.class, rawContactsCursor);
//                        SimpleDao.getInstance().copyData(Data.class, dataCursor);
//                        SimpleDao.getInstance().copyData(MimeTypes.class, mimeTypesCursor);
                        long endTime = SystemClock.uptimeMillis();
                        Log.d(TAG, "start time : " + endTime);
                        Log.d(TAG, "take time : " + (endTime - startTime));
                        return null;
                    }

                    @Override
                    public void onProgressUpdate(Object object) {

                    }

                    @Override
                    public void runAfter(Object object) {
                        mDataBackupAndRestoreFragment.data_phone_backup_btn
                                .setClickable(true);
                        hideLoading();
                    }

                    @Override
                    public void runError() {
                        mDataBackupAndRestoreFragment.data_phone_backup_btn
                                .setClickable(true);
                        hideLoading();
                    }
                }));
    }

    private void dataPhoneRestore() {
        ThreadPool.getFixedThreadPool().execute(
                new CustomRunnable().setCallBack(new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {
                        mDataBackupAndRestoreFragment.data_phone_restore_btn
                                .setClickable(false);
                        showLoading();
                    }

                    @Override
                    public Object running() {
                        long startTime = SystemClock.uptimeMillis();
                        Log.d(TAG, "start time : " + startTime);
                        SimpleDao.getInstance().restoreData(Contacts.class);
                        SimpleDao.getInstance().restoreData(RawContacts.class);
                        SimpleDao.getInstance().restoreData(Data.class);
//                        SimpleDao.getInstance().restoreData(MimeTypes.class);
                        long endTime = SystemClock.uptimeMillis();
                        Log.d(TAG, "start time : " + endTime);
                        Log.d(TAG, "take time : " + (endTime - startTime));
                        return null;
                    }

                    @Override
                    public void onProgressUpdate(Object object) {

                    }

                    @Override
                    public void runAfter(Object object) {
                        mDataBackupAndRestoreFragment.data_phone_restore_btn
                                .setClickable(true);
                        hideLoading();
                    }

                    @Override
                    public void runError() {
                        mDataBackupAndRestoreFragment.data_phone_restore_btn
                                .setClickable(true);
                        hideLoading();
                    }
                }));
    }

    private void dataSmsBackup() {
        ThreadPool.getFixedThreadPool(Constant.FIXEDTHREADPOOLCOUNT).execute(
                new CustomRunnable().setCallBack(new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {
                        mDataBackupAndRestoreFragment.data_sms_backup_btn
                                .setClickable(false);
                        showLoading();
                    }

                    @Override
                    public Object running() {
                        // 4801条数据 花费了366450ms
                        // 4847条数据 花费了95137ms(primaryKey:_id) 98565 98175 99064 97394
                        // 95752 168593
                        long startTime = SystemClock.uptimeMillis();
                        Log.d(TAG, "start time : " + startTime);
                        Cursor cursor = mContext.getContentResolver().query(
                                Uri.parse(Constant.SMS_URI), null, null, null, null);
                        SimpleDao.getInstance().copyData(Sms.class, cursor);
                        long endTime = SystemClock.uptimeMillis();
                        Log.d(TAG, "start time : " + endTime);
                        Log.d(TAG, "take time : " + (endTime - startTime));
                        return null;
                    }

                    @Override
                    public void onProgressUpdate(Object object) {

                    }

                    @Override
                    public void runAfter(Object object) {
                        mDataBackupAndRestoreFragment.data_sms_backup_btn
                                .setClickable(true);
                        hideLoading();
                    }

                    @Override
                    public void runError() {
                        mDataBackupAndRestoreFragment.data_sms_backup_btn
                                .setClickable(true);
                        hideLoading();
                    }
                }));
    }

    private void dataSmsRestore() {
        ThreadPool.getFixedThreadPool(Constant.FIXEDTHREADPOOLCOUNT).execute(
                new CustomRunnable().setCallBack(new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {
                        mDataBackupAndRestoreFragment.data_sms_restore_btn
                                .setClickable(false);
                        showLoading();
                    }

                    @Override
                    public Object running() {
                        long startTime = SystemClock.uptimeMillis();
                        Log.d(TAG, "start time : " + startTime);
                        SimpleDao.getInstance().restoreData(Sms.class);
                        long endTime = SystemClock.uptimeMillis();
                        Log.d(TAG, "start time : " + endTime);
                        Log.d(TAG, "take time : " + (endTime - startTime));
                        return null;
                    }

                    @Override
                    public void onProgressUpdate(Object object) {

                    }

                    @Override
                    public void runAfter(Object object) {
                        mDataBackupAndRestoreFragment.data_sms_restore_btn
                                .setClickable(true);
                        hideLoading();
                    }

                    @Override
                    public void runError() {
                        mDataBackupAndRestoreFragment.data_sms_restore_btn
                                .setClickable(true);
                        hideLoading();
                    }
                }));
    }


}
