package android.goal.explorer.data.android;

import android.goal.explorer.data.android.constants.ClassConstants;
import android.goal.explorer.data.android.constants.MethodConstants;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.util.HashMap;
import java.util.Map;

public class AndroidClass {

    public final SootClass scContext;
    public final SootClass scServiceConnection;

    // Fragments
    public final SootClass scFragmentTransaction;
    public final SootClass scSupportFragmentTransaction;
    public final SootClass scFragmentManager;
    public final SootClass scSupportFragmentManager;
    public final SootClass scFragment;
    public final SootClass scSupportFragment;
    public final SootClass scDialogFragment;
    public final SootClass scSupportDialogFragment;
    public final SootClass scAppCompatDialogFragment;
    public final SootClass scFragmentPagerAdapter;
    public final SootClass scFragmentStatePagerAdapter;
    public final SootClass scSupportViewPager;

    // Activity
    public final SootClass scSupportV7Activity;
    public final SootClass scSupportV4Activity;

    public final SootClass scSupportActionBarDrawerToggle;
    public final SootClass scSupportNavigationView;


    private Map<SootClass, ComponentType> componentTypeCache = new HashMap<>();

    // OS components
    public final SootClass osClassApplication;
    public final SootClass osClassActivity;
    public final SootClass osClassService;
    public final SootClass osClassFragment;
    public final SootClass osClassSupportFragment;
    public final SootClass osClassBroadcastReceiver;
    public final SootClass osClassContentProvider;
    public final SootClass osClassGCMBaseIntentService;
    public final SootClass osClassGCMListenerService;
    public final SootClass osInterfaceServiceConnection;
    public final SootClass osClassIntent;

    //Java async components
    public final SootClass runnableClass;
    public final SootClass executorServiceClass;

    private static AndroidClass instance;

    /**
     * Array containing all types of components supported in Android lifecycles
     */
    public enum ComponentType {
        Application, Activity, Service, Fragment, BroadcastReceiver, ContentProvider,
        GCMBaseIntentService, GCMListenerService, ServiceConnection, Plain, ClickListener
    }

    public AndroidClass(){
        // OS classes
        osClassApplication = Scene.v().getSootClassUnsafe(ClassConstants.APPLICATIONCLASS,false);
        osClassActivity = Scene.v().getSootClassUnsafe(ClassConstants.ACTIVITYCLASS,false);
        osClassService = Scene.v().getSootClassUnsafe(ClassConstants.SERVICECLASS,false);
        osClassFragment = Scene.v().getSootClassUnsafe(ClassConstants.FRAGMENTCLASS,false);
        osClassSupportFragment = Scene.v().getSootClassUnsafe(ClassConstants.SUPPORTFRAGMENTCLASS,false);
        osClassBroadcastReceiver = Scene.v().getSootClassUnsafe(ClassConstants.BROADCASTRECEIVERCLASS,false);
        osClassContentProvider = Scene.v().getSootClassUnsafe(ClassConstants.CONTENTPROVIDERCLASS,false);
        osClassGCMBaseIntentService = Scene.v()
                .getSootClassUnsafe(ClassConstants.GCMBASEINTENTSERVICECLASS,false);
        osClassGCMListenerService = Scene.v().getSootClassUnsafe(ClassConstants.GCMLISTENERSERVICECLASS,false);
        osInterfaceServiceConnection = Scene.v()
                .getSootClassUnsafe(ClassConstants.SERVICECONNECTIONINTERFACE, false);

        scContext = Scene.v().getSootClassUnsafe(ClassConstants.CONTEXTCLASS,false);
        scServiceConnection = Scene.v().getSootClassUnsafe(ClassConstants.SERVICECONNECTIONINTERFACE, false);

        scFragmentTransaction = Scene.v().getSootClassUnsafe(ClassConstants.FRAGMENTTRANSACTIONCLASS,false);
        scFragmentManager = Scene.v().getSootClassUnsafe(ClassConstants.FRAGMENTMANAGERCLASS,false);
        scFragment = Scene.v().getSootClassUnsafe(ClassConstants.FRAGMENTCLASS,false);

        scSupportFragmentTransaction = Scene.v().getSootClassUnsafe(ClassConstants.FRAGMENTSUPPORTTRANSACTIONCLASS,false);
        scSupportFragmentManager = Scene.v().getSootClassUnsafe(ClassConstants.FRAGMENTSUPPORTMANAGERCLASS,false);
        scSupportFragment = Scene.v().getSootClassUnsafe(ClassConstants.SUPPORTFRAGMENTCLASS,false);

        scDialogFragment = Scene.v().getSootClassUnsafe(ClassConstants.DIALOGFRAGMENTCLASS,false);
        scSupportDialogFragment = Scene.v().getSootClassUnsafe(ClassConstants.SUPPORTDIALOGFRAGMENTCLASS,false);
        scAppCompatDialogFragment = Scene.v().getSootClassUnsafe(ClassConstants.APPCOMPATDIALOGFRAGMENTCLASS,false);

        scFragmentPagerAdapter = Scene.v().getSootClassUnsafe(ClassConstants.FRAGMENTPAGERADAPTERCLASS,false);
        scFragmentStatePagerAdapter = Scene.v().getSootClassUnsafe(ClassConstants.FRAGMENTSTATEPAGERADAPTERCLASS,false);
        scSupportViewPager = Scene.v().getSootClassUnsafe(ClassConstants.SUPPORTVIEWPAGERADAPTER,false);

        scSupportActionBarDrawerToggle = Scene.v().getSootClassUnsafe(ClassConstants.SUPPORTACTIONBARDRAWERTOGGLE,false);
        scSupportNavigationView = Scene.v().getSootClassUnsafe(ClassConstants.SUPPORTNAVIGATIONVIEW,false);

        scSupportV7Activity = Scene.v().getSootClassUnsafe(ClassConstants.SUPPORTV7APPCLASS,false);
        scSupportV4Activity = Scene.v().getSootClassUnsafe(ClassConstants.SUPPORTV4APPCLASS,false);

        osClassIntent = Scene.v().getSootClassUnsafe(ClassConstants.INTENTCLASS,false);

        runnableClass = Scene.v().getSootClassUnsafe(ClassConstants.RUNNABLECLASS,false);
        executorServiceClass = Scene.v().getSootClassUnsafe(ClassConstants.EXECUTORSERVICECLASS,false);
    }

    public static synchronized AndroidClass v() {
        if (instance == null) {
            instance = new AndroidClass();
        }
        return instance;
    }

    /**
     * Gets the type of component represented by the given Soot class
     * @param currentClass The class for which to get the component type
     * @return The component type of the given class
     */
    public ComponentType getComponentType(SootClass currentClass) {
        if (componentTypeCache.containsKey(currentClass))
            return componentTypeCache.get(currentClass);

        // Check the type of this class
        ComponentType ctype = ComponentType.Plain;

        // (1) android.app.Application
        if (osClassApplication != null && Scene.v().getOrMakeFastHierarchy().canStoreType(currentClass.getType(),
                osClassApplication.getType()))
            ctype = ComponentType.Application;
            // (2) android.app.Activity
        else if (osClassActivity != null
                && Scene.v().getOrMakeFastHierarchy().canStoreType(currentClass.getType(), osClassActivity.getType()))
            ctype = ComponentType.Activity;
            // (3) android.app.Service
        else if (osClassService != null
                && Scene.v().getOrMakeFastHierarchy().canStoreType(currentClass.getType(), osClassService.getType()))
            ctype = ComponentType.Service;
            // (4) android.app.BroadcastReceiver
        else if (osClassFragment != null
                && Scene.v().getOrMakeFastHierarchy().canStoreType(currentClass.getType(), osClassFragment.getType()))
            ctype = ComponentType.Fragment;
        else if (osClassSupportFragment != null && Scene.v().getOrMakeFastHierarchy()
                .canStoreType(currentClass.getType(), osClassSupportFragment.getType()))
            ctype = ComponentType.Fragment;
            // (5) android.app.BroadcastReceiver
        else if (osClassBroadcastReceiver != null && Scene.v().getOrMakeFastHierarchy()
                .canStoreType(currentClass.getType(), osClassBroadcastReceiver.getType()))
            ctype = ComponentType.BroadcastReceiver;
            // (6) android.app.ContentProvider
        else if (osClassContentProvider != null && Scene.v().getOrMakeFastHierarchy()
                .canStoreType(currentClass.getType(), osClassContentProvider.getType()))
            ctype = ComponentType.ContentProvider;
            // (7) com.google.android.gcm.GCMBaseIntentService
        else if (osClassGCMBaseIntentService != null && Scene.v().getOrMakeFastHierarchy()
                .canStoreType(currentClass.getType(), osClassGCMBaseIntentService.getType()))
            ctype = ComponentType.GCMBaseIntentService;
            // (8) com.google.android.gms.gcm.GcmListenerService
        else if (osClassGCMListenerService != null && Scene.v().getOrMakeFastHierarchy()
                .canStoreType(currentClass.getType(), osClassGCMListenerService.getType()))
            ctype = ComponentType.GCMListenerService;
            // (9) android.content.ServiceConnection
        else if (osInterfaceServiceConnection != null && Scene.v().getOrMakeFastHierarchy()
                .canStoreType(currentClass.getType(), osInterfaceServiceConnection.getType()))
            ctype = ComponentType.ServiceConnection;
//		else if (osClickListener != null
//                && Scene.v().getOrMakeFastHierarchy().canStoreType(currentClass.getType(), osClickListener.getType()))
//            ctype = ComponentType.ClickListener;
        componentTypeCache.put(currentClass, ctype);
        return ctype;
    }

    /**
     * Checks whether the given class is derived from android.app.Application
     *
     * @param clazz The class to check
     * @return True if the given class is derived from android.app.Application,
     * otherwise false
     */
    public boolean isApplicationClass(SootClass clazz) {
        return osClassApplication != null
                && Scene.v().getOrMakeFastHierarchy().canStoreType(clazz.getType(), osClassApplication.getType());
    }

    /**
     * Checks whether the given method is an Android entry point, i.e., a lifecycle
     * method
     *
     * @param method The method to check
     * @return True if the given method is a lifecycle method, otherwise false
     */
    public boolean isEntryPointMethod(SootMethod method) {
        if (method == null)
            throw new IllegalArgumentException("Given method is null");
        ComponentType componentType = getComponentType(method.getDeclaringClass());
        String subsignature = method.getSubSignature();

        if (componentType == ComponentType.Activity
                && MethodConstants.Activity.getLifecycleMethods().contains(subsignature))
            return true;
        if (componentType == ComponentType.Service
                && MethodConstants.Service.getLifecycleMethods().contains(subsignature))
            return true;
        if (componentType == ComponentType.Fragment
                && MethodConstants.Fragment.getLifecycleMethods().contains(subsignature))
            return true;
        if (componentType == ComponentType.BroadcastReceiver
                && MethodConstants.BroadcastReceiver.getLifecycleMethods().contains(subsignature))
            return true;
        if (componentType == ComponentType.ContentProvider
                && MethodConstants.ContentProvider.getLifecycleMethods().contains(subsignature))
            return true;
        if (componentType == ComponentType.GCMBaseIntentService
                && MethodConstants.GCMINTENTSERVICE.getGCMIntentServiceMethods().contains(subsignature))
            return true;
        if (componentType == ComponentType.GCMListenerService
                && MethodConstants.GCMLISTENERSERVICE.getGCMListenerServiceMethods().contains(subsignature))
            return true;
        return componentType == ComponentType.ServiceConnection
                && MethodConstants.Service.getConnectionMethods().contains(subsignature);

    }


}
