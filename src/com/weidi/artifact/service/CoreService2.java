package com.weidi.artifact.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.activity.CameraActivity;
import com.weidi.artifact.activity.ReceiveSMSsActivity;
import com.weidi.artifact.activity.RecentTaskActivity;
import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.db.bean.AppInfos;
import com.weidi.artifact.db.bean.BlacklistInfo;
import com.weidi.artifact.db.bean.BlacklistPhone;
import com.weidi.artifact.db.bean.BlacklistSms;
import com.weidi.artifact.db.dao.BlacklistDao;
import com.weidi.artifact.db.dao.PhoneNumberAddressQueryUtils;
import com.weidi.utils.MyToast;
import com.weidi.utils.MyUtils;
import com.weidi.utils.WeidiUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//SIMIsChangedReceiver在这个广播中启动 还有StartActivity
public class CoreService2 extends Service implements OnClickListener, OnTouchListener {

    private static final String TAG = "CoreService";
    private static final boolean DEBUG = true;
    private Context mContext;
    private TelephonyManager mTelephonyManager;
    private AnnoyingBroadcastReceiver mAnnoyingBroadcastReceiver;
    private IntentFilter mIntentFilter;
    private BlacklistDao mBlacklistDao;
    private BlacklistInfo mBlacklistInfo;
    private WindowManager mCallWindowManager;
    private WindowManager.LayoutParams mCallLayoutParams;

    private Uri uri = Uri.parse("content://Sms");
    private SmsMessage mSmsMessage;
    private ContentResolver mContentResolver;
    private ContentValues mContentValues;

    public static boolean isScreenOnOrOff = true;// 用于判断屏幕是否亮着 true为点亮状态
    // 用于测试
    private PendingIntent securityPhonePendingIntent;

    private List<RunningAppProcessInfo> mRunningAppProcessInfoList;// 正在运行的进程的集合
    private boolean notifyChangeProcessCount_flag = true;

    private SDCardFileObserver mSdCardFileObserver;

    private Vibrator mVibretor;// 振动

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        init();


        // 监听电话的状态
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (mPhoneStateListener == null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        mVibretor = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);

        myBroadcastReceiver();

        if (null == mSdCardFileObserver) {
            String path = "/storage/sdcard0/Download/app/";
            mSdCardFileObserver = new SDCardFileObserver(path);
            mSdCardFileObserver.startWatching(); // 开始监听
        }

        initCallView();

    }

    @Override
    public void onDestroy() {
        if (mPhoneStateListener != null) {
            mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
            mPhoneStateListener = null;
        }
        if (mAnnoyingBroadcastReceiver != null) {
            mContext.unregisterReceiver(mAnnoyingBroadcastReceiver);
            mAnnoyingBroadcastReceiver = null;
        }

        if (securityPhonePendingIntent != null) {
            ((MyApplication) getApplication()).mAlarmManager.cancel(securityPhonePendingIntent);
            securityPhonePendingIntent = null;
        }

        notifyChangeProcessCount_flag = false;

        if (MyUtils.isSpecificServiceAlive(mContext, "PeriodicalSerialKillerService")) {
            if (periodicalSerialKillerServiceIntent != null) {
                stopService(periodicalSerialKillerServiceIntent);
                periodicalSerialKillerServiceIntent = null;
            }
        }
        if (null != mSdCardFileObserver) {
            mSdCardFileObserver.stopWatching(); // 停止监听
            mSdCardFileObserver = null;
        }
    }

    private long idleTime;
    private long ringTime;
    private long hookTime;
    private long outCallTime;
    private long outHookTime;
    private long outIdleTime;
    private boolean inRing = false;
    private boolean inBlackRing = false;
    private boolean inHook = false;
    private boolean outRing = false;
    private boolean outHook = false;
    private String inNumber;
    private String outNumber;
    private String address;// 打出去的电话接通与没有接通这两个状态还没有搞好
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            if (incomingNumber != null) {
                inNumber = incomingNumber;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            BlacklistPhone phone = null;
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE: {// 空闲

                    idleTime = System.currentTimeMillis();
                    System.out.println("CALL_STATE_IDLE---idleTime:" + idleTime);
                    // 打进来的电话
                    if (inRing && inHook) {// 表示打进来的电话已经接通过
                        address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(inNumber);
                        inRing = false;
                        inHook = false;
                        hookTime = idleTime - hookTime;// 通话时间，转换成秒
                        String time = sdf.format(new Date(ringTime));
                        // 打进来的电话有时没号码怎么回事？
                        phone = new BlacklistPhone(inNumber, address, ringTime, (int) (hookTime /
                                1000), time, 1, 1, 1);
                        System.out.println("flag:1" + " inNumber:" + inNumber + " " + address);
                    } else if (inRing && !inBlackRing && !inHook) {
                        address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(inNumber);
                        inRing = false;
                        String time = sdf.format(new Date(ringTime));
                        ringTime = idleTime - ringTime;// 响铃时间
                        phone = new BlacklistPhone(inNumber, address, ringTime, (int) (ringTime /
                                1000), time, 3, 1, 0);
                        System.out.println("flag:0" + " inNumber:" + inNumber + " " + address);
                    }

                    // http://192.168.1.158:8080/UploadFileServer/upload.jsp
                    // update blacklist_phone set number='18918366438' where
                    // date='1429785760219';
                    // 打出去的电话 怎样才能知道打出去的电话没有被接通过？？？
                    // 打出去的电话执行过程：
                    // 1、先执行广播那里
                    // 2、在执行TelephonyManager
                    // .CALL_STATE_OFFHOOK（紧接着上一步）因此outCallTime与outHookTime的数值几乎相等
                    // 3、在执行TelephonyManager.CALL_STATE_IDLE（不管打出去的电话有没有接通过）
                    outIdleTime = System.currentTimeMillis();
                    if (outRing && outHook) {// 拨打出去的电话已经接通过
                        address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(outNumber);
                        outRing = false;
                        outHook = false;
                        outHookTime = outIdleTime - outHookTime;
                        String time = sdf.format(new Date(outCallTime));
                        phone = new BlacklistPhone(outNumber, address, outCallTime, (int)
                                (outHookTime / 1000), time, 2, 1, 4);
                        System.out.println("flag:4");
                    } else if (outRing && !outHook) {// 拨打出去的电话没有接通（根本执行不到）
                        address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(outNumber);
                        outRing = false;
                        String time = sdf.format(new Date(outCallTime));
                        outCallTime = outIdleTime - outCallTime;
                        phone = new BlacklistPhone(outNumber, address, outCallTime, (int)
                                (outCallTime / 1000), time, 2, 1, 3);
                        System.out.println("flag:3");
                    }

                    if (phone != null) {
                        mBlacklistDao.addBlacklistPhone(phone);
                        Intent intent = new Intent();
                        intent.setAction("com.aowin.mobilesafe.updateadapter.phone");
                        intent.putExtra("phone", phone);
                        mContext.sendBroadcast(intent);
                        phone = null;
                    }

                    outNumber = null;
                    address = null;
                    System.out.println("CALL_STATE_IDLE---outIdleTime:" + outIdleTime);
                    break;
                }

                case TelephonyManager.CALL_STATE_RINGING: {// 响铃
                    try {
                        inRing = true;
                        inBlackRing = false;
                        ringTime = System.currentTimeMillis();
                        if (mBlacklistDao.isNumberExist(inNumber)) {
                            mBlacklistInfo = mBlacklistDao.query(inNumber);
                            String mode = mBlacklistInfo.getMode();
                            if ("1".equals(mode) || "3".equals(mode)) {
                                inBlackRing = true;
                                /*ITelephony iTelephony = MyUtils.getITelephony(mContext);
                                iTelephony.silenceRinger();// 静音
                                iTelephony.endCall();// 挂断*/
                                String time = sdf.format(new Date(ringTime));
                                address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery
                                        (inNumber);
                                phone = new BlacklistPhone(inNumber, address, ringTime, 0, time,
                                        1, 1, 2);
                                mBlacklistDao.addBlacklistPhone(phone);

                                Intent intent = new Intent();
                                intent.setAction("com.aowin.mobilesafe.updateadapter.phone");
                                intent.putExtra("phone", phone);
                                mContext.sendBroadcast(intent);

                                outNumber = null;
                                address = null;
                                // System.out.println("flag:2");
                                // iTelephony.answerRingingCall();
                                // iTelephony.setRadio(false);
                                // iTelephony.setRadioPower(false);
                                // iTelephony.toggleRadioOnOff();
                                // iTelephony.updateServiceLocation();
                                // iTelephony.setDataEnabled(false);
                                // iTelephony.silenceRinger();//静音
                                // 虽然被挂断了，但还是要把这次通话给记录下来
                            }
                        }
                        System.out.println("CALL_STATE_RINGING---ringTime:" + ringTime);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }

                case TelephonyManager.CALL_STATE_OFFHOOK: {// 通话（电话一打出去就执行这里的代码）
                    hookTime = System.currentTimeMillis();
                    outHookTime = System.currentTimeMillis();
                    inHook = true;
                    outHook = true;
                    System.out.println("CALL_STATE_OFFHOOK---outHookTime:" + outHookTime);
                    break;
                }
            }
            super.onCallStateChanged(state, incomingNumber);
        }

    };

    private View callView;
    private EditText number;

    /**
     * 打电话的
     */
    private void call() {
        mCallWindowManager.addView(callView, mCallLayoutParams);
    }

    private void initCallView() {
        callView = View.inflate(mContext, R.layout.activity_call, null);
        number = (EditText) callView.findViewById(R.id.number);
        number.setFocusable(true);
        number.setFocusableInTouchMode(true);
        number.requestFocus();
        TextView call = (TextView) callView.findViewById(R.id.call);
        TextView cancel = (TextView) callView.findViewById(R.id.cancel);
        call.setOnClickListener(this);
        cancel.setOnClickListener(this);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context
                        .INPUT_METHOD_SERVICE);
                imm.showSoftInput(number, 0);
                imm = null;
            }
        };
        timer.schedule(task, 100);
        task = null;
        timer = null;

        mCallLayoutParams = new WindowManager.LayoutParams();
        mCallLayoutParams.alpha = 30;
        mCallLayoutParams.gravity = Gravity.TOP + Gravity.LEFT;
        mCallLayoutParams.width = (int) (mCallWindowManager.getDefaultDisplay().getWidth() * 3 / 5);
        mCallLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // mCallLayoutParams.flags =
        // WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//要想弹出软键盘，就不能设置这个属性
        mCallLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        mCallLayoutParams.format = 1;// 很重要 按钮按几下后，状态不恢复到原来那样子，设置这个后就好了

        callView.setOnTouchListener(new OnTouchListener() {
            int startX_call;
            int startY_call;
            int newX_call;
            int newY_call;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        startX_call = (int) event.getRawX();
                        startY_call = (int) event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        newX_call = (int) event.getRawX();
                        newY_call = (int) event.getRawY();
                        int dx = newX_call - startX_call;
                        int dy = newY_call - startY_call;
                        mCallLayoutParams.x += dx;
                        mCallLayoutParams.y += dy;
                        if (mCallLayoutParams.x < 0) {
                            mCallLayoutParams.x = 0;
                        }
                        if (mCallLayoutParams.y < 0) {
                            mCallLayoutParams.y = 0;
                        }
                        if (mCallLayoutParams.x > (mCallWindowManager.getDefaultDisplay().getWidth
                                () - callView.getWidth())) {
                            mCallLayoutParams.x = mCallWindowManager.getDefaultDisplay().getWidth()
                                    - callView.getWidth();
                        }
                        if (mCallLayoutParams.y > (mCallWindowManager.getDefaultDisplay().getHeight
                                () - callView.getHeight())) {
                            mCallLayoutParams.y = mCallWindowManager.getDefaultDisplay().getHeight
                                    () - callView.getHeight();
                        }
                        mCallWindowManager.updateViewLayout(callView, mCallLayoutParams);// 更新view
                        startX_call = (int) event.getRawX();
                        startY_call = (int) event.getRawY();
                        break;
                    }
                }
                return false;
            }
        });
    }

    // Sdcard0Mapping
    private final int SERIAL_KILLER_PROCESS_COUNT = 0;
    private Handler handler;

    private void killRunningProcessIcon() {

        mRunningAppProcessInfoList = MyUtils.getRunningAppProcessInfo(mContext);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                handler = new Handler() {
                    public void handleMessage(android.os.Message msg) {
                        if (msg.what == SERIAL_KILLER_PROCESS_COUNT) {
                            // list = MyUtils.getAllRunningProcesses(mContext);
                            mRunningAppProcessInfoList = MyUtils.getRunningAppProcessInfo(mContext);
                            if (notifyChangeProcessCount_flag) {
                                handler.sendEmptyMessageDelayed(SERIAL_KILLER_PROCESS_COUNT, 2 *
                                        1000);
                            }
                        }
                    }

                    ;
                };
                // 放的位置要注意，不然要抛空指针异常。还有就是不能发送消息。
                handler.sendEmptyMessage(SERIAL_KILLER_PROCESS_COUNT);
                Looper.loop();
            }
        }).start();

        // view.setOnClickListener(new OnClickListener() {
        // long firstClickTime;
        // @Override
        // public void onClick(View v) {//双击事件
        // if(firstClickTime > 0){
        // long secondClickTime = SystemClock.uptimeMillis();
        // long dtime = secondClickTime - firstClickTime;
        // if(dtime <= 500){
        // serialKiller(mContext);
        // }else{
        // firstClickTime = 0;
        // }
        // return;
        // }
        // firstClickTime = SystemClock.uptimeMillis();
        // new Thread(new Runnable() {
        // @Override
        // public void run() {
        // SystemClock.sleep(500);
        // firstClickTime = 0;
        // }
        // }).start();
        // }
        // });

		/*
         * params = new WindowManager.LayoutParams(); params.alpha = 30;
		 * params.format = 1;//很重要 按钮按几下后，状态不恢复到原来那样子，设置这个后就好了 params.gravity =
		 * Gravity.TOP + Gravity.LEFT; params.x =
		 * wm.getDefaultDisplay().getWidth() / 2 - view.getWidth() / 2; params.y
		 * = wm.getDefaultDisplay().getHeight() / 2 - view.getHeight() / 2;
		 * params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
		 * params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		 * params.width = WindowManager.LayoutParams.WRAP_CONTENT; params.height
		 * = WindowManager.LayoutParams.WRAP_CONTENT;
		 */

		/*
         * view.setOnTouchListener(new OnTouchListener() { int startX; int
		 * startY; int newX; int newY;
		 *
		 * @Override public boolean onTouch(View v, MotionEvent event) {
		 * switch(event.getAction()){ case MotionEvent.ACTION_UP:{ break; } case
		 * MotionEvent.ACTION_DOWN:{ startX = (int) event.getRawX(); startY =
		 * (int) event.getRawY(); break; } case MotionEvent.ACTION_MOVE:{ newX =
		 * (int) event.getRawX(); newY = (int) event.getRawY(); int dx = newX -
		 * startX; int dy = newY - startY; params.x += dx; params.y += dy;
		 * if(params.x < 0){ params.x = 0; } if(params.y < 0){ params.y = 0; }
		 * if(params.x > (wm.getDefaultDisplay().getWidth() - view.getWidth())){
		 * params.x = wm.getDefaultDisplay().getWidth() - view.getWidth(); }
		 * if(params.y > (wm.getDefaultDisplay().getHeight() -
		 * view.getHeight())){ params.y = wm.getDefaultDisplay().getHeight() -
		 * view.getHeight(); } wm.updateViewLayout(view, params);//更新view startX
		 * = (int) event.getRawX(); startY = (int) event.getRawY(); break; } }
		 * return false;//如果返回true的话，就没有点击事件了 } }); wm.addView(view, params);
		 */

    }

    /*
     * 点击事件
     */
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.call: {// 打电话按钮
                try {
                    //                    String myNumber = number.getText().toString().trim();
                    //                    MyUtils.getITelephony(mContext).call(myNumber);
                    //                    if (callView != null && callView.isShown()) {
                    //                        mCallWindowManager.removeView(callView);
                    //                        callView = null;
                    //                        mCallLayoutParams = null;
                    //                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
            case R.id.cancel: {// 打电话取消按钮
                if (callView != null && callView.isShown()) {
                    mCallWindowManager.removeView(callView);
                    //					callView = null;
                }
                break;
            }
        }
    }

    // 连环杀手---杀进程
    boolean wait = true;// 防止多次点击

    public void serialKiller(Context context) {// 全杀，包括前台
        if (wait) {
            wait = false;
            // 把集合存到内存中，这样速度更快---把安装的每个应用给杀一遍
            List<AppInfos> appList = ((MyApplication) getApplication()).appList;
            // List<AppInfos> userList =
            // ((WeidiApplication)getApplication()).userList;
            String pkg = null;
            for (AppInfos ai : appList) {
                pkg = ai.getPackageName();
                if (pkg.contains(":")) {
                    int index = pkg.indexOf(":");
                    String newPkg = pkg.substring(0, index);
                    if (!((MyApplication) context.getApplicationContext()).pkgList.contains
                            (newPkg)) {
                        MyUtils.forceStopPackage(context, pkg);
                    }
                } else {// packageName也有可能是com.lbe.security:service这种进程，所以用包含
                    if (!((MyApplication) context.getApplicationContext()).pkgList.contains(pkg)) {
                        MyUtils.forceStopPackage(context, pkg);
                    }
                }
            }
            wait = true;
        }
    }

    /**
     * 程序包新增加或者被删除时发一个通知给MyApplication，让它更新一下数据
     */
    public void notifyPackageAddOrRemove(Context context) {
        Intent intent = new Intent();
        intent.setAction("com.aowin.mobilesafe.package");
        context.sendBroadcast(intent);
    }

    /**
     * 接收短信的业务逻辑
     */
    private void receiveSMSsService(Context context, Intent intent) {
        // 收到短信的广播 短信长度：中文：70 英文：160 （只要有一个中方输入，不包括中方状态下的标点符号，一条短信就只能是70个字符）
        String address = "";
        String body = "";
        long receiveTime = 0;
        String time = "";

        StringBuffer sb = new StringBuffer();
        if (intent != null) {
            MyToast.show("android.provider.Telephony.SMS_RECEIVED");
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            int counts = objs.length;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Object obj : objs) {
                mSmsMessage = SmsMessage.createFromPdu((byte[]) obj);
                address = mSmsMessage.getOriginatingAddress();// 发件人号码
                body = mSmsMessage.getMessageBody().trim();// 短信内容
                sb.append(body);
                receiveTime = mSmsMessage.getTimestampMillis();
                time = sdf.format(receiveTime);// 收到短信时的时间 存入数据库的数据则是long类型的时间值
            }

            // 我没有判断拦截黑名单的服务是否在运行，我想让这个服务一直在运行
            if (mBlacklistDao.isNumberExist(address)) {// 黑名单
                BlacklistInfo info = mBlacklistDao.query(address);
                String mode = info.getMode();
                if ("2".equals(mode) || "3".equals(mode)) {
                    String place = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(address);
                    mBlacklistDao.addBlacklistSms(address, place, receiveTime, time, 1, 1, sb
                            .toString());
                    // 更新黑名单中的短信拦截界面数据
                    // CommunicationGuardBlacklistSmsFragment中定义的广播
                    BlacklistSms sms = new BlacklistSms(address, place, receiveTime, time, 1, 1,
                            sb.toString());
                    intent = new Intent();
                    intent.setAction("com.aowin.mobilesafe.updateadapter.Sms");
                    intent.putExtra("Sms", sms);
                    context.sendBroadcast(intent);
                    return;
                } else {
                    receiveSMSs(context, intent, address, receiveTime, sb, time);
                }
            } else {
                receiveSMSs(context, intent, address, receiveTime, sb, time);
            }

        }

    }

    // 抽取的方法
    public void receiveSMSs(final Context context, Intent intent, String address, long
            receiveTime, StringBuffer sb, String time) {
        Thread ringThread = new Thread() {
            @Override
            public void run() {
                super.run();
                WeidiUtils.ring(context);
            }
        };
        ringThread.start();
        // //系统声音 权限：android.permission.WRITE_SETTINGS
        // Uri notification =
        // RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // Ringtone ring = RingtoneManager.getRingtone(mContext, notification);
        // ring.play();

        Uri uriContacts = Uri.parse("content://com.android.contacts/data");
        mContentValues = new ContentValues();
        mContentValues.put("address", address);
        mContentValues.put("date", receiveTime);
        mContentValues.put("read", 1);// 1为已读 0为未读
        mContentValues.put("type", 1);// 1为接收到的短信 2为发送的短信
        mContentValues.put("body", sb.toString());
        mContentResolver.insert(uri, mContentValues);

        String name = "";
        // 根据号码查找在本地通讯录中是否有联系人，如果有，则把这个人的姓名给取出并显示，否则显示发件人的号码。
        Cursor cursor_raw_contact_id = mContentResolver.query(uriContacts, new
                String[]{"raw_contact_id"}, "data1=?", new String[]{address}, null);
        Cursor cursor_name = null;
        while (cursor_raw_contact_id.moveToNext()) {
            String raw_contact_id = cursor_raw_contact_id.getString(cursor_raw_contact_id
                    .getColumnIndex("raw_contact_id"));
            cursor_name = mContentResolver.query(uriContacts, new String[]{"data1"},
                    "mimetype_id=? and raw_contact_id=?", new String[]{"7", raw_contact_id}, null);
            while (cursor_name.moveToNext()) {
                // 得到联系人的姓名
                name = cursor_name.getString(cursor_name.getColumnIndex("data1"));
            }
        }
        if (cursor_name != null) {
            cursor_name.close();
            cursor_name = null;
        }
        if (cursor_raw_contact_id != null) {
            cursor_raw_contact_id.close();
            cursor_raw_contact_id = null;
        }
        if (TextUtils.isEmpty(name)) {
            name = address;// 没有联系人就显示号码
        }

        // 发到ReceiveSMSsActivity去的
        intent = new Intent();
        intent.setAction("com.aowin.mobilesafe.Sms");
        intent.putExtra("name", name);
        intent.putExtra("address", address);
        intent.putExtra("body", sb.toString());
        intent.putExtra("time", time);
        intent.putExtra("receiveTime", String.valueOf(receiveTime));
        context.sendBroadcast(intent);

        ringThread = null;
        Runtime.getRuntime().gc();

        intent = new Intent();
        intent.setClass(mContext, ReceiveSMSsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("name", name);
        intent.putExtra("address", address);
        intent.putExtra("body", sb.toString());
        intent.putExtra("time", time);
        context.startActivity(intent);
    }

    /**
     * 检测文件的增加、删除
     */
    private class SDCardFileObserver extends FileObserver {
        private Thread installAppThread;

        public SDCardFileObserver(String path) {
            super(path);
        }

        // mask:指定要监听的事件类型，默认为FileObserver.ALL_EVENTS
        public SDCardFileObserver(String path, int mask) {
            super(path, mask);
        }

        @Override
        public void onEvent(int event, final String path) {
            final int action = event & FileObserver.ALL_EVENTS;
            switch (action) {
                case FileObserver.CREATE:// .jpg .png .xml .mp3 .mp4 .avi .txt
                    // .apk
                    installAppThread = new Thread() {
                        @Override
                        public void run() {
                            if (path != null) {
                                String ph = "/storage/sdcard0/Download/app/";
                                File file = new File(ph);// file是否存在也不判断了，因为这是给自己用的，所以本人确实这个文件是存在的
                                File[] files = file.listFiles();
                                for (File f : files) {
                                    if (f.getName().endsWith(".apk")) {// f.getAbsolutePath()
                                        // == ph
                                        // +
                                        // path;
                                        String packageName = MyUtils.getAppPackageName(mContext,
                                                ph, path);
                                        MyUtils.installPackage(mContext, Uri.parse(f
                                                .getAbsolutePath()), packageName, f
                                                .getAbsolutePath());
                                    }
                                }
                                ph = null;
                                file = null;
                                files = null;
                            }
                            installAppThread = null;
                        }
                    };
                    installAppThread.start();
                    break;
                // case FileObserver.OPEN:
                // if(path != null)System.out.println("event: 文件或目录被打开, path: "
                // + path);
                // break;
                // case FileObserver.CLOSE_WRITE:
                // if(path != null)System.out.println("event: 文件或目录被关闭, path: "
                // + path);
                // break;
                // case FileObserver.ACCESS:
                // if(path != null)System.out.println("event: 文件或目录被访问, path: "
                // + path);
                // break;
                // case FileObserver.MODIFY:
                // if(path != null)System.out.println("event: 文件或目录被修改, path: "
                // + path);
                // break;
                // case FileObserver.DELETE:
                // if(path != null)System.out.println("event: 文件或目录被删除, path: "
                // + path);
                // break;
            }
        }

    }

    /**
     * 定义的广播
     */
    private void myBroadcastReceiver() {
        mIntentFilter = new IntentFilter();
        mIntentFilter.setPriority(2147483647);

        mIntentFilter.addAction("android.intent.action.ACTION_POWER_CONNECTED");//
        mIntentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");//
        mIntentFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");//
        mIntentFilter.addAction("android.intent.action.SCREEN_OFF");//
        mIntentFilter.addAction("android.intent.action.SCREEN_ON");//
        mIntentFilter.addAction("android.intent.action.TIME_TICK");//
        mIntentFilter.addAction("android.intent.action.UID_REMOVED");//
        mIntentFilter.addAction("android.media.RINGER_MODE_CHANGED");// 改变声音状态（有声变静音）时广播
        mIntentFilter.addAction("android.media.VIBRATE_SETTING_CHANGED");// 颤动变为不颤动时广播
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");//

        mAnnoyingBroadcastReceiver = new AnnoyingBroadcastReceiver();
        mContext.registerReceiver(mAnnoyingBroadcastReceiver, mIntentFilter);
    }

    private Intent periodicalSerialKillerServiceIntent;// 充电时用于开启服务 拔掉电源时关闭服务

    /**
     * 广播的处理
     */
    private class AnnoyingBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                if ("android.intent.action.ACTION_POWER_CONNECTED".equals(intent.getAction())) {
                    // if(!MyUtils.isSpecificServiceAlive(mContext,
                    // "PeriodicalSerialKillerService")){
                    // periodicalSerialKillerServiceIntent = new
                    // Intent(mContext, PeriodicalSerialKillerService.class);
                    // mContext.startService(periodicalSerialKillerServiceIntent);
                    // }
                } else if ("android.intent.action.ACTION_POWER_DISCONNECTED".equals(intent
                        .getAction())) {
                    // if(MyUtils.isSpecificServiceAlive(mContext,
                    // "PeriodicalSerialKillerService")){
                    // stopService(periodicalSerialKillerServiceIntent);
                    // periodicalSerialKillerServiceIntent = null;
                    // }
                } else if ("android.intent.action.NEW_OUTGOING_CALL".equals(intent.getAction())) {
                    // 有电话打出时的操作
                    outNumber = getResultData();
                    outCallTime = System.currentTimeMillis();
                    outRing = true;
                    System.out.println("NEW_OUTGOING_CALL---outCallTime:" + outCallTime);
                    MyToast.show("android.intent.action.NEW_OUTGOING_CALL");
                } else if ("android.intent.action.SCREEN_OFF".equals(intent.getAction())) {
                    isScreenOnOrOff = false;
                } else if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                    isScreenOnOrOff = true;
                } else if ("android.intent.action.TIME_TICK".equals(intent.getAction())) {
                    // 每分钟广播一次
                } else if ("android.intent.action.UID_REMOVED".equals(intent.getAction())) {
                    notifyPackageAddOrRemove(context);
                    MyToast.show("android.intent.action.UID_REMOVED");
                } else if ("android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
                    receiveSMSsService(context, intent);
                    abortBroadcast();
                }
            }

        }

    }

    // 已经能够截全屏了，只是图片大小有点大。
    private Thread screenShotThread;
    SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    /**
     * 截屏
     */
    @SuppressLint("NewApi")
    public void screenShot() {// 截好屏幕后接下去要实现能够编辑功能，现在还没有实现
        if (screenShotThread == null) {
            screenShotThread = new Thread() {
                public void run() {
                    Date date = new Date();
                    String imagePath = Environment.getExternalStorageDirectory() +
                            "/Pictures/Screenshots/" + mSimpleDateFormat.format(date) + ".png";
                    Bitmap mScreenBitmap;
                    DisplayMetrics mDisplayMetrics;
                    Display mDisplay;

                    mDisplay = mCallWindowManager.getDefaultDisplay();
                    mDisplayMetrics = new DisplayMetrics();
                    mDisplay.getRealMetrics(mDisplayMetrics);

                    float[] dims = {mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels};
                    // screenshot利用反射得到的，所以会费点时间
                    mScreenBitmap = MyUtils.screenshot((int) dims[0], (int) dims[1]);
                    if (mScreenBitmap != null) {
                        try {
                            FileOutputStream out = new FileOutputStream(imagePath);
                            mScreenBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();
                            out = null;
                            mScreenBitmap = null;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mDisplay = null;
                    mDisplayMetrics = null;
                    date = null;
                    if (screenShotThread != null) {
                        screenShotThread.interrupt();
                        screenShotThread = null;
                    }
                    Runtime.getRuntime().gc();
                }

                ;
            };
        }
        screenShotThread.start();
    }

    /***********************************************************************************************************************/
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private int x0, y0, x1, y1;
    private int dx, dy;
    private long time0;
    private View viewLeft, viewTop, viewRight, viewBottom;// 透明窗体
    private int PX = 70;

    /**
     * 初始化
     */
    private void init() {
        mContext = getApplicationContext();
        mCallWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mContentResolver = mContext.getContentResolver();
        mBlacklistDao = new BlacklistDao(mContext);

        viewLeft = LayoutInflater.from(mContext).inflate(R.layout.layout_weidi_left, null);
        viewBottom = LayoutInflater.from(mContext).inflate(R.layout.layout_weidi_bottom, null);
        //		viewRight = LayoutInflater.from(mContext).inflate(R.layout.layout_weidi_right,
        // null);
        /*
         * viewTop =
		 * LayoutInflater.from(mContext).inflate(R.layout.layout_weidi_top,
		 * null); 
		 */

        mWindowManager = (WindowManager) mContext.getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(viewLeft, setLayoutParams(WeidiUtils.px2dip(mContext, PX),
                WindowManager.LayoutParams.MATCH_PARENT, Gravity.TOP, Gravity.LEFT));
        mWindowManager.addView(viewBottom, setLayoutParams(WindowManager.LayoutParams
                .MATCH_PARENT, WeidiUtils.px2dip(mContext, PX), Gravity.BOTTOM, Gravity.LEFT));
        //		mWindowManager.addView(viewRight, setLayoutParams(WeidiUtils.px2dip(mContext, PX),
        // WindowManager.LayoutParams.MATCH_PARENT, Gravity.BOTTOM, Gravity.RIGHT));
        /*
         * mWindowManager.addView(viewTop,
		 * setLayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
		 * WeidiUtils.px2dip(mContext, PX), Gravity.TOP, Gravity.LEFT));
		 */

        viewLeft.setOnTouchListener(this);
        viewBottom.setOnTouchListener(this);
        //		viewRight.setOnTouchListener(this);
        /*
         * viewTop.setOnTouchListener(this);
		 */
    }

    @SuppressLint("InlinedApi")
    private WindowManager.LayoutParams setLayoutParams(int _width, int _height, int position1,
                                                       int position2) {
        mLayoutParams = null;
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.gravity = position1 + position2;
        // mLayoutParams.alpha = 30;
        mLayoutParams.width = _width;
        mLayoutParams.height = _height;
        mLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;// 特别注意在这里设置等级为系统警告
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        // WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
        // WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        mLayoutParams.format = PixelFormat.RGBA_8888;
        return mLayoutParams;
    }

    /*
     * 触摸事件
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                try {
                    x0 = (int) event.getX(0);
                    y0 = (int) event.getY(0);
                    // System.out.println("x0 = "+x0+" y0 = "+y0);
                    x1 = (int) event.getX(1);// 不要注释掉
                    y1 = (int) event.getY(1);// 不要注释掉
                    time0 = System.currentTimeMillis();
                } catch (Exception e1) {
                }
                break;
            case MotionEvent.ACTION_MOVE:

                if (Configuration.ORIENTATION_PORTRAIT == mContext.getResources()
                        .getConfiguration().orientation) {

                    dx = WeidiUtils.getScreenWidth(mContext);
                    dy = WeidiUtils.getScreenHeight(mContext);
                    vertical(v, event);

                } else {

                    dx = WeidiUtils.getScreenWidth(mContext);
                    dy = WeidiUtils.getScreenHeight(mContext);
                    horizontal(v, event);

                }

                break;
            case MotionEvent.ACTION_UP:
                x0 = 0;
                y0 = 0;
                x1 = 0;
                y1 = 0;
                time0 = 0;
                break;
        }
        return true;
    }

    /**
     * 竖屏
     */
    private void vertical(View v, MotionEvent event) {
        try {

            if (event.getX() >= 0 && event.getX() <= PX) {
                // 手指靠左边缘划动
                if (y0 >= 0 && y0 <= (int) (dy / 3.0f) && y0 - event.getY(0) >= PX && System
                        .currentTimeMillis() - time0 >= 500) {
                    // System.out.println("左边缘上---》向上滑动---》");

                    vibrate(20);
                    if (MyUtils.isSpecificServiceAlive(mContext, "PeriodicalSerialKillerService")) {
                        if (periodicalSerialKillerServiceIntent != null) {
                            mContext.stopService(periodicalSerialKillerServiceIntent);
                            periodicalSerialKillerServiceIntent = null;
                            MyToast.show("PeriodicalSerialKillerService is Shutdown");
                        }
                    }

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= 0 && y0 <= (int) (dy / 3.0f) && event.getY(0) - y0 >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("左边缘上---》向下滑动---》");

                    vibrate(20);
                    if (periodicalSerialKillerServiceIntent == null) {
                        periodicalSerialKillerServiceIntent = new Intent(mContext,
                                PeriodicalSerialKillerService.class);
                        mContext.startService(periodicalSerialKillerServiceIntent);
                        MyToast.show("PeriodicalSerialKillerService is Start");
                    }

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= (int) (dy / 3.0f) && y0 <= (int) (2 * dy / 3.0f) && y0 - event
                        .getY(0) >= PX && System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("左边缘中---》向上滑动---》");

                    vibrate(20);
                    turnOffScreen();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= (int) (dy / 3.0f) && y0 <= (int) (2 * dy / 3.0f) && event.getY
                        (0) - y0 >= PX && System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("左边缘中---》向下滑动---》");

                    vibrate(20);
                    goBack();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= (int) (2 * dy / 3.0f) && y0 - event.getY(0) >= PX && System
                        .currentTimeMillis() - time0 >= 500) {
                    // System.out.println("左边缘下---》向上滑动---》");

                    vibrate(20);
                    turnOffScreen();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= (int) (2 * dy / 3.0f) && event.getY(0) - y0 >= PX && System
                        .currentTimeMillis() - time0 >= 500) {
                    // System.out.println("左边缘下---》向下滑动---》");

                    vibrate(20);
                    goBack();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                }
                return;
            }

            if (event.getY(0) >= 0 && event.getY(0) <= PX) {
                // 手指靠下边缘划动
                if (x0 >= 0 && x0 <= (int) (dx / 2.0) && event.getX(0) - x0 >= PX && System
                        .currentTimeMillis() - time0 >= 500) {
                    // System.out.println("下边缘左---》向右滑动---》");

                    vibrate(20);
                    killForegroundApp();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= 0 && x0 <= (int) (dx / 2.0) && x0 - event.getX(0) >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("下边缘左---》向左滑动---》");

                    vibrate(20);
                    goRecentTaskActivity();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= (int) (dx / 2.0) && x0 <= dx && event.getX(0) - x0 >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("下边缘右---》向右滑动---》");

                    time0 = System.currentTimeMillis();
                    vibrate(20);
                    killForegroundApp();

                    x0 = (int) event.getX(0);
                } else if (x0 >= (int) (dx / 2.0) && x0 <= dx && x0 - event.getX(0) >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("下边缘右---》向左滑动---》");

                    vibrate(20);
                    goRecentTaskActivity();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                }
                return;
            }

            if (event.getX(0) - x0 >= PX && System.currentTimeMillis() - time0 >= 500) {
                if (event.getY(0) >= 0 && event.getY(0) < (int) (dy / 3.0f)) {
                    // System.out.println("上");

                    vibrate(20);
                    call();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (event.getY(0) >= (int) (dy / 3.0f) && event.getY(0) < (int) (2 * dy /
                        3.0f)) {
                    // System.out.println("中");

                    vibrate(20);
                    screenShot();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (event.getY(0) >= (int) (2 * dy / 3.0f)) {
                    // System.out.println("下");

                    vibrate(20);
                    goCameraActivity();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                }
                return;
            }

            if (y0 - event.getY(0) >= PX && System.currentTimeMillis() - time0 >= 500) {
                time0 = System.currentTimeMillis();
                y0 = (int) event.getY(0);
                if (event.getX(0) >= 0 && event.getX(0) < (int) (dx / 2.0f)) {
                    // System.out.println("左");

                    vibrate(20);
                    goHome();

                } else if (event.getX(0) >= (int) (dx / 2.0f)) {
                    // System.out.println("右 ");

                    vibrate(20);
                    goHome();

                }
                return;
            }

        } catch (Exception e) {
        }
    }

    /**
     * 横屏
     */
    private void horizontal(View v, MotionEvent event) {
        try {

            if (event.getX() >= 0 && event.getX() <= PX) {
                // 手指靠左边缘划动
                if (y0 >= 0 && y0 <= (int) (dy / 2.0f) && y0 - event.getY(0) >= PX && System
                        .currentTimeMillis() - time0 >= 500) {
                    // System.out.println("左边缘上---》向上滑动---》");

                    vibrate(20);

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= 0 && y0 <= (int) (dy / 2.0f) && event.getY(0) - y0 >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("左边缘上---》向下滑动---》");

                    vibrate(20);
                    goHome();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= (int) (dy / 2.0f) && y0 <= dy && y0 - event.getY(0) >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("左边缘下---》向上滑动---》");

                    vibrate(20);
                    turnOffScreen();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                } else if (y0 >= (int) (dy / 2.0f) && y0 <= dy && event.getY(0) - y0 >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("左边缘下---》向下滑动---》");

                    vibrate(20);
                    goBack();

                    time0 = System.currentTimeMillis();
                    y0 = (int) event.getY(0);
                }
                return;
            }

            if (event.getY(0) >= 0 && event.getY(0) <= PX) {
                // 手指靠下边缘划动
                if (x0 >= 0 && x0 <= (int) (dx / 3.0f) && event.getX(0) - x0 >= PX && System
                        .currentTimeMillis() - time0 >= 500) {
                    // System.out.println("下边缘左---》向右滑动---》");

                    vibrate(20);
                    killForegroundApp();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= 0 && x0 <= (int) (dx / 3.0f) && x0 - event.getX(0) >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("下边缘左---》向左滑动---》");

                    vibrate(20);
                    goRecentTaskActivity();

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= (int) (dx / 3.0f) && x0 <= (int) (2 * dx / 3.0f) && event.getX
                        (0) - x0 >= PX && System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("下边缘中---》向右滑动---》");

                    vibrate(20);

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= (int) (dx / 3.0f) && x0 <= (int) (2 * dx / 3.0f) && x0 - event
                        .getX(0) >= PX && System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("下边缘中---》向左滑动---》");

                    vibrate(20);

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= (int) (2 * dx / 3.0f) && x0 <= dx && event.getX(0) - x0 >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("下边缘右---》向右滑动---》");

                    vibrate(20);

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                } else if (x0 >= (int) (2 * dx / 3.0f) && x0 <= dx && x0 - event.getX(0) >= PX &&
                        System.currentTimeMillis() - time0 >= 500) {
                    // System.out.println("下边缘右---》向左滑动---》");

                    vibrate(20);

                    time0 = System.currentTimeMillis();
                    x0 = (int) event.getX(0);
                }
                return;
            }

            if (event.getX(0) - x0 >= PX && System.currentTimeMillis() - time0 >= 500) {
                time0 = System.currentTimeMillis();
                x0 = (int) event.getX(0);
                if (event.getY(0) >= 0 && event.getY(0) < (int) (dy / 2.0f)) {
                    // System.out.println("上");

                    vibrate(20);

                } else {
                    // System.out.println("下");

                    vibrate(20);

                }
                return;
            }

            if (y0 - event.getY(0) >= PX && System.currentTimeMillis() - time0 >= 500) {
                time0 = System.currentTimeMillis();
                y0 = (int) event.getY(0);
                if (event.getX(0) >= 0 && event.getX(0) < (int) (dx / 3.0f)) {
                    // System.out.println("左");

                    vibrate(20);

                } else if (event.getX(0) >= (int) (dx / 3.0f) && event.getX(0) < (int) (2 * dx /
                        3.0f)) {
                    // System.out.println("中");

                    vibrate(20);

                } else {
                    // System.out.println("右");

                    vibrate(20);

                }
                return;
            }

        } catch (Exception e) {
        }
    }
    /******************************************************************************************/

    /**
     * 后退
     */
    private void goBack() {
        // 经测试必须要开启线程才能达到后退效果
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ((MyApplication) getApplication()).mInstrumentation.sendKeyDownUpSync
                            (KeyEvent.KEYCODE_BACK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Home
     */
    private void goHome() {
        // 后退到桌面并结束当前Activity
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        // 下面两句可能不需要指定 没去测试
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 在服务中开启一个Activity这句少不了
        mContext.startActivity(intent);
        // ((WeidiApplication)getApplication()).getApplicationContext().startActivity(intent);
        intent = null;
    }

    /**
     * 锁屏
     */
    private void turnOffScreen() {
//        if (((WeidiApplication) getApplication()).isAdminActive) {
//            ((WeidiApplication) getApplication()).mDevicePolicyManager.lockNow();// 锁屏
//        } else {
//            Intent intent = new Intent();
//            // 指定动作名称
//            intent.setAction(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//            // 指定给哪个组件授权
//            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, ((WeidiApplication)
//                    getApplication()).myDeviceAdminComponentName);
//            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "注册此组件后才能拥有锁屏功能");
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.startActivity(intent);
//            intent = null;
//        }
    }

    /**
     * 关闭应用
     */
    private void killForegroundApp() {
        List<RunningTaskInfo> list_runningTacks = ((MyApplication) mContext.getApplicationContext
                ()).mActivityManager.getRunningTasks(1);
        if (list_runningTacks != null && list_runningTacks.size() > 0) {
            String packageName = list_runningTacks.get(0).topActivity.getPackageName();
            if (!"com.sec.android.app.launcher".equals(packageName) && !"com.aowin.mobilesafe"
                    .equals(packageName) && !"com.so.launcher".equals(packageName)) {
                MyUtils.forceStopPackage(mContext, packageName);
            }
            list_runningTacks.clear();
            list_runningTacks = null;
            packageName = null;
        }
    }

    /**
     * 杀后台，不杀前台
     */
    private void killBackgroundApp() {
        MyUtils.serialKiller(mContext);// 执行一次大概花费2秒左右的时间
    }

    /**
     * 列出运行过的应用
     */
    private void goRecentTaskActivity() {
        Intent intent = new Intent(((MyApplication) getApplication()).getApplicationContext(),
                RecentTaskActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    /**
     * 拍照
     */
    private void goCameraActivity() {
        Intent cameraActivity = new Intent(mContext, CameraActivity.class);
        cameraActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(cameraActivity);
    }

    private void vibrate(long milliseconds) {
        mVibretor.vibrate(milliseconds);
    }

}
