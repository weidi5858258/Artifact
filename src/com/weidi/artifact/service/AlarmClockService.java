package com.weidi.artifact.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.text.TextUtils;

import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.AlarmClockFragmentController;
import com.weidi.log.MLog;
import com.weidi.service.BaseService;
import com.weidi.threadpool.CustomRunnable;
import com.weidi.threadpool.ThreadPool;
import com.weidi.eventbus.EventBusUtils;
import com.weidi.utils.MyToast;

import java.util.ArrayList;

public class AlarmClockService extends BaseService {

    private static final String TAG = "AlarmClockService";
    private AlarmManager mAlarmManager;
    private PendingIntent mPendingIntent;
    private long mDifferTime;
    private MediaPlayer mMediaPlayer;
    private ArrayList<String> mHasPlayedMusicPath = new ArrayList<String>();
    private String mCurrentPlayMusicPath;
    private boolean mIsPlaying;
    private boolean mHasExit;
    private Object obj = new Object();
    private String[] mMusicPath = new String[]{
            "/storage/sdcard1/BaiduNetdisk/music/王菲 - 容易受伤的女人.mp3",
            "/storage/sdcard1/BaiduNetdisk/music/谭咏麟 - 水中花.mp3",
            "/storage/sdcard1/BaiduNetdisk/music/庄心妍 - 你爱的人到底有几个.mp3",
            "/storage/sdcard1/BaiduNetdisk/music/范思威 - 来生不分手.mp3",
            "/storage/sdcard1/BaiduNetdisk/music/笑看风云（郑少秋）.mp3",
            "/storage/sdcard1/BaiduNetdisk/music/梁朝伟 - 一天一点爱恋.mp3",
            "/storage/sdcard1/BaiduNetdisk/music/徐誉滕 - 做我老婆好不好.mp3",
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 在服务中开启子线程，如果服务停止了，但是进程还在，子线程是否会停止？
    @Override
    public void onCreate() {
        super.onCreate();
        MLog.d(TAG, "onCreate()");
        EventBusUtils.register(this);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmClockService.class);
        intent.putExtra(Constant.STARTALARMCLOCK, Constant.STARTALARMCLOCK);
        mPendingIntent = PendingIntent.getService(
                this,
                0,
                intent,
                0);// PendingIntent.FLAG_UPDATE_CURRENT
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MLog.d(TAG, "onStartCommand():intent = " + intent);
        if (intent == null) {
            return START_STICKY_COMPATIBILITY;
        }
        mDifferTime = intent.getLongExtra(Constant.DIFFERTIME, 0);
        if (mDifferTime > 0) {
            MLog.d(TAG, "onStartCommand():mDifferTime = " + mDifferTime);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, mDifferTime, mPendingIntent);
            return super.onStartCommand(intent, flags, startId);
        }

        String startAlarmClock = intent.getStringExtra(Constant.STARTALARMCLOCK);
        if (Constant.STARTALARMCLOCK.equals(startAlarmClock)) {
            MLog.d(TAG, "onStartCommand():startAlarmClock = " + startAlarmClock);
            startPlayback();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        MLog.d(TAG, "onDestroy()");
        if (mMediaPlayer != null) {
            try {
                mMediaPlayer.setOnPreparedListener(null);
                mMediaPlayer.setOnCompletionListener(null);
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            } catch (Exception e) {
                mMediaPlayer = null;
            }
        }
        if (mHasPlayedMusicPath != null) {
            mHasPlayedMusicPath.clear();
            mHasPlayedMusicPath = null;
        }
        if (mIsPlaying) {
            synchronized (obj) {
                mIsPlaying = false;
                obj.notifyAll();
            }
        }
        mHasExit = true;
        mDifferTime = 0;
        if (mAlarmManager != null && mPendingIntent != null) {
            mAlarmManager.cancel(mPendingIntent);
            mPendingIntent.cancel();
            mPendingIntent = null;
        }
        EventBusUtils.unregister(this);
        super.onDestroy();
    }

    public Object onEvent(int what, Object object) {
        switch (what) {
            case Constant.STOP_ALARMCLOCKSERVICE:
                onDestroy_();
                break;

            default:
        }
        return what;
    }

    private void onDestroy_() {
        EventBusUtils.postSync(
                AlarmClockFragmentController.class, Constant.ALARMCLOCKSERVICE_IS_STOPPED, null);
        stopSelf();
    }

    private void playMusic() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(mCurrentPlayMusicPath);
            MLog.d(TAG, "setDataSource(): " + mCurrentPlayMusicPath);
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    MLog.d(TAG, "onPrepared()");
                    mMediaPlayer.start();
                    if (mHasPlayedMusicPath != null) {
                        mHasPlayedMusicPath.add(mCurrentPlayMusicPath);
                    }
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    MLog.d(TAG, "onCompletion()");
                    if (mIsPlaying) {
                        synchronized (obj) {
                            mIsPlaying = false;
                            obj.notifyAll();
                        }
                    }
                    if (mHasPlayedMusicPath != null
                            && mHasPlayedMusicPath.size() == mMusicPath.length) {
                        MLog.d(TAG, "onDestroy_()");
                        onDestroy_();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            onDestroy_();
        }
    }

    private void startPlayback() {
        MLog.d(TAG, "startPlayback():音乐响起");
        // EventBusUtils.post(Constant.TRANSPORT_TIME_COMPLETE, null);
        MyToast.show("音乐响起");

        final CustomRunnable mCustomRunnable = new CustomRunnable();
        mCustomRunnable.setCallBack(
                new CustomRunnable.CallBack() {

                    @Override
                    public void runBefore() {

                    }

                    @Override
                    public Object running() {
                        int length = mMusicPath.length;
                        for (int i = 0; i < length; i++) {
                            if (!mHasExit) {
                                mCurrentPlayMusicPath = mMusicPath[i];
                                if (!TextUtils.isEmpty(mCurrentPlayMusicPath)
                                        && mCustomRunnable != null
                                        && mHasPlayedMusicPath != null
                                        && !mHasPlayedMusicPath.contains
                                        (mCurrentPlayMusicPath)) {
                                    mIsPlaying = true;
                                    if (!mHasExit) {
                                        mCustomRunnable.publishProgress(null);
                                    }
                                    synchronized (obj) {
                                        while (mIsPlaying && !mHasExit) {
                                            try {
                                                obj.wait();
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        return null;
                    }

                    @Override
                    public void onProgressUpdate(Object object) {
                        playMusic();
                    }

                    @Override
                    public void runAfter(Object object) {

                    }

                    @Override
                    public void runError() {

                    }

                });
        ThreadPool.getFixedThreadPool(Constant.FIXEDTHREADPOOLCOUNT).execute(mCustomRunnable);
    }

    private class AlarmClockCountDownTimer extends CountDownTimer {

        public AlarmClockCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            startPlayback();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            long remainingTime = millisUntilFinished / 1000;
            EventBusUtils.postSync(
                    AlarmClockFragmentController.class,
                    Constant.TRANSPORT_TIME,
                    new Object[]{"倒计时: " + remainingTime + " 秒"});
        }
    }

}
