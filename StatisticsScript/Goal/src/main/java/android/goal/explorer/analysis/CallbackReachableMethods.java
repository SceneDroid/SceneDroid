package android.goal.explorer.analysis;

import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootClass;
import soot.jimple.infoflow.android.InfoflowAndroidConfiguration;
import soot.jimple.infoflow.android.callbacks.ComponentReachableMethods;
import soot.jimple.infoflow.util.SystemClassHandler;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.Filter;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CallbackReachableMethods extends ComponentReachableMethods {

    private MethodOrMethodContext srcMethod;
    private MultiMap<MethodOrMethodContext, List<Edge>> edgeMap;

    /**
     * Creates a new instance of the {@link ComponentReachableMethods} class
     *
     * @param config            The configuration of the data flow solver
     * @param originalComponent The original component for which we are looking for callback
     *                          registrations. This information is used to more precisely model
     *                          calls to abstract methods.
     * @param entryPoint The method to look for reachable methods
     */
    public CallbackReachableMethods(InfoflowAndroidConfiguration config, SootClass originalComponent,
                                    MethodOrMethodContext entryPoint) {
        super(config, originalComponent, Collections.singletonList(entryPoint));
        srcMethod = entryPoint;
        edgeMap = new HashMultiMap<>();
    }

    /**
     * Adds the methods from given edge iterator
     * @param edges The edges in form of Edge iterator
     */
    private void addMethodsFromEdges(Iterator<Edge> edges) {
        while (edges.hasNext()) {
            Edge e = edges.next();
            MethodOrMethodContext tgt = e.getTgt();
            addMethodFromEdge(tgt, e);
        }
    }

    /**
     * Adds a method from a given edge
     * @param e The given edge
     */
    private void addMethodFromEdge(MethodOrMethodContext m, Edge e) {
        // Filter out methods in system classes
        if (!SystemClassHandler.v().isClassInSystemPackage(m.method().getDeclaringClass().getName())) {
            MethodOrMethodContext src = e.getSrc();
            if (set.add(m)) {
                reachables.add(m);

                // update the edge map
                Set<List<Edge>> srcEdges = edgeMap.get(src);
                if (srcEdges!=null && !srcEdges.isEmpty()) {
                    for (List<Edge> edgeList : new ArrayList<>(edgeMap.get(src))) {
                        List<Edge> copy = new ArrayList<>(edgeList);
                        copy.add(e);
                        edgeMap.put(m, copy);
                    }
                } else {
                    edgeMap.put(m, Collections.singletonList(e));
                }
            }
        }
    }

    public void update() {
        while (unprocessedMethods.hasNext()) {
            CallGraph callGraph = Scene.v().getCallGraph();
            MethodOrMethodContext m = unprocessedMethods.next();
            Filter filter = createEdgeFilter();
            Iterator<Edge> targets = filter.wrap(callGraph.edgesOutOf(m));
            this.addMethodsFromEdges(targets);
        }
    }

    /**
     * Gets the map of edges to methods
     * @return The map of edges to methods
     */
    public MultiMap<MethodOrMethodContext, List<Edge>> getEdgeMap() {
        return edgeMap;
    }

    /**
     * Gets the context edges
     * @param method The method to get the context edges
     * @return The context edges
     */
    public Set<List<Edge>> getContextEdges(MethodOrMethodContext method) {
        return edgeMap.get(method);
    }

    /**
     * Gets the source method of this reachable method
     * @return The source method of this reachable method
     */
    public MethodOrMethodContext getSrcMethod() {
        return srcMethod;
    }
}
