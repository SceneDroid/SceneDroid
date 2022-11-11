package android.goal.explorer.model.entity;

import java.util.HashSet;
import java.util.Set;

public class IntentInfo {

    private String className;
    private String data;
    private String action;
    private Set<String> extras;


    public IntentInfo(){
        this.extras = new HashSet<>();
    }

    /**
     * Gets the class name of this intent
     * @return the target class name of this intent
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the target class name of this intent
     * @param className The target class name
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the data of this intent
     * @return The data of this intent
     */
    public String getData() {
        return data;
    }

    /**
     * Sets the data of this intent
     * @param data The data of this intent
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Gets the action of this intent
     * @return The action of this intent
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action of this intent
     * @param action The action of this intent
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * Gets the set of extras
     * @return The set of extras
     */
    public Set<String> getExtras() {
        return extras;
    }

    /**
     * Adds an extra to this intent
     * @param extra The extra to be added
     */
    public void addExtra(String extra) {
        this.extras.add(extra);
    }

    @Override
    public String toString(){
        return getClassName() + " : " + getAction() + " -> " + getData() + " ; " + getExtras();
    }

}
