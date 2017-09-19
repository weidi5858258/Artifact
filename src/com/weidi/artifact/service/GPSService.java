package com.weidi.artifact.service;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Service;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

//在自己的服务里面不能开启自己的服务
public class GPSService extends Service {
	private LocationManager lm = null;
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("GPSService");
		lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);//设置精准度
		String provider = lm.getBestProvider(criteria, true);
		lm.requestLocationUpdates(provider, 0, 0, new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				System.out.println("GPSService--->onStatusChanged()");
			}
			
			@Override
			public void onProviderEnabled(String provider) {//GPS从关闭状态到开启状态时调用
				System.out.println("GPSService--->onProviderEnabled()");
			}
			
			@Override
			public void onProviderDisabled(String provider) {//GPS从开启状态到关闭状态时调用
				System.out.println("GPSService--->onProviderDisabled()");
			}
			
			@Override
			public void onLocationChanged(Location location) {
				System.out.println("GPSService--->onLocationChanged()");
				Toast.makeText(GPSService.this, "GPSService--->onLocationChanged()", 0).show();
				try {
					double longitude= location.getLongitude(); //经度
					double latitude= location.getLatitude();   //纬度
					float accuracy = location.getAccuracy();   //精确度
					System.out.println(longitude+" "+latitude+" "+accuracy);
					
					ModifyOffset mo = ModifyOffset.getInstance(GPSService.class.getResourceAsStream("axisoffset.dat"));
					PointDouble  newPoint = mo.s2c(new PointDouble(longitude,latitude));
					
					String loc = newPoint.toString()+"\r\n";
					
					File file = new File(GPSService.this.getFilesDir(), "location.txt");
					FileOutputStream fos = new FileOutputStream(file,true);
					fos.write(loc.getBytes());
					fos.flush();
					fos.close();
					System.out.println(loc);
					Toast.makeText(GPSService.this, loc, 0).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	 //下面方法是必须要有的
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {//设备管理器里去停止的话会回调这个方法。如果用第三方应用去杀死服务的话，不会回调这个方法。
		super.onDestroy();
		System.out.println("GPSService is over");
	}
}
