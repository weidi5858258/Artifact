package com.weidi.artifact.activity;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.weidi.artifact.R;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.ui.CheckBoxItemBindSIM;
import com.weidi.utils.MyToast;

public class SecurityPhoneSetup2Activity extends BaseGestureActivity implements OnClickListener {
    private TelephonyManager tm = null;
    private CheckBoxItemBindSIM activity_security_setup2_bindsim = null;
    private Button setup2_previous = null;
    private Button setup2_next = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//sp已经在父类中初始化过了
        setContentView(R.layout.activity_security_setup2);
        tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);

        activity_security_setup2_bindsim = (CheckBoxItemBindSIM) this.findViewById(R.id
                .activity_security_setup2_bindsim);
        setup2_previous = (Button) this.findViewById(R.id.setup2_previous);
        setup2_next = (Button) this.findViewById(R.id.setup2_next);

        activity_security_setup2_bindsim.setOnClickListener(this);
        setup2_previous.setOnClickListener(this);
        setup2_next.setOnClickListener(this);
        init();
    }

    //初始化
    private void init() {
        String sim = sp.getString(Constant.SIM, null);
        if (!TextUtils.isEmpty(sim)) {
            activity_security_setup2_bindsim.setTVContent("SIM卡已经绑定");
            activity_security_setup2_bindsim.setChecked(true);
        } else {
            activity_security_setup2_bindsim.setTVContent("SIM卡没有绑定");
            activity_security_setup2_bindsim.setChecked(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_security_setup2_bindsim: {
                Editor editor = sp.edit();
                if (activity_security_setup2_bindsim.isChecked()) {
                    activity_security_setup2_bindsim.setTVContent("SIM卡没有绑定");
                    activity_security_setup2_bindsim.setChecked(false);
                    editor.putString(Constant.SIM, null);
                } else {
                    int state = tm.getSimState();//没有SIM卡的时候state为1
                    if (state != 1) {
                        activity_security_setup2_bindsim.setTVContent("SIM卡已经绑定");
                        activity_security_setup2_bindsim.setChecked(true);
                        // String sim = tm.getSimSerialNumber();
                        String sim = tm.getLine1Number();
                        editor.putString(Constant.SIM, sim);
                        Toast.makeText(SecurityPhoneSetup2Activity.this,
                                "SIM卡绑定成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SecurityPhoneSetup2Activity.this,
                                "检测不到SIM卡", Toast.LENGTH_SHORT).show();
                    }
                }
                editor.commit();
                break;
            }
            case R.id.setup2_previous: {
                goPrevious();
                break;
            }
            case R.id.setup2_next: {
                goNext();
                break;
            }
        }
    }

    @Override
    public void goPrevious() {
        Intent intent = new Intent();
        intent.setClass(SecurityPhoneSetup2Activity.this, SecurityPhoneSetup1Activity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.setup_previous_in, R.anim.setup_previous_out);
    }

    @Override
    public void goNext() {
        if (activity_security_setup2_bindsim.isChecked()) {
            Intent intent = new Intent();
            intent.setClass(SecurityPhoneSetup2Activity.this, SecurityPhoneSetup3Activity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.setup_next_in, R.anim.setup_next_out);
        } else {
            MyToast.show("SIM卡还没有绑定，不能进行后面的操作");
            return;
        }
    }
}
