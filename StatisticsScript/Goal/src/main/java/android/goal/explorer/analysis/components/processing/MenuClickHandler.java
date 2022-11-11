package android.goal.explorer.analysis.components.processing;

import android.goal.explorer.model.stg.node.ScreenNode;
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition;
import soot.jimple.toolkits.callgraph.Edge;
import st.cs.uni.saarland.de.entities.Dialog;

import java.util.List;
import java.util.Set;

public class MenuClickHandler {

    /**
     * Map a menu/drawer interaction to a new screen node
     * @param screenNode
     * @param callback
     * @param screenNodesWorkerList
     * @param edges
     * @param dialogs
     * @param type
     */
    public void handleMenuOrDrawerCallback(ScreenNode screenNode, AndroidCallbackDefinition callback, List<ScreenNode> screenNodesWorkerList, Set<List<Edge>> edges, Set<Dialog> dialogs, String type) {
    }
}
