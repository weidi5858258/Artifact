package com.weidi.artifact.activity;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.weidi.artifact.R;
import com.weidi.utils.MyToast;

//设置安全号码
public class SecurityPhoneSetup3Activity extends BaseGestureActivity implements OnClickListener{
	private Button setup3_previous = null;
	private Button setup3_next = null;
	private EditText activity_security_setup3_phone;
	private Button activity_security_setup3_selectperson;
	private static int REQUESTCODE = 200;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_security_setup3);
		activity_security_setup3_phone = (EditText) findViewById(R.id.activity_security_setup3_phone);
		activity_security_setup3_selectperson = (Button) findViewById(R.id.activity_security_setup3_selectperson);
		setup3_previous = (Button) this.findViewById(R.id.setup3_previous);
		setup3_next = (Button) this.findViewById(R.id.setup3_next);
		activity_security_setup3_selectperson.setOnClickListener(this);
		setup3_previous.setOnClickListener(this);
		setup3_next.setOnClickListener(this);
		
		String number = sp.getString("security_phone", "");
		activity_security_setup3_phone.setText(number);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		StringBuffer sb = null;
		if(requestCode == 200 && resultCode == 200){
			String number = data.getStringExtra("number");
			if(number.contains("-")){
				sb = new StringBuffer();
				String[] str = number.split("-");
				for(String s : str){
					sb.append(s);
				}
				number = sb.toString();
			}else if(number.contains(" ")){
				sb = new StringBuffer();
				String[] str = number.split(" ");
				for(String s : str){
					sb.append(s);
				}
				number = sb.toString();
			}else if(number.startsWith("+86")){
				number = number.substring(3);
			}
			activity_security_setup3_phone.setText(number);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.activity_security_setup3_selectperson:{
				Intent intent = new Intent(SecurityPhoneSetup3Activity.this,ContactsActivity.class);
				startActivityForResult(intent, REQUESTCODE);
				break;
			}
			case R.id.setup3_previous:{
				goPrevious();
				break;
			}
			case R.id.setup3_next:{
				goNext();
				break;
			}
		}
	}
	@Override
	public void goPrevious() {
		Intent intent = new Intent();
		intent.setClass(SecurityPhoneSetup3Activity.this, SecurityPhoneSetup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.setup_previous_in, R.anim.setup_previous_out);
	}
	@Override
	public void goNext() {
		String number = activity_security_setup3_phone.getText().toString().trim();
		if(!TextUtils.isEmpty(number) && number.length() == 11 && number.startsWith("1")){
			Editor editor = sp.edit();
			editor.putString("security_phone", number);
			editor.commit();
			editor = null;
			Intent intent = new Intent();
			intent.setClass(SecurityPhoneSetup3Activity.this, SecurityPhoneSetup4Activity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.setup_next_in, R.anim.setup_next_out);
		}else if(TextUtils.isEmpty(number)){
			MyToast.show("没有设置安全手机号码，不能进行后面的操作");
			return;
		}else if(number.length() != 11 || !number.startsWith("1")){
			MyToast.show("设置的安全手机号码不合理，请重新设置");
			return;
		}
	}
}
