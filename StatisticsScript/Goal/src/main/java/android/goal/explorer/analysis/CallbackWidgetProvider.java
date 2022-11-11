package android.goal.explorer.analysis;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import android.goal.explorer.model.stg.node.ScreenNode;
import org.pmw.tinylog.Logger;

import android.R.layout;
import android.goal.explorer.analysis.value.identifiers.Argument;
import android.goal.explorer.analysis.value.managers.ArgumentValueManager;
import android.goal.explorer.data.value.ResourceValueProvider;
import android.goal.explorer.model.App;
import android.goal.explorer.model.LayoutManager;
import android.goal.explorer.model.component.Activity;

import java.util.*;

import soot.SootMethod;
import soot.SootClass;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.Stmt;
import soot.Local;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition;
import soot.jimple.infoflow.android.resources.controls.AndroidLayoutControl;
import soot.toolkits.scalar.Pair;
import st.cs.uni.saarland.de.reachabilityAnalysis.UiElement;

import static android.goal.explorer.analysis.AnalysisUtils.extractIntArgumentFrom;


/**
 * provides information about the widget that triggers the given callback
 */
public class CallbackWidgetProvider implements Serializable {
    private final String TAG = "CallbackWidgetProvider";

    private static CallbackWidgetProvider instance;

    protected SetupApplication setupApplication;
    protected LayoutManager layoutManager;
    protected Map<SootMethod, AndroidLayoutControl> layoutControls; //keep map from callback to layout control instead
    protected Map<SootMethod, UiElement> widgetsMap; //here is the main thing I care about?
    protected Map<Integer, AndroidLayoutControl> userControls;

    //TO-DO: how to integrate with backstage here ?

    public CallbackWidgetProvider(){
        widgetsMap = new HashMap<>();
    }

    public static synchronized CallbackWidgetProvider v() {
        if (null == instance)
            instance = new CallbackWidgetProvider();
        return instance;
    }

    

    public void initializeProvider(SetupApplication setupApplication, LayoutManager layoutManager){
        this.setupApplication = setupApplication;
        this.layoutManager = layoutManager;
        this.userControls = setupApplication.getLayoutFileParser().getUserControlsByID();
    }


    public UiElement findWidget(AndroidCallbackDefinition callback, Activity activity){
        if(!(callback.getCallbackType() == AndroidCallbackDefinition.CallbackType.Widget)){
            Logger.error("Callback {} is not of type widget. Cannot have associated layout", callback); //TODO: think how to deal with the made up callbacks then
            return null;
        }
        return findWidgetForMethod(callback.getTargetMethod(), activity);
    }


    public UiElement findWidgetForMethod(SootMethod method, Activity activity){
        if (widgetsMap.containsKey(method)){
            return widgetsMap.get(method);
        }
        String targetMethod = method.getSignature();
        List<UiElement> matchingUIElements = new ArrayList<>();
        for(UiElement uiElement: activity.getUiElements()){
            if(uiElement.handlerMethod != null ){
                if(uiElement.signature.equals(targetMethod)){ //found the ui element which triggers the callback
                    matchingUIElements.add(uiElement);
                }
            }
        }

        if (matchingUIElements.size() == 1){
            UiElement uiElement = matchingUIElements.get(0);
            Logger.debug("Matching widget {} for callback {}", uiElement, method);
            widgetsMap.put(method, uiElement);
            return uiElement;
        }
        else{
            UiElement widget = findWidget(method);
            /*if(widget != null)
                activity.addUiElement(widget.globalId, widget);*/
            return findWidget(method);
        }
    }

    public UiElement findWidgetForMethod(SootMethod method, ScreenNode screenNode){
        if (widgetsMap.containsKey(method)){
            return widgetsMap.get(method);
        }
        String targetMethod = method.getSignature();
        List<UiElement> matchingUIElements = new ArrayList<>();
        for(UiElement uiElement: screenNode.getUiElements()){
            if(uiElement.handlerMethod != null ){
                if(uiElement.signature.equals(targetMethod)){ //found the ui element which triggers the callback
                    matchingUIElements.add(uiElement);
                }
            }
        }

        if (matchingUIElements.size() == 1){
            UiElement uiElement = matchingUIElements.get(0);
            Logger.debug("Matching widget {} for callback {}", uiElement, method);
            widgetsMap.put(method, uiElement);
            return uiElement;
        }
        else{
            UiElement widget = findWidget(method);
            /*if(widget != null) 
                activity.addUiElement(widget.globalId, widget);*/
            return findWidget(method);
        }
    }

    //TODO refactor?
    public List<UiElement> findWidgetsForMethod(SootMethod method, Activity activity){
        /*if (widgetsMap.containsKey(method)){
            return new ArrayList<>(widgetsMap.get(method));
        }*/

        String targetMethod = method.getSignature();
        List<UiElement> matchingUIElements = new ArrayList<>();
        for(UiElement uiElement: activity.getUiElements()){
            if(uiElement.handlerMethod != null ){
                if(uiElement.signature.equals(targetMethod)){ //found the ui element which triggers the callback
                    matchingUIElements.add(uiElement);
                }
            }
        }

        if (matchingUIElements.size() >= 1){
            //UiElement uiElement = matchingUIElements.get(0);
            Logger.debug("Matching widgets {} for callback {}", matchingUIElements, method);
            //widgetsMap.put(method, uiElement);
            return matchingUIElements;
        }
        else{
            UiElement widget = findWidget(method);
            if(widget != null) {
                //activity.addUiElement(widget.globalId, widget);
                return Collections.singletonList(widget);
            }
            return new ArrayList<>();
            // return new ArrayList<UiElement>(widget);
        }
    }

    public List<UiElement> findWidgetsForMethod(SootMethod method, ScreenNode screenNode){
        /*if (widgetsMap.containsKey(method)){
            return new ArrayList<>(widgetsMap.get(method));
        }*/

        String targetMethod = method.getSignature();
        List<UiElement> matchingUIElements = new ArrayList<>();
        for(UiElement uiElement: screenNode.getUiElements()){
            if(uiElement.handlerMethod != null ){
                if(uiElement.signature.equals(targetMethod)){ //found the ui element which triggers the callback
                    matchingUIElements.add(uiElement);
                }
            }
        }

        if (matchingUIElements.size() >= 1){
            //UiElement uiElement = matchingUIElements.get(0);
            Logger.debug("Matching widgets {} for callback {}", matchingUIElements, method);
            //widgetsMap.put(method, uiElement);
            return matchingUIElements;
        }
        else{
            UiElement widget = findWidget(method);
            if(widget != null) {
                //activity.addUiElement(widget.globalId, widget);
                return Collections.singletonList(widget);
            }
            return new ArrayList<>();
           // return new ArrayList<UiElement>(widget);
        }
    }
    


    public UiElement findWidget(SootMethod sootMethod){
        Logger.debug("Finding widget for {}", sootMethod);

        //TO-DO: propagate up the callgraph to find methods that might call this ?
        Set<AndroidLayoutControl> layouts = findLayoutsForComponent(sootMethod.getDeclaringClass());
        AndroidLayoutControl layoutControl = null;
        if(layouts != null)
            layoutControl = layouts.stream().filter(control -> sootMethod.getName().equals(control.getClickListener())).findFirst().orElse(null);
            
        //Check if layout control has been found, otherwise look for dynamic registration
        if(layoutControl != null){
            String text = (layoutControl.getAdditionalAttributes() != null && layoutControl.getAdditionalAttributes().containsKey("text"))?((String)layoutControl.getAdditionalAttributes().get("text")): null;
            UiElement uiElement = new UiElement(sootMethod, layoutControl.getID(), layoutControl.getViewClass().getShortName(), text);
            widgetsMap.put(sootMethod, uiElement);
            return uiElement;
        }
        Logger.debug("Layout for {} not statically declared, looking for dynamic registration");
        //Looks into dynamic registrations, throws an error if nothing can be found
        Integer resourceId = findResourceId(sootMethod);
        Logger.debug("Found resource id {} for corresponding layout", resourceId);
        if (resourceId != null)
            layoutControl = userControls.get(resourceId);

        if(layoutControl != null){
            String text = (layoutControl.getAdditionalAttributes() != null && layoutControl.getAdditionalAttributes().containsKey("text"))?((String)layoutControl.getAdditionalAttributes().get("text")): null;
            UiElement uiElement = new UiElement(sootMethod, layoutControl.getID(), layoutControl.getViewClass().getShortName(), text);
            widgetsMap.put(sootMethod, uiElement);
            return uiElement;
        }

        Logger.warn("No widget found for callback {}", sootMethod);
        return null;
    }


    

    /**
     * Gets the layout control associated with the callback
     */
    public AndroidLayoutControl findLayoutControl(AndroidCallbackDefinition callback){
        if(!(callback.getCallbackType() == AndroidCallbackDefinition.CallbackType.Widget)){
            Logger.error("Callback {} is not of type widget. Cannot have associated layout", callback); //TODO: think how to deal with the made up callbacks then
            return null;
        }
        if (layoutControls.containsKey(callback.getTargetMethod())){//why not parent method
            //already parsed by FD, unlikely
            return layoutControls.get(callback.getTargetMethod());
        }
        return findLayoutControl(callback.getTargetMethod());
    }

    /**
     * Gets the id of the layout associated with the callback
     * @param callback
     * @return
     */
    public Integer findLayoutID(AndroidCallbackDefinition callback){
        if(!(callback.getCallbackType() == AndroidCallbackDefinition.CallbackType.Widget)){
            Logger.error("Callback {} is not of type widget. Cannot have associated layout", callback); //TODO: think how to deal with the made up callbacks then
            return null;
        }
        if (layoutControls.containsKey(callback.getTargetMethod())){//why not parent method
            //already parsed by FD, unlikely
            return layoutControls.get(callback.getTargetMethod()).getID();
        }
        return findLayoutID(callback.getTargetMethod());
    }

    //make a function that takes the activity as well
    public AndroidLayoutControl findLayoutControl(SootMethod sootMethod){
        Logger.debug("Finding layout for {}", sootMethod);

        //TO-DO: propagate up the callgraph to find methods that might call this ?
        Set<AndroidLayoutControl> layouts = findLayoutsForComponent(sootMethod.getDeclaringClass());
        AndroidLayoutControl layoutControl = null;
        if(layouts != null){
            for(AndroidLayoutControl lc: layouts){
                if(sootMethod.getName().equals(lc.getClickListener())){
                    layoutControl = lc;
                    break;
                }
            }
        }
        //(layouts != null)?layouts.stream().filter(control -> control.getClickListener() == sootMethod.getName()).findFirst().orElse(null): null; //check non empty here

        //Check if layout control has been found, otherwise look for dynamic registration
        if(layoutControl != null){
            layoutControls.put(sootMethod, layoutControl);
            return layoutControl;
        }
        Logger.debug("Layout for {} not statically declared, looking for dynamic registration");
        //Looks into dynamic registrations, throws an error if nothing can be found
        Integer resourceId = findResourceId(sootMethod);
        Logger.debug("Found resource id {} for corresponding layout", resourceId);
        if (resourceId != null)
            layoutControl = userControls.get(resourceId);

        if(layoutControl != null){
            layoutControls.put(sootMethod, layoutControl);
            return layoutControl;
        }

        Logger.warn("No layout control found for callback {}", sootMethod);
        return null;
    }

    private Integer findLayoutID(SootMethod sootMethod){
        Logger.debug("Finding layout id for {}", sootMethod);

        //TO-DO: propagate up the callgraph to find methods that might call this ?
        Set<AndroidLayoutControl> layouts = findLayoutsForComponent(sootMethod.getDeclaringClass());
        AndroidLayoutControl layoutControl = (layouts != null)?layouts.stream().filter(control -> control.getClickListener() == sootMethod.getName()).findFirst().orElse(null): null; //check non empty here

        //Check if layout control has been found, otherwise look for dynamic registration
        if(layoutControl != null){
            layoutControls.put(sootMethod, layoutControl);
            return layoutControl.getID();
        }
        Logger.debug("Layout for {} not statically declared, looking for dynamic registration", sootMethod);
        //Looks into dynamic registrations, throws an error if nothing can be found
        Integer resourceId = findResourceId(sootMethod);
        Logger.debug("Found resource id {} for corresponding layout", resourceId);
        if (resourceId != null)
            layoutControl = userControls.get(resourceId);

        if(layoutControl != null){
            layoutControls.put(sootMethod, layoutControl);
        }
        else
            Logger.warn("No layout control found for callback {}", sootMethod);
        return resourceId;
    }

    private Set<AndroidLayoutControl> findLayoutsForComponent(SootClass component){
        //We analyze the lifecycle method onCreate to find the resource id for the layout file 
        //(do we need to do that ?) // we already have layouts for each activity
        Integer resId = findLayoutResourceId(component);
        if (resId == null)
            return null;
        //we could do app.getActivity(component).getUIElements();
        
       String fileName = ResourceValueProvider.v().getLayoutResourceString(resId);
       Logger.debug("Layout file name returned by resource provider {}", fileName);
       Set<AndroidLayoutControl> layouts = layoutManager.findUserControlsById(resId);
       Logger.debug("The layouts {} \nand {}", layouts);
       //return flowDroidAnalysis.layoutFileParser.userControls.get(fileName)
        //Then, we get the layouts within the file through flowdroid
        return layouts; //to check
    }

    private Integer findLayoutResourceId(SootClass component){
       Set<Integer> resIds = setupApplication.getCallbackAnalyzer().getLayoutClasses().get(component);
       if (resIds == null || resIds.size() == 0)
        return null;
       return resIds.iterator().next();//need to get the first element
    }

    private Integer findResourceId(SootMethod sootMethod){
        //Get the call sites where the callback class is registered
        Set<Pair<SootMethod, Unit>> callSites = setupApplication.getCallbackAnalyzer().getSetterCallbackMap().get(sootMethod.getDeclaringClass());

        Logger.debug("Found callsites for method {} {}", sootMethod, callSites);
        //TODO: need some logic to narrow down call sites to only relevant ones ?
        for (Pair<SootMethod, Unit> callSite: callSites) {
            //Go backwards through the method until the view registered is found
            Stmt stmt = ((Stmt)callSite.getO2());
            InstanceInvokeExpr invokeExpr = (InstanceInvokeExpr)((InvokeExpr)stmt.getInvokeExpr());
            Value base = invokeExpr.getBase();

            UnitPatchingChain units = callSite.getO1().retrieveActiveBody().getUnits();
            while (stmt != units.getFirst()){
                stmt = ((Stmt)units.getPredOf(stmt));
                //Follow register assignments
                if (stmt instanceof AssignStmt){
                    AssignStmt assignStmt = (AssignStmt)stmt;
                    if (assignStmt.getLeftOp().equals(base)) {
                        if (assignStmt.getRightOp() instanceof Local || assignStmt.getRightOp() instanceof CastExpr){
                            Value rightOp = assignStmt.getRightOp();
                            if (rightOp instanceof CastExpr){
                                rightOp = ((CastExpr)rightOp).getOp();
                            }
                            else if (!(rightOp instanceof Local)){
                                Logger.error("No logic to handle type for assignment {}", rightOp.getType());
                            }
                            base = rightOp;
                        }

                        //Extract the proper resource ID for the view
                        if (stmt.containsInvokeExpr()){
                            InvokeExpr invExpr = stmt.getInvokeExpr();
                            if (invExpr.getMethod().getName().equals("findViewById")) { //can get getItem for menus
                                Logger.debug("Found resource extraction stmt {}", stmt);
                                Value value = invExpr.getArgs().get(0);
                                if(value instanceof IntConstant)
                                    return ((IntConstant)value).value; //TO-DO: add more logic to resolve the type
                                else{
                                    Argument arg = extractIntArgumentFrom(invExpr);
                                    Set<Object> values = ArgumentValueManager.v().getArgumentValues(arg, stmt, null);
                                    if (values!=null && !values.isEmpty()) {
                                        Object val = values.iterator().next();
                                        if (val instanceof Integer) {
                                            return (Integer)val;
                                        }
                                    }
                                    Logger.error("No logic to handle type {}", value.getType());
                                }
                            }
                        }
                        
                    }
                }
                
                
            }
        }
        return null;
        
    }
   
}