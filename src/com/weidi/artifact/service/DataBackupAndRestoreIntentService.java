package com.weidi.artifact.service;

import android.app.IntentService;
import android.content.Intent;


public class DataBackupAndRestoreIntentService extends IntentService {

    public DataBackupAndRestoreIntentService() {//必须要有一个无参的构造方法
        super("DataBackupAndRestoreIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent == null){
            return;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
