package com.weidi.artifact.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.utils.MyToast;
import com.weidi.utils.MyUtils;


public class ReceiveSMSsActivity extends Activity implements OnClickListener{
	private Context mContext;
	private FragmentManager fm;
	private FragmentTransaction ft;
	private Intent in;
	
	private ImageView iv_addresser_portrait;//头像
	private TextView iv_addresser_name;//发件人姓名
	private TextView iv_addresser_number;//发件人号码
	private ImageView iv_addresser_cancel;//取消信息，设为未读
	
	private ImageButton iv_previous;
	private LinearLayout ll_storesmsbody_container;//短信内容的容器
	private ImageButton iv_next;
	
	private TextView tv_sms_counts;//短信条数
	private TextView tv_sms_receive_time;//收到短信时的时间
	
	private ImageView iv_operation_delete;//删除短信
	private TextView iv_operation_markread;//已读短信
	private TextView iv_operation_reply;//回复短信
	private EditText et_sendsms;
	private ImageButton ib_go;
	
	private ReceiveSMSBroadcastReceiver smsBR;
	private IntentFilter filter;
	
	private String name;
	private String address;
	private String body;
	private String time;
	private String receiveTime;
	
	private Uri uri = Uri.parse("content://Sms");
	private ContentResolver cr;
	private List<Fragment> list;
	private List<Map<String,String>> mapList;
	private Map<String,String> map;
	private HashMap<String, String> hashMap;
	
	private NotificationManager nm;
	private SmsManager sm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		setContentView(R.layout.activity_receive_sms_window);
		mContext = ReceiveSMSsActivity.this;
		fm = getFragmentManager();
		list = new ArrayList<Fragment>();
		mapList = new ArrayList<Map<String,String>>();
		nm = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		sm = SmsManager.getDefault();
		cr = getContentResolver();
		
		iv_addresser_portrait = (ImageView) this.findViewById(R.id.iv_addresser_portrait);
		iv_addresser_name = (TextView) this.findViewById(R.id.iv_addresser_name);
		iv_addresser_number = (TextView) this.findViewById(R.id.iv_addresser_number);
		iv_addresser_cancel = (ImageView) this.findViewById(R.id.iv_addresser_cancel);
		
		//存放短信内容的容器 每条内容放在一个framement中
		iv_previous = (ImageButton) this.findViewById(R.id.iv_previous);
		ll_storesmsbody_container = (LinearLayout) this.findViewById(R.id.ll_storesmsbody_container);
		iv_next = (ImageButton) this.findViewById(R.id.iv_next);
		
		tv_sms_counts = (TextView) this.findViewById(R.id.tv_sms_counts);
		tv_sms_receive_time = (TextView) this.findViewById(R.id.tv_sms_receive_time);
		
		iv_operation_delete = (ImageView) this.findViewById(R.id.iv_operation_delete);
		iv_operation_markread = (TextView) this.findViewById(R.id.iv_operation_markread);
		iv_operation_reply = (TextView) this.findViewById(R.id.iv_operation_reply);
		et_sendsms = (EditText) this.findViewById(R.id.et_sendsms);
		ib_go = (ImageButton) this.findViewById(R.id.ib_go);
		
		iv_previous.setOnClickListener(this);
		iv_next.setOnClickListener(this);
		iv_addresser_cancel.setOnClickListener(this);
		iv_operation_delete.setOnClickListener(this);
		iv_operation_markread.setOnClickListener(this);
		iv_operation_reply.setOnClickListener(this);
		ib_go.setOnClickListener(this);
		
		et_sendsms.setVisibility(EditText.GONE);
		ib_go.setVisibility(ImageButton.GONE);
		
		//注册自定义广播
		smsBR = new ReceiveSMSBroadcastReceiver();
		filter = new IntentFilter();
		filter.addAction("com.aowin.mobilesafe.Sms");
		registerReceiver(smsBR, filter);
		
		//下面是第一次接收到短信时的操作
		in = getIntent();
		name = in.getStringExtra("name");
		address = in.getStringExtra("address");
		body = in.getStringExtra("body");
		time = in.getStringExtra("time");
		receiveTime = in.getStringExtra("receiveTime");
		map = new HashMap<String, String>();
		map.put("name", name);
		map.put("address", address);
		map.put("time", time);
		map.put("receiveTime", receiveTime);
		mapList.add(map);
		
		//赋内容
		iv_addresser_name.setText(name);
		iv_addresser_number.setText(address);
		tv_sms_receive_time.setText(time);
		tv_sms_counts.setText(j+"/"+k);
		
		StoreSmsBodyFragment fragment = new StoreSmsBodyFragment();
		Bundle bundle = new Bundle();
		bundle.putString("body", body);
		fragment.setArguments(bundle);
		list.add(fragment);
		
		ft = fm.beginTransaction();
		ft.replace(R.id.ll_storesmsbody_container, list.get(0));
		ft.commit();
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(smsBR);
		smsBR = null;
		list.clear();
		list = null;
	}

	int i = 1;//用于控制短信翻页
	int j = 1;//用于显示当前是第几条短信
	int k = 1;//用于显示总条数
	boolean flag = true;
	@Override
	public void onClick(View v) {
		ft = fm.beginTransaction();
		switch(v.getId()){
			case R.id.iv_previous:{
				i--; 
				if(i > 0 && i < k){
					HashMap<String, String> hashMap = (HashMap<String, String>) mapList.get(i - 1);
					name = hashMap.get("name");
					address = hashMap.get("address");
					time = hashMap.get("time");
					iv_addresser_name.setText(name);
					iv_addresser_number.setText(address);
					tv_sms_receive_time.setText(time);
					j = i;
					tv_sms_counts.setText(j+"/"+k);
//					System.out.println("i:"+i+" j:"+j);
					ft.replace(R.id.ll_storesmsbody_container, list.get(i - 1));
				}else if(i <= 0){
					i = 1;
//					System.out.println("i:"+i+" j:"+j);
					return;
				}
				break;
			}
			case R.id.iv_next:{
				i++;
				if(i > 0 && i <= k){
					HashMap<String, String> hashMap = (HashMap<String, String>) mapList.get(i - 1);
					name = hashMap.get("name");
					address = hashMap.get("address");
					time = hashMap.get("time");
					iv_addresser_name.setText(name);
					iv_addresser_number.setText(address);
					tv_sms_receive_time.setText(time);
					j = i;
					tv_sms_counts.setText(j+"/"+k);
//					System.out.println("i:"+i+" j:"+j);
					ft.replace(R.id.ll_storesmsbody_container, list.get(i - 1));
				}else if(i > k){
					i = k;
//					System.out.println("i:"+i+" j:"+j);
					return;
				}
				break;
			}
			case R.id.iv_addresser_cancel:{
				cancel();//取消短信：如果只有一条短信就关闭窗口，则把这条短信置为未读状态；如果有多条，则点击一下，把这条短信置为未读状态，然后在总条数中减1。
				break;
			}
			case R.id.iv_operation_delete:{//1356047432329这个数据进行测试
				delete();//删除短信，不把短信存入数据库中
				break;
			}
			case R.id.iv_operation_markread:{
				markread();//已读短信：把这条短信存在数据库。然后情况与“取消短信”一样。
				break;
			}
			case R.id.iv_operation_reply:{
				reply();//如果有多条短信时，回复当前用户。
				break;
			}
			case R.id.ib_go:{
				sendSms();//发送短信
				break;
			}
		}
		ft.commit();
	}
	
	private void cancel(){
		Notification notification = new Notification(R.drawable.gee,"亲，来短信啦",0);
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.addCategory(Intent.CATEGORY_APP_MESSAGING);
		PendingIntent pending = PendingIntent.getActivity(mContext, 0, intent, 0);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
//		notification.setLatestEventInfo(mContext, "您有新的短信", "打开看看吧", pending);
		nm.notify(0, notification);
		finish();
	}
	private void delete(){
		hashMap = (HashMap<String, String>) mapList.get(j - 1);
		//有多条短信时根据时间删不行
		receiveTime = map.get("receiveTime");
		cr.delete(uri, "date=?", new String[]{receiveTime});
		mapList.remove(j - 1);
		list.remove(j - 1);
		k--;
		if(k == 0){
			finish();
			return;
		}
		if(i > 0){
			i--;
		}
		if(j > 1){
			j--;
		}
		hashMap = (HashMap<String, String>) mapList.get(j - 1);
		name = hashMap.get("name");
		address = hashMap.get("address");
		body = hashMap.get("body");
		time = hashMap.get("time");
		
		iv_addresser_name.setText(name);
		iv_addresser_number.setText(address);
		tv_sms_receive_time.setText(time);
		tv_sms_counts.setText(j+"/"+k);
		
		StoreSmsBodyFragment fragment = (StoreSmsBodyFragment) list.get(j - 1);
		fragment.iv_addresser_content.setText(body);
		ft.replace(R.id.ll_storesmsbody_container, fragment);
		
//		System.out.println("i:"+i+" j:"+j+" k:"+k);
	}
	private void markread(){
		mapList.remove(j - 1);
		list.remove(j - 1);
		k--;
		if(k == 0){
			finish();
			return;
		}
		if(i > 0){
			i--;
		}
		if(j > 1){
			j--;
		}
		hashMap = (HashMap<String, String>) mapList.get(j - 1);
		name = hashMap.get("name");
		address = hashMap.get("address");
		body = hashMap.get("body");
		time = hashMap.get("time");
		
		iv_addresser_name.setText(name);
		iv_addresser_number.setText(address);
		tv_sms_receive_time.setText(time);
		tv_sms_counts.setText(j+"/"+k);
		
		StoreSmsBodyFragment fragment = (StoreSmsBodyFragment) list.get(j - 1);
		fragment.iv_addresser_content.setText(body);
		ft.replace(R.id.ll_storesmsbody_container, fragment);
	}
	private void reply(){
		if(flag){
			et_sendsms.setVisibility(EditText.VISIBLE);
			ib_go.setVisibility(EditText.VISIBLE);
			MyUtils.popupKeyboard(mContext);
			flag = false;
		}else{
			et_sendsms.setVisibility(EditText.GONE);
			ib_go.setVisibility(EditText.GONE);
			MyUtils.hideKeyboard(this);
			flag = true;
		}
		
//		in = new Intent();
//		in.setAction("com.aowin.mobilesafe.Sms");
//		in.putExtra("name", "山东兄弟"+(k+1));
//		in.putExtra("address", "河北");
//		in.putExtra("body", "呵呵"+(k+1)+"---k:"+(k+1));
//		in.putExtra("time", "今天");
//		mContext.sendBroadcast(in);
	}
	private void sendSms(){
		String content = et_sendsms.getText().toString();
		HashMap<String, String> hashMap = (HashMap<String, String>) mapList.get(j - 1);
		String number = hashMap.get("address");
		if(TextUtils.isEmpty(number)){
			MyToast.show("亲，我不知道您要发到哪儿去？");
			return;
		}
		sm.sendTextMessage(number, null, content, null, null);
		cr = getContentResolver();
		ContentValues cv = new ContentValues();
		long sendTime = System.currentTimeMillis();
		cv.put("address", number);
		cv.put("date", sendTime);
		cv.put("read", 1);//1为已读 0为未读
		cv.put("type", 2);//1为接收到的短信 2为发送的短信
		cv.put("body", content);
		cr.insert(uri, cv);
		MyToast.show("已发送");
	}
	
	private class ReceiveSMSBroadcastReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent != null){
				i++;
				k++;
				in = intent;
				name = intent.getStringExtra("name");
				address = intent.getStringExtra("address");
				body = intent.getStringExtra("body");
				time = intent.getStringExtra("time");
				receiveTime = intent.getStringExtra("receiveTime");
			}
			if(in != null){
				iv_previous.setVisibility(ImageButton.VISIBLE);
				iv_next.setVisibility(ImageButton.VISIBLE);
				//赋内容
				iv_addresser_name.setText(name);
				iv_addresser_number.setText(address);
				tv_sms_receive_time.setText(time);
				map = new HashMap<String, String>();
				map.put("name", name);
				map.put("address", address);
				map.put("time", time);
				map.put("receiveTime", receiveTime);
				mapList.add(map);
				j = k;
				tv_sms_counts.setText(j+"/"+k);
//				System.out.println("i:"+i+" j:"+j+" k:"+k);
				StoreSmsBodyFragment fragment = new StoreSmsBodyFragment();
				list.add(fragment);
				Bundle bundle = new Bundle();
				bundle.putString("body", body);
				fragment.setArguments(bundle);
				ft = fm.beginTransaction();
				ft.replace(R.id.ll_storesmsbody_container, fragment);
				ft.commit();
			}
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME){
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
