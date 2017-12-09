package com.weidi.artifact.controller;

import android.app.Activity;
import android.app.Fragment;
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
import com.weidi.artifact.fragment.FragOperManager;
import com.weidi.artifact.fragment.MainFragment;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.log.Log;
import com.weidi.utils.EventBusUtils;
import com.weidi.utils.HandlerUtils;

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
//    public FragOperManager mFragOperManager;
    private BaseFragment mMainFragment;
    private ILifeCycle mILifeCycle;
//    private Bundle mSavedInstanceState;
//    private BaseFragment mBaseFragment;
//    private String mFragmentTag;
    private String drawerViewTag;

    /*private static HashMap<String, Integer> mFragmentBackTypeSMap;
    public static HashMap<String, String> mDataBackupAndRestoreMap;
    static {
        // MainFragment不要加入Map中
        mFragmentBackTypeSMap = new HashMap<String, Integer>();
        mFragmentBackTypeSMap.put("AppsManagerFragment", Constant.POPBACKSTACK);
        mFragmentBackTypeSMap.put("SettingsFragment", Constant.POPBACKSTACK);
        mFragmentBackTypeSMap.put("BluetoothFragment", Constant.POPBACKSTACK);
        mFragmentBackTypeSMap.put("AlarmClockFragment", Constant.POPBACKSTACK);
        mFragmentBackTypeSMap.put("QrCodeFragment", Constant.POPBACKSTACK);
        mFragmentBackTypeSMap.put("DataBackupAndRestoreFragment", Constant.POPBACKSTACK);
        mFragmentBackTypeSMap.put("SmsFragment", Constant.POPBACKSTACK);
        mFragmentBackTypeSMap.put("PhoneFragment", Constant.POPBACKSTACK);
        mFragmentBackTypeSMap.put("TestImageFragment", Constant.POPBACKSTACK);

        mDataBackupAndRestoreMap = new HashMap<String, String>();
        mDataBackupAndRestoreMap.put("Sms", "content://sms");
    }*/

    public MainActivityController(Activity activity) {
        super(activity);
        mMainActivity = (MainActivity) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "onCreate():mSavedInstanceState = " + savedInstanceState);

        mMainActivity.mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {

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
                /*mMainActivity.mDrawerLayout.setDrawerLockMode(
                        DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);*/
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
        });

        if (savedInstanceState != null) {
            //            onRestore(mSavedInstanceState);
//            this.mSavedInstanceState = savedInstanceState;
        } else {
            mMainActivity.setContainerId(R.id.container);
            // 加载MainFragment
            mMainFragment = new MainFragment();
            mMainActivity.getFragOperManager().enter(mMainFragment, null);
        }
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

        /***
         做这个事的原因:
         如果有多个Fragment开启着,并且相互之间是显示和隐藏,而不是弹出,
         (如果后退时Fragment是弹出的话,不需要这样的代码的;
         如果这些Fragment是像QQ那样实现的底部导航栏形式的,
         在任何一个页面都可以退出,那么也不需要实现这样的代码的);
         那么页面在MainFragment时关闭屏幕,然后在点亮屏幕后,
         MainFragment的onResume()方法比其他Fragment的onResume()方法要先执行,
         最后执行的Fragment就得到了后退的"焦点",
         这样的话要后退时导致在MainFragment页面时就退不出去了.
         */
        /*if (mSavedInstanceState != null) {
            final String fragmentTag = mSavedInstanceState.getString("FragmentTag");
            List<Fragment> fragmentsList = getFragOperManager().getmFragmentsList();
            int count = fragmentsList.size();
            for (int i = 0; i < count; i++) {
                final Fragment fragment = fragmentsList.get(i);
                if (fragment != null && fragment.getClass().getSimpleName().equals(fragmentTag)) {
                    if (fragment instanceof BaseFragment) {
                        HandlerUtils.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                if (((BaseFragment) fragment).getBackHandlerInterface() != null) {
                                    ((BaseFragment) fragment).getBackHandlerInterface()
                                            .setSelectedFragment(
                                                    (BaseFragment) fragment,
                                                    fragmentTag);
                                }
                            }
                        }, 500);
                    }
                    break;
                }
            }
            mSavedInstanceState = null;
        }*/

        // 开启核心服务
        /*if (!MyUtils.isSpecificServiceAlive(
                mContext,
                Constant.CLASS_CORESERVICE)) {
            Intent intent = new Intent(mMainActivity, CoreService.class);
            mMainActivity.startServiceAsUser(intent, UserHandle.OWNER);
        }

        if (!MyUtils.isSpecificServiceAlive(
                mContext,
                Constant.CLASS_PERIODICALSERIALKILLERSERVICE)) {
            Intent intent = new Intent(mMainActivity, PeriodicalSerialKillerService.class);
            mMainActivity.startService(intent);
        }

        if (!MyUtils.isSpecificServiceAlive(
                mContext,
                Constant.CLASS_APPSLOCKSERVICE)) {
            Intent intent = new Intent(mMainActivity, AppsLockService.class);
            mMainActivity.startService(intent);
        }*/

        //        if (mSavedInstanceState != null && !mSavedInstanceState.isEmpty()) {
        //            onRestore(mSavedInstanceState);
        //            mSavedInstanceState = null;
        //        }
        //        if (mILifeCycle != null) {
        //            mILifeCycle.onResume();
        //        }
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.d(TAG, "onPause()");
        //        if (mILifeCycle != null) {
        //            mILifeCycle.onPause();
        //        }
    }

    @Override
    public void onStop() {
        if (DEBUG) Log.d(TAG, "onStop()");
        //        if (mILifeCycle != null) {
        //            mILifeCycle.onStop();
        //        }
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");
        //        mFragOperManager = null;
//        mSavedInstanceState = null;
        //        if (mILifeCycle != null) {
        //            mILifeCycle.onDestroy();
        //        }
    }

    public Object onEvent(int what, Object[] object) {
        return null;
    }

    public void setILifeCycle(ILifeCycle iLifeCycle) {
        mILifeCycle = iLifeCycle;
    }

    /*public FragOperManager getFragOperManager() {
        if (mFragOperManager == null) {
            mFragOperManager = new FragOperManager(mMainActivity, R.id.container);
        }
        return mFragOperManager;
    }*/

    /***
     * 如果多个Fragment以底部Tab方式呈现的话,
     * 那么这些Fragment中的onBackPressed()方法最好返回true.
     * 这样就不需要在MainActivityController中处理onResume()方法了.
     * 如果不是以这种方式呈现,那么这些Fragment中的onBackPressed()方法最好返回false.
     * 然后需要在MainActivityController中处理onResume()方法了.
     */
    public void onBackPressed() {
        /*if (DEBUG) Log.d(TAG, "onBackPressed()");
        if (mBaseFragment == null || mBaseFragment.onBackPressed()) {
            exit();
            return;
        }

        // 实现后退功能(把当前Fragment进行pop或者hide)
        String fragmentName = mBaseFragment.getClass().getSimpleName();
        for (String key : mFragmentBackTypeSMap.keySet()) {
            if (key.equals(fragmentName)) {
                int type = mFragmentBackTypeSMap.get(key);
                EventBusUtils.postAsync(FragOperManager.class, type, new Object[]{mBaseFragment});
                break;
            }
        }*/
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (DEBUG) Log.d(TAG, "onActivityResult():requestCode = " + requestCode
                + " resultCode = " + resultCode + " intent = " + data);
        /*if (mILifeCycle != null) {
            mILifeCycle.onActivityResult(requestCode, resultCode, data);
        }*/
    }

    /**
     * 锁屏时也会被调
     *
     * @param outState
     */
    public void onSaveInstanceState(Bundle outState) {
        if (DEBUG) Log.d(TAG, "onSaveInstanceState():outState = " + outState);
//        outState.putString("FragmentTag", mFragmentTag);
//        this.mSavedInstanceState = outState;

        //        if (outState != null && !outState.isEmpty()) {
        //        if (outState != null) {
        //            if (mILifeCycle != null) {
        //                mILifeCycle.onSaveInstanceState(outState);
        //            }
        //            ArrayList<String> list = getFragOperManager().getAllTags();
        //            if (list != null) {
        //                ArrayList<String> newList = new ArrayList<String>();
        //                int count = list.size();
        //                for (int i = 0; i < count; i++) {
        //                    String tag = list.get(i);
        //                    newList.add(tag);
        //                }
        //                outState.putStringArrayList("TAG", newList);
        //                mSavedInstanceState = outState;
        //                // 放到onDestroy()方法中去就有异常
        //                getFragOperManager().release();
        //                //            mFragOperManager = null;
        //            }
        //        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (DEBUG)
            Log.d(TAG, "onRestoreInstanceState():mSavedInstanceState = " + savedInstanceState);

        /*if (mILifeCycle != null) {
            mILifeCycle.onRestoreInstanceState(mSavedInstanceState);
        }*/
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (DEBUG)
            Log.d(TAG, "onConfigurationChanged():newConfig = " + newConfig);

        /*if (mILifeCycle != null) {
            mILifeCycle.onConfigurationChanged(newConfig);
        }*/
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

    public void setSelectedFragment(BaseFragment selectedFragment, String fragmentTag) {
//        mBaseFragment = selectedFragment;
//        mFragmentTag = fragmentTag;
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
            mMainActivity.finish();
            mMainActivity.exitActivity();

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
