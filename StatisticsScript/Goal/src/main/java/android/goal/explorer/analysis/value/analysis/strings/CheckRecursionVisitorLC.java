package android.goal.explorer.analysis.value.analysis.strings;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class CheckRecursionVisitorLC implements switchLC {
    boolean recursive = false;
    Set<LanguageConstraints> seen;
    boolean follow_calls;

    CheckRecursionVisitorLC() {
        seen = new HashSet<LanguageConstraints>();
    }

    CheckRecursionVisitorLC(boolean follow_calls0) {
        seen = new HashSet<LanguageConstraints>();
        follow_calls = follow_calls0;
    }

    @Override
    public boolean setFieldMode(boolean mode) {
        return false;
    }

    /***************************************************************************/
    @Override
    public void caseTop(LanguageConstraints.Top lc) {
        ;
    }

    @Override
    public void caseBottom(LanguageConstraints.Bottom lc) {
        ;
    }

    @Override
    public void caseTerminal(LanguageConstraints.Terminal lc) {
        ;
    }

    @Override
    public void caseParameter(LanguageConstraints.Parameter lc) {
        ;
    }

    @Override
    public void caseUnion(LanguageConstraints.Union lc) {
        if (seen.contains(lc)) {
            recursive = true;
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
            recursive = true;
            return;
        }
        seen.add(lc);
        lc.left.apply(this);
        lc.right.apply(this);
        assert (seen.contains(lc));
        seen.remove(lc);
    }

    @Override
    public void caseEq(LanguageConstraints.Eq lc) {
        if (seen.contains(lc)) {
            recursive = true;
            return;
        }
        seen.add(lc);
        lc.lcb.apply(this);
        assert (seen.contains(lc));
        seen.remove(lc);
    }

    @Override
    public void casePending(LanguageConstraints.Pending lc) {
        recursive = true;
    }

    @Override
    public void caseCall(LanguageConstraints.Call lc) {
        assert (!follow_calls); // TBD Recursion within a procedure, or overall?
    }
}
