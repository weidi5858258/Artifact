package com.weidi.artifact.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.service.CopyPhoneNumberAddressQueryDdIntentService;
import com.weidi.utils.MyUtils;

//http://localhost:8080/MobileSafe2.0.apk
public class StartActivity extends Activity {
    private Intent intent = null;
    private PackageManager pm = null;
    private PackageInfo pi = null;
    private String versionName = null;
    private String apkurl = null;
    private TextView tv_start_version = null;
    private TextView tv_start_download = null;
    private SharedPreferences sp = null;
    private MyHandler handler = new MyHandler();

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                createDialog();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //开启核心服务
        //		intent = new Intent(StartActivity.this, CoreService.class);
        //		startService(intent);

        sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
        tv_start_version = (TextView) this.findViewById(R.id.tv_start_version);
        tv_start_download = (TextView) this.findViewById(R.id.tv_start_download);
        try {
            pm = ((MyApplication) getApplication()).mPackageManager;
            pi = pm.getPackageInfo(getPackageName(), 0);
            versionName = pi.versionName;
            tv_start_version.setText("当前版本：" + versionName);
        } catch (NameNotFoundException e1) {
            e1.printStackTrace();
        }


        createShortcut();//创建快捷方式
        copyDB("antivirus.db");//复制数据库
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean flag = sp.getBoolean("update", false);
                if (!flag) {
                    SystemClock.sleep(2000);
                    entryHome();
                } else {
                    long startTime = System.currentTimeMillis();
                    long endTime = 0;
                    long resultTime = 0;
                    try {
                        URL url = new URL("http://weidi5858258.nat123.net/version.html");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setConnectTimeout(5000);
                        conn.setReadTimeout(3000);
                        int rc = conn.getResponseCode();
                        endTime = System.currentTimeMillis();
                        if (rc == 200) {
                            //联网成功,判断有没有版本可以更新
                            InputStream is = conn.getInputStream();
                            String versionInfo = MyUtils.readFromStreamToString(is);
                            JSONObject obj = new JSONObject(versionInfo);
                            String version = (String) obj.get("version");
                            String description = (String) obj.get("description");
                            String apkurl = (String) obj.get("apkurl");
                            endTime = System.currentTimeMillis();
                            if (!versionName.equals(version)) {
                                //版本不同，需要升级 在子线程中不能开启窗口
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            } else {
                                //版本相同，不要升级。直接进入主界面
                                resultTime = endTime - startTime;
                                if (resultTime < 3000) {
                                    SystemClock.sleep(3000 - resultTime);
                                    entryHome();
                                } else {
                                    entryHome();
                                }
                            }
                        } else {
                            //联网失败
                            resultTime = endTime - startTime;
                            if (resultTime < 3000) {
                                SystemClock.sleep(3000 - resultTime);
                                entryHome();
                            } else {
                                entryHome();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        SystemClock.sleep(3000);
                        entryHome();
                    }
                }
            }
        }).start();

    }

    //进入主界面
    private void entryHome() {
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //对话框提示是否有更新
    private void createDialog() {
        //Builder是静态内部类，new的时候形式为：外部类.内部类 变量名 = new 外部类.内部类();
        final AlertDialog.Builder builder = new AlertDialog.Builder(StartActivity.this);
        Dialog dialog = builder.create();
        builder.setCancelable(false);
        builder.setTitle("发现新版本！");
        builder.setMessage("新版本增加了很多功能，界面更炫，性能更稳定，请更新！");

        builder.setPositiveButton("立即更新", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击后进行下载，然后安装
                tv_start_download.setVisibility(View.VISIBLE);//下载进度
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            downloadAPK();
                            //下载完成开始安装
                            installAPK();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        builder.setNegativeButton("下次再说", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //点击后直接进入主界面
                dialog.dismiss();
                entryHome();
            }
        });
        builder.show();

    }

    //下载更新版本的spk文件
    private void downloadAPK() throws Exception {
        URL url = new URL("http://weidi5858258.nat123.net/MobileSafe2.0.apk");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(3000);
        int rc = conn.getResponseCode();
        if (rc == 200) {
            InputStream is = conn.getInputStream();
            File file = new File("/storage/sdcard0/MobileSafe2.0.apk");
            FileOutputStream fos = new FileOutputStream(file);
            byte[] bt = new byte[1024];
            int len = 0;
            while ((len = is.read(bt)) != -1) {
                fos.write(bt, 0, len);
            }
        }
    }

    //安装spk文件
    private void installAPK() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        //注意：file:/（再加apk文件路径）
        intent.setDataAndType(Uri.parse("file:/storage/sdcard0/MobileSafe2.0.apk"), "application/vnd.android.package-archive");
        startActivity(intent);
    }


    //把数据库复制到应用目录中  java.lang.OutOfMemoryError有时启动会这样，需要处理
    public void copyDB(String fileName) {
        try {
            String path = "/data/data/com.weidi.artifact/databases/" + fileName;
            InputStream is = getAssets().open(fileName);
            String temp = MyUtils.readFromStreamToString(is);
            long fileLength = temp.length();
            File file = new File(path);
            if (file.exists() && file.length() == fileLength) {
                return;
            } else if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            } else {
                if (file.exists()) {
                    file.delete();
                }
                //这里我想开启一个服务去复制文件。因为在这里如果开启线程去复制的话，这个Activity关掉之后，子线程就不执行了，所以文件没有被复制。如果不开启线程的话，文件太大会阻塞界面。
                intent = new Intent(StartActivity.this, CopyPhoneNumberAddressQueryDdIntentService.class);
                intent.putExtra("fileName", fileName);
                startService(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //创建快捷方式
    public void createShortcut() {
        boolean flag = sp.getBoolean("shortcut", false);
        if (flag) {
            return;
        }
        Editor editor = sp.edit();
        intent = new Intent();
        intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        ApplicationInfo info = pi.applicationInfo;
        String appName = info.loadLabel(pm).toString();
        //		Drawable icon = pm.getApplicationIcon(info);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, appName);
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        Intent shortcutIntent = new Intent();
        shortcutIntent.setAction("android.intent.action.MAIN");
        shortcutIntent.addCategory("android.intent.category.LAUNCHER");
        shortcutIntent.setClassName(getPackageName(), "StartActivity");
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        sendBroadcast(intent);
        editor.putBoolean("shortcut", true);
        editor.commit();
    }

    private void getAllData() {//测试
        long tx = TrafficStats.getUidTcpTxBytes(10000);
        long rx = TrafficStats.getUidTcpRxBytes(10000);
        System.out.println("tx:" + Formatter.formatFileSize(getApplicationContext(), tx) + " rx:" + Formatter.formatFileSize(getApplicationContext(), rx));
    }

    private void getCalls() {
        //		Uri uri = CallLog.Calls.CONTENT_URI;
        Uri uri = Uri.parse("content://call_log/calls");
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(uri, new String[]{"number", "date", "duration", "type"}, null, null, null);
        while (cursor.moveToNext()) {
            String number = cursor.getString(cursor.getColumnIndex("number"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String duration = cursor.getString(cursor.getColumnIndex("duration"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            System.out.println("number:" + number + " date:" + date + " duration:" + duration + " type:" + type);
        }
        cursor.close();
    }

    public void getMethods() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        Class c = ActivityManager.class;
        Method[] methods = c.getDeclaredMethods();
        for (Method method : methods) {
            System.out.println(method.getName());
        }
    }


}
