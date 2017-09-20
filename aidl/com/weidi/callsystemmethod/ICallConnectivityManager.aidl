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

interface ICallConnectivityManager {

    //*********************************IConnectivityManager.aidl*********************************//

    Network getActiveNetwork();
    Network getActiveNetworkForUid(int uid, boolean ignoreBlocked);
    NetworkInfo getActiveNetworkInfo();
    NetworkInfo getActiveNetworkInfoForUid(int uid, boolean ignoreBlocked);
    NetworkInfo getNetworkInfo(int networkType);
    NetworkInfo getNetworkInfoForUid(in Network network, int uid, boolean ignoreBlocked);
    NetworkInfo[] getAllNetworkInfo();
    Network getNetworkForType(int networkType);
    Network[] getAllNetworks();
    NetworkCapabilities[] getDefaultNetworkCapabilitiesForUser(int userId);

    boolean isNetworkSupported(int networkType);

    LinkProperties getActiveLinkProperties();
    LinkProperties getLinkPropertiesForType(int networkType);
    LinkProperties getLinkProperties(in Network network);

    NetworkCapabilities getNetworkCapabilities(in Network network);

    NetworkState[] getAllNetworkState();

    NetworkQuotaInfo getActiveNetworkQuotaInfo();
    boolean isActiveNetworkMetered();

    boolean requestRouteToHostAddress(int networkType, in byte[] hostAddress);

    int tether(String iface);

    int untether(String iface);

    int getLastTetherError(String iface);

    boolean isTetheringSupported();

    void startTethering(int type, in ResultReceiver receiver, boolean showProvisioningUi);

    void stopTethering(int type);

    String[] getTetherableIfaces();

    String[] getTetheredIfaces();

    String[] getTetheringErroredIfaces();

    String[] getTetheredDhcpRanges();

    String[] getTetherableUsbRegexs();

    String[] getTetherableWifiRegexs();

    String[] getTetherableBluetoothRegexs();

    int setUsbTethering(boolean enable);

    List<WifiDevice> getTetherConnectedSta();

    void reportInetCondition(int networkType, int percentage);

    void reportNetworkConnectivity(in Network network, boolean hasConnectivity);

    ProxyInfo getGlobalProxy();

    void setGlobalProxy(in ProxyInfo p);

    ProxyInfo getProxyForNetwork(in Network nework);

    boolean prepareVpn(String oldPackage, String newPackage, int userId);

    void setVpnPackageAuthorization(String packageName, int userId, boolean authorized);

    ParcelFileDescriptor establishVpn(in VpnConfig config);

    VpnConfig getVpnConfig(int userId);

    void startLegacyVpn(in VpnProfile profile);

    LegacyVpnInfo getLegacyVpnInfo(int userId);

    VpnInfo[] getAllVpnInfo();

    boolean updateLockdownVpn();
    boolean setAlwaysOnVpnPackage(int userId, String packageName, boolean lockdown);
    String getAlwaysOnVpnPackage(int userId);

    int checkMobileProvisioning(int suggestedTimeOutMs);

    String getMobileProvisioningUrl();

    void setProvisioningNotificationVisible(boolean visible, int networkType, in String action);

    void setAirplaneMode(boolean enable);

    void registerNetworkFactory(in Messenger messenger, in String name);

    boolean requestBandwidthUpdate(in Network network);

    void unregisterNetworkFactory(in Messenger messenger);

    int registerNetworkAgent(in Messenger messenger, in NetworkInfo ni, in LinkProperties lp,
                             in NetworkCapabilities nc, int score, in NetworkMisc misc);

    NetworkRequest requestNetwork(in NetworkCapabilities networkCapabilities,
                                  in Messenger messenger, int timeoutSec, in IBinder binder, int legacy);

    NetworkRequest pendingRequestForNetwork(in NetworkCapabilities networkCapabilities,
                                            in PendingIntent operation);

    void releasePendingNetworkRequest(in PendingIntent operation);

    NetworkRequest listenForNetwork(in NetworkCapabilities networkCapabilities,
                                    in Messenger messenger, in IBinder binder);

    void pendingListenForNetwork(in NetworkCapabilities networkCapabilities,
                                 in PendingIntent operation);

    void requestLinkProperties(in NetworkRequest networkRequest);
    void requestNetworkCapabilities(in NetworkRequest networkRequest);
    void releaseNetworkRequest(in NetworkRequest networkRequest);

    void setAcceptUnvalidated(in Network network, boolean accept, boolean always);
    void setAvoidUnvalidated(in Network network);

    int getRestoreDefaultNetworkDelay(int networkType);

    boolean addVpnAddress(String address, int prefixLength);
    boolean removeVpnAddress(String address, int prefixLength);
    boolean setUnderlyingNetworksForVpn(in Network[] networks);

    void factoryReset();

    void startNattKeepalive(in Network network, int intervalSeconds, in Messenger messenger,
                            in IBinder binder, String srcAddr, int srcPort, String dstAddr);

    void stopKeepalive(in Network network, int slot);

    String getCaptivePortalServerUrl();

    //*********************************IConnectivityManager.aidl*********************************//

}
