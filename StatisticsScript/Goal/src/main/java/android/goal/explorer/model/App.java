package android.goal.explorer.model;

import android.goal.explorer.analysis.CallbackWidgetProvider;
import android.goal.explorer.data.value.ResourceValueProvider;
import android.goal.explorer.model.component.Activity;
import android.goal.explorer.model.component.Application;
import android.goal.explorer.model.component.BroadcastReceiver;
import android.goal.explorer.model.component.ContentProvider;
import android.goal.explorer.model.component.Fragment;
import android.goal.explorer.model.component.Service;
import android.goal.explorer.model.entity.Dialog;
import android.goal.explorer.model.entity.Menu;
import android.goal.explorer.utils.AxmlUtils;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.pmw.tinylog.Logger;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.manifest.IAndroidApplication;
import soot.jimple.infoflow.android.resources.LayoutFileParser;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition;
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointUtils;
import soot.jimple.infoflow.android.manifest.ProcessManifest;
import soot.util.MultiMap;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class App implements Serializable {

    // Instance of the class
//    private static volatile App instance;

    // Manifest
    private transient ProcessManifest manifest;

    // Package name of the APK file
    private String packageName;

    // initial activity
    private Set<Activity> launchActivities;

    // application class
    @XStreamOmitField
    private transient Application application;

    // Android components
    private Set<Activity> activities;
    private Set<Service> services;
    private Set<BroadcastReceiver> broadcastReceivers;
    private Set<ContentProvider> contentProviders;

    // Fragment class map to activity class
    private transient MultiMap<SootClass, SootClass> fragmentClasses;

    // Callbacks
    private transient MultiMap<SootClass, AndroidCallbackDefinition> callbackMethods;

    // Other UI elements
    private Set<Fragment> fragments;
    private Set<Menu> menus;
    private Set<Dialog> dialogs;

    // layout parser
    private transient LayoutManager layoutManager;

    private static App instance;

    public App(){
        reset();
    }

    public synchronized static App v() {
        if(instance == null)
            instance = new App();
        return instance;
    }

    /**
     * Resets all components of the app
     */
    private void reset() {
        launchActivities = Collections.synchronizedSet(new HashSet<>());
        activities = Collections.synchronizedSet(new HashSet<>());
        services = Collections.synchronizedSet(new HashSet<>());
        broadcastReceivers = Collections.synchronizedSet(new HashSet<>());
        contentProviders = Collections.synchronizedSet(new HashSet<>());
        fragments = Collections.synchronizedSet(new HashSet<>());
        menus = Collections.synchronizedSet(new HashSet<>());
        dialogs = Collections.synchronizedSet(new HashSet<>());
    }

    /**
     * Initialize the model of the app
     * @param app The SetupApplication instance from FlowDroid
     */
    public void initializeAppModel(SetupApplication app) {
        Logger.debug("Initializing App Model for Application");

       this.packageName = app.getManifest().getPackageName();
        Logger.debug(String.format("Package name: %s", this.packageName));

        this.manifest = (ProcessManifest) app.getManifest();


        // Callbacks and fragment classes collected by FlowDroid
        this.callbackMethods = app.getCallbackMethods();
        this.fragmentClasses = app.getFragmentClasses(); 
        //Logger.debug("The callback collected by FlowDroid {}", callbackMethods);
        /*for (AndroidCallbackDefinition callback: callbackMethods.values()){
            Logger.debug("The callback {} and its type {}", callback, callback.getCallbackType());
        }
        Logger.debug("The fragments identified by FlowDroid {}",fragmentClasses);*/

        // initialize the resources
        ResourceValueProvider.v().initializeResources(app.getResourcePackages());

        // initialize all the components
        app.printEntrypoints();
        initializeComponents(app.getEntrypointClasses(), this.manifest);

        // set the launch activity according to the manifest
        setLaunchActivities(this.manifest.getLaunchableActivityNodes());

        // set layout manager
        //Logger.debug("Here is the app : {}", app);
        //Logger.debug("Here is the layout file parser: {}", app.getLayoutFileParser());
        LayoutFileParser lfp = app.getLayoutFileParser();
        setLayoutManager(new LayoutManager(lfp));

        //initialize the widget provider
        if (lfp != null)
            CallbackWidgetProvider.v().initializeProvider(app, this.getLayoutManager());
    }

    /**
     * Initialize the components of the app
     * @param entrypointClasses The entrypoint classes of the app
     * @param manifest The manifest of the app
     */
    private void initializeComponents(Set<SootClass> entrypointClasses, ProcessManifest manifest) {
        // Process all entrypoints
        AndroidEntryPointUtils entryPointUtils = new AndroidEntryPointUtils();
        Logger.debug("Manifest entry points {}", manifest.getEntryPointClasses());
        if(entrypointClasses == null || entrypointClasses.isEmpty())
            processManifestEntries(manifest);
        else{
            for (SootClass comp : entrypointClasses) {
                AndroidEntryPointUtils.ComponentType compType = entryPointUtils.getComponentType(comp);

                Logger.debug(String.format("Entry point class %s is component type %s",
                        comp.getName(),
                        compType.toString()));

                switch (compType) {
                    case Activity:
                        AXmlNode activityNode = manifest.getActivity(comp.getName());
                        if (activityNode != null)
                            this.activities.add(new Activity(activityNode, comp, packageName));
                        else
                            Logger.error("Failed to find activity in the manifest: {}", comp.getName());
                        break;
                    case Service:
                        AXmlNode serviceNode = manifest.getService(comp.getName());
                        if (serviceNode != null)
                            this.services.add(new Service(serviceNode, comp, packageName));
                        else
                            Logger.error("Failed to find service in the manifest: {}", comp.getName());
                        break;
                    case BroadcastReceiver:
                        AXmlNode receiverNode = manifest.getReceiver(comp.getName());
                        if (receiverNode != null)
                            this.broadcastReceivers.add(new BroadcastReceiver(receiverNode,
                                comp, packageName));
                        else
                            Logger.error("Failed to find broadcast receiver in the manifest: {}", comp.getName());
                        break;
                    case ContentProvider:
                        AXmlNode providerNode = manifest.getProvider(comp.getName());
                        if (providerNode != null)
                            this.contentProviders.add(new ContentProvider(providerNode, comp, packageName));
                        else
                            Logger.error("Failed to find content provider in the manifest: {}", comp.getName());
                        break;
                    case Fragment:
                        this.fragments.add(new Fragment(comp));
                        AXmlNode newNode = manifest.getActivity(comp.getName());
                        if (newNode != null)
                            this.activities.add(new Activity(newNode, comp, packageName));
                        break;
                    case Application:
                        IAndroidApplication applicationNode = manifest.getApplication();
                        if (applicationNode != null)
                            this.application = new Application(comp.getName(), comp, packageName);
                        else
                            Logger.error("Failed to find content provider in the manifest: {}", comp.getName());
                        break;
                    case ServiceConnection:
                        Logger.debug("This should not happen. Let's see what are those classes.");
                        break;
                    case GCMBaseIntentService:
                        Logger.debug("This should not happen. Let's see what are those classes.");
                        break;
                    case GCMListenerService:
                        Logger.debug("This should not happen. Let's see what are those classes.");
                        break;
                    case Plain:
                        AXmlNode plainNode = manifest.getActivity(comp.getName());
                        if (plainNode != null) {
                            this.activities.add(new Activity(plainNode, comp, packageName));
                            break;
                        } else {
                            plainNode = manifest.getService(comp.getName());
                            if (plainNode != null) {
                                this.services.add(new Service(plainNode, comp, packageName));
                                break;
                            } else {
                                plainNode = manifest.getReceiver(comp.getName());
                                if (plainNode != null) {
                                    this.broadcastReceivers.add(new BroadcastReceiver(plainNode, comp, packageName));
                                    break;
                                }
                            }
                        }
                }
            }

            // Process fragments
            for(SootClass fragmentClass : fragmentClasses.values()) {
                this.fragments.add(new Fragment(fragmentClass));
            }
        }
    }

    private void processManifestEntries(ProcessManifest manifest){
        manifest.getActivities().forEach(activity -> {
            this.activities.add(new Activity(activity.getAXmlNode(), null, packageName));
        });
        manifest.getServices().forEach(service -> {
            this.services.add(new Service(service.getAXmlNode(), null, packageName));
        });
        manifest.getContentProviders().forEach(contentProvider -> {
            this.contentProviders.add(new ContentProvider(contentProvider.getAXmlNode(), null, packageName));
        });
        manifest.getBroadcastReceivers().forEach(broadcastReceiver -> {
            this.broadcastReceivers.add(new BroadcastReceiver(broadcastReceiver.getAXmlNode(), null, packageName));
        });
    }

    /* ====================================
               Getters and setters
     ======================================*/
    /**
     * Gets the package name of the app
     * @return The package name of the app
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Gets the application class of the app
     * @return The application class of the app
     */
    public Application getApplication() {
        return application;
    }

    /**
     * Gets the manifest of the app
     * @return The manifest of the app
     */
    public ProcessManifest getManifest() {
        return manifest;
    }

    /**
     * Gets the number of activities in the app manifest
     * @return The number of activities
     */
    public Integer getNumActInManifest() {
        return manifest.getActivities().asList().size();
    }

    /**
     * Gets the number of services in the app manifest
     * @return The number of services
     */
    public Integer getNumServiceInManifest() {
        return manifest.getServices().asList().size();
    }

    /**
     * Gets the number of broadcast receivers in the app manifest
     * @return The number of broadcast receivers
     */
    public Integer getNumReceiverInManifest() {
        return manifest.getBroadcastReceivers().asList().size();
    }

    /**
     * Gets all activities
     * @return All activities
     */
    public synchronized Set<Activity> getActivities() {
        return activities;
    }

    /**
     * Gets the specific activity by name
     * @param activityName The activity name
     * @return The activity of specific name
     */
    public synchronized Activity getActivityByName(String activityName) {
        for (Activity activity : activities){
            if (activity.getName().equals(activityName))
                return activity;
            if (activity.getShortName().equals(activityName))
                return activity;
        }
        return null;
    }

    public synchronized Set<Activity> getActivitiesExtending(String sootClass){
        return activities.stream()
            .filter(activity -> activity.getAddedClasses().stream().anyMatch(addedClass -> addedClass.getName().equals(sootClass))).collect(Collectors.toSet());
    }

    /**
     * Adds an activity to the model
     * @param activity The activity to be added
     */
    public synchronized void addActivity(Activity activity) {
        this.activities.add(activity);
    }

    /**
     * Adds a set of activities to the model
     * @param activities The activities to be added
     */
    public synchronized void addActivities(Set<Activity> activities) {
        activities.remove(null);
        this.activities.addAll(activities);
    }

    /**
     * Gets the launch activity of the app
     * @return The launch activities
     */
    public synchronized Set<Activity> getLaunchActivities() {
        return this.launchActivities;
    }

    /**
     * Set the launch activity of the app
     * @param launchActivityNodes The launch activities in form of AXML nodes
     */
    private synchronized void setLaunchActivities(Set<AXmlNode> launchActivityNodes) {
        Logger.debug("Known activities");
        for (AXmlNode node : launchActivityNodes) {
            String activityName = AxmlUtils.processNodeName(node, packageName);
            Activity activity = getActivityByName(activityName);
            Logger.debug(String.format("Found launch activity %s", activityName));

            if (activity!=null)
                this.launchActivities.add(activity);
            else
                Logger.error("Failed to find activity: {}", activityName);
        }
    }

    /**
     * Gets all services of the app
     * @return All services
     */
    public synchronized Set<Service> getServices() {
        return services;
    }

    /**
     * Adds a service to the model
     * @param service The service to be added
     */
    public synchronized void addService(Service service) {
        this.services.add(service);
    }

    /**
     * Gets the specific service by name
     * @param serviceName The service name
     * @return The service with specific name
     */
    public synchronized Service getServiceByName(String serviceName) {
        for(Service service : services){
            if (service.getName().equals(serviceName))
                return service;
            if (service.getShortName().equals(serviceName))
                return service;
        }
        return null;
    }

    /**
     * Adds a set of services to the model
     * @param services The services to be added
     */
    public synchronized void addServices(Set<Service> services) {
        services.remove(null);
        this.services.addAll(services);
    }

    /**
     * Gets all broadcast receivers of the app
     * @return All broadcast receivers of the app
     */
    public synchronized Set<BroadcastReceiver> getBroadcastReceivers() {
        return broadcastReceivers;
    }

    /**
     * Adds a broadcast receiver to the app
     * @param broadcastReceiver All broadcast receiver to be added
     */
    public synchronized void addBroadcastReceiver(BroadcastReceiver broadcastReceiver) {
        this.broadcastReceivers.add(broadcastReceiver);
    }

    /**
     * Adds a set of broadcast receivers to the model
     * @param broadcastReceivers The broadcast receivers to be added
     */
    public synchronized void addBroadcastReceivers(Set<BroadcastReceiver> broadcastReceivers) {
        broadcastReceivers.remove(null);
        this.broadcastReceivers.addAll(broadcastReceivers);
    }

    /**
     * Gets the specific broadcast receiver by name
     * @param receiverName The broadcast receiver name
     * @return The broadcast receiver with specific name
     */
    public synchronized BroadcastReceiver getReceiverByName(String receiverName) {
        for(BroadcastReceiver receiver : broadcastReceivers){
            if (receiver.getName().equals(receiverName))
                return receiver;
            if (receiver.getShortName().equals(receiverName))
                return receiver;
        }
        return null;
    }

    /**
     * Gets all content providers of the app
     * @return All content providers
     */
    public synchronized Set<ContentProvider> getContentProviders() {
        return contentProviders;
    }

    /**
     * Adds the content provider to the app
     * @param contentProvider The content provider to be added
     */
    public synchronized void addContentProvider(ContentProvider contentProvider) {
        this.contentProviders.add(contentProvider);
    }

    /**
     * Adds a set of content providers to the model
     * @param contentProviders The content providers to be added
     */
    public synchronized void addContentProviders(Set<ContentProvider> contentProviders) {
        contentProviders.remove(null);
        this.contentProviders.addAll(contentProviders);
    }

    /**
     * Gets all fragments of the app
     * @return All fragments
     */
    public synchronized Set<Fragment> getFragments() {
        return fragments;
    }

    /**
     * Gets a fragment by name
     * @param name The name of the fragment
     * @return The fragment of given name
     */
    public synchronized Fragment getFragmentByName(String name) {
        for (Fragment fragment : fragments) {
            if (name.equals(fragment.getName()))
                return fragment;
        }
        return null;
    }

    /**
     * Find all fragments with given resource ID
     * @param resId The resource ID to search
     * @return a set of fragment contains the given resource ID
     */
    public synchronized Set<Fragment> getFragmentsByResId(Integer resId) {
        Set<Fragment> fragments = new HashSet<>();
        for (Fragment fragment : this.fragments) {
            for (Integer allResId : fragment.getResourceIds()) {
                if (resId.equals(allResId)) {
                    fragments.add(fragment);
                }
            }
        }
        return fragments;
    }

    /**
     * Gets a fragment by Soot class
     * @param sc The Soot class of the fragment
     * @return The fragment of given Soot class
     */
    public synchronized Fragment getFragmentByClass(SootClass sc) {
        for (Fragment fragment : fragments) {
            if (sc.getName().equals(fragment.getMainClass().getName()))
                return fragment;
        }
        return null;
    }

    /**
     * Gets a fragment by resource id
     * @param resId The resource id of the fragment
     * @return The fragment of given name
     */
//    public synchronized Fragment getFragmentByResId(Integer resId) {
//        for (Fragment fragment : fragments) {
//            if (resId.equals(fragment.getResourceId()))
//                return fragment;
//        }
//        return null;
//    }

    /**
     * Adds a fragment to the model
     * @param fragment The fragment to be added
     */
    public synchronized void addFragment(Fragment fragment) {
        Logger.debug("Fragment {} was added to the app model", fragment);
        this.fragments.add(fragment);
    }

    /**
     * Creates a fragment to the model
     * @param sootClass The Soot class of the fragment to be added
     * @param parentActivity The parent activity of this fragment
     */
    public synchronized void createFragment(SootClass sootClass, Activity parentActivity) {
        Logger.debug("Creating a new fragment model for parent activity {}", parentActivity);
        Fragment fragment = getFragmentByName(sootClass.getName());
        if (fragment==null) {
            fragment = new Fragment(sootClass, parentActivity);
            addFragment(fragment);
        }
        // Set up the parent activity
        addFragmentToActivity(fragment, parentActivity);
    }

    /**
     * Add a fragment to activity
     * @param fragment The fragment
     * @param parentActivity The parent activity
     */
    private synchronized void addFragmentToActivity(Fragment fragment, Activity parentActivity) {
        fragment.AddParentActivity(parentActivity);
        parentActivity.addFragment(fragment);
    }
    /**
     * Creates a fragment to the model
     * @param sootClass The Soot class of the fragment to be added
     */
    public synchronized void createFragment(SootClass sootClass) {
        Logger.debug("Creating a new fragment model for  {}", sootClass);
        Fragment fragment = getFragmentByName(sootClass.getName());
        if (fragment==null)
            addFragment(new Fragment(sootClass));
    }

    /**
     * Creates a fragment to the model
     * @param sc The soot class of this fragment
     * @param resId The resource id of the fragment to be added
     */
    public synchronized Fragment createFragment(SootClass sc, Set<Integer> resId) {
        Logger.debug("Creating a new fragment model for {} with res id {}", sc, resId);
        Fragment fragment = getFragmentByName(sc.getName());
        if (fragment !=null) {
            Logger.error("Fragment {} already exists ...", fragment);
        }
        fragment = new Fragment(sc, resId);
        addFragment(fragment);
        return fragment;
    }

    /**
     * Creates a fragment to the model
     * @param sc The soot class of this fragment
     * @param resId The resource id of the fragment to be added
     */
    public synchronized void createFragment(SootClass sc, Activity parentActivity, Set<Integer> resId) {
        Logger.debug("Creating a new fragment model for {} with res id {}", sc, resId);
        Fragment fragment = getFragmentByName(sc.getName());
        if (fragment==null)
            addFragment(new Fragment(sc, resId));
        addFragmentToActivity(fragment, parentActivity);
    }

    /**
     * Gets all callbacks in form of a MultiMap
     * @return All callbacks mapped to SootClass where the callbacks are registered
     */
    public synchronized MultiMap<SootClass, AndroidCallbackDefinition> getCallbackMethods() {
        return callbackMethods;
    }

    public boolean isCallbackMethod(SootMethod method){
        return callbackMethods.values().stream().anyMatch(callback -> callback.getTargetMethod().equals(method));
    }

    /**
     * Gets the callbacks in a given SootClass
     * @param sc The SootClass to look for callbacks
     * @return The callbacks in the given SootClass
     */
    public synchronized Set<AndroidCallbackDefinition> getCallbacksInSootClass(SootClass sc) {
        return callbackMethods.get(sc);
    }

    /**
     * Gets the layout file manager
     * @return The layout manager
     */
    public synchronized LayoutManager getLayoutManager() {
        return layoutManager;
    }

    /**
     * Sets the layout file manager
     * @param lfp THe layout file manager
     */
    public synchronized void setLayoutManager(LayoutManager lfp) {
        this.layoutManager = lfp;
    }

    public Set<Menu> getMenus() {
        return menus;
    }

    public void addMenu(Menu menu) {
        this.menus.add(menu);
    }

    public Set<Dialog> getDialogs() {
        return dialogs;
    }

    public void addDialog(Dialog dialogs) {
        this.dialogs.add(dialogs);
    }

//    /**
//     * Gets the ICFG
//     * @return ICFG
//     */
//    public JimpleBasedInterproceduralCFG getIcfg() {
//        return icfg;
//    }
//
//    /**
//     * Sets the ICFG
//     * @param icfg The ICFG to be set
//     */
//    public void setIcfg(JimpleBasedInterproceduralCFG icfg) {
//        this.icfg = icfg;
//    }
}
