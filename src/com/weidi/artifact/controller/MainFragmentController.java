package com.weidi.artifact.controller;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.weidi.artifact.R;
import com.weidi.artifact.activity.AdvancedToolsActivity;
import com.weidi.artifact.activity.CleanCacheActivity;
import com.weidi.artifact.activity.CommunicationGuardActivity;
import com.weidi.artifact.activity.KillerVirusActivity;
import com.weidi.artifact.activity.MainActivity;
import com.weidi.artifact.activity.ProcessesManagerActivity;
import com.weidi.artifact.activity.SecurityPhoneActivity;
import com.weidi.artifact.activity.SecurityPhoneSetup1Activity;
import com.weidi.artifact.activity.TrafficStatisticsActivity;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.basecontroller.BaseFragmentController;
import com.weidi.artifact.fragment.AlarmClockFragment;
import com.weidi.artifact.fragment.AppsManagerFragment;
import com.weidi.artifact.fragment.BluetoothFragment;
import com.weidi.artifact.fragment.DataBackupAndRestoreFragment;
import com.weidi.artifact.fragment.MainFragment;
import com.weidi.artifact.fragment.PhoneFragment;
import com.weidi.artifact.fragment.QrCodeFragment;
import com.weidi.artifact.fragment.SettingsFragment;
import com.weidi.artifact.fragment.SmsFragment;
import com.weidi.customadapter.CustomRecyclerViewAdapter;
import com.weidi.customadapter.CustomViewHolder;
import com.weidi.customadapter.listener.OnItemClickListener;
import com.weidi.fragment.base.BaseFragment;
import com.weidi.log.Log;
import com.weidi.utils.MyUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import jackpal.androidterm.Term;

/**
 * Created by root on 17-1-13.
 */

/**
 * 屏幕常亮
 * mMainActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
 * 取消屏幕常亮
 * mMainActivity.getWindow().clearFlags(LayoutParams.FLAG_KEEP_SCREEN_ON);
 */
public class MainFragmentController extends BaseFragmentController {

    private static final String TAG = "MainFragmentController";
    private static final boolean DEBUG = false;

    /*private static int[] images = new int[]{
            R.drawable.safe, R.drawable.callmsgsafe, R.drawable.app,
            R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
            R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings,
            R.drawable.sysoptimize, R.drawable.sysoptimize, R.drawable.sysoptimize,
            R.drawable.sysoptimize, R.drawable.sysoptimize, R.drawable.sysoptimize,
            R.drawable.sysoptimize, R.drawable.sysoptimize, R.drawable.sysoptimize};*/
    private static int[] images = new int[]{
            R.drawable.sysoptimize, R.drawable.sysoptimize, R.drawable.sysoptimize,
            R.drawable.sysoptimize, R.drawable.sysoptimize, R.drawable.sysoptimize,
            R.drawable.sysoptimize, R.drawable.sysoptimize, R.drawable.sysoptimize,
            R.drawable.sysoptimize, R.drawable.sysoptimize, R.drawable.sysoptimize,
            R.drawable.sysoptimize, R.drawable.sysoptimize, R.drawable.sysoptimize,
            R.drawable.sysoptimize, R.drawable.sysoptimize, R.drawable.sysoptimize};
    private static String[] factions = new String[]{
            "手机防盗", "通讯卫士", "软件管理",
            "电话", "短信", "手机杀毒",
            "缓存清理", "高级工具", "设置中心",
            "终端模拟器", "蓝牙相关", "设置闹钟",
            "辅助设置", "二维码", "数据备份",
            "进程管理", "流量统计", "流量统计"};

    private MainFragment mMainFragment;
    private MainFragmentAdapter mMainFragmentAdapter;
    private SharedPreferences mSharedPreferences;

    public MainFragmentController(Fragment fragment) {
        super(fragment.getActivity());
        mMainFragment = (MainFragment) fragment;
    }

    @Override
    public void beforeInitView() {
        if (DEBUG) Log.d(TAG, "beforeInitView()");
    }

    public void afterInitView(
            LayoutInflater inflater,
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
        ((MainActivity) mMainFragment.getActivity()).title.setText("功能列表");
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

    public Object onEvent(int what, Object object) {
        Object result = null;
        switch (what){

            default:
        }
        return result;
    }

    private void init() {
        mSharedPreferences = mContext.getSharedPreferences(
                Constant.APP_CONFIG, Context.MODE_PRIVATE);
        mMainFragmentAdapter = new MainFragmentAdapter(
                mContext, Arrays.asList(factions), R.layout.factionlist_item);
        mMainFragmentAdapter.setOnItemClickListener(mOnItemClickListener);
        mMainFragment.factionlist_recycleview.setLayoutManager(
                new GridLayoutManager(mContext, 3));
        mMainFragment.factionlist_recycleview.setAdapter(mMainFragmentAdapter);
    }

    private static class MainFragmentAdapter extends CustomRecyclerViewAdapter<String> {

        public MainFragmentAdapter(Context context, List items, int layoutResId) {
            super(context, items, layoutResId);
        }

        @Override
        public void onBind(CustomViewHolder customViewHolder,
                           int viewType,
                           int layoutPosition,
                           String item) {
            customViewHolder.setImageResource(R.id.home_factionlist_image, images[layoutPosition]);
            customViewHolder.setText(R.id.home_factionlist_text, factions[layoutPosition]);
        }
    }

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View itemView, int viewType, int position) {
            selectedFaction(itemView, viewType, position);
        }
    };

    private void selectedFaction(View itemView, int viewType, int position) {
        intent = new Intent();
        switch (position) {
            case 0: {// 手机防盗
                run0();
                break;
            }

            case 1: {// 通讯卫士
                run1();
                break;
            }

            case 2: {// 软件管理
                //                 mMainActivity.fragment_container.setVisibility(View.VISIBLE);
                //                 ProcessManagerFragment mProcessManagerFragment = new
                //  ProcessManagerFragment();
                //                 FragmentManager fm = mMainActivity.getFragmentManager();
                //                 FragmentTransaction transaction = fm.beginTransaction();
                //                 transaction.add(R.id.container, mProcessManagerFragment);
                //                 transaction.commit();
                run2();
                break;
            }

            case 3: {// 进程管理
                run3();
                break;
            }

            case 4: {// 流量统计
                run4();
                break;
            }

            case 5: {// 手机杀毒
                run5();
                break;
            }

            case 6: {// 缓存清理
                run6();
                break;
            }

            case 7: {// 高级工具
                run7();
                break;
            }

            case 8: {// 设置中心
                run8();
                break;
            }

            case 9: {// 终端模拟器
                run9();
                break;

            }

            case 10: {// 蓝牙相关
                run10();
                break;
            }

            case 11: {
                run11();
                break;
            }

            case 12: {
                run12();
                break;
            }

            case 13: {
                run13();
                break;
            }

            case 14: {
                run14();
                break;
            }

            case 15: {
                run15();
                break;
            }

            case 16: {
                run16();
                break;
            }

            default:
        }
        mMainActivity.enterActivity();
    }

    private TextView tv = null;
    private EditText et_one = null;
    private EditText et_two = null;
    private Button bt_ok = null;
    private Button bt_cancel = null;
    private AlertDialog dialog = null;
    private Intent intent = null;

    // GridView组件中第0个条目被点击后的事件 下面依次类推
    private void run0() {
        // 试着从SharedPreferences文件中获取密码，如果获取到的密码为空字符串，则表示还没有设置过密码。
        // 那么接下来就要设置密码。如果不是空字符串，则表明已经设置过密码了，接下去就是输入密码登陆。
        final String password = mSharedPreferences.getString("security_password", "");
        View v = View.inflate(mContext, R.layout.activity_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        tv = (TextView) v.findViewById(R.id.title);
        et_one = (EditText) v.findViewById(R.id.passwordone);
        et_two = (EditText) v.findViewById(R.id.passwordtwo);
        bt_ok = (Button) v.findViewById(R.id.ok);
        bt_cancel = (Button) v.findViewById(R.id.cancel);
        // 登陆
        if (!password.isEmpty()) {
            tv.setText("请输入密码");
            et_one.setHint("请输入密码");
            et_two.setVisibility(View.GONE);
            bt_ok.setText("确定");
            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String enterpassword = et_one.getText().toString().trim();
                    try {
                        if (password.equals(MyUtils.md5AddKey(enterpassword))) {
                            // 密码输入正确，进入防盗页面
                            Toast.makeText(mContext,
                                    "密码正确,允许进入！", Toast.LENGTH_SHORT).show();
                            boolean security_setup = mSharedPreferences.getBoolean(
                                    "security_setup", false);
                            if (security_setup) {// true为已设置
                                dialog.dismiss();
                                intent.setClass(mContext, SecurityPhoneActivity.class);
                                mMainActivity.startActivity(intent);
                            } else {
                                dialog.dismiss();
                                intent.setClass(mContext, SecurityPhoneSetup1Activity
                                        .class);
                                mMainActivity.startActivity(intent);
                            }
                        } else {
                            Toast.makeText(mContext,
                                    "密码输入有误，请重新输入！", Toast.LENGTH_SHORT).show();
                            et_one.setText("");
                            return;
                        }
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            });
            bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.setView(v, 0, 0, 0, 0);// 去掉不好看的边框
            dialog.show();
        } else {
            // 设置密码
            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String passwordone = et_one.getText().toString().trim();
                    String passwordtwo = et_two.getText().toString().trim();
                    if (passwordone.isEmpty() || passwordtwo.isEmpty()) {
                        Toast.makeText(mContext,
                                "密码不能为空，请重新输入！", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        // 保存密码
                        try {
                            if (MyUtils.md5AddKey(passwordone).equals(MyUtils.md5AddKey
                                    (passwordtwo))) {
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putString("security_password", MyUtils.md5AddKey
                                        (passwordone));
                                editor.commit();
                                Toast.makeText(mContext,
                                        "密码设置成功！请牢记密码！", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                // 要设置密码，肯定是没有经过防盗页面设置过的
                                intent.setClass(mContext, SecurityPhoneSetup1Activity
                                        .class);
                                mMainActivity.startActivity(intent);
                            } else {
                                Toast.makeText(mContext,
                                        "两次密码输入不相同，请重新输入！", Toast.LENGTH_SHORT)
                                        .show();
                                et_one.setText("");
                                et_two.setText("");
                                return;
                            }
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            bt_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog = builder.create();
            dialog.setView(v, 0, 0, 0, 0);
            dialog.show();
        }
    }

    private void run1() {
        intent.setClass(mContext, CommunicationGuardActivity.class);
        mMainActivity.startActivity(intent);
    }

    private void run2() {
        BaseFragment mAppsManagerFragment = new AppsManagerFragment();
        mMainActivity.mMainActivityController.getFragOperManager().enter(mAppsManagerFragment,
                null);
    }

    private void run3() {
        BaseFragment phoneFragment = new PhoneFragment();
        mMainActivity.mMainActivityController.getFragOperManager().enter(phoneFragment, null);
    }

    private void run4() {
        BaseFragment smsFragment = new SmsFragment();
        mMainActivity.mMainActivityController.getFragOperManager().enter(smsFragment, null);
    }

    private void run5() {
        intent.setClass(mContext, KillerVirusActivity.class);
        mMainActivity.startActivity(intent);
    }

    private void run6() {
        intent.setClass(mContext, CleanCacheActivity.class);
        mMainActivity.startActivity(intent);
    }

    private void run7() {
        intent.setClass(mContext, AdvancedToolsActivity.class);
        mMainActivity.startActivity(intent);

        //         Intent intent = new Intent();
        //         intent.setClass(mContext, ReceiveSMSsActivity.class);
        //         intent.putExtra("name", "name");
        //         intent.putExtra("address", "address");
        //         intent.putExtra("body", "body");
        //         intent.putExtra("time", "time");
        //         mMainActivity.startActivity(intent);
    }

    private void run8() {
        BaseFragment settingsFragment = new SettingsFragment();
        mMainActivity.mMainActivityController.getFragOperManager().enter(settingsFragment, null);
    }

    private void run9() {
        intent.setClass(mContext, Term.class);
        mMainActivity.startActivity(intent);
    }

    private void run10() {
        BaseFragment bluetoothFragment = new BluetoothFragment();
        mMainActivity.mMainActivityController.getFragOperManager().enter(bluetoothFragment, null);
    }

    private void run11() {
        AlarmClockFragment alarmClockFragment = new AlarmClockFragment();
        mMainActivity.mMainActivityController.getFragOperManager().enter(alarmClockFragment, null);
    }

    private void run12() {
        try {
            Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            mMainActivity.startActivity(intent);
            mMainActivity.enterActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void run13() {
        QrCodeFragment qrCodeFragment = new QrCodeFragment();
        mMainActivity.mMainActivityController.getFragOperManager().enter(qrCodeFragment, null);
    }

    private void run14() {
        /*intent.setClass(mContext, MusicPlayActivity.class);
        mMainActivity.startActivity(intent);*/
        DataBackupAndRestoreFragment dataBackupAndRestoreFragment =
                new DataBackupAndRestoreFragment();
        mMainActivity.mMainActivityController.getFragOperManager().enter(
                dataBackupAndRestoreFragment, null);
    }

    private void run15() {
        intent.setClass(mContext, ProcessesManagerActivity.class);
        mMainActivity.startActivity(intent);
    }

    private void run16() {
        intent.setClass(mContext, TrafficStatisticsActivity.class);
        mMainActivity.startActivity(intent);
    }

}
