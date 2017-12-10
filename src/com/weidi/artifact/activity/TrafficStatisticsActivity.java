package com.weidi.artifact.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.weidi.activity.base.BaseActivity;
import com.weidi.artifact.R;
import com.weidi.artifact.db.dao.PhoneNumberAddressQueryUtils;
import com.weidi.artifact.fragment.DataBackupAndRestoreFragment;
import com.weidi.artifact.ui.InputDialog;
import com.weidi.log.Log;
import com.weidi.utils.MyToast;

public class TrafficStatisticsActivity extends Activity implements OnClickListener {
    private Context mContext;
    private AlarmManager am;
    private Intent intent;
    private PendingIntent pi;
    private SharedPreferences sp;
    private Calendar calendar;
    private TextView tv_trafficstatistics_time;
    private TextView tv_trafficstatistics_today_g3;//今日
    private TextView tv_trafficstatistics_today_wifi;
    private TextView tv_trafficstatistics_week_g3;//本周
    private TextView tv_trafficstatistics_week_wifi;
    private TextView tv_trafficstatistics_month_g3;//本月
    private TextView tv_trafficstatistics_month_wifi;
    private TextView tv_trafficstatistics_lastmonth_g3;//上月
    private TextView tv_trafficstatistics_lastmonth_wifi;
    private TextView tv_trafficstatistics_conninfo;//显示剩余流量
    private TextView tv_trafficstatistics_smsinfo;//显示剩余短信数量
    private TextView tv_trafficstatistics_phoneinfo;//显示剩余通话时间
    private Button bt_trafficstatistics_conn;//设置流量
    private Button bt_trafficstatistics_sms;//设置短信
    private Button bt_trafficstatistics_phone;//设置通话时间
    private Button bt_trafficstatistics_net;
    private Button bt_trafficstatistics_stopalarm;
    private InputDialog dialog;
    private int send;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trafficstatistics);
        mContext = TrafficStatisticsActivity.this;
        am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        //专门用于存移动数据类的东西 在这里第一次使用
        sp = mContext.getSharedPreferences("data", Context.MODE_PRIVATE);
        calendar = Calendar.getInstance();

        tv_trafficstatistics_time = (TextView) this.findViewById(R.id.tv_trafficstatistics_time);
        tv_trafficstatistics_time.setText(calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月1日至" + calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日          距月底还有" + (calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH)) + "天");

        tv_trafficstatistics_today_g3 = (TextView) this.findViewById(R.id.tv_trafficstatistics_today_g3);
        tv_trafficstatistics_today_wifi = (TextView) this.findViewById(R.id.tv_trafficstatistics_today_wifi);
        tv_trafficstatistics_week_g3 = (TextView) this.findViewById(R.id.tv_trafficstatistics_week_g3);
        tv_trafficstatistics_week_wifi = (TextView) this.findViewById(R.id.tv_trafficstatistics_week_wifi);
        tv_trafficstatistics_month_g3 = (TextView) this.findViewById(R.id.tv_trafficstatistics_month_g3);
        tv_trafficstatistics_month_wifi = (TextView) this.findViewById(R.id.tv_trafficstatistics_month_wifi);
        tv_trafficstatistics_lastmonth_g3 = (TextView) this.findViewById(R.id.tv_trafficstatistics_lastmonth_g3);
        tv_trafficstatistics_lastmonth_wifi = (TextView) this.findViewById(R.id.tv_trafficstatistics_lastmonth_wifi);

        tv_trafficstatistics_conninfo = (TextView) this.findViewById(R.id.bt_trafficstatistics_conninfo);
        tv_trafficstatistics_smsinfo = (TextView) this.findViewById(R.id.bt_trafficstatistics_smsinfo);
        tv_trafficstatistics_phoneinfo = (TextView) this.findViewById(R.id.bt_trafficstatistics_phoneinfo);
        tv_trafficstatistics_conninfo.setText("当月免费流量" + sp.getInt("traffic", 0) + "MB，剩余" + surplusTraffic() + "MB");
        tv_trafficstatistics_smsinfo.setText("当月免费短信" + sp.getInt("Sms", 0) + "条，剩余" + surplusSms() + "条");
        tv_trafficstatistics_phoneinfo.setText("当月免费通话时间" + sp.getInt("air_time", 0) + "分钟，剩余" + surplusAirtime() + "分钟");

        bt_trafficstatistics_conn = (Button) this.findViewById(R.id.bt_trafficstatistics_conn);
        bt_trafficstatistics_sms = (Button) this.findViewById(R.id.bt_trafficstatistics_sms);
        bt_trafficstatistics_phone = (Button) this.findViewById(R.id.bt_trafficstatistics_phone);
        bt_trafficstatistics_net = (Button) this.findViewById(R.id.bt_trafficstatistics_net);
        bt_trafficstatistics_stopalarm = (Button) this.findViewById(R.id.bt_trafficstatistics_stopalarm);

        bt_trafficstatistics_conn.setOnClickListener(this);//设置
        bt_trafficstatistics_sms.setOnClickListener(this);//设置
        bt_trafficstatistics_phone.setOnClickListener(this);//设置

        bt_trafficstatistics_net.setOnClickListener(this);//联网设置
        bt_trafficstatistics_stopalarm.setOnClickListener(this);
//		saveAllData();
        fromTheEndOfTheMonth();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_trafficstatistics_conn: {//设置
                // test
                Log.d("TrafficStatisticsActivity", "onClick()");
                startActivity(new Intent(this, CameraActivity.class));


                /*int traffic = sp.getInt("traffic", 0);
                dialog = new InputDialog(mContext, traffic, new android.os.Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        doMessage("traffic", msg);
                        return false;
                    }
                });
                dialog.show();*/
                break;
            }
            case R.id.bt_trafficstatistics_sms: {//设置
                int sms = sp.getInt("Sms", 0);
                dialog = new InputDialog(mContext, sms, new android.os.Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        doMessage("Sms", msg);
                        return false;
                    }
                });
                dialog.show();
                break;
            }
            case R.id.bt_trafficstatistics_phone: {//设置
                int air_time = sp.getInt("air_time", 0);
                dialog = new InputDialog(mContext, air_time, new android.os.Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        doMessage("air_time", msg);
                        return false;
                    }
                });
                dialog.show();
                break;
            }
            case R.id.bt_trafficstatistics_net: {//联网设置
                intent = new Intent(mContext, NetworkSettingsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.bt_trafficstatistics_stopalarm: {
                if (pi != null) {
                    am.cancel(pi);
                    pi = null;
                    MyToast.show("闹钟已停止");
                }
                break;
            }
        }

    }

    private void doMessage(String name, Message msg) {//按“确定”后的操作
        if (msg.obj != null) {//air_time 通话时间
            String airtime = (String) msg.obj;
            //除了数字，其他内容都不能输入，所以不用再判断了
//			boolean flag = true;
//			char[] ch = airtime.toCharArray();
//			for(int i=0;i<ch.length;i++){
//				if(!(ch[i]>='0' && ch[i]<='9')){
//					MyUtils.showToast(mContext, "不是一个正整数，请重新输入", 1);
//					flag = false;
//					break;
//				}
//			}
            if (dialog != null) {
                dialog.dismiss();
            }
            Editor editor = sp.edit();
            editor.putInt(name, Integer.parseInt(airtime));
            editor.commit();
            if ("traffic".equals(name)) {
                tv_trafficstatistics_conninfo.setText("当月免费流量" + sp.getInt("traffic", 0) + "MB，剩余" + surplusTraffic() + "MB");
            } else if ("Sms".equals(name)) {
                tv_trafficstatistics_smsinfo.setText("当月免费短信" + sp.getInt("Sms", 0) + "条，剩余" + surplusSms() + "条");
            } else if ("air_time".equals(name)) {
                tv_trafficstatistics_phoneinfo.setText("当月免费通话时间" + sp.getInt("air_time", 0) + "分钟，剩余" + surplusAirtime() + "分钟");
            }
        }
    }

    public void saveAllData() {
        //在23：59：50之前记录一下数据流量，把它们保存进文件中（所有应用所用的流量---上传下载）
        //能计算出所有应用当日所用的流量
        //SystemClock.elapsedRealtime();手机重启后到现在的间隔时间(ms)
        //am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,SystemClock.elapsedRealtime(),6*1000,pi);
//		pi = PendingIntent.getBroadcast(mContext, 0, intent, 0);
//		pi = PendingIntent.getActivity(mContext, 0, intent, 0);
        //设置闹钟从当前时间开始，每隔5s执行一次PendingIntent对象pi，注意第一个参数与第二个参数的关系
        intent = new Intent("com.aowin.mobilesafe.traffic");
        pi = PendingIntent.getService(this, 0, intent, 0);
        long intervalTime = 23 * 3600 * 1000 + 59 * 60 * 1000 + 50 * 1000;
//		am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(),5*1000,pi); 
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }

    //剩余流量
    public String surplusTraffic() {//surplus:adj. 剩余的
        int traffic = sp.getInt("traffic", 0);
        return "";
    }

    //剩余短信
    public String surplusSms() {//对手机卡进行判断，如果是移动的，那么发给10086是免费的，不用计算在其中
        String result = null;
        int sms = sp.getInt("Sms", 0);
        if (sms == 0) {
            result = "0";
        } else {
            Uri uri = Uri.parse("content://Sms");
            ContentResolver cr = mContext.getContentResolver();
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null;
            }
            String number = tm.getLine1Number();
            int count = 0;
            if (number != null) {
                if (number.startsWith("+86")) {
                    number = number.substring(3, number.length());
                }
                String address = PhoneNumberAddressQueryUtils.phoneNumberAddressQuery(number);
                if (address.contains("电信")) {
                    count = getAMonthSmsCounts(uri, cr, "10000");
                } else if (address.contains("移动")) {
                    count = getAMonthSmsCounts(uri, cr, "10086");
                } else if (address.contains("联通")) {
                    count = getAMonthSmsCounts(uri, cr, "10010");
                } else {//查不到归属地时需要处理
                    count = getAMonthSmsCounts(uri, cr, "10010");//我知道自己的卡是联通的，所以这样做了
                }
            } else {//得不到号码时需要处理
                count = getAMonthSmsCounts(uri, cr, null);
            }
            result = String.valueOf(sms - count);
        }
//		cr.query(uri, projection, selection, selectionArgs, sortOrder)
//		Phone mPhone = PhoneFactory.getDefaultPhone();
//		String rawNumber = mPhone.getLine1Number();
//		String formattedNumber = PhoneNumberUtils.formatNumber(rawNumber);
        return result;
    }

    //剩余通话时间
    public String surplusAirtime() {
        int air_time = sp.getInt("air_time", 0);
        return "";
    }

    public void fromTheEndOfTheMonth() {//距月底还有多少天
        int year = calendar.get(Calendar.YEAR);//年
        int month = calendar.get(Calendar.MONTH) + 1;//月
        int day = calendar.get(Calendar.DAY_OF_MONTH);//日
        int day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1;//星期几
        int day_of_week_in_month = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH);
        int day_of_year = calendar.get(Calendar.DAY_OF_YEAR);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);//时
        int minute = calendar.get(Calendar.MINUTE);//分
        int seconds = calendar.get(Calendar.SECOND); //秒
        int milliSecond = calendar.get(Calendar.MILLISECOND);
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);//当月最大天数
        //04-25 20:59:46.255: I/System.out(23752): 2015/4/25 20:59:46---30
        System.out.println(year + "/" + month + "/" + day + " " + day_of_week + " " + day_of_week_in_month + " " + day_of_year + " " + hour + ":" + minute + ":" + seconds + "---" + days);
    }


//	Phone mPhone = PhoneFactory.getDefaultPhone();
//	String rawNumber = mPhone.getLine1Number();  // may be null or empty
//  String formattedNumber = null;
//  if (!TextUtils.isEmpty(rawNumber)) {
//      formattedNumber = PhoneNumberUtils.formatNumber(rawNumber);
//  }

    private int getAMonthSmsCounts(final Uri uri, final ContentResolver cr, final String number) {
        try {//如果这块内容放在子线程中的话，send的值要用handler发送出去，到主线程中去赋值
            send = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            StringBuffer sb = new StringBuffer();
            int year = calendar.get(Calendar.YEAR);//年
            int month = calendar.get(Calendar.MONTH) + 1;//月
            int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

            //2015/01/01 00:00:00:000必须要用这种时间格式转化成long
            if (month < 10) {
                sb.append(year).append("0").append(month).append("01").append("00").append("00").append("00").append("000");
            } else {
                sb.append(year).append(month).append("01").append("00").append("00").append("00").append("000");
            }
            Date date = sdf.parse(sb.toString());
            long start = date.getTime();

            sb = new StringBuffer();
            if (month < 10) {
                sb.append(year).append("0").append(month).append(days).append("23").append("59").append("59").append("999");
            } else {
                sb.append(year).append(month).append(days).append("23").append("59").append("59").append("999");
            }
            date = sdf.parse(sb.toString());
            long end = date.getTime();
            Cursor cursor = cr.query(uri, new String[]{"address", "date"}, "type=?", new String[]{String.valueOf(2)}, null);
            while (cursor.moveToNext()) {
                String address = cursor.getString(cursor.getColumnIndex("address"));
                long dt = cursor.getLong(cursor.getColumnIndex("date"));
                if ("10000".equals(number)) {
                    if (!"10000".equals(address) && dt >= start && dt <= end) {
                        send++;
                    }
                } else if ("10086".equals(number)) {
                    if (!"10086".equals(address) && dt >= start && dt <= end) {
                        send++;
                    }
                } else if ("10010".equals(number)) {
                    if (!"10010".equals(address) && dt >= start && dt <= end) {
                        send++;
                    }
                } else if (null == number) {
                    if (dt >= start && dt <= end) {
                        send++;
                    }
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return send;

    }


}
