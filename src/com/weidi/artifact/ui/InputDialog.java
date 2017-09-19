package com.weidi.artifact.ui;

import com.weidi.artifact.R;
import com.weidi.utils.MyToast;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class InputDialog extends Dialog implements android.view.View.OnClickListener{
	private Context mContext;
	private Callback callback;
	private Message msg;
	private EditText et_inputdialog_input;
	private Button et_inputdialog_sure;
	private Button et_inputdialog_cancel;
	public InputDialog(Context context,int value,Callback callback) {
		super(context);
		mContext = context;
		this.callback = callback;
		View view = View.inflate(context, R.layout.inputdialog_view, null);
		view.setMinimumWidth(500);
		et_inputdialog_input = (EditText) view.findViewById(R.id.et_inputdialog_input);
		et_inputdialog_sure = (Button) view.findViewById(R.id.bt_inputdialog_sure);
		et_inputdialog_cancel = (Button) view.findViewById(R.id.bt_inputdialog_cancel);
		et_inputdialog_sure.setOnClickListener(this);
		et_inputdialog_cancel.setOnClickListener(this);
		et_inputdialog_input.setText(String.valueOf(value));
		
		this.setCancelable(false);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(view);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id == R.id.bt_inputdialog_sure){
			String content = et_inputdialog_input.getText().toString().trim();
			if(TextUtils.isEmpty(content)){
				MyToast.show("没有套餐要设置吗？点“取消”返回。");
				return;
			}else{
				msg = new Message();
				msg.obj = content;
				callback.handleMessage(msg);
			}
		}else if(id == R.id.bt_inputdialog_cancel){
			dismiss();
		}
	}

}
