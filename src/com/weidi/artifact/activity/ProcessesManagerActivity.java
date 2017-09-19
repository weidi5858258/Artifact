package com.weidi.artifact.activity;

import java.util.List;

import com.weidi.artifact.R;
import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.db.dao.ProcessDao;
import com.weidi.artifact.db.bean.ProcessInfos;
import com.weidi.utils.MyToast;
import com.weidi.utils.MyUtils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class ProcessesManagerActivity extends Activity implements OnClickListener{
	private Context mContext;
	private String PACKAGENAME = "com.aowin.mobilesafe";
	private ActivityManager am;
	private ListView lv_processesmanager_processinfos;
	private List<ProcessInfos> list;
	private RunningProcessAdapter adapter;
	private ProgressBar pb_processesmanager_progress;
	private TextView pb_processesmanager__alert;
	private TextView tv_processesmanager_processcounts;
	private TextView tv_processesmanager_meminfo;
	private TextView tv_processesmanager_add;
	private TextView tv_processesmanager_delete;
	private View view;
	private ViewHolder holder;
	private ProcessDao dao;
//	private KillUserAppDao mKillUserAppDao;
//	private KillSystemAppDao mKillSystemAppDao;
	private DialogHelper dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_processesmanager);
		mContext = ProcessesManagerActivity.this;
		am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		lv_processesmanager_processinfos = (ListView) this.findViewById(R.id.lv_processesmanager_processinfos);
		
		pb_processesmanager_progress = (ProgressBar) this.findViewById(R.id.pb_processesmanager_progress);
		pb_processesmanager__alert = (TextView) this.findViewById(R.id.pb_processesmanager__alert);
		
		tv_processesmanager_processcounts = (TextView) this.findViewById(R.id.tv_processesmanager_processcounts);
		tv_processesmanager_meminfo = (TextView) this.findViewById(R.id.tv_processesmanager_meminfo);
		tv_processesmanager_add = (TextView) this.findViewById(R.id.tv_processesmanager_add);
		tv_processesmanager_delete = (TextView) this.findViewById(R.id.tv_processesmanager_delete);
		
		tv_processesmanager_add.setOnClickListener(this);
		tv_processesmanager_delete.setOnClickListener(this);
		tv_processesmanager_meminfo.setText("剩余/总内存："+ MyUtils.getMobileAvailableRAM(mContext)+"/"+MyUtils.getMobileTotalRAM(mContext));
		
		dao = new ProcessDao(mContext);
//		mKillUserAppDao = new KillUserAppDao(mContext);
//		mKillSystemAppDao = new KillSystemAppDao(mContext);
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						pb_processesmanager_progress.setVisibility(ProgressBar.VISIBLE);
						pb_processesmanager__alert.setVisibility(TextView.VISIBLE);
						tv_processesmanager_add.setVisibility(TextView.INVISIBLE);
						tv_processesmanager_delete.setVisibility(TextView.INVISIBLE);
						
						tv_processesmanager_processcounts.setText("运行中进程：个");
					}
				});
				list = MyUtils.getAllRunningProcesses(mContext);//执行一次的时间差不多要2秒
				runOnUiThread(new Runnable() {
					public void run() {
						pb_processesmanager_progress.setVisibility(ProgressBar.INVISIBLE);
						pb_processesmanager__alert.setVisibility(TextView.INVISIBLE);
						tv_processesmanager_add.setVisibility(TextView.VISIBLE);
						tv_processesmanager_delete.setVisibility(TextView.VISIBLE);
						tv_processesmanager_processcounts.setText("运行中进程："+list.size()+"个");
						adapter = new RunningProcessAdapter();
						lv_processesmanager_processinfos.setAdapter(adapter);
					}
				});
			}
		}).start();
		
		//长按事件
		lv_processesmanager_processinfos.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(PACKAGENAME.equals(list.get(position).getPackageName())){
					return true;
				}
				//换图标 存储或者删除数据
				holder = (ViewHolder) view.getTag();
				String packageName = list.get(position).getPackageName();
				if(dao.query(packageName)){
					holder.iv_processinfos_kill.setImageResource(R.drawable.stop);
					dao.delete(packageName);
				}else{
					holder.iv_processinfos_kill.setImageResource(R.drawable.alive);
					dao.add(packageName);
				}
				return true;
			}
		});
		
		//点击事件
		lv_processesmanager_processinfos.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String packageName = list.get(position).getPackageName();
				if(PACKAGENAME.equals(packageName)){
					return;
				}
				//Application用法：用getApplication()得到Application对象后要强转一下
				if(((MyApplication)getApplication()).pkgList.contains(packageName)){
					return;
				}else{
					MyUtils.forceStopPackage(mContext, list.get(position).getPackageName());
					list.remove(position);
					adapter.notifyDataSetChanged();
					tv_processesmanager_processcounts.setText("运行中进程："+list.size()+"个");
					tv_processesmanager_meminfo.setText("剩余/总内存："+MyUtils.getMobileAvailableRAM(mContext)+"/"+MyUtils.getMobileTotalRAM(mContext));
				}
			}
		});
		
	}
	
	//正在运行着的进程的适配器
	private class RunningProcessAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				view = View.inflate(mContext, R.layout.processesmanager_view_item, null);
				holder = new ViewHolder();
				holder.iv_processinfos_icon = (ImageView) view.findViewById(R.id.iv_processinfos_icon);
				holder.tv_processinfos_appname = (TextView) view.findViewById(R.id.tv_processinfos_appname);
				holder.tv_processinfos_memory = (TextView) view.findViewById(R.id.tv_processinfos_memory);
				holder.iv_processinfos_kill = (ImageView) view.findViewById(R.id.iv_processinfos_kill);
				view.setTag(holder);
			}else{
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}
			holder.iv_processinfos_icon.setImageDrawable(list.get(position).getIcon());
			holder.tv_processinfos_appname.setText(list.get(position).getAppName());
			holder.tv_processinfos_memory.setText("RAM used："+list.get(position).getRamUsed());
			
			String packageName = list.get(position).getPackageName();
			if(PACKAGENAME.equals(packageName)){
				holder.iv_processinfos_kill.setImageResource(R.drawable.alive);
				if(!dao.equals(PACKAGENAME)){
					dao.add(PACKAGENAME);
				}
				return view;
			}
			if(dao.query(packageName)){
				holder.iv_processinfos_kill.setImageResource(R.drawable.alive);
			}else{
				holder.iv_processinfos_kill.setImageResource(R.drawable.stop);
			}
			
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
		ImageView iv_processinfos_icon;
		TextView tv_processinfos_appname;
		TextView tv_processinfos_memory;
		ImageView iv_processinfos_kill;
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.tv_processesmanager_add:{
			dialog = new DialogHelper(mContext, new Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					if(msg.what == 1){
						String packageName = (String) msg.obj;
						if(TextUtils.isEmpty(packageName)){
							MyToast.show("包名不能为空");
						}else{
							if(!dao.query(packageName)){
								dao.add(packageName);
								dialog.dismiss();
								MyToast.show("添加成功");
							}else{
								MyToast.show("包名已存在");
							}
						}
					}
					return true;
				}
			});
			dialog.show();
			break;
		}
		case R.id.tv_processesmanager_delete:{
			dialog = new DialogHelper(mContext, new Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					if(msg.what == 1){
						String packageName = (String) msg.obj;
						if(TextUtils.isEmpty(packageName)){
							MyToast.show("包名不能为空");
						}else{
							if(dao.query(packageName)){
								dao.delete(packageName);
								dialog.dismiss();
								MyToast.show("删除成功");
							}else{
								MyToast.show("包名不存在");
							}
						}
					}
					return true;
				}
			});
			dialog.show();
			break;
		}
		}
	}
	
	
	/*
	 * clearApplicationUserData
	 * getProcessMemoryInfo
	 * getAllPackageUsageStats()
	 * switchUser(int userid)
	 * isLargeRAM()
	 */
}
