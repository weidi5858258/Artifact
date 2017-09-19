package com.weidi.artifact.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.utils.MyToast;


public class ContactsActivity extends Activity{
	private ListView lv_contacts;
	private List<String> list;
	private ContactsAdapter adapter;
	private static int RESULTCODE = 200;
	
	private ProgressBar pb_contacts_progress;
	private TextView tv_contacts_alert;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contacts);
		lv_contacts = (ListView) this.findViewById(R.id.lv_contacts);
		pb_contacts_progress = (ProgressBar) this.findViewById(R.id.pb_contacts_progress);
		tv_contacts_alert = (TextView) this.findViewById(R.id.tv_contacts_alert);
		new Thread(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pb_contacts_progress.setVisibility(ProgressBar.VISIBLE);
						tv_contacts_alert.setVisibility(ProgressBar.VISIBLE);
					}
				});
				list = getContactsNameAndNumber();
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						pb_contacts_progress.setVisibility(ProgressBar.INVISIBLE);
						tv_contacts_alert.setVisibility(ProgressBar.INVISIBLE);
						adapter = new ContactsAdapter();
						lv_contacts.setAdapter(adapter);
					}
				});
			}
		}).start();
	}
	
	private class ContactsAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = null;
			ViewHolder holder = null;
			if(convertView == null){
				view = View.inflate(ContactsActivity.this, R.layout.contacts_view_item, null);
				holder = new ViewHolder();
				holder.tv_contacts_name = (TextView) view.findViewById(R.id.tv_contacts_name);
				holder.tv_contacts_number = (TextView) view.findViewById(R.id.tv_contacts_number);
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			
			if(list.get(position).contains("☆")){//没有"☆"的，不是有效的联系人
				String name = list.get(position).substring(list.get(position).lastIndexOf("☆")+1);
				String number = list.get(position).substring(0, list.get(position).lastIndexOf("☆"));
				if(number.contains("☆")){
					String[] str = number.split("☆");
					for(String s : str){
						if(s.startsWith("1")){
							number = s;
							break;
						}
					}
				}
				holder.tv_contacts_name.setText("姓名："+name);
				holder.tv_contacts_number.setText("号码："+number);
				name = null;
				number = null;
			}
			
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(list.get(position).contains("☆")){
						String name = list.get(position).substring(list.get(position).lastIndexOf("☆")+1);
						String number = list.get(position).substring(0, list.get(position).lastIndexOf("☆"));
						if(number.contains("☆")){//某个联系人有多个号码
							String[] str = number.split("☆");
							boolean flag = true;
							for(String s : str){
								if(s.startsWith("1")){
									number = s;
									flag = false;
									break;
								}
							}
							if(flag){//表示没有手机号，因为手机号都是以“1”开头的
								MyToast.show("不是手机号，不能作为安全号码");
								return;
							}
						}else if(!number.startsWith("1")){
							MyToast.show("不是手机号，不能作为安全号码");
							return;
						}
						
						Intent data = new Intent(ContactsActivity.this,SecurityPhoneSetup3Activity.class);
						data.putExtra("number", number);
						setResult(RESULTCODE, data);
						finish();
					}
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
		TextView tv_contacts_name;
		TextView tv_contacts_number;
	}
	
	private List<String> getContactsNameAndNumber(){
		List<String> list = new ArrayList<String>();
		Map<String, String> map = null;
		Uri raw_contacts_uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri data_uri = Uri.parse("content://com.android.contacts/data");
		ContentResolver cr = ContactsActivity.this.getContentResolver();
		Cursor raw_contacts_cursor = cr.query(raw_contacts_uri, new String[]{"contact_id"}, null, null, null);
		boolean flag = false;
		StringBuffer sb = new StringBuffer();
		if(raw_contacts_cursor != null){
			while(raw_contacts_cursor.moveToNext()){
				String contact_id = raw_contacts_cursor.getString(0);//有值 碰到一个值是null
				if(contact_id != null){
					Cursor data_cursor = cr.query(data_uri, new String[]{"mimetype","data1"}, "raw_contact_id=?", new String[]{contact_id}, null);
					if(data_cursor != null){
						while(data_cursor.moveToNext()){
							String mimetype = data_cursor.getString(0);
							String data = data_cursor.getString(1);
							if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
								sb.append(data).append("☆");
							}else if("vnd.android.cursor.item/name".equals(mimetype)){
								flag = true;
								sb.append(data);
							} 
							if(flag){
								list.add(sb.toString());
								sb = new StringBuffer();
								flag = false;
							}
						}
						data_cursor.close();
					}
				}
			}
			raw_contacts_cursor.close();
		}
		return list;
	}
}
