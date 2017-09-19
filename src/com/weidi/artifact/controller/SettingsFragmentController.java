package com.weidi.artifact.controller;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.weidi.artifact.R;
import com.weidi.artifact.activity.MainActivity;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.basecontroller.BaseFragmentController;
import com.weidi.artifact.fragment.SettingsFragment;
import com.weidi.artifact.service.AppsLockService;
import com.weidi.artifact.service.CoreService;
import com.weidi.artifact.service.PeriodicalSerialKillerService;
import com.weidi.artifact.service.ShowAttributionService;
import com.weidi.eventbus.EventBus;
import com.weidi.log.Log;
import com.weidi.utils.MyToast;
import com.weidi.utils.MyUtils;

import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by root on 17-1-13.
 */

public class SettingsFragmentController extends BaseFragmentController {

    private static final String TAG = "SettingsFragmentController";
    private static final boolean DEBUG = false;
    private SettingsFragment mSettingsFragment;
    private SharedPreferences mSharedPreferences;

    public SettingsFragmentController(Fragment fragment) {
        super(fragment.getActivity());
        mSettingsFragment = (SettingsFragment) fragment;
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
        ((MainActivity) mSettingsFragment.getActivity()).title.setText("设置中心");
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
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Intent intent = null;
        switch (view.getId()) {
            //自动更新
            case R.id.settings_update: {
                if (mSettingsFragment.settings_update.isChecked()) {
                    //这里让人感觉有点不合逻辑
                    //这里搞不明白，一定要这样设置，那个组合控件点击除CheckBox外的地方才有效
                    mSettingsFragment.settings_update.setChecked(false);
                    mSettingsFragment.settings_update.setTVContent("自动更新已经关闭");
                    editor.putBoolean("update", false);
                } else {
                    mSettingsFragment.settings_update.setChecked(true);
                    mSettingsFragment.settings_update.setTVContent("自动更新已经开启");
                    editor.putBoolean("update", true);
                }
                editor.commit();
                break;
            }

            //手机号归属地
            case R.id.settings_showattribution: {
                if (mSettingsFragment.settings_showattribution.isChecked()) {
                    mSettingsFragment.settings_showattribution.setChecked(false);
                    mSettingsFragment.settings_showattribution.setTVContent("归属地显示已经关闭");
                    intent = new Intent(mMainActivity, ShowAttributionService.class);
                    mMainActivity.stopService(intent);
                } else {
                    mSettingsFragment.settings_showattribution.setChecked(true);
                    mSettingsFragment.settings_showattribution.setTVContent("归属地显示已经开启");
                    intent = new Intent(mMainActivity, ShowAttributionService.class);
                    mMainActivity.startService(intent);
                }
                break;
            }

            case R.id.settings_showinstallpackage: {
                intent = new Intent();
                intent.setAction(Constant.PACKAGEMANAGERSERVICE);
                if (mSettingsFragment.settings_showinstallpackage.isChecked()) {
                    mSettingsFragment.settings_showinstallpackage.setChecked(false);
                    mSettingsFragment.settings_showinstallpackage.setTVContent("允许应用安装已经关闭");
                    editor.putBoolean(Constant.ISINTERCEPTINSTALL, false);
                    intent.putExtra(Constant.ISINTERCEPTINSTALL, false);
                    intent.putExtra(
                            Constant.ISINTERCEPTUNINSTALL,
                            mSharedPreferences.getBoolean(Constant.ISINTERCEPTUNINSTALL, true));
                } else {
                    mSettingsFragment.settings_showinstallpackage.setChecked(true);
                    mSettingsFragment.settings_showinstallpackage.setTVContent("允许应用安装已经开启");
                    editor.putBoolean(Constant.ISINTERCEPTINSTALL, true);
                    intent.putExtra(Constant.ISINTERCEPTINSTALL, true);
                    intent.putExtra(
                            Constant.ISINTERCEPTUNINSTALL,
                            mSharedPreferences.getBoolean(Constant.ISINTERCEPTUNINSTALL, true));
                }
                editor.commit();
                mMainActivity.sendBroadcastAsUser(intent, UserHandle.OWNER);
                break;
            }

            case R.id.settings_showuninstallpackage: {
                intent = new Intent();
                intent.setAction(Constant.PACKAGEMANAGERSERVICE);
                if (mSettingsFragment.settings_showuninstallpackage.isChecked()) {
                    mSettingsFragment.settings_showuninstallpackage.setChecked(false);
                    mSettingsFragment.settings_showuninstallpackage.setTVContent("允许应用卸载已经关闭");
                    editor.putBoolean(Constant.ISINTERCEPTUNINSTALL, false);
                    intent.putExtra(
                            Constant.ISINTERCEPTINSTALL,
                            mSharedPreferences.getBoolean(Constant.ISINTERCEPTINSTALL, true));
                    intent.putExtra(Constant.ISINTERCEPTUNINSTALL, false);
                } else {
                    mSettingsFragment.settings_showuninstallpackage.setChecked(true);
                    mSettingsFragment.settings_showuninstallpackage.setTVContent("允许应用卸载已经开启");
                    editor.putBoolean(Constant.ISINTERCEPTUNINSTALL, true);
                    intent.putExtra(
                            Constant.ISINTERCEPTINSTALL,
                            mSharedPreferences.getBoolean(Constant.ISINTERCEPTINSTALL, true));
                    intent.putExtra(Constant.ISINTERCEPTUNINSTALL, true);
                }
                editor.commit();
                mMainActivity.sendBroadcastAsUser(intent, UserHandle.OWNER);
                break;
            }

            case R.id.settings_showusbdebug: {
                if (mSettingsFragment.settings_showusbdebug.isChecked()) {
                    mSettingsFragment.settings_showusbdebug.setChecked(false);
                    mSettingsFragment.settings_showusbdebug.setTVContent("USB调试已经关闭");
                    editor.putBoolean(Constant.USB_DEBUG, false);
                } else {
                    mSettingsFragment.settings_showusbdebug.setChecked(true);
                    mSettingsFragment.settings_showusbdebug.setTVContent("USB调试已经开启");
                    editor.putBoolean(Constant.USB_DEBUG, true);
                }
                editor.commit();
                break;
            }

            case R.id.settings_showsdcardandusbdisk: {
                if (mSettingsFragment.settings_showsdcardandusbdisk.isChecked()) {
                    mSettingsFragment.settings_showsdcardandusbdisk.setChecked(false);
                    mSettingsFragment.settings_showsdcardandusbdisk.setTVContent("外置存储卡已经关闭");
                    editor.putBoolean(Constant.SDCARD_USBDISK, false);
                } else {
                    mSettingsFragment.settings_showsdcardandusbdisk.setChecked(true);
                    mSettingsFragment.settings_showsdcardandusbdisk.setTVContent("外置存储卡已经开启");
                    editor.putBoolean(Constant.SDCARD_USBDISK, true);
                }
                editor.commit();
                break;
            }

            //黑名单拦截
            case R.id.settings_blacklist: {
                if (mSettingsFragment.settings_blacklist.isChecked()) {
                    mSettingsFragment.settings_blacklist.setChecked(false);
                    mSettingsFragment.settings_blacklist.setTVContent("核心服务已经关闭");
                    // stopService
                    EventBus.getDefault().post(Constant.CORESERVICE, null);
                } else {
                    mSettingsFragment.settings_blacklist.setChecked(true);
                    mSettingsFragment.settings_blacklist.setTVContent("核心服务已经开启");
                    intent = new Intent(mMainActivity, CoreService.class);
                    mMainActivity.startService(intent);
                }
                break;
            }

            //连环杀进程
            case R.id.settings_periodicalserialkiller: {
                if (mSettingsFragment.settings_periodicalserialkiller.isChecked()) {
                    mSettingsFragment.settings_periodicalserialkiller.setChecked(false);
                    mSettingsFragment.settings_periodicalserialkiller.setTVContent("连环杀进程已经关闭");
                    EventBus.getDefault().post(Constant.PERIODICALSERIALKILLERSERVICE, null);
                } else {
                    mSettingsFragment.settings_periodicalserialkiller.setChecked(true);
                    mSettingsFragment.settings_periodicalserialkiller.setTVContent("连环杀进程已经开启");
                    intent = new Intent(mMainActivity, PeriodicalSerialKillerService.class);
                    mMainActivity.startService(intent);
                }
                break;
            }

            //程序锁
            case R.id.settings_applock: {
                boolean flag = mSharedPreferences.getBoolean("applock", false);
                if (flag) {
                    if (mSettingsFragment.settings_applock.isChecked()) {
                        mSettingsFragment.settings_applock.setChecked(false);
                        mSettingsFragment.settings_applock.setTVContent("程序锁已经关闭");
                        EventBus.getDefault().post(Constant.APPSLOCKSERVICE, null);
                    } else {
                        mSettingsFragment.settings_applock.setChecked(true);
                        mSettingsFragment.settings_applock.setTVContent("程序锁已经开启");
                        intent = new Intent(mMainActivity, AppsLockService.class);
                        mMainActivity.startService(intent);
                    }
                } else {
                    MyToast.show("先设置一个密码后再开启程序锁");
                }
                break;
            }

            case R.id.settings_applock_password: {
                setAppsLockPassword();
                break;
            }

            default:
                break;
        }
    }

    //初始化
    private void init() {
        mSettingsFragment.settings_applock_password.setText("设置或者更改程序锁密码");

        boolean flag = mSharedPreferences.getBoolean("update", false);
        if (flag) {
            mSettingsFragment.settings_update.setChecked(true);
            mSettingsFragment.settings_update.setTVContent("自动更新已经开启");
        } else {
            mSettingsFragment.settings_update.setChecked(false);
            mSettingsFragment.settings_update.setTVContent("自动更新已经关闭");
        }

        //服务活着就表示开启，服务死亡就表示关闭
        flag = MyUtils.isSpecificServiceAlive(
                mContext, "com.weidi.artifact.service.ShowAttributionService");
        if (flag) {
            mSettingsFragment.settings_showattribution.setChecked(true);
            mSettingsFragment.settings_showattribution.setTVContent("归属地显示已经开启");
        } else {
            mSettingsFragment.settings_showattribution.setChecked(false);
            mSettingsFragment.settings_showattribution.setTVContent("归属地显示已经关闭");
        }

        Intent intent = new Intent();
        intent.setAction(Constant.PACKAGEMANAGERSERVICE);
        flag = mSharedPreferences.getBoolean(Constant.ISINTERCEPTINSTALL, true);
        if (flag) {
            mSettingsFragment.settings_showinstallpackage.setChecked(true);
            mSettingsFragment.settings_showinstallpackage.setTVContent("允许应用安装已经开启");
            intent.putExtra(Constant.ISINTERCEPTINSTALL, true);
        } else {
            mSettingsFragment.settings_showinstallpackage.setChecked(false);
            mSettingsFragment.settings_showinstallpackage.setTVContent("允许应用安装已经关闭");
            intent.putExtra(Constant.ISINTERCEPTINSTALL, false);
        }

        flag = mSharedPreferences.getBoolean(Constant.ISINTERCEPTUNINSTALL, true);
        if (flag) {
            mSettingsFragment.settings_showuninstallpackage.setChecked(true);
            mSettingsFragment.settings_showuninstallpackage.setTVContent("允许应用卸载已经开启");
            intent.putExtra(Constant.ISINTERCEPTUNINSTALL, true);
        } else {
            mSettingsFragment.settings_showuninstallpackage.setChecked(false);
            mSettingsFragment.settings_showuninstallpackage.setTVContent("允许应用卸载已经关闭");
            intent.putExtra(Constant.ISINTERCEPTUNINSTALL, false);
        }
        mMainActivity.sendBroadcastAsUser(intent, UserHandle.OWNER);

        flag = mSharedPreferences.getBoolean(Constant.USB_DEBUG, true);
        if (flag) {
            mSettingsFragment.settings_showusbdebug.setChecked(true);
            mSettingsFragment.settings_showusbdebug.setTVContent("USB调试已经开启");
        } else {
            mSettingsFragment.settings_showusbdebug.setChecked(false);
            mSettingsFragment.settings_showusbdebug.setTVContent("USB调试已经关闭");
        }

        flag = mSharedPreferences.getBoolean(Constant.SDCARD_USBDISK, true);
        if (flag) {
            mSettingsFragment.settings_showsdcardandusbdisk.setChecked(true);
            mSettingsFragment.settings_showsdcardandusbdisk.setTVContent("外置存储卡已经开启");
        } else {
            mSettingsFragment.settings_showsdcardandusbdisk.setChecked(false);
            mSettingsFragment.settings_showsdcardandusbdisk.setTVContent("外置存储卡已经关闭");
        }

        flag = MyUtils.isSpecificServiceAlive(
                mContext, Constant.CLASS_CORESERVICE);
        if (flag) {
            mSettingsFragment.settings_blacklist.setChecked(true);
            mSettingsFragment.settings_blacklist.setTVContent("核心服务已经开启");
        } else {
            mSettingsFragment.settings_blacklist.setChecked(false);
            mSettingsFragment.settings_blacklist.setTVContent("核心服务已经关闭");
        }

        flag = MyUtils.isSpecificServiceAlive(
                mContext, Constant.CLASS_PERIODICALSERIALKILLERSERVICE);
        if (flag) {
            mSettingsFragment.settings_periodicalserialkiller.setChecked(true);
            mSettingsFragment.settings_periodicalserialkiller.setTVContent("连环杀进程已经开启");
        } else {
            mSettingsFragment.settings_periodicalserialkiller.setChecked(false);
            mSettingsFragment.settings_periodicalserialkiller.setTVContent("连环杀进程已经关闭");
        }

        flag = MyUtils.isSpecificServiceAlive(
                mContext, Constant.CLASS_APPSLOCKSERVICE);
        if (flag) {
            mSettingsFragment.settings_applock.setChecked(true);
            mSettingsFragment.settings_applock.setTVContent("程序锁已经开启");
        } else {
            mSettingsFragment.settings_applock.setChecked(false);
            mSettingsFragment.settings_applock.setTVContent("程序锁已经关闭");
        }
    }

    private TextView tv = null;
    private EditText et_one = null;
    private EditText et_two = null;
    private EditText et_three = null;
    private Button bt_ok = null;
    private Button bt_cancel = null;
    private AlertDialog dialog = null;
    private Intent intent = new Intent();

    private void setAppsLockPassword() {
        final String password = mSharedPreferences.getString(Constant.APPSLOCK_PASSWORD, "");
        boolean flag = mSharedPreferences.getBoolean("applock", false);
        View v = View.inflate(mContext, R.layout.activity_password, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity);
        tv = (TextView) v.findViewById(R.id.title);
        et_one = (EditText) v.findViewById(R.id.passwordone);
        et_two = (EditText) v.findViewById(R.id.passwordtwo);
        et_three = (EditText) v.findViewById(R.id.passwordthree);
        bt_ok = (Button) v.findViewById(R.id.ok);
        bt_cancel = (Button) v.findViewById(R.id.cancel);

        if (flag) {//已经设置过密码了，现在是更改密码
            tv.setText("请输入密码");
            et_one.setHint("请输入原密码");
            et_two.setHint("请输入新密码");
            et_three.setHint("请再次输入新密码");
            et_three.setVisibility(View.VISIBLE);
            bt_ok.setText("确定");
            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String enterpassword = et_one.getText().toString().trim();
                    try {
                        if (password.equals(MyUtils.md5AddKey(enterpassword))) {
                            String passwordtwo = et_two.getText().toString().trim();
                            String passwordthree = et_three.getText().toString().trim();
                            if (passwordtwo.isEmpty() || passwordthree.isEmpty()) {
                                Toast.makeText(
                                        mContext, "新密码不能为空，请重新输入！", Toast.LENGTH_SHORT)
                                        .show();
                                return;
                            } else {
                                //保存密码
                                try {
                                    if (MyUtils.md5AddKey(passwordtwo).equals(MyUtils.md5AddKey
                                            (passwordthree))) {
                                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                                        editor.putString(
                                                Constant.APPSLOCK_PASSWORD,
                                                MyUtils.md5AddKey(passwordtwo));
                                        editor.putBoolean("applock", true);
                                        editor.commit();
                                        Toast.makeText(mContext, "新密码设置成功！请牢记密码！",
                                                Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(mContext,
                                                "两次新密码输入不相同，请重新输入！",
                                                Toast.LENGTH_SHORT).show();
                                        et_two.setText("");
                                        et_three.setText("");
                                        return;
                                    }
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(
                                    mContext, "原密码输入有误，请重新输入！", Toast.LENGTH_SHORT)
                                    .show();
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
            //			dialog = builder.create();
            //			dialog.setView(v, 0, 0, 0, 0);//去掉不好看的边框
            //			dialog.show();
        } else {//还没有设置密码，现在开始设置密码
            bt_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String passwordone = et_one.getText().toString().trim();
                    String passwordtwo = et_two.getText().toString().trim();
                    if (passwordone.isEmpty() || passwordtwo.isEmpty()) {
                        Toast.makeText(
                                mContext, "密码不能为空，请重新输入！", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    } else {
                        //保存密码
                        try {
                            if (MyUtils.md5AddKey(passwordone).equals(MyUtils.md5AddKey
                                    (passwordtwo))) {
                                SharedPreferences.Editor editor = mSharedPreferences.edit();
                                editor.putString(Constant.APPSLOCK_PASSWORD, MyUtils.md5AddKey
                                        (passwordone));
                                editor.putBoolean("applock", true);
                                editor.commit();
                                Toast.makeText(
                                        mContext, "密码设置成功！请牢记密码！", Toast.LENGTH_SHORT)
                                        .show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(
                                        mContext, "两次新密码输入不相同，请重新输入！", Toast.LENGTH_SHORT)
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
        }
        //下面三句一起的效果是默认得到焦点
        et_one.setFocusable(true);
        et_one.setFocusableInTouchMode(true);
        et_one.requestFocus();
        //延迟弹出键盘 必须要有上面三句代码的支持（已经测试过了）
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager input =
                        (InputMethodManager) mContext.getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                input.showSoftInput(et_one, 0);
            }
        }, 100);

        dialog = builder.create();
        dialog.setView(v, 0, 0, 0, 0);
        dialog.show();
    }

}
