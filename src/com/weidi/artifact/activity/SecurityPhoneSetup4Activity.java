package com.weidi.artifact.activity;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.weidi.artifact.R;
import com.weidi.artifact.ui.CheckBoxItemAntitheftProtection;

public class SecurityPhoneSetup4Activity extends BaseGestureActivity implements OnClickListener{
	private Button setup4_previous = null;
	private Button setup4_success = null;
	private CheckBoxItemAntitheftProtection cbiap = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_security_setup4);
		
		cbiap = (CheckBoxItemAntitheftProtection) this.findViewById(R.id.tv_security_antitheftprotection);
		setup4_previous = (Button) this.findViewById(R.id.setup4_previous);
		setup4_success = (Button) this.findViewById(R.id.setup4_success);
		
		setup4_previous.setOnClickListener(this);
		setup4_success.setOnClickListener(this);
		cbiap.setOnClickListener(this);
		
		boolean flag = sp.getBoolean("antitheftprotection", false);//antitheftprotection防盗保护
		if(flag){
			cbiap.setChecked(true);
			cbiap.setTVContent("防盗保护已经开启");
		}else{
			cbiap.setChecked(false);
			cbiap.setTVContent("防盗保护已经关闭");
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.tv_security_antitheftprotection:{
				if(cbiap.isChecked()){
					cbiap.setChecked(false);
					cbiap.setTVContent("防盗保护已经关闭");
				}else{
					cbiap.setChecked(true);
					cbiap.setTVContent("防盗保护已经开启");
				}
				break;
			}
			case R.id.setup4_previous:{
				goPrevious();
				break;
			}
			case R.id.setup4_success:{
				goNext();
				break;
			}
		}
	}
	@Override
	public void goPrevious() {
		Intent intent = new Intent();
		intent.setClass(SecurityPhoneSetup4Activity.this, SecurityPhoneSetup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.setup_previous_in, R.anim.setup_previous_out);
	}
	@Override
	public void goNext() {
		Editor editor = sp.edit();
		if(cbiap.isChecked()){
			editor.putBoolean("antitheftprotection", true);
		}else{
			editor.putBoolean("antitheftprotection", false);
		}
		editor.putBoolean("security_setup", true);
		editor.commit();
		editor = null;
		
		//进入防盗页面
		Intent intent = new Intent(SecurityPhoneSetup4Activity.this, SecurityPhoneActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.setup_next_in, R.anim.setup_next_out);
	}
}
