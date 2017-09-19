package com.weidi.artifact.constant;

import com.weidi.artifact.service.AlarmClockService;

/**
 * Created by root on 16-7-30.
 */

public interface Constant {

    int HIDE = 0;
    int POPBACKSTACK = 1;
    int POPBACKSTACKALL = 2;
    int SCANNIN_GREQUEST_CODE = 1;
    int PASSWORDERRORCOUNT = 5;
    int FIXEDTHREADPOOLCOUNT = 15;// 长久能够运行的线程数量
    int ACTION_REQUEST_ENABLE = 100;
    int INPUT_BT_ADDRESS_REQUESTCODE = 1001;

    int CORESERVICE = 1000;
    int INPUT_BT_ADDRESS_RESULTCODE = 1002;
    int GOHOME = 1003;
    int SCREEN_ON = 1004;
    int SCREEN_OFF = 1005;
    int PASSWORD_CORRECT = 1006;
    int APPSLOCKSERVICE = 1007;
    int PERIODICALSERIALKILLERSERVICE = 1008;
    int UPDATEAPPSLOCKLIST = 1009;
    int SHOWGESTUREVIEW = 1010;
    int HIDEGESTUREVIEW = 1011;
    int ILLEGALUNLOCK_ENTER = 1012;
    int ILLEGALUNLOCK_EXIT = 1013;
    int STOP_ALARMCLOCKSERVICE = 1014;
    int ALARMCLOCKSERVICE_IS_STOPPED = 1015;
    int UNLOCK_MY_PHONE_TAG = 1016;
    int TRANSPORT_TIME = 1017;
    int TRANSPORT_TIME_COMPLETE = 1018;
    int BEKILLEDPROCESSNAME = 1019;
    int CHANGEAPP = 1020;
    int MAINACTIVITY = 1021;

    String DB_NAME = "artifact.db";
    String SHAREDPREFERENCES = "record_value";
    String PACKAGE_NAME = "com.weidi.artifact";
    String REMOTE_PACKAGE = "com.weidi.callsystemmethod";
    String REMOTE_PACKAGE_CLASS = "com.weidi.callsystemmethod.CallSystemMethodService";
    String APP_CONFIG = "config";
    String REQUESTCODE = "requtestCode";
    String APP_NAME = "app_name";
    String APP_PACKAGE_NAME = "package_name";
    String LAUNCHER = "com.cyanogenmod.trebuchet";
    String STOPSELF = "android.intent.action.STOPSELF";
    String SAFE_EXIT = "Safe_Exit";
    String SCREENOFFTIME = "ScreenOffTime";
    String APPSLOCK_PASSWORD = "appslock_password";
    String USB_DEBUG = "usb_debug";
    String SDCARD_USBDISK = "sdcard1_usbdisk0";
    String INCALLSCREEN = "com.android.phone.InCallScreen";
    String DIFFERTIME = "DifferTime";
    String ALARMTIME = "AlarmTime";
    String LOCK_MY_PHONE = "weidi5858258@@@lock";// @@@@@weidi5858258@@@@@
    String UNLOCK_MY_PHONE = "weidi5858258@@@unlock";// ######weidi5858258######
    String UNLOCK_MY_PHONE_COMPLETE = "weidi5858258@@@unlockComplete";// $$$$$$$weidi5858258$$$$$$$
    String SHUTDOWN = "weidi5858258@@@shutdown";// %%%%%%%%weidi5858258%%%%%%%%
    String REBOOT = "weidi5858258@@@reboot";// *********weidi5858258*********
    String TAKEPICTURE = "weidi5858258@@@takePicture";
    String SHOWMYINFO = "weidi5858258@@@showMyInfo";
    String SIM = "sim";
    String CLASS_CORESERVICE = "com.weidi.artifact.service.CoreService";
    String CLASS_PERIODICALSERIALKILLERSERVICE =
            "com.weidi.artifact.service.PeriodicalSerialKillerService";
    String CLASS_APPSLOCKSERVICE = "com.weidi.artifact.service.AppsLockService";
    String POWERMANAGERSERVICE = "com.android.server.power.PowerManagerService";
    String PACKAGEMANAGERSERVICE = "com.android.server.pm.PackageManagerService";
    String INPUTMANAGERSERVICE = "com.android.server.input.InputManagerService";
    String MOUNTSERVICE = "com.android.server.MountService";
    String INTERCEPTKEYCODE = "INTERCEPTKEYCODE";
    String ISINTERCEPT = "ISINTERCEPT";
    String ISINTERCEPTINSTALL = "ISINTERCEPTINSTALL";
    String ISINTERCEPTUNINSTALL = "ISINTERCEPTUNINSTALL";
    String SDCARD1 = "/storage/sdcard1";
    String USBDISK0 = "/storage/usbdisk0";
    String LEFTFRAGMENT = "LEFT";
    String RIGHTFRAGMENT = "RIGHT";
    // 短信标志
    String PDUS = "pdus";
    String STARTALARMCLOCK = "startAlarmClock";
    String REMOTEPACKAGENAME = "com.weidi.callsystemmethod";
    String REMOTESERVICENAME = "com.weidi.callsystemmethod.CallSystemMethodService";
    String HOSTADDRESS = "HostAddress";
    String SMS_URI = "content://sms";
    String PHONE_URI = "content://com.android.contacts/contacts";
    String RAW_CONTACTS_URI = "content://com.android.contacts/raw_contacts";
    String DATA_URI = "content://com.android.contacts/data";
    String MIMETYPES_URI = "content://com.android.contacts/data/mimetypes";

    String MAINFRAGMENT = "MainFragment";
    String SETTINGSFRAGMENT = "SettingsFragment";
    String APPSMANAGER = "AppsManagerFragment";
    String APPSOPERATIONDIALOGFRAGMENT = "AppsOperationDialogFragment";

}
