// ICallSystemMethod.aidl
package com.weidi.callsystemmethod;

import android.os.Messenger;
import android.os.Bundle;
import android.os.WorkSource;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcelable;
import android.os.RemoteException;
import android.os.ParcelUuid;
import android.os.ParcelFileDescriptor;

import android.telephony.CellInfo;
import android.telephony.NeighboringCellInfo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHealthAppConfiguration;
import android.bluetooth.IBluetoothCallback;
import android.bluetooth.IBluetoothHealthCallback;
import android.bluetooth.IBluetoothStateChangeCallback;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
// import android.content.pm.ContainerEncryptionParams;
import android.content.pm.FeatureInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageMoveObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
// import android.content.pm.ManifestDigest;
// import android.content.pm.PackageCleanItem;
// import android.content.pm.ParceledListSlice;
import android.content.pm.ProviderInfo;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.UserInfo;
// import android.content.pm.VerificationParams;
// import android.content.pm.VerifierDeviceIdentity;
import android.content.pm.ThemeInfo;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.ComponentName;
import android.content.IntentSender;
import android.content.IIntentReceiver;
import android.content.res.Configuration;

import android.net.Uri;
import android.net.LinkProperties;
import android.net.NetworkInfo;
// import android.net.NetworkQuotaInfo;
// import android.net.NetworkState;
// import android.net.ProxyProperties;

import android.net.wifi.WifiInfo;
// import android.net.wifi.WifiConfiguration;
import android.net.wifi.ScanResult;
import android.net.DhcpInfo;

// import com.android.internal.net.LegacyVpnInfo;
// import com.android.internal.net.VpnConfig;
// import com.android.internal.net.VpnProfile;

import java.util.List;
// import android.graphics.Bitmap;

// Declare any non-default types here with import statements

interface ICallSystemMethod {

    //*********************************Custom*********************************//

    boolean forceStopPackage(String packageName);

    boolean deletePackage(String packageName, IPackageDeleteObserver observer);

    boolean installPackage(
                String pathAndFilename,
                String installerPackageName,
                IPackageInstallObserver observer);

    void goBack();

    void lockScreen();

    void takeScreenshot();

    //*********************************Custom*********************************//

    //*********************************Input.java*********************************//

    void input(in String[] commands);

    //*********************************Input.java*********************************//

    //*********************************USB调试*********************************//

    void usbDebug(boolean open);

    //*********************************USB调试*********************************//

    //*********************************IMountService.java*********************************//

    void mountVolume(String mountPoint);
    void unmountVolume(String mountPoint, boolean force, boolean removeEncryption);

    //*********************************IMountService.java*********************************//

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

    //*********************************ITelephony.aidl*********************************//

    /**
     * Dial a number. This doesn't place the call. It displays
     * the Dialer screen.
     * @param number the number to be dialed. If null, this
     * would display the Dialer screen with no number pre-filled.
     */
    void dial(String number);

    /**
     * Place a call to the specified number.
     * @param number the number to be called.
     */
    void call(String number);

    /**
     * If there is currently a call in progress, show the call screen.
     * The DTMF dialpad may or may not be visible initially, depending on
     * whether it was up when the user last exited the InCallScreen.
     *
     * @return true if the call screen was shown.
     */
    boolean showCallScreen();

    /**
     * Variation of showCallScreen() that also specifies whether the
     * DTMF dialpad should be initially visible when the InCallScreen
     * comes up.
     *
     * @param showDialpad if true, make the dialpad visible initially,
     *                    otherwise hide the dialpad initially.
     * @return true if the call screen was shown.
     *
     * @see showCallScreen
     */
    boolean showCallScreenWithDialpad(boolean showDialpad);

    /**
     * End call if there is a call in progress, otherwise does nothing.
     *
     * @return whether it hung up
     */
    boolean endCall();

    /**
     * Answer the currently-ringing call.
     *
     * If there's already a current active call, that call will be
     * automatically put on hold.  If both lines are currently in use, the
     * current active call will be ended.
     *
     * TODO: provide a flag to let the caller specify what policy to use
     * if both lines are in use.  (The current behavior is hardwired to
     * "answer incoming, end ongoing", which is how the CALL button
     * is specced to behave.)
     *
     * TODO: this should be a oneway call (especially since it's called
     * directly from the key queue thread).
     */
    void answerRingingCall();

    /**
     * Silence the ringer if an incoming call is currently ringing.
     * (If vibrating, stop the vibrator also.)
     *
     * It's safe to call this if the ringer has already been silenced, or
     * even if there's no incoming call.  (If so, this method will do nothing.)
     *
     * TODO: this should be a oneway call too (see above).
     *       (Actually *all* the methods here that return void can
     *       probably be oneway.)
     */
    void silenceRinger();

    /**
     * Check if we are in either an active or holding call
     * @return true if the phone state is OFFHOOK.
     */
    boolean isOffhook();

    /**
     * Check if an incoming phone call is ringing or call waiting.
     * @return true if the phone state is RINGING.
     */
    boolean isRinging();

    /**
     * Check if the phone is idle.
     * @return true if the phone state is IDLE.
     */
    boolean isIdle();

    /**
     * Check to see if the radio is on or not.
     * @return returns true if the radio is on.
     */
    boolean isRadioOn();

    /**
     * Check if the SIM pin lock is enabled.
     * @return true if the SIM pin lock is enabled.
     */
    boolean isSimPinEnabled();

    /**
     * Cancels the missed calls notification.
     */
    void cancelMissedCallsNotification();

    /**
     * Supply a pin to unlock the SIM.  Blocks until a result is determined.
     * @param pin The pin to check.
     * @return whether the operation was a success.
     */
    boolean supplyPin(String pin);

    /**
     * Supply puk to unlock the SIM and set SIM pin to new pin.
     *  Blocks until a result is determined.
     * @param puk The puk to check.
     *        pin The new pin to be set in SIM
     * @return whether the operation was a success.
     */
    boolean supplyPuk(String puk, String pin);

    /**
     * Handles PIN MMI commands (PIN/PIN2/PUK/PUK2), which are initiated
     * without SEND (so <code>dial</code> is not appropriate).
     *
     * @param dialString the MMI command to be executed.
     * @return true if MMI command is executed.
     */
    boolean handlePinMmi(String dialString);

    /**
     * Toggles the radio on or off.
     */
    void toggleRadioOnOff();

    /**
     * Set the radio to on or off
     */
    boolean setRadio(boolean turnOn);

    /**
     * Request to update location information in service state
     */
    void updateServiceLocation();

    /**
     * Enable location update notifications.
     */
    void enableLocationUpdates();

    /**
     * Disable location update notifications.
     */
    void disableLocationUpdates();

    /**
     * Enable a specific APN type.
     */
    int enableApnType(String type);

    /**
     * Disable a specific APN type.
     */
    int disableApnType(String type);

    /**
     * Allow mobile data connections.
     */
    boolean enableDataConnectivity();

    /**
     * Disallow mobile data connections.
     */
    boolean disableDataConnectivity();

    /**
     * Report whether data connectivity is possible.
     */
    boolean isDataConnectivityPossible();

    Bundle getCellLocation();

    /**
     * Returns the neighboring cell information of the device.
     */
    List<NeighboringCellInfo> getNeighboringCellInfo();

    int getCallState();
    int getDataActivity();
    int getDataState();

    /**
     * Returns the current active phone type as integer.
     * Returns TelephonyManager.PHONE_TYPE_CDMA if RILConstants.CDMA_PHONE
     * and TelephonyManager.PHONE_TYPE_GSM if RILConstants.GSM_PHONE
     */
    int getActivePhoneType();

    /**
     * Returns the CDMA ERI icon index to display
     */
    int getCdmaEriIconIndex();

    /**
     * Returns the CDMA ERI icon mode,
     * 0 - ON
     * 1 - FLASHING
     */
    int getCdmaEriIconMode();

    /**
     * Returns the CDMA ERI text,
     */
    String getCdmaEriText();

    /**
     * Returns true if OTA service provisioning needs to run.
     * Only relevant on some technologies, others will always
     * return false.
     */
    boolean needsOtaServiceProvisioning();

    /**
      * Returns the unread count of voicemails
      */
    int getVoiceMessageCount();

    /**
      * Returns the network type
      */
    int getNetworkType();

    /**
     * Return true if an ICC card is present
     */
    boolean hasIccCard();

    /**
     * Return if the current radio is LTE on CDMA. This
     * is a tri-state return value as for a period of time
     * the mode may be unknown.
     *
     * @return {@link Phone#LTE_ON_CDMA_UNKNOWN}, {@link Phone#LTE_ON_CDMA_FALSE}
     * or {@link PHone#LTE_ON_CDMA_TRUE}
     */
    int getLteOnCdmaMode();

    /**
     * Returns the all observed cell information of the device.
     */
    List<CellInfo> getAllCellInfo();

    //*********************************ITelephony.aidl*********************************//

    //*********************************IPowerManager.aidl*********************************//

    // WARNING: The first two methods must remain the first two methods because their
    // transaction numbers must not change unless IPowerManager.cpp is also updated.
    void acquireWakeLock(IBinder lock, int flags, String tag, in WorkSource ws);
    void releaseWakeLock(IBinder lock, int flags);

    void updateWakeLockWorkSource(IBinder lock, in WorkSource ws);
    boolean isWakeLockLevelSupported(int level);

    void userActivity(long time, int event, int flags);
    void wakeUp(long time);
    void goToSleep(long time, int reason);
    void nap(long time);

    boolean isScreenOn();
    void reboot(boolean confirm, String reason, boolean wait);
    void shutdown(boolean confirm, boolean wait);
    void crash(String message);

    void setStayOnSetting(int val);
    void setMaximumScreenOffTimeoutFromDeviceAdmin(int timeMs);

    // temporarily overrides the screen brightness settings to allow the user to
    // see the effect of a settings change without applying it immediately
    void setTemporaryScreenBrightnessSettingOverride(int brightness);
    void setTemporaryScreenAutoBrightnessAdjustmentSettingOverride(float adj);

    // sets the attention light (used by phone app only)
    void setAttentionLight(boolean on, int color);

    void cpuBoost(int duration);

    void setKeyboardVisibility(boolean visible);

    void setKeyboardLight(boolean on, int key);

    //*********************************IPowerManager.aidl*********************************//

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
    /**
    * 检测连接状态
    * int a2dp = bluetoothAdapter.getProfileConnectionState(BluetoothProfile.A2DP);
    */
    int getProfileConnectionState(int profile);

    BluetoothDevice[] getBondedDevices();
    boolean createBond(in BluetoothDevice device);
    boolean cancelBondProcess(in BluetoothDevice device);
    boolean removeBond(in BluetoothDevice device);
    int getBondState(in BluetoothDevice device);

    String getRemoteName(in BluetoothDevice device);
    String getRemoteAlias(in BluetoothDevice device);
    boolean setRemoteAlias(in BluetoothDevice device, in String name);
    int getRemoteClass(in BluetoothDevice device);
    ParcelUuid[] getRemoteUuids(in BluetoothDevice device);
    boolean fetchRemoteUuids(in BluetoothDevice device);

    boolean setPin(in BluetoothDevice device, boolean accept, int len, in byte[] pinCode);
    boolean setPasskey(in BluetoothDevice device, boolean accept, int len, in byte[] passkey);
    boolean setPairingConfirmation(in BluetoothDevice device, boolean accept);

    void sendConnectionStateChange(in BluetoothDevice device, int profile, int state, int prevState);

    void registerCallback(in IBluetoothCallback callback);
    void unregisterCallback(in IBluetoothCallback callback);

    // For Socket
    ParcelFileDescriptor connectSocket(in BluetoothDevice device, int type, in ParcelUuid uuid, int port, int flag);
    ParcelFileDescriptor createSocketChannel(int type, in String serviceName, in ParcelUuid uuid, int port, int flag);

    //*********************************IBluetooth.aidl*********************************//

    //*********************************IBluetoothA2dp.aidl*********************************//

    boolean connectA2dp(in BluetoothDevice device);
    boolean disconnectA2dp(in BluetoothDevice device);
    List<BluetoothDevice> getConnectedDevicesA2dp();
    List<BluetoothDevice> getDevicesMatchingConnectionStatesA2dp(in int[] states);
    int getConnectionStateA2dp(in BluetoothDevice device);
    boolean setPriorityA2dp(in BluetoothDevice device, int priority);
    int getPriorityA2dp(in BluetoothDevice device);
    boolean isA2dpPlayingA2dp(in BluetoothDevice device);

    //*********************************IBluetoothA2dp.aidl*********************************//

    //*********************************IBluetoothHeadset.aidl*********************************//

    boolean connectHeadset(in BluetoothDevice device);
    boolean disconnectHeadset(in BluetoothDevice device);
    List<BluetoothDevice> getConnectedDevicesHeadset();
    List<BluetoothDevice> getDevicesMatchingConnectionStatesHeadset(in int[] states);
    int getConnectionStateHeadset(in BluetoothDevice device);
    boolean setPriorityHeadset(in BluetoothDevice device, int priority);
    int getPriorityHeadset(in BluetoothDevice device);
    boolean startVoiceRecognitionHeadset(in BluetoothDevice device);
    boolean stopVoiceRecognitionHeadset(in BluetoothDevice device);
    boolean isAudioConnectedHeadset(in BluetoothDevice device);

    // APIs that can be made in future
    int getBatteryUsageHintHeadset(in BluetoothDevice device);

    // Internal functions, not be made public
    boolean acceptIncomingConnectHeadset(in BluetoothDevice device);
    boolean rejectIncomingConnectHeadset(in BluetoothDevice device);
    int getAudioStateHeadset(in BluetoothDevice device);

    boolean isAudioOnHeadset();
    boolean connectAudioHeadset();
    boolean disconnectAudioHeadset();
    boolean startScoUsingVirtualVoiceCallHeadset(in BluetoothDevice device);
    boolean stopScoUsingVirtualVoiceCallHeadset(in BluetoothDevice device);
    void phoneStateChangedHeadset(int numActive, int numHeld, int callState, String number, int type);
    void roamChangedHeadset(boolean roam);
    void clccResponseHeadset(int index, int direction, int status, int mode, boolean mpty,
                      String number, int type);

    //*********************************IBluetoothHeadset.aidl*********************************//

    //*********************************IBluetoothHealth.aidl*********************************//

    boolean registerAppConfigurationHealth(in BluetoothHealthAppConfiguration config,
        in IBluetoothHealthCallback callback);
    boolean unregisterAppConfigurationHealth(in BluetoothHealthAppConfiguration config);
    boolean connectChannelToSourceHealth(in BluetoothDevice device, in BluetoothHealthAppConfiguration config);
    boolean connectChannelToSinkHealth(in BluetoothDevice device, in BluetoothHealthAppConfiguration config,
        int channelType);
    boolean disconnectChannelHealth(in BluetoothDevice device, in BluetoothHealthAppConfiguration config, int id);
    ParcelFileDescriptor getMainChannelFdHealth(in BluetoothDevice device, in BluetoothHealthAppConfiguration config);
    List<BluetoothDevice> getConnectedHealthDevicesHealth();
    List<BluetoothDevice> getHealthDevicesMatchingConnectionStatesHealth(in int[] states);
    int getHealthDeviceConnectionStateHealth(in BluetoothDevice device);

    //*********************************IBluetoothHealth.aidl*********************************//

    //*********************************IConnectivityManager.aidl*********************************//

    NetworkInfo getActiveNetworkInfo();
    NetworkInfo getActiveNetworkInfoForUid(int uid);
    NetworkInfo getNetworkInfo(int networkType);
    NetworkInfo[] getAllNetworkInfo();
    LinkProperties getActiveLinkProperties();
    LinkProperties getLinkProperties(int networkType);
    // NetworkState[] getAllNetworkState();
    // NetworkQuotaInfo getActiveNetworkQuotaInfo();
    // ProxyProperties getGlobalProxy();
    // void setGlobalProxy(in ProxyProperties p);
    // ProxyProperties getProxy();
    boolean protectVpn(in ParcelFileDescriptor socket);
    // ParcelFileDescriptor establishVpn(in VpnConfig config);
    // LegacyVpnInfo getLegacyVpnInfo();
    void captivePortalCheckComplete(in NetworkInfo info);
    int startUsingNetworkFeature(int networkType, in String feature,
            in IBinder binder);
    // void startLegacyVpn(in VpnProfile profile);

    void setNetworkPreference(int pref);

    int getNetworkPreference();

    boolean isNetworkSupported(int networkType);

    boolean isActiveNetworkMetered();

    boolean setRadios(boolean onOff);

    // why???
//    boolean setRadio(int networkType, boolean turnOn);

    int stopUsingNetworkFeature(int networkType, in String feature);

    boolean requestRouteToHost(int networkType, int hostAddress);

    boolean requestRouteToHostAddress(int networkType, in byte[] hostAddress);

    boolean getMobileDataEnabled();

    void setMobileDataEnabled(boolean enabled);

    /** Policy control over specific {@link NetworkStateTracker}. */
    void setPolicyDataEnable(int networkType, boolean enabled);

    int tether(String iface);

    int untether(String iface);

    int getLastTetherError(String iface);

    boolean isTetheringSupported();

    /**
     * Return list of interface pairs that are actively tethered.  Even indexes are
     * remote interface, and odd indexes are corresponding local interfaces.
     */
    String[] getTetheredIfacePairs();

    String[] getTetheringErroredIfaces();

    String[] getTetherableUsbRegexs();

    String[] getTetherableWifiRegexs();

    String[] getTetherableBluetoothRegexs();

    String[] getTetherableIfaces();

    String[] getTetheredIfaces();

    int setUsbTethering(boolean enable);

    void requestNetworkTransitionWakelock(in String forWhom);

    void reportInetCondition(int networkType, int percentage);

    void setDataDependency(int networkType, boolean met);

    boolean prepareVpn(String oldPackage, String newPackage);

    boolean updateLockdownVpn();

    //*********************************IConnectivityManager.aidl*********************************//

    //*********************************IPackageManager.aidl*********************************//

    PackageInfo getPackageInfo(String packageName, int flags, int userId);
    int getPackageUid(String packageName, int userId);
    int[] getPackageGids(String packageName);

    String[] currentToCanonicalPackageNames(in String[] names);

    PermissionInfo getPermissionInfo(String name, int flags);

    List<PermissionInfo> queryPermissionsByGroup(String group, int flags);

    PermissionGroupInfo getPermissionGroupInfo(String name, int flags);

    List<PermissionGroupInfo> getAllPermissionGroups(int flags);

    ApplicationInfo getApplicationInfo(String packageName, int flags ,int userId);

    ActivityInfo getActivityInfo(in ComponentName className, int flags, int userId);

    ActivityInfo getReceiverInfo(in ComponentName className, int flags, int userId);

    ServiceInfo getServiceInfo(in ComponentName className, int flags, int userId);

    ProviderInfo getProviderInfo(in ComponentName className, int flags, int userId);

    int checkPermission(String permName, String pkgName);

    int checkUidPermission(String permName, int uid);

    boolean addPermission(in PermissionInfo info);

    void removePermission(String name);

    void grantPermission(String packageName, String permissionName);

    void revokePermission(String packageName, String permissionName);

    boolean isProtectedBroadcast(String actionName);

    int checkSignatures(String pkg1, String pkg2);

    int checkUidSignatures(int uid1, int uid2);

    String[] getPackagesForUid(int uid);

    String getNameForUid(int uid);

    int getUidForSharedUser(String sharedUserName);

    ResolveInfo resolveIntent(in Intent intent, String resolvedType, int flags, int userId);

    List<ResolveInfo> queryIntentActivities(in Intent intent,
            String resolvedType, int flags, int userId);

    List<ResolveInfo> queryIntentActivityOptions(
            in ComponentName caller, in Intent[] specifics,
            in String[] specificTypes, in Intent intent,
            String resolvedType, int flags, int userId);

    List<ResolveInfo> queryIntentReceivers(in Intent intent,
            String resolvedType, int flags, int userId);

    ResolveInfo resolveService(in Intent intent,
            String resolvedType, int flags, int userId);

    List<ResolveInfo> queryIntentServices(in Intent intent,
            String resolvedType, int flags, int userId);

    // ParceledListSlice getInstalledPackages(int flags, in String lastRead, in int userId);

    // ParceledListSlice getInstalledApplications(int flags, in String lastRead, int userId);

    List<ApplicationInfo> getPersistentApplications(int flags);

    ProviderInfo resolveContentProvider(String name, int flags, int userId);

    void querySyncProviders(inout List<String> outNames,
            inout List<ProviderInfo> outInfo);

    List<ProviderInfo> queryContentProviders(
            String processName, int uid, int flags);

    InstrumentationInfo getInstrumentationInfo(
            in ComponentName className, int flags);

    List<InstrumentationInfo> queryInstrumentation(
            String targetPackage, int flags);

//    void installPackage(in Uri packageURI, IPackageInstallObserver observer, int flags,
//            in String installerPackageName);
//    void deletePackage(in String packageName, IPackageDeleteObserver observer, int flags);

    void finishPackageInstall(int token);

    void setInstallerPackageName(in String targetPackage, in String installerPackageName);

    String getInstallerPackageName(in String packageName);

    void addPackageToPreferred(String packageName);

    void removePackageFromPreferred(String packageName);

    List<PackageInfo> getPreferredPackages(int flags);

    void addPreferredActivity(in IntentFilter filter, int match,
            in ComponentName[] set, in ComponentName activity, int userId);

    void replacePreferredActivity(in IntentFilter filter, int match,
            in ComponentName[] set, in ComponentName activity);

    void clearPackagePreferredActivities(String packageName);

    int getPreferredActivities(out List<IntentFilter> outFilters,
            out List<ComponentName> outActivities, String packageName);

    boolean getPrivacyGuardSetting(in String packageName, int userId);

    void setPrivacyGuardSetting(in String packageName, boolean enabled, int userId);

    void setComponentEnabledSetting(in ComponentName componentName,
            in int newState, in int flags, int userId);

    int getComponentEnabledSetting(in ComponentName componentName, int userId);

    void setApplicationEnabledSetting(in String packageName, in int newState, int flags, int userId);

    int getApplicationEnabledSetting(in String packageName, int userId);

    void setPackageStoppedState(String packageName, boolean stopped, int userId);

    void freeStorageAndNotify(in long freeStorageSize,
             IPackageDataObserver observer);

    void freeStorage(in long freeStorageSize,
             in IntentSender pi);

    void deleteApplicationCacheFiles(in String packageName, IPackageDataObserver observer);

    void clearApplicationUserData(in String packageName, IPackageDataObserver observer, int userId);

    void getPackageSizeInfo(in String packageName, int userHandle, IPackageStatsObserver observer);

    String[] getSystemSharedLibraryNames();

    FeatureInfo[] getSystemAvailableFeatures();

    boolean hasSystemFeature(String name);

    void enterSafeMode();
    boolean isSafeMode();
    void systemReady();
    boolean hasSystemUidErrors();

    void performBootDexOpt();

    boolean performDexOpt(String packageName);

    void updateExternalMediaStatus(boolean mounted, boolean reportStatus);

    // PackageCleanItem nextPackageToClean(in PackageCleanItem lastPackage);

    void movePackage(String packageName, IPackageMoveObserver observer, int flags);

    boolean addPermissionAsync(in PermissionInfo info);

    boolean setInstallLocation(int loc);
    int getInstallLocation();

    /*void installPackageWithVerification(in Uri packageURI, in IPackageInstallObserver observer,
            int flags, in String installerPackageName, in Uri verificationURI,
            in ManifestDigest manifestDigest, in ContainerEncryptionParams encryptionParams);*/

    /*void installPackageWithVerificationAndEncryption(in Uri packageURI,
            in IPackageInstallObserver observer, int flags, in String installerPackageName,
            in VerificationParams verificationParams,
            in ContainerEncryptionParams encryptionParams);*/

    int installExistingPackage(String packageName);

    void verifyPendingInstall(int id, int verificationCode);
    void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay);

    // VerifierDeviceIdentity getVerifierDeviceIdentity();

    boolean isFirstBoot();
    boolean isOnlyCoreApps();

    void setPermissionEnforced(String permission, boolean enforced);
    boolean isPermissionEnforced(String permission);

    boolean isStorageLow();

    //*********************************IPackageManager.aidl*********************************//

    //*********************************IActivityManager.java*********************************//

    void unregisterReceiver(IIntentReceiver receiver);

    void activityResumed(IBinder token);

    void activityPaused(IBinder token);

    void removeContentProviderExternal(String name, IBinder token);

    boolean refContentProvider(IBinder connection, int stableDelta, int unstableDelta);

    void unstableProviderDied(IBinder connection);

    void serviceDoneExecuting(IBinder token, int type, int startId, int res);

    void clearPendingBackup();

    void backupAgentCreated(String packageName, IBinder agent);

    void killApplicationProcess(String processName, int uid);

    Configuration getConfiguration();

    void setRequestedOrientation(IBinder token, int requestedOrientation);

    int getRequestedOrientation(IBinder token);

    ComponentName getActivityClassForToken(IBinder token);

    String getPackageForToken(IBinder token);

    int handleIncomingUser(int callingPid, int callingUid, int userId, boolean allowAll,
            boolean requireFull, String name, String callerPackage);

    void setProcessLimit(int max);

    int getProcessLimit();

    void setProcessForeground(IBinder token, int pid, boolean isForeground);

    boolean isPrivacyGuardEnabledForProcess(int pid);

    void killBackgroundProcesses(String packageName, int userId);

    void killAllBackgroundProcesses();

    void unhandledBack();

    void setAlwaysFinish(boolean enabled);

    boolean killProcessesBelowForeground(String reason);

    void startRunning(String pkg, String cls, String action, String data);

    void signalPersistentProcesses(int signal);

    // ConfigurationInfo getDeviceConfigurationInfo();

    int getLaunchedFromUid(IBinder activityToken);

    void requestBugReport();

    long inputDispatchingTimedOut(int pid, boolean aboveSystem);

    void showBootMessage(CharSequence msg, boolean always);

    void dismissKeyguardOnNextActivity();

    boolean targetTaskAffinityMatchesActivity(IBinder token, String destAffinity);

    boolean switchUser(int userid);

    boolean isUserRunning(int userid, boolean orStopping);

    int[] getRunningUserIds();

    boolean removeSubTask(int taskId, int subTaskIndex);

    boolean removeTask(int taskId, int flags);

    int getFrontActivityScreenCompatMode();

    void setFrontActivityScreenCompatMode(int mode);

    int getPackageScreenCompatMode(String packageName);

    void setPackageScreenCompatMode(String packageName, int mode);

    boolean getPackageAskScreenCompat(String packageName);

    void setPackageAskScreenCompat(String packageName, boolean ask);

    void setImmersive(IBinder token, boolean immersive);

    boolean isImmersive(IBinder token);

    boolean isTopActivityImmersive();

    void crashApplication(int uid, int initialPid, String packageName, String message);

    void stopAppSwitches();

    void resumeAppSwitches();

    void killApplicationWithAppId(String pkg, int appid);

    void closeSystemDialogs(String reason);

    void overridePendingTransition(IBinder token, String packageName, int enterAnim, int exitAnim);

    boolean isUserAMonkey();

    void finishHeavyWeightApp();

    void finishSubActivity(IBinder token, String resultWho, int requestCode);

    boolean finishActivityAffinity(IBinder token);

    boolean willActivityBeVisible(IBinder token);

    void activitySlept(IBinder token);

    void activityDestroyed(IBinder token);

    String getCallingPackage(IBinder token);

    ComponentName getCallingActivity(IBinder token);

    List getServices(int maxNum, int flags);

    boolean moveActivityTaskToBack(IBinder token, boolean nonRoot);

    void moveTaskBackwards(int task);

    int getTaskForActivity(IBinder token, boolean onlyRoot);

    void goingToSleep();

    void wakingUp();

    void setLockScreenShown(boolean shown);

    //*********************************IActivityManager.java*********************************//

    //*********************************IWifiManager.aidl*********************************//

    // List<WifiConfiguration> getConfiguredNetworks();

    // int addOrUpdateNetwork(in WifiConfiguration config);

    boolean removeNetwork(int netId);

    boolean enableNetwork(int netId, boolean disableOthers);

    boolean disableNetwork(int netId);

    boolean pingSupplicant();

    void startScan(boolean forceActive);

    List<ScanResult> getScanResults();

    void disconnect();

    void reconnect();

    void reassociate();

    WifiInfo getConnectionInfo();

    boolean setWifiEnabled(boolean enable);

    int getWifiEnabledState();// 1 off 3 on

    void setCountryCode(String country, boolean persist);

    String getCountryCode();

    void setFrequencyBand(int band, boolean persist);

    int getFrequencyBand();

    boolean isDualBandSupported();

    boolean isIbssSupported();

    // List<WifiChannel> getSupportedChannels();

    boolean saveConfiguration();

    DhcpInfo getDhcpInfo();

    boolean acquireWifiLock(IBinder lock, int lockType, String tag, in WorkSource ws);

    void updateWifiLockWorkSource(IBinder lock, in WorkSource ws);

    boolean releaseWifiLock(IBinder lock);

    void initializeMulticastFiltering();

    boolean isMulticastEnabled();

    void acquireMulticastLock(IBinder binder, String tag);

    void releaseMulticastLock();

    // void setWifiApEnabled(in WifiConfiguration wifiConfig, boolean enable);

    int getWifiApEnabledState();

    // WifiConfiguration getWifiApConfiguration();

    // void setWifiApConfiguration(in WifiConfiguration wifiConfig);

    void startWifi();

    void stopWifi();

    void addToBlacklist(String bssid);

    void clearBlacklist();

    Messenger getWifiServiceMessenger();

    Messenger getWifiStateMachineMessenger();

    String getConfigFile();

    //*********************************IWifiManager.aidl*********************************//

}
