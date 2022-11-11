package android.goal.explorer.model.component;

import android.goal.explorer.model.entity.IntentFilter;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootClass;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import org.pmw.tinylog.Logger;

import static android.goal.explorer.utils.AxmlUtils.processNodeName;

public class AbstractComponent implements Serializable {
    private String name;
    private String shortName;
    private transient SootClass mainClass;
    private transient Set<SootClass> addedClasses;

    // Callback methods
    @XStreamOmitField()
    protected transient Set<AndroidCallbackDefinition> callbacks;

    // Lifecycle methods
    @XStreamOmitField()
    private transient LinkedList<MethodOrMethodContext> lifecycleMethods;

    public AbstractComponent(String name) {
        this.name = name;
    }

    public AbstractComponent(String name, String packageName){
        this.name = name;
        if(this.name.length() < packageName.length()){
            Logger.error("Issue with app name {} and package name {}", name, packageName);
            setShortName(name);
        }
        else setShortName(this.name.substring(packageName.length()));
        this.addedClasses = new HashSet<>();
        this.callbacks = new HashSet<>();
        this.lifecycleMethods = new LinkedList<>();
    }

    public AbstractComponent(AXmlNode node, String packageName) {
        this.name = processNodeName(node, packageName);
        if(this.name.length() < packageName.length()){
            Logger.error("Issue with app name {} and package name {}", name, packageName);
            setShortName(name);
        }
        else setShortName(this.name.substring(packageName.length()));
        this.addedClasses = new HashSet<>();
        this.callbacks = new HashSet<>();
        this.lifecycleMethods = new LinkedList<>();
    }

    public AbstractComponent(AXmlNode node, SootClass sc, String packageName) {
        this.name = processNodeName(node, packageName);
        if(this.name.length() < packageName.length()){
            Logger.error("Issue with app name {} and package name {}", name, packageName);
            setShortName(name);
        }
        else setShortName(this.name.substring(packageName.length()));
        this.mainClass = sc;
        addedClasses = new HashSet<>();
        callbacks = new HashSet<>();
        lifecycleMethods = new LinkedList<>();
    }

    public AbstractComponent(String name, SootClass sc, String packageName) {
        this.name = name;
        if(this.name.length() < packageName.length()){
            Logger.error("Issue with app name {} and package name {}", name, packageName);
            setShortName(name);
        }
        else setShortName(this.name.substring(packageName.length()));
        this.mainClass = sc;
        addedClasses = new HashSet<>();
        callbacks = new HashSet<>();
        lifecycleMethods = new LinkedList<>();
    }

    public AbstractComponent(String name, SootClass sc) {
        this.name = name;
        setShortName(this.name.substring(name.lastIndexOf('.')+1));
        this.mainClass = sc;
        addedClasses = new HashSet<>();
        callbacks = new HashSet<>();
        lifecycleMethods = new LinkedList<>();
    }

    /**
     * Creates the intent filters from the intent filter string
     * @param action list of action intent filters
     * @param category list of category intent filters
     * @return Set of IntentFilters
     */
    Set<IntentFilter> createIntentFilters(List<String> action, List<String> category) {
        Set<IntentFilter> intentFilters = new HashSet<>();
        if (action != null && !action.isEmpty()) {
            for (String actionFilter : action) {
                intentFilters.add(new IntentFilter(actionFilter, IntentFilter.Type.Action));
            }
        }

        if (category != null && !category.isEmpty()) {
            for (String categoryFilter : category) {
                intentFilters.add(new IntentFilter(categoryFilter, IntentFilter.Type.Category));
            }
        }
        return intentFilters;
    }

    /* =======================================
              Getters and setters
     =========================================*/
    /**
     * Gets the name of this component
     * @return The name of this component
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the component
     * @param name The component name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the short name of the component
     * @return The short name of the component
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the short name of the component
     * @param shortName The short name of the cscreenBuilderomponent
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * Gets the parse SootClass of this component
     * @return The parse SootClass
     */
    public SootClass getMainClass() {
        return mainClass;
    }

    /**
     * Sets the parse SootClass of this componescreenBuildernt
     * @param mainClass The parse soot class
     */
    public void setMainClass(SootClass mainClass) {
        this.mainClass = mainClass;
    }

    /**
     * Gets the added SootClasses of this component
     * @return The added SootClasses of this component
     */
    public Set<SootClass> getAddedClasses() {
        return addedClasses;
    }

    /**
     * Adds the added SootClasses to this component
     * @param addedClasses The added SootClasses
     */
    public void addAddedClasses(Set<SootClass> addedClasses) {
        if (this.addedClasses == null)
            this.addedClasses = new HashSet<>();
        this.addedClasses.addAll(addedClasses);
    }

    /**
     * Adds the added SootClass of this component
     * @param addedClass The added SootClasses
     */
    public void addAddedClass(SootClass addedClass) {
        if (this.addedClasses == null)
            this.addedClasses = new HashSet<>();
        this.addedClasses.add(addedClass);
    }

    /**
     * Gets all lifecycle methods of this component
     * @return The lifecycle methods
     */
    public LinkedList<MethodOrMethodContext> getLifecycleMethods() {
        return lifecycleMethods;
    }

    /**
     * Adds all lifecycle methods to this component
     * @param lifecycleMethods The lifecycle methods to be set
     */
    public void addLifecycleMethods(LinkedList<MethodOrMethodContext> lifecycleMethods) {
        this.lifecycleMethods.addAll(lifecycleMethods);
    }

    /**
     * Adds a lifecycle method to this activity
     * @param lifecycleMethod The lifecycle method to be added
     */
    public void addLifecycleMethod(MethodOrMethodContext lifecycleMethod) {
        this.lifecycleMethods.add(lifecycleMethod);
    }

    /**
     * Gets the callback definitions in the current component
     * @return The callback definitions
     */
    public Set<AndroidCallbackDefinition> getCallbacks() {
        if (callbacks!=null && !callbacks.isEmpty()){
            return callbacks;
        } else {
            return new HashSet<>();
        }
    }

    /**
     * Adds the callback definition to the current component
     * @return true if we have successfully added the callbacks
     */
    public boolean addCallbacks(Set<AndroidCallbackDefinition> callbacks) {
        return this.callbacks.addAll(callbacks);
    }

    /**
     * Adds the callback definition to the current component
     * @return true if we have successfully added the callbacks
     */
    public boolean addCallback(AndroidCallbackDefinition callback) {
        return this.callbacks.addAll(Collections.singleton(callback));
    }

    public boolean removeCallback(AndroidCallbackDefinition callback){
        return this.callbacks.remove(callback);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((mainClass == null) ? 0 : mainClass.hashCode());
        //result = prime * result + ((addedClasses == null) ? 0 : addedClasses.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;

        AbstractComponent other = (AbstractComponent) obj;
        if (mainClass == null) {
            if (other.mainClass != null)
                return false;
        } else if (!mainClass.equals(other.mainClass))
            return false;
        if (addedClasses == null) {
            if (other.addedClasses != null)
                return false;
        } else if (!addedClasses.equals(other.addedClasses))
            return false;
        if (name == null) {
            return other.name == null;
        } else return name.equals(other.name);
    }

    public String toString() {
        return name;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(mainClass.getName());
        //System.out.println("The added classes before serialization "+addedClasses);
        //oos.writeObject(addedClasses.stream().map(addedClass -> addedClass.getName()).collect(Collectors.toSet()));
    }

    private List<String> writeCallbackDefinition(AndroidCallbackDefinition callback){
        ArrayList<String> callbackInfo = new ArrayList<>();

        callbackInfo.add(callback.getTargetMethod().getSignature());
        callbackInfo.add(callback.getParentMethod().getSignature());
        callbackInfo.add(callback.getCallbackType().name());
        return callbackInfo;
    }

    /*private AndroidCallbackDefinition readCallbackDefinition(List<String> callbackInfo){
        return new AndroidCallbackDefinition(callbackInfo.get())
    }

    private List<String> writeMethodOrMethodContext(MethodOrMethodContext methodOrMethodContext){
        ArrayList<String>
    }*/

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        ois.defaultReadObject();
        SootClass sootClass = Scene.v().getSootClass((String)ois.readObject());
        this.mainClass = sootClass;
        this.addedClasses = new HashSet<>();
        this.callbacks = new HashSet<>();
        this.lifecycleMethods = new LinkedList<>();
                //((Set<String>)ois.readObject()).stream().map(name -> Scene.v().getSootClass(name)).collect(Collectors.toSet());
        //System.out.println("The added classes after serialization "+addedClasses);
    }
}
