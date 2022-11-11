package android.goal.explorer.analysis.dependency;

import android.goal.explorer.analysis.value.Constants;
import android.goal.explorer.analysis.value.analysis.ArgumentValueAnalysis;
import android.goal.explorer.analysis.value.managers.ArgumentValueManager;
import org.pmw.tinylog.Logger;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.EqExpr;
import soot.jimple.NeExpr;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.MHGDominatorsFinder;
import soot.toolkits.graph.pdg.IRegion;
import soot.toolkits.graph.pdg.PDGNode;
import soot.toolkits.graph.pdg.ProgramDependenceGraph;
import soot.util.queue.ChunkedQueue;
import soot.util.queue.QueueReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PDGUtils {
    /**
     * findNodeOf finds node of unit u in pdg
     *
     * @param u   the unit who wants to find its PDG node
     * @param pdg the pdg where the unit lives
     * @return    the PDG node where the unit lives at
     */
    public static PDGNode findNodeOf(Unit u, ProgramDependenceGraph pdg) {
        PDGNode node = null;

        for (PDGNode n : pdg) {
            Iterator<Unit> iterator = unitIteratorOfPDGNode(n);
            if (iterator == null) continue;

            while (iterator.hasNext()) {
                if (iterator.next().equals(u) && (n.getType().equals(PDGNode.Type.CFGNODE))) { node = n; break; }
            }

            if (node != null) { break; }
        }

        return node;
    }

    /**
     * findNodeOf finds node of stmt in pdg
     *
     * @param stmt  the stmt of which we want to find its PDG node
     * @param pdg   the pdg where the unit lives
     * @return      the PDG node where the unit lives at
     */
    public static PDGNode findNodeOf(Stmt stmt, ProgramDependenceGraph pdg) {
        PDGNode node = null;

        for (PDGNode n : pdg) {
            Iterator<Unit> iterator = unitIteratorOfPDGNode(n);
            if (iterator == null) continue;

            while (iterator.hasNext()) {
                Unit u = iterator.next();
                if (u instanceof Stmt && (n.getType().equals(PDGNode.Type.CFGNODE))) {
                    if (u.equals(stmt)) {
                        node = n;
                        break;
                    }
                }
            }

            if (node != null) { break; }
        }
        return node;
    }

    /**
     * In javaDoc of Soot, the following information are mentioned:
     *
     *   This class(PDGNode) defines a Node in the Program Dependence
     *   Graph. There might be a need to store additional information
     *   in the PDG nodes. In essence, the PDG nodes represent (within
     *   them) either CFG nodes or Region nodes.
     *
     * So we simply considered that as only CFGNODE and REGION are allowed
     *
     * @param n the PDGNode to find
     * @return  iterator of n's units
     */
    public static Iterator<Unit> unitIteratorOfPDGNode(PDGNode n) {
        Iterator<Unit> iterator = null;
        PDGNode.Type type = n.getType();

        // get iterator
        if (type.equals(PDGNode.Type.CFGNODE)) {
            Block block = (Block) n.getNode();
            List<Unit> unitList = new ArrayList<>();
            Unit tail = block.getTail();
            unitList.add(tail);
            Unit nextUnit = block.getPredOf(tail);
            while (nextUnit != null) {
                unitList.add(nextUnit);
                nextUnit = block.getPredOf(nextUnit);
            }
            Collections.reverse(unitList);
            iterator = unitList.iterator();
//            iterator = ((Block) n.getNode()).iterator();
        } else if (type.equals(PDGNode.Type.REGION)) {
            iterator = ((IRegion) n.getNode()).getUnits().iterator();
        }
        return iterator;
    }

    /**
     * Gets the last unit of the pdg node
     *
     * @param n the PDGNode to find
     * @return  the last unit of PDGNode
     */
    public static Unit getLastUnitOfPDGNode(PDGNode n) {
        Iterator<Unit> iterator = unitIteratorOfPDGNode(n);
        Unit lastUnit = iterator.next();
        while(iterator.hasNext()) {
            lastUnit = iterator.next();
        }
        return lastUnit;
    }

    /**
     * Checks if a region contains the given unit
     *
     * @param region The region to check
     * @param u The unit
     * @return  true if the unit can be found within the region
     */
    public static boolean regionContainsUnit(IRegion region, Unit u) {
        for (Unit unit : region.getUnits()){
            if (unit.equals(u))
                return true;
        }
        return false;
    }

    /**
     * Gets the direct dependents of a PDGNode
     * @param srcNode The source node
     * @param pdg The program dependency graph
     * @return A list of dependents (PDGNodes)
     */
    public static List<PDGNode> getDirectDependentPDGNode(PDGNode srcNode, ProgramDependenceGraph pdg) {
        List<PDGNode> dependents = new ArrayList<>();
        pdg.getDependents(srcNode).forEach(x -> {
            if (x.getType().equals(PDGNode.Type.CFGNODE)) {
                dependents.addAll(pdg.getDependents(x));
            } else if (x.getType().equals(PDGNode.Type.REGION)) {
                dependents.add(x);
            }
        });
        return dependents;
    }

    /**
     * Gets the forward slicing of units in the program dependency graph
     * @return The set of forward sliced units
     */
    public static Map<Integer, List<PDGNode>> findConditionalMapping(Unit unit, ProgramDependenceGraph pdg) {
        if (!(unit instanceof Stmt)) { return new HashMap<>(); }

        Map<Integer, List<PDGNode>> results = new HashMap<>();

        // Find the corresponding PDGNode
        PDGNode srcNode = findNodeOf(unit, pdg);

        // Get the direct dependents
        List<PDGNode> dependents = getDirectDependentPDGNode(srcNode, pdg);
        //Logger.debug("Successor nodes {}", pdg.getSuccsOf(srcNode));

        // Find the resource id value of the conditional stmt
        Unit conditionalUnit = getLastUnitOfPDGNode(srcNode); //what if not in the same block
        if (conditionalUnit instanceof LookupSwitchStmt) {
            LookupSwitchStmt conditionalStmt = (LookupSwitchStmt) conditionalUnit;
            // Check the size of the look up switch should match the size of dependents
            int length;
            if (dependents.size() < conditionalStmt.getTargetCount()) {
                Logger.warn("Size of targets do not match the dependents in PDG! Ignoring some target values...");
                length = dependents.size();
            } else if (dependents.size() > conditionalStmt.getTargetCount()) {
                Logger.warn("Size of targets do not match the dependents in PDG! Ignoring some dependents values...");
                length = conditionalStmt.getTargetCount();
            } else {
                length = dependents.size();
            }

            for (int i = 0; i<length; i++){
                // Resource ID value
                int lookupValue = conditionalStmt.getLookupValue(i);
                PDGNode dependent = dependents.get(i);
                /*for(PDGNode succ: succs){
                    Logger.debug("Succ level 0 {}", succ);
                    while(succ != null){
                        List<PDGNode> succList = pdg.getSuccsOf(succ);
                        if(succList != null && succList.size() > 0){
                            succ = pdg.getSuccsOf(succ).get(0);
                            Logger.debug("Succ internal : "+succ);
                        }
                        else 
                            succ = null;
                    }
                }*/

                // The dependents nodes
                List<PDGNode> nodeQueue = getAllDependentsInclude(dependent, pdg);
                results.put(lookupValue, nodeQueue);
            }
        } else {
            Iterator<Unit> unitIter = unitIteratorOfPDGNode(srcNode);
            int i = 0;
            while (unitIter.hasNext()) {
                Stmt stmt = (Stmt) unitIter.next();
                if (stmt instanceof IfStmt) {
                    Value value = ((IfStmt) stmt).getCondition();
                    Logger.debug("Found an if statement wit condition {} {}", value, value.getType());
                    if (value instanceof EqExpr || value instanceof NeExpr) {
                       
                        Value lhv = ((ConditionExpr) value).getOp1();
                        Value rhv = ((ConditionExpr) value).getOp2();
                        Logger.debug("Found eq expr {} with leftOp {} and rightOp {}", ((ConditionExpr)value), lhv.getType(), rhv.getType());
                        
                        Stmt target = (value instanceof EqExpr)?((IfStmt)stmt).getTarget(): (unitIter.hasNext()?((Stmt)unitIter.next()): null);
                        if (target == null)
                            return results; //TO-DO deal with the neq case by extracting the ids with backstage and mapping everything that's left
                        Logger.debug("Found target: {} ", target);
                        PDGNode targetNode =  findNodeOf(((IfStmt) stmt).getTarget(), pdg);
                        if (lhv instanceof IntConstant) {
                            
                            // The dependents nodes
                            List<PDGNode> nodeQueue = getAllDependentsInclude(targetNode, pdg);
                            results.put(((IntConstant) lhv).value, nodeQueue);
                        } else if (rhv instanceof IntConstant) {
                            List<PDGNode> nodeQueue = getAllDependentsInclude(targetNode, pdg);
                            results.put(((IntConstant) rhv).value, nodeQueue);
                        } else if (rhv instanceof Local) {
                            ArgumentValueAnalysis analysis = ArgumentValueManager.v().
                                    getArgumentValueAnalysis(Constants.DefaultArgumentTypes.Scalar.INT);
                            Set<Object> possibleValues = analysis.computeVariableValues(rhv, stmt);
                            for (Object possibleValue : possibleValues) {
                                if (possibleValue instanceof Integer) {
                                   
                                    List<PDGNode> nodeQueue = getAllDependentsInclude(targetNode, pdg);
                                    results.put((Integer)possibleValue, nodeQueue);
                                }
                            }
                        }
                    }
                }
            }
        }
        return results;
    }

    private static List<PDGNode> getAllDependentsRecursive(PDGNode srcNode, ProgramDependenceGraph pdg){
        List<PDGNode> dependents = getDirectDependentPDGNode(srcNode, pdg);
        for(PDGNode dependent: dependents){
            dependents.addAll(getAllDependentsRecursive(dependent, pdg));
        }
        return dependents;
    }

    /**
     * Gets all dependents of current PDGNode including its self
     * @param dependent The PDGNode
     * @param pdg The Program Dependency Graph
     * @return The dependents in ChuckedQueue
     */
    private static List<PDGNode> getAllDependentsInclude(PDGNode dependent, ProgramDependenceGraph pdg) {
        ChunkedQueue<PDGNode> nodeQueue = new ChunkedQueue<>();
        List<PDGNode> result = new ArrayList<>();
        QueueReader<PDGNode> unprocessedNodes = nodeQueue.reader();
        nodeQueue.add(dependent);
        result.add(dependent);


        while (unprocessedNodes.hasNext()) {
            PDGNode next = unprocessedNodes.next();
            for (PDGNode directDependent : getDirectDependentPDGNode(next, pdg)) {
                nodeQueue.add(directDependent);
                result.add(directDependent);
            }
        }
        return result;
    }

    private static List<PDGNode> getAllDependentsAndSuccessorsInclude(PDGNode dependent, ProgramDependenceGraph pdg) {
        ChunkedQueue<PDGNode> nodeQueue = new ChunkedQueue<>();
        List<PDGNode> result = new ArrayList<>();
        QueueReader<PDGNode> unprocessedNodes = nodeQueue.reader();
        nodeQueue.add(dependent);
        result.add(dependent);

        while (unprocessedNodes.hasNext()) {
            PDGNode next = unprocessedNodes.next();
                for (PDGNode directDependent : getDirectDependentPDGNode(next, pdg)) {
                Logger.debug("The node to add {}", directDependent);
                nodeQueue.add(directDependent);
                result.add(directDependent);
            }
            for (PDGNode successor: pdg.getSuccsOf(next)){
                nodeQueue.add(successor);
                result.add(successor);
            }
        }
        return result;
    }

    /**
     * Gets all dependents of current PDGNode excluding its self
     * @param dependent The PDGNode
     * @param pdg The Program Dependency Graph
     * @return The dependents in ChuckedQueue
     */
    private static ChunkedQueue<PDGNode> getAllDependentsExclude(PDGNode dependent, ProgramDependenceGraph pdg) {
        ChunkedQueue<PDGNode> nodeQueue = new ChunkedQueue<>();
        ChunkedQueue<PDGNode> result = new ChunkedQueue<>();
        nodeQueue.add(dependent);
        QueueReader<PDGNode> unprocessedNodes = nodeQueue.reader();

        while (unprocessedNodes.hasNext()) {
            for (PDGNode directDependent : getDirectDependentPDGNode(unprocessedNodes.next(), pdg)) {
                nodeQueue.add(directDependent);
                result.add(directDependent);
            }
        }
        return result;
    }

//    /**
//     * Finds the backward slicing that starts from given unit to the given if unit in given method
//     * @param u The unit to start
//     * @param ifUnit The if unit, which is the stopping unit
//     * @param method The method where the if unit should locate
//     * @param icfg The inter-procedural CFG
//     * @return The set of back sliced units
//     */
//    public static Set<Unit> findBackwardSlicing(Unit u, Unit ifUnit, SootMethod method, JimpleBasedInterproceduralCFG icfg) {
//        Set<Unit> backwardSlicing = new HashSet<>();
//
//        /*
//         * A slicing includes the data-flow dependencies and control-flow dependencies
//         * We do not only rely on the Program Dependency Graph as it is no longer maintained
//         * and produced incomplete PDG in some testing apps.
//         */
//
//        // 1. data-flow dependencies
//        Set<Unit> backwardDataBackwardDependencies = findBackwardDataDependencies(u, m, cg, d3Algo);
//        backwardSlicing.addAll(backwardDataBackwardDependencies);
//
//        // 2. control-flow dependencies
//        Set<Unit> dominators = findDominators(u, m, icfg);
//        for (Unit d : dominators) {
//            if (!backwardSlicing.contains(d)) {
//                // find data-flow dependencies of this dominator
//                backwardSlicing.addAll(findBackwardDataDependencies(d, icfg.getMethodOf(d), cg, d3Algo));
//            }
//        }
//        backwardSlicing.addAll(dominators);
//
//        // 3. we use the built-in backward slicing to get the intra-procedural backward slicing
//        try {
//            Set<Unit> builtInBackwardSlicing = findInternalBackwardSlicing(u,
//                    new HashMutablePDG(new BriefUnitGraph(m.getActiveBody())));
//            backwardSlicing.addAll(builtInBackwardSlicing);
//        } catch (Exception e) {
//            // do nothing here
//        }
//
//        // 4. TRICK here: we add all IfStmt before u and its corresponding definitions into slicing,
//        //    because most developers will use this method
//        try {
//            Set<Unit> trickySlicing = findTrickySlicing(u, m);
//            backwardSlicing.addAll(trickySlicing);
//        } catch (Exception e) {
//            // do nothing
//        }
//
//        return backwardSlicing;
//    }

    /**
     * findDominators find all dominators of u in icfg
     *
     * @param u    the unit who wants to find its dominators
     * @param m    the method where the unit lives at
     * @param icfg the icfg where the unit lives at
     * @return     the set of dominators in the icfg of the unit
     */
    public static Set<Unit> findDominators(Unit u, SootMethod m, JimpleBasedInterproceduralCFG icfg) {
        try {
            DirectedGraph<Unit> graph = icfg.getOrCreateUnitGraph(m);
            MHGDominatorsFinder<Unit> mhgDominatorsFinder = new MHGDominatorsFinder<>(graph);
            return new HashSet<>(mhgDominatorsFinder.getDominators(u));
        } catch (Exception e) {
            return new HashSet<>();
        }
    }
}
