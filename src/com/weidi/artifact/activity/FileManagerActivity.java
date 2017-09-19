package com.weidi.artifact.activity;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.view.View;
import android.view.View.OnClickListener;

import com.weidi.artifact.R;

public class FileManagerActivity extends Activity implements OnClickListener{
	private Context mContext;
	private FileObserver mFileObserver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cleancache);
		mContext = FileManagerActivity.this;
		
		if(null == mFileObserver) {
			mFileObserver = new SDCardFileObserver(Environment.getExternalStorageDirectory().getPath());
			mFileObserver.startWatching(); //开始监听
		}
		
	}

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if(null != mFileObserver){
    		mFileObserver.stopWatching(); //停止监听
    	}
    }

	private static class SDCardFileObserver extends FileObserver {
		
		public SDCardFileObserver(String path) {
			super(path);
		}
		
		//mask:指定要监听的事件类型，默认为FileObserver.ALL_EVENTS
	    public SDCardFileObserver(String path, int mask) {
	        super(path, mask);
	    }
	
	
	    @Override
	    public void onEvent(int event, String path) {
	        final int action = event & FileObserver.ALL_EVENTS;
	        switch (action) {
		        case FileObserver.CREATE:
		        	System.out.println("event: 文件或目录被创建, path: " + path);
		        	break;
		        case FileObserver.OPEN:
		        	System.out.println("event: 文件或目录被打开, path: " + path);
		        	break;
		        case FileObserver.CLOSE_WRITE:
		        	System.out.println("event: 文件或目录被关闭, path: " + path);
		        	break;
		        case FileObserver.ACCESS:
		            System.out.println("event: 文件或目录被访问, path: " + path);
		            break;
		        case FileObserver.MODIFY:
		        	System.out.println("event: 文件或目录被修改, path: " + path);
		        	break;
		        case FileObserver.DELETE:
		            System.out.println("event: 文件或目录被删除, path: " + path);
		            break;
	        }
		 }
		    
	}

	@Override
	public void onClick(View v) {
		
	}


	
	
}
