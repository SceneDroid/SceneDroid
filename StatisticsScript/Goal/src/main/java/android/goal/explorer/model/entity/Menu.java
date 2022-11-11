package android.goal.explorer.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import soot.SootMethod;
import soot.Unit;


public class Menu extends AbstractEntity {

    private String name;
    private Integer buttonId;
    private String button; //TO-DO: assign to menu resource id name and use for transition label
    private List<Integer> items;
    private transient Map<Integer, SootMethod> menuItemsCallbacks;

    private String layoutFile;

    private static final String contentDesc = "More options";

    public Menu(Integer resId) {
        super(resId, Type.MENU, "More options", "More options");
        items = new ArrayList<>();
    }

    public Menu(st.cs.uni.saarland.de.entities.Menu menu){
        super(menu.getId(), Type.MENU);
    }

    public Menu(Integer resId, List<Integer> items) {
        super(resId, Type.MENU, "More options", "More options");
        this.items = items;
    }


    public Menu(Integer resId, Map<Integer, SootMethod> menuItemsCallbacks, boolean dynamicallyDeclared) {
       // String cDesc = dynamicallyDeclared ? "Menu" : "More options";
        super(resId, Type.MENU, "More options", "More options");
        if(dynamicallyDeclared){
            setOpenEntityContentDesc("Menu");
            setCloseEntityContentDesc("Menu");
        }

        if (menuItemsCallbacks != null)
            this.items = Lists.newArrayList(menuItemsCallbacks.keySet());
        this.menuItemsCallbacks = menuItemsCallbacks;
    }

    public Menu(Integer resId, Map<Integer, SootMethod> menuItemsCallbacks) {
        this(resId, menuItemsCallbacks, false);
    }

    public Menu(Integer resId, String name, Map<Integer, SootMethod> menuItemsCallbacks, boolean dynamicallyDeclared) {
        this(resId, menuItemsCallbacks, dynamicallyDeclared);
        this.name = name;
    }

    public Menu(Integer resId, String name, Map<Integer, SootMethod> menuItemsCallbacks){
        this(resId, name, menuItemsCallbacks, false);
    }


    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    /**
     * Gets the resource id of the items
     * @return The resource ids of the items
     */
    public List<Integer> getItems() {
        return items;
    }


    public Map<Integer, SootMethod> getMenuItemsCallbacks(){
        return menuItemsCallbacks;
    }

    public void setMenuItemsCallbacks(Map<Integer, SootMethod> menuItemsCallbacks){
        this.menuItemsCallbacks = menuItemsCallbacks;
    }

    /**
     * Adds an item to this entity
     * @param item the item to be added
     * @return true if successfully added
     */
    public boolean addItem(Integer item) {
        return items.add(item);
    }

    /**
     * Adds a list of items to this entity
     * @param items the list of items to be added
     * @return true if successfully added
     */
    public boolean addItems(List<Integer> items) {
        return this.items.addAll(items);
    }

    /**
     * Gets the layout file associated with this menu
     * @return The layout file
     */
    public String getLayoutFile() {
        return layoutFile;
    }

    /**
     * Sets the layout file
     * @param layoutFile The layout file
     */
    public void setLayoutFile(String layoutFile) {
        this.layoutFile = layoutFile;
    }

    /**
     * Gets the button which opens the menu
     * @return The button
     */
    public String getButton() {
        return button;
    }

    public Integer getButtonId() {
        return buttonId;
    }

    /**
     * Sets the button which opens the menu
     * @param button The button
     */
    public void setButton(String button) {
        this.button = button;
    }

    public void setButtonId(Integer id) { this.buttonId = id; }

    public static String getContentDesc(){
        return contentDesc;
    }

    /*public void setContentDesc(String contentDesc){
        this.contentDesc = contentDesc;
    }*/

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        result = prime * result + ((button == null) ? 0 : button.hashCode());
        result = prime * result + ((buttonId == null) ? 0 : buttonId);
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((menuItemsCallbacks == null) ? 0 : menuItemsCallbacks.hashCode());
        return result;
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

        Menu other = (Menu) obj;

        if (items == null) {
            if (other.getItems() != null)
                return false;
        } else if (!items.equals(other.getItems()))
            return false;

        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;

        if (menuItemsCallbacks == null){
            if (other.menuItemsCallbacks != null)
                return false;
        } else if (!menuItemsCallbacks.equals(other.menuItemsCallbacks))
            return false;
        if(buttonId == null) {
            if(other.buttonId != null)
                return false;
        }
        else if(!buttonId.equals(other.buttonId))
            return false;

        if (button == null) {
            return other.getButton() == null;
        } else return button.equals(other.getButton());
    }

    @Override
    public String toString(){
        List<String> itemString = new ArrayList<>();
        if(menuItemsCallbacks != null)
            menuItemsCallbacks.keySet().forEach(x -> itemString.add(Integer.toString(x)));
        return "Menu - resId: " + getResId() + ((buttonId != null)?" -buttonId "+buttonId: "")+"; items: " + itemString;
    }

    @Override
    public Menu clone() {
        Menu menu = new Menu(getResId(), getItems());
        if (button != null) menu.setButton(button);
        if (buttonId != null) menu.setButtonId(buttonId);
        if (name != null) menu.setName(name);
        if (layoutFile != null) menu.setLayoutFile(layoutFile);
        if (getParentClass() != null) menu.setParentClass(getParentClass());
        if (getCallbackMethods() != null) menu.addCallbackMethods(getCallbackMethods());
        if (menuItemsCallbacks != null) menu.setMenuItemsCallbacks(menuItemsCallbacks);
        menu.setOpenEntityContentDesc(getOpenEntityContentDesc());
        menu.setCloseEntityContentDesc(getCloseEntityContentDesc());

        return menu;
    }
}
