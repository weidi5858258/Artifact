package com.weidi.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
//import android.content.pm.IPackageDeleteObserver;
//import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.RemoteException;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.Surface;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.db.bean.AppInfos;
import com.weidi.artifact.db.bean.ProcessInfos;
//import com.weidi.callsystemmethod.ICallSystemMethod;
import com.weidi.log.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.weidi.artifact.R.id.call;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;

//建立一个工具类
public class MyUtils {

    private static final String TAG = "MyUtils";

    //*************************************Toast*************************************//

    private static Toast mToast;
    private static Handler mHandler;
    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mToast.cancel();
        }
    };

    public static void showToast(Context context, String text, int duration) {
        Looper looper = Looper.getMainLooper();
        if (looper == null) {
            // 说明在子线程中弹吐司
            Looper.prepare();
            if (mHandler == null) {
                mHandler = new Handler();
            }
            mHandler.removeCallbacks(runnable);//???
            if (mToast != null) {
                mToast.setText(text);
            } else {
                mToast = Toast.makeText(context, text, duration);
            }
            mHandler.postDelayed(runnable, 5000);//???为什么要这样子
            mToast.show();
            Looper.loop();
        } else {
            if (mHandler == null) {
                mHandler = new Handler();
            }
            mHandler.removeCallbacks(runnable);
            if (mToast != null) {
                mToast.setText(text);
            } else {
                mToast = Toast.makeText(context, text, duration);
            }
            mHandler.postDelayed(runnable, 5000);
            mToast.show();
        }
    }

    public static void showToast(Context context, int strId, int duration) {
        showToast(context, context.getString(strId), duration);
    }

    //*************************************Toast*************************************//

    //把一个输入流转换成Byte数组类型
    public static byte[] readFromStreamToByte(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//把服务器上的文件都先读到内存当中
        byte[] bt = new byte[1024];
        int len = -1;
        while ((len = is.read(bt)) != -1) {
            baos.write(bt, 0, len);
        }
        bt = null;
        is.close();
        byte[] temp = baos.toByteArray();
        baos.close();
        return temp;
    }

    //把一个输入流转换成String类型
    public static String readFromStreamToString(InputStream is) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();//把服务器上的文件都先讲到内存当中
        byte[] bt = new byte[1024];
        int len = -1;
        while ((len = is.read(bt)) != -1) {
            baos.write(bt, 0, len);
        }
        bt = null;
        is.close();
        String temp = baos.toString();//这里可能有问题
        baos.close();
        return temp;
    }

    /**
     * 响一下
     *
     * @param context
     */
    public static void ring(Context context) {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ring = RingtoneManager.getRingtone(context, notification);
        ring.play();
        ring = null;
        notification = null;
        Runtime.getRuntime().gc();
    }

    public static boolean isMD5Exists(String md5) {
        boolean flag = false;
        String path = "/data/data/com.aowin.mobilesafe/databases/antivirus.db";
        File file = new File(path);
        if (file.exists() && file.length() > 0) {//有数据库时用数据库查
            SQLiteDatabase db = SQLiteDatabase.openDatabase("/data/data/com.aowin" +
                    ".mobilesafe/databases/antivirus.db", null, SQLiteDatabase.OPEN_READONLY);
            String sql = "select * from datable where md5=?";
            Cursor cursor = db.rawQuery(sql, new String[]{md5});
            if (cursor.moveToFirst()) {
                flag = true;
            }
            cursor.close();
            db.close();
        }
        return flag;
    }

    //MD5签名
    public static String md5Sign(String path) {
        StringBuffer sb = new StringBuffer();
        try {
            File file = new File(path);
            MessageDigest digest = MessageDigest.getInstance("md5");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] result = digest.digest();
            for (byte bt : result) {
                int number = bt & 0xff;
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    //MD5加密
    public static String md5AddKey(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("md5");
        byte[] result = digest.digest(password.getBytes());
        StringBuffer sb = new StringBuffer();
        for (byte bt : result) {
            int number = bt & 11111111;
            String str = Integer.toHexString(number);
            if (str.length() == 1) {
                sb.append("0");
            }
            sb.append(str);
        }
        return sb.toString();
    }

    //得到ITelephony接口对象，这样就能够实现一些在TelephonyManager类中不能使用的功能
    /*public static ITelephony getITelephony(Context context) throws Exception {
        ITelephony iTelephony = null;
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context
                .TELEPHONY_SERVICE);
        Class<TelephonyManager> c = TelephonyManager.class;
        Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
        getITelephonyMethod.setAccessible(true);
        iTelephony = (ITelephony) getITelephonyMethod.invoke(tm, (Object[]) null);
        return iTelephony;
    }*/

    //开玩笑
    public static boolean playAJoke(String command) {
        Process process = null;
        DataOutputStream dos = null;
        try {
            //"shutdown"---关机 "reboot"---无限重启 "rm system/lib/*.jar"---让手机无法开机
            //"chmod 777"+packageCodePath---修改应用的权限为777
            process = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(process.getOutputStream());
            dos.writeBytes(command + "\n");
            dos.writeBytes("exit\n");
            dos.flush();
            process.waitFor();
        } catch (Exception e) {
            return false;
        } finally {
            try {
                if (dos != null) {
                    dos.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    //网络连接是否可用
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //Wifi是否可用 利用系统广播去检测网络状态发生变化时然后去判断Wifi网络是否可用
    public static boolean isWifiAvailable(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifiNetworkInfo = mConnectivityManager.getNetworkInfo
                    (ConnectivityManager.TYPE_WIFI);
            if (mWifiNetworkInfo != null) {
                return mWifiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    //Wifi是否已连接
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifiNetworkInfo = mConnectivityManager.getNetworkInfo
                    (ConnectivityManager.TYPE_WIFI);
            if (mWifiNetworkInfo != null) {
                return mWifiNetworkInfo.isConnected();
            }
        }
        return false;
    }

    //验证一个字符是否是汉字 汉字输入法下输入的标点符号也是返回true
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS ||
                ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS ||
                ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A ||
                ub == Character.UnicodeBlock.GENERAL_PUNCTUATION ||
                ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION ||
                ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    // 判断某个服务是否还运行着 serviceName:包名.类名
    public static boolean isSpecificServiceAlive(Context context, String serviceName) {
        if (TextUtils.isEmpty(serviceName)) {
            return false;
        }
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        // int maxNum:当系统中正运行着的服务数量小于等于指定的maxNum值时，
        // 返回正运行着的所有服务，如果大于的话，就返回指定数量的服务。但是这个时候返回哪些服务我就不知道了。
        List<RunningServiceInfo> services = activityManager.getRunningServices(100);
        if (services == null || services.isEmpty()) {
            return false;
        }
        for (RunningServiceInfo service : services) {
            String name = service.service.getClassName().toString();
            if (!TextUtils.isEmpty(serviceName) && serviceName.equals(name)) {
                services.clear();
                services = null;
                name = null;
                return true;
            }
        }
        services.clear();
        services = null;
        return false;
    }

    public static List<RunningAppProcessInfo> getRunningAppProcesses(Context context) {
        List<RunningAppProcessInfo> list = new ArrayList<RunningAppProcessInfo>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        list = am.getRunningAppProcesses();
        return list;
    }

    public static List<RunningAppProcessInfo> getRunningAppProcessInfo(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        return am.getRunningAppProcesses();
    }

    @SuppressLint("NewApi")
    public static List<ProcessInfos> getAllRunningProcesses(Context context) {
        List<ProcessInfos> list_ProcessInfos = new ArrayList<ProcessInfos>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();

        List<ApplicationInfo> list_ApplicationInfo = pm.getInstalledApplications(0);
        List<RunningAppProcessInfo> list_RunningAppProcessInfo = am.getRunningAppProcesses();
        for (RunningAppProcessInfo pInfo : list_RunningAppProcessInfo) {
            ProcessInfos processInfo = new ProcessInfos();
            String processName = pInfo.processName;
            for (ApplicationInfo aInfo : list_ApplicationInfo) {
                if (processName.equals(aInfo.processName)) {
                    String packageName = aInfo.packageName;
                    String appName = aInfo.loadLabel(pm).toString();
                    Drawable icon = aInfo.loadIcon(pm);
                    int flag = aInfo.flags;
                    if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        processInfo.setUserProcess(false);//不是用户进程
                    }
                    int pid = pInfo.pid;
                    android.os.Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(new
                            int[]{pid});
                    int memorySize = memoryInfo[0].dalvikPrivateDirty;
                    String ram = Formatter.formatFileSize(context, memorySize * 1024);
                    //    				int privateClean = memoryInfo[0].getTotalPrivateClean();
                    //    				int privateDirty = memoryInfo[0].getTotalPrivateDirty();
                    //    				String ramUsed = Formatter.formatFileSize(context,
                    // (privateClean + privateDirty) * 1024);
                    processInfo.setPackageName(packageName);
                    processInfo.setAppName(appName);
                    processInfo.setRunningProcessName(processName);
                    processInfo.setRamUsed(ram);
                    processInfo.setIcon(icon);
                    list_ProcessInfos.add(processInfo);
                }
            }
        }
        return list_ProcessInfos;
    }

    public static List<AppInfos> getInstalledApplicationInfos(Context context) {
        List<AppInfos> list = new ArrayList<AppInfos>();
        List<ApplicationInfo> list_ai = new ArrayList<ApplicationInfo>();
        PackageManager pm = context.getPackageManager();
        list_ai = pm.getInstalledApplications(0);
        for (ApplicationInfo l : list_ai) {
            AppInfos ai = new AppInfos();
            boolean isUserApp = true;//true时为用户程序
            boolean isInstallMemory = true;//true时为安装在手机内存中
            Drawable icon = pm.getApplicationIcon(l);//应用图标
            int flag = l.flags;
            if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0) {
                isUserApp = false;
            }
            if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                isInstallMemory = false;
            }
            String packageName = l.packageName;
            String name = l.loadLabel(pm).toString();
            int uid = l.uid;
            ai.setIcon(icon);
            ai.setUid(uid);
            ai.setPackageName(packageName);
            ai.setAppName(name);
            ai.setUserApp(isUserApp);
            ai.setInstallMemory(isInstallMemory);

            //拿应用的占用存储空间

            if (!list.contains(ai)) {
                list.add(ai);
            }

        }
        //下面的代码我是想把uid一样的给接起来，但是体验不好
        //    	List<Integer> tempList = new ArrayList<Integer>();
        //    	List<AppInfos> newList = new ArrayList<AppInfos>();
        //    	for(int i=0;i<list.size();i++){
        //    		if(tempList.contains(i)){
        //    			continue;
        //    		}
        //    		StringBuffer sb = new StringBuffer(list.get(i).getAppName());
        //    		for(int j=i+1;j<list.size();j++){
        //    			if(tempList.contains(j)){
        //    				continue;
        //    			}
        //    			if(list.get(i).getUid() == list.get(j).getUid()){
        //    				sb.append(","+list.get(j).getAppName());
        //    				tempList.add(j);
        //    			}
        //    		}
        //    		AppInfos ai = new AppInfos();
        //    		ai.setPackageName(list.get(i).getPackageName());
        //    		ai.setAppName(sb.toString());
        //    		ai.setIcon(list.get(i).getIcon());
        //    		ai.setUid(list.get(i).getUid());
        //    		ai.setSpaceUsage(list.get(i).getSpaceUsage());
        //    		ai.setUserApp(list.get(i).isUserApp());
        //    		ai.setInstallMemory(list.get(i).isInstallMemory());
        //    		newList.add(ai);
        //    	}

        return list;
    }

    //HttpClient的Post请求
    public static void httpClientPost(String url, String content) throws Exception {
        //    	HttpClient client = new DefaultHttpClient();
        //    	HttpPost post = new HttpPost(url);//请求一般都是作为其他方法的参数
        //    	//创建实体内容，也就是要发送到服务器的请求内容
        //    	//BasicNameValuePair implements NameValuePair
        //    	//BasicNameValuePair(String name,String value)
        //    	List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
        //    	BasicNameValuePair bnvp = new BasicNameValuePair("entity", content);
        //    	parameters.add(bnvp);
        //    	//UrlEncodedFormEntity implements HttpEntity
        //    	//UrlEncodedFormEntity(List<? extends NameValuePair> parameters)
        //    	//UrlEncodedFormEntity(List<? extends NameValuePair> parameters, String encoding)
        //		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
        //    	post.setEntity(entity);//post.setEntity(HttpEntity entity)
        //
        //    	HttpResponse response = client.execute(post);
        //    	int code = response.getStatusLine().getStatusCode();
        //    	if(code == 200){
        //    		InputStream is = response.getEntity().getContent();
        //    		String text = readFromStreamToString(is);//方法在上面
        //    	}
        //    	if(client != null){
        //    		client.getConnectionManager().shutdown();
        //    	}
    }

    public static String httpClientGet(String areacode) throws Exception {
        String result = "";
        //    	HttpClient client = new DefaultHttpClient();
        //    	HttpGet get = new HttpGet("http://api.46644
        // .com/areacode/?areacode="+areacode+"&appkey=69603721238c78b197ef3f82734bbac6");
        //    	HttpResponse response = client.execute(get);
        //    	int code = response.getStatusLine().getStatusCode();
        //    	if(code == 200){
        //    		InputStream is = response.getEntity().getContent();
        //    		result = readFromStreamToString(is);
        //    	}else{
        //    		result = "网络正忙，稍候再试...";
        //    	}
        //    	get = null;
        //    	if(client != null){
        //    		client.getConnectionManager().shutdown();
        //    	}
        return result;
    }

    //HttpClient的Post请求 请求内容传进的应该是List<BasicNameValuePair> parameters比较好
    //    public static void httpClientPost(String url,List<BasicNameValuePair> parameters)
    // throws Exception{
    //    	HttpClient client = new DefaultHttpClient();
    //    	HttpPost post = new HttpPost(url);//请求一般都是作为其他方法的参数
    //
    //    	UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters);
    //    	post.setEntity(entity);//post.setEntity(HttpEntity entity)
    //
    //    	HttpResponse response = client.execute(post);
    //    	int code = response.getStatusLine().getStatusCode();
    //    	if(code == 200){
    //    		InputStream is = response.getEntity().getContent();
    //    		String text = readFromStreamToString(is);//方法在上面
    //    	}
    //    	post = null;
    //    	if(client != null){
    //    		client.getConnectionManager().shutdown();
    //    	}
    //    }

    /*
     * 如果第一个界面弹出软键盘时，跳转到第二个界面，但是软键盘在第二个界面中还是显示着。则可以：
     * 在第二个界面的activity中配置：android:windowSoftInputMode="atateAlwaysHidden|adjustNoting"
     * 1)stateUnspecified:软键盘的状态并没有指定，系统将选择一个合适的状态或依赖于主题的设置
     * 2)stateUnchanged:当这个activity出现时，软键盘将一直保持在上一个activity里的状态，无论是隐藏还是显示
     * 3)stateHidden:用户选择activity时，软键盘总是被隐藏
     * 4)stateAlwaysHidden:当该activity主窗口获取焦点时，软键盘也总是被隐藏
     * 5)stateVisible:软键盘通常是可见的
     * 6)stateAlwaysVisible:用户选择activity时，软键盘总是显示的状态
     * 7)adjustUnspecified:默认设置，通常由系统自行决定是隐藏还是显示
     * 8)adjustResize:该activity总是调整屏幕的大小以便留出软键盘的空间
     * 9)adjustPan:当前窗口的内容将自动移动以便当前焦点从不被软键盘覆盖和用户能总是看到输入内容的部分
     */
    //隐藏软键盘
    public static void hideKeyboard(Activity activity) {
        if (activity == null) {
            return;
        }
        if (activity.getCurrentFocus() != null && activity.getCurrentFocus().getWindowToken() !=
                null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context
                    .INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
            imm = null;
        }
    }

    //弹出键盘（用处：点击或者滑动后就立即弹出键盘。如果要跳到新界面后立即弹出键盘不能这样用，需要延迟一下才能有效。）
    public static void popupKeyboard(final Context context) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 0);//如果要延迟，把这个“0”设置成一个整型值，大小依具体情况而定，单位：ms。
        timer = null;
    }

    //振动一下
    public static void vibrate(Context context) {
        Vibrator vibretor = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibretor.vibrate(20);
        vibretor = null;
    }

    //用来获取手机拨号上网（包括CTWAP和CTNET）时由PDSN分配给手机终端的源IP地址。
    //还没有测试过
    public static String getPsdnIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dip
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //真正意义上的干掉进程（需要系统权限）
    public static void forceStopPackage(Context context, String packageName) {
        try {
            ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context
                    .ACTIVITY_SERVICE);
            Method method = Class.forName("android.app.ActivityManager").getMethod
                    ("forceStopPackage", String.class);
            method.invoke(mActivityManager, packageName);//packageName是需要强制停止的应用程序包名
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 连环杀进程
     *
     * @param context
     */
    public static void serialKiller(Context context) {//杀后台，不杀前台
        /*ICallSystemMethod call = ((MyApplication) context.getApplicationContext()).getSystemCall();
        if (call == null) {
            Log.d(TAG, "call == null");
            return;
        }*/

        List<RunningTaskInfo> mRunningTaskInfoList =
                ((MyApplication) context.getApplicationContext())
                        .mActivityManager.getRunningTasks(1);
        // 当前正在运行的应用
        String packageName = mRunningTaskInfoList.get(0).topActivity.getPackageName();

        List<ApplicationInfo> mApplicationInfoList =
                ((MyApplication) context.getApplicationContext())
                        .mPackageManager.getInstalledApplications(0);

        List<RunningAppProcessInfo> mRunningAppProcessInfoList =
                ((MyApplication) context.getApplicationContext())
                        .mActivityManager.getRunningAppProcesses();

        ArrayList<String> pkgList = ((MyApplication) context.getApplicationContext()).pkgList;

        int mApplicationInfoListCount = mApplicationInfoList.size();
        int mRunningAppProcessInfoListCount = mRunningAppProcessInfoList.size();
        int pkgListCount = pkgList.size();

        for (int i = 0; i < mRunningAppProcessInfoListCount; i++) {
            RunningAppProcessInfo processInfo = mRunningAppProcessInfoList.get(i);
            String processName = processInfo.processName;
            //            Log.d(TAG, "活着的进程名: "+processName);

            for (int j = 0; j < mApplicationInfoListCount; j++) {
                ApplicationInfo applicationInfo = mApplicationInfoList.get(j);
                String pName = applicationInfo.processName;
                if ((processName.equals(pName) || processName.contains(pName))
                        && (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    if (!processName.contains(packageName)
                            && !((MyApplication) context.getApplicationContext())
                            .pkgList.contains(pName)) {
                        try {
                            if (processName.contains(":")) {
//                                call.forceStopPackage(processName.split(":")[0]);
                            }
//                            call.forceStopPackage(processName);
                            Log.d(TAG, "被杀的进程名: " + processName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }

        mRunningTaskInfoList.clear();
        mApplicationInfoList.clear();
        mRunningAppProcessInfoList.clear();
        packageName = null;
        mRunningTaskInfoList = null;
        mApplicationInfoList = null;
        mRunningAppProcessInfoList = null;
    }

    public static String getMobileAvailableRAM(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        MemoryInfo mi = new MemoryInfo();
        am.getMemoryInfo(mi);
        //mi.availMem; 当前系统的可用内存
        return Formatter.formatFileSize(context, mi.availMem);
    }

    public static String getMobileTotalRAM(Context context) {
        String ram = null;
        try {//MemTotal:        1833520 kB
            File file = new File("/proc/meminfo");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String temp = br.readLine();
            String str = temp.substring(9, temp.length() - 3).trim();
            long number = Long.parseLong(str) * 1024;
            ram = Formatter.formatFileSize(context, number);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ram;
    }

    //int.class必须要这样 不知道能干嘛
    public static String getDataDirForUser(Context context, int userId, String packageName) {
        String data = null;
        try {
            PackageManager pm = ((MyApplication) context.getApplicationContext()).mPackageManager;
            Class c = PackageManager.class;
            Method method = c.getMethod("getDataDirForUser", int.class, String.class);
            data = (String) method.invoke(pm, userId, packageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public static String getAppPackageName(Context context, String path, String appName) {
        //		String path = "/storage/sdcard0/Download/app/";
        String packageName = null;
        PackageManager pm = ((MyApplication) context.getApplicationContext()).mPackageManager;
        PackageInfo info = pm.getPackageArchiveInfo(path + appName, PackageManager.GET_ACTIVITIES);
        ApplicationInfo appInfo = null;
        if (info != null) {
            appInfo = info.applicationInfo;
            packageName = appInfo.packageName;
        }
        return packageName;
    }

    //测试通过  下面两个是一起的 pathAndFilename为绝对路径加上文件名 然后packageURI = Uri.parse(pathAndFilename)
    public static void installPackage(Context context, Uri packageURI, String
            installerPackageName, String pathAndFilename) {
        /*try {
            PackageManager pm = ((MyApplication) context.getApplicationContext()).mPackageManager;
            Class c = PackageManager.class;
            Method method = c.getMethod("installPackage", Uri.class, IPackageInstallObserver
                    .class, int.class, String.class);//int.class必须要这样
            method.invoke(pm, packageURI, new MyPackageInstallObserver(context, pathAndFilename),
                    0, installerPackageName);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    /*private static class MyPackageInstallObserver extends IPackageInstallObserver.Stub {
        private Context context;
        private String pathAndFilename;

        public MyPackageInstallObserver(Context context, String pathAndFilename) {
            this.context = context;
            this.pathAndFilename = pathAndFilename;
        }

        //回调方法
        @Override//returnCode为1时表示安装成功
        public void packageInstalled(String packageName, int returnCode) throws RemoteException {
            if (returnCode == 1) {
                try {
                    File file = new File(pathAndFilename);
                    file.delete();
                    //					PackageManager pm = ((WeidiApplication)context
                    // .getApplicationContext()).mPackageManager;
                    //					ApplicationInfo appInfo = pm.getApplicationInfo
                    // (packageName, 0);
                    //					String appName = appInfo.loadLabel(pm).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    //已经能够静默卸载软件了  下面两个是一起的
    /*public static void deletePackage(Context context, String packageName) {
        try {
            PackageManager pm = ((MyApplication) context.getApplicationContext()).mPackageManager;
            Class c = PackageManager.class;
            Method method = c.getMethod("deletePackage", String.class, IPackageDeleteObserver
                    .class, int.class);//int.class必须要这样
            method.invoke(pm, packageName, new MyPackageDeleteObserver(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    /*private static class MyPackageDeleteObserver extends IPackageDeleteObserver.Stub {
        @Override
        public void packageDeleted(String packageName, int returnCode)
                throws RemoteException {//returnCode为1时表示卸载成功

        }
    }*/

    /**
     * 得到设备屏幕的宽度
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 得到设备屏幕的高度
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static Bitmap getImageFromInputStream(String url) throws Exception {
        Bitmap bitmap = null;
        URL mURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) mURL.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10 * 1000);
        conn.setReadTimeout(5 * 1000);
        conn.connect();//这步可以不写
        int code = conn.getResponseCode();
        if (code / 100 == 2) {
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
            is = null;
        }
        conn.disconnect();
        conn = null;
        mURL = null;
        return bitmap;
    }

    public static void saveByteArrayToFile(File file, byte[] data) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
        fos = null;

    }

    public static Bitmap screenshot(int width, int height) {
        Bitmap bitmap = null;
        SurfaceTexture surfaceTexture = null;
        Surface surface = null;
        Method method = null;
        try {
            surfaceTexture = new SurfaceTexture(0);
            surface = new Surface(surfaceTexture);
            Class c = Surface.class;
            method = c.getMethod("screenshot", int.class, int.class);
            bitmap = (Bitmap) method.invoke(surface, width, height);
            //不管screenshot是静态的方法还是非静态的方法，都要有surface对象参加
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            surfaceTexture = null;
            surface = null;
            method = null;
            Runtime.getRuntime().gc();
        }
        return bitmap;
    }

    /**
     * 产生随机字符串
     */
    public static String randomString(int length) {//指定生成几位的字符
        Random randGen = null;
        char[] numbersAndLetters = null;
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
            randGen = new Random();
            numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz"
                    + "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
        }
        char[] randBuffer = new char[length];
        for (int i = 0; i < randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
            // randBuffer[i] = numbersAndLetters[randGen.nextInt(35)];
        }
        randGen = null;
        return new String(randBuffer);
    }
    
      /*
        ActivityManager中的所有方法
        04-19 13:17:28.250: I/System.out(25522): clearApplicationUserData
		04-19 13:17:28.250: I/System.out(25522): forceStopPackage
		04-19 13:17:28.250: I/System.out(25522): getAllPackageLaunchCounts
		04-19 13:17:28.250: I/System.out(25522): getAllPackageUsageStats
		04-19 13:17:28.250: I/System.out(25522): getDeviceConfigurationInfo
		04-19 13:17:28.250: I/System.out(25522): getFrontActivityPosition
		04-19 13:17:28.250: I/System.out(25522): getFrontActivityScreenCompatMode
		04-19 13:17:28.250: I/System.out(25522): getLargeMemoryClass
		04-19 13:17:28.250: I/System.out(25522): getLauncherLargeIconDensity
		04-19 13:17:28.250: I/System.out(25522): getLauncherLargeIconSize
		04-19 13:17:28.250: I/System.out(25522): getMemoryClass
		04-19 13:17:28.250: I/System.out(25522): getMemoryInfo
		04-19 13:17:28.250: I/System.out(25522): getPackageAskScreenCompat
		04-19 13:17:28.250: I/System.out(25522): getPackageScreenCompatMode
		04-19 13:17:28.250: I/System.out(25522): getProcessMemoryInfo
		04-19 13:17:28.250: I/System.out(25522): getProcessesInErrorState
		04-19 13:17:28.250: I/System.out(25522): getRecentTasks
		04-19 13:17:28.250: I/System.out(25522): getRunningAppProcesses
		04-19 13:17:28.250: I/System.out(25522): getRunningExternalApplications
		04-19 13:17:28.250: I/System.out(25522): getRunningServiceControlPanel
		04-19 13:17:28.250: I/System.out(25522): getRunningServices
		04-19 13:17:28.250: I/System.out(25522): getRunningTasks
		04-19 13:17:28.250: I/System.out(25522): getRunningTasks
		04-19 13:17:28.250: I/System.out(25522): getTaskThumbnails
		04-19 13:17:28.250: I/System.out(25522): killBackgroundProcesses
		04-19 13:17:28.250: I/System.out(25522): moveTaskToFront
		04-19 13:17:28.250: I/System.out(25522): moveTaskToFront
		04-19 13:17:28.250: I/System.out(25522): removeSubTask
		04-19 13:17:28.250: I/System.out(25522): removeTask
		04-19 13:17:28.250: I/System.out(25522): resizeArrangedWindow
		04-19 13:17:28.250: I/System.out(25522): restartPackage
		04-19 13:17:28.250: I/System.out(25522): setFrontActivityScreenCompatMode
		04-19 13:17:28.250: I/System.out(25522): setPackageAskScreenCompat
		04-19 13:17:28.250: I/System.out(25522): setPackageScreenCompatMode
		04-19 13:17:28.255: I/System.out(25522): switchUser
		04-19 13:17:28.255: I/System.out(25522): updateTasksOrder
		04-19 13:17:28.255: I/System.out(25522): checkComponentPermission
		04-19 13:17:28.255: I/System.out(25522): getMyMemoryState
		04-19 13:17:28.255: I/System.out(25522): isHighEndGfx
		04-19 13:17:28.255: I/System.out(25522): isLargeRAM
		04-19 13:17:28.255: I/System.out(25522): isRunningInTestHarness
		04-19 13:17:28.255: I/System.out(25522): isUserAMonkey
		04-19 13:17:28.255: I/System.out(25522): staticGetLargeMemoryClass
		04-19 13:17:28.255: I/System.out(25522): staticGetMemoryClass 
     */

}


