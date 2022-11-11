package android.goal.explorer.model.stg.output;

import android.goal.explorer.model.stg.STG;
import android.goal.explorer.model.stg.edge.TransitionEdge;
import android.goal.explorer.model.stg.node.AbstractNode;
import android.goal.explorer.model.stg.node.BroadcastReceiverNode;
import android.goal.explorer.model.stg.node.ScreenNode;
import android.goal.explorer.model.stg.node.ServiceNode;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import kotlin.Pair;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

@XStreamAlias("ScreenTransitionGraph")
public class OutSTG {
    public Set<OutTransitionEdge> transitionEdges;
    public Set<OutScreenNode> screenNodeSet;
    public Set<OutServiceNode> serviceNodeSet;
    public Set<OutBroadcastReceiverNode> broadcastReceiverNodeSet;


    public OutSTG(STG stg, Map<AbstractNode, Pair<Boolean,String>> targetMarking) {
        transitionEdges = new HashSet<>();
        screenNodeSet = new HashSet<>();
        serviceNodeSet = new HashSet<>();
        broadcastReceiverNodeSet = new HashSet<>();

        for (TransitionEdge edge : stg.getTransitionEdges()) {
            if (edge.getSrcNode() == null || edge.getTgtNode() == null) {
                continue;
            }
            transitionEdges.add(new OutTransitionEdge(edge));
        }
        for (ScreenNode node : stg.getAllScreens()) {
            OutScreenNode n = (OutScreenNode) ConvertToOutput.convertNode(node);
            if (targetMarking != null && !targetMarking.isEmpty()){
                Pair<Boolean, String> mark = targetMarking.get(node);
                if(mark.component1()) {
                    n.target = true;
                    n.targetAction = new OutTargetActionTag(mark.component2());
                }
            }
            screenNodeSet.add(n);
        }
        for (ServiceNode node : stg.getAllServices()) {
            OutServiceNode n = (OutServiceNode) ConvertToOutput.convertNode(node);
            if (targetMarking != null && !targetMarking.isEmpty()){
                Pair<Boolean, String> mark = targetMarking.get(node);
                if(mark.component1()) {
                    n.target = true;
                    n.targetAction = new OutTargetActionTag(mark.component2());
                }
            }
            serviceNodeSet.add(n);
        }
        for (BroadcastReceiverNode node : stg.getAllBroadcastReceivers()) {
            broadcastReceiverNodeSet.add((OutBroadcastReceiverNode) ConvertToOutput.convertNode(node));
        }
    }

    public OutSTG(OutSTG outSTG, Map<OutAbstractNode, Pair<Boolean, String>> targetMarking){
        transitionEdges = outSTG.transitionEdges;
        screenNodeSet = outSTG.screenNodeSet;
        serviceNodeSet = outSTG.serviceNodeSet;
        broadcastReceiverNodeSet = outSTG.broadcastReceiverNodeSet;
        for (OutScreenNode node: screenNodeSet){
            Pair<Boolean, String> mark = targetMarking.get(node);
            if(mark.component1()){
                node.target = true;
                node.targetAction = new OutTargetActionTag(mark.component2());
            }
            else node.target = false;
        }

        for (OutServiceNode node: serviceNodeSet){
            Pair<Boolean, String> mark = targetMarking.get(node);
            if(mark.component1()){
                node.target = true;
                node.targetAction = new OutTargetActionTag(mark.component2());
            }
            else node.target = false;
        }

        for (OutBroadcastReceiverNode node: broadcastReceiverNodeSet){
            Pair<Boolean, String> mark = targetMarking.get(node);
            if(mark.component1()){
                node.target = true;
                node.targetAction = new OutTargetActionTag(mark.component2());
            }
            else node.target = false;
        }
    }

    @NotNull
    public Set<OutAbstractNode> getAbstractNodes(){
        Set<OutAbstractNode> allNodes = new HashSet<>();
        allNodes.addAll(screenNodeSet);
        allNodes.addAll(serviceNodeSet);
        allNodes.addAll(broadcastReceiverNodeSet);
        return allNodes;
    }

    @NotNull
    public Set<OutTransitionEdge> getEdgesWithSrcNode(@Nullable OutAbstractNode node) {
        return transitionEdges.stream().filter(edge -> edge.getSrcNode().equals(node)).collect(Collectors.toSet());
    }

    @NotNull
    public Set<OutTransitionEdge> getEdgesWithTgtNode(@Nullable OutAbstractNode node) {
        return transitionEdges.stream().filter(edge -> edge.getTgtNode().equals(node)).collect(Collectors.toSet());
    }
}
