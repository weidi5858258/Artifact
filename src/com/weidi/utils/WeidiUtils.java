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
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
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
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.db.bean.AppInfos;
import com.weidi.artifact.db.bean.ProcessInfos;

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
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import org.apache.http.HttpResponse;
//import org.apache.http.HttpStatus;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpGet;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;

//伟弟的工具类
public class WeidiUtils {

    private static Toast mToast;
    private static Handler mHandler;
    private static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mToast.cancel();
        }
    };

    /**
     * 在主线程中弹出吐司
     *
     * @param context
     * @param text
     * @param duration
     */
    public static void showToast(Context context, String text, int duration) {
        mHandler = new Handler();
        mHandler.removeCallbacks(runnable);
        if (mToast != null) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(context, text, duration);
        }
        mHandler.postDelayed(runnable, 5000);
        mToast.show();
    }

    /**
     * 在子线程中弹出吐司
     *
     * @param context
     * @param text
     * @param duration
     */
    public static void showToastAtThread(Context context, String text, int duration) {
        Looper.prepare();
        mHandler = new Handler();
        mHandler.removeCallbacks(runnable);
        if (mToast != null) {
            mToast.setText(text);
        } else {
            mToast = Toast.makeText(context, text, duration);
        }
        mHandler.postDelayed(runnable, 5000);
        mToast.show();
        Looper.loop();
    }

    public static void showToast(Context context, int strId, int duration) {
        showToast(context, context.getString(strId), duration);
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
        try {
            PackageManager pm = ((MyApplication) context.getApplicationContext()).mPackageManager;
            Class c = PackageManager.class;
            Method method = c.getMethod("installPackage", Uri.class, IPackageInstallObserver
					.class, int.class, String.class);//int.class必须要这样
            method.invoke(pm, packageURI, new MyPackageInstallObserver(context, pathAndFilename),
					0, installerPackageName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MyPackageInstallObserver extends IPackageInstallObserver.Stub {
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

    @SuppressLint("NewApi")//还要一个实体类ProcessInfos
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
                        processInfo.setUserProcess(false);
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

    //还要一个实体类AppInfos
    public static List<AppInfos> getInstalledApplicationInfos(Context context) {
        List<AppInfos> list = new ArrayList<AppInfos>();
        List<ApplicationInfo> list_ai = new ArrayList<ApplicationInfo>();
        PackageManager pm = context.getPackageManager();
        list_ai = pm.getInstalledApplications(0);//PackageManager.GET_ACTIVITIES得到有Activity的应用
        for (ApplicationInfo l : list_ai) {
            AppInfos ai = new AppInfos();
            boolean isUserApp = true;//true时为用户程序
            boolean isInstallMemory = true;//true时为安装在手机内存中
            Drawable icon = pm.getApplicationIcon(l);//应用图标
            int flag = l.flags;
            if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0) {//等于“0”时为用户应用
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


            list.add(ai);
        }

        return list;
    }

    //连环杀进程
    public static void serialKiller(Context context) {//杀后台，不杀前台
        List<RunningTaskInfo> list_runningTacks = ((MyApplication) context.getApplicationContext
				()).mActivityManager.getRunningTasks(1);
        //当前正在运行的应用
        String packageName = list_runningTacks.get(0).topActivity.getPackageName();
        List<AppInfos> appList = ((MyApplication) context.getApplicationContext()).appList;
        String pkg = null;
        for (AppInfos ai : appList) {
            pkg = ai.getPackageName();
            if (pkg.contains(":")) {
                int index = pkg.indexOf(":");
                String newPkg = pkg.substring(0, index);
                if (packageName.contains(newPkg)) {
                    continue;
                }
                if (!((MyApplication) context.getApplicationContext()).pkgList.contains(newPkg)) {
                    WeidiUtils.forceStopPackage(context, pkg);
                }
            } else {//packageName也有可能是com.lbe.security:service这种进程，所以用包含
                if (packageName.contains(pkg)) {
                    continue;
                }
                if (!((MyApplication) context.getApplicationContext()).pkgList.contains(pkg)) {
                    WeidiUtils.forceStopPackage(context, pkg);
                }
            }
        }
        packageName = null;
        list_runningTacks.clear();
        list_runningTacks = null;
        pkg = null;
        Runtime.getRuntime().gc();
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

    //已经能够静默卸载软件了  下面两个是一起的
    public static void deletePackage(Context context, String packageName) {
        try {
            PackageManager pm = ((MyApplication) context.getApplicationContext()).mPackageManager;
            Class c = PackageManager.class;
            Method method = c.getMethod("deletePackage", String.class, IPackageDeleteObserver
					.class, int.class);//int.class必须要这样
            method.invoke(pm, packageName, new MyPackageDeleteObserver(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MyPackageDeleteObserver extends IPackageDeleteObserver.Stub {
        @Override
        public void packageDeleted(String packageName, int returnCode)
                throws RemoteException {//returnCode为1时表示卸载成功

        }
    }

    /**
     * 把一个字节输入流转换成Byte数组
     *
     * @param is
     * @return
     * @throws Exception
     */
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

    /**
     * 把一个字节输入流转换成String
     *
     * @param is
     * @return
     * @throws Exception
     */
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
     * 给一个文件加上MD5签名
     *
     * @param path 路径
     * @return
     */
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

    /**
     * 用MD5加密方式给一个字符串加密
     *
     * @param password 字符串
     * @return
     * @throws NoSuchAlgorithmException
     */
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

    //开玩笑 没测试
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

    /**
     * 网络连接是否可用
     *
     * @param context 上下文
     * @return
     */
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

    /**
     * Wifi是否可用 利用系统广播去检测网络状态发生变化时然后去判断Wifi网络是否可用
     *
     * @param context
     * @return
     */
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

    /**
     * Wifi是否已连接
     *
     * @param context
     * @return
     */
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

    /**
     * 验证一个字符是否是否是汉字 汉字输入法下输入的标点符号也是一个汉字字符，返回true
     *
     * @param c
     * @return
     */
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

    /**
     * 判断某个服务是否还运行着
     *
     * @param context
     * @param serviceName "包名.类名"
     * @return
     */
    public static boolean isSpecificServiceAlive(Context context, String serviceName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //int maxNum:当系统中正运行着的服务数量小于等于指定的maxNum值时，返回正运行着的所有服务，如果大于的话，就返回指定数量的服务。但是这个时候返回哪些服务我就不知道了。
        List<RunningServiceInfo> services = am.getRunningServices(100);
        for (RunningServiceInfo service : services) {
            String name = service.service.getClassName();
            if (serviceName != null && serviceName.equals(name)) {
                name = null;
                am = null;
                services.clear();
                services = null;
                return true;
            }
        }
        am = null;
        return false;
    }

    /**
     * 得到正在运行的进程的信息
     *
     * @param context
     * @return
     */
    public static List<RunningAppProcessInfo> getRunningAppProcesses(Context context) {
        List<RunningAppProcessInfo> list = new ArrayList<RunningAppProcessInfo>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        list = am.getRunningAppProcesses();
        return list;
    }

    /**
     * HttpClient的Post请求
     *
     * @param url
     * @param content
     * @throws Exception
     */
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

    /**
     * HttpClient的Get请求
     *
     * @param areacode 地区 如输入“浙江”
     * @return
     * @throws Exception
     */
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

    /**
     * HttpClient的Post请求 请求内容传进的应该是List<BasicNameValuePair> parameters比较好
     * @param url
     * @param parameters
     * @throws Exception
     */
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

    /**
     * 隐藏软键盘
     *
     * @param activity
     */
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

    /**
     * 弹出键盘（用处：点击或者滑动后就立即弹出键盘。如果要跳到新界面后立即弹出键盘不能这样用，需要延迟一下才能有效。）
     *
     * @param context
     */
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

    /**
     * 振动一下
     *
     * @param context
     */
    public static void vibrate(Context context) {
        Vibrator vibretor = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibretor.vibrate(100);
        vibretor.cancel();
        vibretor = null;
        Runtime.getRuntime().gc();
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

    /**
     * 用来获取手机拨号上网（包括CTWAP和CTNET）时由PDSN分配给手机终端的源IP地址（还没有测试过）
     *
     * @return
     */
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

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param context
     * @param pxValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param context
     * @param spValue （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        return (int) (spValue * context.getResources().getDisplayMetrics().scaledDensity + 0.5f);
    }

    /**
     * 真正意义上的干掉进程（需要系统签名）
     *
     * @param context
     * @param packageName
     */
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

    public static Bitmap getBitmapFromUrl(String url) throws Exception {
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

    public static byte[] getByteArrayFromUrl(String url) throws Exception {
        byte[] bt = null;
        URL mURL = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) mURL.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(10 * 1000);
        conn.setReadTimeout(5 * 1000);
        conn.connect();//这步可以不写
        int code = conn.getResponseCode();
        if (code / 100 == 2) {
            InputStream is = conn.getInputStream();
            bt = readFromStreamToByte(is);
            is.close();
            is = null;
        }
        conn.disconnect();
        conn = null;
        mURL = null;
        return bt;
    }

    /**
     * 把字节数组保存到本地一个文件中
     *
     * @param file
     * @param data
     * @throws Exception
     */
    public static void saveByteArrayToFile(File file, byte[] data) throws Exception {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
        fos = null;
    }

    /**
     * 根据文件路径保存Bitmap
     */
    public static Bitmap saveBitmap2SDByPath(String filePath, int w, int h) {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, opts);
            int srcWidth = opts.outWidth;
            int srcHeight = opts.outHeight;
            int destWidth = 0;
            int destHeight = 0;
            double ratio = 0.0;
            if (srcWidth < w || srcHeight < h) {
                ratio = 0.0;
                destWidth = srcWidth;
                destHeight = srcHeight;
            } else if (srcWidth > srcHeight) {
                ratio = (double) srcWidth / w;
                destWidth = w;
                destHeight = (int) (srcHeight / ratio);
            } else {
                ratio = (double) srcHeight / h;
                destHeight = h;
                destWidth = (int) (srcWidth / ratio);
            }
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inSampleSize = (int) ratio + 1;
            newOpts.inJustDecodeBounds = false;
            newOpts.outHeight = destHeight;
            newOpts.outWidth = destWidth;
            return BitmapFactory.decodeFile(filePath, newOpts);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @Description 校验是否是手机号（13、14、15、18 xxxx）
     * @author <a href="http://t.cn/RvIApP5">ceychen@foxmail.com</a>
     * @date 2014-7-9 17:46:54
     */
    public static boolean isPhoneNO(String no) {
        Pattern p = Pattern.compile("^1[3|4|5|8]\\d{9}$");
        Matcher m = p.matcher(no);
        return m.matches();
    }

    /**
     * 对常见类型判断是否为空
     */
    public static boolean empty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof String && (obj.equals("") || obj.equals("0"))) {
            return true;
        } else if (obj instanceof Number && ((Number) obj).doubleValue() == 0) {
            return true;
        } else if (obj instanceof Boolean && !((Boolean) obj)) {
            return true;
        } else if (obj instanceof Collection && ((Collection) obj).isEmpty()) {
            return true;
        } else if (obj instanceof Map && ((Map) obj).isEmpty()) {
            return true;
        } else if (obj instanceof Object[] && ((Object[]) obj).length == 0) {
            return true;
        }
        return false;
    }

    /**
     * 产生一个最大为max随机数
     */
    public static String getRandomNum(int max) {
        Random random = new Random();
        return String.valueOf(random.nextInt(max + 1));
    }

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

    /**
     * 返回屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getWindowWidth(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 返回屏幕高度
     *
     * @param context
     * @return
     */
    public static int getWindowHeight(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 得到设备的密度
     */
    public static float getScreenDensity(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        File[] fileList = file.listFiles();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].isDirectory()) {
                size = size + getFolderSize(fileList[i]);
            } else {
                size = size + fileList[i].length();
            }
        }
        //Formatter.formatFileSize(context, size);
        return size;
    }

    /**
     * @Description 删除某目录下所有文件
     */
    public static void deleteAllFilesOfDir(File path, boolean flag) {
        if (!path.exists() || path == null)
            return;
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();//能直到这一步说明path是个目录了
        for (int i = 0; i < files.length; i++) {//删除path下所有文件和目录，但不删除自身
            deleteAllFilesOfDir(files[i], true);//递归调用 会再判断files[i]是文件还是目录
        }
        if (flag) {//flag为true时把自身也删了
            path.delete();
        }
    }

    /**
     * 截屏
     *
     * @param width
     * @param height
     * @return
     */
    @SuppressLint("NewApi")
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

    /**
     * @return true if the current device is "voice capable".
     * <p>
     * "Voice capable" means that this device supports circuit-switched
     * (i.e. voice) phone calls over the telephony network, and is allowed
     * to display the in-call UI while a cellular voice call is active.
     * This will be false on "data only" devices which can't make voice
     * calls and don't support any in-call UI.
     * <p>
     * Note: the meaning of this flag is subtly different from the
     * PackageManager.FEATURE_TELEPHONY system feature, which is available
     * on any device with a telephony radio, even if the device is
     * data-only.
     */
    //    public static boolean isVoiceCapable(Context context) {//判断当前环境是不是一部可打电话的移动设备
    //    	boolean flag = false;
    //		try {
    //			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context
	// .TELEPHONY_SERVICE);
    //			Class c = TelephonyManager.class;
    //			Method method = c.getDeclaredMethod("isVoiceCapable", null);
    //			flag = (Boolean) method.invoke(tm, null);
    //		} catch (NoSuchMethodException e) {
    //			e.printStackTrace();
    //		} catch (IllegalAccessException e) {
    //			e.printStackTrace();
    //		} catch (IllegalArgumentException e) {
    //			e.printStackTrace();
    //		} catch (InvocationTargetException e) {
    //			e.printStackTrace();
    //		}
    //		return flag;
    //    }

    //    下面方法跟我原来的httpGet()、httpPost()方法有冲突
    //    public String httpGet(String url, String queryString, int timeout) throws Exception {
    //		String responseData = null;
    //
    //		if(!TextUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith
	// ("https://"))){
    //			if (queryString != null && !queryString.equals("")) {
    //				if (url.indexOf("?") != -1) {
    //					url += "&" + queryString;
    //				} else {
    //					url += "?" + queryString;
    //				}
    //			}
    //			if (timeout == 0) {
    //				timeout = 10 * 60 * 1000;
    //			}
    //			HttpClient httpClient = new HttpClient();
    //			GetMethod httpGet = new GetMethod(url);
    //			httpGet.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
    //			httpGet.getParams().setParameter("http.socket.timeout", new Integer(timeout));//设置超时
    //			try {
    //				int statusCode = httpClient.executeMethod(httpGet);
    //				if (statusCode != HttpStatus.SC_OK) {
    //					System.err.println("HttpGet Method failed: " + httpGet.getStatusLine());
    //				}
    //				responseData = httpGet.getResponseBodyAsString();
    //				System.err.println("NetHttpClient：httpGet 请求编码：" + httpGet.getRequestCharSet() + "，响应编码："
    //						+ httpGet.getResponseCharSet());
    //			} catch (Exception e) {
    //				e.printStackTrace();
    //			} finally {
    //				httpGet.releaseConnection();
    //				httpGet = null;
    //				httpClient = null;
    //			}
    //		}
    //
    //		return responseData;
    //	}
    //
    //	public String httpPost(String url, String queryString, int timeout) throws Exception {
    //		String responseData = null;
    //
    //		if(!TextUtils.isEmpty(url) && (url.startsWith("http://") || url.startsWith("https://"))){
    //			if (timeout == 0) {
    //				timeout = 10 * 60 * 1000;
    //			}
    //			HttpClient httpClient = new HttpClient();
    //			PostMethod httpPost = new PostMethod(url);
    //			httpPost.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
    //			httpPost.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    //			httpPost.getParams().setParameter("http.socket.timeout", new Integer(timeout));
    //			if (queryString != null && !queryString.equals("")) {
    //				httpPost.setRequestEntity(new ByteArrayRequestEntity(queryString.getBytes()));
    //			}
    //
    //			try {
    //				int statusCode = httpClient.executeMethod(httpPost);
    //				if (statusCode != HttpStatus.SC_OK) {
    //					System.err.println("HttpPost Method failed: " + httpPost.getStatusLine());
    //				}
    //				responseData = httpPost.getResponseBodyAsString();
    //				System.err.println("NetHttpClient：httpPost 请求编码：" + httpPost.getRequestCharSet() + "，响应编码："
    //						+ httpPost.getResponseCharSet());
    //			} catch (Exception e) {
    //				throw new Exception(e);
    //			} finally {
    //				httpPost.releaseConnection();
    //				httpPost = null;
    //				httpClient = null;
    //			}
    //		}
    //
    //		return responseData;
    //	}
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


