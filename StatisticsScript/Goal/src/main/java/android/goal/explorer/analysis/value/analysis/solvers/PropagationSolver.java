package android.goal.explorer.analysis.value.analysis.solvers;

import android.goal.explorer.analysis.value.analysis.problem.PropagationProblem;
import android.goal.explorer.analysis.value.values.propagation.BasePropagationValue;
import heros.solver.IDESolver;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;

/**
 * A solver for the MVMF constant propagation problem.
 */
public class PropagationSolver extends
        IDESolver<Unit, Value, SootMethod, BasePropagationValue, JimpleBasedInterproceduralCFG> {

    public PropagationSolver(PropagationProblem propagationProblem) {
        super(propagationProblem);
    }

}
