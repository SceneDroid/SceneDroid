package android.goal.explorer.analysis.value.analysis;

import android.goal.explorer.analysis.value.AnalysisParameters;
import android.goal.explorer.analysis.value.Constants;

import com.google.common.primitives.Ints;

import org.pmw.tinylog.Logger;

import soot.jimple.infoflow.util.SystemClassHandler;

import soot.Local;
import soot.Scene;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.DefinitionStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.FieldRef;
import soot.jimple.LongConstant;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * An argument value analysis for integer types.
 */
public class IntValueAnalysis extends BackwardValueAnalysis {
    private static final int TOP_VALUE = Constants.ANY_INT;

    @Override
    public Set<Object> computeInlineArgumentValues(String[] inlineValues) {
        Set<Object> result = new HashSet<>(inlineValues.length);

        for (String intString : inlineValues) {
            result.add(Integer.parseInt(intString));
        }

        return result;
    }

    @Override
    public Set<Object> computeVariableValues(Value value, Stmt callSite) {
        return computeVariableValues(value, callSite, new HashSet<>());
    }

    /**
     * Returns the possible values for an integer variable.
     *
     * @param value The variable whose value we are looking for.
     * @param start The statement where the variable is used.
     * @return The set of possible values for the variable.
     */
    @Override
    public Set<Object> computeVariableValues(Value value, Stmt start, Set<List<Edge>> edges) {
        Logger.debug("Computing variable values for {} {}", value, start);
        if (value instanceof IntConstant) {
            return Collections.singleton((Object) ((IntConstant) value).value);
        } else if (value instanceof LongConstant) {
            return Collections.singleton((Object) ((LongConstant) value).value);
        } else if (value instanceof Local) {
            return findIntAssignmentsForLocal(start, (Local) value, new HashSet<Stmt>(), edges);
        } 
        else if(value instanceof StaticFieldRef){
            SootField field = ((StaticFieldRef)value).getField(); //maybe check if the name is like R.id and then look up the value in ResourceValueProvider ?
            Logger.debug("The tags, {} and declaration {}", field.getTags(), field.getDeclaration());
            //List<DefinitionStmt> definitionStmts = findAssignmentsForFieldRef(start, (FieldRef)value, true, new HashSet<>());
            //Logger.debug("The definitions : {}", definitionStmts);
            if(!field.getTags().isEmpty()){
                int finalValue = Ints.fromByteArray(field.getTags().get(0).getValue());
                return Collections.singleton((Object)finalValue);
            }
        }
        return Collections.singleton((Object) TOP_VALUE);
    }

    /**
     * Return all possible values for an integer local variable.
     *
     * @param start The statement where the analysis should start.
     * @param local The local variable whose values we are looking for.
     * @param visitedStmts The set of visited statement.
     * @return The set of possible values for the local variable.
     */
    private Set<Object> findIntAssignmentsForLocal(Stmt start, Local local, Set<Stmt> visitedStmts,
                                                   Set<List<Edge>> contextEdges) {
        List<DefinitionStmt> assignStmts =
                findAssignmentsForLocalOrFieldRef(start, local, true, new HashSet<>());
        Set<Object> result = new HashSet<>(assignStmts.size());

        for (DefinitionStmt assignStmt : assignStmts) {
            Value rhsValue = assignStmt.getRightOp();
            if (rhsValue instanceof IntConstant) {
                result.add(((IntConstant) rhsValue).value);
            } else if (rhsValue instanceof LongConstant) {
                result.add(((LongConstant) rhsValue).value);
            } else if (rhsValue instanceof ParameterRef) {
                ParameterRef parameterRef = (ParameterRef) rhsValue;
                Iterator<Edge> edges =
                        Scene.v().getCallGraph()
                                .edgesInto(AnalysisParameters.v().getIcfg().getMethodOf(assignStmt));
                while (edges.hasNext()) {
                    Edge edge = edges.next();
                    InvokeExpr invokeExpr = edge.srcStmt().getInvokeExpr();
                    Value argValue = invokeExpr.getArg(parameterRef.getIndex());
                    if (argValue instanceof IntConstant) {
                        result.add(((IntConstant) argValue).value);
                    } else if (argValue instanceof LongConstant) {
                        result.add(((LongConstant) argValue).value);
                    } else if (argValue instanceof Local) {
                        Set<Object> newResults =
                                findIntAssignmentsForLocal(edge.srcStmt(), (Local) argValue, visitedStmts, contextEdges);
                        result.addAll(newResults);
                    } else {
                        result.add(TOP_VALUE);
                    }
                }
            }
           /* else if (rhsValue instanceof FieldRef){
                Logger.debug("Found a field ref {}", rhsValue);
                return 
                //computeVariableValues(rhsValue, assignStmt);
            }*/
             else if (rhsValue instanceof InvokeExpr) {
                SootMethod sm = ((InvokeExpr) rhsValue).getMethod(); //TO-DO: need to find the target of the method call and look its definitions
                if (SystemClassHandler.v().isClassInSystemPackage(sm.method().getDeclaringClass().getName()))
                    return findIntAssignmentsFromFrameworkMethod(local, assignStmt, (InvokeExpr)rhsValue, sm, contextEdges);
                for (List<Edge> edgeList : contextEdges) {
                    Edge edge = edgeList.iterator().next();
                    // Check for method overridden
                    if (edge.src().getDeclaringClass().declaresMethod(sm.getSubSignature()))
                        sm = edge.src().getDeclaringClass().getMethod(sm.getSubSignature());

                    Collection<Unit> returnSites = AnalysisParameters.v().getIcfg().getEndPointsOf(sm);
                    for (Unit returnSite : returnSites) {
                        if (returnSite instanceof ReturnStmt) {
                            Value value = ((ReturnStmt) returnSite).getOp();
                            return computeVariableValues(value, (Stmt)returnSite);
                        }
                    }
                }
            } else {
                return Collections.singleton((Object) TOP_VALUE);
            }
        }

        return result;
    }

    private Set<Object> findIntAssignmentsFromFrameworkMethod(Local local, Stmt stmt, InvokeExpr expr, SootMethod frameworkMethod, Set<List<Edge>> contextEdges){
        if(frameworkMethod.getName().equals("getId")){ //TODO: refine with signature instead
            //extract the target of the invocation
            Value target = ((InstanceInvokeExpr)expr).getBase();
            if(target instanceof Local)
                return findIntAssignmentsForLocal(stmt, (Local)target, new HashSet<Stmt>(),contextEdges);
        }
        else if(frameworkMethod.getName().equals("findViewById")){
            Value elementId = expr.getArg(0);
            return computeVariableValues(elementId, stmt, contextEdges);
        }
        return Collections.singleton((Object) TOP_VALUE);
    }

    @Override
    public Object getTopValue() {
        return TOP_VALUE;
    }

}
