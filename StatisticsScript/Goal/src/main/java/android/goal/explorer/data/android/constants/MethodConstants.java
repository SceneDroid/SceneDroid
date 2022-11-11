package android.goal.explorer.data.android.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Class containing constants for the well-known Android lifecycle methods
 */
public class MethodConstants {
    public static class Application {
        public static final String APPLICATION_ONCREATE = "void onCreate()";
        public static final String APPLICATION_ONTERMINATE = "void onTerminate()";

        private static final String[] applicationMethods = {APPLICATION_ONCREATE, APPLICATION_ONTERMINATE};
        private static final List<String> applicationMethodList = Arrays.asList(applicationMethods);

        public static List<String> getApplicationLifecycleMethods() {
            return applicationMethodList;
        }
    }

    public static class Activity {
        /*
        Activity
         */
        // before running
        public static final String ACTIVITY_ONCREATE = "void onCreate(android.os.Bundle)";
        public static final String ACTIVITY_ONPOSTCREATE = "void onPostCreate(android.os.Bundle)";
        public static final String ACTIVITY_ONSTART = "void onStart()";
        public static final String ACTIVITY_ONRESTART = "void onRestart()";
        public static final String ACTIVITY_ONRESTOREINSTANCESTATE = "void onRestoreInstanceState(android.os.Bundle)";
        public static final String ACTIVITY_ONRESUME = "void onResume()";
        public static final String ACTIVITY_ONPOSTRESUME = "void onPostResume()";
        public static final String ACTIVITY_ONATTACHFRAGMENT = "void onAttachFragment(android.app.Fragment)";

        // after running
        public static final String ACTIVITY_ONSAVEINSTANCESTATE = "void onSaveInstanceState(android.os.Bundle)";
        public static final String ACTIVITY_ONPAUSE = "void onPause()";
        public static final String ACTIVITY_ONSTOP = "void onStop()";
        public static final String ACTIVITY_ONDESTROY = "void onDestroy()";
        public static final String ACTIVITY_ONCREATEDESCRIPTION = "java.lang.CharSequence onCreateDescription()";

        // Collection
        private static final String[] lifecycleMethodsPreRun = {ACTIVITY_ONCREATE, ACTIVITY_ONPOSTCREATE,
                ACTIVITY_ONSTART, ACTIVITY_ONRESTART, ACTIVITY_ONRESUME, ACTIVITY_ONRESTOREINSTANCESTATE,
                ACTIVITY_ONPOSTRESUME, ACTIVITY_ONATTACHFRAGMENT};
        private static final String[] lifecycleMethodsAfterRun = {ACTIVITY_ONPAUSE, ACTIVITY_ONSTOP,
                ACTIVITY_ONDESTROY, ACTIVITY_ONCREATEDESCRIPTION, ACTIVITY_ONSAVEINSTANCESTATE};
        private static final String[] lifecycleMethodsOnPause = {ACTIVITY_ONPAUSE, ACTIVITY_ONRESUME, ACTIVITY_ONPOSTRESUME};
        private static final String[] lifecycleMethodsOnStop = {ACTIVITY_ONPAUSE, ACTIVITY_ONSTOP, ACTIVITY_ONRESTART,
                ACTIVITY_ONRESUME, ACTIVITY_ONPOSTRESUME};

        private static final List<String> activityPreRunMethodList = Arrays.asList(lifecycleMethodsPreRun);
        private static final List<String> activityMethodList = Arrays.asList(
                Stream.concat(Arrays.stream(lifecycleMethodsPreRun),
                        Arrays.stream(lifecycleMethodsAfterRun)).toArray(String[] :: new));
        private static final List<String> activityOnPauseMethodList = Arrays.asList(lifecycleMethodsOnPause);
        private static final List<String> activityOnStopMethodList = Arrays.asList(lifecycleMethodsOnStop);

        // Getters
        public static List<String> getLifecycleMethods() { return activityMethodList; }
        public static List<String> getlifecycleMethodsPreRun() {return activityPreRunMethodList;}
        public static List<String> getlifecycleMethodsOnPause() {return activityOnPauseMethodList;}
        public static List<String> getlifecycleMethodsOnStop() {return activityOnStopMethodList;}
    }

    public static class ActivityLifecycleCallback {
        public static final String ACTIVITYLIFECYCLECALLBACK_ONACTIVITYSTARTED = "void onActivityStarted(android.app.Activity)";
        public static final String ACTIVITYLIFECYCLECALLBACK_ONACTIVITYSTOPPED = "void onActivityStopped(android.app.Activity)";
        public static final String ACTIVITYLIFECYCLECALLBACK_ONACTIVITYSAVEINSTANCESTATE = "void onActivitySaveInstanceState(android.app.Activity,android.os.Bundle)";
        public static final String ACTIVITYLIFECYCLECALLBACK_ONACTIVITYRESUMED = "void onActivityResumed(android.app.Activity)";
        public static final String ACTIVITYLIFECYCLECALLBACK_ONACTIVITYPAUSED = "void onActivityPaused(android.app.Activity)";
        public static final String ACTIVITYLIFECYCLECALLBACK_ONACTIVITYDESTROYED = "void onActivityDestroyed(android.app.Activity)";
        public static final String ACTIVITYLIFECYCLECALLBACK_ONACTIVITYCREATED = "void onActivityCreated(android.app.Activity,android.os.Bundle)";

        private static final String[] activityLifecycleMethods = {ACTIVITYLIFECYCLECALLBACK_ONACTIVITYSTARTED,
                ACTIVITYLIFECYCLECALLBACK_ONACTIVITYSTOPPED, ACTIVITYLIFECYCLECALLBACK_ONACTIVITYSAVEINSTANCESTATE,
                ACTIVITYLIFECYCLECALLBACK_ONACTIVITYRESUMED, ACTIVITYLIFECYCLECALLBACK_ONACTIVITYPAUSED,
                ACTIVITYLIFECYCLECALLBACK_ONACTIVITYDESTROYED, ACTIVITYLIFECYCLECALLBACK_ONACTIVITYCREATED};
        private static final List<String> activityLifecycleMethodList = Arrays.asList(activityLifecycleMethods);

        public static List<String> getLifecycleCallbackMethods() {
            return activityLifecycleMethodList;
        }
    }

    public static class Service {
        public static final String SERVICE_ONCREATE = "void onCreate()";
        public static final String SERVICE_ONSTART1 = "void onStart(android.content.Intent,int)";
        public static final String SERVICE_ONSTART2 = "int onStartCommand(android.content.Intent,int,int)";
        public static final String SERVICE_ONBIND = "android.os.IBinder onBind(android.content.Intent)";
        public static final String SERVICE_ONREBIND = "void onRebind(android.content.Intent)";
        public static final String SERVICE_ONUNBIND = "boolean onUnbind(android.content.Intent)";
        public static final String SERVICE_ONDESTROY = "void onDestroy()";

        public static final String SERVICECONNECTION_ONSERVICECONNECTED = "void onServiceConnected(android.content.ComponentName,android.os.IBinder)";
        public static final String SERVICECONNECTION_ONSERVICEDISCONNECTED = "void onServiceDisconnected(android.content.ComponentName)";

        private static final String[] serviceMethods = {SERVICE_ONCREATE, SERVICE_ONDESTROY, SERVICE_ONSTART1,
                SERVICE_ONSTART2, SERVICE_ONBIND, SERVICE_ONREBIND, SERVICE_ONUNBIND};
        private static final String[] servicePreRunMethods = {SERVICE_ONCREATE, SERVICE_ONSTART1,
                SERVICE_ONSTART2, SERVICE_ONBIND};
        private static final List<String> serviceMethodList = Arrays.asList(serviceMethods);
        private static final List<String> servicePreRunMethodList = Arrays.asList(servicePreRunMethods);

        public static List<String> getLifecycleMethods() {
            return serviceMethodList;
        }
        public static List<String> getLifecycleMethodsPrerun() {
            return servicePreRunMethodList;
        }

        private static final String[] serviceConnectionMethods = {SERVICECONNECTION_ONSERVICECONNECTED,
                SERVICECONNECTION_ONSERVICEDISCONNECTED};
        private static final List<String> serviceConnectionMethodList = Arrays.asList(serviceConnectionMethods);
        public static List<String> getConnectionMethods() {
            return serviceConnectionMethodList;
        }
    }

    public static class Fragment {
        public static final String FRAGMENT_ONCREATE = "void onCreate(android.os.Bundle)";
        public static final String FRAGMENT_ONATTACH = "void onAttach(android.app.Activity)";
        public static final String FRAGMENT_ONCREATEVIEW = "android.view.View onCreateView(android.view.LayoutInflater,android.view.ViewGroup,android.os.Bundle)";
        public static final String FRAGMENT_ONVIEWCREATED = "void onViewCreated(android.view.View,android.os.Bundle)";
        public static final String FRAGMENT_ONSTART = "void onStart()";
        public static final String FRAGMENT_ONACTIVITYCREATED = "void onActivityCreated(android.os.Bundle)";
        public static final String FRAGMENT_ONVIEWSTATERESTORED = "void onViewStateRestored(android.app.Activity)";
        public static final String FRAGMENT_ONRESUME = "void onResume()";
        public static final String FRAGMENT_ONPAUSE = "void onPause()";
        public static final String FRAGMENT_ONSTOP = "void onStop()";
        public static final String FRAGMENT_ONDESTROYVIEW = "void onDestroyView()";
        public static final String FRAGMENT_ONDESTROY = "void onDestroy()";
        public static final String FRAGMENT_ONDETACH = "void onDetach()";
        public static final String FRAGMENT_ONSAVEINSTANCESTATE = "void onSaveInstanceState(android.os.Bundle)";

        private static final String[] fragmentMethods = {FRAGMENT_ONCREATE, FRAGMENT_ONDESTROY, FRAGMENT_ONPAUSE,
                FRAGMENT_ONATTACH, FRAGMENT_ONDESTROYVIEW, FRAGMENT_ONRESUME, FRAGMENT_ONSTART, FRAGMENT_ONSTOP,
                FRAGMENT_ONCREATEVIEW, FRAGMENT_ONACTIVITYCREATED, FRAGMENT_ONVIEWSTATERESTORED, FRAGMENT_ONDETACH};
        private static final List<String> fragmentMethodList = Arrays.asList(fragmentMethods);

        public static List<String> getLifecycleMethods() {
            return fragmentMethodList;
        }
    }

    public static class GCMINTENTSERVICE {
        public static final String GCMINTENTSERVICE_ONDELETEDMESSAGES = "void onDeletedMessages(android.content.Context,int)";
        public static final String GCMINTENTSERVICE_ONERROR = "void onError(android.content.Context,java.lang.String)";
        public static final String GCMINTENTSERVICE_ONMESSAGE = "void onMessage(android.content.Context,android.content.Intent)";
        public static final String GCMINTENTSERVICE_ONRECOVERABLEERROR = "void onRecoverableError(android.content.Context,java.lang.String)";
        public static final String GCMINTENTSERVICE_ONREGISTERED = "void onRegistered(android.content.Context,java.lang.String)";
        public static final String GCMINTENTSERVICE_ONUNREGISTERED = "void onUnregistered(android.content.Context,java.lang.String)";

        private static final String[] gcmIntentServiceMethods = {GCMINTENTSERVICE_ONDELETEDMESSAGES,
                GCMINTENTSERVICE_ONERROR, GCMINTENTSERVICE_ONMESSAGE, GCMINTENTSERVICE_ONRECOVERABLEERROR,
                GCMINTENTSERVICE_ONREGISTERED, GCMINTENTSERVICE_ONUNREGISTERED};
        private static final List<String> gcmIntentServiceMethodList = Arrays.asList(gcmIntentServiceMethods);

        // Getters
        public static List<String> getGCMIntentServiceMethods() {
            return gcmIntentServiceMethodList;
        }
    }

    public static class GCMLISTENERSERVICE {
        public static final String GCMLISTENERSERVICE_ONDELETEDMESSAGES = "void onDeletedMessages()";
        public static final String GCMLISTENERSERVICE_ONMESSAGERECEIVED = "void onMessageReceived(java.lang.String,android.os.Bundle)";
        public static final String GCMLISTENERSERVICE_ONMESSAGESENT = "void onMessageSent(java.lang.String)";
        public static final String GCMLISTENERSERVICE_ONSENDERROR = "void onSendError(java.lang.String,java.lang.String)";

        private static final String[] gcmListenerServiceMethods = {GCMLISTENERSERVICE_ONDELETEDMESSAGES,
                GCMLISTENERSERVICE_ONMESSAGERECEIVED, GCMLISTENERSERVICE_ONMESSAGESENT, GCMLISTENERSERVICE_ONSENDERROR};
        private static final List<String> gcmListenerServiceMethodList = Arrays.asList(gcmListenerServiceMethods);

        // Getters
        public static List<String> getGCMListenerServiceMethods() {
            return gcmListenerServiceMethodList;
        }
    }

    public static class BroadcastReceiver {
        public static final String BROADCAST_ONRECEIVE = "void onReceive(android.content.Context,android.content.Intent)";

        private static final String[] broadcastMethods = {BROADCAST_ONRECEIVE};
        private static final List<String> broadcastMethodList = Arrays.asList(broadcastMethods);

        // Getters
        public static List<String> getLifecycleMethods() {
            return broadcastMethodList;
        }
    }

    public static class ContentProvider {
        public static final String CONTENTPROVIDER_ONCREATE = "boolean onCreate()";

        private static final String[] contentproviderMethods = {CONTENTPROVIDER_ONCREATE};
        private static final List<String> contentProviderMethodList = Arrays.asList(contentproviderMethods);

        // Getters
        public static List<String> getLifecycleMethods() {
            return contentProviderMethodList;
        }
    }

    public static class ComponentCallback {
        public static final String COMPONENTCALLBACKS_ONCONFIGURATIONCHANGED = "void onConfigurationChanged(android.content.res.Configuration)";
        public static final String COMPONENTCALLBACKS2_ONTRIMMEMORY = "void onTrimMemory(int)";

        private static final String[] componentCallbackMethods = {COMPONENTCALLBACKS_ONCONFIGURATIONCHANGED};
        private static final List<String> componentCallbackMethodList = Arrays.asList(componentCallbackMethods);

        private static final String[] componentCallback2Methods = {COMPONENTCALLBACKS2_ONTRIMMEMORY};
        private static final List<String> componentCallback2MethodList = Arrays.asList(componentCallback2Methods);

        // Getters
        public static List<String> getCallbackMethods() {
            return componentCallbackMethodList;
        }
        public static List<String> getCallback2Methods() {
            return componentCallback2MethodList;
        }
    }

    public static class Menu {
        public static final String OPTIONMENU_ONCREATE_FRAGMENT = "void onCreateOptionsMenu(android.view.Menu,android.view.MenuInflater)";
        public static final String OPTIONMENU_ONCREATE_ACTIVITY = "boolean onCreateOptionsMenu(android.view.Menu)";
        public static final String OPTIONMENU_ONPREPARE_ACTIVITY = "boolean onPrepareOptionsMenu(android.view.Menu)";
        public static final String PANELMENU_ONCREATE_ACTIVITY = "void onCreatePanelMenu(int,android.view.Menu)";
        public static final String CONTEXTMENU_ONCREATE_1 = "void onCreateContextMenu(android.view.ContextMenu," +
                "android.view.View,android.view.ContextMenu$ContextMenuInfo)";
        public static final String CONTEXTMENU_ONCREATE_2 = "void onCreateContextMenu(android.view.ContextMenu," +
                "android.view.View,android.view.ContextMenu$ContextMenuInfo,android.view.MenuInflater)";


        public static final String CLICKCALLBACK = "boolean onOptionsItemSelected(android.view.MenuItem)";
        public static final String ITEMCLICKCALLBACK = "boolean onMenuItemClick(android.view.MenuItem)";
        public static final String ITEMSELECTEDCALLBACK = "boolean onMenuItemSelected(int,android.view.MenuItem)";
        public static final String CONTEXTCLICKCALLBACK = "boolean onContextItemSelected(android.view.MenuItem)";
        public static final String CONTEXTACTIONCLICKCALLBACK = "boolean onActionItemClicked(android.view.ActionMode," +
                "android.view.MenuItem)";

        public static final String[] optionMenuCreate_activity = {OPTIONMENU_ONCREATE_ACTIVITY, OPTIONMENU_ONPREPARE_ACTIVITY};
        public static final String[] optionMenuCreate_fragment = {OPTIONMENU_ONCREATE_FRAGMENT};
        public static final String[] contextMenuCreate = {CONTEXTMENU_ONCREATE_1, CONTEXTMENU_ONCREATE_2};
        public static final List<String> optionMenuCreateMethodList_activity = Arrays.asList(optionMenuCreate_activity);
        public static final List<String> optionMenuCreateMethodList_fragment = Arrays.asList(optionMenuCreate_fragment);
        public static final List<String> contextMenuCreateMethodList = Arrays.asList(contextMenuCreate);

        public static final String[] optionMenuCallbackMethods = {CLICKCALLBACK, ITEMCLICKCALLBACK, ITEMSELECTEDCALLBACK};
        public static final String[] contextMenuCallbackMethods = {CONTEXTCLICKCALLBACK, ITEMCLICKCALLBACK, CONTEXTACTIONCLICKCALLBACK};
        public static final List<String> optionMenuCallbackMethodList = Arrays.asList(optionMenuCallbackMethods);
        public static final List<String> contextMenuCallbackMethodList = Arrays.asList(contextMenuCallbackMethods);
        public static final Set<String> optionMenuCallbackMethodSet = new HashSet<>(optionMenuCallbackMethodList);
        public static final Set<String> contextMenuCallbackMethodSet = new HashSet<>(contextMenuCallbackMethodList);

        // Getters
        public static List<String> getOptionMenuCreateForActivity() { return optionMenuCreateMethodList_activity; }
        public static List<String> getOptionMenuCreateForFragment() { return optionMenuCreateMethodList_fragment; }
        public static List<String> getOptionMenuCallbackMethodList() { return optionMenuCallbackMethodList; }
        public static Set<String> getOptionMenuCallbackMethodSet() { return optionMenuCallbackMethodSet; }
        public static List<String> getContextMenuCreateMethodList() { return contextMenuCreateMethodList;}
        public static List<String> getContextMenuCallbackMethodList() { return contextMenuCallbackMethodList;}
        public static Set<String> getContextMenuCallbackMethodSet() { return contextMenuCallbackMethodSet;}
    }

    public static class Drawer {
        public static final String DRAWER_ADDLISTENER = "void addDrawerListener(DrawerLayout.DrawerListener)";
        public static final String NAVIGATIONITEMSELECTEDCALLBACK = "boolean onNavigationItemSelected(android.view.MenuItem)";
        public static final String DRAWER_OPENDRAWER = "";
        public static final String DRAWER_CLOSEDRAWER = ""; //do the same for show menu
        public static final String DRAWER_OPEN_CALLBACK = "void onDrawerOpened(android.view.View)";
        public static final String DRAWER_CLOSE_CALLBACK = "vvoid onDrawerClosed(android.view.View) ";
        public static final String DRAWER_STATE_CHANGED_CALLBACK = "void onDrawerStateChanged(android.view.View) ";

        public static final String[] drawerCallbackMethods = {NAVIGATIONITEMSELECTEDCALLBACK};

        public static final List<String> drawerCallbackMethodsList = Arrays.asList(drawerCallbackMethods);
        public static final Set<String> drawerCallbackMethodsSet = new HashSet<>(drawerCallbackMethodsList);

        //Getters
        public static List<String> getDrawerCallbackMethods() { return drawerCallbackMethodsList; }
        public static Set<String> getDrawerCallbackMethodsSet() { return drawerCallbackMethodsSet; }
    }

    public static class Dialog {
        public static final String DIALOGCLICKCALLBACK = "void onClick(android.content.DialogInterface,int)";

        public static final String[] dialogCallbackMethods = {DIALOGCLICKCALLBACK};
        public static final List<String> dialogCallbackMethodsList = Arrays.asList(dialogCallbackMethods);
        public static final Set<String> dialogCallbackMethodsSet = new HashSet<>(dialogCallbackMethodsList);

        //Getters
        public static List<String> getDialogCallbackMethods() { return dialogCallbackMethodsList; }
        public static Set<String> getDialogCallbackMethodsSet() { return dialogCallbackMethodsSet; }
    }

    public static class Intent {
        // constructor
        public static final String INIT_DEFAULT="<android.content.Intent: void <init>()>";
        public static final String INIT_WITH_ACTION="<android.content.Intent: void <init>(java.lang.String)>";
        public static final String INIT_WITH_ACTION_URI="<android.content.Intent: void <init>(java.lang.String,android.net.Uri)>";
        public static final String INIT_WITH_CONTEXT_CLASS="<android.content.Intent: void <init>(android.content.Context,java.lang.Class)>";
        public static final String INIT_WITH_ACTION_URI_CONTEXT_CLASS="<android.content.Intent: void <init>(java.lang.String,android.net.Uri,android.content.Context,java.lang.Class)>";

        // Setters
        public static final String SET_DATA_METHOD="<android.content.Intent: android.content.Intent setData(android.net.Uri)>";
        public static final String SET_ACTION="<android.content.Intent: android.content.Intent setAction(java.lang.String)>";
        public static final String SET_CLASS="<android.content.Intent: android.content.Intent setClassName(android.content.Context,java.lang.String)>";
        public static final String PUT_EXTRA="<android.content.Intent: android.content.Intent putExtra(java.lang.String,java.lang.String[])>";
        public static final String PUT_EXTRA2="<android.content.Intent: android.content.Intent putExtra(java.lang.String,java.lang.String)>";

        public static final String GET_CLASS="<java.lang.Class: java.lang.String getName()>";
        public static final String PUT_EXTRAS_BUNDLE="<android.content.Intent: android.content.Intent putExtras(android.os.Bundle)>";
        public static final String PUT_STRING_TO_BUNDLE="<android.os.Bundle: void putString(java.lang.String,java.lang.String)>";

        public static final String SET_COMPONENT="<android.content.Intent: android.content.Intent setComponent(android.content.ComponentName)>";
        public static final String COMPONENT_INIT="<android.content.ComponentName: void <init>(java.lang.String,java.lang.String)>";
        public static final String COMPONENT_INIT_CLASS="<android.content.ComponentName: void <init>(java.lang.String,java.lang.Class)>";

        public static Set<String> getIntentConstructors(){
            Set<String> constructors = new HashSet<>();
            constructors.add(INIT_DEFAULT);
            constructors.add(INIT_WITH_ACTION);
            constructors.add(INIT_WITH_ACTION_URI);
            constructors.add(INIT_WITH_ACTION_URI_CONTEXT_CLASS);
            constructors.add(INIT_WITH_CONTEXT_CLASS);
            return constructors;
        }
    }

    public static class ClickListeners {
        // Registrations
        public static final String[] SET_ONCLICKLISTENER = {
                "void addOnUnhandledKeyEventListener(android.view.View.OnUnhandledKeyEventListener)",
                "void setOnClickListener(android.view.View.OnClickListener)",
                "void setOnContextClickListener(android.view.View.OnContextClickListener)",
                "void setOnCreateContextMenuListener(android.view.View.OnCreateContextMenuListener)",
                "void setOnDragListener(android.view.View.OnDragListener)",
                "void setOnFocusChangeListener(android.view.View.OnFocusChangeListener)",
                "void setOnGenericMotionListener(android.view.View.OnGenericMotionListener)",
                "void setOnKeyListener(android.view.View.OnKeyListener)",
                "void setOnLongClickListener(android.view.View.OnLongClickListener)",
                "void setOnTouchListener(android.view.View.OnTouchListener)"

        };

        public static Set<String> getSetListenerMethods() {
            return new HashSet<>(Arrays.asList(SET_ONCLICKLISTENER));
        }
    }
}
