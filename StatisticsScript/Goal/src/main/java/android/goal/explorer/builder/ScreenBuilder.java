package android.goal.explorer.builder;

import android.goal.explorer.analysis.*;

import android.goal.explorer.analysis.dependency.BranchedFlowAnalysis;
import android.goal.explorer.analysis.value.identifiers.Argument;
import android.goal.explorer.analysis.value.managers.ArgumentValueManager;
import android.goal.explorer.cmdline.GlobalConfig;
import android.goal.explorer.data.android.AndroidClass;
import android.goal.explorer.data.android.constants.ClassConstants;
import android.goal.explorer.data.android.constants.MethodConstants;
import android.goal.explorer.data.value.ResourceValueProvider;
import android.goal.explorer.model.App;
import android.goal.explorer.model.component.Activity;
import android.goal.explorer.model.component.Fragment;
import android.goal.explorer.model.entity.AbstractEntity;
import android.goal.explorer.model.entity.Drawer;
import android.goal.explorer.model.entity.Menu;
import android.goal.explorer.model.stg.STG;
import android.goal.explorer.model.stg.edge.EdgeTag;
import android.goal.explorer.model.stg.node.ScreenNode;
import android.goal.explorer.topology.TopologyExtractor;
import android.goal.explorer.utils.InvokeExprHelper;
import android.goal.explorer.utils.SootUtils;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.pmw.tinylog.Logger;


import soot.Local;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition;
import soot.jimple.infoflow.android.callbacks.filters.ICallbackFilter;
import soot.jimple.infoflow.android.resources.controls.AndroidLayoutControl;
import soot.jimple.infoflow.util.SystemClassHandler;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.MHGPostDominatorsFinder;
import soot.util.MultiMap;
import soot.util.queue.ChunkedQueue;
import soot.util.queue.QueueReader;
import st.cs.uni.saarland.de.entities.*;
import st.cs.uni.saarland.de.helpClasses.Helper;
import st.cs.uni.saarland.de.reachabilityAnalysis.UiElement;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static android.goal.explorer.analysis.AnalysisUtils.extractIntArgumentFrom;
import static android.goal.explorer.analysis.AnalysisUtils.extractStringArgumentFrom;
import static android.goal.explorer.analysis.AnalysisUtils.getIntValue;
import static android.goal.explorer.utils.InvokeExprHelper.invokesInflate;
import static android.goal.explorer.utils.InvokeExprHelper.invokesSetContentView;

public class ScreenBuilder {

    private static final String TAG = "ScreenBuilder";

    private App app;
    private final Application backstageApp;
    private STG stg;


    private MultiMap<SootClass, AndroidCallbackDefinition> callbacksMap;
    private Map<AndroidCallbackDefinition, List<List<Unit>>> callbacksToFragmentTransactions;
    private Map<Integer, List<List<Unit>>> menuItemsToFragmentTransactions;
    private GlobalConfig config;

    private final List<ICallbackFilter> callbackFilters = new ArrayList<>();

    /**
     * Default constructor
     * @param app The application model
     */
    public ScreenBuilder(App app, STG stg, Application backstageApp, GlobalConfig config) {
        this.app = app;
        this.stg = stg;
        this.callbacksMap = app.getCallbackMethods();
        this.backstageApp = backstageApp;
        this.config = config;
        this.callbacksToFragmentTransactions = new HashMap<>();
        this.menuItemsToFragmentTransactions = new HashMap<>();
    }

    /**
     * Construct the screens for each activity
     */
    public void constructScreens() {
        // Collects the initial screens with set of fragments from lifecycle methods
        collectInitialScreens();

        // Initialize the filters
        for (ICallbackFilter filter : callbackFilters)
            filter.reset();

        // Analyze screens with changes in callback methods
        analyzeCallbacksForScreenVariations();
        //analyzeCallbacksForFragmentChangesOptimized();
    
        // analyze entity (menu/drawer/dialog)
//        for (ScreenNode screenNode : stg.getAllScreens()) {
//            screenNode.get
//        }

        processNestedTabScreens();
    }

    //TO-DO: Manifest only when no entry points (ok)
    //TO-DO: Separate screens from transitions, transitions builder can invoke screen builder ? (nope)
    //TO-DO: Add transitions between lifecycle methods
    //TO-DO: Double check logic for fragments for lifecyle
    //TO-DO: Optimize callback analysis


    /**
     * Collects the initial screen from lifecycle methods
     */
    private void collectInitialScreens() {
        // For each activity, we analyze its hosted fragments
        for (Activity activity : app.getActivities()) {
            // Collect initial screen for this activity
            collectInitialScreenForActivity(activity);
        }
    }

    private void processNestedTabScreens() {
        List<ScreenNode> toAdd = new ArrayList<>();
        for (ScreenNode baseScreen : stg.getAllScreens()) {
            Tab tab = baseScreen.getTab();
            if (tab == null)
                continue;

            String contentName = tab.getContentActivityName();
            for(ScreenNode contentScreen: stg.getScreenNodesByName(contentName)){
                if(contentScreen.getTab() != null) //Can we have inner tabs lol
                    continue;
                ScreenNode nestedScreen = contentScreen.clone();
                nestedScreen.setAsBaseScreenNode(false);
                nestedScreen.setName(baseScreen.getName());
                nestedScreen.setTab(tab);
                Logger.debug("Adding new screen with tab {}", nestedScreen); //Myabe  we should just replace instead of adding it? lol
                toAdd.add(nestedScreen);
            }
        }
        stg.getAllScreens().addAll(toAdd);
    }


    //TODO: Find menu registrations when collecting lifecycle (as those are static, deal with dynamically modified menus later)
    //Either look for the mapped handler now or later
    //In callback analysis: nothing to do (since onOptionsItemSelected already part of the menu methods)
    //When building transitions, now IC3 + backstage to map each method to its target soot class ?

    //TODO: Cleanup menu screens construction
    //Clicking on a menu item does not always lead to dismissal of the menu
    //For all screens, if it has an open menu, no callback is parsed except for onOptionsItemSelected
    // + additional onMenuClosed/click any element on the screen to go to the closed menu
    // This work, cause any other callback clicks on the screen, which will get rid of the menu anyways
    //If the screen does not have a menu:
    //If the menu is closed, then any callback can be parsed + onMenuOpened (which can be called at any moment I guess)
    //If there's no menu at all, then all the menu related ones shoul not be parsed

    //How about screen variations, the menu can be closed or opened at any moment
    //Generate base screens (+menu, an version opened and a version closed)
    //For every screen variation with fragments (add to set like right now), on menu opened can be parsed anytime after generating those I guess (for a screen with a closed menu),
    //for a screen with an open menu, then we need to make a version without the menu ?
    /**todo
     * for dialogs, get from backstage, creation screen variation for dialogs, with outgoing transitions for positive, negative click
     * Check how basckstage maps the listeners (unneeded maybe)
     * Need to deal with fragment analysis tho
     */
    /**todo
     * for dialogs, get from backstage, creation screen variation for dialogs, with outgoing transitions for positive, negative click
     * Check how backstage maps the listeners (unneeded maybe)
     * Need to deal with fragment analysis tho
     */

    /**
     * For context menus
     * We can store in a MenuEntity + we need info about the UI element which triggers it I guess?
     * We could do the same and only parse onContextItemSelected if there's an open menu
     * The only difference is there's a specific menu item we need to click on before
     */
     //TODO if the UIelement associated with the trigger has a targetSootClass, then no need to process the handler associated for intra-component stuff


/***************************************************** Screens components extraction *******************************************************************/
    /**
     * Collects initial screens for the given activity
     * @param activity the activity to collect initial screens
     */
    private void collectInitialScreenForActivity(Activity activity) {
        //NOTE: Fragments should be combined within a chain, e.g onPause executed after onCreate, so it should have the fragments of onCreate
        Logger.debug("Collecting initial screens and fragments for activity "+activity+"  from lifecycles");
        // Initial fragment classes (before activity is running)
        List<Map<Integer, SootClass>> initialFragmentClasses = new ArrayList<>();
        initialFragmentClasses.add(new HashMap<Integer, SootClass>());
        Set<Fragment> staticFragments = new HashSet<>();
        //Extract the dialogs defined in lifecycle method
        Set<Dialog> dialogsDefinedInLifecycle = null;

        // Gets the fragment from layout
        Map<Integer, XMLLayoutFile> layoutMap = activity.getLayouts();
        for (XMLLayoutFile layout : layoutMap.values()) {
            for (Integer includeId : layout.getStaticIncludedLayoutIDs()) {
                staticFragments.addAll(app.getFragmentsByResId(includeId));
            }
        }

        if(activity.getLifecycleMethodsPreRun().isEmpty()){
            createNewBaseScreen(activity, initialFragmentClasses, staticFragments, new HashSet<>());
            return;
        }

        // Analyze reachable methods from lifecycle methods pre-run
        for (MethodOrMethodContext lifecycleMethod : activity.getLifecycleMethodsPreRun()) { //To-DO: Create chains and fragment lifecycle chain as well
            CallbackReachableMethods rm = new CallbackReachableMethods(config.getFlowdroidConfig(),
                    activity.getMainClass(), lifecycleMethod);
            rm.update();

            // iterate through the reachable methods
           // QueueReader<MethodOrMethodContext> reachableMethods = rm.listener();
            SootMethod method = lifecycleMethod.method();
            Logger.debug("Here is the analyzed method from lifecycle, "+method);
            // Do not analyze system classes
            if (SystemClassHandler.v().isClassInSystemPackage(method.getDeclaringClass().getName()))
                continue;
            if (!method.isConcrete())
                continue;
            // Edges for context-sensitive point-to analysis
            Set<List<Edge>> edges = rm.getContextEdges(method);

            // Analyze layout: assigns resource id of the activity, and connects it with the layout file
            if (method.getName().contains("onCreate")) {
                analyzeLayout(method, activity, edges);
            }

            //TODO deal with dialog definitions properly per lifecycle? (how to integrate with fragments)
            //TODO something about fragment dialogs?
            dialogsDefinedInLifecycle = activity.getDialogs().stream()
                                        .filter(dialog -> method.getSignature().equals(dialog.getDialogCreationMethodSignature()))
                                        .collect(Collectors.toSet());
            Logger.debug("Dialogs defined in lifecycle for {} {}", activity, dialogsDefinedInLifecycle);
            // Find fragments
            FragmentChangeAnalysis fragmentChangeAnalyzer = new FragmentChangeAnalysis(config, method, edges);
            fragmentChangeAnalyzer.setAppPackageName(app.getPackageName());
            //Extract fragment transactions
            List<List<Unit>> fragmentTransactions = fragmentChangeAnalyzer.calculateFragmentTransactions();
            //Apply fragment transactions to obtain new layouts
            //if(fragmentTransactions != null && !fragmentTransactions.isEmpty()){
            Logger.debug("Here are the fragment transactions {}", fragmentTransactions);
            initialFragmentClasses = initialFragmentClasses.stream()
                                                            .flatMap(layout -> fragmentChangeAnalyzer.calculateFragmentChanges(layout, fragmentTransactions).stream())
                                                            .collect(Collectors.toList());
            Logger.debug("Here are the initial fragments, obtained from lifecycle, "+initialFragmentClasses);
            // }
            if(dialogsDefinedInLifecycle != null && !dialogsDefinedInLifecycle.isEmpty())
                // add fragments to the screen
                createNewBaseScreen(activity, initialFragmentClasses, staticFragments, dialogsDefinedInLifecycle);
        }
        // add fragments to the screen
        createNewBaseScreen(activity, initialFragmentClasses, staticFragments, dialogsDefinedInLifecycle);

        collectMenuAndDrawersForInitialScreens(activity);
        //TODO collect menus for activity fragments, likely merge into one
    }

    /**
     * Checks whether the method assigns a resource id to the activity
     * @param method The method to check for resource id
     * @param activity The activity
     */
    private void analyzeLayout(SootMethod method, Activity activity, Set<List<Edge>> edges) {
        for (Unit u : method.getActiveBody().getUnits()) {
            Stmt stmt = (Stmt) u;
            if (stmt.containsInvokeExpr()) {
                InvokeExpr inv = stmt.getInvokeExpr();
                // if it invokes setContentView or inflate
                if (invokesSetContentView(inv) || invokesInflate(inv)) {
                    Argument arg = extractIntArgumentFrom(inv);
                    Set<Object> values = ArgumentValueManager.v().getArgumentValues(arg, u, edges);
                    if (values.size()==1) {
                        Object value = values.iterator().next();
                        if (value instanceof Integer)
                            activity.setResourceId((int)value);
                    }
                }
            }
        }

        addScreensFromTabs(activity, activity.getTabs());
    }

    //TODO should be done after the entire screen was built
    private void addScreensFromTabs(Activity activity, Set<Tab> tabs) {
        List<ScreenNode> screens = new ArrayList<>();
        ScreenNode noTab = new ScreenNode(activity);
        noTab.setAsBaseScreenNode(true);
        for(Tab tab : tabs) {
            ScreenNode screen = new ScreenNode(activity);
            screen.setTab(tab);
            screen.setAsBaseScreenNode(false);
            stg.addScreen(screen);
            screens.add(screen);
            EdgeTag edgeTag = new EdgeTag("TabButton",
                    null,
                    tab.getIndicatorTextResId().equals("") ? null : Integer.parseInt(tab.getIndicatorTextResId()),
                    null,
                    tab.getIndicatorText().equals("") ? null : tab.getIndicatorText());
            stg.addTransitionEdge(noTab, screen, edgeTag);
        }
    }

    //Flow:  For each activity, Fragment extraction --> Menu + drawer extraction --> Base screen creations (why are we doing the menu transitions on the base screens ?)

    /**
     * Collect the menus and drawers declared for this activity
     * Custom callbacks are created for modeling opening/closing operations
     * Screen variations are handled by analyzeCallbacksForScreenVariations
     * @param activity
     */
    private void collectMenuAndDrawersForInitialScreens(Activity activity) {
        //Parse menus and drawers for the current activity
        //TODO: IF WE EVER HANDLE MENU ITEM MODIFICATIONS THEN ACTUAL MENU SHOULD BE STORED IN SCREEN INSTEAD OF ACTIVITY
        boolean isMenuPresent = collectMenusForActivity(activity);
        boolean isDrawerPresent = collectDrawersForActivity(activity);

        if(isMenuPresent){
            //TODO:  need two callbacks for menu (since icon might be gone) --HOW THO?
            AndroidCallbackDefinition menuClickCallback = buildCallbackDef("boolean onClickMenuIcon(int,android.Menu)");
            activity.addCallback(menuClickCallback);
        }
        if(isDrawerPresent){
            AndroidCallbackDefinition drawerClickCallback = buildCallbackDef("boolean onClickDrawerIcon(android.view.View)");
            activity.addCallback(drawerClickCallback);
        }
    }

    //TODO super class for activity and fragments (to avoid code duplication)

    /**
     * Collect the menu for an activity
     * @param activity
     * @return true if options menu found
     */
    private boolean collectMenusForActivity(Activity activity) {

        Set<Menu> contextMenus;
        if(activity.hasContextMenu()){
            Logger.debug("Activity {} has context menus", activity);
            contextMenus = collectContextMenusForComponent(activity.getMainClass(), activity.getBackstageContextMenus(), activity.getContextMenuCallbackMethods());
            if(!contextMenus.isEmpty())
                activity.setContextMenuEntities(contextMenus);
        }
        else {
            Logger.warn("No context menu found by backstage for {}, might need custom analysis", activity.getName());
            Logger.debug("No context menu found by backstage, might need custom analysis");
        }
        //}
       //for()
        Menu menu = collectOptionsMenuForComponent(activity.getMainClass(), activity.hasMenu(), activity.getBackstageMenu(), activity.getMenuOnCreateMethods(), activity.getMenuCallbackMethods());
        if (menu != null) {
            //TODO: ARE WE DEALING WITH ALL WAYS/CALLBACKS TO CREATE A MENU
            Logger.debug("Updating the visible menu or the current activity");
            activity.setMenuEntity(menu);
            return true;
        }
        return false;
    }

    private boolean collectMenusForFragment(Fragment fragment) {
        Set<Menu> contextMenus;
        if (fragment.hasContextMenu()) {
            Logger.debug("Fragment {} has context menus", fragment);
            contextMenus = collectContextMenusForComponent(fragment.getMainClass(), fragment.getBackstageContextMenus(), fragment.getContextMenuCallbackMethods());
            if (!contextMenus.isEmpty()) {
                fragment.setContextMenuEntities(contextMenus);
            }
        } else {
            Logger.warn("No context menu found by backstage for {}, might need custom analysis", fragment.getName());
            Logger.debug("No context menu found by backstage, might need custom analysis");
        }

        Menu menu = collectOptionsMenuForComponent(fragment.getMainClass(), fragment.hasMenu(), fragment.getBackstageMenu(), fragment.getMenuRegistrationMethods(), fragment.getMenuCallbackMethods());
        if (menu != null) {
            fragment.setMenuEntity(menu);
            return true;
        }
        return false;
    }

    private Set<Menu> collectContextMenusForComponent(SootClass mainClass, Set<st.cs.uni.saarland.de.entities.Menu> backstageContextMenus, Set<MethodOrMethodContext> contextMenuCallbackMethods){
        //Iterate through all possible context menu creation methods
       /* for (MethodOrMethodContext contextMenuCreationMethod: activity.getContextMenuOnCreateMethods()){
            Logger.debug("Parsing context menu creation method {}", contextMenuCreationMethod);*/
        Set<Menu> contextMenus = new HashSet<>();
        for(st.cs.uni.saarland.de.entities.Menu menu: backstageContextMenus) {
            Menu contextMenu = parseMenuFromBackstage(mainClass, menu, contextMenuCallbackMethods);
            if(contextMenu != null) {
                if(menu.getRootElementID() != 0) {
                    Logger.debug("Looking for context trigger element with id {}", menu.getRootElementID());
                    AppsUIElement rootElement = backstageApp.getUiElement(menu.getRootElementID());
                    if(rootElement != null) {
                        if (rootElement instanceof SpecialXMLTag) {
                            contextMenu.setButtonId(menu.getRootElementID());
                            Logger.debug("Adding context menu tied to {} {}", menu.getRootElementID(), contextMenu);
                            contextMenus.add(contextMenu);
                        } else {
                            List<Integer> parentsDyn = rootElement.getParentsWithDyn();
                            for (Integer parentId : parentsDyn) {
                                Menu contextMenuClone = contextMenu.clone();
                                contextMenuClone.setButtonId(parentId);
                                Logger.debug("Adding context menu tied to {} {}", parentId, contextMenu);
                                contextMenus.add(contextMenuClone);
                            }
                        }
                    }
                }
                else {
                    Logger.debug("Root element of context menu not found {}", contextMenu);
                    contextMenus.add(contextMenu);
                }
            }
        }
        return contextMenus;
    }

    public Menu collectOptionsMenuForComponent(SootClass mainClass, boolean foundByBackstage, st.cs.uni.saarland.de.entities.Menu backstageMenu, Set<MethodOrMethodContext> menuOnCreateMethods, Set<MethodOrMethodContext> menuCallbackMethods) {
        //Iterate through all possible menu creation methods
        for (MethodOrMethodContext menuCreationMethod : menuOnCreateMethods) {
            Menu menu = null;
            if (foundByBackstage) {
                menu = parseMenuFromBackstage(mainClass, backstageMenu, menuCallbackMethods);
            } else {
                try {
                    for (Unit unit : menuCreationMethod.method().retrieveActiveBody().getUnits()) {
                        Stmt stmt = (Stmt) unit;
                        if (stmt.containsInvokeExpr()) {
                            InvokeExpr inv = stmt.getInvokeExpr();
                            if (InvokeExprHelper.invokesMenuInflate(inv)) {
                                //extract info about the menu (res, id, name)
                                Value menuId = inv.getArg(0);
                                Integer resId = getIntValue(menuId, unit);
                                //TODO replace with value extraction function
                                //name probably is in layout manager or smth
                                if (resId != -1) {
                                    String menuName = ResourceValueProvider.v().getMenuResourceName(resId);
                                    Logger.debug("The collected menu name {}", menuName);
                                    Map<Integer, SootMethod> menuItemsCallbacks = collectMenuItemsCallbacks(mainClass, menuCallbackMethods, new ArrayList<>(), new HashMap<>()); //to double check for crashh if no items
                                    Logger.debug("Done collecting menu items for {}", mainClass);
                                    Logger.debug("The collected menu items for  {}: {}", mainClass, menuItemsCallbacks);
                                    menu = new Menu(resId, menuName, menuItemsCallbacks); //maybe store the callback ?
                                    break;
                                }
                            }
                        }
                    }
                }
                catch (Exception e){
                    Logger.error("Issue resolving method body for {} {}", mainClass, menuCreationMethod.method());
                    Logger.error("Soot exception {}", e);
                }
            }
            return menu;
        }
        return null;
    }



    public Menu parseMenuFromBackstage(SootClass mainClass, st.cs.uni.saarland.de.entities.Menu backstageMenu, Set<MethodOrMethodContext> callbacks) {
        Logger.debug("Parsing menu from backstage for activity/fragment {}", mainClass);
        Menu menu;
        Map<Integer, List<Listener>> itemsListenerMap = backstageMenu.getElementListenersMap(backstageApp.getUIElementsMap()); //probably need to set some of the parameters of that menu element (if already built I guess)
        List<Integer> itemsFromBackstage = backstageMenu.getUIElementIDs().stream()
                .filter(elementId -> backstageApp.getUIElementsMap().get(elementId).getKindOfUiElement().equals("item"))
                .collect(Collectors.toList());

        List<Integer> itemIds = itemsFromBackstage;
        Map<Integer,Integer> realDynamicEIDs = null;
        if(backstageMenu.isDynamicallyRegistered()){
            realDynamicEIDs = backstageMenu.getRealDynamicEIDs();
            itemIds = new ArrayList<Integer>(realDynamicEIDs.keySet());
            Logger.debug("Menu dynamically registered, actualIds {}", realDynamicEIDs);
        }
        //TODO: MAP Listeners to actualIds
        Map<Integer, SootMethod> menuItemsCallbacks = collectMenuItemsCallbacks(mainClass, callbacks, itemIds, itemsListenerMap);
        Logger.debug("Done collecting callbacks");
        //Logger.debug("The collected menu items : {}", menuItemsCallbacks);
        if(backstageMenu.isDynamicallyRegistered()){
            Map<Integer,Integer> eIDsMap = realDynamicEIDs;

            Map<Integer, SootMethod> map = new HashMap<>();
            if(menuItemsCallbacks != null)
                menuItemsCallbacks.forEach((id, method) -> {
                    if(eIDsMap.containsKey(id))
                        map.put(eIDsMap.get(id), method);
                    else map.put(id, method);
                });
            menu = new Menu(backstageMenu.getId(), backstageMenu.getName(), map, true);
        }
        else menu = new Menu(backstageMenu.getId(), backstageMenu.getName(), menuItemsCallbacks);
        Logger.debug("The new menu after parsing backstage {}", menu);
        return menu;
    }



    /**
     * Collect drawers for the activity
     * Drawer parsed by Backstage ui analysis
     * @param activity
     * @return true if drawer found
     */
    private boolean collectDrawersForActivity(Activity activity) {
        SootClass mainClass = activity.getMainClass();
        Logger.debug("Extracting created drawers in activity {}", activity);
        MethodOrMethodContext drawerCreationMethod = SootUtils.findAndAddMethod(MethodConstants.Activity.ACTIVITY_ONCREATE, activity);//TO-DO: a for loop ?
        if (drawerCreationMethod != null){
            Integer resId = 0;
            st.cs.uni.saarland.de.entities.Menu menuInDrawer = extractMenuContainedInDrawer(activity, drawerCreationMethod.method());
            if(menuInDrawer != null && backstageApp != null){
                Logger.debug("Extracted menu element {}", menuInDrawer);
                Map<Integer, List<Listener>> itemsListenerMap = menuInDrawer.getElementListenersMap(backstageApp.getUIElementsMap()); //probably need to set some of the parameters of that menu element (if already built I guess)
                List<Integer> itemsFromBackstage = menuInDrawer.getUIElementIDs().stream().filter(elementId -> backstageApp.getUIElementsMap().get(elementId).getKindOfUiElement().equals("item"))
                                                                                    .collect(Collectors.toList()); //get text of the element I guess
                Map<Integer, SootMethod> menuItemsCallbacks = collectMenuItemsCallbacks(mainClass, activity.getDrawerMenuCallbackMethods(), itemsFromBackstage, itemsListenerMap);
                Menu menu = new Menu(menuInDrawer.getId(), menuInDrawer.getName(), menuItemsCallbacks); //TO-DO maybe add backstage menuInDrawer to Activity
                Drawer drawer = new Drawer(resId, menu); //which id should go here
                Pair<String, String> drawerContentDesc = extractDrawerIconContentDesc(activity, drawerCreationMethod.method());
                if (drawerContentDesc != null)
                    drawer.setContentDescs(drawerContentDesc.getLeft(), drawerContentDesc.getRight());
                activity.setDrawer(drawer);
                return true;
            }
        }
        return false;
    }


    //TODO: UPDATE WITH RECENT

    /**
     * Collect the
     * @param mainClass
     * @param menuCallbackMethods
     * @param items
     * @param itemsToListeners
     * @return mapping from menu element to callback
     */
    private Map<Integer, SootMethod> collectMenuItemsCallbacks(SootClass mainClass, Set<MethodOrMethodContext> menuCallbackMethods, List<Integer> items, Map<Integer, List<Listener>> itemsToListeners) {
        for (MethodOrMethodContext menuItemCallbackMethod : menuCallbackMethods) {
            Logger.debug("Callback under analysis {}", menuCallbackMethods);
            SootMethod method = menuItemCallbackMethod.method();
            CallbackReachableMethods rm = new CallbackReachableMethods(config.getFlowdroidConfig(),
                    mainClass, menuItemCallbackMethod);

            rm.update();

            BranchedFlowAnalysis flowAnalysis = new BranchedFlowAnalysis(method, "int getItemId()", items, rm.getContextEdges(menuItemCallbackMethod));
            return flowAnalysis.performBranchedAnalysis().entrySet()
                    .stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                        List<Listener> listeners = itemsToListeners.get(entry.getKey());
                        if (listeners != null && !listeners.isEmpty()) {
                            //for now, just one listener
                            //MethodOrMethodContext methodContext = SootUtils.findAndAddMethod(listeners.get(0).getListenerMethod(), activity); //double check if listener class is activity ?
                            //if (methodContext != null)
                            //    return methodContext.method();
                        }
                        List<Unit> units = entry.getValue();
                        SootMethod tmpMethod = new SootMethod(method.getName() + entry.getKey(), method.getParameterTypes(), method.getReturnType());
                        try {
                            JimpleBody body = Jimple.v().newBody(tmpMethod);
                            tmpMethod.setActiveBody(body);
                            body.getLocals().addAll(method.getActiveBody().getLocals());
                            if (units != null)
                                body.getUnits().addAll(units);
                            else
                                Logger.error("Empty mapping of units for menu item {}", entry.getKey());
                        }
                        catch (Exception e){
                            Logger.error("Issue while resolving statements for menu item {} : \n{}",entry.getKey(), e);
                            e.printStackTrace();
                            Logger.debug("Defaulting to original body for item {}", entry.getKey());
                            tmpMethod.setActiveBody(method.getActiveBody());
                        }
                        return tmpMethod;
                    }));
        }
        return null;
    }








/***************************************************** ******************************************* *******************************************************************/



/********************************************************************** Screen creation *******************************************************************/
  /**
     * Creates a new screen with the set of fragments
     * @param activity The activity
     * @param fragmentClassesPreRun The fragment classes collected
     * @param staticFragments The fragments declared in XML layout file
    * @param dialogs The dialogs declared within the lifecycle methods
     */

  //TODO model call sequence for lifecycles to deal with fragments properly
    private void createNewBaseScreen(Activity activity, List<Map<Integer, SootClass>> fragmentClassesPreRun, Set<Fragment> staticFragments, Set<Dialog> dialogs) {
        //Process the fragments
        Logger.debug("About to create new screens with fragments declared in layout, size " + staticFragments.size());
        Logger.debug("About to create new screens with fragments declared in lifecycle, size " + fragmentClassesPreRun.size() + " content " + fragmentClassesPreRun);
        if (fragmentClassesPreRun.isEmpty()) { //no dynamic changes
            stg.addScreen(new ScreenNode(activity, new HashSet<>(staticFragments), dialogs, true));
        }
        for (Map<Integer, SootClass> layout : fragmentClassesPreRun) {
            Map<Integer, Fragment> dynamicFragments = new HashMap<>();
            for (Map.Entry<Integer, SootClass> entry : layout.entrySet()) {
                int containerId = entry.getKey();
                SootClass sc = entry.getValue();
                Fragment fragment = app.getFragmentByClass(sc);
                Logger.debug("Trying to add a new fragment {}", sc);
                if (fragment != null) {
                    Logger.debug("The fragment is already present in the app");
                    dynamicFragments.put(containerId, fragment);
                    //Add current activity to parents?
                    if(!fragment.isUpToDate())
                        updateFragmentMenusAndDialogs(fragment);
                } else {
                    //we check that there is a backstage app, otherwise we have no mapping
                    Logger.debug("The fragment is not present yet, adding to the app but not to the screen for some reason: {}", sc);
                    Set<Integer> resIds = (backstageApp != null) ? backstageApp.getFragmentClassToLayout().get(sc.toString()) : new HashSet<>();
                    fragment = app.createFragment(sc, resIds);
                    //why is this not added ?
                    //should be added if not null
                   dynamicFragments.put(containerId, fragment);
                   updateFragmentUi(sc, fragment);
                        //then we need to add all callbacks of the fragments, that way it's dealt with automatically
                }
            }
            stg.addScreen(new ScreenNode(activity, new HashSet<>(staticFragments), dynamicFragments, dialogs, true));
        }
    }

    //TODO refactor
    public void updateFragmentUi(SootClass sc, Fragment fragment) {
        //TODO need to add the appriopriate callbacks to the activity?
        if (backstageApp != null) {
            backstageApp.getAllUiElementsWithListeners().stream()
                    .filter(uiElement -> sc.getName().equals(uiElement.declaringSootClass))
                    .forEach(uiElement -> fragment.addUiElement(uiElement));
            updateFragmentMenusAndDialogs(fragment);
        }
    }

    public void updateFragmentMenusAndDialogs(Fragment fragment) {
        TopologyExtractor.collectExtendedClasses(fragment);
        TopologyExtractor.collectLifecycleMethods(fragment);
        //TODO parent should be onClick or based on type of ui element action?
        Set<AndroidCallbackDefinition> callbacks =
        fragment.getUiElements().stream().filter(uiElement -> uiElement.handlerMethod != null)
                            .map(uiElement -> new AndroidCallbackDefinition(uiElement.handlerMethod, uiElement.handlerMethod, AndroidCallbackDefinition.CallbackType.Widget))
                                    .collect(Collectors.toSet());
        callbacks.addAll(app.getCallbacksInSootClass(fragment.getMainClass()));
        TopologyExtractor.collectCallbackMethods(fragment, callbacks);

        if(backstageApp != null) {
            Collection<Integer> layouts = backstageApp.getActivityToXMLLayoutFiles().get(fragment.getName());
            if (layouts != null) {
                Map<Integer, st.cs.uni.saarland.de.entities.Menu> menuMap = backstageApp.getMenus();
                Map<Integer, st.cs.uni.saarland.de.entities.Dialog> dialogMap = backstageApp.getDialogs();
                for (Integer layoutId : layouts) {
                    st.cs.uni.saarland.de.entities.Menu menu = menuMap.get(layoutId);
                    Dialog dialog = dialogMap.get(layoutId);
                    if (menu != null) {
                        if (menu.getKindOfMenu().equals("Contextual"))
                            fragment.addBackstageContextMenu(menu);
                        else fragment.setMenu(menu);
                    }
                    if (dialog != null) {
                        fragment.addDialog(dialog); //TODO deal with entities?
                        //add dialog creation signature method as callback
                        if (dialog.getDialogCreationMethodSignature() != null) {
                            SootMethod dialogCreationMethod = Scene.v().getMethod(dialog.getDialogCreationMethodSignature());
                            if (!MethodConstants.Fragment.getLifecycleMethods().contains(dialogCreationMethod.getSubSignature())) {
                                fragment.addCallback(new AndroidCallbackDefinition(dialogCreationMethod, dialogCreationMethod, AndroidCallbackDefinition.CallbackType.Widget));
                            }
                        }
                        for (Listener listener : dialog.getPosListener()) {
                            SootMethod method = Scene.v().getMethod(listener.getSignature());
                            fragment.addDialogCallbackMethod(method);
                        }
                        for (Listener listener : dialog.getNegativeListener()) {
                            SootMethod method = Scene.v().getMethod(listener.getSignature());
                            fragment.addDialogCallbackMethod(method);
                        }
                        for (Listener listener : dialog.getNeutralListener()) {
                            SootMethod method = Scene.v().getMethod(listener.getSignature());
                            fragment.addDialogCallbackMethod(method);
                        }
                        for (Listener listener : dialog.getItemListener()) {
                            SootMethod method = Scene.v().getMethod(listener.getSignature());
                            fragment.addDialogCallbackMethod(method);
                        }
                    }
                }
            }
        }
        if(collectMenusForFragment(fragment)) { //we added an option menu
            AndroidCallbackDefinition menuClickCallback = buildCallbackDef("boolean onClickMenuIcon(int,android.Menu)");
            fragment.addCallback(menuClickCallback);
        }
        fragment.setIsUpToDate(true);
    }



    /**
     * Create new screen if we have found changes in fragments or dialogs
     * @param screenNode
     * @param possibleFragmentLayouts
     * @param callback
     * @param dialogs
     * @return
     */
    private Set<ScreenNode> createNewScreenIfDialogOrFragmentChanges(ScreenNode screenNode, List<Map<Integer, SootClass>> possibleFragmentLayouts, AndroidCallbackDefinition callback, Set<Dialog> dialogs) {
        return createNewScreenIfDialogOrFragmentChanges(screenNode, possibleFragmentLayouts, callback, dialogs, null, null, null);
    }

    //TODO maybe we should check if the ui element already has a target soot class, in that case we don't add anything
    //Distinguish between, dialogs open by the parent activity after switching fragments
    //dialogs open within the fragment itself?
    private Set<ScreenNode> createNewScreenIfDialogOrFragmentChanges(ScreenNode screenNode, List<Map<Integer, SootClass>> possibleFragmentLayouts, AndroidCallbackDefinition callback,
                                                     Set<Dialog> dialogs, Integer id,  String uiText, String uiElementType) {
        Logger.debug("About to create new screens for fragment changes (dynamic), with fragmentClasses "+possibleFragmentLayouts+"  for callback "+callback);
        Activity activity = (Activity) screenNode.getComponent();
        Set<ScreenNode> newScreenNodes = new HashSet<>();
        if(possibleFragmentLayouts == null) { //only deal with dialogs
            //TODO issue here because of dialogs defined inside fragments
            //For now, this assumes dialogs will be closed after clicking on any dialog button? (which is probably wrong, to check)
            ScreenNode newScreenNode = new ScreenNode(screenNode);
            newScreenNode.setDialogs(dialogs);
            newScreenNode.setDialogsVisibility(dialogs != null && !dialogs.isEmpty());
            if(!stg.getAllScreens().contains(newScreenNode)){
                stg.addScreen(newScreenNode);
                newScreenNodes.add(newScreenNode);
                Logger.debug("Getting ui element text for {}", id);
                //TODO here get the ui element and check if it has a target soot class with the same handler?
                String uiElementText = (id != null) ? getUIElementText(id) : uiText;
                EdgeTag tag = getEdgeTag(activity, callback, id, uiElementType, null, uiElementText);
                stg.addTransitionEdge(screenNode, newScreenNode, tag);
                Logger.debug("The number of edges in the STG {} {}", stg.getNumEdges(), stg.getAllEdges());
                return newScreenNodes;
            }
            return null;
        }
        for (Map<Integer, SootClass> possibleLayout: possibleFragmentLayouts){
            Map<Integer, Fragment> dynamicFragments = new HashMap<>();
            for (Map.Entry<Integer, SootClass> entry: possibleLayout.entrySet()){
                int containerId = entry.getKey();
                SootClass sc = entry.getValue();
                Fragment fragment = app.getFragmentByClass(sc);
                Logger.debug("Trying to add the fragment "+sc);
                Logger.debug("Fragment callbacks: {}", app.getCallbacksInSootClass(sc));
                if (fragment == null) {
                    //TODO here we can update the fragment's uiElements
                    Fragment newFragment = new Fragment(sc);
                    app.addFragment(newFragment);
                    dynamicFragments.put(containerId, newFragment);
                    if(backstageApp != null){
                        updateFragmentUi(sc, newFragment);
                    }
                    //TODO update fragment menus and dialogs as well?
                }
                else{
                    Logger.debug("The fragment was already in the app and originally not added to the set: {}", fragment);
                    dynamicFragments.put(containerId, fragment);
                    if(!fragment.isUpToDate())
                        updateFragmentMenusAndDialogs(fragment);
                } //added line --potential fix
            }
            Logger.debug("[{}] From old screen with dynamic fragments: {} ", TAG, screenNode.getDynamicFragments());
            Logger.debug("[{}] Creating new screen with fragments: {} in callback: {}", TAG, dynamicFragments, callback);

            int previousStgSize = stg.getNumScreens();
            ScreenNode newScreenNode = new ScreenNode(activity, new HashSet<>(screenNode.getStaticFragments()), dynamicFragments, dialogs);
            if (!newScreenNode.equals(screenNode)){
                stg.addScreen(newScreenNode);
                Logger.debug("These two screens are apparently not the same: {} {}", screenNode, newScreenNode);
                if(stg.getNumScreens() != previousStgSize){
                    Logger.debug("Created a new screen node with fragments: {}", newScreenNode.getFragments());
                    newScreenNodes.add(newScreenNode);
                } //we add an edge in any case
                //Logger.debug("Adding a new edge ");
                //if (callback.getCallbackType().equals(AndroidCallbackDefinition.CallbackType.Widget)) {
                EdgeTag tag = null;

                Logger.debug("Getting ui element text for {}", id);
                String uiElementText = (id != null) ? getUIElementText(id) : uiText;
                tag = getEdgeTag(activity, callback, id, uiElementType, null, uiElementText);
                // stg.addTransitionEdge(screenNode, newScreenNode, tag);
                /* }
                else*/
                stg.addTransitionEdge(screenNode, newScreenNode, tag);
                Logger.debug("The number of edges in the STG {} {}", stg.getNumEdges(), stg.getAllEdges());
            }
        }
        return newScreenNodes;
    }


     public ScreenNode createNewScreenIfMenuOrDrawerStateChanges(ScreenNode initialScreenNode, AndroidCallbackDefinition callback,  boolean isElementVisible, boolean isElementOfTypeMenu) {
        //If menu is true, then we want to open the menu stored in the activity
        Logger.debug("Creating a new screen after open/closing a drawer or menu for {}", initialScreenNode);
        ScreenNode newScreenNode = new ScreenNode(initialScreenNode);
        Activity activity = (Activity)initialScreenNode.getComponent();
        AbstractEntity entity;
        if(isElementOfTypeMenu) {
            entity = initialScreenNode.getMenuEntity(); //change to screennode.getMenuEntity
            Logger.debug("The menu entity {}", entity);
            newScreenNode.setMenuVisibility(isElementVisible);
            newScreenNode.setMenu((Menu) entity);
        }
        else{
            entity = activity.getDrawerEntity();
            Logger.debug("The drawer entity {}", entity); //TODO fragments?
            newScreenNode.setDrawerVisibility(isElementVisible);
            newScreenNode.setDrawer((Drawer) entity);
        }
        int previousStgSize = stg.getNumScreens();
        stg.addScreen(newScreenNode);
        if(stg.getNumScreens() != previousStgSize){
             //get content desc
            String contentDesc = (isElementVisible) ? entity.getOpenEntityContentDesc() : entity.getCloseEntityContentDesc();
            String type = entity.getType().name().toLowerCase(Locale.ROOT);
            int resId = entity.getResId();
            EdgeTag tag = getEdgeTag(activity, callback, resId, type, contentDesc);
            stg.addTransitionEdge(initialScreenNode, newScreenNode, tag);
            return newScreenNode;
        }
        return null;
        //If menu is false, we want to close the menu
    }

    public ScreenNode createNewScreenIfContextMenuStateChanges(ScreenNode initialScreenNode, AndroidCallbackDefinition callback, boolean isElementVisible, Menu contextMenu) {
        Logger.debug("Creating a new screen after open/closing a context menu for {}", initialScreenNode);
        ScreenNode newScreenNode = new ScreenNode(initialScreenNode);
        Activity activity = (Activity)initialScreenNode.getComponent();
        newScreenNode.setContextMenuVisibility(isElementVisible);
        newScreenNode.setContextMenu(contextMenu);
        int previousStgSize = stg.getNumScreens();
        stg.addScreen(newScreenNode);
        if(stg.getNumScreens() != previousStgSize){
            String type = "view";
            String contentDesc = null;
            //(isElementVisible) ? contextMenu.getOpenEntityContentDesc() : contextMenu.getCloseEntityContentDesc();
            Integer resId = contextMenu.getButtonId();
            EdgeTag tag = null;
            if(resId != null){
                AppsUIElement element = backstageApp.getUiElement(resId); //Should be not null
                String text = element.getTextFromElement().get("default_value");
                type = element.getKindOfUiElement();
                tag = getEdgeTag(activity, callback, resId, type, contentDesc, text);
            }
            else tag = getEdgeTag(activity, callback, resId, type, contentDesc);
            stg.addTransitionEdge(initialScreenNode, newScreenNode, tag);
            return newScreenNode;
        }
        return null;
    }




 /***************************************************** ********************************************** *******************************************************************
  * ******************************************************************************* Screen variations *************************************************************************/

     private void analyzeCallbacksForScreenVariations() {
         Logger.debug("Analyzing callbacks to extract screen variations");
         LinkedList<ScreenNode> screenNodesWorkerList = new LinkedList<>(stg.getAllScreens());

         //TODO build base screens whenever new fragment uncovered (with all elements found in fragment lifecyles

         while (!screenNodesWorkerList.isEmpty()) {
             Logger.debug("The current size of the worklist {} {}", screenNodesWorkerList.size(), screenNodesWorkerList);
             //Pop a screen node
             ScreenNode screenNode = screenNodesWorkerList.remove();
             Logger.debug("The current node {}",screenNode);
             //Get the activity and fragments
             Activity activity = (Activity) screenNode.getComponent();
             Set<Fragment> fragments = screenNode.getFragments();
             //Extract the callbacks from the activity, its fragments and initial layout
             Set<AndroidCallbackDefinition> callbacks = activity.getCallbacks();
             Map<Integer, SootClass> fragmentLayout = screenNode.getFragmentLayout();
             //TODO deal with fragments lifecycles as well
             //TODO when a fragment is resolved for a screen/activity, all its ui elements should be added as well?
             //Or we should keep ui elements for fragments as well
             fragments.stream().forEach(fragment -> callbacks.addAll(fragment.getCallbacks()));

             //Iterate through all callbacks
             for (AndroidCallbackDefinition callback : callbacks) {
                 Logger.debug("Callback under analysis: {}", callback.getTargetMethod().getSubSignature());
                 SootMethod method = callback.getTargetMethod();
                 String subSignature = method.getSubSignature();
                 //if it's drawer state change method (should i keep it as icon click or menu closed, since the icon might not be identifiable anymore once the menu is opened in the dynamic part (technically we need to click anywhere in the screen)

                 //Menu or drawer opened or closed
                 if (isMenuOrDrawerStateChangeCallback(method)) {
                     //handleMenuOrDrawerStateChanged(screenNode, callback, screenNodeLinkedList);
                     Logger.debug("Handling menu or drawer state change");
                     handleMenuOrDrawerStateChanged(screenNode, callback, screenNodesWorkerList);
                     continue;
                 }
                if(subSignature.contains("onPreferenceClick") || subSignature.contains("onClickMenuIcon"))
                    continue;
                 if (!filterAccepts(activity.getMainClass(), method))
                     continue;
                 if (!filterAccepts(activity.getMainClass(), method.getDeclaringClass()))
                     continue;
                 // Do not analyze system classes
                 if (SystemClassHandler.v().isClassInSystemPackage(method.getDeclaringClass().getName()))
                     continue;
                 if (!method.isConcrete())
                     continue;

                 CallbackReachableMethods rm = new CallbackReachableMethods(config.getFlowdroidConfig(),
                         activity.getMainClass(), method);
                 rm.update();

                 //Logger.debug("Here is the method under analysis from a callback"+method);
                 // Edges
                 Set<List<Edge>> edges = rm.getContextEdges(method);
                 //Extract all dialogs created in this callback
                 Set<Dialog> dialogsDefinedInCallback = screenNode.getDialogEntities().stream()
                         .filter(dialog -> method.getSignature().equals(dialog.getDialogCreationMethodSignature()))
                         .collect(Collectors.toSet());

                //if there is an open dialog, we skip everything besides the dialog callback
                 //TODO: for lists, it's an analysis like for menus
                 //TODO: same listener can be used for all buttons of a dialog, switch analysis on 2nd argument int which
                 if (MethodConstants.Dialog.getDialogCallbackMethodsSet().contains(subSignature)) { //TODO here should be within activity or fragments
                     //OnClickDialog
                     if(!screenNode.hasVisibleDialogs())
                         continue;
                 }
                 else if(screenNode.hasVisibleDialogs()) //Here it could be inside the fragment I guess?
                     continue;

                 if (MethodConstants.Menu.getOptionMenuCallbackMethodSet().contains(subSignature)) {//i.e onOptionsItemSelected(...)
                     //handleMenuOrDrawerCallback(screenNode, callback, edges);
                     //we only handle the callback if there's an open menu
                     handleMenuOrDrawerCallback(screenNode, callback, screenNodesWorkerList, edges, dialogsDefinedInCallback, "menu");
                     if (MethodConstants.Menu.getContextMenuCallbackMethodSet().contains(subSignature)) {//i.e onContextItemSelected
                         handleContextMenuCallback(screenNode, callback, screenNodesWorkerList, edges, dialogsDefinedInCallback);
                     }
                     continue;
                 }
                 if (MethodConstants.Drawer.getDrawerCallbackMethodsSet().contains(subSignature)) {//i.e onNavigationItemSelected(...)
                     handleMenuOrDrawerCallback(screenNode, callback, screenNodesWorkerList, edges, dialogsDefinedInCallback, "drawer");
                     continue;
                 }

                 if (MethodConstants.Menu.getContextMenuCallbackMethodSet().contains(subSignature)) {//i.e onContextItemSelected
                     handleContextMenuCallback(screenNode, callback, screenNodesWorkerList, edges, dialogsDefinedInCallback);
                     continue;
                 }

                 //If there's an open menu on the screen, we don't do anything
                 if (screenNode.hasVisibleMenu() || screenNode.hasVisibleContextMenu())
                    continue;

                 //TODO: we need to keep transitions that just open dialogs as well, even if there's no fragment
                 // we clicked on a dialog button
                 //might not work since it's an interface method


                 //Deal with fragments
                 List<List<Unit>> fragmentTransactions;
                 List<Map<Integer, SootClass>> possibleFragmentLayouts = null;

                 FragmentChangeAnalysis fragmentChangeAnalysis = new FragmentChangeAnalysis(config, method, edges);
                 Logger.debug("Extracting fragments registered in callback");
                 if (callbacksToFragmentTransactions.containsKey(callback)) {
                     //Extract info for memoized callback
                     Logger.debug("Fragment transactions memoized, no need to recompute");
                     fragmentTransactions = callbacksToFragmentTransactions.get(callback);
                 } else {
                     //Generate info for the newly visited callback
                     fragmentTransactions = fragmentChangeAnalysis.calculateFragmentTransactions();
                     //Store computed fragment transactions
                     callbacksToFragmentTransactions.put(callback, fragmentTransactions);
                 }
                 //TODO case where dialog in newly found fragment's lifecycle?
                 if (fragmentTransactions != null && fragmentChangeAnalysis.hasFoundTransactions()) { //found new fragments
                     //Extract all possible screen layouts
                     possibleFragmentLayouts = fragmentChangeAnalysis.calculateFragmentChanges(fragmentLayout, fragmentTransactions);

                     /*Set<ScreenNode> newlyAddedScreens =
                             createNewScreenIfFragmentChanges(screenNode, possibleFragmentLayouts, callback, dialogsDefinedInCallback);*/

                 }
                 //Create new screen with collected fragments and dialogs
                 Set<ScreenNode> newlyAddedScreens = createNewScreenIfDialogOrFragmentChanges(screenNode, possibleFragmentLayouts, callback, dialogsDefinedInCallback);
                 if (newlyAddedScreens != null)
                     screenNodesWorkerList.addAll(newlyAddedScreens);
             }

             //OnClickDialog should only be processed if the screen has a currently active dialog
             //look thrught all the listeners for the dialog in the screen and if any match then take its UUID
             //(actually are there uiid for dialogs button, probably not, need to focus on the text instead)
             //then pass it has to the transitions building with the text only and -1 as a UUID ?

             //otherwise, there might be a dialog created in the current screen, then we need to check if it's one of the defining methods vs one ofthe listeners
                    /*if(activity.hasDialog()){
                        Set<Dialogs> dialogsDefinedInCallback = activity.getDialogs().stream().filter(dialog -> method.equals(dialog.getDefinitionSiteMethod()));
                        }
                        //create screens with dialogs

                    }*/
             //other type of callbacks
         }
     }


    public boolean isMenuOrDrawerStateChangeCallback(SootMethod method){
        String signature = method.getSubSignature();
        //Logger.debug("The signature of the method {}", signature);
        String menuStateSignature = "boolean onClickMenuIcon(int,android.Menu)",//getMenuOrDrawerStateChangeCallback("MENU").getTargetMethod().getSubSignature(),
        drawerStateSignature = "boolean onClickDrawerIcon(android.view.View)";//getMenuOrDrawerStateChangeCallback("DRAWER").getTargetMethod().getSubSignature();
        //Logger.debug("The signatureS of the method {} {}", menuStateSignature, drawerStateSignature);
        return signature.equals(menuStateSignature)
                || signature.equals(drawerStateSignature);
    }

    public AndroidCallbackDefinition getMenuOrDrawerStateChangeCallback(String type){
        if (type.equals("MENU"))
            return /*Utils.*/buildCallbackDef("boolean onClickMenuIcon(int,android.Menu)");
        return buildCallbackDef("boolean onClickDrawerIcon(android.view.View)");
    }

    /**
     * Open or close a menu or a drawer
     * @param screenNode
     * @param callback
     * @param screenNodesWorkerList
     */
    public void handleMenuOrDrawerStateChanged(ScreenNode screenNode, AndroidCallbackDefinition callback, List<ScreenNode> screenNodesWorkerList) {
        String type = "menu";
        boolean visibility = false, isMenu = false, drawer = false;
        Activity activity = (Activity) screenNode.getComponent();
        //If it's a click on a menu
        //MAKE A CONSTANT INSTEAD
        if(callback.getTargetMethod().getSubSignature().equals(getMenuOrDrawerStateChangeCallback("MENU").getTargetMethod().getSubSignature())) {
            Logger.debug("Found menu state change for {}", activity);
            if (!screenNode.hasMenu()) //TODO change to screenNode.hasMenu
                return;
            isMenu = true;
            //If there's no visible menu, we're opening the menu
            if (!screenNode.hasVisibleMenu())
                visibility = true; //open menu
            else visibility = false; //close menu
        }
        else{//drawer
            if(!activity.hasDrawer())
                return;
            if (!screenNode.hasVisibleDrawer())
                visibility = true; //open drawer
            else visibility = false; //close drawer
        }
        ScreenNode newScreenNode = createNewScreenIfMenuOrDrawerStateChanges(screenNode, callback, visibility, isMenu);
        //Otherwise close it, same for drawer
        if (newScreenNode != null) {
            screenNodesWorkerList.add(newScreenNode);
        }
    }

    /**
     * Map a menu/drawer interaction to a new screen node
     * @param screenNode
     * @param callback
     * @param screenNodesWorkerList
     * @param edges
     * @param dialogs
     * @param type
     */
    public void handleMenuOrDrawerCallback(ScreenNode screenNode, AndroidCallbackDefinition callback,  List<ScreenNode> screenNodesWorkerList, Set<List<Edge>> edges, Set<Dialog> dialogs, String type){
        //If we already memoized it
        Activity activity = (Activity)screenNode.getComponent();
        if((screenNode.hasVisibleMenu() && type.equals("menu")) || (screenNode.hasVisibleDrawer() && type.equals("drawer"))){
            //TODO check if can be replaced with screenNode.getDrawer
            Map<Integer, SootMethod> itemsToCallbacks = (type.equals("menu")? screenNode.getVisibleMenu().getMenuItemsCallbacks(): activity.getDrawer().getMenuItemsCallbacks());
            handleMenuItems(screenNode, callback, screenNodesWorkerList, edges, dialogs, itemsToCallbacks);
        }
        else{
            Logger.debug("Menu not found before parsing items");
        }
    }


    public void handleContextMenuCallback(ScreenNode screenNode, AndroidCallbackDefinition callback,  List<ScreenNode> screenNodesWorkerList, Set<List<Edge>> edges, Set<Dialog> dialogs) {
        //If we already memoized it
        Activity activity = (Activity)screenNode.getComponent();
        Logger.debug("Handling context menu item selection");
        //Here we need to add the icon lead to a screen with open menu, then from this screen to the screen where the elements can be clicked?
        if(screenNode.hasVisibleContextMenu()) {
            Logger.debug("Current context menu {}", screenNode.getVisibleContextMenu());
            Map<Integer, SootMethod> itemsToCallbacks = screenNode.getVisibleContextMenu().getMenuItemsCallbacks();
            handleMenuItems(screenNode, callback, screenNodesWorkerList, edges, dialogs, itemsToCallbacks);
        }
        else{
            Logger.debug("Context menu not found before parsing items");
            Logger.debug("Adding click on trigger element");
            try {
                SootMethod sootMethod = Scene.v().getMethod("<android.view.View$OnLongClickListener: boolean onLongClick(android.view.View)>");
                AndroidCallbackDefinition callbackDefinition = new AndroidCallbackDefinition(sootMethod, sootMethod, AndroidCallbackDefinition.CallbackType.Widget);
                for (Menu contextMenu : screenNode.getContextMenuEntities()) { //TODO fragments?
                    //Add new screen with context menus as
                    //What if we did not resolve it?
                    //TODO CHECK FOR NPE
                    ScreenNode newScreenNode = createNewScreenIfContextMenuStateChanges(screenNode, callbackDefinition, true, contextMenu);
                    if (newScreenNode != null) {
                        screenNodesWorkerList.add(newScreenNode);
                    }

                }
            }
            catch (NullPointerException e) {
                Logger.error("Could not find soot method onLongClick {}", e.toString());
            }
        }
    }

    public void handleMenuItems(ScreenNode screenNode, AndroidCallbackDefinition callback,  List<ScreenNode> screenNodesWorkerList, Set<List<Edge>> edges, Set<Dialog> dialogs,
                                Map<Integer, SootMethod> itemsToCallbacks) {
        Map<Integer, SootClass> fragmentLayout = screenNode.getFragmentLayout();
        if (itemsToCallbacks != null) {
            itemsToCallbacks.forEach((id, tmpMethod) -> {
                try {
                    List<List<Unit>> fragmentTransactions, dialogTransactions;
                    List<Map<Integer, SootClass>> possibleFragmentLayouts = null;
                    FragmentChangeAnalysis fragmentChangeAnalysis = new FragmentChangeAnalysis(config, tmpMethod, edges);
                    if (menuItemsToFragmentTransactions.containsKey(id))
                        fragmentTransactions = menuItemsToFragmentTransactions.get(id);
                    else {
                        fragmentTransactions = fragmentChangeAnalysis.calculateFragmentTransactions();
                        menuItemsToFragmentTransactions.put(id, fragmentTransactions);
                    }

                    //Note: for now, assume all item clicks can add dialogs
                    //if(activity.hasDialogs() && activity.dialogs defined in this method)
                    //    Run the dialoganalyzer just like the fragment one
                    //TODO run dialoganalyzer to figure out which menu item added dialogs and memoize dialog transactions
                    //DialogAnalyzer dialogAnalyzer = new DialogAnalyzer(config, tmpMethod, edges);
                    if (fragmentTransactions != null && !fragmentTransactions.isEmpty())
                        possibleFragmentLayouts = fragmentChangeAnalysis.calculateFragmentChanges(fragmentLayout, fragmentTransactions);
                    Set<ScreenNode> newlyAddedScreens = createNewScreenIfDialogOrFragmentChanges(screenNode, possibleFragmentLayouts, callback, dialogs, id, null, "item");
                    if (newlyAddedScreens != null)
                        screenNodesWorkerList.addAll(newlyAddedScreens);
                }
                catch (Exception e){
                    Logger.error("Issue while resolving statements for menu item {} : \n{}",id, e);
                    e.printStackTrace();
                    Logger.debug("Moving on, no analysis for item {}", id);
                }
            });
        }
    }

    /**
     * Create screen variation based on dialog element clicked on
     * @param screenNode
     * @param callback
     * @param screenNodesWorkerList
     * @param edges
     * @param dialogsDefinedInCallback
     */
    public void handleDialogCallback(ScreenNode screenNode, AndroidCallbackDefinition callback, LinkedList<ScreenNode> screenNodesWorkerList, Set<List<Edge>> edges, Set<Dialog> dialogsDefinedInCallback) {
        //We need
        //Map from each type of button (positive, neutral, ...)
        //Need the text of each element (since we don't have the id)
        //Need to know which code elements it maps to for items
        //store a map eventually ?
        //we create a transition from the current screen to the new labelled with the initiator
        Activity activity = (Activity) screenNode.getComponent();
        SootMethod method = callback.getTargetMethod();
        //If the current screen has open dialogs
        if (screenNode.hasVisibleDialogs()) {

            //Extract the text of the dialog elemet clicked on
            Optional<String> uiElementText = screenNode.getDialogEntities().stream().map(dialog -> {
                if (dialog.getPosListener().stream().anyMatch(listener -> method.getDeclaringClass().toString().equals(listener.getListenerClass())
                        && method.getSubSignature().equals(listener.getListenerMethod()))) //TODO: put signature here instead?
                    return dialog.getPosText();
                if (dialog.getNegativeListener().stream().anyMatch(listener -> method.getDeclaringClass().toString().equals(listener.getListenerClass())
                        && method.getSubSignature().equals(listener.getListenerMethod())))
                    return dialog.getNegText();
                if (dialog.getNeutralListener().stream().anyMatch(listener -> method.getDeclaringClass().toString().equals(listener.getListenerClass())
                        && method.getSubSignature().equals(listener.getListenerMethod())))
                    return dialog.getNeutralText();
                if (dialog.getItemListener().stream().anyMatch(listener -> method.getDeclaringClass().toString().equals(listener.getListenerClass())
                        && method.getSubSignature().equals(listener.getListenerMethod())))
                    //TODO: how do we know whick item is it
                    return dialog.getItemTexts();
                return "";
            }).filter(text -> !text.isEmpty()).findFirst();

            //TODO: Revise this assumption
            if (uiElementText.isPresent()) {
                String text = uiElementText.get();
                Map<Integer, SootClass> fragmentLayout = screenNode.getFragmentLayout();
                //Multiple items ?
                String[] arrayElements = text.split("#");
                for (String element : arrayElements) {
                    //Search for fragments
                    //TODO: branching analysis
                    SootMethod tmpMethod = method; //if multiple items (arrayElements.size() > 0)
                    List<List<Unit>> fragmentTransactions;
                    FragmentChangeAnalysis fragmentChangeAnalysis = new FragmentChangeAnalysis(config, tmpMethod, edges);
                    //TODO: memoize
                    if (callbacksToFragmentTransactions.containsKey(callback))
                        //Extract info for memoized callback
                        fragmentTransactions = callbacksToFragmentTransactions.get(callback);
                    else {
                        //Generate info for the newly visited callback
                        fragmentTransactions = fragmentChangeAnalysis.calculateFragmentTransactions();
                        //Store computed fragment transactions
                        callbacksToFragmentTransactions.put(callback, fragmentTransactions);
                    }

                    List<Map<Integer, SootClass>> possibleFragmentLayouts = null;
                    if (fragmentTransactions != null && !fragmentTransactions.isEmpty())
                        possibleFragmentLayouts = fragmentChangeAnalysis.calculateFragmentChanges(fragmentLayout, fragmentTransactions);

                    Set<ScreenNode> newlyAddedScreens = createNewScreenIfDialogOrFragmentChanges(screenNode, possibleFragmentLayouts, callback, dialogsDefinedInCallback, null, element, "dialog item");

                    if (newlyAddedScreens != null)
                        screenNodesWorkerList.addAll(newlyAddedScreens);
                    //TODO
                }
            }

        }
        //use this for the transition to add
        //need to get the ui element clicked on
    }






    /******************************************************************* Building edges *************************************************************************************************/


    private EdgeTag getEdgeTag(Activity activity, AndroidCallbackDefinition callback, Integer id, String type, String contentDesc) {
        return getEdgeTag(activity, callback, id, type, contentDesc, null);
    }

    //TODO: for dialogs, there might be an issue with the overlapping of ids (since it can be the same listener for all buttons)
    //We should probably create as many transitions as there are buttons (and do some switch analysis)

    private EdgeTag getEdgeTag(Activity activity, AndroidCallbackDefinition callback, Integer id, String type, String contentDesc, String text) {
        EdgeTag tag = null;
        Logger.debug("Attempting to find the widget from which the callback originates");
        if (id == null){
            UiElement widget = CallbackWidgetProvider.v().findWidget(callback, activity);
            Logger.debug("Found widget {} for callback {}", widget, callback);
            if(widget != null){
                id = widget.globalId;
                type = widget.kindOfElement.toLowerCase();
                if (text == null){
                    text = getUIElementText(widget);
                    Map<String, String> texts = widget.text;
                    if(texts != null && texts.containsKey("default_value"))
                        text = texts.get("default_value");
                }
            }
        }
        Logger.debug("Callback info, parent mthod {} , target method {}", callback.getParentMethod(), callback.getTargetMethod());
        tag = new EdgeTag(type, callback.getParentMethod().getSubSignature(), id, contentDesc, text);
        return tag;
    }

    private String getUIElementText(UiElement uiElement) {
        String elementText = "";
        Map<String, String> texts = uiElement.text;
        if(texts!= null && texts.containsKey("default_value"))
            elementText = texts.get("default_value");
        else Logger.debug("No matching text for UI element found");
        return elementText;
        //return  backstageApp.getUIElementsMap().get(elementId).ge
    }


/******************************************************************************************************************************************************************************/

    //TODO:
        /*
        * <Drawer> <button><type><resId><content-desc> ? same for menu
        For drawers:
        Parse onCreate to find ActionBarDrawerToggle, extract the last two elements and map to strings using ResourceValueProvider I guess
        Then add the content desc to the drawer
        *
        * The handler method also needs to change, should be the same for both onClick res id on the drawer icon, then a case for open and a case for closed I guess

        For menus:
        not sure yet how to get it, needs testing
        * put 'more options for the menu itself'
        * keep the text for menu items


        Keep text of elements if provided by Backstage, or even by Flowdroid for default elements
        */

    private AndroidCallbackDefinition buildCallbackDef(String methodSignature) {
        MethodOrMethodContext methodContext = null;
        try{
            methodContext = Scene.v().getMethod(methodSignature);
        }
        catch(Exception e){
            Logger.debug("Error while looking for method: {}", e);
        }
        finally{
            if(methodContext == null){
                String[] elements = methodSignature.split("\\s|\\(|\\)");
                String returnType = elements[0];
                //String[] remainingElements = elements[1].split("\\(");
                String name = elements[1];//remainingElements[0];
                String[] params = elements[2]/*remainingElements[1].split("\\)")[0]*/.split(",");
                List<Type> paramTypes = Arrays.asList(params).stream().map(type -> ((Type)Scene.v().getTypeUnsafe((String)type))).collect(Collectors.toList());
                methodContext = buildMethodDef(name, paramTypes, Scene.v().getTypeUnsafe(returnType));

            }
        }
        return new AndroidCallbackDefinition(methodContext.method(), methodContext.method(), AndroidCallbackDefinition.CallbackType.Default);
    }


    private MethodOrMethodContext buildMethodDef(String name, List<Type> parameterTypes, Type returnType) {
        return new SootMethod(name, parameterTypes, returnType);
    }


    //TODO update drawer in activity and create new screens
    // Handle item selection: need to check addDrawerListener and parse oNnNavigationItemSelected
    //Or use navgraph and if drawer has menu, then match with nav graph elements
    private st.cs.uni.saarland.de.entities.Menu extractMenuContainedInDrawer(Activity activity, SootMethod method) {
        int navigationContainerId = extractNavViewResId(activity, method);
        if(navigationContainerId != -1){
            AndroidLayoutControl uiControl = app.getLayoutManager().findUserControlById(navigationContainerId);
            Logger.debug("Navigation view found by Soot {}", uiControl);

            if(backstageApp != null){
                AppsUIElement element = backstageApp.getUiElement(navigationContainerId);
                if(element != null && (element instanceof XMLNavigation)) {
                    XMLNavigation navElement = (XMLNavigation)element;
                            Logger.debug("Navigation view found by backstage {}", navElement);

                    String containedMenuLayout = navElement.getIncludedMenuLayoutFileName();
                    int containedMenuId = navElement.getIncludedMenuId();
                    Logger.debug("Included menu id from backstage {} {}", containedMenuId, containedMenuLayout);
                    if (containedMenuId == 0) {
                        containedMenuId = ResourceValueProvider.v().getMenuResIdByLayoutFile(containedMenuLayout);
                        Logger.debug("Internal menu id extraction {}", containedMenuId);
                    }
                    st.cs.uni.saarland.de.entities.Menu containedMenu = (st.cs.uni.saarland.de.entities.Menu) backstageApp.getXmlLayoutFile(containedMenuId);
                    return containedMenu;
                }
            }
    
        }
        Logger.debug("Navigation view not found");
        return null;
        //either get AndroidLayoutControl from soot
        //does not seem like the attribute can be obtained from backstage right away

    }

    private String getUIElementText(int elementId) {
        String elementText = "";
        Logger.debug("Extracting UI element text");
        if(backstageApp != null && backstageApp.getUIElementsMap().containsKey(elementId)){
            Map<String, String> texts = backstageApp.getUIElementsMap().get(elementId).getTextFromElement();
            if(texts != null && texts.containsKey("default_value"))
                elementText = texts.get("default_value");
            else Logger.debug("No matching text for UI element found");
           
        }
        else {
            Logger.debug("UI element {} not found in backstage map", elementId);
        }
        return elementText;
       
        //return  backstageApp.getUIElementsMap().get(elementId).ge
    }
    //should I map the items to text right away or whenever needed

    private Pair<String, String> extractDrawerIconContentDesc(Activity activity, SootMethod sootMethod) {
        for(Unit unit: sootMethod.getActiveBody().getUnits()){
            Stmt stmt = (Stmt)unit;

            if(stmt.containsInvokeExpr()){
                Logger.debug("Looking for content desc in drawer initialization {}", stmt);
                InvokeExpr inv = stmt.getInvokeExpr();
                
                //looking for specialinvoke $r3.<android.support.v7.app.ActionBarDrawerToggle: 
                    //void <init>(android.app.Activity,android.support.v4.widget.DrawerLayout,android.support.v7.widget.Toolbar,int,int)>(r0, $r2, r5, 2131689530, 2131689529)\n'
                if(inv.getMethod().getName().equals("<init>")){
                    SootClass declaringClass = inv.getMethod().getDeclaringClass();
                    Logger.debug("Declaring class {}", inv.getMethod().getDeclaringClass());
                     //need to check if unit invokes constructor of ActionBarDrawerToggle
                    if(declaringClass.equals(AndroidClass.v().scSupportActionBarDrawerToggle)){
                         //probably check for subclass instead
                        //extract the arguments from the last two params of the constructor
                        int numArgs = inv.getArgs().size();
                        Value openDrawerArg = inv.getArgs().get(numArgs - 2), closeDrawerArg = inv.getArgs().get(numArgs - 1);
                        Integer openDrawerId = null, closeDrawerId = null;

                        if(openDrawerArg instanceof IntConstant && closeDrawerArg instanceof IntConstant){
                            openDrawerId = ((IntConstant)openDrawerArg).value;
                            closeDrawerId = ((IntConstant)closeDrawerArg).value;
                            Logger.debug("The ids drawer icon open {} and close {}", openDrawerId, closeDrawerId);
                            String openDrawer = ResourceValueProvider.v().getStringById(openDrawerId)
                            , closeDrawer = ResourceValueProvider.v().getStringById(closeDrawerId);
                            Logger.debug("The content desc for drawer icon open {} and close {}", openDrawer, closeDrawer);
                            return new ImmutablePair<>(openDrawer, closeDrawer);
                        }
                        else{
                            //TODO
                            Logger.warn("Unhandled type for args {} {}", openDrawerArg.getType(), closeDrawerArg.getType());
                            Logger.debug("Unhandled type for args {} {}", openDrawerArg.getType(), closeDrawerArg.getType());
                            return null;
                        }
                        
                    }
                }
                
            }
        }
        return null;
    }

    private int extractNavViewResId(Activity activity, SootMethod sootMethod) {
        Value view = null;
        int viewId = -1;
        if(!sootMethod.hasActiveBody()){
            try {
                sootMethod.retrieveActiveBody();
            }
            catch (Exception e){
                return -1;
            }
        }
        for(Unit unit: sootMethod.getActiveBody().getUnits()){
            Stmt stmt = (Stmt)unit;

            // extracts the proper resource ID for the view
            if (stmt.containsInvokeExpr()){
                InvokeExpr inv = stmt.getInvokeExpr();
                if (inv.getMethod().getName().equals("findViewById")){
                    Logger.debug("Found findViewById method in stmt {}", stmt);
                    if (stmt instanceof DefinitionStmt){
                        DefinitionStmt definitionStmt = (DefinitionStmt)stmt;
                        view = definitionStmt.getLeftOp();
                        //Logger.debug("Left op {} and type {}", leftOp, leftOp.getType());
                        Value id = inv.getArg(0);
                        if(id instanceof IntConstant){
                            viewId = ((IntConstant)id).value;
                        }
                        else {
                            Argument arg = extractIntArgumentFrom(inv);
                            Set<Object> values = ArgumentValueManager.v().getArgumentValues(arg, unit, null);
                            if (values!=null && !values.isEmpty()) {
                                Object value = values.iterator().next();
                                if (value instanceof Integer) {
                                    viewId = (Integer)value;
                                }
                            }
                        }
                    }
                }
            }
            else if (stmt instanceof DefinitionStmt){
               DefinitionStmt definitionStmt = (DefinitionStmt)stmt;
                Value rightOp = definitionStmt.getRightOp();
                if ((rightOp instanceof Local && view == rightOp) || (rightOp instanceof CastExpr && view == ((CastExpr)rightOp).getOp())){
                    Logger.debug("Found a statement using the view fetched with findViewById {}", stmt);
                    if (isNavigationView(definitionStmt.getLeftOp())){
                        Logger.debug("Found a navigation view inflation");
                        return viewId;
                    } //TO-DO deal with bottom navigationView as well
                    Logger.debug("Did not find a navigation view inflation in this statement");
                }                
                
            }
        } //not found: need to parse xml
        //to do: parse the layouts file of the activity until you reach a NavigationView/BottomView inside a DrawerLayout
        //get the app menu parameter and look if already inflated
        //useful for nav graph if the navigation view is not accessed within the code
        return -1;
    }

    private boolean isNavigationView(Value arg) { //TO-DO: refactor

        Type navViewType = Scene.v().getTypeUnsafe(ClassConstants.SUPPORTNAVIGATIONVIEW, false);
        if(navViewType == null)
            return false;
        //here resolve possible types and only use default if types empty
        return Scene.v().getFastHierarchy().canStoreType(arg.getType(), navViewType);
    }

    private boolean isBottomNavigationView(Type type) {
        return false;
    }


   

    /**
     * Execute the task on a separate thread with timeout specified in configuration file
     * @param task The task to execute
     * @param method The method for which the task executes on
     */
    private void runTask(Future<Void> task, SootMethod method) {
        try {
            task.get(config.getTimeout(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Logger.info("[{}] Interrupted an entrypoint: {} from a parent thread", TAG, method.getName());
            task.cancel(true);
        } catch (TimeoutException e) {
            Logger.info("[{}] Timeout for entrypoint {}", TAG, method.getName());
            task.cancel(true);
        } catch (Exception e) {
            Logger.error(Helper.exceptionStacktraceToString(e));
            Helper.saveToStatisticalFile(Helper.exceptionStacktraceToString(e));
            task.cancel(true);
        }
    }

//    


   













/********************************** Refactoring in progress *******************************************************************************************************/


    //TODO: Have a method that fetches the widget element if not found by backstage and updates it

    
    /**
     * Adds a new filter that checks every callback before it is associated with the
     * respective host component
     *
     * @param filter
     *            The filter to add
     */
    public void addCallbackFilter(ICallbackFilter filter) {
        this.callbackFilters.add(filter);
    }

    /**
     * Checks whether all filters accept the association between the callback class
     * and its parent component
     *
     * @param lifecycleElement
     *            The hosting component's class
     * @param targetClass
     *            The class implementing the callbacks
     * @return True if all filters accept the given component-callback mapping,
     *         otherwise false
     */
    private boolean filterAccepts(SootClass lifecycleElement, SootClass targetClass) {
        for (ICallbackFilter filter : callbackFilters)
            if (!filter.accepts(lifecycleElement, targetClass))
                return false;
        return true;
    }

    /**
     * Checks whether all filters accept the association between the callback method
     * and its parent component
     *
     * @param lifecycleElement
     *            The hosting component's class
     * @param targetMethod
     *            The method implementing the callback
     * @return True if all filters accept the given component-callback mapping,
     *         otherwise false
     */
    private boolean filterAccepts(SootClass lifecycleElement, SootMethod targetMethod) {
        for (ICallbackFilter filter : callbackFilters)
            if (!filter.accepts(lifecycleElement, targetMethod))
                return false;
        return true;
    }




    /**
     * for screen in WorkerList
     * if screen.hasVisibleMenu --> available actions (close the menu, select an item)
     * else forany callback
     * if select item --> ignore
     * if screen.hasClosedMenu --> available action (open the menu), either transition manually added after each screen generation or tmp methods added to fake callback and ignored otherwise
     */

    /**
     * for dialogs, technically can be defined in any callback
     * let's assume found by backstage!
     * for activity check if there's a dialog
     * if so then check if current scr
     *
     * TODO: dialogs defined in menus handler wouldn't be provided by backstage (which item created the creation of the dialogs,just the dialog analysis)
     * OnClickDialog can refer to any dialog element right ? need check the ui element analysis or compare with backstage( refine)
     */



}
