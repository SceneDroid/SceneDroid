package android.goal.explorer.analysis.value.analysis;

import android.goal.explorer.analysis.value.AnalysisParameters;
import org.pmw.tinylog.Logger;
import soot.Local;
import soot.SootField;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.IdentityStmt;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Stack;

/**
 * An abstract backward argument value analysis, which provides utility functions.
 */
public abstract class BackwardValueAnalysis extends ArgumentValueAnalysis {

    /**
     * Returns all assignments for a local variable. This walks the interprocedural control flow graph
     * back from a statement looking for all assignments to a given local variable.
     *
     * @param start The statement where the analysis should start.
     * @param local The local variable whose assignments should be found.
     * @param init A boolean that indicates whether the analysis should be initialized. This should
     *          always be true for non-recursive calls.
     * @param visitedUnits The set of statements visited by the analysis.
     * @return The set of assignment statements for the local variable.
     */
    protected List<DefinitionStmt> findAssignmentsForLocal(Unit start, Local local, boolean init,
                                                           Set<Pair<Unit, Local>> visitedUnits) {
        SootMethod method = AnalysisParameters.v().getIcfg().getMethodOf(start);
        ExceptionalUnitGraph graph = new ExceptionalUnitGraph(method.getActiveBody());
        List<DefinitionStmt> result = new ArrayList<>();

        Stack<Unit> stack = new Stack<>();
        stack.push(start);
        if (init) {
            visitedUnits.clear();
        }

        while (!stack.empty()) {
            Unit current = stack.pop();
            Logger.debug(current + " " + current.getClass());
            //Pair<Unit, Value> pair = new Pair<>(current, local);
            Pair<Unit, Local> pair = new Pair<>(current, local);
            if (visitedUnits.contains(pair)) {
                continue;
            }
            visitedUnits.add(pair);
            if (current instanceof IdentityStmt) {
                IdentityStmt identityStmt = (IdentityStmt) current;
                // method.
                if (identityStmt.getLeftOp().equivTo(local)) {
                    result.add(identityStmt);
                }
            } else if (current instanceof AssignStmt) {
                AssignStmt assignStmt = (AssignStmt) current;
                if (assignStmt.getLeftOp().equivTo(local)) {
                    if (assignStmt.getRightOp() instanceof Local) {
                        result.addAll(findAssignmentsForLocal(current, (Local) assignStmt.getRightOp(), false,
                                visitedUnits));
                    } 
                    else{
                        
                        result.add(assignStmt);
                    }
                    // The assignment generates the local on that path.
                    // Anything before is irrelevant.
                    continue;
                }
            }
            for (Unit pred : graph.getPredsOf(current)) {
                stack.push(pred);
            }
        }
        return result;
    }

    protected List<DefinitionStmt> findAssignmentsForLocalOrFieldRef(Unit start, Value local, boolean init,
                                                           Set<Pair<Unit, Value>> visitedUnits) {
        List<DefinitionStmt> result = new ArrayList<>();
        SootMethod method = AnalysisParameters.v().getIcfg().getMethodOf(start);
        if(method != null) {
            ExceptionalUnitGraph graph = new ExceptionalUnitGraph(method.getActiveBody());


            Stack<Unit> stack = new Stack<>();
            stack.push(start);
            if (init) {
                visitedUnits.clear();
            }

            while (!stack.empty()) {
                Unit current = stack.pop();
                Logger.debug(current + " " + current.getClass());
                //Pair<Unit, Value> pair = new Pair<>(current, local);
                Pair<Unit, Value> pair = new Pair<>(current, local);
                if (visitedUnits.contains(pair)) {
                    continue;
                }
                visitedUnits.add(pair);
                if (current instanceof IdentityStmt) {
                    IdentityStmt identityStmt = (IdentityStmt) current;
                    // method.
                    if (identityStmt.getLeftOp().equivTo(local)) {
                        result.add(identityStmt);
                    }
                } else if (current instanceof AssignStmt) {
                    AssignStmt assignStmt = (AssignStmt) current;
                    if (assignStmt.getLeftOp().equivTo(local)) {
                        if ((assignStmt.getRightOp() instanceof Local) || (assignStmt.getRightOp() instanceof InstanceFieldRef)) {
                            result.addAll(findAssignmentsForLocalOrFieldRef(current, assignStmt.getRightOp(), false,
                                    visitedUnits));
                        } else if (assignStmt.getRightOp() instanceof CastExpr) {
                            Logger.debug("Found a cast expr");
                            Value op = ((CastExpr) assignStmt.getRightOp()).getOp();
                            if (op instanceof InstanceFieldRef || op instanceof Local) {
                                result.addAll(findAssignmentsForLocalOrFieldRef(current, op, false, visitedUnits));
                                //Logger.debug("The references {}", findAssignmentsForFieldRef(current, (FieldRef) op, false, new HashSet<>()));
                            } else
                                result.add(assignStmt);
                        } else
                            result.add(assignStmt);
                        // The assignment generates the local on that path.
                        // Anything before is irrelevant.
                        continue;
                    }
                }
                for (Unit pred : graph.getPredsOf(current)) {
                    stack.push(pred);
                }
            }
        }
        else
            Logger.error("Could not retrieve method for start unit {}", start);
        return result;
    }
}
