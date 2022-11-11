package android.goal.explorer.analysis.value.analysis.strings;

import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class RecursiveDAGSolverVisitorLC implements switchLC {
    // Must NOT be applied to a recursive graph
    boolean fieldMode = false;

    static int g_uid = 0;
    int uid;
    int warnings = 0;

    Set<Object> result;
    int inline_depth = 0;
    Set<LanguageConstraints> seen;
    LanguageConstraints.Box[] parameters;
    MethodReturnValueAnalysisInterface methodReturnValueAnalysisInterface;

    public RecursiveDAGSolverVisitorLC(int inline_depth0, LanguageConstraints.Box[] actual_arguments,
                                MethodReturnValueAnalysisInterface mrvai) {
        uid = g_uid++;
        seen = new HashSet<LanguageConstraints>();
        result = new HashSet<Object>();
        parameters = actual_arguments;
        inline_depth = inline_depth0;
        methodReturnValueAnalysisInterface = mrvai;
    }

    public RecursiveDAGSolverVisitorLC(int inline_depth0, MethodReturnValueAnalysisInterface mrvai) {
        this(inline_depth0, null, mrvai);
    }

    public RecursiveDAGSolverVisitorLC(int inline_depth0) {
        this(inline_depth0, null, new MethodReturnValueAnalysisInterface() {
            @Override
            public Set<Object> getMethodReturnValues(LanguageConstraints.Call call) {
                return null;
            }
        });
    }

    public RecursiveDAGSolverVisitorLC() {
        this(0);
    }

    public boolean solve(LanguageConstraints.Box lcb) {
        if (lcb == null)
            return false;
        lcb.apply(this);
        return true;
    }

    @Override
    public boolean setFieldMode(boolean mode) {
        boolean old_fieldMode = fieldMode;
        fieldMode = mode;
        return old_fieldMode;
    }

    /***************************************************************************/
    @Override
    public void caseTop(LanguageConstraints.Top lc) {
        warnings++;
    }

    @Override
    public void caseBottom(LanguageConstraints.Bottom lc) {
        result.add("(.*)");
    }

    @Override
    public void caseTerminal(LanguageConstraints.Terminal lc) {
        result.add(lc.term);
    }

    @Override
    public void caseParameter(LanguageConstraints.Parameter lc) {
        if (!fieldMode && parameters != null // we are evaluating a parameter in a call context.
        ) {
            if (parameters[lc.paramNum] != null)
                parameters[lc.paramNum].apply(this);
            else
                result.add("(.*)");
        } else {
            // we are evaluating a field, or parameter in all contexts
            // TBD: Potential infinite recursion!
            LanguageConstraints.Box lcb = Res2Constr.getArgument(lc.sm, lc.paramNum);
            lcb.apply(this);
        }
    }

    @Override
    public void caseUnion(LanguageConstraints.Union lc) {
        if (seen.contains(lc)) {
            result.add("(.*)");
            return;
        }
        seen.add(lc);
        Iterator<LanguageConstraints.Box> it = lc.elements.iterator();
        while (it.hasNext()) {
            LanguageConstraints.Box lcb = it.next();
            lcb.apply(this);
        }
        assert (seen.contains(lc));
        seen.remove(lc);
    }

    @Override
    public void caseConcatenate(LanguageConstraints.Concatenate lc) {
        if (seen.contains(lc)) {
            result.add("(.*)");
            return;
        }
        seen.add(lc);
        Set<Object> old_result = result;
        Set<Object> left_result = new HashSet<Object>();
        result = left_result;
        lc.left.apply(this);

        Set<Object> right_result = new HashSet<Object>();
        result = right_result;
        lc.right.apply(this);

        result = old_result;

        assert (!CCRefVisitor.ARRAY_FIELDS || !left_result.isEmpty());
        assert (!CCRefVisitor.ARRAY_FIELDS || !right_result.isEmpty());
        for (Iterator<Object> lit = left_result.iterator(); lit.hasNext();) {
            Object lstr = lit.next();
            for (Iterator<Object> rit = right_result.iterator(); rit.hasNext();) {
                Object rstr = rit.next();
                if (lstr instanceof String && rstr instanceof String) {
                    result.add((String) lstr + (String) rstr);
                } else {
                    result.add("(.*)");
                }
            }
        }
        assert (seen.contains(lc));
        seen.remove(lc);
    }

    @Override
    public void caseEq(LanguageConstraints.Eq lc) {
        if (seen.contains(lc)) {
            result.add("(.*)");
            return;
        }
        seen.add(lc);
        lc.lcb.apply(this);
        assert (seen.contains(lc));
        seen.remove(lc);
    }

    @Override
    public void casePending(LanguageConstraints.Pending lc) {
        throw new RuntimeException("BAD PENDING!");
    }

    public interface MethodReturnValueAnalysisInterface {
        Set<Object> getMethodReturnValues(LanguageConstraints.Call call);
    }

    @Override
    public void caseCall(LanguageConstraints.Call lc) {
        assert (inline_depth >= 0);
        if (inline_depth == 0)
            result.add("(.*)");
        else {
            Set<Object> methodReturnValues = methodReturnValueAnalysisInterface.getMethodReturnValues(lc);
            if (methodReturnValues == null) {
                // Take care of parameters!
                DBG.BUG("While recursing, parameters in this context look the same as "
                        + "the parameters in the callee context. "
                        + "That is a LanguageConstraints.Parameter passed via lc.arguments"
                        + " is really a parameter of this(caller) function, but it will be"
                        + " mistakenly interpreted as a parameter of the callee");
                RecursiveDAGSolverVisitorLC solveCall =
                        new RecursiveDAGSolverVisitorLC(inline_depth - 1, lc.arguments,
                                methodReturnValueAnalysisInterface);
                // SootMethod callee = lc.callee(); // TBD FIX THIS FOR POLYMORPHISM!/MULTIPLE
                // TARGETS:
                CallGraph cg = Scene.v().getCallGraph();
                boolean seen_any_HACK = false;

                for (Iterator<Edge> ite = cg.edgesOutOf(lc.stmt); ite.hasNext();) {
                    Edge e = ite.next();
                    SootMethod callee = e.tgt();
                    if (callee.getSignature().endsWith(": void <clinit>()>"))
                        continue;
                    seen_any_HACK = true;
                    if (Res2Constr.knownReturn(callee)) {
                        LanguageConstraints.Box lcb_callee_return = Res2Constr.getReturn(callee);
                        /* Note that solveCall.fieldMode is properly reset to false */
                        lcb_callee_return.apply(solveCall);
                        assert (!solveCall.result.isEmpty());
                        result.addAll(solveCall.result);
                    } else
                        result.add("(.*)");
                }
                // Workaround to the fact that sometimes edgesOutOf(lc.stmt) looses info
                if (!seen_any_HACK)
                    result.add("(.*)");
            } else {
                result.addAll(methodReturnValues);
            }
        }
    }

    public Set<Object> getResult() {
        return result;
    }
}
