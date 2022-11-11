package android.goal.explorer.model;

import android.goal.explorer.data.value.ResourceValueProvider;
import android.goal.explorer.model.entity.Menu;
import org.pmw.tinylog.Logger;
import soot.SootClass;
import soot.jimple.infoflow.android.resources.LayoutFileParser;
import soot.jimple.infoflow.android.resources.controls.AndroidLayoutControl;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LayoutManager implements Serializable {
    private final MultiMap<Integer, AndroidLayoutControl> userControls;
    private final MultiMap<String, String> callbackMethods;
    private final transient MultiMap<String, SootClass> fragments;
    private final Map<Integer, Menu> menus;

    /**
     * Setup the layout manager
     * @param lfp The layout file parser
     */
    public LayoutManager(LayoutFileParser lfp) {
        this.userControls = new HashMultiMap<>();
        this.menus = new HashMap<>();
        if(lfp != null){
            this.callbackMethods = lfp.getCallbackMethods();
            this.fragments = lfp.getFragments();
            // put the user controls in (id, layout) map
            for (String key : lfp.getUserControls().keySet()) {
                this.userControls.putAll(ResourceValueProvider.v().
                        getLayoutResourceId(key.substring(key.lastIndexOf("/")+1, key.lastIndexOf("."))),
                        lfp.getUserControls().get(key));
            }
        //Logger.debug("The user controls, fragments {} {}", userControls, fragments);
        }
        else{
            this.callbackMethods =  new HashMultiMap<>();
            this.fragments = new HashMultiMap<>();
        }
    }

    /* ================================
            Getters and setters
     =================================*/
    /**
     * Gets all user controls (layouts)
     * @return All user controls (layouts)
     */
    public MultiMap<Integer, AndroidLayoutControl> getUserControls() {
        return userControls;
    }

    /**
     * Gets the user controls found in the layout XML file. The result is a mapping
     * from the id to the respective layout control.
     *
     * @return The layout controls found in the XML file.
     */
    public AndroidLayoutControl findUserControlById(int resId) {
        for (AndroidLayoutControl lc : this.userControls.values()) {
            if (lc.getID() == resId)
                return lc;
        }
        Logger.warn("Cannot find user control by id: {}", resId);
        return null;
    }

    /**
     * Gets the user control by resource id
     * @param resId The resource id of the user control
     * @return The user control
     */
    public Set<AndroidLayoutControl> findUserControlsById(int resId) {
        Set<AndroidLayoutControl> layoutControls = userControls.get(resId);
        if (layoutControls == null)
            Logger.warn("[WARN] Cannot find user control by resource id: {}", resId);
        return layoutControls;
    }

    /**
     * Gets all callback methods defined in layout files
     * @return All user callback methods
     */
    public MultiMap<String, String> getCallbackMethods() {
        return callbackMethods;
    }

    /**
     * Gets all fragments
     * @return All fragments
     */
    public MultiMap<String, SootClass> getFragments() {
        return fragments;
    }

    /**
     * Gets all fragments in layout file by filename
     * @param name The name of the layout file
     * @return The fragments
     */
    public Set<SootClass> getFragmentsByFilename(String name) {
        return fragments.get(name);
    }

    /**
     * Gets all menus mapped to resource id
     * @return All menus in a map
     */
    public Map<Integer, Menu> getMenus(){ return menus; }

    /**
     * Gets menu by resource id
     * @param resId The resource id of the menu
     * @return The menu with given resource id
     */
    public Menu getMenu(Integer resId){ return menus.get(resId); }
}
