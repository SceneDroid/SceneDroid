package android.goal.explorer.model.stg.node;

import android.goal.explorer.model.component.Activity;
import android.goal.explorer.model.component.Fragment;
import android.goal.explorer.model.entity.Drawer;
import android.util.Log;
import soot.SootClass;
import soot.baf.StaticGetInst;
import st.cs.uni.saarland.de.entities.Dialog;
import st.cs.uni.saarland.de.entities.Menu;
import org.pmw.tinylog.Logger;
import st.cs.uni.saarland.de.entities.Tab;
import st.cs.uni.saarland.de.reachabilityAnalysis.UiElement;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ScreenNode extends AbstractNode {

    //should be a map from fragments to container id instead ?
    private final Set<Fragment> staticFragments;
    private Set<Fragment> fragments; //TO REMOVE
    private Map<Integer, Fragment> dynamicFragments;
    private boolean isBaseScreenNode, hasVisibleMenu, hasVisibleDrawer, hasVisibleDialogs, hasVisibleContextMenu;
    private Menu menu;
    private android.goal.explorer.model.entity.Menu visibleMenu, visibleContextMenu;
    private Drawer drawer;
    private Set<Dialog> dialogs;
    private Tab tab;

    /**
     * Constructor of screen node with no fragments nor menu/drawer
     * @param activity The activity
     */
    public ScreenNode(Activity activity) {
        super(activity);
        staticFragments = new HashSet<>();
        dynamicFragments = new HashMap<>();
        dialogs = new HashSet<>();
        isBaseScreenNode = false;
        hasVisibleMenu = false;
        hasVisibleContextMenu = false;
        hasVisibleDrawer = false;
        hasVisibleDialogs = false;
	    //menu = activity.getMenu();
    }

    /**
     * Constructor of screen node with fragments
     * @param activity The activity
     * @param staticFragments The set of fragments
     */
    public ScreenNode(Activity activity, Set<Fragment> staticFragments) {
        super(activity);
        this.staticFragments = staticFragments;
        this.dynamicFragments = new HashMap<>();
        dialogs = new HashSet<>();
	    //menu = activity.getMenu();
    }

    public ScreenNode(Activity activity, Set<Fragment> staticFragments, Set<Dialog> dialogs) {
        super(activity);
        this.staticFragments = staticFragments;
        this.dynamicFragments = new HashMap<>();
        this.dialogs = (dialogs == null)?new HashSet<>(): dialogs;
        hasVisibleDialogs = !this.dialogs.isEmpty();
        //menu = activity.getMenu();
    }

    public ScreenNode(Activity activity, Set<Fragment> staticFragments, Set<Dialog> dialogs, boolean isBaseScreenNode) {
        super(activity);
        this.staticFragments = staticFragments;
        this.dynamicFragments = new HashMap<>();
        this.dialogs = (dialogs == null)?new HashSet<>(): dialogs;
        hasVisibleDialogs = !this.dialogs.isEmpty();
        this.isBaseScreenNode = isBaseScreenNode;
        //menu = activity.getMenu();
    }

    public ScreenNode(Activity activity, Set<Fragment> staticFragments, Map<Integer, Fragment> dynamicFragments){
        super(activity);
        this.staticFragments = staticFragments;
        this.dynamicFragments = dynamicFragments;
        this.dialogs = new HashSet<>();
	    //menu = activity.getMenu();
    }

    public ScreenNode(Activity activity, Set<Fragment> staticFragments, Map<Integer, Fragment> dynamicFragments, Set<Dialog> dialogs){
        super(activity);
        this.staticFragments = staticFragments;
        this.dynamicFragments = dynamicFragments;
        this.dialogs = (dialogs == null)?new HashSet<>(): dialogs;
        hasVisibleDialogs = !this.dialogs.isEmpty();
        //menu = activity.getMenu();
    }

    public ScreenNode(Activity activity, Set<Fragment> staticFragments, Map<Integer, Fragment> dynamicFragments, Set<Dialog> dialogs, boolean isBaseScreenNode){
        super(activity);
        this.staticFragments = staticFragments;
        this.dynamicFragments = dynamicFragments;
        this.dialogs = (dialogs == null)?new HashSet<>(): dialogs;
        hasVisibleDialogs = !this.dialogs.isEmpty();
        this.isBaseScreenNode = isBaseScreenNode;
        //menu = activity.getMenu();
    }

    /**
     * A copy constructor
     * @param toClone The screen to clone
     */
    public ScreenNode(ScreenNode toClone) { //TO REWRITE ?
        super(toClone.getComponent());
        if (toClone.getFragments()!=null && !toClone.getFragments().isEmpty()) {
            staticFragments = new HashSet<>(toClone.getStaticFragments());
            dynamicFragments = new HashMap<>(toClone.getDynamicFragments());
        } else {
            fragments = new HashSet<>();
            staticFragments = new HashSet<>();
            dynamicFragments = new HashMap<>();
        }
        menu = toClone.getMenu();
        drawer = toClone.getDrawer();
        dialogs = new HashSet<>(toClone.getDialogs());
        hasVisibleDialogs = !this.dialogs.isEmpty();
        tab = toClone.getTab();
    }

    /**
     * Clone the screen
     * @return The new screen which is a copy of the original screen
     */
    public ScreenNode clone(){
        ScreenNode screenNode = new ScreenNode((Activity) this.getComponent());
        screenNode.addStaticFragments(staticFragments);
        screenNode.addDynamicFragments(dynamicFragments);
        screenNode.isBaseScreenNode = isBaseScreenNode;
        screenNode.hasVisibleMenu = hasVisibleMenu;
        screenNode.hasVisibleDialogs = hasVisibleDialogs;
        screenNode.hasVisibleDrawer = hasVisibleDrawer;
        screenNode.hasVisibleContextMenu = hasVisibleContextMenu;
        screenNode.visibleMenu = visibleMenu;
        screenNode.visibleContextMenu = visibleContextMenu;
        screenNode.tab = tab;
        if (menu != null)
            screenNode.setMenu(menu);
        if (!dialogs.isEmpty())
            screenNode.dialogs.addAll(dialogs);
        return screenNode;
    }


    public boolean isBaseScreenNode() {
        return isBaseScreenNode;
    }

    public void setAsBaseScreenNode(boolean baseScreenNode) {
        isBaseScreenNode = baseScreenNode;
    }

    /**
     * Gets the fragments
     * @return The fragments
     */
    public Set<Fragment> getFragments() {
        Set<Fragment> fragments = new HashSet<>(staticFragments);
        fragments.addAll(dynamicFragments.values());
        //Logger.debug("The current screen node "+this.getName()+" contains "+fragments.size()+" fragments: "+fragments);
        return fragments;
    }


    public Collection<UiElement> getUiElements(){
        Collection<UiElement> uiElements = new HashSet<>();
        uiElements.addAll(((Activity)getComponent()).getUiElements());
        staticFragments.forEach(fragment -> uiElements.addAll(fragment.getUiElements()));
        dynamicFragments.values().forEach(fragment -> uiElements.addAll(fragment.getUiElements()));
        return uiElements;
    }

    /**
     * Gets the static fragments
     */
    public Set<Fragment> getStaticFragments(){
        return staticFragments;
    }

    /**
     * Gets the dynamic fragments
     */
    public Map<Integer, Fragment> getDynamicFragments(){
        return dynamicFragments;
    }

    /**
     * Gets the fragment layouts
     * @return a mapping from a fragment container id to a fragment class
     */
    public Map<Integer, SootClass> getFragmentLayout(){
        return dynamicFragments.entrySet().stream().collect(Collectors.toMap(Map.Entry<Integer, Fragment>::getKey, entry -> (entry.getValue()).getMainClass()));
    }

    /**
     * Adds a set of fragments to the activity
     * @param fragments The set of fragments to be added
     */
    public void addFragments(Set<Fragment> fragments) { //TO FIX
        this.fragments.addAll(fragments);
    }

    /**
     * Adds a set of fragments (static) to the activity
     * @param fragments static fragments
     */
    public void addStaticFragments(Set<Fragment> fragments){
        this.staticFragments.addAll(fragments);
    }

    public void addStaticFragment(Fragment fragment){
        this.staticFragments.add(fragment);
    }

    public void addDynamicFragments(Map<Integer, Fragment> dynamicFragments){
        this.dynamicFragments.putAll(dynamicFragments);
    }

    public void addDynamicFragment(int containerId, Fragment fragment){
        this.dynamicFragments.put(containerId, fragment);
    }

    /**
     * Adds a fragment to the activity
     * @param fragment The fragment to be added
     */
    public void addFragment(Fragment fragment) { //TO FIX
        this.fragments.add(fragment);
    }

    /**
     * Menu of this screen node
     * @return gets the menu
     */
    public Menu getMenu() {
        return menu;
    }

    public android.goal.explorer.model.entity.Menu getMenuEntity() {
        Activity activity = (Activity) getComponent();
        android.goal.explorer.model.entity.Menu entity = activity.getMenuEntity();
        if(entity == null){
            for(Fragment fragment: staticFragments){
                if(fragment.hasMenu())
                    return fragment.getMenuEntity();
            }
            for(Fragment fragment: dynamicFragments.values()){
                if(fragment.hasMenu())
                    return fragment.getMenuEntity();
            }
        }
        return entity;
    }

    public Set<android.goal.explorer.model.entity.Menu> getContextMenuEntities() {
        Activity activity = (Activity) getComponent();
        Set<android.goal.explorer.model.entity.Menu> entities = activity.getContextMenuEntities();
        staticFragments.forEach(fragment -> entities.addAll(fragment.getContextMenuEntities()));
        dynamicFragments.values().forEach(fragment -> entities.addAll(fragment.getContextMenuEntities()));
        return entities;
    }

    public android.goal.explorer.model.entity.Menu getVisibleMenu(){
        return visibleMenu;
    }

    public android.goal.explorer.model.entity.Menu getVisibleContextMenu() {
        return visibleContextMenu;
    }


    /**
     * Drawer of this screen node
     * @return gets the drawer
     */
    public Drawer getDrawer(){
        return drawer;
    } //we can get it from the activity

    /**
     * Gets the dialog of this screen node
     * @return the dialog in this screen node
     */
    public Set<Dialog> getDialogs() {
        return dialogs;
    }

    /**
     * Gets all dialogs within this screen (activity and fragments)
     * @return
     */
    public Set<Dialog> getDialogEntities() {
        Activity activity = (Activity) getComponent();
        Set<Dialog> allDialogs = addFragmentDialogs();
        allDialogs.addAll(activity.getDialogs());
        return allDialogs;
    }

    private Set<Dialog> addFragmentDialogs(){
        Set<Dialog> allDialogs = new HashSet<>();
        staticFragments.forEach(fragment -> allDialogs.addAll(fragment.getDialogs()));
        dynamicFragments.values().forEach(fragment -> allDialogs.addAll(fragment.getDialogs()));
        return allDialogs;
    }

    /**
     * sets the menu of this screen node
     * @param menu The menu to set
     */
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public void setMenu(android.goal.explorer.model.entity.Menu menu) {
        this.visibleMenu = menu;
    }

    public void setContextMenu(android.goal.explorer.model.entity.Menu menu) {this.visibleContextMenu = menu;}

    public void setDrawer(Drawer drawer){
        this.drawer = drawer;
    }

    /**
     * Sets the dialogs of this screen node
     * @param dialogs the dialogs to set
     */
    public void setDialogs(Set<Dialog> dialogs) {
        this.dialogs = dialogs;
    }

    /**
     * Returns if the screen node (or one of its fragments) contains a menu component (open or closed)
     * @return true if
     */
    //TODO keep this vlaue up to date instead of recomputing since we never modify screen nodes after they're built
    public boolean hasMenu() { return ((Activity)this.getComponent()).hasMenu() || this.staticFragments.stream().anyMatch(fragment -> fragment.hasMenu()) || this.dynamicFragments.values().stream().anyMatch(fragment -> fragment.hasMenu());}

    //Fragments menus are already taken into account when setting hasVisibleMenu
    public boolean hasVisibleMenu(){
        return this.hasVisibleMenu;
    } //TODO or the fragments visibleMenus

    public boolean hasVisibleContextMenu() {return this.hasVisibleContextMenu;}

    public boolean hasVisibleDrawer(){
        return this.hasVisibleDrawer;
    }

    public boolean hasVisibleDialogs() {
        return hasVisibleDialogs;
    }

    public boolean hasTab() {return tab != null; }

    public void setMenuVisibility(boolean menuVisibility){
        this.hasVisibleMenu = menuVisibility;
    }

    public void setContextMenuVisibility(boolean isElementVisible) {
        this.hasVisibleContextMenu = isElementVisible;
    }

    public void setDrawerVisibility(boolean drawerVisibility){
        this.hasVisibleDrawer = drawerVisibility;
    }

    public void setDialogsVisibility(boolean dialogsVisibility) {
        this.hasVisibleDialogs = dialogsVisibility;
    }
    public Tab getTab() {
        return tab;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }

    @Override
    public String toString(){
        return getComponent().getName() + " base node: "+isBaseScreenNode+" static fragments: " + staticFragments + "dynamic fragments " + dynamicFragments.values() + " menu: " + menu + " visible menu: " + visibleMenu + " dialogs " + dialogs;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((staticFragments == null) ? 0 : staticFragments.hashCode());
        result = prime * result + ((dynamicFragments == null) ? 0 : dynamicFragments.hashCode());
        result = prime * result + ((menu == null) ? 0 : menu.hashCode());
        result = prime * result + ((visibleMenu == null) ? 0 : visibleMenu.hashCode());
        result = prime * result + ((visibleContextMenu == null) ? 0 : visibleContextMenu.hashCode());
        result = prime * result + ((drawer == null) ? 0 : drawer.hashCode());
        result = prime * result + ((tab == null) ? 0 : tab.hashCode());
        result = prime * result + ((dialogs.isEmpty()) ? 0 : dialogs.hashCode());
        return result;
    }

    public boolean emptyBesidesTab() {
        return staticFragments.isEmpty() &&
                dynamicFragments.isEmpty() &&
                dialogs.isEmpty() &&
                menu == null &&
                visibleMenu == null &&
                visibleContextMenu == null &&
                drawer == null &&
                tab != null;
    }

    public boolean equalsBesidesTabAndTarget(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
//        if (!super.equals(obj))
//            return false;
        if (getClass() != obj.getClass())
            return false;

        ScreenNode other = (ScreenNode) obj;

        if (staticFragments == null) {
            if (other.staticFragments != null)
                return false;
        } else if (!staticFragments.equals(other.staticFragments))
            return false;

        if (dynamicFragments == null) {
            if (other.dynamicFragments != null)
                return false;
        } else if (!dynamicFragments.equals(other.dynamicFragments))
            return false;
        if (visibleMenu == null){
            if(other.visibleMenu != null)
                return false;
        }
        else if (!visibleMenu.equals(other.visibleMenu))
            return false;
        if (visibleContextMenu == null){
            if(other.visibleContextMenu != null)
                return false;
        }
        else if (!visibleContextMenu.equals(other.visibleContextMenu))
            return false;
        if (drawer == null){
            if (other.drawer != null)
                return false;
        }
        else if (!drawer.equals(other.drawer))
            return false;
        if (menu == null) {
            return other.menu == null;
        } else return menu.equals(other.menu);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;

        ScreenNode other = (ScreenNode) obj;

        if (staticFragments == null) {
            if (other.staticFragments != null)
                return false;
        } else if (!staticFragments.equals(other.staticFragments))
            return false;

        if (dynamicFragments == null) {
            if (other.dynamicFragments != null)
                return false;
        } else if (!dynamicFragments.equals(other.dynamicFragments))
            return false;
        if (visibleMenu == null){
            if(other.visibleMenu != null)
                return false;
        }
        else if (!visibleMenu.equals(other.visibleMenu))
            return false;
        if (visibleContextMenu == null){
            if(other.visibleContextMenu != null)
                return false;
        }
        else if (!visibleContextMenu.equals(other.visibleContextMenu))
            return false;
        if (drawer == null){
            if (other.drawer != null)
                return false;
        }
        else if (!drawer.equals(other.drawer))
            return false;
        if (dialogs == null){
            if (other.dialogs != null)
                return false;
        }
        else if (!dialogs.equals(other.dialogs))
            return false;
        if (tab == null){
            if(other.tab != null)
                return false;
        }
        else if (!tab.equals(other.tab))
            return false;
        if (menu == null) {
            return other.menu == null;
        } else return menu.equals(other.menu);
    }
}
