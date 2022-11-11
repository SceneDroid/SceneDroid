package android.goal.explorer.model.stg;

import android.goal.explorer.model.App;
import android.goal.explorer.model.component.Activity;
import android.goal.explorer.model.component.BroadcastReceiver;
import android.goal.explorer.model.component.Fragment;
import android.goal.explorer.model.component.Service;
import android.goal.explorer.model.stg.edge.EdgeTag;
import android.goal.explorer.model.stg.edge.TransitionEdge;
import android.goal.explorer.model.stg.node.AbstractNode;
import android.goal.explorer.model.stg.node.BroadcastReceiverNode;
import android.goal.explorer.model.stg.node.ScreenNode;
import android.goal.explorer.model.stg.node.ServiceNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.pmw.tinylog.Logger;

public class STG {
//    private static STG instance;
    private Set<TransitionEdge> transitionEdges;
    private Set<ScreenNode> screenNodeSet;
    private Set<ServiceNode> serviceNodeSet;
    private Set<BroadcastReceiverNode> broadcastReceiverNodeSet;

//    public static synchronized STG v() {
//        if (instance == null)
//            throw new RuntimeException("SSTG was not initialized. Please make sure to call "
//                    + "initialize() before accessing the singleton");
//        return instance;
//    }

//    public static void initialize(Set<Service> serviceSet,
//                                  Set<BroadcastReceiver> broadcastReceiverSet) {
//        instance = new STG();
//
//        // initialize empty sets
//        instance.screenNodeSet = Collections.synchronizedSet(new HashSet<>());
//        instance.serviceNodeSet = Collections.synchronizedSet(new HashSet<>());
//        instance.broadcastReceiverNodeSet = Collections.synchronizedSet(new HashSet<>());
//        instance.transitionEdges = Collections.synchronizedList(new ArrayList<>());
//        instance.outEdgeMap = new HashMultiMap<>();
//        instance.inEdgeMap = new HashMultiMap<>();
//
//        // create initial nodes
//        for (Service service : serviceSet) {
//            instance.serviceNodeSet.add(new ServiceNode(service));
//        }
//        for (BroadcastReceiver receiver : broadcastReceiverSet) {
//            instance.broadcastReceiverNodeSet.add(new BroadcastReceiverNode(receiver));
//        }
//    }

    /**
     * Constructor for STG that initialize STG with service and broadcast receiver nodes
     */
    public STG(App app) {
        // initialize empty sets
        this.screenNodeSet = Collections.synchronizedSet(new HashSet<>());
        this.serviceNodeSet = Collections.synchronizedSet(new HashSet<>());
        this.broadcastReceiverNodeSet = Collections.synchronizedSet(new HashSet<>());
        this.transitionEdges = Collections.synchronizedSet(new HashSet<>());

        // create initial nodes
        for (Service service : app.getServices()) {
            this.serviceNodeSet.add(new ServiceNode(service));
        }
        for (BroadcastReceiver receiver : app.getBroadcastReceivers()) {
            this.broadcastReceiverNodeSet.add(new BroadcastReceiverNode(receiver));
        }
    }

    /**
     * Adds a screen to the graph
     * @param screenNode The screen
     */
    public void addScreen(ScreenNode screenNode) {
        this.screenNodeSet.add(screenNode);
    }

    /**
     * Add a new transition edge
     * @param srcNode The source activity
     * @param tgtNode The target activity
     */
    public void addTransitionEdge(AbstractNode srcNode, AbstractNode tgtNode) {
        TransitionEdge newEdge = new TransitionEdge(srcNode, tgtNode);
        transitionEdges.add(newEdge);
    }

    /**
     * Add a new transition edge
     * @param srcNode The source activity
     * @param tgtNode The target activity
     * @param tag The widget assigned to this edge
     */
    public void addTransitionEdge(AbstractNode srcNode, AbstractNode tgtNode, EdgeTag tag) {
        TransitionEdge newEdge = new TransitionEdge(srcNode, tgtNode, tag);
        transitionEdges.add(newEdge);
        
    }

    /**
     * Gets the component nodes by name
     * @param name The name to look up component node
     * @return The component nodes
     */
    public Set<AbstractNode> getNodesByName(String name) {
        Set<AbstractNode> comps = new HashSet<>();
        for (ScreenNode screenNode : screenNodeSet) {
            if (screenNode.getComponent().getName().equalsIgnoreCase(name))
                comps.add(screenNode);
        }
        for (ServiceNode serviceNode : serviceNodeSet) {
            if (serviceNode.getName().equalsIgnoreCase(name))
                 comps.add(serviceNode);
        }
        for (BroadcastReceiverNode receiverNode : broadcastReceiverNodeSet) {
            if (receiverNode.getName().equalsIgnoreCase(name))
                comps.add(receiverNode);
        }
        return comps;
    }

    /**
     * Gets the number of screen nodes in the model
     * @return The number of screen nodes
     */
    public int getNumScreens(){
        return screenNodeSet.size();
    }

    /**
     * Gets the number of edges in the model
     * @return
     */
    public int getNumEdges(){
        return transitionEdges.size();
    }

    /**
     * Gets the screen nodes by name
     * @param name The name to look up screen node
     * @return The screen nodes
     */
    public Set<ScreenNode> getScreenNodesByName(String name) {
        Set<ScreenNode> screenNodes = new HashSet<>();
        for (ScreenNode screenNode : screenNodeSet) {
            if (screenNode.getName().equalsIgnoreCase(name)) {
                screenNodes.add(screenNode);
            }
        }
        return screenNodes;
    }

    public Set<AbstractNode> getBaseComponentNodeByName(String name){
        return getNodesByName(name).stream().filter(abstractNode -> {
            if(abstractNode instanceof ScreenNode){
                return ((ScreenNode)abstractNode).isBaseScreenNode();
            }
            return true; //a service or broadcast receiver node
        }).collect(Collectors.toSet()) ;
    }

    public Set<ScreenNode> getBaseScreenNodesByName(String name) {
        return getScreenNodesByName(name).stream().filter(screenNode -> screenNode.isBaseScreenNode()).collect(Collectors.toSet());
    }

    /**
     * Gets the screen nodes with menu by name
     * @param name The name to look up screen node
     * @return The screen nodes
     */
    public Set<ScreenNode> getScreenNodesWithMenuByName(String name) {
        Set<ScreenNode> screenNodes = new HashSet<>();
        for (ScreenNode screenNode : screenNodeSet) {
            if (screenNode.getName().equalsIgnoreCase(name)) {
                if (screenNode.getMenu() != null) {
                    screenNodes.add(screenNode);
                }
            }
        }
        return screenNodes;
    }

//    /**
//     * Gets the screen nodes with drawer by name
//     * @param name The name to look up screen node
//     * @return The screen nodes
//     */
//    public Set<ScreenNode> getScreenNodeWithDrawerByName(String name) {
//        Set<ScreenNode> screenNodes = new HashSet<>();
//        for (ScreenNode screenNode : screenNodeSet) {
//            if (screenNode.getName().equalsIgnoreCase(name)) {
//                if (screenNode.getAbstractEntity() != null) {
//                    if (screenNode.getAbstractEntity() instanceof Drawer)
//                        screenNodes.add(screenNode);
//                }
//            }
//        }
//        return screenNodes;
//    }

    /**
     * Gets the screen node with set of fragments
     * @param activity The host activity
     * @param fragments The fragments
     * @return The screen nodes
     */
    public ScreenNode getScreenNode(Activity activity, Set<Fragment> fragments) {
        Set<ScreenNode> screenNodes = new HashSet<>();
        for (ScreenNode screenNode : screenNodeSet) {
            if (screenNode.getComponent().equals(activity)) {
                if (screenNode.getFragments() != null) {
                    if (screenNode.getFragments().equals(fragments))
                        return screenNode;
                }
            }
        }
        return null;
    }

    /**
     * Gets all services in the STG
     * @return All services
     */
    public Set<ServiceNode> getAllServices(){
        return serviceNodeSet;
    }

    /**
     * Gets the service node by name
     * @param name The name to look up service node
     * @return The service node
     */
    public ServiceNode getServiceNodeByName(String name) {
        for (ServiceNode serviceNode : serviceNodeSet) {
            if (serviceNode.getName().equalsIgnoreCase(name))
                return serviceNode;
        }
        return null;
    }

    /**
     * Gets all services in the STG
     * @return All services
     */
    public Set<BroadcastReceiverNode> getAllBroadcastReceivers(){
        return broadcastReceiverNodeSet;
    }

    /**
     * Gets the broadcast receiver node by name
     * @param name The name to look up broadcast receiver node
     * @return The receiver node
     */
    public BroadcastReceiverNode getReceiverNodeByName(String name) {
        for (BroadcastReceiverNode receiverNode : broadcastReceiverNodeSet) {
            if (receiverNode.getName().equalsIgnoreCase(name))
                return receiverNode;
        }
        return null;
    }

    /**
     * Gets all screen nodes in the graph
     * @return All screen nodes
     */
    public Set<ScreenNode> getAllScreens(){
        return screenNodeSet;
    }

    public Set<AbstractNode> getAllNodes() {
        Set<AbstractNode> allNodes = new HashSet<>();
        allNodes.addAll(screenNodeSet);
        allNodes.addAll(serviceNodeSet);
        allNodes.addAll(broadcastReceiverNodeSet);
        return allNodes;
    }

    public Set<AbstractNode> getAllBaseNodes() {
        return getAllNodes().stream().filter(node -> !(node instanceof ScreenNode) || ((ScreenNode)node).isBaseScreenNode()).collect(Collectors.toSet());
    }

    public Set<TransitionEdge> getAllEdges() {
        return transitionEdges;
    }

    /**
     * Gets the transition edges with given source activity node
     * @param srcNode The source activity node
     * @return The set of transition edges that contain given source activity node
     */
    public Set<TransitionEdge> getEdgesWithSrcNode(AbstractNode srcNode) {
        Set<TransitionEdge> edgeSet = new HashSet<>();
        for (TransitionEdge edge : transitionEdges) {
            if (edge.getSrcNode().equals(srcNode))
                edgeSet.add(edge);
        }
        return edgeSet;
    }

    /**
     * Gets the transition edges with given target component node
     * @param tgtNode The target component node
     * @return The set of transition edges that contain given target component node
     */
    public Set<TransitionEdge> getEdgeWithTgtNode(AbstractNode tgtNode) {
        Set<TransitionEdge> edgeSet = new HashSet<>();
        for (TransitionEdge edge : transitionEdges) {
            if (edge.getTgtNode().equals(tgtNode))
                edgeSet.add(edge);
        }
        return edgeSet;
    }

    /**
     * Gets the transition edge with given source and target component nodes
     * @param srcNode The source component node
     * @param tgtNode The target component node
     * @return The transition edge
     */
    public Set<TransitionEdge> getEdges(AbstractNode srcNode, AbstractNode tgtNode) {
        Set<TransitionEdge> edgeSet = new HashSet<>();
        for (TransitionEdge edge : transitionEdges) {
            if (edge.getTgtNode().equals(tgtNode))
                if (edge.getSrcNode().equals(srcNode))
                    edgeSet.add(edge);
        }
        return edgeSet;
    }

    /**
     * Sets the widget assigned to the transition edge with given source and target component nodes
     * @param srcNode The source component node
     * @param tgtNode The target component node
     * @param tag The tag to be assigned to this transition
     */
    public void setEdgeTag(AbstractNode srcNode, AbstractNode tgtNode, EdgeTag tag) {
        for (TransitionEdge edge : transitionEdges) {
            if (edge.getTgtNode().equals(tgtNode))
                if (edge.getSrcNode().equals(srcNode))
                    edge.setEdgeTag(tag);
        }
    }

    /**
     * Gets the tag assigned to the transition edge with given source and target component nodes
     * @param srcNode The source component node
     * @param tgtNode The target component node
     * @return  The tag to be assigned to this transition
     */
    public EdgeTag getEdgeTag(AbstractNode srcNode, AbstractNode tgtNode) {
        for (TransitionEdge edge : transitionEdges) {
            if (edge.getTgtNode().equals(tgtNode))
                if (edge.getSrcNode().equals(srcNode))
                    return edge.getEdgeTag();
        }
        return null;
    }

    /**
     * Gets the successor of the given node
     * @param node The given node
     * @return The successor of the given node
     */
    public List<AbstractNode> getSuccsOf(AbstractNode node) {
        List<AbstractNode> succNodeList = new ArrayList<>();
        for (TransitionEdge edge : getEdgesWithSrcNode(node)) {
            succNodeList.add(edge.getTgtNode());
        }
        return succNodeList;
    }

    /**
     * Gets the predecessor of the given component
     * @param node The given component
     * @return The predecessor of the given component
     */
    public List<AbstractNode> getPredsOf(AbstractNode node) {
        List<AbstractNode> predNodeList = new ArrayList<>();
        for (TransitionEdge edge : getEdgeWithTgtNode(node)) {
            predNodeList.add(edge.getSrcNode());
        }
        return predNodeList;
    }

    /**
     * Get transition edge widget
     * @param edge The transition edge
     * @return The widget assigned to this transition edge
     */
    public EdgeTag getTag(TransitionEdge edge) {
        return edge.getEdgeTag();
    }

     /**
     * Check if a screen is in the graph
     * @param screenNode The screen node to check
     * @return True if the given screen node can be found in the graph
     */
    public boolean isScreenInGraph(ScreenNode screenNode) {
        return screenNodeSet.contains(screenNode);
    }

    /**
     * Gets all transition edges in the model
     * @return All transition edges
     */
    public Set<TransitionEdge> getTransitionEdges(){ return transitionEdges; }


}
