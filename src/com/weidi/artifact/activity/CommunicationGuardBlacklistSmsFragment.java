package com.weidi.artifact.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.db.dao.BlacklistDao;
import com.weidi.artifact.db.bean.BlacklistSms;
import com.weidi.artifact.ui.CheckBoxItemBlacklistSms;
import com.weidi.utils.MyToast;

public class CommunicationGuardBlacklistSmsFragment extends Fragment implements OnClickListener{
	private Context mContext;
	private ListView lv_communicationguard_sms;
	private BlacklistSmsAdapter adapter;
	private ViewHolder holder;
	private CheckBoxItemBlacklistSms cbibs;
	private List<BlacklistSms> list;
	private BlacklistDao dao;
	private BlacklistSms sms;
	
	private UpdateAdapterBroadcastReceiver updateAdapterBR;
	private IntentFilter filter;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		dao = new BlacklistDao(mContext);
		list = dao.queryBlacklistSms();
		View view = inflater.inflate(com.weidi.artifact.R.layout.activity_communicationguard_blacklist_sms, null);
		lv_communicationguard_sms = (ListView) view.findViewById(R.id.lv_communicationguard_sms);
		adapter = new BlacklistSmsAdapter();
		lv_communicationguard_sms.setAdapter(adapter);
		
		lv_communicationguard_sms.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				//弹出一个菜单供用户选择
				popupMenu();
				return true;
			}
		});
		
		updateAdapterBR = new UpdateAdapterBroadcastReceiver();
		//在CoreService中发送的
		filter = new IntentFilter();
		filter.addAction("com.aowin.mobilesafe.updateadapter.Sms");
		getActivity().registerReceiver(updateAdapterBR, filter);
		
		return view;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		getActivity().unregisterReceiver(updateAdapterBR);
		updateAdapterBR = null;
	}



	private class BlacklistSmsAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				holder = new ViewHolder();
				cbibs = new CheckBoxItemBlacklistSms(mContext);
				holder.tv_blacklistsms_number = cbibs.tv_blacklistsms_number;
				holder.tv_blacklistsms_address = cbibs.tv_blacklistsms_address;
				holder.tv_blacklistsms_time = cbibs.tv_blacklistsms_time;
				holder.tv_blacklistsms_body = cbibs.tv_blacklistsms_body;
				holder.cb_blacklistsms_check = cbibs.cb_blacklistsms_check;
				cbibs.setTag(holder);
			}else{
				cbibs = (CheckBoxItemBlacklistSms) convertView;
				holder = (ViewHolder) cbibs.getTag();
			}
			holder.tv_blacklistsms_number.setText(list.get(position).getNumber());
			holder.tv_blacklistsms_address.setText(list.get(position).getAddress());
			holder.tv_blacklistsms_time.setText(list.get(position).getTime());
			holder.tv_blacklistsms_body.setText(list.get(position).getBody());
			
			return cbibs;
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
	
	class ViewHolder{
		TextView tv_blacklistsms_number;
		TextView tv_blacklistsms_address;
		TextView tv_blacklistsms_time;
		TextView tv_blacklistsms_body;
		CheckBox cb_blacklistsms_check;
	}
	
	private class UpdateAdapterBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent != null){
				sms = (BlacklistSms) intent.getSerializableExtra("sms");
				list.add(0, sms);//把短信添加到第一个位置后再通知更新
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	public void popupMenu(){//这样弹出一个窗口也蛮好的
		new AlertDialog.Builder(mContext).setTitle("请选择：")
										 .setItems(new String[]{"删除","恢复到手机信息","呼叫","回复短信","添加联系人","添加白名单","标记"}, new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												String mode = "";
													switch(which){
													case 0:mode = "删除";break;
													case 1:mode = "恢复到手机信息";break;
													case 2:mode = "呼叫";break;
													case 3:mode = "回复短信";break;
													case 4:mode = "添加联系人";break;
													case 5:mode = "添加白名单";break;
													case 6:mode = "标记";break;
												}
												MyToast.show(mode);
											}
										  })
										.show();
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			
		}
	}
	
}
