package com.weidi.artifact.activity;


import android.app.Dialog;
import android.content.Context;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.weidi.artifact.R;

public class DialogHelper extends Dialog implements android.view.View.OnClickListener{
	private Callback callback;
	private EditText add_delete_packagename_content;
	private Button add_delete_packagename_sure;
	private Button add_delete_packagename_cancel;
	private int SURE = 1;
	public DialogHelper(Context context,Callback callback) {
		super(context);
		this.callback = callback;
		View view = this.getLayoutInflater().inflate(R.layout.add_delete_packagename_view, null);
		view.setMinimumWidth(600);
		add_delete_packagename_content = (EditText) view.findViewById(R.id.add_delete_packagename_content);
		add_delete_packagename_sure = (Button) view.findViewById(R.id.add_delete_packagename_sure);
		add_delete_packagename_cancel = (Button) view.findViewById(R.id.add_delete_packagename_cancel);
//		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
//		params.width = WindowManager.LayoutParams.MATCH_PARENT;
//		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		add_delete_packagename_sure.setOnClickListener(this);
		add_delete_packagename_cancel.setOnClickListener(this);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//		wm.addView(view, params);
		this.setContentView(view);
		this.setCancelable(false);
	}

	@Override
	public void onClick(View v) {
		String content = add_delete_packagename_content.getText().toString().trim();
		Message msg = new Message();
		switch(v.getId()){
			case R.id.add_delete_packagename_sure:{
				msg.what = SURE;
				msg.obj = content;
				callback.handleMessage(msg);
				break;
			}
			case R.id.add_delete_packagename_cancel:{
				this.dismiss();
				break;
			}
		}
	}
	
}
