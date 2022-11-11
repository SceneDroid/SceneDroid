package android.goal.explorer.builder;

import android.goal.explorer.data.android.constants.ClassConstants;
import android.goal.explorer.model.App;
import android.goal.explorer.model.component.Activity;
import android.goal.explorer.model.stg.STG;
import android.goal.explorer.model.stg.edge.EdgeTag;
import android.goal.explorer.model.stg.edge.TransitionEdge;
import android.goal.explorer.model.stg.node.AbstractNode;
import android.goal.explorer.model.stg.node.ScreenNode;

import soot.MethodOrMethodContext;
import soot.SootClass;
import soot.SootMethod;
import soot.Scene;

import android.goal.explorer.analysis.CallbackWidgetProvider;
import android.goal.explorer.analysis.ICCParser;
import android.goal.explorer.cmdline.GlobalConfig;

import org.pmw.tinylog.Logger;
import org.json.simple.*;


import soot.jimple.toolkits.callgraph.Edge;
import st.cs.uni.saarland.de.reachabilityAnalysis.UiElement;

import java.util.*;
import java.util.stream.Collectors;

public class TransitionBuilder {
    public App app;
    public STG stg;

    private static final String TAG = "TransitionBuilder";
    private ICCParser.ICCData iccData;

    /**
     * Default constructor
     * @param stg The stg model
     */
    public TransitionBuilder(STG stg) {
        this.stg = stg;
        //this.iccData = ICCParser.v().getICCData();
    }

    public TransitionBuilder(STG stg, App app, GlobalConfig config) {
        this.stg = stg;
        this.app = app;
        this.iccData = ICCParser.v().getICCData();
        ICCParser.v().setIccResultsFolder(config.getIc3ResultsFolder());
        ICCParser.v().parseIccData();
    }

    private String getUIElementText(UiElement uiElement){
        String elementText = "";
        Map<String, String> texts = uiElement.text;
        if(texts!= null && texts.containsKey("default_value"))
            elementText = texts.get("default_value");
        else Logger.debug("No matching text for UI element found");
        return elementText;
        //return  backstageApp.getUIElementsMap().get(elementId).ge
    }


    public void collectTransitions() {
        collectIccTransitions();
        //Should we iterate over ui elements with targets instead?
        for (ScreenNode screen : stg.getAllScreens()) {
            Activity mainActivity = (Activity) screen.getComponent();
            // iterate all UI elements to find triggers to transitions
            Logger.debug("Building transitions for activity {}", mainActivity);
            for (UiElement uiElement : screen.getUiElements()) {
                if (uiElement.targetSootClass != null) {
                    constructTransition(screen, uiElement);
                }
            }
        }
        // This should be done last.
        addTabTransitions();
    }

    private void addTabTransitions() {
        addInterTabTransitions();
        addIntraTabTransitions();
    }

    /**
     * Adds all possible combinations of tab switches
     * e.g ActivityA(Tab1) ---> Activity1(Tab2) on click on tab2
     */
    private void addInterTabTransitions() {
        for (ScreenNode screen : stg.getAllScreens()) {
            if(screen.getTab() != null) {
                for(ScreenNode tgtScreen : stg.getAllScreens()) {
                    if(!screen.getName().equals(tgtScreen.getName()) || tgtScreen.getTab() == null || !tgtScreen.emptyBesidesTab() || screen.getTab().equals(tgtScreen.getTab()))
                        continue;

                    EdgeTag edge = new EdgeTag("TabButton",
                            null, //onClick maybe
                            tgtScreen.getTab().getIndicatorTextResId().equals("") ? null : Integer.parseInt(tgtScreen.getTab().getIndicatorTextResId()),
                            null,
                            tgtScreen.getTab().getIndicatorText().equals("") ? null : tgtScreen.getTab().getIndicatorText());
                    stg.addTransitionEdge(screen, tgtScreen, edge);
                }
            }
        }
    }

    /**
     * Maps all transitions from contained activity in tab to container activity
     * i.e Tab1 --> Activity B mapped to ActivityA(Tab1) --> ActivityB
     */
    private void addIntraTabTransitions() {
        List<TransitionEdge> toAdd = new ArrayList<>();
        for (TransitionEdge edge : stg.getTransitionEdges()) {
            //Skip tab switches
            if(edge.getEdgeTag().getTypeOfUiElement() != null && edge.getEdgeTag().getTypeOfUiElement().equals("TabButton")) {
                continue;
            }

            AbstractNode srcNode = edge.getSrcNode(), tgtNode = edge.getTgtNode();
            if(!(srcNode instanceof ScreenNode && tgtNode instanceof ScreenNode))
                continue;
            ScreenNode source = (ScreenNode) srcNode;
            //source.setAsBaseScreenNode(source.);
            ScreenNode target = (ScreenNode) tgtNode;
            for (ScreenNode screen : stg.getAllScreens()) {
                //If the screen's tab matches with the source activity name
                if (screen.getTab() == null || !screen.getTab().getContentActivityName().equals(source.getName()) || !screen.equalsBesidesTabAndTarget(source)) {
                    continue;
                }
                ScreenNode sourceToAdd = source.clone(); //tabs are not base screens by default
                //sourceToAdd.setAsBaseScreenNode(source.isBaseScreenNode());
                sourceToAdd.setAsBaseScreenNode(false);
                ScreenNode targetToAdd = target.clone();
                //targetToAdd.setAsBaseScreenNode(target.isBaseScreenNode());
                Logger.debug("Tab Transition: " + screen.getName() + " " + targetToAdd.getName());
                if(source.getName().equals(target.getName())) {
                    targetToAdd.setName(screen.getName());
                    targetToAdd.setTab(screen.getTab());
                }
                sourceToAdd.setName(screen.getName());
                sourceToAdd.setTab(screen.getTab());
                //Looks like the parent id is gone for some reason?
                TransitionEdge transition = new TransitionEdge(sourceToAdd, targetToAdd, edge.getEdgeTag());
                toAdd.add(transition);
            }
        }
        stg.getTransitionEdges().addAll(toAdd);
    }

    private void addIntraTabTransitions2(){
        //Get all screens with tabs
        //Iterate through all other screens
        //If screenname matches the tab, then map the transitions
    }


    private void collectIccTransitions(){
        //adding icc transitions;
        if(iccData == null || iccData.getTransitions()  == null)
            return;
        for (Object o : iccData.getTransitions()) {
            JSONObject transitionInfo = (JSONObject) o;
            String handler = "<" + ((String) transitionInfo.get("handler")).replace("/", ": ") + ">";
            Iterator sourceIterator = ((JSONArray) transitionInfo.get("sources")).iterator(), targetIterator = ((JSONArray) transitionInfo.get("targets")).iterator();

            while (sourceIterator.hasNext()) {
                String source = (String) sourceIterator.next();
                while (targetIterator.hasNext()) {
                    String target = (String) targetIterator.next();
                    if (target.equals(source))
                        continue;
                    MethodOrMethodContext methodContext = null;
                    try {
                        methodContext = Scene.v().getMethod(handler);
                    } catch (Exception e) {
                        Logger.error("Error while looking for method from icc: {}", e);
                    } finally {
                        if (methodContext != null) { //TODO maybe check if it's already returned by Backstage?
                            //Here to do: check if declaring class of the method is the activity
                            //if not, either it's a callback and it's just invalid (ignore)
                            //or, it's a method and need to explore the callsites to find it?
                            Activity activity = app.getActivityByName(source);
                            SootMethod method = methodContext.method();
                            if (activity != null) {
                                //Here we should check within the fragment as well
                                List<UiElement> uiElements = CallbackWidgetProvider.v().findWidgetsForMethod(method, activity);
                                Logger.debug("Widgets extracted for {} {}", method, uiElements);
                                if (uiElements == null || uiElements.isEmpty()) {
                                    List<SootMethod> allCallbacks = getCallerCallbacks(method, new HashSet<>());
                                    Logger.debug("Mapped method {} to callbacks {}", method, allCallbacks);
                                    Logger.error("Mapped method {} to callbacks {}", method, allCallbacks);
                                    uiElements = allCallbacks.stream().flatMap(callback -> CallbackWidgetProvider.v().findWidgetsForMethod(callback, activity).stream()).filter(Objects::nonNull)
                                            .collect(Collectors.toList());
                                }
                                boolean foundUiElement = false, foundTargetOnce;
                                for (UiElement uiElement : uiElements) { //menus should be dealt with right?
                                    if (uiElement != null) {
                                        Logger.debug("Found from icc {} --> {} for handler {} {}", source, target, handler, uiElement);
                                        Logger.error("Found from icc {} --> {} for handler {} {}", source, target, handler, uiElement);
                                        foundUiElement = true;
                                        if (uiElement.targetSootClass == null) { //to build potentially many targets per ui element
                                            /*if (uiElement.kindOfElement != null && uiElement.kindOfElement.equals("item")){
                                                //If backstage already found the target class, but it was associated with another item?
                                                if (uiElements.stream().anyMatch(uiElement1 -> uiElement1.targetSootClass != null && uiElement1.targetSootClass.getName().equals(target)))
                                                    break;
                                            }*/
                                            uiElement.targetSootClass = Scene.v().getSootClass(target); //TODO what if same handler for multiple elements (i.e menu item?), need to redo control sensitive analysis I guess
                                        }
                                    }
                                }
                                if (!foundUiElement) {
                                    //if ui element is null, just add the transition without a ui element I guess?
                                    Set<AbstractNode> srcNodes = stg.getNodesByName(source);//, tgtNodes = stg.getBaseScreenNodesByName(target);
                                    /*if (tgtNodes.isEmpty()) {
                                        Logger.warn("[{}] Cannot find the screen node matches component name {}", TAG,
                                                target);
                                        break;
                                    }*/
                                    List<SootMethod> allCallbacks = getCallerCallbacks(method, new HashSet<>());
                                    Logger.debug("Mapped method {} to callbacks {}", method, allCallbacks);
                                    Logger.warn("Adding transition without ui element {} ---> {}, {} with callbacks {}", source, target, method, allCallbacks);
                                    UiElement mockUiElement = new UiElement();
                                    mockUiElement.targetSootClass = Scene.v().getSootClass(target);
                                    mockUiElement.handlerMethod = (allCallbacks == null || allCallbacks.isEmpty()) ? method : allCallbacks.get(0);
                                    mockUiElement.signature = mockUiElement.handlerMethod.getSignature();
                                    if (handler.contains("ItemSelected"))
                                        mockUiElement.kindOfElement = "item"; //should we just ignore it?
                                    else if (handler.contains("ItemClick"))
                                        mockUiElement.kindOfElement = "listviewitem";
                                    else if (handler.contains("DialogInterface"))
                                        mockUiElement.kindOfElement = "dialog";
                                    else mockUiElement.kindOfElement = "";
                                    srcNodes.forEach(srcNode -> constructTransition(srcNode, mockUiElement));
                                    //probably should filter screens with menus and such?
                                    EdgeTag tag = new EdgeTag(null, handler.split(":")[1], null);
                                    /*for(AbstractNode srcNode: srcNodes){
                                        if(srcNode instanceof ScreenNode){
                                            ScreenNode screen = (ScreenNode)srcNode;
                                            //if current screen has menu and it's not a menu callback ignore
                                        }
                                    }*/
                                }

                            }

                        } else
                            Logger.error("No method found for icc transition src: {}, tgt: {}, handler: {}", source, target, handler);

                        //otherwise need to do some mapping?
                    }
                }
            }
        }
    }

    private List<SootMethod> getCallerCallbacks(SootMethod method, Set<SootMethod> visitedMethods){
        List<SootMethod> callerCallbacks = new ArrayList<>();
        if(method == null)
            return callerCallbacks;
        visitedMethods.add(method);
        if(app.isCallbackMethod(method)){
                Logger.debug("Reached a callback method {}", method);
             //reached a callback;
             callerCallbacks.add(method);
             return callerCallbacks;
        }
        List<SootMethod> methods = getMethodToEdgeInto(method);
        for(SootMethod sootMethod: methods) {
            Iterator<Edge> edgesInto = Scene.v().getCallGraph().edgesInto(sootMethod);
            while (edgesInto.hasNext()) { //for each edge
                Edge edge = edgesInto.next(); //the caller
                MethodOrMethodContext caller = edge.getSrc();
                Logger.debug("The caller for {} {}", sootMethod, caller.method());
                if (visitedMethods.contains(caller.method()))
                    break;
                callerCallbacks.addAll(getCallerCallbacks(caller.method(), visitedMethods));
            }
        }
        return callerCallbacks;
    }

    /**
     * Returns method to look for edges into
     * Augmented with special cases (i.e AsyncTask, Runnable)
     * @param method
     * @return
     */
    private List<SootMethod> getMethodToEdgeInto(SootMethod method){
        List<SootMethod> methods = new ArrayList<>();
        if(method.getDeclaringClass().hasSuperclass() && method.getDeclaringClass().getSuperclass().getName().equals(ClassConstants.ASYNCTASKCLASS) && !method.getName().startsWith("execute")){ //TODO check all superclasses
            //Note, can not be initialized prior as abstract classes cause issues with Soot hierarchy
            SootClass osAsyncTask = Scene.v().getSootClassUnsafe(ClassConstants.ASYNCTASKCLASS);
            osAsyncTask.getMethods().stream().filter(m -> m.getName().startsWith("execute")).forEach(methods::add);
            if (methods.size() > 0) {
                Logger.debug("Found async task method {}, looking for callers for {} instead", method, methods);
                return methods; //for now
            }
        }
        else if(method.getSignature().equals("void run()") && method.getDeclaringClass().hasSuperclass() && method.getDeclaringClass().getName().equals(ClassConstants.RUNNABLECLASS)) {
            //We want to look for submit class as a caller?
            //Look for init of the Runnable class
            //Then need to find submit within same method using that register
        }
        //TODO runnable
        //submitRunnable --> run
        methods.add(method);
        return methods;
     }


    private void constructTransition(AbstractNode node, UiElement uiElement){
        Logger.debug("UI element under analysis {}, with name {}", uiElement, uiElement.targetSootClass);
        //Here we should only mark the base nodes
        //TODO get nodes or nodes with such fragments

        if (uiElement.targetSootClass.getName().equals(node.getName())){
            Logger.warn("Found non ICC transition from {} , ignoring ...", uiElement.targetSootClass.getName());
            return;
        }
        Set<AbstractNode> targetNodes = stg.getBaseComponentNodeByName(uiElement.targetSootClass.getName());
        if (targetNodes.isEmpty()) {
            Logger.warn("[{}] Cannot find the screen node matches component name {}", TAG,
                    uiElement.targetSootClass.getName());
            return;
        }
        if(node instanceof ScreenNode){
            ScreenNode screen = (ScreenNode) node;
            if(uiElement.kindOfElement.equals("item") || uiElement.handlerMethod.getSubSignature().contains("ItemSelected(")){ //TODO check if only one type of menu is ever open at once
                if(!screen.hasVisibleMenu() && !screen.hasVisibleDrawer() && !screen.hasVisibleContextMenu())
                    return;
                if(uiElement.handlerMethod.getSubSignature().contains("Context") && !screen.hasVisibleContextMenu())
                    return;
                if(uiElement.handlerMethod.getSubSignature().contains("Option") && !screen.hasVisibleMenu())
                    return;
                //else check for drawer?
            }
            else if(screen.hasVisibleMenu() || screen.hasVisibleContextMenu() || screen.hasVisibleDrawer()) //only available action should be item selection
                return;
            if(uiElement.kindOfElement.equalsIgnoreCase("dialog")){
                if(!screen.hasVisibleDialogs())
                    return;
                //if(uiElement.handlerMethod.getSubSignature().contains("DialogInterface"))
            }
            else if(screen.hasVisibleDialogs())
                return;
        }
        EdgeTag tag = new EdgeTag(uiElement.kindOfElement.toLowerCase(), uiElement.handlerMethod.getSubSignature(), uiElement.globalId, null, getUIElementText(uiElement));
        tag.setParentId(uiElement.parentId);
        targetNodes.forEach(targetNode -> stg.addTransitionEdge(node, targetNode, tag));
    }

    private void buildICCTransition(){
    }
}
