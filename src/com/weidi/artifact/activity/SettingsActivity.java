package com.weidi.artifact.activity;

import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.weidi.artifact.R;
import com.weidi.artifact.service.AppsLockService;
import com.weidi.artifact.service.CoreService;
import com.weidi.artifact.service.PeriodicalSerialKillerService;
import com.weidi.artifact.service.ShowAttributionService;
import com.weidi.artifact.ui.CheckBoxItemAppLock;
import com.weidi.artifact.ui.CheckBoxItemBlacklist;
import com.weidi.artifact.ui.CheckBoxItemPeriodicalSerialKiller;
import com.weidi.artifact.ui.CheckBoxItemShowAttribution;
import com.weidi.artifact.ui.CheckBoxItemUpdate;
import com.weidi.utils.MyToast;
import com.weidi.utils.MyUtils;

public class SettingsActivity extends Activity implements OnClickListener {
    //好多的组件组合成了一个“组件”使用，就把这个“组件”当成一个普通的Button那样处理就好了
    private SharedPreferences sp;
    private CheckBoxItemUpdate settings_update;
    private CheckBoxItemShowAttribution settings_showattribution;
    private CheckBoxItemBlacklist settings_blacklist;
    private CheckBoxItemPeriodicalSerialKiller settings_periodicalserialkiller;
    private CheckBoxItemAppLock settings_applock;
    private TextView settings_applock_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_view);
        sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
        settings_update = (CheckBoxItemUpdate) this.findViewById(R.id.settings_update);
        settings_showattribution = (CheckBoxItemShowAttribution) this.findViewById(R.id
                .settings_showattribution);
        settings_blacklist = (CheckBoxItemBlacklist) this.findViewById(R.id.settings_blacklist);
        settings_periodicalserialkiller = (CheckBoxItemPeriodicalSerialKiller) this.findViewById
                (R.id.settings_periodicalserialkiller);
        settings_applock = (CheckBoxItemAppLock) this.findViewById(R.id.settings_applock);
        settings_applock_password = (TextView) this.findViewById(R.id.settings_applock_password);

        settings_update.setOnClickListener(this);
        settings_showattribution.setOnClickListener(this);
        settings_blacklist.setOnClickListener(this);
        settings_periodicalserialkiller.setOnClickListener(this);
        settings_applock.setOnClickListener(this);
        settings_applock_password.setOnClickListener(this);

        init();
    }

    //初始化
    private void init() {
        boolean flag = sp.getBoolean("update", false);
        if (flag) {
            settings_update.setChecked(true);
            settings_update.setTVContent("自动更新已经开启");
        } else {
            settings_update.setChecked(false);
            settings_update.setTVContent("自动更新已经关闭");
        }
        //服务活着就表示开启，服务死亡就表示关闭
        flag = MyUtils.isSpecificServiceAlive(SettingsActivity.this, "ShowAttributionService");
        if (flag) {
            settings_showattribution.setChecked(true);
            settings_showattribution.setTVContent("归属地显示已经开启");
        } else {
            settings_showattribution.setChecked(false);
            settings_showattribution.setTVContent("归属地显示已经关闭");
        }
        flag = MyUtils.isSpecificServiceAlive(SettingsActivity.this, "CoreService");
        if (flag) {
            settings_blacklist.setChecked(true);
            settings_blacklist.setTVContent("黑名单拦截已经开启");
        } else {
            settings_blacklist.setChecked(false);
            settings_blacklist.setTVContent("黑名单拦截已经关闭");
        }
        flag = MyUtils.isSpecificServiceAlive(SettingsActivity.this,
                "PeriodicalSerialKillerService");
        if (flag) {
            settings_periodicalserialkiller.setChecked(true);
            settings_periodicalserialkiller.setTVContent("周期性杀进程已经开启");
        } else {
            settings_periodicalserialkiller.setChecked(false);
            settings_periodicalserialkiller.setTVContent("周期性杀进程已经关闭");
        }
        flag = MyUtils.isSpecificServiceAlive(SettingsActivity.this, "AppsLockService");
        if (flag) {
            settings_applock.setChecked(true);
            settings_applock.setTVContent("程序锁已经开启");
        } else {
            settings_applock.setChecked(false);
            settings_applock.setTVContent("程序锁已经关闭");
        }
    }

    @Override
    public void onClick(View v) {
        Editor editor = sp.edit();
        Intent intent = null;
        switch (v.getId()) {
            //自动更新
            case R.id.settings_update: {
                if (settings_update.isChecked()) {
                    //这里让人感觉有点不合逻辑
                    //这里搞不明白，一定要这样设置，那个组合控件点击除CheckBox外的地方才有效
                    settings_update.setChecked(false);
                    settings_update.setTVContent("自动更新已经关闭");
                    editor.putBoolean("update", false);
                } else {
                    settings_update.setChecked(true);
                    settings_update.setTVContent("自动更新已经开启");
                    editor.putBoolean("update", true);
                }
                editor.commit();
                break;
            }
            //手机号归属地
            case R.id.settings_showattribution: {
                if (settings_showattribution.isChecked()) {
                    settings_showattribution.setChecked(false);
                    settings_showattribution.setTVContent("归属地显示已经关闭");
                    intent = new Intent(SettingsActivity.this, ShowAttributionService.class);
                    stopService(intent);
                } else {
                    settings_showattribution.setChecked(true);
                    settings_showattribution.setTVContent("归属地显示已经开启");
                    intent = new Intent(SettingsActivity.this, ShowAttributionService.class);
                    startService(intent);
                }
                editor.commit();
                break;
            }
            //黑名单拦截
            case R.id.settings_blacklist: {
                if (settings_blacklist.isChecked()) {
                    settings_blacklist.setChecked(false);
                    settings_blacklist.setTVContent("黑名单拦截已经关闭");
                    intent = new Intent(SettingsActivity.this, CoreService.class);
                    stopService(intent);
                } else {
                    settings_blacklist.setChecked(true);
                    settings_blacklist.setTVContent("黑名单拦截已经开启");
                    intent = new Intent(SettingsActivity.this, CoreService.class);
                    startService(intent);
                }
                break;
            }
            //周期性杀进程
            case R.id.settings_periodicalserialkiller: {
                if (settings_periodicalserialkiller.isChecked()) {
                    settings_periodicalserialkiller.setChecked(false);
                    settings_periodicalserialkiller.setTVContent("周期性杀进程已经关闭");
                    intent = new Intent(SettingsActivity.this, PeriodicalSerialKillerService.class);
                    stopService(intent);
                } else {
                    settings_periodicalserialkiller.setChecked(true);
                    settings_periodicalserialkiller.setTVContent("周期性杀进程已经开启");
                    intent = new Intent(SettingsActivity.this, PeriodicalSerialKillerService.class);
                    startService(intent);
                }
                break;
            }
            //程序锁
            case R.id.settings_applock: {
                boolean flag = sp.getBoolean("applock", false);
                if (flag) {
                    if (settings_applock.isChecked()) {
                        settings_applock.setChecked(false);
                        settings_applock.setTVContent("程序锁已经关闭");
                        intent = new Intent(SettingsActivity.this, AppsLockService.class);
                        stopService(intent);
                    } else {
                        settings_applock.setChecked(true);
                        settings_applock.setTVContent("程序锁已经开启");
                        intent = new Intent(SettingsActivity.this, AppsLockService.class);
                        startService(intent);
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
        final String password = sp.getString("appslock_password", "");
        boolean flag = sp.getBoolean("applock", false);
        View v = View.inflate(SettingsActivity.this, R.layout.activity_password, null);
        AlertDialog.Builder builder = new Builder(SettingsActivity.this);
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
            bt_ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String enterpassword = et_one.getText().toString().trim();
                    try {
                        if (password.equals(MyUtils.md5AddKey(enterpassword))) {
                            String passwordtwo = et_two.getText().toString().trim();
                            String passwordthree = et_three.getText().toString().trim();
                            if (passwordtwo.isEmpty() || passwordthree.isEmpty()) {
                                Toast.makeText(getApplicationContext(), "新密码不能为空，请重新输入！", 0).show();
                                return;
                            } else {
                                //保存密码
                                try {
                                    if (MyUtils.md5AddKey(passwordtwo).equals(MyUtils.md5AddKey
                                            (passwordthree))) {
                                        Editor editor = sp.edit();
                                        editor.putString("appslock_password", MyUtils.md5AddKey
                                                (passwordtwo));
                                        editor.putBoolean("applock", true);
                                        editor.commit();
                                        Toast.makeText(getApplicationContext(), "新密码设置成功！请牢记密码！",
                                                0).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(getApplicationContext(),
                                                "两次新密码输入不相同，请重新输入！", 0).show();
                                        et_two.setText("");
                                        et_three.setText("");
                                        return;
                                    }
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "原密码输入有误，请重新输入！", 0).show();
                            et_one.setText("");
                            return;
                        }
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                }
            });
            bt_cancel.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            //			dialog = builder.create();
            //			dialog.setView(v, 0, 0, 0, 0);//去掉不好看的边框
            //			dialog.show();
        } else {//还没有设置密码，现在开始设置密码
            bt_ok.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String passwordone = et_one.getText().toString().trim();
                    String passwordtwo = et_two.getText().toString().trim();
                    if (passwordone.isEmpty() || passwordtwo.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "密码不能为空，请重新输入！", 0).show();
                        return;
                    } else {
                        //保存密码
                        try {
                            if (MyUtils.md5AddKey(passwordone).equals(MyUtils.md5AddKey
                                    (passwordtwo))) {
                                Editor editor = sp.edit();
                                editor.putString("appslock_password", MyUtils.md5AddKey
                                        (passwordone));
                                editor.putBoolean("applock", true);
                                editor.commit();
                                Toast.makeText(getApplicationContext(), "密码设置成功！请牢记密码！", 0).show();
                                dialog.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "两次新密码输入不相同，请重新输入！", 0)
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
            bt_cancel.setOnClickListener(new OnClickListener() {
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
                InputMethodManager input = (InputMethodManager) SettingsActivity.this
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                input.showSoftInput(et_one, 0);
            }
        }, 100);

        dialog = builder.create();
        dialog.setView(v, 0, 0, 0, 0);
        dialog.show();
    }

}
