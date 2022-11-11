package android.goal.explorer.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import soot.SootMethod;
import soot.Unit;


public class Drawer extends AbstractEntity {

    private String name;
    private String button; //TO-DO: assign to menu resource id name and use for transition label

    private Menu visibleMenu;
    private st.cs.uni.saarland.de.entities.Menu backStageMenu;
    
    private String layoutFile;

    private List<Integer> items;

    private String openDrawerDesc, closeDrawerDesc;

    public Drawer(Integer resId) {
        super(resId, Type.DRAWER);
        items = new ArrayList<>();
    }

    public Drawer(Integer resId, List<Integer> items) {
        super(resId, Type.DRAWER);
        this.items = items;
    }

    public Drawer(Integer resId, st.cs.uni.saarland.de.entities.Menu menu){
        super(resId, Type.DRAWER);
    }

    public Drawer(Integer resId, Menu menu, st.cs.uni.saarland.de.entities.Menu backstageMenu){
        super(resId, Type.DRAWER);
        this.backStageMenu = backstageMenu;
        this.visibleMenu = menu;
    }


    public Drawer(Integer resId, Menu menu){
        super(resId, Type.DRAWER); //here put menu.getResId ?
        this.visibleMenu = menu;
    }

    public Menu getMenu(){
        return this.visibleMenu;
    }

    public void setMenu(Menu menu){
        this.visibleMenu = menu;
    }

    /**
     * Gets the resource id of the items
     * @return The resource ids of the items
     */
    public List<Integer> getItems() {
        return items;
    }

    public Map<Integer, SootMethod> getMenuItemsCallbacks(){
        return visibleMenu.getMenuItemsCallbacks();
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
     * Gets the button which opens the menu
     * @return The button
     */
    public String getButton() {
        return button;
    }

    /**
     * Sets the button which opens the menu
     * @param button The button
     */
    public void setButton(String button) {
        this.button = button;
    }

    public String getOpenDrawerDesc(){
        return this.openDrawerDesc;
    }

    public String getCloseDrawerDesc(){
        return this.closeDrawerDesc;
    }

    public void setContentDescs(String openDrawerDesc, String closeDrawerDesc) {
        setOpenEntityContentDesc(openDrawerDesc);
        setCloseEntityContentDesc(closeDrawerDesc);
        this.openDrawerDesc = openDrawerDesc;
        this.closeDrawerDesc = closeDrawerDesc;
    }

    @Override
    public int hashCode(){
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((items == null) ? 0 : items.hashCode());
        result = prime * result + ((button == null) ? 0 : button.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) { //TO-UPDATE
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;

        Drawer other = (Drawer) obj;

        if (items == null) {
            if (other.items != null)
                return false;
        } else if (!items.equals(other.items))
            return false;
        if (visibleMenu == null){
            if(other.visibleMenu != null)
                return false;
        }
        else if(!visibleMenu.equals(other.visibleMenu))
            return false;
        if (button == null) {
            return other.button == null;
        } else return button.equals(other.button);
    }

    @Override
    public String toString(){
        return "Drawer - button: "+ button +" menu: "+visibleMenu;
        //return "Drawer - button: " + button + " items: " + items;
    }

    @Override
    public Drawer clone() {
        Drawer drawer = new Drawer(getResId(), getItems());
        if (button != null) drawer.setButton(button);
        if (getItems() != null) drawer.addItems(items);
        //if (visibleMenu != null) 
        if (getParentClass() != null) drawer.setParentClass(getParentClass());
        if (getCallbackMethods() != null) drawer.addCallbackMethods(getCallbackMethods());
        if (visibleMenu != null) drawer.setMenu(visibleMenu);
        return drawer;
    }
}
