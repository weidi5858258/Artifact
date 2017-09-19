package com.weidi.artifact.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/*
 * 我想在这里实现的功能是：通过电脑发送一些命令，让对方接收，然后执行我发送的命令。可能用UDP通信。
 */
public class HttpService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
