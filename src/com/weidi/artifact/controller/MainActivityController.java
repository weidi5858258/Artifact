package com.weidi.artifact.controller;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;
import com.weidi.artifact.R;
import com.weidi.artifact.activity.MainActivity;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.basecontroller.BaseActivityController;
import com.weidi.artifact.fragment.MainFragment;
import com.weidi.fragment.FragOperManager;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.log.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 17-1-13.
 */

public class MainActivityController extends BaseActivityController {

    private static final String TAG = "MainActivityController";
    private static final boolean DEBUG = false;
    private MainActivity mMainActivity;
    private BaseFragment mMainFragment;
    private String drawerViewTag;

    public MainActivityController(Activity activity) {
        super(activity);
        mMainActivity = (MainActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "onCreate():mSavedInstanceState = " + savedInstanceState);

        /*mMainActivity.mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {

            @Override
            public void onDrawerStateChanged(int newState) {

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                View mContent = mMainActivity.mDrawerLayout.getChildAt(0);
                View mMenu = drawerView;
                float scale = 1 - slideOffset;
                float rightScale = 0.8f + scale * 0.2f;

                if (drawerView.getTag().equals(Constant.LEFTFRAGMENT)) {
                    float leftScale = 1 - 0.3f * scale;
                    ViewHelper.setScaleX(mMenu, leftScale);
                    ViewHelper.setScaleY(mMenu, leftScale);
                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
                    ViewHelper.setTranslationX(mContent,
                            mMenu.getMeasuredWidth() * (1 - scale));
                    ViewHelper.setPivotX(mContent, 0);
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);
                    drawerViewTag = Constant.LEFTFRAGMENT;

                } else {
                    ViewHelper.setTranslationX(mContent,
                            -mMenu.getMeasuredWidth() * slideOffset);
                    ViewHelper.setPivotX(mContent, mContent.getMeasuredWidth());
                    ViewHelper.setPivotY(mContent,
                            mContent.getMeasuredHeight() / 2);
                    mContent.invalidate();
                    ViewHelper.setScaleX(mContent, rightScale);
                    ViewHelper.setScaleY(mContent, rightScale);
                    drawerViewTag = Constant.RIGHTFRAGMENT;

                }

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                drawerViewTag = null;
                *//*mBaseActivity.mDrawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);*//*
            }
        });

        mMainActivity.mMainActivityLayout.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                float downX = 0;
                float upX = 0;
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        downX = event.getX();
                        Log.d(TAG, "downX = " + downX);
                        break;

                    case MotionEvent.ACTION_UP:
                        upX = event.getX();
                        Log.d(TAG, "upX = " + upX);

                        if ((upX - downX) > 200) {
                            if (drawerViewTag != null
                                    && drawerViewTag.equals(Constant.RIGHTFRAGMENT)) {
                                closeRightFragment();
                            } else {
                                openLeftFragment();
                            }
                        } else if ((upX - downX) < -200) {
                            if (drawerViewTag != null
                                    && drawerViewTag.equals(Constant.LEFTFRAGMENT)) {
                                closeLeftFragment();
                            } else {
                                openRightFragment();
                            }
                        }
                        break;

                    case MotionEvent.ACTION_MOVE:
                        Log.d(TAG, "moveX = " + event.getX());
                        break;

                    default:
                }
                return false;
            }
        });*/

//        mMainActivity.setContainerId(R.id.container);
        FragOperManager.getInstance().setActivityAndContainerId(mMainActivity, R.id.container);
        // 加载MainFragment
        mMainFragment = new MainFragment();
        FragOperManager.getInstance().enter(mMainActivity, mMainFragment, null);
    }

    @Override
    public void onStart() {
        if (DEBUG) Log.d(TAG, "onStart()");
    }

    @Override
    public void onRestart() {
        if (DEBUG) Log.d(TAG, "onRestart()");
    }

    @Override
    public void onResume() {
        if (DEBUG) Log.d(TAG, "onResume()");
        mMainActivity.title.setText("功能列表");

        // 开启核心服务
        /*if (!MyUtils.isSpecificServiceAlive(
                mContext,
                Constant.CLASS_CORESERVICE)) {
            Intent intent = new Intent(mBaseActivity, CoreService.class);
            mBaseActivity.startServiceAsUser(intent, UserHandle.OWNER);
        }

        if (!MyUtils.isSpecificServiceAlive(
                mContext,
                Constant.CLASS_PERIODICALSERIALKILLERSERVICE)) {
            Intent intent = new Intent(mBaseActivity, PeriodicalSerialKillerService.class);
            mBaseActivity.startService(intent);
        }

        if (!MyUtils.isSpecificServiceAlive(
                mContext,
                Constant.CLASS_APPSLOCKSERVICE)) {
            Intent intent = new Intent(mBaseActivity, AppsLockService.class);
            mBaseActivity.startService(intent);
        }*/
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

    public Object onEvent(int what, Object[] object) {
        return null;
    }

    public void setILifeCycle(ILifeCycle iLifeCycle) {
        // mILifeCycle = iLifeCycle;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DEBUG) Log.d(TAG, "onActivityResult():requestCode = " + requestCode
                + " resultCode = " + resultCode + " intent = " + data);
    }

    /***
     * 锁屏时也会被调
     *
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        if (DEBUG) Log.d(TAG, "onSaveInstanceState():outState = " + outState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (DEBUG)
            Log.d(TAG, "onRestoreInstanceState():mSavedInstanceState = " + savedInstanceState);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged():newConfig = " + newConfig);
        if (DEBUG)
            Log.d(TAG, "onConfigurationChanged():newConfig = " + newConfig);
    }

    public void openLeftFragment() {
        mMainActivity.mDrawerLayout.openDrawer(Gravity.LEFT);
        mMainActivity.mDrawerLayout.setDrawerLockMode(
                DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.LEFT);
    }

    public void closeLeftFragment() {
        mMainActivity.mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    public void openRightFragment() {
        mMainActivity.mDrawerLayout.openDrawer(Gravity.RIGHT);
        mMainActivity.mDrawerLayout.setDrawerLockMode(
                DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
    }

    public void closeRightFragment() {
        mMainActivity.mDrawerLayout.closeDrawer(Gravity.RIGHT);
    }

    private void onRestore(Bundle savedInstanceState) {
        List<Map<String, BaseFragment>> mapList = new ArrayList<Map<String, BaseFragment>>();
        Map<String, BaseFragment> map = new HashMap<String, BaseFragment>();

        ArrayList<String> list = savedInstanceState.getStringArrayList("TAG");
        if (list != null) {
            int count = list.size();
            String tag = null;
            BaseFragment topFragment = null;
            for (int i = 0; i < count; i++) {
                String className = "com.weidi.artifact.fragment." + list.get(i);
                try {
                    Class<BaseFragment> clazz = (Class<BaseFragment>) Class.forName(className);
                    BaseFragment fragment = clazz.newInstance();
                    if (i < count - 1) {
                        map.put(list.get(i), fragment);
                    } else {
                        tag = list.get(i);
                        topFragment = fragment;
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            mapList.add(map);
            // getFragOperManager().add(mapList);
            if (!TextUtils.isEmpty(tag) && topFragment != null) {
                // getFragOperManager().enterFragment(topFragment, tag);
            }
        }
    }

    private static final int TIME = 2000;
    private long PRESS_TIME = 0;

    private void exit() {
        mMainActivity.finish();
        mMainActivity.exitActivity();

        // goHome();

        // 因为第一次按的时候“PRESS_TIME”为“0”，所以肯定大于2000
        /*if (SystemClock.uptimeMillis() - PRESS_TIME > TIME) {
            Toast.makeText(mContext,
                    "再按一次 退出" + mContext.getResources().getString(R.string.app_name),
                    Toast.LENGTH_SHORT).show();
            PRESS_TIME = SystemClock.uptimeMillis();

        } else {
            // 按第二次的时候如果距离前一次的时候在2秒之内，那么就走下面的路线
            mBaseActivity.finish();
            mBaseActivity.exitActivity();

        }*/
    }

    private void goHome() {
        /*if (((MyApplication) mContext.getApplicationContext()).getSystemCall() != null) {
            try {
                ((MyApplication) mContext.getApplicationContext())
                        .getSystemCall().input(CoreService.commandsHome);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
    }

}
