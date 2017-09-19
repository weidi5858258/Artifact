package com.weidi.artifact.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;

public class LockScreenService extends Service {
	private DevicePolicyManager dpm = null;
	private ComponentName cn = null;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		dpm = (DevicePolicyManager) this.getSystemService(DEVICE_POLICY_SERVICE);
		dpm.lockNow();
//		cn = new ComponentName(LockScreenService.this, MyDeviceAdmin.class);
//		dpm.setMaximumTimeToLock(cn, 5000);
	}
}
