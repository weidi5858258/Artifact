package com.weidi.artifact.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.weidi.artifact.R;


public class SecurityPhoneActivity extends Activity {
	private SharedPreferences sp = null;
	private TextView activity_securityphone_resetup = null;
	private TextView activity_securityphone = null;
	private ImageView activity_securityphone_iv = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_securityphone);
		
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		
		activity_securityphone = (TextView) this.findViewById(R.id.activity_securityphone);
		activity_securityphone_iv = (ImageView) this.findViewById(R.id.activity_securityphone_iv);
		activity_securityphone_resetup = (TextView) this.findViewById(R.id.activity_securityphone_resetup);
		
		String number = sp.getString("security_phone", "");
		activity_securityphone.setText(number);
		
		boolean flag = sp.getBoolean("antitheftprotection", false);
		if(flag){
			activity_securityphone_iv.setImageResource(R.drawable.lock);
		}else{
			activity_securityphone_iv.setImageResource(R.drawable.unlock);
		}
		
		//重新设置
		activity_securityphone_resetup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SecurityPhoneActivity.this, SecurityPhoneSetup1Activity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onNewIntent(Intent intent) {
		String number = sp.getString("security_phone", "");
		activity_securityphone.setText(number);
		number = null;
		
		boolean flag = sp.getBoolean("antitheftprotection", false);
		if(flag){
			activity_securityphone_iv.setImageResource(R.drawable.lock);
		}else{
			activity_securityphone_iv.setImageResource(R.drawable.unlock);
		}
	}
	
	
}
