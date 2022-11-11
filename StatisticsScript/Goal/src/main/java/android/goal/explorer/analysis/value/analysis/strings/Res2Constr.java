package android.goal.explorer.analysis.value.analysis.strings;

import soot.SootMethod;
import soot.jimple.FieldRef;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Res2Constr {
    private static Map<Stmt, LanguageConstraints.Box> stmt2constr =
            new HashMap<Stmt, LanguageConstraints.Box>();
    private static Set<Stmt> processed = new HashSet<Stmt>();

    // /////////////////////////////////////////////
    static LanguageConstraints.Box getStmt(Stmt s) {
        LanguageConstraints.Box lcb = stmt2constr.get(s);
        if (lcb == null) {
            lcb = new LanguageConstraints.Box();// //LanguageConstraints.PendingBox()
            stmt2constr.put(s, lcb);
            assert (stmt2constr.get(s) == lcb);
        }
        assert (lcb != null);
        return lcb;
    }

    private static void putInBox(LanguageConstraints.Box lcb, LanguageConstraints.Box new_lcb) {
        LanguageConstraints lc = lcb.getLC();
        /*
         * TBD: optimize and get rid of some of the LanguageConstraints.Eq If rop_lcb is a "temporary",
         * the result of evaluating an expression, rather then the box for some other left-value (e.g.
         * obtained by rop_lcb = Res2Constr.getStmt(...)) Perhaps set a bit in the boxes used by
         * Res2Constr.put..., and if (the bit for rop_lcb is set) lcb.setLC(new
         * LanguageConstraints.Eq(rop_lcb)); else lcb.setLC(rop_lcb.getLC()); // no need for Eq: rop_lcb
         * is a temp
         */
        if (lc == null || lc instanceof LanguageConstraints.Top)
            lcb.setLC(new LanguageConstraints.Eq(new_lcb));
        else if (lc instanceof LanguageConstraints.Union)
            ((LanguageConstraints.Union) lc).addLCB(new_lcb);
        else {
            LanguageConstraints.Union lcu = new LanguageConstraints.Union();
            lcu.addLCB(new LanguageConstraints.Box(lc));
            lcu.addLCB(new_lcb);
            lcb.setLC(lcu);
        }
    }

    static void putStmt(Stmt s, LanguageConstraints.Box new_lcb) {
        LanguageConstraints.Box lcb = getStmt(s);
        putInBox(lcb, new_lcb);
    }

    static void putStmtBottom(Stmt s) {
        LanguageConstraints.Box lcb = getStmt(s);
        lcb.setLC(LanguageConstraints.Bottom.v());
    }

    static boolean checkAndSetProcessedStmt(Stmt s) {
        boolean res = processed.contains(s);
        processed.add(s);
        return res;
    }

    // ///////////////////////////////////////
    private static Map<String, LanguageConstraints.Box> field2constr =
            new HashMap<String, LanguageConstraints.Box>();

    static Map<String, LanguageConstraints.Box> get_field2constr() {
        return field2constr;
    }

    static LanguageConstraints.Box getField(FieldRef fr) {
        String key = fr.getField().toString();
        LanguageConstraints.Box lcb = field2constr.get(key);
        if (lcb == null) {
            lcb = new LanguageConstraints.FieldBox();// //LanguageConstraints.PendingBox()
            field2constr.put(key, lcb);
            assert (field2constr.get(key) == lcb);
        }
        assert (lcb != null);
        return lcb;
    }

    static void putField(FieldRef fr, LanguageConstraints.Box new_lcb) {
        LanguageConstraints.Box lcb = getField(fr);
        putInBox(lcb, new_lcb);
    }

    // ///////////////////////////////////////
    private static Map<SootMethod, LanguageConstraints.Box> ret2constr =
            new HashMap<SootMethod, LanguageConstraints.Box>();

    static boolean knownReturn(SootMethod method) {
        return ret2constr.containsKey(method);
    }

    static LanguageConstraints.Box getReturn(SootMethod method) {
        SootMethod key = method;
        LanguageConstraints.Box lcb = ret2constr.get(key);
        if (lcb == null) {
            lcb = new LanguageConstraints.Box();// //LanguageConstraints.PendingBox()
            ret2constr.put(key, lcb);
            assert (ret2constr.get(key) == lcb);
        }
        assert (lcb != null);
        return lcb;
    }

    static void putReturn(SootMethod sm, ReturnStmt rstmt, LanguageConstraints.Box new_lcb) {
        LanguageConstraints.Box lcb = getReturn(sm);
        putInBox(lcb, new_lcb);
    }

    // ///////////////////////////////////////
    private static Map<String, LanguageConstraints.Box> argument2constr =
            new HashMap<String, LanguageConstraints.Box>();

    static LanguageConstraints.Box getArgument(SootMethod sm, int arg) {
        String key = sm.getSignature() + "#" + arg;
        LanguageConstraints.Box lcb = argument2constr.get(key);
        if (lcb == null) {
            lcb = new LanguageConstraints.Box();// //LanguageConstraints.PendingBox()
            argument2constr.put(key, lcb);
            assert (argument2constr.get(key) == lcb);
        }
        assert (lcb != null);
        return lcb;
    }

    static void putArgument(SootMethod sm, int arg, LanguageConstraints.Box new_lcb) {
        LanguageConstraints.Box lcb = getArgument(sm, arg);
        putInBox(lcb, new_lcb);
    }

    static void
    putCallArguments(SootMethod sm_context, Stmt stmt, LanguageConstraints.Box[] arguments) {
        // result = new LanguageConstraints.Box(new LanguageConstraints.Call(sm, stmt, arguments));
        SootMethod callee = stmt.getInvokeExpr().getMethod();
        for (int i = 0; i < arguments.length; ++i) {
            LanguageConstraints.Box lcb = arguments[i];
            if (lcb != null)
                putArgument(callee, i, lcb);
        }
    }
}
