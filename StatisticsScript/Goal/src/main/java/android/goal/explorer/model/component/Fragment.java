package android.goal.explorer.model.component;

import android.goal.explorer.model.entity.Listener;
import android.goal.explorer.model.widget.AbstractWidget;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import soot.MethodOrMethodContext;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition;
import st.cs.uni.saarland.de.entities.Dialog;
import st.cs.uni.saarland.de.entities.Menu;
import st.cs.uni.saarland.de.reachabilityAnalysis.UiElement;

import java.util.*;

public class Fragment extends AbstractComponent {

    @XStreamOmitField()
    private Set<Listener> listeners;
    @XStreamOmitField()
    private Set<AbstractWidget> widgets;

    @XStreamOmitField()
    private Set<Integer> resourceIds; //Not sure what these resource ids are for
    //Should keep the container id as well (useful for replace)
    //Or put it in the screen instead?
    @XStreamOmitField()
    private Set<Activity> parentActivities;

    @XStreamOmitField()
    private Set<MethodOrMethodContext> menuRegistrationMethods;
    @XStreamOmitField()
    private Set<MethodOrMethodContext> menuCallbackMethods;
    @XStreamOmitField()
    private  Set<MethodOrMethodContext> contextMenuRegistrationMethods, contextMenuCallbackMethods;
    @XStreamOmitField
    private Set<SootMethod> dialogCallbackMethods;

    private Menu backstageMenu;
    private android.goal.explorer.model.entity.Menu visibleMenu;
    private Set<android.goal.explorer.model.entity.Menu> visibleContextMenus;
    private Set<Menu> backstageContextMenus;
    private Set<Dialog> dialogs;
    private Map<Integer, UiElement> uiElementsMap; //will be populated with the declaring classes of ui elements
    private boolean isUpToDate = false;

    public Fragment(SootClass sootClass, Activity parentActivity) {
        super(sootClass.getName(), sootClass);
        this.parentActivities = new HashSet<>(Collections.singletonList(parentActivity));
        this.menuRegistrationMethods = new HashSet<>();
        this.menuCallbackMethods = new HashSet<>();
        this.contextMenuRegistrationMethods = new HashSet<>();
        this.contextMenuCallbackMethods = new HashSet<>();
        this.backstageContextMenus = new HashSet<>();
        this.visibleContextMenus = new HashSet<>();
        this.resourceIds = new HashSet<>();
        this.dialogs = new HashSet<>();
        this.dialogCallbackMethods = new HashSet<>();
        this.uiElementsMap = new HashMap<>();
    }

    public Fragment(SootClass sootClass) {
        super(sootClass.getName(), sootClass);
        this.menuRegistrationMethods = new HashSet<>();
        this.menuCallbackMethods = new HashSet<>();
        this.contextMenuRegistrationMethods = new HashSet<>();
        this.contextMenuCallbackMethods = new HashSet<>();
        this.backstageContextMenus = new HashSet<>();
        this.visibleContextMenus = new HashSet<>();
        this.resourceIds = new HashSet<>();
        this.parentActivities = new HashSet<>();
        this.dialogs = new HashSet<>();
        this.dialogCallbackMethods = new HashSet<>();
        this.uiElementsMap = new HashMap<>();
    }

    public Fragment(SootClass sootClass, Set<Integer> resourceIds) {
        super(sootClass.getName(), sootClass);
        this.parentActivities = new HashSet<>();
        this.resourceIds = (resourceIds !=null)?resourceIds: new HashSet<>();
        this.menuRegistrationMethods = new HashSet<>();
        this.menuCallbackMethods = new HashSet<>();
        this.contextMenuRegistrationMethods = new HashSet<>();
        this.contextMenuCallbackMethods = new HashSet<>();
        this.backstageContextMenus = new HashSet<>();
        this.visibleContextMenus = new HashSet<>();
        //this.resourceIds = new HashSet<>();
        this.dialogs = new HashSet<>();
        this.dialogCallbackMethods = new HashSet<>();
        this.uiElementsMap = new HashMap<>();

    }

    /*
    Getters and setters
     */

    public Set<Listener> getListeners() {
        return listeners;
    }

    public Set<AbstractWidget> getWidgets() {
        return widgets;
    }

    public Collection<UiElement> getUiElements() { return uiElementsMap.values(); }

    public Set<Integer> getResourceIds() {
        return resourceIds;
    }

    public Set<Activity> getParentActivities() {
        return parentActivities;
    }

    public Set<Dialog> getDialogs() {
        return dialogs;
    }

    public android.goal.explorer.model.entity.Menu getMenuEntity () {
        return visibleMenu;
    }

    public Set<android.goal.explorer.model.entity.Menu> getContextMenuEntities() { return this.visibleContextMenus;}

    public Menu getBackstageMenu() {
        return backstageMenu;
    }

    public Set<Menu> getBackstageContextMenus() {
        return backstageContextMenus;
    }

    public boolean hasMenu(){
        return backstageMenu != null;
    }

    public boolean hasContextMenu() { return backstageContextMenus != null && !backstageContextMenus.isEmpty();}

    public void setMenu(Menu menu) {
        this.backstageMenu = menu;
    }

    public void setMenuEntity(android.goal.explorer.model.entity.Menu menu){
        this.visibleMenu = menu;
    }

    public void setContextMenuEntities(Set<android.goal.explorer.model.entity.Menu> contextMenus) { this.visibleContextMenus.addAll(contextMenus);}


    public boolean isUpToDate() {return isUpToDate;}

    public void setIsUpToDate(boolean isUpToDate) { this.isUpToDate = isUpToDate;}



    /**
     * Adds a listener to the fragment
     * @param listener The listener to be added
     */
    public void addListener(Listener listener) {
        this.listeners.add(listener);
    }

    public void addUiElement(UiElement uiElement) {
        this.uiElementsMap.put(uiElement.globalId, uiElement);
    }

    /**
     * Adds a widget to the fragment
     * @param widget The widget to be added
     */
    public void addWidget(AbstractWidget widget) {
        this.widgets.add(widget);
    }

    /**
     * Sets the resource id of the fragment
     * @param resourceIds The resource ids of the fragment
     */
    public void setResourceIds(Set<Integer> resourceIds) {
        this.resourceIds = resourceIds;
    }

    /**
     * Adds the parent activity to the fragment. This method adds another activity
     * @param parentActivity The parent activity
     */
    public void AddParentActivity(Activity parentActivity) {
        this.parentActivities.add(parentActivity);
    }

    /**
     * Gets the menu methods of this activity
     * @return The menu methods
     */
    public Set<MethodOrMethodContext> getMenuRegistrationMethods() {
        return menuRegistrationMethods;
    }

    public Set<MethodOrMethodContext> getContextMenuRegistrationMethods() {
        return contextMenuRegistrationMethods;
    }

    public void addBackstageContextMenu(Menu menu){
        backstageContextMenus.add(menu);
    }

    /**
     * Adds the menu methods to this activity
     * @param menuMethods The menu methods to be added
     */
    public void addMenuRegistrationMethods(Set<MethodOrMethodContext> menuMethods) {
        this.menuRegistrationMethods.addAll(menuMethods);
    }

    /**
     * Adds a menu method to this activity
     * @param menuMethod The menu method to be added
     */
    public void addMenuRegistrationMethod(MethodOrMethodContext menuMethod) {
        this.menuRegistrationMethods.add(menuMethod);
    }

    public void addContextMenuRegistrationMethods(Set<MethodOrMethodContext> menuMethods) {
        this.contextMenuRegistrationMethods.addAll(menuMethods);
    }

    public void addContextMenuRegistrationMethod(MethodOrMethodContext menuMethod) {
        this.contextMenuRegistrationMethods.add(menuMethod);
    }

    /**
     * Gets the menu methods of this activity
     * @return The menu methods
     */
    public Set<MethodOrMethodContext> getMenuCallbackMethods() {
        return menuCallbackMethods;
    }

    public Set<MethodOrMethodContext> getContextMenuCallbackMethods() {
        return contextMenuCallbackMethods;
    }

    /**
     * Adds the menu methods to this activity
     * @param menuMethods The menu methods to be added
     */
    public void addMenuCallbackMethods(Set<MethodOrMethodContext> menuMethods) {
        this.menuCallbackMethods.addAll(menuMethods);
    }

    public void addContextMenuCallbackMethods(Set<MethodOrMethodContext> menuMethods) {
        this.contextMenuCallbackMethods.addAll(menuMethods);
    }

    /**
     * Adds a menu method to this activity
     * @param menuMethod The menu method to be added
     */
    public void addMenuCallbackMethod(MethodOrMethodContext menuMethod) {
        this.menuCallbackMethods.add(menuMethod);
    }

    public void addContextMenuCallbackMethod(MethodOrMethodContext menuMethod) {
        this.contextMenuCallbackMethods.add(menuMethod);
    }

    public Set<SootMethod> getDialogCallbackMethods() {
        return dialogCallbackMethods;
    }

    public void addDialog(Dialog dialog) {
        this.dialogs.add(dialog);
    }

    //TODO move to topology extend
    public void addDialogCallbackMethod(SootMethod method) {
        this.dialogCallbackMethods.add(method);
        this.addCallback(new AndroidCallbackDefinition(method, method, AndroidCallbackDefinition.CallbackType.Widget));
    }

    @Override
    public String toString(){
        return getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((listeners == null) ? 0 : listeners.hashCode());
        result = prime * result + ((widgets == null) ? 0 : widgets.hashCode());
        result = prime * result + (resourceIds.isEmpty() ? 0 : resourceIds.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        if (!super.equals(obj))
            return false;

        Fragment other = (Fragment) obj;
        return this.getMainClass().equals(other.getMainClass());
    }
}
