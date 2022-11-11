package android.goal.explorer;

import android.goal.explorer.analysis.CallbackWidgetProvider;

import android.goal.explorer.analysis.value.AnalysisParameters;
import android.goal.explorer.analysis.value.PropagationIcfg;
import android.goal.explorer.analysis.value.managers.ArgumentValueManager;
import android.goal.explorer.analysis.value.managers.MethodReturnValueManager;
import android.goal.explorer.analysis.value.transformers.field.FieldTransformerManager;
import android.goal.explorer.builder.LifecycleCallbackFilter;
import android.goal.explorer.builder.ScreenBuilder;
import android.goal.explorer.builder.TransitionBuilder;
import android.goal.explorer.cmdline.GlobalConfig;
import android.goal.explorer.model.App;
import android.goal.explorer.model.component.Activity;
import android.goal.explorer.model.component.Fragment;
import android.goal.explorer.model.stg.STG;
import android.goal.explorer.topology.TopologyExtractor;
import android.goal.explorer.utils.CustomSecurityManager;
import org.pmw.tinylog.Logger;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.callbacks.filters.AlienHostComponentFilter;
import soot.jimple.infoflow.android.callbacks.filters.ApplicationCallbackFilter;
import soot.jimple.infoflow.android.callbacks.filters.UnreachableConstructorFilter;
import soot.jimple.infoflow.util.SystemClassHandler;
import soot.jimple.toolkits.callgraph.Edge;
import st.cs.uni.saarland.de.entities.*;
import st.cs.uni.saarland.de.helpClasses.Helper;
import st.cs.uni.saarland.de.reachabilityAnalysis.UiElement;
import st.cs.uni.saarland.de.testApps.AppController;
import st.cs.uni.saarland.de.testApps.Settings;

import java.util.*;
import java.util.stream.Collectors;

import static st.cs.uni.saarland.de.testApps.TestApp.initializeSootForUiAnalysis;
import static st.cs.uni.saarland.de.testApps.TestApp.performUiAnalysis;
import static st.cs.uni.saarland.de.testApps.TestApp.runReachabilityAnalysis;



public class STGExtractor {
    // Logging tags
    private static final String RESOURCE_PARSER = "ResourceParser";
    private static final String FRAGMENT_ANALYZER = "FragmentAnalyzer";
    private static final String CALLBACK_ANALYZER = "CallbackAnalyzer";
    private static final String CLASS_ANALYZER = "JimpleAnalyzer";
    private static final String ICC_PARSER = "IccParser";
    private static final String GRAPH_BUILDER = "GraphBuilder";

    // Configuration
    private GlobalConfig config;

    // Model for the current apk being analyzed
    private App app;
    public App getApp() {
        return app;
    }

    // BACKSTAGE app model
    private Application backstageApp;
    public Application getBackstageApp() {
        return backstageApp;
    }

    private SetupApplication flowDroidApp;
    public SetupApplication getFlowDroidApp() {
        return flowDroidApp;
    }

    // Entrypoints
    private Set<SootClass> entrypoints;
    public Set<SootClass> getEntrypoints() {
        return entrypoints;
    }

    // Screen transition graph
    private STG stg;
    public STG getStg() {
        return stg;
    }

    //Callback widget provider
    private CallbackWidgetProvider callbackWidgetProvider;

    /**
     * Default constructor
     * @param config The configuration file
     */
    public STGExtractor(GlobalConfig config, Settings settings) {


        // Setup analysis config
        this.config = config;

        this.flowDroidApp = new SetupApplication(config.getFlowdroidConfig());

        // Setup the app using FlowDroid
        Logger.debug("Creating app model using FlowDroid");
        // Initializes the app to proper state
        initialize(flowDroidApp);

        this.entrypoints = flowDroidApp.getEntrypointClasses();


        CustomSecurityManager securityManager = new CustomSecurityManager();
        System.setSecurityManager(securityManager);

        try{
            // initialize backstage
            Helper.loadNotAnalyzedLibs();
            Helper.loadBundlesAndParsable();
            Helper.deleteLogFileIfExist();

            // run backstage and merge the app object
            backstageApp = runBackstage(settings);

        }
        catch(SecurityException e){
            Logger.error("Backstage exited the system, recovering ...");
            Logger.debug("Moving on without backstage after recovering from exit caused by Backstage");
        }
        catch(Exception e){
            Logger.error("Backstage threw an exception: {}", e);
            Logger.debug("Moving on without backstage");
        }
        Logger.debug("Done with UI Analysis with Backstage");

        
    }


    /**
     * Perform the whole analysis
     */
    public void constructSTG() {
        // initialize the STG with services and broadcast receivers
        stg = new STG(app);

        //if(this.entrypoints != null){//flowdroid analysis succeeded
            // topology - (1) construct callgraph (2) collect lifecycle and callback methods for each component
            Logger.debug("Starting Topology collection");
            collectTopology(config.getTimeout(), config.getNumThread());
            if(backstageApp != null)
                mergeResults(app, backstageApp);
            // register propagation analysis
            if(entrypoints != null && entrypoints.size() > 0)
                registerPropagationAnalysis();
            // build the initial and transition screens
            collectScreens();

            // collect transitions
            collectTransitions();
        /*}
        else{*/
            //collectManifestScreens();
      //  }
        
    }

    /**
     * Merge the results from backstage and our topology analyzer
     * @param app The app model generated by our approach
     * @param backstageApp The app model generated by Backstage model
     */
    private void mergeResults(App app, Application backstageApp) {

        Logger.debug("Here is the backstage app: "+backstageApp);
        int numDialogs = backstageApp.getDialogs().size();
        int numMenus = backstageApp.getMenus().size();
        int numUiElementsWithListeners = backstageApp.getUiElementsWithListeners().size();
        int numActivities = backstageApp.getActivities().size();
        int numXmlLayoutFiles = backstageApp.getXmlLayoutFiles().size();
        int numFragmentClassToLayout = backstageApp.getFragmentClassToLayout().size();
        int numTabs = backstageApp.getTabs().size();


        String resultString = "Result of Analysis\n"
                .concat(String.format("\t numDialogs: %d\n", numDialogs))
                .concat(String.format("\t numMenus: %d\n", numMenus))
                .concat(String.format("\t numUiElementsWithListeners: %d\n", numUiElementsWithListeners))
                .concat(String.format("\t numActivities: %d\n", numActivities))
                .concat(String.format("\t numXmlLayoutFiles: %d\n", numXmlLayoutFiles))
                .concat(String.format("\t numFragmentClassToLayout: %d\n", numFragmentClassToLayout))
                .concat(String.format("\t numTabs: %d\n", numTabs));

        Logger.debug(resultString);


        // dialogs and menus
        Map<Integer, Dialog> dialogMap = backstageApp.getDialogs();
        Map<Integer, Menu> menuMap = backstageApp.getMenus();

        Logger.debug("Backstage identified menus {} and dialogs {}", menuMap, dialogMap);

        for (UiElement uiElement : backstageApp.getAllUiElementsWithListeners()) {
            Logger.debug("Backstage found {} as a ui element with listener and target {}", uiElement, (uiElement != null)?uiElement.targetSootClass: "none");
            String declaringClass = uiElement.declaringSootClass;
            Logger.debug("The declaring soot class of the ui element is: {}", declaringClass);
            if(uiElement != null && uiElement.handlerMethod != null){
                if(uiElement.declaringSootClass == null || uiElement.declaringSootClass.equals("default_value")){ //TODO: Investigate why
                    String listener = uiElement.signature;
                    uiElement.declaringSootClass = listener.split(":")[0].split("<")[1];
                }
                if(uiElement.declaringSootClass != null && uiElement.declaringSootClass.contains("$"))
                    uiElement.declaringSootClass = uiElement.declaringSootClass.split("\\$",2)[0];
                declaringClass = uiElement.declaringSootClass;
                Logger.debug("The updated declaring soot class of the ui element is: {}", declaringClass);
            }
            //TODO deal with ui elements with same id, i.e dialogs
            //For now assume, two elements can't have the same id (static id) in the same activity?
            //Hmm about list views lol
            //We need to find all activites that extends that class
            //TODO should we have different UI elements or only one for the base class, then deal with extending here?
            //We plan to parse the callback for all extending classes, so maybe we should have a UI element associated as well then?
            //Or just a different listener?
            if(declaringClass != null && !declaringClass.isEmpty() && !declaringClass.equals("null"))  {
                Activity curActivity = app.getActivityByName(declaringClass);
                if (curActivity != null) { //TODO, for all extending classes where the callback is not defined, add the ui element (otherwise it would have been already parsed)
                    Logger.debug("Adding UI element from backstage {} to activity {}", uiElement, curActivity);
                    curActivity.addUiElement(uiElement.globalId, uiElement);
                } else {
                    Logger.warn("[WARN] cannot find activity {}", declaringClass);
                    Logger.warn("Checking activities extending class");
                    Scene.v().getActiveHierarchy().getSubclassesOf(Scene.v().getSootClassUnsafe(declaringClass, false)).forEach(subClass -> {
                        String name = subClass.getName();
                        Activity curAct = app.getActivityByName(name);
                        if (curAct != null) {
                            Logger.debug("Adding UI element from backstage {} to activity {}", uiElement, curAct);
                            curAct.addUiElement(uiElement.globalId, uiElement);
                        } else {
                            Logger.warn("[WARN] cannot find activity {}", name);
                        }
                    });
                    Logger.debug("Also found activities extending {} {}", declaringClass, app.getActivitiesExtending(declaringClass));
                    //Logger.debug("Checking activities extending class");
                }
             }
        }

        // add UI elements & layouts to activities
        for (Activity activity : app.getActivities()) {
            st.cs.uni.saarland.de.entities.Activity backActivity = backstageApp.getActivityByName(activity.getName());
            updateActivity(activity,backActivity,dialogMap, menuMap);

        }

        // add UI elements to fragments
        //TODO add ui elements for all static fragments as well
        //Necessary to obtain res ids
        //TODO add the callbacks to the topologu
        for (Map.Entry<String, Set<Integer>> fragmentEntry : backstageApp.getFragmentClassToLayout().entrySet()) {
            Fragment newFragment = new Fragment(Scene.v().getSootClassUnsafe(fragmentEntry.getKey(),false), fragmentEntry.getValue());
            backstageApp.getAllUiElementsWithListeners().stream()
                    .filter(uiElement -> newFragment.getName().equals(uiElement.declaringSootClass))
                    .forEach(uiElement -> newFragment.addUiElement(uiElement));
            app.addFragment(newFragment);
        }
    }

    private void updateActivity(Activity activity,  st.cs.uni.saarland.de.entities.Activity backActivity, Map<Integer, Dialog> dialogMap, Map<Integer, Menu> menuMap ) {
        if(backActivity == null)
            return;
        Logger.debug("For activity {}, checking backstage activity {}", activity, backActivity.getName());
        for (Integer layoutId : backActivity.getXmlLayouts()) {
            // check if the layout id maps to a layout file
            XMLLayoutFile layout = backstageApp.getXmlLayoutFile(layoutId);
            if (layout != null) {
                activity.addLayout(layoutId, layout);
            }

            // add UI element if the resource id maps to an UI element
            UiElement uiElement = backstageApp.getUiElementWithListenerById(layoutId, activity.getName());
            if (uiElement != null) {
                activity.addUiElement(layoutId, uiElement);
            }

            // check if the XML file is a menu
            Dialog dialog = dialogMap.get(layoutId);
            Menu menu = menuMap.get(layoutId);
            if (dialog != null) {
                activity.addDialog(dialog);
                Logger.debug("Added dialog {} to activity {}", dialog, activity);
            }
            if (menu != null) {
                if(menu.getKindOfMenu().equals("Contextual"))
                    activity.addBackstageContextMenu(menu);
                else activity.setBackstageMenu(menu);
                Logger.debug("Added menu {} to activity {}", menu, activity);
            }
        }

        for (Tab tab : backstageApp.getTabs()) {
            if(tab.getParentActivityName().equals(activity.getName())) {
                activity.addTab(tab);
            }
        }
        Logger.info("Updated Activity:" + activity.toString());
    }

    /**
     * Use BACKSTAGE to process the app
     */
    private Application runBackstage(Settings settings) {
        long beforeRun = System.nanoTime();
        //initializeSootForUiAnalysis(settings.apkPath, settings.androidJar, settings.saveJimple, false);
        AppController appResults = performUiAnalysis(settings, flowDroidApp);
        if(appResults == null){
            return null;
        }
        Helper.saveToStatisticalFile("UI Analysis has run for " + (System.nanoTime() - beforeRun) / 1E9 + " seconds");
        //PackManager.v().writeOutput();

        List<UiElement> uiElementObjectsForReachabilityAnalysis =
                appResults.getUIElementObjectsForReachabilityAnalysis(true);
        List<UiElement> distinctUiElements = uiElementObjectsForReachabilityAnalysis
                .stream().distinct().collect(Collectors.toList());
        Logger.debug(distinctUiElements.toString());
        runReachabilityAnalysis(distinctUiElements, appResults.getActivityNames(), settings,flowDroidApp);
        return AppController.getInstance().getApp();
    }

    /**
     * Collects the topology (lifecycle and callback methods for each component)
     * @param timeout The max timeout when analyzing each component (default: 5 minutes)
     * @param numThread The number of threads used in parallel analysis
     */
    public void collectTopology(Integer timeout, Integer numThread) {
        TopologyExtractor topologyExtractor = new TopologyExtractor(app, timeout, numThread);
        topologyExtractor.extractTopopology();
    }

    /**
     * Collects the screens
     */
    private void collectScreens() {
        Logger.debug("Starting screens collection");
        ScreenBuilder screenBuilder = new ScreenBuilder(app, stg, backstageApp, config);
        if(this.entrypoints != null){
            screenBuilder.addCallbackFilter(new AlienHostComponentFilter(entrypoints));
            screenBuilder.addCallbackFilter(new ApplicationCallbackFilter(entrypoints));
            screenBuilder.addCallbackFilter(new UnreachableConstructorFilter());
            if (app.getApplication() == null)
                screenBuilder.addCallbackFilter(new LifecycleCallbackFilter((String) null));
            else
                screenBuilder.addCallbackFilter(new LifecycleCallbackFilter(app.getApplication().getMainClass()));
        }
        screenBuilder.constructScreens();
    }

    /**
     * Collects all transitions in the app
     */
    public void collectTransitions() {
        Logger.debug("Starting transitions collection");
        TransitionBuilder transitionBuilder = new TransitionBuilder(stg, app, config);
        //TODO add path to the options of the algo I guess
        transitionBuilder.collectTransitions();
        stg = transitionBuilder.stg;
    }


    /**
     * initialize the app model
     */
    public void initialize(SetupApplication setupApplication) {
        // Construct the callgraph
        setupApplication.constructCallgraph();

        Logger.debug("Initializing app model");
        // initialize the model
        app = App.v();
        app.initializeAppModel(setupApplication);
    }

    /**
     * Registers propagation analysis
     */
    private void registerPropagationAnalysis() {
        AnalysisParameters.v().setIcfg(new PropagationIcfg());

        // Register field analysis
        FieldTransformerManager.v().registerDefaultFieldTransformerFactories();

        // Register value analysis
        ArgumentValueManager.v().registerDefaultArgumentValueAnalyses();

        // Register method return value analysis
        MethodReturnValueManager.v().registerDefaultMethodReturnValueAnalyses();

        // Add application classes (other classes will be ignored during the propagation)
        Set<String> analysisClasses = new HashSet<>();
        Scene.v().getApplicationClasses().snapshotIterator().forEachRemaining(x -> {
            String className = x.getName();
            if (!SystemClassHandler.v().isClassInSystemPackage(className)){
                    //isClassInSystemPackageExcludingAppPackageName(className, app.getPackageName())) {
                analysisClasses.add(className);
            }
        });
        AnalysisParameters.v().addAnalysisClasses(analysisClasses);
    }
}
