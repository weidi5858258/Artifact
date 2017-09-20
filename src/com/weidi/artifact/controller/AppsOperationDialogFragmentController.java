package com.weidi.artifact.controller;

import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.weidi.artifact.R;
import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.basecontroller.BaseFragmentController;
import com.weidi.artifact.db.dao.AppLockDao;
import com.weidi.artifact.db.dao.ProcessDao;
import com.weidi.artifact.fragment.AppsOperationDialogFragment;
import com.weidi.artifact.fragment.base.BaseDialogFragment;
import com.weidi.artifact.listener.OnResultListener;
//import com.weidi.callsystemmethod.ICallSystemMethod;
import com.weidi.log.Log;
import com.weidi.threadpool.CustomRunnable;
import com.weidi.threadpool.ThreadPool;
import com.weidi.utils.MyToast;

/**
 * Created by root on 17-1-13.
 */

public class AppsOperationDialogFragmentController extends BaseFragmentController {

    private static final String TAG = "AppsOperationDialogFragmentController";
    private AppsOperationDialogFragment mAppsOperationDialogFragment;
    private Bundle mBundle;
    private AppLockDao mAppLockDao;
    private ProcessDao mProcessDao;
    private OnResultListener mOnResultListener;

    public AppsOperationDialogFragmentController(BaseDialogFragment fragment) {
        super(fragment.getActivity());
        mAppsOperationDialogFragment = (AppsOperationDialogFragment) fragment;
    }

    @Override
    public void beforeInitView() {
        mBundle = mAppsOperationDialogFragment.getArguments();
        mAppLockDao = new AppLockDao(mContext);
        mProcessDao = new ProcessDao(mContext);
    }

    @Override
    public void afterInitView(LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        mAppsOperationDialogFragment.app_name_tv.setText(mBundle.getString(Constant.APP_NAME));
        String packageName = mBundle.getString(Constant.APP_PACKAGE_NAME);
        mAppsOperationDialogFragment.app_start_btn.setText("开启");
        mAppsOperationDialogFragment.app_delete_btn.setText("卸载");
        mAppsOperationDialogFragment.app_share_btn.setText("分享");

        if (mAppLockDao.query(packageName)) {
            mAppsOperationDialogFragment.app_lock_btn.setText("解锁");
        } else {
            mAppsOperationDialogFragment.app_lock_btn.setText("加锁");
        }

        if (!mProcessDao.query(packageName)) {
            mAppsOperationDialogFragment.app_kill_btn.setText("免杀");
        } else {
            mAppsOperationDialogFragment.app_kill_btn.setText("要杀");
        }
        mAppsOperationDialogFragment.app_close_btn.setText("关闭");
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDestroy() {

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d(TAG, "onActivityResult():requestCode = " + requestCode
//                + " resultCode = " + resultCode + " intent = " + data);
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//
//    }
//
//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//
//    }

    public void onClick(View view) {
        final String packageName = mBundle.getString(Constant.APP_PACKAGE_NAME);
        switch (view.getId()) {
            case R.id.app_start_btn:
                if (!TextUtils.isEmpty(packageName)) {
                    Intent intent = mMainActivity.getPackageManager()
                            .getLaunchIntentForPackage(packageName);
                    if (intent != null && !mContext.getPackageName().equals(packageName)) {
                        mMainActivity.startActivity(intent);
                    } else if (mContext.getPackageName().equals(packageName)) {
                        return;
                    } else {
                        MyToast.show("我无能为力，启动不了这个东西");
                    }
                }
                break;
            case R.id.app_delete_btn:
                if (!TextUtils.isEmpty(packageName)) {
                    /*final ICallSystemMethod call = ((MyApplication) mContext
                            .getApplicationContext())
                            .getSystemCall();
                    if (call != null && !mContext.getPackageName().equals(packageName)) {
                        final CustomRunnable mCustomRunnable = new CustomRunnable();
                        mCustomRunnable.setCallBack(
                                new CustomRunnable.CallBack() {

                                    @Override
                                    public void runBefore() {

                                    }

                                    @Override
                                    public Object running() {
                                        try {
                                            call.deletePackage(
                                                    packageName,
                                                    new android.content.pm.IPackageDeleteObserver
                                                            .Stub() {
                                                        @Override
                                                        public void packageDeleted(String s, int i)
                                                                throws RemoteException {
                                                            Log.d(TAG, "s = " + s + " i = " + i);
                                                            mCustomRunnable.publishProgress(s);
                                                        }
                                                    });
                                        } catch (RemoteException e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }

                                    @Override
                                    public void onProgressUpdate(Object object) {
                                        OnResultListener listener =
                                                mAppsOperationDialogFragment
                                                        .getOnResultListener();
                                        if (listener != null) {
                                            listener.onResult(0, 0, null);
                                        }
                                    }

                                    @Override
                                    public void runAfter(Object object) {

                                    }

                                    @Override
                                    public void runError() {

                                    }

                                });
                        ThreadPool.getCachedThreadPool().execute(mCustomRunnable);
                    }*/
                }
                break;
            case R.id.app_share_btn:
                if (!TextUtils.isEmpty(packageName)) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.SEND");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT,
                            "分享一款软件：" + mBundle.getString(Constant.APP_NAME));
                    mMainActivity.startActivity(intent);
                }
                break;
            case R.id.app_lock_btn:
                if (mAppLockDao.query(packageName)) {
                    mAppLockDao.delete(packageName);
                } else {
                    mAppLockDao.add(packageName);
                }
                OnResultListener listener = mAppsOperationDialogFragment.getOnResultListener();
                if (listener != null) {
                    listener.onResult(0, 0, null);
                }
                break;
            case R.id.app_kill_btn:
                if (!TextUtils.isEmpty(packageName)) {
                    if (!mProcessDao.query(packageName)) {
                        mProcessDao.add(packageName);
                        MyToast.show("添加成功");
                    } else {
                        mProcessDao.delete(packageName);
                        MyToast.show("删除成功");
                    }
                }
                break;
            case R.id.app_close_btn:
                //                mAppsOperationDialogFragment.dismiss();
                break;
            default:
                //                mAppsOperationDialogFragment.dismiss();
        }
        mAppsOperationDialogFragment.dismiss();
    }


}
