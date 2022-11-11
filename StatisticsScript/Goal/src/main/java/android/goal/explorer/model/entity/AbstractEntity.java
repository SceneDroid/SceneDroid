package android.goal.explorer.model.entity;

import soot.MethodOrMethodContext;
import soot.SootClass;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractEntity implements Serializable {

    public enum Type {
        MENU, DRAWER, DIALOG
    }

    private Integer resId;
    private Type type;

    private String openEntityContentDesc, closeEntityContentDesc;

    private SootClass parentClass;
    private Set<MethodOrMethodContext> callbackMethods;

    AbstractEntity(Integer resId, Type type, String openEntityContentDesc, String closeEntityContentDesc) {
        this.resId = resId;
        this.type = type;
        this.openEntityContentDesc = openEntityContentDesc;
        this.closeEntityContentDesc = closeEntityContentDesc;
        this.callbackMethods = new HashSet<>();
    }

    AbstractEntity(Integer resId, Type type) {
        this.resId = resId;
        this.type = type;
        this.callbackMethods = new HashSet<>();
    }

    AbstractEntity(Type type) {
        this.type = type;
        this.callbackMethods = new HashSet<>();
    }

    /**
     * Gets the resource id of this entity
     * @return The resource id of this entity
     */
    public Integer getResId() {
        return resId;
    }

    /**
     * Sets the resource id of this entity
     * @param resId The resource id to be set
     */
    public void setResId(Integer resId) {
        this.resId = resId;
    }



    public Type getType(){
        return type;
    }

    public void setType(Type type){
        this.type = type;
    }


    public String getOpenEntityContentDesc(){
        return openEntityContentDesc;
    }

    public String getCloseEntityContentDesc(){
        return closeEntityContentDesc;
    }


    public void setOpenEntityContentDesc(String openEntityContentDesc){
        this.openEntityContentDesc = openEntityContentDesc;
    }

    public void setCloseEntityContentDesc(String closeEntityContentDesc){
        this.closeEntityContentDesc = closeEntityContentDesc;
    }
    

    /**
     * Adds callback methods to this menu
     * @param callbackMethods The callback methods to be added
     */
    public void addCallbackMethods(Set<MethodOrMethodContext> callbackMethods) {
        this.callbackMethods.addAll(callbackMethods);
    }

    /**
     * Adds a callback method to this menu
     * @param callbackMethod The callback method to be added
     */
    public void addCallbackMethod(MethodOrMethodContext callbackMethod) {
        this.callbackMethods.add(callbackMethod);
    }

    /**
     * Gets the callback methods in this menu
     * @return The callback methods
     */
    public Set<MethodOrMethodContext> getCallbackMethods(){
        return this.callbackMethods;
    }

    /**
     * Gets the soot class where this menu is declared
     * @return The soot class where this menu is declared
     */
    public SootClass getParentClass() {
        return parentClass;
    }

    /**
     * Sets the soot class where this menu is declared
     * @param parentClass The soot class where this menu is declared
     */
    public void setParentClass(SootClass parentClass) {
        this.parentClass = parentClass;
    }

    public String toString() {
        return "Type: " + type + "; resId: " + resId;
    }

    public abstract AbstractEntity clone();

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AbstractEntity other = (AbstractEntity) obj;
        return other.getResId().equals(other.getResId());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((resId == null) ? 0 : resId.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }
}
