package com.weidi.artifact.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.weidi.utils.MyUtils;

public class NetworkChangedReceiver extends BroadcastReceiver {
	
	//不知道怎么回事，能接收好多次系统发出的广播。但是下面的方面我只要执行一次就行了。
	//接收到广播后，去开启一个服务。
	@Override
	public void onReceive(Context context, Intent intent) {
		System.out.println("有包安装了");
		System.out.println("有包卸载了");
		if(MyUtils.isNetworkConnected(context)){//只要有网络就行，不管是什么样的网络
			System.out.println("NetworkConnected");
		}else{
			System.out.println("not NetworkConnected");
		}
		if(MyUtils.isWifiAvailable(context)){
			System.out.println("WifiAvailable");
		}else{
			System.out.println("not WifiAvailable");
		}
	}

}
