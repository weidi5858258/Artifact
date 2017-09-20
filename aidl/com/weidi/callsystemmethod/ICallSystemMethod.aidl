// ICallSystemMethod.aidl
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

interface ICallSystemMethod {

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                    double aDouble, String aString);

}
