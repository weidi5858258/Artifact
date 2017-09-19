package com.weidi.artifact.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.db.dao.BlacklistDao;
import com.weidi.artifact.db.bean.BlacklistInfo;
import com.weidi.artifact.ui.CheckBoxItemAddBlacklist;
import com.weidi.utils.MyToast;

public class CommunicationGuardBlacklistFragment extends Fragment implements OnClickListener{
	private Context mContext;
	private ListView lv_communicationguard_blacklist;
	private Button iv_communicationguard_add;
	private List<BlacklistInfo> list;
	private BlacklistAdapter adapter;
	private BlacklistDao dao;
	private AlertDialog.Builder builder = null;
	private AlertDialog dialog = null;
	
	private static int index;
	//弹出框的控件
	private TextView tv_blacklist_title = null;
	private EditText et_blacklist_name = null;
	private EditText et_blacklist_number = null;
	private CheckBoxItemAddBlacklist cb_blacklist_phone = null;
	private CheckBoxItemAddBlacklist cb_blacklist_message = null;
	private Button bt_blacklist_sure = null;
	private Button bt_blacklist_cancel = null;
//	private Button bt_communicationguard_phone;
//	private Button bt_communicationguard_blacklist;
//	private Button bt_communicationguard_sms;
	private String addOrAlter = "";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		View view = inflater.inflate(com.weidi.artifact.R.layout.activity_communicationguard, null);
		iv_communicationguard_add = (Button) view.findViewById(R.id.iv_communicationguard_add);
//		bt_communicationguard_phone = (Button) view.findViewById(R.id.bt_communicationguard_phone);
//		bt_communicationguard_blacklist = (Button) view.findViewById(R.id.bt_communicationguard_blacklist);
//		bt_communicationguard_sms = (Button) view.findViewById(R.id.bt_communicationguard_sms);
		iv_communicationguard_add.setOnClickListener(CommunicationGuardBlacklistFragment.this);
//		bt_communicationguard_phone.setOnClickListener(CommunicationGuardBlacklistFragment.this);
//		bt_communicationguard_blacklist.setOnClickListener(CommunicationGuardBlacklistFragment.this);
//		bt_communicationguard_sms.setOnClickListener(CommunicationGuardBlacklistFragment.this);
		lv_communicationguard_blacklist = (ListView) view.findViewById(R.id.lv_communicationguard_blacklist);
		dao = new BlacklistDao(mContext);
		list = dao.query();
		adapter = new BlacklistAdapter();
		lv_communicationguard_blacklist.setAdapter(adapter);
		return view;
	}
	
private class BlacklistAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if(convertView == null){
				view = View.inflate(mContext, R.layout.blacklist_view_item, null);
				holder = new ViewHolder();
				holder.tv_name = (TextView) view.findViewById(R.id.blacklist_name);
				holder.tv_number = (TextView) view.findViewById(R.id.blacklist_number);
				holder.tv_mode = (TextView) view.findViewById(R.id.blacklist_mode);
				holder.iv_delete = (ImageView) view.findViewById(R.id.blacklist_delete);
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			String name = list.get(position).getName();
			if(name == null){
				name = "大坏蛋";
			}
			holder.tv_name.setText("黑        人："+name);
			holder.tv_number.setText("黑号码："+list.get(position).getNumber());
			String mode = list.get(position).getMode();
			if("1".equals(mode)){
				mode = "拦截模式：电话拦截";
			}else if("2".equals(mode)){
				mode = "拦截模式：短信拦截";
			}else if("3".equals(mode)){
				mode = "拦截模式：电话拦截+短信拦截";
			}
			holder.tv_mode.setText(mode);
			
			/*final long[] mHits = new long[2];//双击事件 跟下面的代码放在一起的时候就没用了
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
					mHits[mHits.length-1] = SystemClock.uptimeMillis();
					if(mHits[0] >= (SystemClock.uptimeMillis() - 500)){
						MyUtils.showToast(mContext, "双击进行拦截模式的修改", 1);
					}
				}
			});*/
			//我现在实现不了单击发生这种事件，双击发生另外一种事件
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					addOrAlter = "修改";
					index = position;
					builder = new AlertDialog.Builder(mContext);
					dialog = builder.create();
					View view = View.inflate(mContext, R.layout.addblacklist_view, null);
					tv_blacklist_title = (TextView) view.findViewById(R.id.tv_blacklist_title);
					et_blacklist_name = (EditText) view.findViewById(R.id.et_blacklist_name);
					et_blacklist_number = (EditText) view.findViewById(R.id.et_blacklist_number);
					cb_blacklist_phone = (CheckBoxItemAddBlacklist) view.findViewById(R.id.cb_blacklist_phone);
					cb_blacklist_message = (CheckBoxItemAddBlacklist) view.findViewById(R.id.cb_blacklist_message);
					bt_blacklist_sure = (Button) view.findViewById(R.id.bt_blacklist_sure);
					bt_blacklist_cancel = (Button) view.findViewById(R.id.bt_blacklist_cancel);
					
					et_blacklist_name.setText(list.get(position).getName());
					et_blacklist_name.selectAll();//默认选中内容
					et_blacklist_number.setText(list.get(position).getNumber());
					et_blacklist_number.setFocusable(false);
					String mode = list.get(position).getMode();
					if("1".equals(mode)){
						cb_blacklist_phone.setChecked(true);
					}else if("2".equals(mode)){
						cb_blacklist_message.setChecked(true);
					}else if("3".equals(mode)){
						cb_blacklist_phone.setChecked(true);
						cb_blacklist_message.setChecked(true);
					}
					tv_blacklist_title.setText("修改拦截模式");
					cb_blacklist_phone.setTVColor(0xFFFCFCFC);
					cb_blacklist_phone.setTVContent("电话拦截");
					cb_blacklist_message.setTVColor(0xFFFCFCFC);
					cb_blacklist_message.setTVContent("短信拦截");
					
					cb_blacklist_phone.setOnClickListener(CommunicationGuardBlacklistFragment.this);
					cb_blacklist_message.setOnClickListener(CommunicationGuardBlacklistFragment.this);
					bt_blacklist_sure.setOnClickListener(CommunicationGuardBlacklistFragment.this);
					bt_blacklist_cancel.setOnClickListener(CommunicationGuardBlacklistFragment.this);
					dialog.setView(view, 0, 0, 0, 0);
					dialog.setCancelable(false);
					
					//下面三句一起的效果是默认得到焦点
					et_blacklist_name.setFocusable(true);
					et_blacklist_name.setFocusableInTouchMode(true);
					et_blacklist_name.requestFocus();
					//延迟弹出键盘 必须要有上面三句代码的支持（已经测试过了）
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							InputMethodManager input = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
							input.showSoftInput(et_blacklist_name, 0);
						}
					 }, 100);
					
					 dialog.show();
				}
			});
			
			holder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					builder = new AlertDialog.Builder(mContext);
					builder.setTitle("友情提示");
					builder.setMessage("确定要删除吗？");
					builder.setPositiveButton("确定", new  DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dao.delete(list.get(position).getNumber());
							list.remove(position);
							adapter.notifyDataSetChanged();
						}
					});
					builder.setNegativeButton("取消", null);
					builder.show();
				}
			});
			
			return view;
		}
		
		@Override
		public Object getItem(int position) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		
	}
	
	private class ViewHolder{
		TextView tv_name;
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_delete;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.iv_communicationguard_add:{
				addOrAlter = "添加";
				builder = new AlertDialog.Builder(mContext);
				dialog = builder.create();
				View view = View.inflate(mContext, R.layout.addblacklist_view, null);
				tv_blacklist_title = (TextView) view.findViewById(R.id.tv_blacklist_title);
				et_blacklist_name = (EditText) view.findViewById(R.id.et_blacklist_name);
				et_blacklist_number = (EditText) view.findViewById(R.id.et_blacklist_number);
				cb_blacklist_phone = (CheckBoxItemAddBlacklist) view.findViewById(R.id.cb_blacklist_phone);
				cb_blacklist_message = (CheckBoxItemAddBlacklist) view.findViewById(R.id.cb_blacklist_message);
				bt_blacklist_sure = (Button) view.findViewById(R.id.bt_blacklist_sure);
				bt_blacklist_cancel = (Button) view.findViewById(R.id.bt_blacklist_cancel);
				
				tv_blacklist_title.setText("添加黑名单");
				
				cb_blacklist_phone.setTVColor(0xFFFCFCFC);
				cb_blacklist_phone.setTVContent("电话拦截");
				cb_blacklist_message.setTVColor(0xFFFCFCFC);
				cb_blacklist_message.setTVContent("短信拦截");
				
				cb_blacklist_phone.setOnClickListener(this);
				cb_blacklist_message.setOnClickListener(this);
				bt_blacklist_sure.setOnClickListener(this);
				bt_blacklist_cancel.setOnClickListener(this);
				dialog.setView(view, 0, 0, 0, 0);
				dialog.setCancelable(false);
				
				//下面三句一起的效果是默认得到焦点
				et_blacklist_number.setFocusable(true);
				et_blacklist_number.setFocusableInTouchMode(true);
				et_blacklist_number.requestFocus();
				//延迟弹出键盘 必须要有上面三句代码的支持（已经测试过了）
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						InputMethodManager input = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
						input.showSoftInput(et_blacklist_number, 0);
					}
				}, 100);
				
				dialog.show();
				break;
			}
//			case R.id.bt_communicationguard_phone:{
//				
//				break;
//			}
//			case R.id.bt_communicationguard_blacklist:{
//				
//				break;
//			}
//			case R.id.bt_communicationguard_sms:{
//				
//				break;
//			}
			case R.id.cb_blacklist_phone:{
				if(cb_blacklist_phone.isChecked()){
					cb_blacklist_phone.setChecked(false);
				}else{
					cb_blacklist_phone.setChecked(true);
				}
				break;
			}
			case R.id.cb_blacklist_message:{
				if(cb_blacklist_message.isChecked()){
					cb_blacklist_message.setChecked(false);
				}else{
					cb_blacklist_message.setChecked(true);
				}
				break;
			}
			case R.id.bt_blacklist_sure:{
				String name = et_blacklist_name.getText().toString().trim();
				String number = et_blacklist_number.getText().toString().trim();
				if("修改".equals(addOrAlter)){
					if(!cb_blacklist_phone.isChecked() && !cb_blacklist_message.isChecked()){
						MyToast.show("必须选择一个拦截模式");
						return;
					}
					String mode = "";
					if(cb_blacklist_phone.isChecked() && !cb_blacklist_message.isChecked()){
						mode = "1";
					}else if(!cb_blacklist_phone.isChecked() && cb_blacklist_message.isChecked()){
						mode = "2";
					}else if(cb_blacklist_phone.isChecked() && cb_blacklist_message.isChecked()){
						mode = "3";
					}
					dao.update(name,number, mode);
					list.remove(index);
					BlacklistInfo info = new BlacklistInfo(name,number,mode);
					list.add(index, info);
					adapter.notifyDataSetChanged();
					dialog.dismiss();

				}else if("添加".equals(addOrAlter)){
					if(TextUtils.isEmpty(number)){
						MyToast.show("号码不能为空");
						Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
						et_blacklist_number.startAnimation(shake);
						return;
					}
					if(dao.isNumberExist(number)){
						MyToast.show("您输入的坏蛋的号码已经存在了");
						return;
					}
					if(!cb_blacklist_phone.isChecked() && !cb_blacklist_message.isChecked()){
						MyToast.show("必须选择一个拦截模式");
						return;
					}
					String mode = "";
					if(cb_blacklist_phone.isChecked() && !cb_blacklist_message.isChecked()){
						mode = "1";
					}else if(!cb_blacklist_phone.isChecked() && cb_blacklist_message.isChecked()){
						mode = "2";
					}else if(cb_blacklist_phone.isChecked() && cb_blacklist_message.isChecked()){
						mode = "3";
					}
					BlacklistInfo info = new BlacklistInfo(name,number,mode);
					dao.add(info);
					if(TextUtils.isEmpty(name)){
						info.setName("大坏蛋");
					}
					list.add(0, info);
					adapter.notifyDataSetChanged();
					dialog.dismiss();
				}
				break;
			}
			case R.id.bt_blacklist_cancel:{
				if(dialog != null){
					dialog.dismiss();
				}
				break;
			}
			
		}
	}
	
}
