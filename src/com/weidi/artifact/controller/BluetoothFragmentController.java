package com.weidi.artifact.controller;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
//import android.bluetooth.BluetoothUuid;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelUuid;
import android.os.RemoteException;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.weidi.artifact.R;
import com.weidi.artifact.activity.MainActivity;
import com.weidi.artifact.adapter.DevicesAdapter;
import com.weidi.artifact.application.MyApplication;
import com.weidi.artifact.constant.Constant;
import com.weidi.artifact.controller.basecontroller.BaseFragmentController;
import com.weidi.artifact.fragment.BluetoothFragment;
import com.weidi.artifact.listener.OnResultListener;
import com.weidi.artifact.modle.BTDevice;
//import com.weidi.callsystemmethod.ICallSystemMethod;
import com.weidi.customadapter.listener.OnItemClickListener;
import com.weidi.dbutil.SimpleDao;
import com.weidi.log.Log;
import com.weidi.threadpool.ThreadPool;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;
import static com.weidi.artifact.constant.Constant.FIXEDTHREADPOOLCOUNT;

/**
 * Created by root on 17-1-13.
 */

public class BluetoothFragmentController extends BaseFragmentController {

    private static final String TAG = "BluetoothFragmentController";
    private static final boolean DEBUG = false;
    private BluetoothFragment mBluetoothFragment;

    private DevicesAdapter mDevicesAdapter;
    private ArrayList<BluetoothDevice> btList;
    private BluetoothDevice mBluetoothDevice;
    private MHandler mMHandler;
//    private ICallSystemMethod mICallSystemMethod;

    private static int canSaveMyMsgType = 0;
    private static int canSaveOtherMsgType = 0;

    public BluetoothFragmentController(Fragment fragment) {
        super(fragment.getActivity());
        mBluetoothFragment = (BluetoothFragment) fragment;
//        mICallSystemMethod = ((MyApplication) mContext.getApplicationContext()).getSystemCall();
        mMHandler = new MHandler(this, Looper.getMainLooper());
        BTController.getInstance().setContext(mContext);
    }

    @Override
    public void beforeInitView() {
        if (DEBUG) Log.d(TAG, "beforeInitView()");
    }

    public void afterInitView(LayoutInflater inflater,
                              ViewGroup container,
                              Bundle savedInstanceState) {
        if (DEBUG) Log.d(TAG, "afterInitView():savedInstanceState = " + savedInstanceState);
        if (savedInstanceState == null) {
            init();
        }
    }

    @Override
    public void onResume() {
        if (DEBUG) Log.d(TAG, "onResume()");
        ((MainActivity) mBluetoothFragment.getActivity()).title.setText("蓝牙");
    }

    @Override
    public void onPause() {
        if (DEBUG) Log.d(TAG, "onPause()");
    }

    @Override
    public void onStop() {
        if (DEBUG) Log.d(TAG, "onStop()");
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.ACTION_REQUEST_ENABLE && resultCode == -1) {
            inin1();
        } else {
            mBluetoothFragment.onBackPressed();
        }
    }

    public void onClick(View view) {
        if (!BTController.getInstance().isOpenedBluetooth()) {
            showInfo("请先开启蓝牙");
            return;
        }
        //        if (!mBluetoothFragment.as_service_btn.isClickable()
        //                && view.getId() != R.id.search_device_btn
        //                && view.getId() != R.id.be_searched_btn
        //                && view.getId() != R.id.reset_btn
        //                && view.getId() != R.id.chat_btn) {
        //            showInfo("本机已作为服务端,除了\n" +
        //                    "\"主查\"或者\"被查\"或者\"重置\"或者\"聊天\"\n" +
        //                    "不能再进行其他操作");
        //            return;
        //        }
        //        if (!mBluetoothFragment.connect_btn.isClickable()
        //                && (view.getId() == R.id.as_service_btn
        //                || view.getId() == R.id.search_device_btn   // 这个还不能确定能不能操作
        //                || view.getId() == R.id.be_searched_btn)) { // 这个还不能确定能不能操作
        //            showInfo("请\"重置\"后再操作");
        //            return;
        //        }
        //        if (mBluetoothDevice != null
        //                && mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING
        //                && (view.getId() == R.id.as_service_btn
        //                || view.getId() == R.id.search_device_btn
        //                || view.getId() == R.id.be_searched_btn)) {
        //            showInfo("请稍等,正在配对中...");
        //            return;
        //        }
        switch (view.getId()) {
            case R.id.as_service_btn:
                //                reset();
                showInfo("本机已作为服务端\n正在等待客户端的连接...");
                setServerButtonDisable();

                //                if (((BTApplication) mContext.getApplicationContext())
                // .getConnectionType() ==
                //                        Constant.CLIENT) {
                //                    ((BTApplication) mContext.getApplicationContext())
                //                            .setConnectionType(Constant.CS);
                //                } else {
                //                    ((BTApplication) mContext.getApplicationContext())
                //                            .setConnectionType(Constant.SERVER);
                //                }
                //                BTServer.getInstance().setIRemoteConnection
                // (mServerIRemoteConnection);
                //                //                BTClient.getInstance()
                // .setRemoteBluetoothSocket(null);
                //                ThreadPool.getFixedThreadPool(FIXEDTHREADPOOLCOUNT).execute(new
                // Runnable() {
                //                    @Override
                //                    public void run() {
                //                        BTServer.getInstance().accept();
                //                    }
                //                });
                break;
            case R.id.search_device_btn:
                setSearchButtonEnable(false);
                mDevicesAdapter.clear();
                BTController.getInstance().scanDevice();
                break;
            case R.id.be_searched_btn:
                // 在300s内能被其他手机发现
                Intent discoverableIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
                mBluetoothFragment.startActivity(discoverableIntent);
                break;
            case R.id.cancel_search_btn:
                BTController.getInstance().cancelScanDevice();
                break;
            case R.id.reset_btn:
                reset();
                break;
            case R.id.disconnect_btn:
                //                if (((BTApplication) mContext.getApplicationContext())
                // .getConnectionType()
                //                        == Constant.CLIENT
                //                        && BTClient.getInstance().isConnected()) {
                //                    ThreadPool.getCachedThreadPool().execute(new Runnable() {
                //                        @Override
                //                        public void run() {
                //                            BTClient.getInstance().disConnect();
                //                        }
                //                    });
                //                } else if (((BTApplication) mContext.getApplicationContext())
                // .getConnectionType()
                //                        == Constant.SERVER
                //                        && BTServer.getInstance().getBtSocketList() != null
                //                        && BTServer.getInstance().getBtSocketList().size() > 0) {
                //
                //                } else {
                //                    showInfo("没有设备连接不需要此操作");
                //                }
                break;
            case R.id.pair_btn:
                if (mBluetoothDevice != null) {
                    if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                        showInfo("已经配对成功不需要再次配对");
                    } else if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {

                    } else {
                        ThreadPool.getCachedThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                if (!BTController.getInstance().createBond(mBluetoothDevice)) {
                                    mMHandler.sendEmptyMessage(3);
                                }
                            }
                        });
                    }
                } else {
                    showInfo("\"查找\"到设备后\n并选中一个设备再进行操作");
                }
                break;
            case R.id.disconnect_pair_btn:
                if (mBluetoothDevice != null) {
                    if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                        BTController.getInstance().removeBond(mBluetoothDevice);
                    } else {
                        showInfo("跟远程设备\n配对成功后再进行操作");
                    }
                } else {
                    showInfo("\"查找\"到设备后\n并选中一个设备再进行操作");
                }
                break;
            case R.id.cancel_pair_btn:
                if (mBluetoothDevice != null) {
                    if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                        showInfo("已经配对成功没法\"取消\"");
                    } else if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                        ThreadPool.getCachedThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                BTController.getInstance().cancelBondProcess(mBluetoothDevice);
                            }
                        });
                    } else {
                        showInfo("没有配对成功不需要\"取消\"");
                    }
                } else {
                    showInfo("\"查找\"到设备后\n并选中一个设备再进行操作");
                }
                break;
            case R.id.check_state_btn:
                if (mBluetoothDevice != null) {
                    mMHandler.sendEmptyMessage(3);
                } else {
                    showInfo("\"查找\"到设备后\n并选中一个设备再进行操作");
                }
                break;
            case R.id.connect_btn:
                if (mBluetoothDevice != null) {
                    //                    if (mBluetoothDevice.getBondState() == BluetoothDevice
                    // .BOND_BONDED) {
                    //
                    //                        if (((BTApplication) mContext.getApplicationContext())
                    //                                .getConnectionType() == Constant.SERVER) {
                    //                            ((BTApplication) mContext.getApplicationContext())
                    //                                    .setConnectionType(Constant.CS);
                    //                        } else {
                    //                            ((BTApplication) mContext.getApplicationContext())
                    //                                    .setConnectionType(Constant.CLIENT);
                    //                        }
                    //
                    //                        setConnectButtonDisable();
                    //                        showProgressBar();
                    //                        showInfo("正在连接...");
                    //                        //                        BTServer.getInstance()
                    // .setIRemoteConnection(null);
                    //                        BTClient.getInstance().setIRemoteConnection
                    // (mClientIRemoteConnection);
                    //                        ThreadPool.getCachedThreadPool().execute(new
                    // Runnable() {
                    //                            @Override
                    //                            public void run() {
                    //                                BTClient.getInstance().connect
                    // (mBluetoothDevice.getAddress());
                    //                            }
                    //                        });
                    //                    } else {
                    //                        showInfo("跟远程设备\n配对成功后再进行操作");
                    //                    }
                } else {
                    showInfo("\"查找\"到设备后\n并选中一个设备再进行操作");
                }
                break;

            case R.id.chat_btn:
                //                if (((BTApplication) mContext.getApplicationContext())
                // .getConnectionType()
                //                        == Constant.CLIENT
                //                        && BTClient.getInstance().isConnected()
                //                        || (((BTApplication) mContext.getApplicationContext())
                // .getConnectionType()
                //                        == Constant.SERVER
                //                        && BTServer.getInstance().getBtSocketList() != null
                //                        && BTServer.getInstance().getBtSocketList().size() > 0)) {
                //                } else {
                //                    showInfo("亲,先连接一个设备再说吧");
                //                }
                break;

            case R.id.input_btn:

                break;

            case R.id.cs_btn:
                if (mBluetoothDevice != null) {
                    if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                        showProgressBar();
                        ThreadPool.getCachedThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    } else {
                        showInfo("跟远程设备\n配对成功后再进行操作");
                    }
                } else {
                    showInfo("\"查找\"到设备后\n并选中一个设备再进行操作");
                }
                break;

            case R.id.test1_btn:
                try {
//                    int headset = mICallSystemMethod.getProfileConnectionState(BluetoothProfile
//                            .HEADSET);
//                    int a2dp = mICallSystemMethod.getProfileConnectionState(BluetoothProfile.A2DP);
//                    int health = mICallSystemMethod.getProfileConnectionState(BluetoothProfile
//                            .HEALTH);
//                    int input = mICallSystemMethod.getProfileConnectionState(BluetoothProfile
//                            .INPUT_DEVICE);
//                    int pan = mICallSystemMethod.getProfileConnectionState(BluetoothProfile.PAN);
//                    int pbap = mICallSystemMethod.getProfileConnectionState(BluetoothProfile.PBAP);
//                    Log.d(TAG, "headset = " + headset +
//                            " a2dp = " + a2dp +
//                            " health = " + health +
//                            " input = " + input +
//                            " pan = " + pan +
//                            " pbap = " + pbap);
                    if(mBluetoothDevice != null){
                        /*ParcelUuid[] localUuids = BTController.getInstance().getBluetoothAdapter().getUuids();
                        ParcelUuid[] uuids = mBluetoothDevice.getUuids();
                        String uuid = Arrays.toString(uuids);
                        if ((BluetoothUuid.isUuidPresent(localUuids, BluetoothUuid.HSP_AG) &&
                                BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.HSP)) ||
                                (BluetoothUuid.isUuidPresent(localUuids, BluetoothUuid.Handsfree_AG) &&
                                        BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.Handsfree))) {
                            Log.d(TAG, "Adding local HEADSET profile");
                        }

                        ParcelUuid[] SINK_UUIDS = {
                                BluetoothUuid.AudioSink,
                                BluetoothUuid.AdvAudioDist,
                        };
                        if (BluetoothUuid.containsAnyUuid(uuids, SINK_UUIDS)) {
                            Log.d(TAG, "Adding local A2DP profile");
                        }

                        if (BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.ObexObjectPush)) {
                            Log.d(TAG, "Adding local OPP profile");
                        }

                        if (BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.Hid)){
                            // Hid
                        }

                        if (BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.NAP)){
                            // Pan
                        }*/

                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.test2_btn:
                if (mBluetoothDevice != null) {

                }
                break;

            case R.id.test3_btn:
                if (mBluetoothDevice != null) {
                    try {
                        /*boolean result1 = mICallSystemMethod.connectHeadset(mBluetoothDevice);
                        boolean result2 = mICallSystemMethod.connectA2dp(mBluetoothDevice);
                        int state1 = mICallSystemMethod.getConnectionStateHeadset(mBluetoothDevice);
                        int state2 = mICallSystemMethod.getConnectionStateA2dp(mBluetoothDevice);
                        Log.d(TAG, "result1 = " + result1+
                        " result2 = "+result2+
                        " state1 = "+state1+
                        " state2 = "+state2);*/
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.test4_btn:
                if (mBluetoothDevice != null) {
                    /*try {
                        boolean result = mICallSystemMethod.connectA2dp(mBluetoothDevice);
                        int state = mICallSystemMethod.getConnectionStateA2dp(mBluetoothDevice);
                        Log.d(TAG, "resultA2dp = " + result);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }*/
                }
                break;

            default:
                break;
        }
    }

    private int position = 0;

    //    private boolean hasItemClick = false;

    public static int getCanSaveMyMsgType() {
        return canSaveMyMsgType;
    }

    public static int getCanSaveOtherMsgType() {
        return canSaveOtherMsgType;
    }

    private void init() {
        if (BTController.getInstance().isSupportedBluetooth()) {
            if (!BTController.getInstance().isOpenedBluetooth()) {
                mBluetoothFragment.bt_switch.setChecked(false);
                Intent enableIntent = new Intent(ACTION_REQUEST_ENABLE);
                mBluetoothFragment.startActivityForResult(enableIntent, Constant
                        .ACTION_REQUEST_ENABLE);
            } else {
                inin1();
            }
        } else {
            //
        }
    }

    private void inin1() {
        mBluetoothFragment.bt_address_tv.setText(
                BTController.getInstance().getLocalBluetoothAdress());
        // 注册蓝牙扫描广播
        BTController.getInstance().registerBluetoothReceiver(mContext);
        BTController.getInstance().setIBluetoothAction(mIBluetoothAction);
        btList = new ArrayList<BluetoothDevice>();
        //
        mDevicesAdapter = new DevicesAdapter(mContext, btList, R.layout.item_bluetooth_device);
        mDevicesAdapter.setOnItemClickListener(mOnItemClickListener);
        mBluetoothFragment.bluetooth_device_recyclerview.setLayoutManager(
                new LinearLayoutManager(mContext));
        //设置条目的间距
        //        mBluetoothFragment.bluetooth_device_recyclerview.addItemDecoration(new
        // SpaceItemDecoration(5));
        mBluetoothFragment.bluetooth_device_recyclerview.setAdapter(mDevicesAdapter);

        mBluetoothFragment.bt_switch.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            // 打开蓝牙
                            if (BTController.getInstance().openBluetooth()) {
                                showInfo("蓝牙已打开");
                            }
                        } else {
                            // 关闭蓝牙
                            reset();
                            BTController.getInstance().closeBluetooth();
                        }
                    }
                });
        if (BTController.getInstance().isOpenedBluetooth()) {
            mBluetoothFragment.bt_switch.setChecked(true);
            ThreadPool.getCachedThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    ArrayList<BTDevice> mDevicesList =
                            (ArrayList<BTDevice>) SimpleDao.getInstance().queryAll(BTDevice.class);
                    if (mDevicesList == null) {
                        return;
                    }
                    int devicesListSize = mDevicesList.size();
                    mDevicesAdapter.setData(mDevicesList, devicesListSize);
                    try {
                        Class<BluetoothDevice> cls = BluetoothDevice.class;
                        Constructor<BluetoothDevice> constructor =
                                cls.getDeclaredConstructor(String.class);
                        constructor.setAccessible(true);
                        for (int i = 0; i < devicesListSize; i++) {
                            BTDevice btDevice = mDevicesList.get(i);
                            if (btDevice == null) {
                                continue;
                            }
                            BluetoothDevice device =
                                    constructor.newInstance(btDevice.remoteDeviceAddress);
                            if (btDevice.remoteDeviceBondState == BluetoothDevice.BOND_BONDED
                                    && !btList.contains(btDevice)) {
                                Message msg = mMHandler.obtainMessage();
                                msg.what = 4;
                                msg.obj = device;
                                mMHandler.sendMessage(msg);
                                continue;
                            }
                            if (!btList.contains(btDevice)) {
                                Message msg = mMHandler.obtainMessage();
                                msg.what = 5;
                                msg.obj = device;
                                mMHandler.sendMessage(msg);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    /**
     * 连接状态:
     * 服务端 查看所有设备的连接情况
     * 客户端 查看跟服务端的连接情况
     */
    private void checkState() {
        StringBuilder info = new StringBuilder("");
        //        if (((BTApplication) mContext.getApplicationContext()).getConnectionType()
        //                == Constant.CLIENT) {
        //            info.append("客户端\n");
        //        } else if (((BTApplication) mContext.getApplicationContext()).getConnectionType()
        //                == Constant.SERVER) {
        //            info.append("服务端\n");
        //        } else if (((BTApplication) mContext.getApplicationContext()).getConnectionType()
        //                == Constant.CS) {
        //            info.append("服务端 + 客户端\n");
        //        }

        if (mBluetoothDevice != null) {
            if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                info.append("亲,已经配对\n");
            } else if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                info.append("亲,正在配对\n");
            } else if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                info.append("亲,没有配对\n");
            }
            info.append("蓝牙名称: ");
            info.append(mBluetoothDevice.getName());
            info.append("\n");
            info.append("蓝牙地址: ");
            info.append(mBluetoothDevice.getAddress());
            info.append("\n");
            info.append("蓝牙状态: ");
            info.append(mBluetoothDevice.getBondState());
        }

        showInfo(info.toString());
        info = null;
    }

    private void reset() {
        try {
            mBluetoothFragment.reset_btn.setClickable(false);
            mBluetoothDevice = null;
            mDevicesAdapter.clear();
            hideProgressBar();
            showInfo("");
            setServerButtonEnable();
            setConnectButtonEnable();
            //            if (BTClient.getInstance().getRemoteBluetoothSocket() != null) {
            //                BTClient.getInstance().getRemoteBluetoothSocket().close();
            //            }
            //            if (BTServer.getInstance().getBtSocketList() != null) {
            //                int size = BTServer.getInstance().getBtSocketList().size();
            //                for (int i = 0; i < size; i++) {
            //                    BluetoothSocket socket = BTServer.getInstance().getBtSocketList
            // ().get(i);
            //                    if (socket == null) {
            //                        continue;
            //                    }
            //                    socket.close();
            //                }
            //            }
            //            BTClient.getInstance().setIRemoteConnection(null);
            //            BTServer.getInstance().setIRemoteConnection(null);
            //            BTClient.setBTClientToNull();
            //            BTServer.setBTServerToNull();
            //            ((BTApplication) mContext.getApplicationContext()).setConnectionType
            // (Constant.NONE);
            mBluetoothFragment.reset_btn.setClickable(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showInfo(String info) {
        mBluetoothFragment.bt_status_text.setVisibility(View.VISIBLE);
        mBluetoothFragment.bt_status_text.setText(info);
    }

    private void showProgressBar() {
        mBluetoothFragment.process_bar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mBluetoothFragment.process_bar.setVisibility(View.GONE);
    }

    private void setServerButtonDisable() {
        mBluetoothFragment.as_service_btn.setClickable(false);
    }

    private void setServerButtonEnable() {
        mBluetoothFragment.as_service_btn.setClickable(true);
    }

    private void setConnectButtonDisable() {
        mBluetoothFragment.connect_btn.setClickable(false);
    }

    private void setConnectButtonEnable() {
        mBluetoothFragment.connect_btn.setClickable(true);
    }

    private void setSearchButtonEnable(boolean enable) {
        if (enable) {
            mBluetoothFragment.searth_device_btn.setEnabled(enable);
        } else {
            mBluetoothFragment.searth_device_btn.setEnabled(!enable);
        }
    }

    private static class MHandler extends Handler {

        private WeakReference<BluetoothFragmentController> mWeakReference;

        private MHandler(BluetoothFragmentController controller, Looper looper) {
            super(looper);
            mWeakReference = new WeakReference<BluetoothFragmentController>(controller);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BluetoothFragmentController controller = mWeakReference.get();
            if (controller == null) {
                return;
            }
            switch (msg.what) {
                case 0:
                    controller.hideProgressBar();
                    controller.showInfo("连接成功");
                    break;
                case 1:
                    //                    if (((BTApplication) controller.mContext
                    // .getApplicationContext())
                    //                            .getConnectionType() == Constant.CS) {
                    //                        ((BTApplication) controller.mContext
                    // .getApplicationContext())
                    //                                .setConnectionType(Constant.SERVER);
                    //                    } else if (((BTApplication) controller.mContext
                    // .getApplicationContext())
                    //                            .getConnectionType() == Constant.CLIENT) {
                    //                        ((BTApplication) controller.mContext
                    // .getApplicationContext())
                    //                                .setConnectionType(Constant.NONE);
                    //                    }
                    //                    BTClient.getInstance().setRemoteBluetoothSocket(null);
                    controller.hideProgressBar();
                    controller.showInfo("与服务端未连接");
                    controller.setServerButtonEnable();
                    controller.setConnectButtonEnable();
                    break;
                case 2:
                    //                    BluetoothSocket socket = (BluetoothSocket) msg.obj;
                    //                    if (socket != null) {
                    //                        BTServer.getInstance().removeClientBluetoothSocket
                    // (socket);
                    //                        BluetoothDevice device = socket.getRemoteDevice();
                    //                        if (device != null) {
                    //                            controller.showInfo("与 " + device.getName() + "
                    // 未连接");
                    //                        }
                    //                    }
                    break;
                case 3:
                    controller.checkState();
                    break;
                case 4:
                    controller.mDevicesAdapter.addDevice(0, (BluetoothDevice) msg.obj);
                    break;
                case 5:
                    controller.mDevicesAdapter.addDevice(-1, (BluetoothDevice) msg.obj);
                    break;
                case 6:
                    break;
            }
        }

    }

    private BTController.IBluetoothAction mIBluetoothAction = new BTController.IBluetoothAction() {

        @Override
        public void actionDiscoveryStarted() {
            showProgressBar();
            showInfo("正在查找附近的蓝牙设备...");
        }

        @Override
        public void actionDiscoveryFinished() {
            setSearchButtonEnable(true);
            if (!BTController.getInstance().isOpenedBluetooth()) {
                showInfo("");
                return;
            }
            hideProgressBar();
            showInfo("查找结束");
            checkState();
        }

        @Override
        public void actionFound(BluetoothDevice device) {
            if (device == null) {
                return;
            }

            try {
                ContentValues values = new ContentValues();
                values.put("remoteDeviceName", device.getName());
                values.put("remoteDeviceAddress", device.getAddress());
                // values.put("remoteDeviceType", String.valueOf(device.getType()));
                /*if (mICallSystemMethod != null) {
                    values.put("remoteDeviceAlias", mICallSystemMethod.getRemoteAlias(device));
                }*/
                values.put("remoteDeviceBondState", String.valueOf(device.getBondState()));
                values.put("remoteDeviceBluetoothClass", device.getBluetoothClass().toString());

                SimpleDao.getInstance().add2OrUpdate(
                        BTDevice.class,
                        values,
                        "remoteDeviceAddress",
                        device.getAddress());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (device.getBondState() == BluetoothDevice.BOND_BONDED
                    && !btList.contains(device)) {
                mDevicesAdapter.add(0, device);
                return;
            }
            if (!btList.contains(device)) {
                mDevicesAdapter.add(device);
            }

        }

        @Override
        public void actionBondStateChanged(BluetoothDevice device) {

        }

        @Override
        public void actionPairingRequest() {

        }

        @Override
        public void btBondBonding(BluetoothDevice device) {
            showProgressBar();
            checkState();
        }

        @Override
        public void btBondBonded(BluetoothDevice device) {
            hideProgressBar();
            checkState();
            if (SimpleDao.getInstance().isExists(
                    BTDevice.class, "remoteDeviceAddress", device.getAddress())) {
                ContentValues values = new ContentValues();
                values.put("remoteDeviceName", device.getName());
                values.put("remoteDeviceAddress", device.getAddress());
                // values.put("remoteDeviceType", String.valueOf(device.getType()));
                /*if (mICallSystemMethod != null) {
                    try {
                        values.put("remoteDeviceAlias", mICallSystemMethod.getRemoteAlias(device));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }*/
                values.put("remoteDeviceBondState", String.valueOf(device.getBondState()));
                values.put("remoteDeviceBluetoothClass", device.getBluetoothClass().toString());
                SimpleDao.getInstance().update(
                        BTDevice.class, values, "remoteDeviceAddress", device.getAddress());
            }
            mDevicesAdapter.refresh(mBluetoothFragment.bluetooth_device_recyclerview, 0, device);
        }

        @Override
        public void btBondNone(BluetoothDevice device) {
            hideProgressBar();
            checkState();
            if (SimpleDao.getInstance().isExists(
                    BTDevice.class, "remoteDeviceAddress", device.getAddress())) {
                ContentValues values = new ContentValues();
                values.put("remoteDeviceName", device.getName());
                values.put("remoteDeviceAddress", device.getAddress());
                // values.put("remoteDeviceType", String.valueOf(device.getType()));
                /*if (mICallSystemMethod != null) {
                    try {
                        values.put("remoteDeviceAlias", mICallSystemMethod.getRemoteAlias(device));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }*/
                values.put("remoteDeviceBondState", String.valueOf(device.getBondState()));
                values.put("remoteDeviceBluetoothClass", device.getBluetoothClass().toString());
                SimpleDao.getInstance().update(
                        BTDevice.class, values, "remoteDeviceAddress", device.getAddress());
            }
            mDevicesAdapter.refresh(mBluetoothFragment.bluetooth_device_recyclerview, -1, device);
        }

    };

    private OnResultListener mOnResultListener = new OnResultListener() {

        @Override
        public void onResult(int requestCode, int resultCode, Object object) {
            try {
                if (requestCode == Constant.INPUT_BT_ADDRESS_REQUESTCODE
                        && resultCode == Constant.INPUT_BT_ADDRESS_RESULTCODE) {
                    String address = (String) object;

                    Class<BluetoothDevice> cls = BluetoothDevice.class;
                    Constructor<BluetoothDevice> constructor =
                            cls.getDeclaredConstructor(String.class);
                    constructor.setAccessible(true);
                    BluetoothDevice device =
                            constructor.newInstance(address);

                    mBluetoothDevice = device;
                    checkState();
                    if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                        showInfo("已经配对成功不需要再次配对");
                    } else if (mBluetoothDevice.getBondState() ==
                            BluetoothDevice.BOND_BONDING) {

                    } else {
                        ThreadPool.getCachedThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                if (!BTController.getInstance().createBond(mBluetoothDevice)) {
                                    mMHandler.sendEmptyMessage(3);
                                }
                            }
                        });
                    }
                } else if (true) {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    private OnItemClickListener mOnItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(View itemView, int viewType, int position) {
            setSearchButtonEnable(true);
            hideProgressBar();
            BTController.getInstance().cancelScanDevice();
            mBluetoothDevice = btList.get(position);
            checkState();
        }

    };


}
