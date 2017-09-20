package com.weidi.artifact.activity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import com.weidi.artifact.R;
import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.db.bean.AppInfos;
import com.weidi.utils.MyUtils;

import android.app.Activity;
import android.content.Context;
//import android.content.pm.IPackageDataObserver;
//import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CleanCacheActivity extends Activity implements OnClickListener{
	private static final int STATUS = 0;
	private static final int SUCCESS = 1;
	private Context mContext;
	private ListView lv_cleancache_appcache;
	private Button bt_cleancache_deleteall;
	private ProgressBar pb_cleancache_progress;
	private TextView tv_cleancache_appname;
	private AppCacheAdapter adapter;
	private List<AppInfos> list;
	private Vector<AppInfos> cacheList;
	private int count;
	private long garbage;
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch(msg.what){
				case STATUS:{
					AppInfos info = (AppInfos) msg.obj;
					tv_cleancache_appname.setText(info.getAppName());
					pb_cleancache_progress.setProgress(count);
					break;
				}
				case SUCCESS:{
					if(cacheList.size() > 0){
						tv_cleancache_appname.setText("扫描完成！！！   找到"+Formatter.formatFileSize(mContext, garbage)+"垃圾！！！");
						bt_cleancache_deleteall.setVisibility(Button.VISIBLE);
						bt_cleancache_deleteall.setOnClickListener(CleanCacheActivity.this);
					}else{
						tv_cleancache_appname.setText("扫描完成！！！   您的系统很干净！！！");
					}
					break;
				}
			}
		}
		
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cleancache);
		mContext = CleanCacheActivity.this;
		lv_cleancache_appcache = (ListView) this.findViewById(R.id.lv_cleancache_appcache);
		bt_cleancache_deleteall = (Button) this.findViewById(R.id.bt_cleancache_deleteall);
		pb_cleancache_progress = (ProgressBar) this.findViewById(R.id.pb_cleancache_progress);
		tv_cleancache_appname = (TextView) this.findViewById(R.id.tv_cleancache_appname);
		
		list = MyUtils.getInstalledApplicationInfos(mContext);
		cacheList = new Vector<AppInfos>();
		pb_cleancache_progress.setMax(list.size());
		adapter = new AppCacheAdapter();
		lv_cleancache_appcache.setAdapter(adapter);
		
		
		//ListView注册的点击事件
		lv_cleancache_appcache.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					final int position, long id) {
				if(cacheList.size() > 1){
					AppInfos info = cacheList.get(position);
					long cache = info.getCache();
					garbage -= cache;
					tv_cleancache_appname.setText("还剩下"+Formatter.formatFileSize(mContext, garbage)+"垃圾！！！");
				}else{
					tv_cleancache_appname.setText("您的系统现在已经很干净了！！！");
				}
//				deleteApplicationCacheFiles(mContext,cacheList.get(position).getPackageName());
				cacheList.remove(position);
				adapter.notifyDataSetChanged();
			}
		});//注册的点击事件
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				for(final AppInfos info : list){
					count++;
					SystemClock.sleep(10);
//					getPackageSizeInfo(mContext, info.getPackageName());
					Message msg = Message.obtain();
					msg.what = STATUS;
					msg.obj = info;
					handler.sendMessage(msg);
				}
				Message msg = Message.obtain();
				handler.sendEmptyMessage(SUCCESS);
			}
		}).start();
		
	}
	
	private class AppCacheAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(cacheList.size() > 0){
				return cacheList.size();
			}else{
				return 0;
			}
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			AppInfos info;
			if(convertView == null){
				holder = new ViewHolder();
				view = View.inflate(mContext, R.layout.cleancache_view_item, null);
				holder.iv_cleancache_icon = (ImageView) view.findViewById(R.id.iv_cleancache_icon);
				holder.tv_cleancache_appname = (TextView) view.findViewById(R.id.tv_cleancache_appname);
				holder.tv_cleancache_cache = (TextView) view.findViewById(R.id.tv_cleancache_cache);
				holder.iv_cleancache_clean = (ImageView) view.findViewById(R.id.iv_cleancache_clean);
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			info = cacheList.get(position);
			holder.iv_cleancache_icon.setImageDrawable(info.getIcon());
			holder.tv_cleancache_appname.setText(info.getAppName());
			holder.tv_cleancache_cache.setText("Cache created："+Formatter.formatFileSize(mContext, info.getCache()));
			
			return view;
		}
		
	}
	
	class ViewHolder{
		ImageView iv_cleancache_icon;
		TextView tv_cleancache_appname;
		TextView tv_cleancache_cache;
		ImageView iv_cleancache_clean;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.bt_cleancache_deleteall:{//全部删除缓存文件
				if(cacheList.size() > 0){
					new Thread(new Runnable() {
						@Override
						public void run() {
							for(AppInfos info : cacheList){
//								deleteApplicationCacheFiles(mContext,info.getPackageName());
							}
							cacheList.clear();
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									tv_cleancache_appname.setText("您的系统现在已经很干净了！！！");
									adapter.notifyDataSetChanged();
								}
							});
						}
					}).start();
				}
				break;
			}
		}
		
	}
	
	//下面两个是连在一起的
	/*public void getPackageSizeInfo(Context context,String packageName){//一调用这个方法，就会执行下面类中的回调方法
    	try {
    		PackageManager pm = ((MyApplication)context.getApplicationContext()).mPackageManager;
			Class<PackageManager> c = PackageManager.class;
			Method method = c.getMethod("getPackageSizeInfo", String.class, IPackageStatsObserver.class);
			method.invoke(pm, packageName,new MyPackageStatsObserver());
    	} catch (Exception e) {
			e.printStackTrace();
		}
    }
	private class MyPackageStatsObserver extends IPackageStatsObserver.Stub{
		@Override
		public void onGetStatsCompleted(final PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cache = pStats.cacheSize;
			for(AppInfos info : list){
				if(info.getPackageName().equals(pStats.packageName) && cache > 0){
					info.setCache(cache);
					cacheList.add(info);
					runOnUiThread(new  Runnable() {
						public void run() {
							adapter.notifyDataSetChanged();//每次加一下数据都要及时更新一下数据
						}
					});
					garbage += cache;
				}
			}
		}
	}*/
	
	//下面两个是一起的
	/*public void deleteApplicationCacheFiles(Context context,String packageName){
		try {
    		PackageManager pm = ((MyApplication)context.getApplicationContext()).mPackageManager;
			Class<PackageManager> c = PackageManager.class;
			Method method = c.getMethod("deleteApplicationCacheFiles", String.class, IPackageDataObserver.class);
			method.invoke(pm, packageName,new MyPackageDataObserver());
    	} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private class MyPackageDataObserver extends IPackageDataObserver.Stub{
		@Override
		public void onRemoveCompleted(String packageName, boolean succeeded)
				throws RemoteException {
			//这里不用干什么活 succeeded表示是否删除成功，true表示删除成功
		}
	}*/
	
}
