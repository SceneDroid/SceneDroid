package main.java.analyze.utils;

/*-
 * #%L
 * RAICC
 * 
 * %%
 * Copyright (C) 2020 Jordan Samhi
 * University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - TruX - All rights reserved
 *
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

public class RAICCConstants {

	/**
	 * Classes
	 */
	public static final String ANDROID_APP_ALARMMANAGER = "android.app.AlarmManager";
	public static final String ANDROID_CONTENT_CONTEXT = "android.content.Context";
	public static final String ANDROID_APP_ACTIVITY = "android.app.Activity";

	/**
	 * Method signatures
	 */
	// package intent into a PendingIntent
	public static final String ANDROID_APP_PENDING_INTENT_GET_ACTIVITY = "<android.app.PendingIntent: android.app.PendingIntent getActivity(android.content.Context,int,android.content.Intent,int)>";
	public static final String ANDROID_APP_PENDING_INTENT_GET_SERVICE = "<android.app.PendingIntent: android.app.PendingIntent getService(android.content.Context,int,android.content.Intent,int)>";
	public static final String ANDROID_APP_PENDING_INTENT_GET_BROADCAST = "<android.app.PendingIntent: android.app.PendingIntent getBroadcast(android.content.Context,int,android.content.Intent,int)>";

	public static final String ANDROID_APP_PENDING_INTENT_GET_INTENTSENDER = "<android.app.PendingIntent: android.content.IntentSender getIntentSender(android.app.PendingIntent)>";

	// send PendingIntent
	public static final String ANDROID_APP_ACTIVITYOPTIONS_REQUESTUSAGETIMEREPORT = "<android.app.ActivityOptions: void requestUsageTimeReport(android.app.PendingIntent)>";
	public static final String ANDROID_APP_ALARMMANAGER_SETALARMCLOCK = "<android.app.AlarmManager: void setAlarmClock(android.app.AlarmManager$AlarmClockInfo,android.app.PendingIntent)>";
	public static final String ANDROID_APP_ALARMMANAGER_SETANDALLOWWHILEIDLE = "<android.app.AlarmManager: void setAndAllowWhileIdle(int,long,android.app.PendingIntent)>";
	public static final String ANDROID_APP_ALARMMANAGER_SETEXACTANDALLOWWHILEIDLE = "<android.app.AlarmManager: void setExactAndAllowWhileIdle(int,long,android.app.PendingIntent)>";
	public static final String ANDROID_APP_ALARMMANAGER_SETEXACT = "<android.app.AlarmManager: void setExact(int,long,android.app.PendingIntent)>";
	public static final String ANDROID_APP_ALARMMANAGER_SETINEXACTREPEATING = "<android.app.AlarmManager: void setInexactRepeating(int,long,long,android.app.PendingIntent)>";
	public static final String ANDROID_APP_ALARMMANAGER_SET = "<android.app.AlarmManager: void set(int,long,android.app.PendingIntent)>";
	public static final String ANDROID_APP_ALARMMANAGER_SETREPEATING = "<android.app.AlarmManager: void setRepeating(int,long,long,android.app.PendingIntent)>";
	public static final String ANDROID_APP_ALARMMANAGER_SETWINDOW = "<android.app.AlarmManager: void setWindow(int,long,long,android.app.PendingIntent)>";

	public static final String ANDROID_APP_NOTIFICATION$ACTION$BUILDER_1 = "<android.app.Notification$Action$Builder: void <init>(android.graphics.drawable.Icon,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_1 = "<android.support.v4.app.NotificationCompat$Action$Builder: void <init>(android.graphics.drawable.Icon,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATION$ACTION$BUILDER_2 = "<android.app.Notification$Action$Builder: void <init>(int,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_2 = "<android.support.v4.app.NotificationCompat$Action$Builder: void <init>(int,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATION$BUBBLEMETADATA$BUILDER_SETDELETEINTENT = "<android.app.Notification$BubbleMetadata$Builder: android.app.Notification$BubbleMetadata$Builder setDeleteIntent(android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATIONCOMPAT$BUBBLEMETADATA$BUILDER_SETDELETEINTENT = "<android.support.v4.app.NotificationCompat$BubbleMetadata$Builder: android.support.v4.app.NotificationCompat$BubbleMetadata$Builder setDeleteIntent(android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATION$BUBBLEMETADATA$BUILDER_SETINTENT = "<android.app.Notification$BubbleMetadata$Builder: android.app.Notification$BubbleMetadata$Builder setIntent(android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATIONCOMPAT$BUBBLEMETADATA$BUILDER_SETINTENT = "<android.support.v4.app.NotificationCompat$BubbleMetadata$Builder: android.support.v4.app.NotificationCompat$BubbleMetadata$Builder setIntent(android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATION$BUILDER_ADDACTION = "<android.app.Notification$Builder: android.app.Notification$Builder addAction(int,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATIONCOMPAT$BUILDER_ADDACTION = "<android.support.v4.app.NotificationCompat$Builder: android.support.v4.app.NotificationCompat$Builder addAction(int,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATION$BUILDER_SETDELETEINTENT = "<android.app.Notification$Builder: android.app.Notification$Builder setDeleteIntent(android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATIONCOMPAT$BUILDER_SETDELETEINTENT = "<android.support.v4.app.NotificationCompat$Builder: android.support.v4.app.NotificationCompat$Builder setDeleteIntent(android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATION$BUILDER_SETFULLSCREENINTENT = "<android.app.Notification$Builder: android.app.Notification$Builder setFullScreenIntent(android.app.PendingIntent,boolean)>";
	public static final String ANDROID_APP_NOTIFICATIONCOMPAT$BUILDER_SETFULLSCREENINTENT = "<android.support.v4.app.NotificationCompat$Builder: android.support.v4.app.NotificationCompat$Builder setFullScreenIntent(android.app.PendingIntent,boolean)>";
	public static final String ANDROID_APP_NOTIFICATIONCOMPAT$BUILDER_SETCONTENTINTENT1 = "<android.support.v4.app.NotificationCompat$Builder: android.support.v4.app.NotificationCompat$Builder setContentIntent(android.app.PendingIntent)";
	public static final String ANDROID_APP_NOTIFICATIONCOMPAT$BUILDER_SETCONTENTINTENT2 = "<android.app.Notification$Builder: android.app.Notification$Builder setContentIntent(android.app.PendingIntent)>";

	public static final String ANDROID_APP_NOTIFICATION$CAREXTENDER$BUILDER_SETREADPENDINGINTENT = "<android.app.Notification$CarExtender$Builder: android.app.Notification$CarExtender$Builder setReadPendingIntent(android.app.PendingIntent)>";
	public static final String ANDROID_APP_NOTIFICATION$CAREXTENDER$BUILDER_SETREPLYACTION = "<android.app.Notification$CarExtender$Builder: android.app.Notification$CarExtender$Builder setReplyAction(android.app.PendingIntent,android.app.RemoteInput)>";
	public static final String ANDROID_APP_NOTIFICATION$WEARABLEEXTENDER_SETDISPLAYINTENT = "<android.app.Notification$WearableExtender: android.app.Notification$WearableExtender setDisplayIntent(android.app.PendingIntent)>";
	public static final String ANDROID_APP_SLICE_SLICE$BUILDER_ADDACTION = "<android.app.slice.Slice$Builder: android.app.slice.Slice$Builder addAction(android.app.PendingIntent,android.app.slice.Slice,java.lang.String)>";
	public static final String ANDROID_BLUETOOTH_LE_BLUETOOTHLESCANNER_STARTSCAN = "<android.bluetooth.le.BluetoothLeScanner: int startScan(java.util.List<android.bluetooth.le.ScanFilter>,android.bluetooth.le.ScanSettings,android.app.PendingIntent)>";
	public static final String ANDROID_LOCATION_LOCATIONMANAGER_ADDPROXIMITYALERT = "<android.location.LocationManager: void addProximityAlert(double,double,float,long,android.app.PendingIntent)>";
	public static final String ANDROID_LOCATION_LOCATIONMANAGER_REQUESTLOCATIONUPDATES_1 = "<android.location.LocationManager: void requestLocationUpdates(java.lang.String,long,float,android.app.PendingIntent)>";
	public static final String ANDROID_LOCATION_LOCATIONMANAGER_REQUESTLOCATIONUPDATES_2 = "<android.location.LocationManager: void requestLocationUpdates(long,float,android.location.Criteria,android.app.PendingIntent)>";
	public static final String ANDROID_LOCATION_LOCATIONMANAGER_REQUESTSINGLEUPDATE_1 = "<android.location.LocationManager: void requestSingleUpdate(android.location.Criteria,android.app.PendingIntent)>";
	public static final String ANDROID_LOCATION_LOCATIONMANAGER_REQUESTSINGLEUPDATE_2 = "<android.location.LocationManager: void requestSingleUpdate(java.lang.String,android.app.PendingIntent)>";
	public static final String ANDROID_MEDIA_AUDIOMANAGER_REGISTERMEDIABUTTONEVENTRECEIVER = "<android.media.AudioManager: void registerMediaButtonEventReceiver(android.app.PendingIntent)>";
	public static final String ANDROID_MEDIA_SESSION_MEDIASESSION_SETMEDIABUTTONRECEIVER = "<android.media.session.MediaSession: void setMediaButtonReceiver(android.app.PendingIntent)>";
	public static final String ANDROID_MEDIA_SESSION_MEDIASESSION_SETSESSIONACTIVITY = "<android.media.session.MediaSession: void setSessionActivity(android.app.PendingIntent)>";
	public static final String ANDROID_NET_CONNECTIVITYMANAGER_REGISTERNETWORKCALLBACK = "<android.net.ConnectivityManager: void registerNetworkCallback(android.net.NetworkRequest,android.app.PendingIntent)>";
	public static final String ANDROID_NET_CONNECTIVITYMANAGER_REQUESTNETWORK = "<android.net.ConnectivityManager: void requestNetwork(android.net.NetworkRequest,android.app.PendingIntent)>";
	public static final String ANDROID_NET_SIP_SIPMANAGER_OPEN = "<android.net.sip.sipmanager: void open(android.net.sip.SipProfile,android.app.PendingIntent,android.net.sip.SipRegistrationListener)>";
	public static final String ANDROID_NET_VPNSERVICE_BUILDER_SETCONFIGUREINTENT = "<android.net.VpnService.Builder: android.net.VpnService.Builder setConfigureIntent(android.app.PendingIntent)>";
	public static final String ANDROID_NFC_NFCADAPTER_ENABLEFOREGROUNDDISPATCH = "<android.nfc.NfcAdapter: void enableForegroundDispatch(android.app.Activity,android.app.PendingIntent,android.content.IntentFilter[],java.lang.String[][])>";
	public static final String ANDROID_PRINT_PRINTERINFO_BUILDER_SETINFOINTENT = "<android.print.PrinterInfo.Builder: android.print.PrinterInfo.Builder setInfoIntent(android.app.PendingIntent)>";
	public static final String ANDROID_SUPPORT_V4_APP_ACTIVITYOPTIONSCOMPAT_REQUESTUSAGETIMEREPORT = "<android.support.v4.app.ActivityOptionsCompat: void requestUsageTimeReport(android.app.PendingIntent)>";
	public static final String ANDROID_SUPPORT_V4_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_1 = "<android.support.v4.app.NotificationCompat$Action$Builder: void <init>(int,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String ANDROID_SUPPORT_V4_APP_NOTIFICATIONCOMPAT$ACTION_1 = "<android.support.v4.app.NotificationCompat$Action: void <init>(int,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String ANDROID_SUPPORT_V4_APP_NOTIFICATIONCOMPAT$BUILDER_ADDACTION = "<android.support.v4.app.NotificationCompat$Builder: android.support.v4.app.NotificationCompat$Builder addAction(int,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String ANDROID_SUPPORT_V4_APP_NOTIFICATIONCOMPAT$BUILDER_SETFULLSCREENINTENT = "<android.support.v4.app.NotificationCompat$Builder: android.support.v4.app.NotificationCompat$Builder setFullScreenIntent(android.app.PendingIntent,boolean)>";
	public static final String ANDROID_SUPPORT_V4_MEDIA_APP_NOTIFICATIONCOMPAT$MEDIASTYLE_SETCANCELBUTTONINTENT = "<android.support.v4.media.app.NotificationCompat$MediaStyle: android.support.v4.media.app.NotificationCompat$MediaStyle setCancelButtonIntent(android.app.PendingIntent)>";
	public static final String ANDROID_SUPPORT_V4_MEDIA_SESSION_MEDIASESSIONCOMPAT_SETMEDIABUTTONRECEIVER = "<android.support.v4.media.session.MediaSessionCompat: void setMediaButtonReceiver(android.app.PendingIntent)>";
	public static final String ANDROID_SUPPORT_V4_MEDIA_SESSION_MEDIASESSIONCOMPAT_SETSESSIONACTIVITY = "<android.support.v4.media.session.MediaSessionCompat: void setSessionActivity(android.app.PendingIntent)>";
	public static final String ANDROID_TELEPHONY_EUICC_EUICCMANAGER_DOWNLOADSUBSCRIPTION = "<android.telephony.euicc.EuiccManager: void downloadSubscription(android.telephony.euicc.DownloadableSubscription,boolean,android.app.PendingIntent)>";
	public static final String ANDROID_TELEPHONY_EUICC_EUICCMANAGER_STARTRESOLUTIONACTIVITY = "<android.telephony.euicc.EuiccManager: void startResolutionActivity(android.app.Activity,int,android.content.Intent,android.app.PendingIntent)>";
	public static final String ANDROID_TELEPHONY_EUICC_EUICCMANAGER_SWITCHTOSUBSCRIPTION = "<android.telephony.euicc.EuiccManager: void switchToSubscription(int,android.app.PendingIntent)>";
	public static final String ANDROID_TELEPHONY_EUICC_EUICCMANAGER_UPDATESUBSCRIPTIONNICKNAME = "<android.telephony.euicc.EuiccManager: void updateSubscriptionNickname(int,java.lang.String,android.app.PendingIntent)>";
	public static final String ANDROID_TELEPHONY_SMSMANAGER_CREATEAPPSPECIFICSMSTOKENWITHPACKAGEINFO = "<android.telephony.SmsManager: java.lang.String createAppSpecificSmsTokenWithPackageInfo(java.lang.String,android.app.PendingIntent)>";
	public static final String ANDROID_TELEPHONY_SMSMANAGER_DOWNLOADMULTIMEDIAMESSAGE = "<android.telephony.SmsManager: void downloadMultimediaMessage(android.content.Context,java.lang.String,android.net.Uri,android.os.Bundle,android.app.PendingIntent)>";
	public static final String ANDROID_TELEPHONY_SMSMANAGER_INJECTSMSPDU = "<android.telephony.SmsManager: void injectSmsPdu(byte[],java.lang.String,android.app.PendingIntent)>";
	public static final String ANDROID_TELEPHONY_SMSMANAGER_SENDDATAMESSAGE = "<android.telephony.SmsManager: void sendDataMessage(java.lang.String,java.lang.String,short,byte[],android.app.PendingIntent,android.app.PendingIntent)>";
	public static final String ANDROID_TELEPHONY_SMSMANAGER_SENDMULTIMEDIAMESSAGE = "<android.telephony.SmsManager: void sendMultimediaMessage(android.content.Context,android.net.Uri,java.lang.String,android.os.Bundle,android.app.PendingIntent)>";
	public static final String ANDROID_TELEPHONY_SMSMANAGER_SENDTEXTMESSAGE_1 = "<android.telephony.SmsManager: void sendTextMessage(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)>";
	public static final String ANDROID_TELEPHONY_SMSMANAGER_SENDTEXTMESSAGE_2 = "<android.telephony.SmsManager: void sendTextMessage(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)>";
	public static final String ANDROID_TELEPHONY_SMSMANAGER_SENDTEXTMESSAGEWITHOUTPERSISTING = "<android.telephony.SmsManager: void sendTextMessageWithoutPersisting(java.lang.String,java.lang.String,java.lang.String,android.app.PendingIntent,android.app.PendingIntent)>";
	public static final String ANDROID_WIDGET_REMOTEVIEWS_SETONCLICKPENDINGINTENT = "<android.widget.RemoteViews: void setOnClickPendingIntent(int,android.app.PendingIntent)>";
	public static final String ANDROID_WIDGET_REMOTEVIEWS_SETREMOTEADAPTER = "<android.widget.RemoteViews: void setRemoteAdapter(int,android.content.Intent)";
	public static final String ANDROID_WIDGET_REMOTEVIEWS_SETPENDINGINTENTTEMPLATE = "<android.widget.RemoteViews: void setPendingIntentTemplate(int,android.app.PendingIntent)>";
	public static final String ANDROIDX_BROWSER_BROWSERACTIONS_BROWSERACTIONITEM = "<androidx.browser.browseractions.BrowserActionItem: void <init>(java.lang.String,android.app.PendingIntent,int)>";
	public static final String ANDROIDX_BROWSER_BROWSERACTIONS_BROWSERACTIONSINTENT$BUILDER_SETONITEMSELECTEDACTION = "<androidx.browser.browseractions.BrowserActionsIntent$Builder: androidx.browser.browseractions.BrowserActionsIntent$Builder setOnItemSelectedAction(android.app.PendingIntent)>";
	public static final String ANDROIDX_CORE_APP_ALARMMANAGERCOMPAT_SETEXACT = "<androidx.core.app.AlarmManagerCompat: void setExact(android.app.AlarmManager,int,long,android.app.PendingIntent)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_1 = "<androidx.core.app.NotificationCompat$Action$Builder: void <init>(androidx.core.graphics.drawable.IconCompat,java.lang.CharSequence,android.app.PendingIntent,android.os.Bundle,androidx.core.app.RemoteInput[],boolean,int,boolean,boolean)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_2 = "<androidx.core.app.NotificationCompat$Action$Builder: void <init>(int,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_3 = "<androidx.core.app.NotificationCompat$Action$Builder: void <init>(int,java.lang.CharSequence,android.app.PendingIntent,android.os.Bundle,androidx.core.app.RemoteInput[],boolean,int,boolean)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION$BUILDER_4 = "<androidx.core.app.NotificationCompat$Action$Builder: void <init>(int,java.lang.CharSequence,android.app.PendingIntent,android.os.Bundle,androidx.core.app.RemoteInput[],boolean,int,boolean,boolean)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION_1 = "<androidx.core.app.NotificationCompat$Action: void <init>(androidx.core.graphics.drawable.IconCompat,java.lang.CharSequence,android.app.PendingIntent,android.os.Bundle,androidx.core.app.RemoteInput[],androidx.core.app.RemoteInput[],boolean,int,boolean,boolean)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION_2 = "<androidx.core.app.NotificationCompat$Action: void <init>(int,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION_3 = "<androidx.core.app.NotificationCompat$Action: void <init>(int,java.lang.CharSequence,android.app.PendingIntent,android.os.Bundle,androidx.core.app.RemoteInput[],androidx.core.app.RemoteInput[],boolean,int,boolean)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$ACTION_4 = "<androidx.core.app.NotificationCompat$Action: void <init>(int,java.lang.CharSequence,android.app.PendingIntent,android.os.Bundle,androidx.core.app.RemoteInput[],androidx.core.app.RemoteInput[],boolean,int,boolean,boolean)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUBBLEMETADATA$BUILDER_SETDELETEINTENT = "<androidx.core.app.NotificationCompat$BubbleMetadata$Builder: androidx.core.app.NotificationCompat$BubbleMetadata$Builder setDeleteIntent(android.app.PendingIntent)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUBBLEMETADATA$BUILDER_SETINTENT = "<androidx.core.app.NotificationCompat$BubbleMetadata$Builder: androidx.core.app.NotificationCompat$BubbleMetadata$Builder setIntent(android.app.PendingIntent)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUBBLEMETADATA_1 = "<androidx.core.app.NotificationCompat$BubbleMetadata: void <init>(android.app.PendingIntent,android.app.PendingIntent,androidx.core.graphics.drawable.IconCompat,int,int,int)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUBBLEMETADATA_2 = "<androidx.core.app.NotificationCompat$BubbleMetadata: void <init>(android.app.PendingIntent,android.app.PendingIntent,androidx.core.graphics.drawable.IconCompat,int,int,int,androidx.core.app.NotificationCompat$1)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUILDER_ADDACTION = "<androidx.core.app.NotificationCompat$Builder: androidx.core.app.NotificationCompat$Builder addAction(int,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$CAREXTENDER$UNREADCONVERSATION = "<androidx.core.app.NotificationCompat$CarExtender$UnreadConversation: void <init>(java.lang.String[],androidx.core.app.RemoteInput,android.app.PendingIntent,android.app.PendingIntent,java.lang.String[],long)>";
	public static final String ANDROID_APP_NOTIFICATION_SETLATESTEVENTINFO = "<android.app.Notification: void setLatestEventInfo(android.content.Context,java.lang.CharSequence,java.lang.CharSequence,android.app.PendingIntent)>";
	public static final String COM_GOOGLE_ANDROID_GMS_LOCATION_ACTIVITYRECOGNITIONAPI_REQUESTACTIVITYUPDATES = "<com.google.android.gms.location.ActivityRecognitionApi: com.google.android.gms.common.api.PendingResult requestActivityUpdates(com.google.android.gms.common.api.GoogleApiClient,long,android.app.PendingIntent)>";
	public static final String COM_GOOGLE_ANDROID_GMS_LOCATION_FUSEDLOCATIONPROVIDERAPI_REQUESTLOCATIONUPDATES = "<com.google.android.gms.location.FusedLocationProviderApi: com.google.android.gms.common.api.PendingResult requestLocationUpdates(com.google.android.gms.common.api.GoogleApiClient,com.google.android.gms.location.LocationRequest,android.app.PendingIntent)>";
	public static final String COM_GOOGLE_ANDROID_GMS_LOCATION_FUSEDLOCATIONPROVIDERCLIENT_REQUESTLOCATIONUPDATES = "<com.google.android.gms.location.FusedLocationProviderClient: com.google.android.gms.tasks.Task requestLocationUpdates(com.google.android.gms.location.LocationRequest,android.app.PendingIntent)>";
	public static final String COM_GOOGLE_ANDROID_GMS_LOCATION_GEOFENCINGAPI_ADDGEOFENCES = "<com.google.android.gms.location.GeofencingApi: com.google.android.gms.common.api.PendingResult addGeofences(com.google.android.gms.common.api.GoogleApiClient,java.util.List,android.app.PendingIntent)>";
	public static final String COM_GOOGLE_ANDROID_GMS_LOCATION_LOCATIONCLIENT_ADDGEOFENCES = "<com.google.android.gms.location.LocationClient: void addGeofences(java.util.List,android.app.PendingIntent,com.google.android.gms.location.LocationClient$OnAddGeofencesResultListener)>";
	public static final String COM_GOOGLE_ANDROID_VENDING_EXPANSION_DOWNLOADER_DOWNLOADERCLIENTMARSHALLER_STARTDOWNLOADSERVICEIFREQUIRED = "<com.google.android.vending.expansion.downloader.DownloaderClientMarshaller: int startDownloadServiceIfRequired(android.content.Context,android.app.PendingIntent,java.lang.Class)>";
	public static final String COM_GOOGLE_VR_NDK_BASE_DAYDREAMAPI_LAUNCHINVR = "<com.google.vr.ndk.base.DaydreamApi: void launchInVr(android.app.Activity,android.app.PendingIntent)>";
	public static final String COM_GOOGLE_VR_NDK_BASE_DAYDREAMAPI_LAUNCHINVRFORRESULT = "<com.google.vr.ndk.base.DaydreamApi: void launchInVrForResult(android.app.Activity,android.app.PendingIntent,int)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUILDER_SETCONTENTINTENT = "<androidx.core.app.NotificationCompat$Builder: androidx.core.app.NotificationCompat$Builder setContentIntent(android.app.PendingIntent)>";
	public static final String ANDROIDX_CORE_APP_NOTIFICATIONCOMPAT$BUILDER_SETDELETEINTENT = "<androidx.core.app.NotificationCompat$Builder: androidx.core.app.NotificationCompat$Builder setDeleteIntent(android.app.PendingIntent)>";

	// send IntentSender
	public static final String ANDROID_CONTENT_PM_PACKAGEINSTALLER$SESSION_COMMIT = "<android.content.pm.PackageInstaller$Session: void commit(android.content.IntentSender)>";
	public static final String ANDROID_CONTENT_PM_PACKAGEINSTALLER_INSTALLEXISTINGPACKAGE = "<android.content.pm.PackageInstaller: void installExistingPackage(java.lang.String,int,android.content.IntentSender)>";
	public static final String ANDROID_COMPANION_COMPANIONDEVICEMANAGER$CALLBACK_ONDEVICEFOUND = "<android.companion.CompanionDeviceManager$Callback: void onDeviceFound(android.content.IntentSender)>";
	public static final String ANDROID_CONTENT_INTENTSENDER$ONFINISHED_ONSENDFINISHED = "<android.content.IntentSender$OnFinished: void onSendFinished(android.content.IntentSender,android.content.Intent,int,java.lang.String,android.os.Bundle)>";
	public static final String ANDROID_APP_FRAGMENTHOSTCALLBACK_ONSTARTINTENTSENDERFROMFRAGMENT = "<android.app.FragmentHostCallback: void onStartIntentSenderFromFragment(android.app.Fragment,android.content.IntentSender,int,android.content.Intent,int,int,int,android.os.Bundle)>";
	public static final String ANDROID_SERVICE_AUTOFILL_SAVECALLBACK_ONSUCCESS = "<android.service.autofill.SaveCallback: void onSuccess(android.content.IntentSender)>";
	public static final String ANDROIDX_CORE_CONTENT_PM_SHORTCUTMANAGERCOMPAT_REQUESTPINSHORTCUT = "<androidx.core.content.pm.ShortcutManagerCompat: boolean requestPinShortcut(android.content.pm.ShortcutInfo,android.content.IntentSender)>";
	public static final String ANDROID_SERVICE_AUTOFILL_FILLRESPONSE$BUILDER_SETAUTHENTICATION_1 = "<android.service.autofill.FillResponse$Builder: android.service.autofill.FillResponse$Builder setAuthentication(android.content.IntentSender)>";
	public static final String ANDROID_SERVICE_AUTOFILL_FILLRESPONSE$BUILDER_SETAUTHENTICATION_2 = "<android.service.autofill.FillResponse$Builder: android.service.autofill.FillResponse$Builder setAuthentication(android.view.autofill.AutofillId[],android.content.IntentSender,android.widget.RemoteViews)>";
	public static final String ANDROID_CONTENT_CONTEXT_STARTINTENTSENDER_1 = "<android.content.Context: void startIntentSender(android.content.IntentSender,android.content.Intent,int,int,int)>";
	public static final String ANDROID_CONTENT_CONTEXT_STARTINTENTSENDER_2 = "<android.content.Context: void startIntentSender(android.content.IntentSender,android.content.Intent,int,int,int,android.os.Bundle)>";
	public static final String ANDROID_APP_ACTIVITY_STARTINTENTSENDERFORRESULT_1 = "void startIntentSenderForResult(android.content.IntentSender,int,android.content.Intent,int,int,int)>";
	public static final String ANDROID_APP_ACTIVITY_STARTINTENTSENDERFORRESULT_2 = "void startIntentSenderForResult(android.content.IntentSender,int,android.content.Intent,int,int,int,android.os.Bundle)>";
	public static final String START_INTENTSENDER_FOR_RESULT_1 = "void startIntentSenderForResult(android.content.IntentSender,int,android.content.Intent,int,int,int)";
	public static final String START_INTENTSENDER_FOR_RESULT_2 = "void startIntentSenderForResult(android.content.IntentSender,int,android.content.Intent,int,int,int,android.os.Bundle)";
	public static final String ANDROID_APP_ACTIVITY_STARTINTENTSENDERFROMCHILD_1 = "<android.app.Activity: void startIntentSenderFromChild(android.app.Activity,android.content.IntentSender,int,android.content.Intent,int,int,int)>";
	public static final String ANDROID_APP_ACTIVITY_STARTINTENTSENDERFROMCHILD_2 = "<android.app.Activity: void startIntentSenderFromChild(android.app.Activity,android.content.IntentSender,int,android.content.Intent,int,int,int,android.os.Bundle)>";
	public static final String ANDROID_CONTENT_PM_PACKAGEINSTALLER_UNINSTALL_1 = "<android.content.pm.PackageInstaller: void uninstall(android.content.pm.VersionedPackage,android.content.IntentSender)>";
	public static final String ANDROID_CONTENT_PM_PACKAGEINSTALLER_UNINSTALL_2 = "<android.content.pm.PackageInstaller: void uninstall(java.lang.String,android.content.IntentSender)>";

	// send intent in PendingIntent and IntentSender. Note, not use!!!!
	public static final String ANDROID_APP_PENDINGINTENT_SEND_1 = "<android.app.PendingIntent: void send(android.content.Context,int,android.content.Intent,android.app.PendingIntent$OnFinished,android.os.Handler,java.lang.String,android.os.Bundle)>";
	public static final String ANDROID_APP_PENDINGINTENT_SEND_2 = "<android.app.PendingIntent: void send(android.content.Context,int,android.content.Intent,android.app.PendingIntent$OnFinished,android.os.Handler,java.lang.String)>";
	public static final String ANDROID_APP_PENDINGINTENT_SEND_3 = "<android.app.PendingIntent: void send(android.content.Context,int,android.content.Intent,android.app.PendingIntent$OnFinished,android.os.Handler)>";
	public static final String ANDROID_APP_PENDINGINTENT_SEND_4 = "<android.app.PendingIntent: void send(android.content.Context,int,android.content.Intent)>";
	public static final String ANDROID_APP_PENDINGINTENT_SEND_5 = "<android.app.PendingIntent: void send(int,android.content.Intent,android.app.PendingIntent$OnFinished,android.os.Handler)>";
	public static final String ANDROID_APP_PENDINGINTENT_SEND_6 = "<android.app.PendingIntent: void send()>";
	public static final String ANDROID_APP_PENDINGINTENT_SEND_7 = "<android.app.PendingIntent: void send(int)>";
	public static final String ANDROID_CONTENT_INTENTSENDER_SENDINTENT_1 = "<android.content.IntentSender: void sendIntent(android.content.Context,int,android.content.Intent,android.content.IntentSender$OnFinished,android.os.Handler)>";
	public static final String ANDROID_CONTENT_INTENTSENDER_SENDINTENT_2 = "<android.content.IntentSender: void sendIntent(android.content.Context,int,android.content.Intent,android.content.IntentSender$OnFinished,android.os.Handler,java.lang.String)>";

	// query from PackageManager
	public static final String ANDROID_queryBroadcastReceivers = "<android.content.pm.PackageManager: java.util.List queryBroadcastReceivers(android.content.Intent,int)>";
	public static final String ANDROID_queryIntentActivities = "<android.content.pm.PackageManager: java.util.List queryIntentActivities(android.content.Intent,int)>";
	public static final String ANDROID_queryIntentServices = "<android.content.pm.PackageManager: java.util.List queryIntentServices(android.content.Intent,int)>";
}
