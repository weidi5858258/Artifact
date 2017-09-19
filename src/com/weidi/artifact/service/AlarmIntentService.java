package com.weidi.artifact.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONObject;

import com.weidi.artifact.db.bean.AppInfos;
import com.weidi.utils.MyUtils;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

//专门用于处理闹钟服务
public class AlarmIntentService extends IntentService {
    private List<AppInfos> list;
    private SharedPreferences sp;

    public AlarmIntentService() {//必须要有一个无参的构造方法
        super("AlarmIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            if ("com.aowin.mobilesafe.traffic".equals(intent.getAction())) {
                //				saveAllData();
            } else if ("com.aowin.mobilesafe.securityphone".equals(intent.getAction())) {
                //手机防盗部分的内容
                doSecurityPhone();//接收手机指令
            }
        }
    }

    //每五分钟保存一下应用所用的流量
    private void saveAllData() {
        list = MyUtils.getInstalledApplicationInfos(getApplicationContext());
        sp = getApplicationContext().getSharedPreferences("data", Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        int uid = -1;
        for (AppInfos info : list) {
            uid = info.getUid();
            long tx = TrafficStats.getUidTxBytes(uid);
            long rx = TrafficStats.getUidRxBytes(uid);
            editor.putLong("tx" + uid, tx);//保存每个应用上传下载的流量
            editor.putLong("rx" + uid, rx);
        }
        long tx_mobile = TrafficStats.getMobileTxBytes();
        long rx_mobile = TrafficStats.getMobileRxBytes();
        long tx_wifi = TrafficStats.getTotalTxBytes() - tx_mobile;
        long rx_wifi = TrafficStats.getTotalRxBytes() - rx_mobile;
        editor.putLong("alltx_mobile", tx_mobile);
        editor.putLong("allrx_mobile", rx_mobile);
        editor.putLong("allrx_wifi", tx_wifi);
        editor.putLong("allrx_wifi", rx_wifi);
        editor.commit();
    }


    private void doSecurityPhone() {
        try {
            URL url = new URL("http://weidi5858258.nat123.net/security_phone.html");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(5000);
            int code = conn.getResponseCode();
            if (code == 200) {
                InputStream is = conn.getInputStream();
                String securityphone = MyUtils.readFromStreamToString(is);
                JSONObject obj = new JSONObject(securityphone);
                String command = (String) obj.get("securityphone");
                is.close();
                if ("command".equals(command)) {
                    return;
                } else if ("location".equals(command)) {
                    getGPSLocation();
                } else if ("alarm".equals(command)) {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager
                            .TYPE_NOTIFICATION);
                    Ringtone ring = RingtoneManager.getRingtone(getApplicationContext(),
                            notification);
                    ring.play();
                    ring = null;
                    notification = null;
                } else if ("wipedata".equals(command)) {
                    //					((WeidiApplication)getApplication()).mDevicePolicyManager
                    // .wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                } else if ("lockscreen".equals(command)) {
                    if (CoreService.isScreenOnOrOff) {//如果屏幕亮着的话就锁屏
                        //                        if (((WeidiApplication) getApplication())
                        // .isAdminActive) {
                        //                            ((WeidiApplication) getApplication())
                        // .mDevicePolicyManager.lockNow();// 锁屏
                        //                        } else {
                        //                            Intent intent = new Intent();
                        //                            // 指定动作名称
                        //                            intent.setAction(DevicePolicyManager
                        // .ACTION_ADD_DEVICE_ADMIN);
                        //                            // 指定给哪个组件授权
                        //                            intent.putExtra(DevicePolicyManager
                        // .EXTRA_DEVICE_ADMIN, (
                        //                                    (WeidiApplication) getApplication())
                        // .myDeviceAdminComponentName);
                        //                            intent.putExtra(DevicePolicyManager
                        // .EXTRA_ADD_EXPLANATION,
                        //                                    "注册此组件后才能拥有锁屏功能");
                        //                            intent.setFlags(Intent
                        // .FLAG_ACTIVITY_NEW_TASK);
                        //                            startActivity(intent);
                        //                            intent = null;
                        //                        }
                    } else {//点亮屏幕（已测试，可行）

                        //逻辑是：1、点亮屏幕--->2、自动解锁--->3、处理事情--->4、自动加锁--->5、释放资源

                        //						PowerManager pm=(PowerManager)
                        // getApplicationContext().getSystemService(Context.POWER_SERVICE);
                        //						KeyguardManager km= (KeyguardManager)
                        // getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
                        //				        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,
                        // 最后的是LogCat里用的Tag
                        //				        PowerManager.WakeLock wl = pm.newWakeLock
                        // (PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager
                        // .SCREEN_DIM_WAKE_LOCK, "bright");
                        //				        KeyguardManager.KeyguardLock kl = km
                        // .newKeyguardLock("unLock");
                        //				        //点亮屏幕
                        //				        wl.acquire();
                        //
                        //				        //解锁
                        //				        kl.disableKeyguard();
                        //
                        //				        //这里是要处理的事情
                        //				        SystemClock.sleep(5000);
                        //
                        //				        kl.reenableKeyguard();
                        //				        //释放
                        //				        wl.release();
                        //
                        //				        wl = null;
                        //				        kl = null;

                    }

                }
            }
            conn.disconnect();
            url = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getGPSLocation() {
        LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
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
                Toast.makeText(getApplicationContext(), "GPSService--->onLocationChanged()",
                        Toast.LENGTH_SHORT).show();
                try {
                    double longitude = location.getLongitude(); //经度
                    double latitude = location.getLatitude();   //纬度
                    float accuracy = location.getAccuracy();   //精确度
                    System.out.println(longitude + " " + latitude + " " + accuracy);

                    ModifyOffset mo = ModifyOffset.getInstance(GPSService.class
                            .getResourceAsStream("axisoffset.dat"));
                    PointDouble newPoint = mo.s2c(new PointDouble(longitude, latitude));

                    String loc = newPoint.toString() + "\r\n";

                    File file = new File(getApplicationContext().getFilesDir(), "location.txt");
                    FileOutputStream fos = new FileOutputStream(file, true);
                    fos.write(loc.getBytes());
                    fos.flush();
                    fos.close();
                    System.out.println(loc);
                    Toast.makeText(getApplicationContext(), loc, 0).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
