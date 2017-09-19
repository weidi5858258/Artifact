package com.weidi.artifact.activity;

import com.weidi.artifact.R;
import com.weidi.artifact.db.dao.PhoneNumberAddressQueryUtils;
import com.weidi.utils.MyToast;
import com.weidi.utils.MyUtils;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PhoneNumberAddressQueryActivity extends Activity implements OnClickListener{
	private EditText phonenumberaddressquery_et_phonenumber;
	private Button phonenumberaddressquery_bt_query;
	private Button phonenumberaddressquery_bt_httpquery;
	private TextView phonenumberaddressquery_tv_show;
	private LinearLayout ll;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_phonenumberaddressquery);
		ll = (LinearLayout) findViewById(R.id.ll);
		phonenumberaddressquery_et_phonenumber = (EditText) findViewById(R.id.phonenumberaddressquery_et_phonenumber);
		phonenumberaddressquery_bt_query = (Button) findViewById(R.id.phonenumberaddressquery_bt_query);
		phonenumberaddressquery_bt_httpquery = (Button) findViewById(R.id.phonenumberaddressquery_bt_httpquery);
		phonenumberaddressquery_tv_show = (TextView) findViewById(R.id.phonenumberaddressquery_tv_show);
		
		ll.setOnClickListener(this);
		phonenumberaddressquery_bt_query.setOnClickListener(this);
		phonenumberaddressquery_bt_httpquery.setOnClickListener(this);
		phonenumberaddressquery_et_phonenumber.addTextChangedListener(new TextWatcher() {
			//下面三个是文本输入框中内容改变的时候回调
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(s != null && s.length() >= 3){
					String address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(s.toString());
					phonenumberaddressquery_tv_show.setText(address);
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}
	
	private static boolean flag = true;
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.phonenumberaddressquery_bt_query:{
				String phoneNumber = phonenumberaddressquery_et_phonenumber.getText().toString().trim();
				if(phoneNumber.matches("^\\d{3,25}$")){
					String address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(phoneNumber);
					phonenumberaddressquery_tv_show.setText(address);
				}else{
					MyToast.show("请输入至少3位的号码");
				}
				break;
			}
			case R.id.phonenumberaddressquery_bt_httpquery:{
				final String phoneNumber = phonenumberaddressquery_et_phonenumber.getText().toString().trim();
				if(phoneNumber.length() >= 3 && phoneNumber.length() <= 5 && phoneNumber.startsWith("0")){
					if(flag){
						new Thread(new Runnable() {
							@Override
							public void run() {
								flag = false;
								final String address = PhoneNumberAddressQueryUtils.phoneNumberAddressHttpQuery(phoneNumber);
								runOnUiThread(new Runnable() {
									public void run() {
										phonenumberaddressquery_tv_show.setText(address);
										flag = true;
									}
								});
							}
						}).start();
					}
				}else{
					MyToast.show("只能查区号的归属地，位数为3~5位");
				}
				break;
			}
			case R.id.ll:{
				MyUtils.hideKeyboard(PhoneNumberAddressQueryActivity.this);
				break;
			}
		}
	}
	
}
