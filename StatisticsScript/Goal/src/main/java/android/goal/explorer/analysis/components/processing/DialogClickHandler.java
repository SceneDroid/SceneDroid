package android.goal.explorer.analysis.components.processing;

import android.goal.explorer.analysis.FragmentChangeAnalysis;
import android.goal.explorer.cmdline.GlobalConfig;
import android.goal.explorer.model.component.Activity;
import android.goal.explorer.model.stg.node.ScreenNode;
import jdk.nashorn.internal.objects.Global;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition;
import soot.jimple.toolkits.callgraph.Edge;
import st.cs.uni.saarland.de.entities.Dialog;

import java.util.*;

public class DialogClickHandler {
    private static GlobalConfig config;
    //need to have global access to those maps


    /**
     * Create screen variation based on dialog element clicked on
     * @param screenNode
     * @param callback
     * @param screenNodesWorkerList
     * @param edges
     * @param dialogsDefinedInCallback
     */
    public void handleDialogCallback(ScreenNode screenNode, AndroidCallbackDefinition callback, LinkedList<ScreenNode> screenNodesWorkerList, Set<List<Edge>> edges, Set<Dialog> dialogsDefinedInCallback) {

        //There should be some global static access to private static Map<AndroidCallbackDefinition, List<List<Unit>>> callbacksToFragmentTrans
    }

}
