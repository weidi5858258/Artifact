package com.weidi.artifact.activity;

import java.util.List;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.db.dao.BlacklistDao;
import com.weidi.artifact.db.bean.BlacklistPhone;
import com.weidi.artifact.ui.CheckBoxItemBlacklistPhone;

public class CommunicationGuardBlacklistPhoneFragment extends Fragment implements OnClickListener{
	private Context mContext;
	private ListView lv_communicationguard_phone;
	private BlacklistPhoneAdapter adapter;
	private ViewHolder holder;
	private CheckBoxItemBlacklistPhone cbibp;
	private List<BlacklistPhone> list;
	private BlacklistDao dao;
	private UpdateAdapterBroadcastReceiver updateAdapterBR;
	private IntentFilter filter;
	private BlacklistPhone phone;
	/* (non-Javadoc)
	 * @see android.app.BaseFragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		dao = new BlacklistDao(mContext);
		list = dao.queryBlacklistPhone();
		View view = inflater.inflate(com.weidi.artifact.R.layout.activity_communicationguard_blacklist_phone, null);
		lv_communicationguard_phone = (ListView) view.findViewById(R.id.lv_communicationguard_phone);
		adapter = new BlacklistPhoneAdapter();
		lv_communicationguard_phone.setAdapter(adapter);
		lv_communicationguard_phone.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
			}
		});
		
		updateAdapterBR = new UpdateAdapterBroadcastReceiver();
		//在CoreService中发送的
		filter = new IntentFilter();
		filter.addAction("com.aowin.mobilesafe.updateadapter.phone");
		getActivity().registerReceiver(updateAdapterBR, filter);
		
		return view;
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(updateAdapterBR);
		updateAdapterBR = null;
	}



	private class BlacklistPhoneAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				holder = new ViewHolder();
				cbibp = new CheckBoxItemBlacklistPhone(mContext);
				holder.tv_blacklistphone_number = cbibp.tv_blacklistphone_number;
				holder.tv_blacklistphone_address = cbibp.tv_blacklistphone_address;
				holder.tv_blacklistphone_time = cbibp.tv_blacklistphone_time;
				holder.tv_blacklistphone_duration = cbibp.tv_blacklistphone_duration;
//				holder.cb_blacklistphone_check = cbibp.cb_blacklistphone_check;
				cbibp.setTag(holder);
			}else{
				cbibp = (CheckBoxItemBlacklistPhone) convertView;
				holder = (ViewHolder) cbibp.getTag();
			}
			phone = list.get(position);
			holder.tv_blacklistphone_number.setText(phone.getNumber());
			holder.tv_blacklistphone_address.setText(phone.getAddress());
			holder.tv_blacklistphone_time.setText(phone.getTime());
			//打进来的电话已经好了
			if(0 == phone.getFlag()){
				holder.tv_blacklistphone_duration.setText("响铃时间（打进）："+phone.getDuration()+"秒");
			}else if(1 == phone.getFlag()){
				holder.tv_blacklistphone_duration.setText("通话时间（打进）："+phone.getDuration()+"秒");
			}else if(2 == phone.getFlag()){
				holder.tv_blacklistphone_duration.setText("被挂断（打进）");//黑名单
			}else if(3 == phone.getFlag()){
				holder.tv_blacklistphone_duration.setText("响铃时间（打出）："+phone.getDuration()+"秒");
			}else if(4 == phone.getFlag()){
				holder.tv_blacklistphone_duration.setText("通话时间（打出）："+phone.getDuration()+"秒");
			}
			return cbibp;
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
		TextView tv_blacklistphone_number;
		TextView tv_blacklistphone_address;
		TextView tv_blacklistphone_time;
		TextView tv_blacklistphone_duration;
//		CheckBox cb_blacklistphone_check;
	}
	
	private class UpdateAdapterBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent != null){
				phone = (BlacklistPhone) intent.getSerializableExtra("phone");
				list.add(0, phone);
				adapter.notifyDataSetChanged();
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			
			
		}
	}
	
}
