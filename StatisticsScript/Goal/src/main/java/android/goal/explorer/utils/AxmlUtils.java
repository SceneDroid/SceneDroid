package android.goal.explorer.utils;

import android.goal.explorer.model.entity.IntentFilter;
import android.goal.explorer.model.widget.EditWidget;
import pxb.android.axml.AxmlVisitor;
import soot.jimple.infoflow.android.axml.AXmlAttribute;
import soot.jimple.infoflow.android.axml.AXmlNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AxmlUtils {

    /**
     * Find the name of current node
     * @param node The axml node to be parsed
     * @param packageName The package name of the app
     * @return The name of current node
     */
    public static String processNodeName(AXmlNode node, String packageName){
        String className = null;
        AXmlAttribute<?> attrEnabled = node.getAttribute("enabled");
        if (attrEnabled == null || !attrEnabled.getValue().equals(Boolean.FALSE)) {
            // process name
            AXmlAttribute<?> attrName = node.getAttribute("name");
            if (attrName != null){
                className = expandClassName((String) attrName.getValue(), packageName);
            } else {
                // This component does not have a name, so this might be
                // obfuscated malware. We apply a heuristic.
                for (Map.Entry<String, AXmlAttribute<?>> a : node.getAttributes().entrySet())
                    if (a.getValue().getName().isEmpty() && a.getValue().getType() == AxmlVisitor.TYPE_STRING) {
                        String name = (String) a.getValue().getValue();
                        if (isValidComponentName(name))
                            className = expandClassName(name, packageName);
                    }
            }
        }
        return className;
    }

    /**
     * Find the parent activity of current activity node
     * @param node The current activity node
     * @param packageName The package name of the app
     * @return The parent node of current activity node
     */
    public static String processNodeParent(AXmlNode node, String packageName){
        String className = null;
        AXmlAttribute<?> attrEnabled = node.getAttribute("enabled");
        if (attrEnabled == null || !attrEnabled.getValue().equals(Boolean.FALSE)) {
            // process name
            AXmlAttribute<?> attrName = node.getAttribute("parentActivityName");
            if (attrName != null){
                className = expandClassName((String) attrName.getValue(), packageName);
            }
        }
        return className;
    }

    /**
     * Find if the node is exported in the manifest
     * @param node The Axml node
     * @return True if it is exported
     */
    public static boolean processNodeExported(AXmlNode node) {
        AXmlAttribute<?> attrEnabled = node.getAttribute("enabled");
        if (attrEnabled == null || !attrEnabled.getValue().equals(Boolean.FALSE)) {
            // process name
            AXmlAttribute<?> attrExported = node.getAttribute("exported");
            if (attrExported == null)
                return true;
            else {
                return (Boolean)attrExported.getValue();
            }
        }
        return false;
    }

    /**
     * Find the intent filters specified in the current node
     * @param node The current axml node
     * @param type which type of intent filters we want to parse (action or category)
     * @return The intent filters as a list of strings
     */
    public static List<String> processIntentFilter(AXmlNode node, IntentFilter.Type type){
        String typeString;
        switch (type) {
            case Action: typeString = "action";
            case Category: typeString = "category";
            default: typeString = "null";
        }

        List<AXmlNode> ifNodes = node.getChildrenWithTag("intent-filter");
        List<String> intentFilters = new ArrayList<>();

        for (AXmlNode ifNode : ifNodes){
            for (AXmlNode subnode : ifNode.getChildrenWithTag(typeString)){
                intentFilters.add((String) subnode.getAttribute("name").getValue());
            }
        }

        return intentFilters;
    }

    /**
     * Find the authorities of the content provider node
     * @param node The current axml node
     * @return The authorities of the content provider
     */
    public static String processAuthorities(AXmlNode node){
        AXmlAttribute<?> attrAuthorities = node.getAttribute("authorities");
        if (attrAuthorities!=null)
            return String.valueOf(attrAuthorities.getValue());
        else
            return null;
    }

    /**
     * Checks if the specified name is a valid Android component name
     *
     * @param name
     *            The Android component name to check
     * @return True if the given name is a valid Android component name,
     *         otherwise false
     */
    public static boolean isValidComponentName(String name) {
        if (name.isEmpty())
            return false;
        if (name.equals("true") || name.equals("false"))
            return false;
        if (Character.isDigit(name.charAt(0)))
            return false;

        return name.startsWith(".");

        // Be conservative
    }

    /**
     * Generates a full class name from a short class name by appending the
     * globally-defined package when necessary
     *
     * @param className
     *            The class name to expand
     * @return The expanded class name for the given short name
     */
    public static String expandClassName(String className, String packageName) {
        if (className.startsWith("."))
            return packageName + className;
        else if (!className.contains("."))
            return packageName + "." + className;
        else
            return className;
    }

    public static EditWidget createEditWidgetNode(AXmlNode node){
        int resourceId = -1;
        String text = "";
        String contentDescription = "";
        String hint = "";
        int inputType = -1;

        AXmlAttribute<?> attrId = node.getAttribute("id");
        if (attrId != null){
            resourceId = Integer.parseInt((String) attrId.getValue());
        }
        AXmlAttribute<?> attrText = node.getAttribute("text");
        if (attrText != null){
            text = (String) attrText.getValue();
        }
        AXmlAttribute<?> attrContent = node.getAttribute("contentDescription");
        if (attrContent != null){
            contentDescription = (String) attrContent.getValue();
        }
        AXmlAttribute<?> attrHint = node.getAttribute("hint");
        if (attrHint != null){
            hint = (String) attrHint.getValue();
        }
        AXmlAttribute<?> attrInputType = node.getAttribute("inputType");
        if (attrInputType != null){
            inputType = (Integer) attrInputType.getValue();
        }

        return new EditWidget(resourceId, text, contentDescription, hint, inputType);
    }
}
