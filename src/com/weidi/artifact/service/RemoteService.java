package com.weidi.artifact.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.v4.app.NotificationCompat;

import com.weidi.artifact.R;
import com.weidi.artifact.activity.MainActivity;
//import com.weidi.daemon.IDaemonServiceAidlInterface;

public class RemoteService extends Service {

    private static final String TAG = "RemoteService";
    private static final int id = 0x0001;
    private Context mContext;
//    private DaemonService mDaemonService;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*if(mDaemonService == null){
            mDaemonService = new DaemonService();
        }*/
        mContext = getApplicationContext();

        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        Notification.Builder builder = new Notification.Builder(mContext)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setContentTitle("")
                .setContentInfo("")
                .setSmallIcon(R.drawable.app_icon)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent);

//        setForeground(true);
        Notification notification = builder.build();
        startForeground(id, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
//        mDaemonService = null;
        super.onDestroy();
    }

    /*private class DaemonService extends IDaemonServiceAidlInterface.Stub {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double
                aDouble, String aString) throws RemoteException {

        }
    }*/

    private ServiceConnection mLocalServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
//            mDaemonService = null;
            mLocalServiceConnection = null;

            Intent intent = new Intent(mContext, CoreService.class);
            startService(intent);
            /*bindService(
                    intent,
                    mLocalServiceConnection,
                    Context.BIND_AUTO_CREATE,
                    UserHandle.getUserId(Process.myUid()));*/
        }
    };

}
