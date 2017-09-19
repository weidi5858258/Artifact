package com.weidi.artifact.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.weidi.artifact.R;
import com.weidi.artifact.db.MyInfosSQLiteOpenHelper;
import com.weidi.utils.MyToast;

@SuppressLint("NewApi")
public class AdvancedToolsActivity extends Activity implements OnClickListener {
    private Context mContext;
    private WindowManager wm;
    private TextView advancedtools_tv_backuporrestoreinfos;
    private TextView advancedtools_tv_phonenumberaddressquery;
    private TextView advancedtools_tv_weather;
    private View view;
    private ProgressBar pb_backup_restore;
    private TextView tv_backup_restore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advancedtools);
        mContext = AdvancedToolsActivity.this;
        wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        advancedtools_tv_backuporrestoreinfos = (TextView) findViewById(R.id
                .advancedtools_tv_backuporrestoreinfos);
        advancedtools_tv_phonenumberaddressquery = (TextView) findViewById(R.id
				.advancedtools_tv_phonenumberaddressquery);
        advancedtools_tv_weather = (TextView) findViewById(R.id.advancedtools_tv_weather);
        advancedtools_tv_backuporrestoreinfos.setOnClickListener(this);
        advancedtools_tv_phonenumberaddressquery.setOnClickListener(this);
        advancedtools_tv_weather.setOnClickListener(this);
    }

    @SuppressLint("NewApi")
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            //备份或者恢复短信
            case R.id.advancedtools_tv_backuporrestoreinfos: {
                backupOrRestoreInfos();
                break;
            }
            //手机号码归属地查询
            case R.id.advancedtools_tv_phonenumberaddressquery: {
                intent = new Intent(mContext, PhoneNumberAddressQueryActivity.class);
                startActivity(intent);
                break;
            }
            //天气查询
            case R.id.advancedtools_tv_weather: {//做这块


                break;
            }
            //身份证查询
            case R.id.bt_backup_callrecords: {

                break;
            }
            //全国车辆交通违章查询接口
            case R.id.bt_restore_callrecords: {

                break;
            }
            //备份短信
            case R.id.bt_backup_smss: {
                pb_backup_restore.setVisibility(ProgressBar.VISIBLE);
                tv_backup_restore.setVisibility(TextView.VISIBLE);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SystemClock.sleep(5000);
                            final String result = backupSMSs(mContext);
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    pb_backup_restore.setVisibility(ProgressBar.INVISIBLE);
                                    tv_backup_restore.setVisibility(TextView.INVISIBLE);
                                    // MyUtils.showToast(mContext, result, 1);
                                    MyToast.show(result);
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            }
            //恢复短信
            case R.id.bt_restore_smss: {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //						restoreSMSs(mContext);//还没有测试
                    }
                }).start();
                break;
            }
            //关闭弹出的窗口
            case R.id.bt_cancel: {
                wm.removeView(view);
                break;
            }

        }
    }

    private void backupOrRestoreInfos() {
        view = View.inflate(mContext, R.layout.backup_restore_sms_view, null);
        Button bt_backup_callrecords = (Button) view.findViewById(R.id.bt_backup_callrecords);
        Button bt_restore_callrecords = (Button) view.findViewById(R.id.bt_restore_callrecords);
        Button bt_backup_smss = (Button) view.findViewById(R.id.bt_backup_smss);
        Button bt_restore_smss = (Button) view.findViewById(R.id.bt_restore_smss);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
        pb_backup_restore = (ProgressBar) view.findViewById(R.id.pb_backup_restore);
        tv_backup_restore = (TextView) view.findViewById(R.id.tv_backup_restore);
        bt_backup_callrecords.setOnClickListener(this);
        bt_restore_callrecords.setOnClickListener(this);
        bt_backup_smss.setOnClickListener(this);
        bt_restore_smss.setOnClickListener(this);
        bt_cancel.setOnClickListener(this);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = wm.getDefaultDisplay().getWidth() - 40;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.TOP + Gravity.LEFT;
        params.x = (int) (wm.getDefaultDisplay().getWidth() / 2 - params.width / 2);
        params.y = 350;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        view.setOnTouchListener(new OnTouchListener() {
            int startX;
            int startY;
            int newX;
            int newY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        newX = (int) event.getRawX();
                        newY = (int) event.getRawY();
                        int dx = newX - startX;
                        int dy = newY - startY;
                        params.x += dx;
                        params.y += dy;
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > (wm.getDefaultDisplay().getWidth() - view.getWidth())) {
                            params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
                        }
                        if (params.y > (wm.getDefaultDisplay().getHeight() - view.getHeight())) {
                            params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
                        }
                        wm.updateViewLayout(view, params);
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    }
                }
                return true;
            }
        });
        wm.addView(view, params);
    }

    //体验还可以 以后有业务需要可以参考这里
    private void myToast() {
		/* 用到下面的内容就加上点击事件
		 * case R.id.bt_querytools_sure:{
				break;
			}
			case R.id.bt_querytools_cancel:{
				wm.removeView(view);
				break;
			}
		 */
        view = View.inflate(mContext, R.layout.querytools_view, null);
        Button bt_querytools_sure = (Button) view.findViewById(R.id.bt_querytools_sure);
        Button bt_querytools_cancel = (Button) view.findViewById(R.id.bt_querytools_cancel);
        bt_querytools_sure.setOnClickListener(this);
        bt_querytools_cancel.setOnClickListener(this);
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        //窗口宽高
        params.width = (int) (wm.getDefaultDisplay().getWidth() / 3 * 2);
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //位于左上角
        params.gravity = Gravity.TOP + Gravity.LEFT;
        //相对于左上角的起始位置
        params.x = (int) (wm.getDefaultDisplay().getWidth() / 2 - params.width / 2);
        params.y = (int) (wm.getDefaultDisplay().getHeight() / 2 - params.height / 2);

        //		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|//不需要指定
        //				       WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING;
        // 这种模式在当前页面中弹出的窗口有比较好的体验，不能执行其他的操作
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        params.alpha = 50;//透明度
        view.setOnTouchListener(new OnTouchListener() {
            int startX;
            int startY;
            int newX;
            int newY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        break;
                    }
                    case MotionEvent.ACTION_DOWN: {
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        newX = (int) event.getRawX();
                        newY = (int) event.getRawY();
                        int dx = newX - startX;
                        int dy = newY - startY;
                        params.x += dx;
                        params.y += dy;
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > (wm.getDefaultDisplay().getWidth() - view.getWidth())) {
                            params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
                        }
                        if (params.y > (wm.getDefaultDisplay().getHeight() - view.getHeight())) {
                            params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
                        }
                        wm.updateViewLayout(view, params);
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    }
                }
                return true;
            }
        });
        wm.addView(view, params);
    }

    //备份短信
    public String backupSMSs(Context context) throws Exception {
        String result = null;
        Cursor cursor = null;
        MyInfosSQLiteOpenHelper myInfos = new MyInfosSQLiteOpenHelper(context);
        SQLiteDatabase db = myInfos.getWritableDatabase();
        try {
            db.beginTransaction();
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.parse("content://sms");
            cursor = cr.query(uri, new String[]{"address", "date", "read", "type", "body"}, null, null, null);
            db.execSQL("drop table Sms");
            db.execSQL("CREATE TABLE sms (_id integer primary key autoincrement,address varchar not null,date varchar not null,read int not null,type int not null,body varchar not null);");
            while (cursor.moveToNext()) {
                String address = cursor.getString(cursor.getColumnIndex("address"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String read = cursor.getString(cursor.getColumnIndex("read"));
                String type = cursor.getString(cursor.getColumnIndex("type"));
                String body = cursor.getString(cursor.getColumnIndex("body"));
                String sql = "insert into sms (address,date,read,type,body) values (?,?,?,?,?);";
                db.execSQL(sql, new String[]{address, date, read, type, body});
            }
            db.setTransactionSuccessful();
            result = "备份成功！";
        } catch (Exception e) {
            result = "备份失败！";
        } finally {
            db.endTransaction();
            cursor.close();
            db.close();
        }
        return result;
    }

    //恢复短信
    public void restoreSMSs(Context context) throws Exception {
        MyInfosSQLiteOpenHelper myInfos = new MyInfosSQLiteOpenHelper(context);
        SQLiteDatabase db = myInfos.getReadableDatabase();
        ContentResolver cr = context.getContentResolver();
        String sql = "select address,date,read,type,body from sms";
        Cursor cursor = db.query(sql, null, null, null, null, null, null);
        Uri uri = Uri.parse("content://sms");
        ContentValues cv = null;
        while (cursor.moveToNext()) {
            String address = cursor.getString(cursor.getColumnIndex("address"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String read = cursor.getString(cursor.getColumnIndex("read"));
            String type = cursor.getString(cursor.getColumnIndex("type"));
            String body = cursor.getString(cursor.getColumnIndex("body"));
            cv = new ContentValues();
            cv.put("address", address);
            cv.put("date", date);
            cv.put("read", read);
            cv.put("type", type);
            cv.put("body", body);
            cr.insert(uri, cv);
        }
        cursor.close();
        db.close();
    }


}
