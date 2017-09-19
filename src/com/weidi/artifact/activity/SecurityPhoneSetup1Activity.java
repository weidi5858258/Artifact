package com.weidi.artifact.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.weidi.artifact.R;


public class SecurityPhoneSetup1Activity extends BaseGestureActivity {
	private Button setup1_next = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_security_setup1);
		setup1_next = (Button) this.findViewById(R.id.setup1_next);
		setup1_next.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				goNext();
			}
		});
	}
	@Override
	public void goPrevious() {}
	@Override
	public void goNext() {
		Intent intent = new Intent(SecurityPhoneSetup1Activity.this, SecurityPhoneSetup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.setup_next_in, R.anim.setup_next_out);
	}
}
