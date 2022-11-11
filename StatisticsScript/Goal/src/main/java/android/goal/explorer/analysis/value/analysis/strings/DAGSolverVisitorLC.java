package android.goal.explorer.analysis.value.analysis.strings;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class DAGSolverVisitorLC implements switchLC {
    // Must NOT be applied to a recursive graph

    int warnings = 0;
    Set<String> result;
    int inline_depth = 0;

    DAGSolverVisitorLC() {
        result = new HashSet<String>();
    }

    DAGSolverVisitorLC(int inline_depth0) {
        result = new HashSet<String>();
        inline_depth = inline_depth0;
    }

    boolean solve(LanguageConstraints.Box lcb) {
        if (lcb == null)
            return false;
        CheckRecursionVisitorLC checker = new CheckRecursionVisitorLC();
        lcb.apply(checker);
        if (checker.recursive)
            return false;
        lcb.apply(this);
        return true;
    }

    @Override
    public boolean setFieldMode(boolean mode) {
        return false;
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
        result.add("(.*)");
    }

    @Override
    public void caseUnion(LanguageConstraints.Union lc) {
        Iterator<LanguageConstraints.Box> it = lc.elements.iterator();
        while (it.hasNext()) {
            LanguageConstraints.Box lcb = it.next();
            lcb.apply(this);
        }
    }

    @Override
    public void caseConcatenate(LanguageConstraints.Concatenate lc) {
        DAGSolverVisitorLC solveLeft = new DAGSolverVisitorLC();
        lc.left.apply(solveLeft);
        ToStringVisitor.show_uid = false; // Hackish ...
        DAGSolverVisitorLC solveRight = new DAGSolverVisitorLC();
        lc.right.apply(solveRight);
        for (Iterator<String> lit = solveLeft.result.iterator(); lit.hasNext();) {
            String lstr = lit.next();
            for (Iterator<String> rit = solveRight.result.iterator(); rit.hasNext();) {
                String rstr = rit.next();
                result.add(lstr + rstr);
            }
        }
        warnings += solveLeft.warnings + solveRight.warnings;
    }

    @Override
    public void caseEq(LanguageConstraints.Eq lc) {
        lc.lcb.apply(this);
    }

    @Override
    public void casePending(LanguageConstraints.Pending lc) {
        throw new RuntimeException("BAD PENDING!");
    }

    @Override
    public void caseCall(LanguageConstraints.Call lc) {

        if (inline_depth == 0)
            result.add("(.*)");
        else
            assert (inline_depth == 0); // TBD if > 0
    }
}
