package android.goal.explorer.model.component;

import android.goal.explorer.analysis.value.values.fields.SetFieldValue;
import android.goal.explorer.data.android.constants.MethodConstants;
import android.goal.explorer.model.entity.Drawer;
import android.goal.explorer.model.entity.IntentFilter;
import android.goal.explorer.model.entity.Listener;
import android.goal.explorer.model.widget.AbstractWidget;
import android.goal.explorer.utils.AxmlUtils;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootClass;
import soot.jimple.infoflow.android.axml.AXmlNode;
import st.cs.uni.saarland.de.entities.Dialog;
import st.cs.uni.saarland.de.entities.Menu;
import st.cs.uni.saarland.de.entities.Tab;
import st.cs.uni.saarland.de.entities.XMLLayoutFile;
import st.cs.uni.saarland.de.reachabilityAnalysis.UiElement;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;

import static android.goal.explorer.utils.SootUtils.findAndAddMethod;

public class Activity extends AbstractComponent  {

    private transient Set<MethodOrMethodContext> menuOnCreateMethods;
    private transient Set<MethodOrMethodContext> menuCallbackMethods;
    private transient Set<MethodOrMethodContext> contextMenuOnCreateMethods, contextMenuCallbackMethods;
    private transient Set<MethodOrMethodContext> drawerMenuCallbackMethods;
    private transient Set<MethodOrMethodContext> dialogCallbackMethods;


    private Set<String> menuOnCreateMethodsStrings;
    private Set<String> contextMenuOnCreateMethodsStrings;
    private Set<String> menuCallbackMethodsStrings;
    private Set<String> contextMenuCallbackMethodsStrings;
    private Set<String> drawerMenuCallbackMethodsStrings;
    private  Set<String> dialogCallbackMethodsStrings;

    private Set<IntentFilter> intentFilters;
    private Set<Listener> listeners;
    private Set<Fragment> fragments;
    private Set<AbstractWidget> widgets;

    private Set<XMLLayoutFile> layoutFiles;

    private Integer resourceId;
    private Integer mainXmlLayoutResId;
    private Set<Integer> addedXmlLayoutResId;
    private String alias = null;

    private String parentCompString;
    private AbstractComponent parentComp;
    private Set<String> childCompStrings;
    private Set<AbstractComponent> childComps;

    // For BACKSTAGE
    private Map<Integer, XMLLayoutFile> layouts;
    private Map<Integer, UiElement> uiElementsMap;
    //private Menu menu; //rename to backstage menu
    private Menu backstageMenu;
    private android.goal.explorer.model.entity.Menu visibleMenu;
    private Set<Menu> backstageContextMenus;
    private Drawer drawer;
    private Set<Dialog> dialogs;
    private Set<Tab> tabs;
    private Set<android.goal.explorer.model.entity.Menu> visibleContextMenus;


    public Activity(AXmlNode node, SootClass sc, String packageName) {
        super(node, sc, packageName);

        this.intentFilters = createIntentFilters(AxmlUtils.processIntentFilter(node, IntentFilter.Type.Action),
                AxmlUtils.processIntentFilter(node, IntentFilter.Type.Category));

        parentCompString = AxmlUtils.processNodeParent(node, packageName);
        resourceId = -1;

        menuOnCreateMethods = new HashSet<>();
        menuCallbackMethods = new HashSet<>();
        contextMenuOnCreateMethods = new HashSet<>();
        contextMenuCallbackMethods = new HashSet<>();
        drawerMenuCallbackMethods = new HashSet<>();
        dialogCallbackMethods = new HashSet<>();

        menuOnCreateMethodsStrings = new HashSet<>();
        menuCallbackMethodsStrings = new HashSet<>();
        contextMenuOnCreateMethodsStrings = new HashSet<>();
        contextMenuCallbackMethodsStrings = new HashSet<>();
        drawerMenuCallbackMethodsStrings = new HashSet<>();
        dialogCallbackMethodsStrings = new HashSet<>();

        listeners = new HashSet<>();
        fragments = new HashSet<>();
        widgets = new HashSet<>();
        childCompStrings = new HashSet<>();
        childComps = new HashSet<>();

        // For BACKSTAGE
        layouts = new HashMap<>();
        uiElementsMap = new HashMap<>();
        layoutFiles = new HashSet<>();
        dialogs = new HashSet<>();
        backstageContextMenus = new HashSet<>();
        tabs = new HashSet<>();

        visibleContextMenus = new HashSet<>();
    }

    /* ========================================
                Getters and setters
       ========================================*/

    /**
     * Gets the resource id of this activity
     * @return The resource id of the activity
     */
    public Integer getResourceId(){ return resourceId; }

    /**
     * Sets the resource id of this activity
     * @param resourceId The resource id of the activity
     */
    public void setResourceId(Integer resourceId){ this.resourceId = resourceId; }

    /**
     * Gets the intent filters of this activity
     * @return The intent filters
     */
    public Set<IntentFilter> getIntentFilters() {
        return intentFilters;
    }

    /**
     * Adds a new intent filter to this activity
     * @param intentFilter The intent filter to be added
     */
    public void addIntentFilter(IntentFilter intentFilter) {
        this.intentFilters.add(intentFilter);
    }

    /**
     * Adds new intent filter to this activity
     * @param intentFilters The intent filters to be added
     */
    public void addIntentFilters(Set<IntentFilter> intentFilters) {
        this.intentFilters.addAll(intentFilters);
    }

    /**
     * Gets the menu methods of this activity
     * @return The menu methods
     */
    public Set<MethodOrMethodContext> getMenuOnCreateMethods() {
        return menuOnCreateMethods;
    }

    public Set<MethodOrMethodContext> getContextMenuOnCreateMethods() {return contextMenuCallbackMethods;}

    public Set<String> getMenuOnCreateMethodsStrings() {
        return menuOnCreateMethodsStrings;
    }

    /**
     * Adds the menu methods to this activity
     * @param menuMethods The menu methods to be added
     */
    public void addMenuOnCreateMethods(List<String> menuMethods) {
        this.menuOnCreateMethodsStrings.addAll(menuMethods);
        this.callbacks.stream().filter(callback -> menuOnCreateMethodsStrings.contains(callback.getTargetMethod().getSubSignature()))
                .forEach(callback -> menuOnCreateMethods.add(callback.getTargetMethod()));
    }


    /**
     * Adds a menu method to this activity
     * @param menuMethod The menu method to be added
     */
    public void addMenuOnCreateMethod(String menuMethod) {
        this.menuOnCreateMethodsStrings.add(menuMethod);
    }

    public void addContextMenuOnCreateMethods(List<String> contextMenuMethods){
        this.contextMenuOnCreateMethodsStrings.addAll(contextMenuMethods);
        this.callbacks.stream().filter(callback -> contextMenuOnCreateMethodsStrings.contains(callback.getTargetMethod().getSubSignature()))
                .forEach(callback -> contextMenuOnCreateMethods.add(callback.getTargetMethod()));
    }

    /**
     * Gets the menu methods of this activity
     * @return The menu methods
     */
    public Set<MethodOrMethodContext> getMenuCallbackMethods() {
        return menuCallbackMethods;
    }
    public Set<MethodOrMethodContext> getContextMenuCallbackMethods() { return contextMenuCallbackMethods;}

    public Set<String> getMenuCallbackMethodsStrings() {
        return menuCallbackMethodsStrings;
    }

    /**
     * Adds the menu methods to this activity
     * @param menuMethods The menu methods to be added
     */
    public void addMenuCallbackMethods(List<String> menuMethods) {
        this.menuCallbackMethodsStrings.addAll(menuMethods);
        this.callbacks.stream().filter(callback -> menuCallbackMethodsStrings.contains(callback.getTargetMethod().getSubSignature()))
                .forEach(callback -> menuCallbackMethods.add(callback.getTargetMethod()));
    }

    /**
     * Adds a menu method to this activity
     * @param menuMethod The menu method to be added
     */
    public void addMenuCallbackMethod(String menuMethod) {
        this.menuCallbackMethodsStrings.add(menuMethod);
    }

    public void addContextMenuCallbackMethods(List<String> contextMenuMethods){
        this.contextMenuCallbackMethodsStrings.addAll(contextMenuMethods);
        this.callbacks.stream().filter(callback -> contextMenuCallbackMethodsStrings.contains(callback.getTargetMethod().getSubSignature()))
                .forEach(callback -> contextMenuCallbackMethods.add(callback.getTargetMethod()));
    }


    /**
     * Gets the drawer menu methods of this activity
     * @return
     */
    public Set<MethodOrMethodContext> getDrawerMenuCallbackMethods() {
        return drawerMenuCallbackMethods;
    }

    public Set<String> getDrawerMenuCallbackMethodsStrings() {
        return drawerMenuCallbackMethodsStrings;
    }

    /**
     * Adds the drawer menu methods to this activity
     * @param menuMethods The menu methods to be added
     */
    public void addDrawerMenuCallbackMethods(List<String> menuMethods) {
        this.drawerMenuCallbackMethodsStrings.addAll(menuMethods);
        this.callbacks.stream().filter(callback -> drawerMenuCallbackMethodsStrings.contains(callback.getTargetMethod().getSubSignature()))
                .forEach(callback -> this.drawerMenuCallbackMethods.add(callback.getTargetMethod()));

    }

    /**
     * Adds a drawer menu methods to this activity
     * @param drawerMenuMethod The menu method to be added
     */
    public void addDrawerMenuCallbackMethod(String drawerMenuMethod) {
        this.drawerMenuCallbackMethodsStrings.add(drawerMenuMethod);
    }


    /**
     * Gets the dialog callbacks in this activity (i.e )
     * @return
     */
    public Set<MethodOrMethodContext> getDialogCallbackMethods() {
        return this.dialogCallbackMethods;
    }
    public void addDialogCallbackMethods(List<String> dialogMethods){
        this.dialogCallbackMethodsStrings.addAll(dialogMethods);
        this.callbacks.stream().filter(callback -> dialogCallbackMethodsStrings.contains(callback.getTargetMethod().getSubSignature()))
                                .forEach(callback -> this.dialogCallbackMethods.add(callback.getTargetMethod()));

    }


    /**
     * Gets the resource id of the parse XML layout file of this activity
     * @return The resource id of the parse XML layout file of this activity
     */
    public Integer getMainXmlLayoutResId() {
        return mainXmlLayoutResId;
    }

    /**
     * Sets the resource id of the parse XML layout file of this activity
     * @param mainXmlLayoutResId The resource id of the parse XML layout file of this activity
     */
    public void setMainXmlLayoutResId(Integer mainXmlLayoutResId) {
        this.mainXmlLayoutResId = mainXmlLayoutResId;
    }

    /**
     * Gets the resource ids of the added XML layout file of this activity
     * @return The resource ids of the added XML layout file of this activity
     */
    public Set<Integer> getAddedXmlLayoutResId() {
        return addedXmlLayoutResId;
    }

    /**
     * Sets the resource id of the added XML layout file of this activity
     * @param addedXmlLayoutResId The resource id of the added XML layout file of this activity
     */
    public void setAddedXmlLayoutResId(Set<Integer> addedXmlLayoutResId) {
        this.addedXmlLayoutResId = addedXmlLayoutResId;
    }

    /**
     * Gets the alias of this activity
     * @return The alias of this activity
     */
    public String getAlias() {
        return alias;
    }

    /**
     * Sets the alias of this activity
     * @param target The alias of this activity
     */
    public void setAlias(String target) {
        alias = target;
    }

    /**
     * Gets all listeners of this activity
     * @return All listeners in this activity
     */
    public Set<Listener> getListeners() {
        return listeners;
    }

    /**
     * Adds new listeners to this activity
     * @param listeners The listeners to be added
     */
    public void addListeners(Set<Listener> listeners) {
        if (this.listeners == null)
            this.listeners = new HashSet<>();
        this.listeners.addAll(listeners);
    }

    /**
     * Adds a new listener to this activity
     * @param listener The listener to be added
     */
    public void addListener(Listener listener) {
        if (this.listeners == null)
            this.listeners = new HashSet<>();
        this.listeners.add(listener);
    }

    /**
     * Gets all fragments implemented in this activity
     * @return All fragments implemented in this activity
     */
    public Set<Fragment> getFragments() {
        return fragments;
    }

    /**
     * Adds a new fragment to this activity
     * @param fragment The new fragment to be added
     */
    public void addFragment(Fragment fragment) {
        if (this.fragments == null)
            this.fragments = new HashSet<>();
        this.fragments.add(fragment);
    }

    /**
     * Gets all widgets implemented in this activity
     * @return All widgets implemented in this activity
     */
    public Set<AbstractWidget> getWidgets() {
        return widgets;
    }

    /**
     * Gets the parent component name in String
     * @return The parent component name in String
     */
    public String getParentCompString() {
        return this.parentCompString;
    }

    /**
     * Sets the parent component name in String
     * @param parent The parent component name in String
     */
    public void setParentCompString(String parent) {
        this.parentCompString = parent;
    }

    /**
     * Adds a new widget to this activity
     * @param widget The new widget to be added to this activity
     */
    public void addWidget(AbstractWidget widget) {
        this.widgets.add(widget);
    }

    /**
     * Gets the parent component from manifest
     * @return The parent component from manifest
     */
    public AbstractComponent getParentComp() {
        return parentComp;
    }

    /**
     * Sets the parent component from manifest
     * @param parentComp The parent component from manifest
     */
    public void setParentComp(AbstractComponent parentComp) {
        this.parentComp = parentComp;
    }

    /**
     * Gets the child component (string) from manifest
     * @return The child component (string) from manifest
     */
    public Set<String> getChildCompStrings() {
        return childCompStrings;
    }

    /**
     * Adds a child component (string) from manifest to this activity
     * @param childCompString The child component (string) from manifest to be added
     */
    public void addChildCompString(String childCompString) {
        this.childCompStrings.add(childCompString);
    }

    /**
     * Gets the child component from manifest
     * @return The child component from manifest
     */
    public Set<AbstractComponent> getChildComps() {
        return childComps;
    }

    /**
     * Adds a child component from manifest
     * @param childComp The child component from manifest to be added
     */
    public void addChildComp(AbstractComponent childComp) {
        this.childComps.add(childComp);
    }

    /*============================
    for BACKSTAGE
     */
    /**
     * Gets the layouts associated with the activity
     * @return The layouts
     */
    public Map<Integer, XMLLayoutFile> getLayouts() {
        return layouts;
    }

    /**
     * Sets the layouts associated with the activity
     */
    public void setLayouts(Map<Integer, XMLLayoutFile> layouts) {
        this.layouts.putAll(layouts);
    }

    public void addLayout(Integer resId, XMLLayoutFile layout) {
        layouts.put(resId, layout);
    }

    public void getLayout(Integer resId) {
        layouts.get(resId);
    }


    public boolean hasMenu(){
        return backstageMenu != null;
    }

    public boolean hasContextMenu() { return backstageContextMenus != null &&  !backstageContextMenus.isEmpty();}

    public boolean hasDrawer(){
        return drawer != null;
    }

    //public boolean hasDialogs() { return dialogs != null && ! dialogs.isEmpty()};


    public Menu getMenu() {
        return backstageMenu;
    }

    public Menu getBackstageMenu(){
        return backstageMenu;
    }


    public void setBackstageMenu(Menu menu) {
        this.backstageMenu = menu;
        //this.menu = menu;
    }

    public Set<Menu> getBackstageContextMenus() {
        return backstageContextMenus;
    }

    public void addBackstageContextMenu(Menu menu){
        this.backstageContextMenus.add(menu);
    }

    public android.goal.explorer.model.entity.Menu getVisibleMenu(){
        return visibleMenu;
    }

    public android.goal.explorer.model.entity.Menu getMenuEntity(){
        return visibleMenu;
    }

    public void setMenu(android.goal.explorer.model.entity.Menu menu){
        visibleMenu = menu;
    }

    public void setMenuEntity(android.goal.explorer.model.entity.Menu menu){
        this.visibleMenu = menu;
    }

    public Set<android.goal.explorer.model.entity.Menu> getContextMenuEntities() { return this.visibleContextMenus;}

    public void setContextMenuEntities(Set<android.goal.explorer.model.entity.Menu> contextMenus) { this.visibleContextMenus.addAll(contextMenus);}

    public Drawer getDrawerEntity(){
        return drawer;
    }

    public Drawer getDrawer(){
        return drawer;
    }

    public void setDrawer(Drawer drawer){
        this.drawer = drawer;
    }

    public Set<Dialog> getDialogs() {
        return dialogs;
    }

    public void setDialogs(Set<Dialog> dialogs) {
        this.dialogs = dialogs;
    }

    public boolean hasDialogs(){
        return this.dialogs != null && !this.dialogs.isEmpty();
    }
    public void addDialog(Dialog dialog) {
        this.dialogs.add(dialog);
    }

    public Set<Tab> getTabs() {
        return tabs;
    }

    public void setTabs(Set<Tab> tabs) {
        this.tabs = tabs;
    }

    public void addTab(Tab tab) {
        this.tabs.add(tab);
    }

    /**
     * Gets the lifecycle methods before the activity is running
     * @return The lifecycle methods before the activity is running
     */
    public LinkedList<MethodOrMethodContext> getLifecycleMethodsPreRun() {
        LinkedList<MethodOrMethodContext> lifecycleMethodsPreRun = new LinkedList<>();

        List<String> lifecycleMethods = MethodConstants.Activity.getlifecycleMethodsPreRun();

        // Collect the list of lifecycle methods pre-run (in order)
        for (String lifecycleMethod : lifecycleMethods) {
            MethodOrMethodContext method = findAndAddMethod(lifecycleMethod, this);
            if (method != null) lifecycleMethodsPreRun.add(method);
        }
        return lifecycleMethodsPreRun;
    }

    /**
     * Gets the lifecycle methods when the activity is paused
     * @return The lifecycle methods when the activity is paused
     */
    public LinkedList<MethodOrMethodContext> getLifecycleMethodsOnPause() {
        LinkedList<MethodOrMethodContext> lifecycleMethodsOnPause = new LinkedList<>();

        List<String> lifecycleMethods = MethodConstants.Activity.getlifecycleMethodsOnPause();

        // Collect the list of lifecycle methods pre-run (in order)
        lifecycleMethods.iterator().forEachRemaining(x -> {
            MethodOrMethodContext method = findAndAddMethod(x, this);
            if (method!=null)
                lifecycleMethodsOnPause.add(method);
        });

        return lifecycleMethodsOnPause;
    }

    /**
     * Gets the lifecycle methods when the activity is stopped
     * @return The lifecycle methods when the activity is stopped
     */
    public LinkedList<MethodOrMethodContext> getLifecycleMethodsOnStop() {
        LinkedList<MethodOrMethodContext> lifecycleMethodsOnStop = new LinkedList<>();

        List<String> lifecycleMethods = MethodConstants.Activity.getlifecycleMethodsOnStop();

        // Collect the list of lifecycle methods pre-run (in order)
        lifecycleMethods.iterator().forEachRemaining(x -> {
            MethodOrMethodContext method = findAndAddMethod(x, this);
            if (method!=null)
                lifecycleMethodsOnStop.add(method);
        });

        return lifecycleMethodsOnStop;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = prime * super.hashCode();
        //result = prime * result + ((resourceId == null || resourceId) ? 0 : resourceId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;
        if (!super.equals(obj))
            return false;

        Activity other = (Activity) obj;

        /*if (!resourceId.equals(other.resourceId))
            return false;*/
        return getName().equals(other.getName());
    }

    public Collection<UiElement> getUiElements() {
        return uiElementsMap.values();
    }

    public UiElement getUiElement(Integer id) {
        return uiElementsMap.getOrDefault(id, null);
    }

    public void addUiElement(Integer id, UiElement uiElement) {

        uiElementsMap.put(id, uiElement);
    }


    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        this.menuOnCreateMethods = new HashSet<>();
        this.menuCallbackMethods = new HashSet<>();
        this.drawerMenuCallbackMethods = new HashSet<>();
        this.dialogCallbackMethods = new HashSet<>();
    }


}
