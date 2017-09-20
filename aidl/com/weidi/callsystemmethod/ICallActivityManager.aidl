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

interface ICallActivityManager {

    //*********************************IActivityManager.java*********************************//

    /*int startActivity(IApplicationThread caller, String callingPackage, Intent intent,
            String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags,
            ProfilerInfo profilerInfo, Bundle options);
    int startActivityAsUser(IApplicationThread caller, String callingPackage, Intent intent,
            String resolvedType, IBinder resultTo, String resultWho, int requestCode, int flags,
            ProfilerInfo profilerInfo, Bundle options, int userId);
    int startActivityAsCaller(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho, int requestCode,
            int flags, ProfilerInfo profilerInfo, Bundle options, boolean ignoreTargetSecurity,
            int userId);
    WaitResult startActivityAndWait(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho,
            int requestCode, int flags, ProfilerInfo profilerInfo, Bundle options,
            int userId);
    int startActivityWithConfig(IApplicationThread caller, String callingPackage,
            Intent intent, String resolvedType, IBinder resultTo, String resultWho,
            int requestCode, int startFlags, Configuration newConfig,
            Bundle options, int userId);
    int startActivityIntentSender(IApplicationThread caller,
            IntentSender intent, Intent fillInIntent, String resolvedType,
            IBinder resultTo, String resultWho, int requestCode,
            int flagsMask, int flagsValues, Bundle options);*/
    /*int startVoiceActivity(String callingPackage, int callingPid, int callingUid,
            in Intent intent, String resolvedType,
            IVoiceInteractionSession session,
            IVoiceInteractor interactor, int flags,
            ProfilerInfo profilerInfo,
            Bundle options, int userId);*/
    /*boolean startNextMatchingActivity(IBinder callingActivity,
            Intent intent, Bundle options);*/
    int startActivityFromRecents(int taskId, in Bundle options);
    boolean finishActivity(IBinder token, int code, in Intent data, int finishTask);
    void finishSubActivity(IBinder token, String resultWho, int requestCode);
    boolean finishActivityAffinity(IBinder token);
    void finishVoiceTask(IVoiceInteractionSession session);
    boolean releaseActivityInstance(IBinder token);
    boolean willActivityBeVisible(IBinder token);
    void unregisterReceiver(IIntentReceiver receiver);
    /*void releaseSomeActivities(IApplicationThread app);
    Intent registerReceiver(IApplicationThread caller, String callerPackage,
            IIntentReceiver receiver, IntentFilter filter,
            String requiredPermission, int userId);
    int broadcastIntent(IApplicationThread caller, Intent intent,
            String resolvedType, IIntentReceiver resultTo, int resultCode,
            String resultData, Bundle map, String[] requiredPermissions,
            int appOp, Bundle options, boolean serialized, boolean sticky, int userId);
    void unbroadcastIntent(IApplicationThread caller, Intent intent, int userId);
    void attachApplication(IApplicationThread app);*/
    /*void finishReceiver(IBinder who, int resultCode, String resultData, Bundle map,
            boolean abortBroadcast, int flags);*/
    void activityResumed(IBinder token);
    void activityIdle(IBinder token, in Configuration config, boolean stopProfiling);
    void activityPaused(IBinder token);
    /*void activityStopped(IBinder token, in Bundle state,
            in PersistableBundle persistentState, CharSequence description);*/
    void activitySlept(IBinder token);
    void activityDestroyed(IBinder token);
    void activityRelaunched(IBinder token);
    void reportSizeConfigurations(IBinder token, in int[] horizontalSizeConfiguration,
                                  in int[] verticalSizeConfigurations, in int[] smallestWidthConfigurations);
    String getCallingPackage(IBinder token);
    ComponentName getCallingActivity(IBinder token);
    // List<IAppTask> getAppTasks(String callingPackage);
    /*int addAppTask(IBinder activityToken, Intent intent,
            ActivityManager.TaskDescription description, Bitmap thumbnail);*/
    Point getAppTaskThumbnailSize();
    // List<RunningTaskInfo> getTasks(int maxNum, int flags);
    /*ParceledListSlice<ActivityManager.RecentTaskInfo> getRecentTasks(int maxNum,
            int flags, int userId);*/
    // ActivityManager.TaskThumbnail getTaskThumbnail(int taskId);
    // List<RunningServiceInfo> getServices(int maxNum, int flags);
    // List<ActivityManager.ProcessErrorStateInfo> getProcessesInErrorState();
    void moveTaskToFront(int task, int flags, in Bundle options);
    boolean moveActivityTaskToBack(IBinder token, boolean nonRoot);
    void moveTaskBackwards(int task);
    void moveTaskToStack(int taskId, int stackId, boolean toTop);
    boolean moveTaskToDockedStack(int taskId, int createMode, boolean toTop, boolean animate,
                                  in Rect initialBounds, boolean moveHomeStackFront);
    boolean moveTopActivityToPinnedStack(int stackId, in Rect bounds);

    /**
     * Resizes the input stack id to the given bounds.
     *
     * @param stackId Id of the stack to resize.
     * @param bounds Bounds to resize the stack to or {@code null} for fullscreen.
     * @param allowResizeInDockedMode True if the resize should be allowed when the docked stack is
     *                                active.
     * @param preserveWindows True if the windows of activities contained in the stack should be
     *                        preserved.
     * @param animate True if the stack resize should be animated.
     * @param animationDuration The duration of the resize animation in milliseconds or -1 if the
     *                          default animation duration should be used.
     * @throws RemoteException
     */
    void resizeStack(int stackId, in Rect bounds, boolean allowResizeInDockedMode,
                     boolean preserveWindows, boolean animate, int animationDuration);

    /**
     * Moves all tasks from the docked stack in the fullscreen stack and puts the top task of the
     * fullscreen stack into the docked stack.
     */
    void swapDockedAndFullscreenStack();

    /**
     * Resizes the docked stack, and all other stacks as the result of the dock stack bounds change.
     *
     * @param dockedBounds The bounds for the docked stack.
     * @param tempDockedTaskBounds The temporary bounds for the tasks in the docked stack, which
     *                             might be different from the stack bounds to allow more
     *                             flexibility while resizing, or {@code null} if they should be the
     *                             same as the stack bounds.
     * @param tempDockedTaskInsetBounds The temporary bounds for the tasks to calculate the insets.
     *                                  When resizing, we usually "freeze" the layout of a task. To
     *                                  achieve that, we also need to "freeze" the insets, which
     *                                  gets achieved by changing task bounds but not bounds used
     *                                  to calculate the insets in this transient state
     * @param tempOtherTaskBounds The temporary bounds for the tasks in all other stacks, or
     *                            {@code null} if they should be the same as the stack bounds.
     * @param tempOtherTaskInsetBounds Like {@code tempDockedTaskInsetBounds}, but for the other
     *                                 stacks.
     * @throws RemoteException
     */
    void resizeDockedStack(in Rect dockedBounds, in Rect tempDockedTaskBounds,
                           in Rect tempDockedTaskInsetBounds,
                           in Rect tempOtherTaskBounds, in Rect tempOtherTaskInsetBounds);
    /**
     * Resizes the pinned stack.
     *
     * @param pinnedBounds The bounds for the pinned stack.
     * @param tempPinnedTaskBounds The temporary bounds for the tasks in the pinned stack, which
     *                             might be different from the stack bounds to allow more
     *                             flexibility while resizing, or {@code null} if they should be the
     *                             same as the stack bounds.
     */
    void resizePinnedStack(in Rect pinnedBounds, in Rect tempPinnedTaskBounds);
    void positionTaskInStack(int taskId, int stackId, int position);
    // List<StackInfo> getAllStackInfos();
    // StackInfo getStackInfo(int stackId);
    boolean isInHomeStack(int taskId);
    void setFocusedStack(int stackId);
    int getFocusedStackId();
    void setFocusedTask(int taskId);
    void registerTaskStackListener(ITaskStackListener listener);
    int getTaskForActivity(IBinder token, boolean onlyRoot);
    /*ContentProviderHolder getContentProvider(IApplicationThread caller,
            String name, int userId, boolean stable);
    ContentProviderHolder getContentProviderExternal(String name, int userId, IBinder token);*/
    void removeContentProvider(IBinder connection, boolean stable);
    void removeContentProviderExternal(String name, IBinder token);
    // void publishContentProviders(IApplicationThread caller,List<ContentProviderHolder>
    // providers);
    boolean refContentProvider(IBinder connection, int stableDelta, int unstableDelta);
    void unstableProviderDied(IBinder connection);
    void appNotRespondingViaProvider(IBinder connection);
    PendingIntent getRunningServiceControlPanel(in ComponentName service);
    /*ComponentName startService(IApplicationThread caller, Intent service,
            String resolvedType, String callingPackage, int userId);
    int stopService(IApplicationThread caller, Intent service,
            String resolvedType, int userId);
    int bindService(IApplicationThread caller, IBinder token, Intent service,
            String resolvedType, IServiceConnection connection, int flags,
            String callingPackage, int userId);*/
    boolean stopServiceToken(in ComponentName className, IBinder token,
                             int startId);
    void setServiceForeground(in ComponentName className, IBinder token,
                              int id, in Notification notification, int flags);
    boolean unbindService(IServiceConnection connection);
    void publishService(IBinder token,
                        in Intent intent, IBinder service);
    void unbindFinished(IBinder token, in Intent service,
                        boolean doRebind);
    /* oneway */
    void serviceDoneExecuting(IBinder token, int type, int startId,
                              int res);
    IBinder peekService(in Intent service, String resolvedType, String callingPackage);

    boolean bindBackupAgent(String packageName, int backupRestoreMode, int userId);
    void clearPendingBackup();
    void backupAgentCreated(String packageName, IBinder agent);
    void unbindBackupAgent(in ApplicationInfo appInfo);
    void killApplicationProcess(String processName, int uid);

    boolean startInstrumentation(in ComponentName className, String profileFile,
                                 int flags, in Bundle arguments, IInstrumentationWatcher watcher,
                                 IUiAutomationConnection connection, int userId,
                                 String abiOverride);
    // void finishInstrumentation(IApplicationThread target, int resultCode, Bundle results);

    Configuration getConfiguration();
    void updateConfiguration(in Configuration values);
    void setRequestedOrientation(IBinder token,
                                 int requestedOrientation);
    int getRequestedOrientation(IBinder token);

    ComponentName getActivityClassForToken(IBinder token);
    String getPackageForToken(IBinder token);

    IIntentSender getIntentSender(int type,
                                  String packageName, IBinder token, String resultWho,
                                  int requestCode, in Intent[] intents, in String[] resolvedTypes,
                                  int flags, in Bundle options, int userId);
    void cancelIntentSender(IIntentSender sender);
    boolean clearApplicationUserData(String packageName,
                                                     IPackageDataObserver observer, int userId);
    String getPackageForIntentSender(IIntentSender sender);
    int getUidForIntentSender(IIntentSender sender);

    int handleIncomingUser(int callingPid, int callingUid, int userId, boolean allowAll,
                           boolean requireFull, String name, String callerPackage);

    void setProcessLimit(int max);
    int getProcessLimit();

    void setProcessForeground(IBinder token, int pid,
                              boolean isForeground);

    int checkPermission(String permission, int pid, int uid);
    int checkPermissionWithToken(String permission, int pid, int uid, IBinder callerToken);

    int checkUriPermission(in Uri uri, int pid, int uid, int mode, int userId,
                           IBinder callerToken);
    /*void grantUriPermission(IApplicationThread caller, String targetPkg, Uri uri,
            int mode, int userId);*/
    // void revokeUriPermission(IApplicationThread caller, Uri uri, int mode, int userId);
    void takePersistableUriPermission(in Uri uri, int modeFlags, int userId);
    void releasePersistableUriPermission(in Uri uri, int modeFlags, int userId);
    /*ParceledListSlice<UriPermission> getPersistedUriPermissions(
            String packageName, boolean incoming);*/

    // Gets the URI permissions granted to an arbitrary package.
    // NOTE: this is different from getPersistedUriPermissions(), which returns the URIs the package
    // granted to another packages (instead of those granted to it).
    // ParceledListSlice<UriPermission> getGrantedUriPermissions(String packageName, int userId);

    // Clears the URI permissions granted to an arbitrary package.
    void clearGrantedUriPermissions(String packageName, int userId);

    // void showWaitingForDebugger(IApplicationThread who, boolean waiting);

    // void getMemoryInfo(ActivityManager.MemoryInfo outInfo);

    void killBackgroundProcesses(String packageName, int userId);
    void killAllBackgroundProcesses();
    void killPackageDependents(String packageName, int userId);
    void forceStopPackage(String packageName, int userId);

    // Note: probably don't want to allow applications access to these.
    void setLockScreenShown(boolean showing, boolean occluded);

    void unhandledBack();
    ParcelFileDescriptor openContentUri(in Uri uri);
    void setDebugApp(String packageName, boolean waitForDebugger, boolean persistent);
    void setAlwaysFinish(boolean enabled);
    void setActivityController(IActivityController watcher, boolean imAMonkey);
    void setLenientBackgroundCheck(boolean enabled);
    int getMemoryTrimLevel();

    void enterSafeMode();

    void noteWakeupAlarm(IIntentSender sender, int sourceUid, String sourcePkg, String tag);
    void noteAlarmStart(IIntentSender sender, int sourceUid, String tag);
    void noteAlarmFinish(IIntentSender sender, int sourceUid, String tag);

    boolean killPids(in int[] pids, String reason, boolean secure);
    boolean killProcessesBelowForeground(String reason);

    // Special low-level communication with activity manager.
    /*void handleApplicationCrash(IBinder app,
            ApplicationErrorReport.CrashInfo crashInfo);
    boolean handleApplicationWtf(IBinder app, String tag, boolean system,
            ApplicationErrorReport.CrashInfo crashInfo);*/

    // A StrictMode violation to be handled.  The violationMask is a
    // subset of the original StrictMode policy bitmask, with only the
    // bit violated and penalty bits to be executed by the
    // ActivityManagerService remaining set.
    /*void handleApplicationStrictModeViolation(IBinder app, int violationMask,
            StrictMode.ViolationInfo crashInfo);*/

    /*
     * This will deliver the specified signal to all the persistent processes. Currently only
     * SIGUSR1 is delivered. All others are ignored.
     */
    void signalPersistentProcesses(int signal);
    // Retrieve running application processes in the system
    // List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses();
    // Retrieve info of applications installed on external media that are currently
    // running.
    List<ApplicationInfo> getRunningExternalApplications();
    // Get memory information about the calling process.
    // void getMyMemoryState(ActivityManager.RunningAppProcessInfo outInfo);
    // Get device configuration
    // ConfigurationInfo getDeviceConfigurationInfo();

    // Turn on/off profiling in a particular process.
    /*boolean profileControl(String process, int userId, boolean start,
            ProfilerInfo profilerInfo, int profileType);*/

    boolean shutdown(int timeout);

    void stopAppSwitches();
    void resumeAppSwitches();

    void addPackageDependency(String packageName);

    void killApplication(String pkg, int appId, int userId, String reason);

    void closeSystemDialogs(String reason);

    // Debug.MemoryInfo[] getProcessMemoryInfo(int[] pids);

    void overridePendingTransition(IBinder token, String packageName,
                                   int enterAnim, int exitAnim);

    boolean isUserAMonkey();

    void setUserIsMonkey(boolean monkey);

    void finishHeavyWeightApp();

    boolean convertFromTranslucent(IBinder token);
    // boolean convertToTranslucent(IBinder token, ActivityOptions options);
    void notifyActivityDrawn(IBinder token);
    // ActivityOptions getActivityOptions(IBinder token);

    void bootAnimationComplete();

    void setImmersive(IBinder token, boolean immersive);
    boolean isImmersive(IBinder token);
    boolean isTopActivityImmersive();
    boolean isTopOfTask(IBinder token);

    void crashApplication(int uid, int initialPid, String packageName,
                          String message);

    String getProviderMimeType(in Uri uri, int userId);

    IBinder newUriPermissionOwner(String name);
    IBinder getUriPermissionOwnerForActivity(IBinder activityToken);
    void grantUriPermissionFromOwner(IBinder owner, int fromUid, String targetPkg,
                                     in Uri uri, int mode, int sourceUserId, int targetUserId);
    void revokeUriPermissionFromOwner(IBinder owner, in Uri uri,
                                      int mode, int userId);

    int checkGrantUriPermission(int callingUid, String targetPkg, in Uri uri,
                                int modeFlags, int userId);

    // Cause the specified process to dump the specified heap.
    boolean dumpHeap(String process, int userId, boolean managed, String path,
                     in ParcelFileDescriptor fd);

    /*int startActivities(IApplicationThread caller, String callingPackage,
            Intent[] intents, String[] resolvedTypes, IBinder resultTo,
            Bundle options, int userId);*/

    int getFrontActivityScreenCompatMode();
    void setFrontActivityScreenCompatMode(int mode);
    int getPackageScreenCompatMode(String packageName);
    void setPackageScreenCompatMode(String packageName, int mode);
    boolean getPackageAskScreenCompat(String packageName);
    void setPackageAskScreenCompat(String packageName, boolean ask);

    // Multi-user APIs
    boolean switchUser(int userid);
    boolean startUserInBackground(int userid);
    boolean unlockUser(int userid, in byte[] token, in byte[] secret, IProgressListener listener);
    int stopUser(int userid, boolean force, IStopUserCallback callback);
    UserInfo getCurrentUser();
    boolean isUserRunning(int userid, int flags);
    int[] getRunningUserIds();

    boolean removeTask(int taskId);

    void registerProcessObserver(IProcessObserver observer);
    void unregisterProcessObserver(IProcessObserver observer);

    void registerUidObserver(IUidObserver observer, int which);
    void unregisterUidObserver(IUidObserver observer);

    boolean isIntentSenderTargetedToPackage(IIntentSender sender);

    boolean isIntentSenderAnActivity(IIntentSender sender);

    Intent getIntentForIntentSender(IIntentSender sender);

    String getTagForIntentSender(IIntentSender sender, String prefix);

    void updatePersistentConfiguration(in Configuration values);

    long[] getProcessPss(in int[]pids);

    void showBootMessage(CharSequence msg, boolean always);

    void keyguardWaitingForActivityDrawn();

    /**
     * Notify the system that the keyguard is going away.
     *
     * @param flags See {@link android.view.WindowManagerPolicy#KEYGUARD_GOING_AWAY_FLAG_TO_SHADE}
     *              etc.
     */
    void keyguardGoingAway(int flags);

    boolean shouldUpRecreateTask(IBinder token, String destAffinity);

    boolean navigateUpTo(IBinder token, in Intent target, int resultCode, in Intent resultData);

    // These are not because you need to be very careful in how you
    // manage your activity to make sure it is always the uid you expect.
    int getLaunchedFromUid(IBinder activityToken);
    String getLaunchedFromPackage(IBinder activityToken);

    void registerUserSwitchObserver(IUserSwitchObserver observer,
                                    String name);
    void unregisterUserSwitchObserver(IUserSwitchObserver observer);

    void requestBugReport(int bugreportType);

    long inputDispatchingTimedOut(int pid, boolean aboveSystem, String reason);

    Bundle getAssistContextExtras(int requestType);

    boolean requestAssistContextExtras(int requestType, IResultReceiver receiver,
                                       in Bundle receiverExtras,
                                       IBinder activityToken, boolean focused, boolean newSessionId);

    void reportAssistContextExtras(IBinder token, in Bundle extras,
                                   in AssistStructure structure, in AssistContent content, in Uri
                                   referrer);

    boolean launchAssistIntent(in Intent intent, int requestType, String hint, int userHandle,
                               in Bundle args);

    boolean isAssistDataAllowedOnCurrentActivity();

    boolean showAssistFromActivity(IBinder token, in Bundle args);

    void killUid(int appId, int userId, String reason);

    void hang(IBinder who, boolean allowRestart);

    void reportActivityFullyDrawn(IBinder token);

    void restart();

    void performIdleMaintenance();

    void sendIdleJobTrigger();

    /*IActivityContainer createVirtualActivityContainer(IBinder parentActivityToken,
            IActivityContainerCallback callback);*/

    // IActivityContainer createStackOnDisplay(int displayId);

    // void deleteActivityContainer(IActivityContainer container);

    int getActivityDisplayId(IBinder activityToken);

    void startSystemLockTaskMode(int taskId);

    // void startLockTaskModeWithTaskId(int taskId);

    void startLockTaskMode(IBinder token);

    void stopLockTaskMode();

    void stopSystemLockTaskMode();

    boolean isInLockTaskMode();

    int getLockTaskModeState();

    void showLockTaskEscapeMessage(IBinder token);

    // void setTaskDescription(IBinder token, ActivityManager.TaskDescription values);
    void setTaskResizeable(int taskId, int resizeableMode);
    void resizeTask(int taskId, in Rect bounds, int resizeMode);

    Rect getTaskBounds(int taskId);
    Bitmap getTaskDescriptionIcon(String filename, int userId);

    // void startInPlaceAnimationOnFrontMostApplication(ActivityOptions opts);

    boolean requestVisibleBehind(IBinder token, boolean visible);
    boolean isBackgroundVisibleBehind(IBinder token);
    void backgroundResourcesReleased(IBinder token);

    void notifyLaunchTaskBehindComplete(IBinder token);
    void notifyEnterAnimationComplete(IBinder token);

    void notifyCleartextNetwork(int uid, in byte[] firstPacket);

    void setDumpHeapDebugLimit(String processName, int uid, long maxMemSize,
                               String reportPackage);
    void dumpHeapFinished(String path);

    void setVoiceKeepAwake(IVoiceInteractionSession session, boolean keepAwake);
    void updateLockTaskPackages(int userId, in String[] packages);
    void updateDeviceOwner(String packageName);

    int getPackageProcessState(String packageName, String callingPackage);

    boolean setProcessMemoryTrimLevel(String process, int uid, int level);

    boolean isRootVoiceInteraction(IBinder token);

    // Start Binder transaction tracking for all applications.
    boolean startBinderTracking();

    // Stop Binder transaction tracking for all applications and dump trace data to the given file
    // descriptor.
    boolean stopBinderTrackingAndDump(in ParcelFileDescriptor fd);

    int getActivityStackId(IBinder token);
    void exitFreeformMode(IBinder token);

    void suppressResizeConfigChanges(boolean suppress);

    void moveTasksToFullscreenStack(int fromStackId, boolean onTop);

    int getAppStartMode(int uid, String packageName);

    boolean isInMultiWindowMode(IBinder token);

    boolean isInPictureInPictureMode(IBinder token);

    void enterPictureInPictureMode(IBinder token);

    int setVrMode(IBinder token, boolean enabled, in ComponentName packageName);

    boolean isVrModePackageEnabled(in ComponentName packageName);

    boolean isAppForeground(int uid);

    void startLocalVoiceInteraction(IBinder token, in Bundle options);

    void stopLocalVoiceInteraction(IBinder token);

    boolean supportsLocalVoiceInteraction();

    void notifyPinnedStackAnimationEnded();

    void removeStack(int stackId);

    void notifyLockedProfile(int userId);

    void startConfirmDeviceCredentialIntent(in Intent intent);

    int sendIntentSender(IIntentSender target, int code, in Intent intent, String resolvedType,
                         IIntentReceiver finishedReceiver, String requiredPermission, in Bundle options);

    void setVrThread(int tid);
    void setRenderThread(int tid);

    /**
     * Lets activity manager know whether the calling process is currently showing "top-level" UI
     * that is not an activity, i.e. windows on the screen the user is currently interacting with.
     *
     * <p>This flag can only be set for persistent processes.
     *
     * @param hasTopUi Whether the calling process has "top-level" UI.
     */
    void setHasTopUi(boolean hasTopUi);

    //*********************************IActivityManager.java*********************************//

}
