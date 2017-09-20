// ICallPowerManager.aidl
package com.weidi.callsystemmethod;

import android.content.pm.IPackageMoveObserver;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.ProxyInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.os.RemoteException;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.ContainerEncryptionParams;
import android.content.pm.EphemeralApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageInstallObserver2;
import android.content.pm.IPackageInstaller;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.IOnPermissionsChangeListener;
import android.content.pm.IntentFilterVerificationInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.KeySet;
import android.content.pm.PackageInfo;
import android.content.pm.ManifestDigest;
import android.content.pm.PackageCleanItem;
import android.content.pm.ParceledListSlice;
import android.content.pm.ProviderInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
import android.content.pm.VerifierDeviceIdentity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.content.IntentSender;
import android.os.WorkSource;
import android.os.Bundle;
import java.util.List;
import android.os.IBinder;
import aidl.android.bluetooth.IBluetoothHealthCallback;
import android.net.NetworkState;
import android.net.NetworkQuotaInfo;
import android.net.wifi.WifiDevice;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnInfo;
import com.android.internal.net.VpnProfile;
import android.net.NetworkCapabilities;
import android.net.NetworkMisc;

//import android.app.IAppTask;
import android.net.wifi.WifiConnectionStatistics;
import android.net.wifi.WifiActivityEnergyInfo;

import android.app.ITaskStackListener;
import android.app.IServiceConnection;
import android.app.IInstrumentationWatcher;
import android.app.IUiAutomationConnection;
import android.app.IActivityController;
import android.app.IStopUserCallback;
import android.app.IProcessObserver;
import android.app.IUidObserver;
import android.app.IUserSwitchObserver;
import android.bluetooth.OobData;
import android.bluetooth.IBluetoothCallback;
import android.app.PendingIntent;
import android.app.Notification;
import android.os.ResultReceiver;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.ScanResult;
import android.net.wifi.PasspointManagementObjectDefinition;
import android.net.wifi.ScanSettings;
import android.net.wifi.WifiInfo;
import android.net.DhcpInfo;
import android.os.Messenger;
import android.os.ParcelUuid;
import android.net.Network;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHealthAppConfiguration;

//import android.annotation.UserIdInt;
//import android.app.ActivityManager.RunningServiceInfo;
//import android.app.ActivityManager.RunningTaskInfo;
//import android.app.ActivityManager.StackInfo;
import android.app.assist.AssistContent;
import android.app.assist.AssistStructure;
//import android.content.ComponentName;
//import android.content.ContentProviderNative;
//import android.content.IContentProvider;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
//import android.content.UriPermission;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.ConfigurationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.ParceledListSlice;
import android.content.pm.ProviderInfo;
import android.content.pm.UserInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Debug;
import android.os.IBinder;
import android.os.IInterface;
import android.os.IProgressListener;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
//import android.os.PersistableBundle;
import android.os.RemoteException;
//import android.os.StrictMode;
import android.service.voice.IVoiceInteractionSession;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.os.IResultReceiver;

import android.service.carrier.CarrierIdentifier;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telephony.CellInfo;
import android.telephony.IccOpenLogicalChannelResponse;
import android.telephony.ModemActivityInfo;
import android.telephony.NeighboringCellInfo;
import android.telephony.RadioAccessFamily;
import android.telephony.ServiceState;
import android.telephony.TelephonyHistogram;
import android.telephony.VisualVoicemailSmsFilterSettings;
import com.android.internal.telephony.CellNetworkScanResult;
import com.android.internal.telephony.OperatorInfo;

import java.util.List;

// Declare any non-default types here with import statements

interface ICallBluetooth {

    //*********************************IBluetooth.aidl*********************************//

    boolean isEnabled();
    int getState();
    boolean enable();
    boolean enableNoAutoConnect();
    boolean disable();

    String getAddress();
    ParcelUuid[] getUuids();
    boolean setName(in String name);
    String getName();

    int getScanMode();
    boolean setScanMode(int mode, int duration);

    int getDiscoverableTimeout();
    boolean setDiscoverableTimeout(int timeout);

    boolean startDiscovery();
    boolean cancelDiscovery();
    boolean isDiscovering();

    int getAdapterConnectionState();
    int getProfileConnectionState(int profile);

    BluetoothDevice[] getBondedDevices();
    boolean createBond(in BluetoothDevice device, in int transport);
    boolean createBondOutOfBand(in BluetoothDevice device, in int transport, in OobData oobData);
    boolean cancelBondProcess(in BluetoothDevice device);
    boolean removeBond(in BluetoothDevice device);
    int getBondState(in BluetoothDevice device);
    long getSupportedProfiles();
    int getConnectionState(in BluetoothDevice device);

    String getRemoteName(in BluetoothDevice device);
    int getRemoteType(in BluetoothDevice device);
    String getRemoteAlias(in BluetoothDevice device);
    boolean setRemoteAlias(in BluetoothDevice device, in String name);
    int getRemoteClass(in BluetoothDevice device);
    ParcelUuid[] getRemoteUuids(in BluetoothDevice device);
    boolean fetchRemoteUuids(in BluetoothDevice device);
    boolean sdpSearch(in BluetoothDevice device, in ParcelUuid uuid);

    boolean setPin(in BluetoothDevice device, boolean accept, int len, in byte[] pinCode);
    boolean setPasskey(in BluetoothDevice device, boolean accept, int len, in byte[] passkey);
    boolean setPairingConfirmation(in BluetoothDevice device, boolean accept);

    int getPhonebookAccessPermission(in BluetoothDevice device);
    boolean setPhonebookAccessPermission(in BluetoothDevice device, int value);
    int getMessageAccessPermission(in BluetoothDevice device);
    boolean setMessageAccessPermission(in BluetoothDevice device, int value);
    int getSimAccessPermission(in BluetoothDevice device);
    boolean setSimAccessPermission(in BluetoothDevice device, int value);

    void sendConnectionStateChange(in BluetoothDevice device, int profile, int state, int prevState);

    void registerCallback(in IBluetoothCallback callback);
    void unregisterCallback(in IBluetoothCallback callback);

    // For Socket
    ParcelFileDescriptor connectSocket(in BluetoothDevice device, int type, in ParcelUuid uuid, int port, int flag);
    ParcelFileDescriptor createSocketChannel(int type, in String serviceName, in ParcelUuid uuid, int port, int flag);

    boolean configHciSnoopLog(boolean enable);
    boolean factoryReset();

    boolean isMultiAdvertisementSupported();
    boolean isPeripheralModeSupported();
    boolean isOffloadedFilteringSupported();
    boolean isOffloadedScanBatchingSupported();
    boolean isActivityAndEnergyReportingSupported();
    // BluetoothActivityEnergyInfo reportActivityInfo();

    /**
     * Requests the controller activity info asynchronously.
     * The implementor is expected to reply with the
     * {@link android.bluetooth.BluetoothActivityEnergyInfo} object placed into the Bundle with the
     * key {@link android.os.BatteryStats#RESULT_RECEIVER_CONTROLLER_KEY}.
     * The result code is ignored.
     */
    oneway void requestActivityInfo(in ResultReceiver result);

    void onLeServiceUp();
    void onBrEdrDown();

    int setSocketOpt(int type, int port, int optionName, in byte[] optionVal, int optionLen);
    int getSocketOpt(int type, int port, int optionName, out byte[] optionVal);

    //*********************************IBluetooth.aidl*********************************//

}
