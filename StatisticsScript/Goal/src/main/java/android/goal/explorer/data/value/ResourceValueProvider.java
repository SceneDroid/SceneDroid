package android.goal.explorer.data.value;

import org.pmw.tinylog.Logger;
import soot.jimple.infoflow.android.resources.ARSCFileParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceValueProvider {
    private final String RESOURCE_PARSER = "ResourceValueProvider";

    private static ResourceValueProvider instance;

    // Atomic ID counter for assigning unique IDs
    private static AtomicInteger idCounter = new AtomicInteger(100000000);

    // Mappings
    private Map<Integer, String> idToString;
    private Map<String, Integer> stringToId;

    private Map<Integer, String> stringResource;
    private Map<Integer, String> resourceId;
    private Map<String, Integer> layoutResource;
    private Map<String, Integer> menuResId;

    private ResourceValueProvider(){}

    public static synchronized ResourceValueProvider v() {
        if (null == instance)
            instance = new ResourceValueProvider();
        return instance;
    }

    /**
     * Gets and increment the unique ID
     * @return The unique ID
     */
    public static int getNewUniqueID() {
        return idCounter.getAndIncrement();
    }

    /**
     * Initialize the resources of the resource value provider
     * @param resPackages The resource packages from the ARSC resource parser
     */
    public void initializeResources(List<ARSCFileParser.ResPackage> resPackages) {
        // initialize the resource packages for resource value provider
        stringResource = new HashMap<>();
        resourceId = new HashMap<>();
        layoutResource = new HashMap<>();
        menuResId = new HashMap<>();

        // Collect the resource mappings
        for (ARSCFileParser.ResPackage resPackage : resPackages) {
            for (ARSCFileParser.ResType resType : resPackage.getDeclaredTypes()) {
               // Logger.debug("Possible resource types {} {}", resType.getTypeName(), resType.getAllResourceNames());
                switch (resType.getTypeName()) {
                    case "string":
                        // only keep English Strings
                        for (ARSCFileParser.ResConfig string : resType.getConfigurations()) {
                            if (string.getConfig().getLanguage().equals("\u0000\u0000")) {
                                for (ARSCFileParser.AbstractResource resource : string.getResources()) {
                                    if (resource instanceof ARSCFileParser.StringResource) {
                                        stringResource.put(resource.getResourceID(), ((ARSCFileParser.StringResource) resource).getValue());
                                    }
                                }
                            }
                        }
                        break;
                    case "id":
                        for (ARSCFileParser.ResConfig resIdConfig : resType.getConfigurations()) {
                            for (ARSCFileParser.AbstractResource resource : resIdConfig.getResources()) {
                                resourceId.put(resource.getResourceID(), resource.getResourceName());
                            }
                        }
                        break;
                    case "layout":
                        for (ARSCFileParser.ResConfig resLayoutConfig : resType.getConfigurations()) {
                            for (ARSCFileParser.AbstractResource resource : resLayoutConfig.getResources()) {
                                layoutResource.put(resource.getResourceName(), resource.getResourceID());
                            }
                        }
                        break;
                    case "menu":
                        for (ARSCFileParser.ResConfig resLayoutConfig : resType.getConfigurations()) {
                            for (ARSCFileParser.AbstractResource resource : resLayoutConfig.getResources()) {
                                menuResId.put(resource.getResourceName(), resource.getResourceID());
                            }
                        }
                    //add navigation ?
                }
            }
        }

        Logger.debug(String.format(
                "Resources Initialized\n" +
                        "\t stringResources: %d\n" +
                        "\t resourceIds: %d\n" +
                        "\t layoutResources: %d\n" +
                        "\t menuResIds: %d\n",
                stringResource.size(),
                resourceId.size(),
                layoutResource.size(),
                menuResId.size())
        );
    }

    public String getStringById(Integer id) {
        return stringResource.get(id);
    }

    public String getResourceIdString(Integer id) {
        return resourceId.get(id);
    }

    public Integer getResouceIdByString(String resourceString) {
        for (Integer key : resourceId.keySet()) {
            if (resourceId.get(key).equalsIgnoreCase(resourceString))
                return key;
        }
        return null;
    }

    public String findResourceById(Integer id) {
        String resString = getStringById(id);
        if (resString!=null)
            return resString;
        else {
            resString = getLayoutResourceString(id);
            if (resString!=null)
                return resString;
            else {
                Logger.warn("[{}] Cannot find layout ");
                return null;
            }
        }
    }

    public Integer getLayoutResourceId(String filename) {
        return layoutResource.get(filename);
    }

    public String getLayoutResourceString(Integer resId) {
        for (String layoutString : layoutResource.keySet()) {
            if (layoutResource.get(layoutString).equals(resId)) {
                return layoutString;
            }
        }
        return null;
    }

    public Map<String, Integer> getMenuResId(){
        return menuResId;
    }

    public String getMenuResourceName(Integer resId){
        for (String menu: menuResId.keySet()){
            if (menuResId.get(menu).equals(resId))
                return menu;
        }
        return null;
    }

    public Integer getMenuResIdByLayoutFile(String key) {
        return menuResId.get(key);
    }

}
