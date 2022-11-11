package android.goal.explorer.analysis.value.analysis.strings;

import soot.SootMethod;
import soot.jimple.Stmt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public abstract class LanguageConstraints {
    static int g_uid = 0;
    public final int uid;

    public LanguageConstraints() {
        uid = ++g_uid;
    }

    @Override
    public String toString() {
        ToStringVisitor tsv = new ToStringVisitor();
        this.apply(tsv);
        return tsv.result.toString();
    }

    public String toString(Set<LanguageConstraints> seen) {
        ToStringVisitor tsv = new ToStringVisitor(seen);
        this.apply(tsv);
        return tsv.result.toString();
    }

    public abstract void apply(switchLC slc);

    /*************************************************************************/
    /* Top means an unknown/undefined/unconstrained ... ********************* */
    /*************************************************************************/
    public static final class Top extends LanguageConstraints {
        private static final Top top = new Top();

        private Top() {
        };

        public static LanguageConstraints v() {
            return top;
        }

        @Override
        public void apply(switchLC slc) {
            slc.caseTop(this);
        }
    }

    /*************************************************************************/
    /* Bottom means the universal language (.* in terms of regular expr) */
    /*************************************************************************/
    public static final class Bottom extends LanguageConstraints {
        private static final Bottom bottom = new Bottom();

        public static LanguageConstraints v() {
            return bottom;
        }

        @Override
        public void apply(switchLC slc) {
            slc.caseBottom(this);
        }
    }

    /*************************************************************************/
    /* Terminal/known constant values */
    /*************************************************************************/
    public static final class Terminal extends LanguageConstraints {
        public final String term;

        public Terminal(String t) {
            term = t;
        }

        @Override
        public void apply(switchLC slc) {
            slc.caseTerminal(this);
        }
    }

    /*************************************************************************/
    /* Parameter */
    /*************************************************************************/
    public static final class Parameter extends LanguageConstraints {
        SootMethod sm;
        public final int paramNum;

        public Parameter(SootMethod sm0, int paramNum0) {
            sm = sm0;
            paramNum = paramNum0;
        }

        @Override
        public void apply(switchLC slc) {
            slc.caseParameter(this);
        }
    }

    /*************************************************************************/
    /* Call */
    /*************************************************************************/
    public static final class Call extends LanguageConstraints {
        SootMethod sm_context;
        public Stmt stmt;
        Box[] arguments;

        public Call(SootMethod sm0, Stmt stmt0, Box[] arguments0) {
            sm_context = sm0;
            stmt = stmt0;
            arguments = arguments0;
        }

        SootMethod callee() {
            return stmt.getInvokeExpr().getMethod();
        }

        @Override
        public void apply(switchLC slc) {
            slc.caseCall(this);
        }
    }

    /*************************************************************************/
    public static class Union extends LanguageConstraints {
        public List<Box> elements;

        public Union() {
            elements = new ArrayList<Box>();
        }

        void addLCB(Box elm) {
            elements.add(elm);
        }

        List<Box> getElementBoxes() {
            return elements;
        }

        @Override
        public void apply(switchLC slc) {
            slc.caseUnion(this);
        }
    }

    /*************************************************************************/
    public static class Concatenate extends LanguageConstraints {
        public Box left, right;

        public Concatenate(Box l, Box r) {
            left = l;
            right = r;
        }

        @Override
        public void apply(switchLC slc) {
            slc.caseConcatenate(this);
        }
    }

    /*************************************************************************/
    public static class Eq extends LanguageConstraints {
        public Box lcb;

        Eq(Box lcb0) {
            lcb = lcb0;
        }

        void setLCB(Box lcb0) {
            lcb = lcb0;
        }

        Box getLCB() {
            return lcb;
        }

        @Override
        public void apply(switchLC slc) {
            slc.caseEq(this);
        }
    }

    /*************************************************************************/
    /*
     * Pending is placeholder, to be used to stop a possibily recursive traversal
     * /************************************************************************
     */
    // TBD: looks like I can get rid of this
    public static final class Pending extends LanguageConstraints {
        private static final Pending pending = new Pending();

        public static LanguageConstraints v() {
            return pending;
        }

        @Override
        public void apply(switchLC slc) {
            slc.casePending(this);
        }
    }

    /*************************************************************************/
    /*************************************************************************/
    public static class Box {
        static int g_uid = 0;
        int uid;

        LanguageConstraints lc;

        public Box() {
            uid = ++g_uid;
            lc = null;
        }

        public Box(LanguageConstraints lc0) {
            uid = ++g_uid;
            lc = lc0;
        }

        public void setLC(LanguageConstraints lc0) {
            lc = lc0;
        }

        public LanguageConstraints getLC() {
            return lc;
        }

        static boolean show_uid = false;

        @Override
        public String toString() {
            String lcstr = (lc == null) ? "{NULL}" : lc.toString();
            if (show_uid)
                return "[" + uid + "]" + lcstr;
            else
                return lcstr;
        }

        public String toString(Set<LanguageConstraints> seen) {
            String lcstr = (lc == null) ? "{NULL}" : lc.toString(seen);
            if (show_uid)
                return "[" + uid + "]" + lcstr;// only used for debugging
            return lcstr;
        }

        public void apply(switchLC slc) {
            if (lc != null)
                lc.apply(slc);
        }

        // ///////////////////////////////////////////////
        static Box mergeListLCB(List<Box> lst) {
            if (lst.size() == 1)
                return lst.get(0);
            Union lcu = new Union();
            for (Iterator<Box> it = lst.iterator(); it.hasNext();) {
                Box lcb = it.next();
                lcu.addLCB(lcb);
            }
            Box res = new Box(lcu);
            return res;
        }
    }

    /*************************************************************************/
    public static class FieldBox extends Box {
        @Override
        public void apply(switchLC slc) {
            boolean old_mode = slc.setFieldMode(true);
            if (lc != null)
                lc.apply(slc);
            else {// HACK
                if (slc instanceof RecursiveDAGSolverVisitorLC) {
                    Box tmpNullBox = NullConstantBox();
                    tmpNullBox.apply(slc);
                }
            }
            slc.setFieldMode(old_mode);
        }
    }

    /*************************************************************************/
    private static Box bottomBox = null;

    public static Box BottomBox() {
        if (bottomBox == null)
            bottomBox = new Box(Bottom.v());
        return bottomBox;
    }

    public static Box NullConstantBox() {
        return new Box(new Terminal("NULL-CONSTANT"));
    }
}

class ToStringVisitor implements switchLC {
    boolean recursive = false;
    Set<LanguageConstraints> seen;
    StringBuilder result;

    ToStringVisitor() {
        seen = new HashSet<LanguageConstraints>();
        result = new StringBuilder();
    }

    ToStringVisitor(Set<LanguageConstraints> seen0) {
        seen = seen0;
        result = new StringBuilder();
    }

    static boolean show_uid = false;

    String uid(LanguageConstraints lc) {
        if (show_uid)
            return "#" + lc.uid + ":";
        else
            return "";
    }

    @Override
    public boolean setFieldMode(boolean mode) {
        return false;
    }

    /***************************************************************************/
    @Override
    public void caseTop(LanguageConstraints.Top lc) {
        result.append(uid(lc) + "<Top>");
    }

    @Override
    public void caseBottom(LanguageConstraints.Bottom lc) {
        result.append(uid(lc) + "(.*)");// More info?
    }

    @Override
    public void caseTerminal(LanguageConstraints.Terminal lc) {
        result.append(uid(lc) + "term(" + lc.term + ")");
    }

    @Override
    public void caseParameter(LanguageConstraints.Parameter lc) {
        result.append(uid(lc) + "param#" + lc.sm + "#" + lc.paramNum);
    }

    @Override
    public void caseUnion(LanguageConstraints.Union lc) {
        if (seen.contains(lc)) {
            recursive = true;
            result.append(uid(lc) + "{...}");
            return;
        }
        seen.add(lc);
        // get all elements, and sort them for deterministic results
        ArrayList<String> list = new ArrayList<String>();
        Iterator<LanguageConstraints.Box> it = lc.elements.iterator();
        while (it.hasNext()) {
            LanguageConstraints.Box lcb = it.next();
            list.add(lcb.toString(seen));
        }
        Collections.sort(list);
        result.append(uid(lc) + "union(");
        for (Iterator<String> lit = list.iterator(); lit.hasNext();) {
            String lstr = lit.next();
            result.append(lstr);
            if (lit.hasNext())
                result.append(" | ");
        }
        result.append(")");
        assert (seen.contains(lc));
        seen.remove(lc);
    }

    @Override
    public void caseConcatenate(LanguageConstraints.Concatenate lc) {
        if (seen.contains(lc)) {
            recursive = true;
            result.append(uid(lc) + "{...}");
            return;
        }
        seen.add(lc);
        result.append(uid(lc) + "cat((" + lc.left.toString(seen) + ")(" + lc.right.toString(seen)
                + "))");
        assert (seen.contains(lc));
        seen.remove(lc);
    }

    @Override
    public void caseEq(LanguageConstraints.Eq lc) {
        if (seen.contains(lc)) {
            recursive = true;
            result.append(uid(lc) + "{...}");
            return;
        }
        seen.add(lc);
        // result.append(uid(lc) + "eq(" + lc.lcb.toString(seen) + ")");
        // result.append(uid(lc) + "[" + lc.lcb.toString(seen) + "]");
        result.append(uid(lc) + lc.lcb.toString(seen));
        assert (seen.contains(lc));
        seen.remove(lc);
    }

    @Override
    public void casePending(LanguageConstraints.Pending lc) {
        result.append(uid(lc) + "<PENDING??!>");
    }

    @Override
    public void caseCall(LanguageConstraints.Call lc) {
        if (seen.contains(lc)) {
            recursive = true;
            result.append(uid(lc) + "{...}");
            return;
        }
        seen.add(lc);

        result.append(uid(lc) + "[call]" + lc.callee().getName() + "(");
        for (int i = 0; i < lc.arguments.length; ++i) {
            String str_arg = (lc.arguments[i] == null) ? "null" : lc.arguments[i].toString(seen);
            if (i < lc.arguments.length - 1)
                str_arg = str_arg + ",";
            result.append(uid(lc) + str_arg);
        }
        result.append(")");

        assert (seen.contains(lc));
        seen.remove(lc);
    }
}