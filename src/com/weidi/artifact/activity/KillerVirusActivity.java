package com.weidi.artifact.activity;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.application.MyApplication;
import com.weidi.utils.MyUtils;

public class KillerVirusActivity extends Activity {
	private Context mContext;
	private ListView lv_killervirus_appname;
	private List<PackageInfo> list;
	private ImageView iv_killervirus_scan;
	private ProgressBar pb_killervirus_progress;
	private AppsManagerInfoAdapter adapter;
	private TextView tv_killervirus_info;
	private Thread myThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_killervirus);
		mContext = KillerVirusActivity.this;
		lv_killervirus_appname = (ListView) findViewById(R.id.lv_killervirus_appname);
		iv_killervirus_scan = (ImageView) findViewById(R.id.iv_killervirus_scan);
		tv_killervirus_info = (TextView) findViewById(R.id.tv_killervirus_info);
		pb_killervirus_progress = (ProgressBar) findViewById(R.id.pb_killervirus_progress);
		
		list = ((MyApplication)getApplication()).mPackageManager.getInstalledPackages(0);
		//旋转动画
		RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(1000);
		ra.setRepeatCount(Animation.INFINITE);
		iv_killervirus_scan.startAnimation(ra);
		
		pb_killervirus_progress.setMax(list.size());
		
		adapter = new AppsManagerInfoAdapter();
		lv_killervirus_appname.setAdapter(adapter);
		
		scanVirus();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(myThread != null){
			myThread = null;
		}
		adapter.vector.clear();
		adapter.notifyDataSetChanged();
	}
	
	private class AppsManagerInfoAdapter extends BaseAdapter{
		Vector<ScanInfo> vector;
		public AppsManagerInfoAdapter(){
			vector = new Vector<ScanInfo>();
		}
		
		public void add(ScanInfo info){
			vector.add(info);
		}
		
		public void addTop(ScanInfo info){
			vector.add(0, info);
		}
		
		@Override
		public int getCount() {
			if(vector.size() > 0){
				return vector.size();
			}else{
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, final ViewGroup parent) {
			TextView tv;
			ScanInfo info;
			if(convertView != null){
				tv = (TextView) convertView;
			}else{
				tv = new TextView(mContext);
			}
			info = vector.get(position);
			if(info.flag){
				tv.setTextColor(Color.RED);
				tv.setText("发现病毒："+info.appName);
			}else{
				tv.setTextColor(Color.GREEN);
				tv.setText("扫描安全："+info.appName);
			}
			
			return tv;
		}

	}
	
	private int count;
	public void scanVirus(){
		myThread = new Thread() {
			@Override
			public void run() {
				for(PackageInfo info : list){
					count++;
					final ScanInfo si = new ScanInfo();
					String source = info.applicationInfo.sourceDir;
					boolean isVirus = MyUtils.isMD5Exists(MyUtils.md5Sign(source));
					si.packageName = info.packageName;
					si.appName = (String) info.applicationInfo.loadLabel(((MyApplication)getApplication()).mPackageManager);
					si.flag = isVirus;
					runOnUiThread(new Runnable() {
						public void run() {
							adapter.addTop(si);
							pb_killervirus_progress.setProgress(count);
							adapter.notifyDataSetChanged();
						}
					});
					SystemClock.sleep(10);
				}
				runOnUiThread(new Runnable() {
					public void run() {
						tv_killervirus_info.setText("扫描完成！！！");
						iv_killervirus_scan.clearAnimation();
					}
				});
			}
		};
		myThread.start();
	}
	
	class ScanInfo{
		String packageName;
		String appName;
		boolean flag;
	}
	
	
}
