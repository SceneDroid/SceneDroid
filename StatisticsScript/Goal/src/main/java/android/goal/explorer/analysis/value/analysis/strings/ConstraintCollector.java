package android.goal.explorer.analysis.value.analysis.strings;

import soot.Body;
import soot.G;
import soot.Local;
import soot.MethodOrMethodContext;
import soot.NullType;
import soot.Scene;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.BreakpointStmt;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.DefinitionStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.GotoStmt;
import soot.jimple.IdentityRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NewExpr;
import soot.jimple.NopStmt;
import soot.jimple.ParameterRef;
import soot.jimple.Ref;
import soot.jimple.RetStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.StmtSwitch;
import soot.jimple.StringConstant;
import soot.jimple.TableSwitchStmt;
import soot.jimple.ThisRef;
import soot.jimple.ThrowStmt;
import soot.jimple.internal.AbstractInstanceInvokeExpr;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.shimple.ShimpleBody;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SmartLocalDefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class ConstraintCollector {
    CCVisitor cc_svis;// TBD: get rid of this
    private boolean done_methodCollection = false;
    ExceptionalUnitGraph graph;

    public ConstraintCollector(ExceptionalUnitGraph exceptionalUnitGraph) {
        graph = exceptionalUnitGraph;
        cc_svis = new CCVisitor(graph);
    }

    /*
     * Get the constraints for the values that 'l' would have if used in 's' Assumptions: There must
     * be reaching definitions of l to s. Otherwise an assertion will fail -> TBD should return null
     * This is NOT ssa, so if 'l' is both defined and used by stmt, the result will be for the used
     * value, before the definition. That is, if "s: l = use(l);" the result describes the 'l' inside
     * 'use(l)';
     */
    public LanguageConstraints.Box getConstraintOfAt(Local l, Stmt stmt) {
        if (!done_methodCollection && !done_globalCollection) {
            handleMethod(graph.getBody().getMethod());
            done_methodCollection = true;
        }
        return cc_svis.mergeDefsOfAt(l, stmt);
    }

    public LanguageConstraints.Box getConstraintOfValueAt(Value v, Stmt stmt) {
        if (v instanceof Local)
            return getConstraintOfAt((Local) v, stmt);
        if (v instanceof StringConstant)
            return new LanguageConstraints.Box(new LanguageConstraints.Terminal(
                    ((StringConstant) v).value));
        return null; // BottomBox?
    }

    // ////////////////////////////////////////////////////
    public static interface ModelInterface {
        boolean isExcludedClass(String class_name);
    }

    public static class CCModelInterface implements ModelInterface {
        @Override
        public boolean isExcludedClass(String class_name) {
            // G.v().out.println("DBG: class_name="+class_name);
            // return !class_name.startsWith("dummy");
            return class_name.startsWith("sun.") || class_name.startsWith("java.")
                    || class_name.startsWith("com.") || class_name.startsWith("org.");
        }
    }

    // ////////////////////////////////////////////////////
    static void handleMethod(SootMethod method) {
        // DBG.print("DBG: handle-method="+method);
        // G.v().out.println("DBG: handle-method="+method);
        Body body = method.getActiveBody();
        // if (method.getName().equals("testMergePaths"))
        // G.v().out.println("DBG: handling-non-adjusted-body=" + body);
        AliasAdjuster.changeBody(body);
        // if (method.getName().equals("testMergePaths"))
        // G.v().out.println("DBG: handling-aliasadjusted-body=" + body);
        ExceptionalUnitGraph cfg = new ExceptionalUnitGraph(body);
        // ConstraintCollector cc = new ConstraintCollector(cfg);
        CCVisitor method_svis = new CCVisitor(cfg);
        Stack<Unit> stack = new Stack<Unit>();
        for (Unit unit : cfg.getHeads()) {
            stack.push(unit);
        }
        Set<Unit> visited = new HashSet<Unit>();
        while (!stack.empty()) {
            Unit unit = stack.pop();

            if (visited.contains(unit)) {
                continue;
            } else {
                visited.add(unit);
                Stmt s = (Stmt) unit;
                DBG.dbgStmt(s, "......... looking");
                s.apply(method_svis);
                DBG.dbgStmt(s, "DONE looking and got lcb-for-s: %s", Res2Constr.getStmt(s));
            }
            for (Unit successor : cfg.getSuccsOf(unit)) {
                stack.push(successor);
            }
        }
    }

    private static boolean done_globalCollection = false;

    public static void globalCollection(ModelInterface ccModel) {
        if (done_globalCollection)
            return;
        done_globalCollection = true;
        List<MethodOrMethodContext> eps =
                new ArrayList<MethodOrMethodContext>(Scene.v().getEntryPoints());
        ReachableMethods reachableMethods =
                new ReachableMethods(Scene.v().getCallGraph(), eps.iterator(), null);
        reachableMethods.update();
        for (Iterator<MethodOrMethodContext> iter = reachableMethods.listener(); iter.hasNext();) {
            SootMethod method = iter.next().method();
            // G.v().out.println("DBG: considering-method="+method);
            // G.v().out.println("DBG: method-hasActiveBody="+method.hasActiveBody());
            if (method.hasActiveBody() && !ccModel.isExcludedClass(method.getDeclaringClass().getName()))
                handleMethod(method);
        }
    }
}

// /////////////////////////////////////////////////////////////////////////////
class MySmartLocalDefs {
    /*
     * This class is a wrapper to SmartLocalDefs, and a workaround a major pain in jimple: Example:
     * jimple code for 'String copy1(String s1) { return s1+"!!!";}' is: >>>>> java.lang.String
     * copy1(java.lang.String) { s1: dummy r0; s2: java.lang.String r1, $r3, $r5; s3:
     * java.lang.StringBuilder $r2, $r4;
     *
     * s4: r0 := @this: dummy; s5: r1 := @parameter0: java.lang.String; s6: $r2 = new
     * java.lang.StringBuilder; s7: $r3 = staticinvoke <java.lang.String: java.lang.String
     * valueOf(java.lang.Object)>(r1); s8: specialinvoke $r2.<java.lang.StringBuilder: void
     * <init>(java.lang.String)>($r3); s9: $r4 = virtualinvoke $r2.<java.lang.StringBuilder:
     * java.lang.StringBuilder append(java.lang.String)>("!!!"); s10: $r5 = virtualinvoke
     * $r4.<java.lang.StringBuilder: java.lang.String toString()>(); s11: return $r5; } <<<<< So, $r2
     * seems to be defined only at s6, and SmartLocalDefs.getDefsOfAt($r2, s9) returns s2 But this is
     * misleading, because s8 is in fact the last statement that changed r2.
     *
     * Workaround: keep track of a proxy map. In the above example: s8 is a proxy for s2. So, whenever
     * SmartLocalDefs.getDefsOfAt(l, s) contains a statement that has a proxy (such as s2), replace
     * that value with its proxy (respectively s8),unless* the proxy is the 's' parameter.
     *
     * AUCH! this should be extended for other operations on StringBuilder, such as s9 in the above
     * example. s9 has in fact two results $r4 (explicit) and $r2 (implicit). It's a mess! TBD: can
     * alias analysis fix this?
     */
    Map<Unit, Unit> unit2proxy;
    ExceptionalUnitGraph graph;
    SmartLocalDefs sld;

    MySmartLocalDefs(ExceptionalUnitGraph exceptionalUnitGraph) {
        graph = exceptionalUnitGraph;
        sld = new SmartLocalDefs(graph, new SimpleLiveLocals(graph));
        unit2proxy = new HashMap<Unit, Unit>();

        Body body = graph.getBody();
        /* In case we move to shimple: need to review the uses of getDefsOfAt */
        // G.v().out.println(body);
        assert (!(body instanceof ShimpleBody));

        /*
         * fill in the unit2proxy map: 1. Search for statements like: s1: specialinvoke
         * $Local.<java.lang.StringBuilder: void <init>(java.lang.String)>(Value); 2. For each such
         * statement, find the reaching definition of $Local If my assumption about jimple is correct
         * there should be exactly one such definition; it must be
         * "s0: $Local = new java.lang.StringBuilder;" Record 's1' to be a proxy for 's0':
         * unit2proxy.put(s0, s1);
         */
        // System.out.println(body);
        // System.out.println(body.getMethod());
        Iterator<Unit> unitIt = graph.iterator();
        while (unitIt.hasNext()) {
            Unit u = unitIt.next();
            if (u instanceof InvokeStmt) {
                InvokeStmt iStmt = (InvokeStmt) u;
                InvokeExpr iexpr = iStmt.getInvokeExpr();
                if (isSpecialInvokeHack(iexpr)) {
                    Value vBase = ((soot.jimple.internal.JSpecialInvokeExpr) iexpr).getBase();
                    assert (vBase instanceof Local);
                    assert (vBase.getType().toString().equals("java.lang.StringBuilder") || vBase.getType()
                            .toString().equals("java.lang.String"));

                    // System.out.println(vBase);
                    // System.out.println(iStmt);
                    List<Unit> defs = sld.getDefsOfAt((Local) vBase, iStmt);
                    // Expect exactly one def: $rXXX = new java.lang.StringBuilder;
                    assert (defs.size() == 1);
                    Unit ud = defs.get(0);
                    assert (ud != null);
                    assert (ud instanceof DefinitionStmt);
                    DefinitionStmt dStmt = (DefinitionStmt) ud;
                    Value lop = dStmt.getLeftOp();
                    Value rop = dStmt.getRightOp();
                    assert (CCVisitor.isStringLike(lop));
                    assert (rop instanceof NewExpr);
                    unit2proxy.put(ud, iStmt);
                }
            }
        }
    }

    public List<Unit> getDefsOfAt(Local l, Unit s) {
        List<Unit> res = new ArrayList<Unit>();
        List<Unit> defs = sld.getDefsOfAt(l, s);
        Iterator<Unit> rDefsIt = defs.iterator();
        while (rDefsIt.hasNext()) {
            Unit orig = rDefsIt.next();
            Unit proxy = unit2proxy.get(orig);
            if (proxy != null && proxy != s)
                res.add(proxy);
            else
                res.add(orig);
        }
        return res;
    }

    static boolean isSpecialInvokeHack(Value v) {
        if (!(v instanceof soot.jimple.internal.JSpecialInvokeExpr))
            return false;
        SootMethod sm = ((InvokeExpr) v).getMethod();
        String m_signature = sm.getSignature();
        return m_signature.startsWith("<java.lang.StringBuilder: void <init>(")
                || m_signature.startsWith("<java.lang.String: void <init>");
        // equals("<java.lang.StringBuilder: void <init>(java.lang.String)>")
        // equals("<java.lang.StringBuilder: void <init>()>");
    }

    static boolean hasStringParameters(Value v) {
        if (!(v instanceof InvokeExpr))
            return false;
        InvokeExpr iexpr = (InvokeExpr) v;
        SootMethod sm = iexpr.getMethod();
        String m_signature = sm.getSignature();
        return m_signature.contains("java.lang.String,") || m_signature.contains("java.lang.String)");
    }

    static boolean hasStringSideEffectHack(Value v) {
        if (!(v instanceof AbstractInstanceInvokeExpr))
            return false;
        SootMethod sm = ((AbstractInstanceInvokeExpr) v).getMethod();
        String m_signature = sm.getSignature();
        // TBD: make this more accurate...
        return m_signature.startsWith("<java.lang.StringBuilder: java.lang.StringBuilder");
    }
}

/*
 * CCVisitor does the real work.
 */
class CCVisitor implements StmtSwitch {
    static int verbose_level = 0;
    MySmartLocalDefs mld;
    CCExprVisitor ccExprVisitor;
    ExceptionalUnitGraph graph;
    SootMethod sm;

    CCVisitor(ExceptionalUnitGraph graph0) {
        graph = graph0;
        sm = graph.getBody().getMethod();
        mld = new MySmartLocalDefs(graph);
        ccExprVisitor = new CCExprVisitor(this, sm);
    }

    protected void dbg(String what, Stmt s, Object... args) {
        if (verbose_level > 10)
            G.v().out.println("DBG:CCVisitor." + String.format(what, args) + " " + s + "  class= "
                    + s.getClass().getName());
    }

    protected void dbg(String what) {
        if (verbose_level > 10)
            G.v().out.println("DBG:CCVisitor." + what);
    }

    protected void ignore(String what, Stmt s) {
        dbg(what, s);
    }

    /*
     * Because we don't have phi functions in jimple, take union of reaching defs However I expect
     * this to be very expensive in both time and space. Ex.: if () x = 1; else x = 2; use1(x)
     * use2(x); The mergeDefsOfAt(x,use1) and mergeDefsOfAt(x,use1) will both redundantly compute the
     * same value. TBD: hash it? ...
     * ********************************************************************** This can also be used to
     * return the values 'l' can have at statment 's' a functionality that would also be simpler in
     * SSA.
     */
    LanguageConstraints.Box mergeDefsOfAt(Local l, Stmt stmt) {
        List<Unit> defs = mld.getDefsOfAt(l, stmt);
        assert (defs.size() > 0);

        List<LanguageConstraints.Box> lst = new ArrayList<LanguageConstraints.Box>();
        Iterator<Unit> rDefsIt = defs.iterator();
        Set<Stmt> sset = new HashSet<Stmt>();
        while (rDefsIt.hasNext()) {
            Stmt sdef = (Stmt) rDefsIt.next();
            assert (!sset.contains(sdef));
            sset.add(sdef);
            sdef.apply(this);// recursive
            // G.v().out.println("dbg:def-at: statement:"+ stmt +" local: " + l +
            // "  ...sdef "+sdef);
            LanguageConstraints.Box defb = Res2Constr.getStmt((sdef));
            assert (defb != null);
            lst.add(defb);
        }
        LanguageConstraints.Box lcb = LanguageConstraints.Box.mergeListLCB(lst);
        assert (lcb != null);
        return lcb;
    }

    int countDefsOfAt(Local l, Stmt stmt) {
        int res = mld.getDefsOfAt(l, stmt).size();
        assert (res > 0);
        return res;
    }

    static boolean isStringLike(Value v) { // DL: TBD: I am not sure if this is OK
        // Turns out that we have to chase both Strings and StringBuilders
        // Unsure: what else? Is this the right way to do it, or should use RefType?
        Type type = v.getType();
        String type_name = type.toString();
        return type_name.equals("java.lang.String") || type_name.equals("java.lang.StringBuilder")
                || type_name.equals("java.lang.String[]") // TBD: this seems very
                // hackish
                || (type instanceof NullType) || MySmartLocalDefs.isSpecialInvokeHack(v);
    }

    // ///////////////////
    @Override
    public void caseBreakpointStmt(BreakpointStmt stmt) {
        ignore("caseBreakpointStmt", stmt);
    }

    @Override
    public void caseInvokeStmt(InvokeStmt iStmt) {
        dbg("caseInvokeStmt", iStmt);
        if (Res2Constr.checkAndSetProcessedStmt((iStmt)))
            return;
        if (!(isStringLike(iStmt.getInvokeExpr())
                || MySmartLocalDefs.hasStringParameters(iStmt.getInvokeExpr()) // args may end up in fields
                || MySmartLocalDefs.hasStringSideEffectHack(iStmt.getInvokeExpr())))
            return; // track strings only

        LanguageConstraints.Box iexpr_lcb = ccExprVisitor.eval(iStmt.getInvokeExpr(), iStmt);
        Res2Constr.putStmt(iStmt, iexpr_lcb);
        if (true) {// TBD activate this!
            if (MySmartLocalDefs.hasStringSideEffectHack(iStmt.getInvokeExpr())) {
                AbstractInstanceInvokeExpr iexpr = (AbstractInstanceInvokeExpr) iStmt.getInvokeExpr();
                Value base = iexpr.getBase();
                DBG.dbgDefsOfAt(base, iStmt, graph);
                // //////////////////////////////////////
                SmartLocalDefs sld = new SmartLocalDefs(graph, new SimpleLiveLocals(graph));
                assert (base instanceof Local);
                Local l = (Local) base;
                List<Unit> defs = sld.getDefsOfAt(l, iStmt);
                assert (defs.size() == 1);
                Stmt sdef = (Stmt) defs.get(0);
                Res2Constr.putStmt(sdef, iexpr_lcb);
                // //////////////////////////////////////
            }
        }
    }

    @Override
    public void caseAssignStmt(AssignStmt stmt) {
        if (Res2Constr.checkAndSetProcessedStmt((stmt)))
            return;
        Value lop = stmt.getLeftOp();
        Value rop = stmt.getRightOp();
        if (!(isStringLike(lop) || MySmartLocalDefs.hasStringParameters(rop) // args may end up in
                // fields
        )) {
            Res2Constr.putStmtBottom(stmt);
            return; // track strings only
        }
        dbg("caseAssignStmt", stmt);
        DBG.dbgValue("DBG:caseAssignStmt's lop:", lop);
        assert !(rop instanceof IdentityRef); // DL: don't think this should happen

        LanguageConstraints.Box rop_lcb = ccExprVisitor.eval(rop, stmt);
        Res2Constr.putStmt(stmt, rop_lcb);
        if (lop instanceof Ref) {
            LanguageConstraints.Box lcb = Res2Constr.getStmt(stmt);
            CCRefVisitor ccrv = new CCRefVisitor(this);
            lop.apply(ccrv.s(lcb, stmt));
        }
        // TBD: do the hasStringSideEffectHack thing?
    }

    @Override
    public void caseIdentityStmt(IdentityStmt stmt) {
        dbg("caseIdentityStmt", stmt);
        if (Res2Constr.checkAndSetProcessedStmt((stmt)))
            return;
        Value rop = stmt.getRightOp();
        assert (rop instanceof IdentityRef);

        if (!(rop instanceof ThisRef || rop instanceof ParameterRef || rop instanceof CaughtExceptionRef))
            G.v().out.println("should throw new RuntimeException(\"Unexpected right operand kind!\")");// throw
        // new
        // RuntimeException("Unexpected right operand kind!");

        LanguageConstraints.Box rop_lcb = ccExprVisitor.eval(rop, stmt);
        Res2Constr.putStmt(stmt, rop_lcb);

        dbg("caseIdentityStmt:rop %s class= %s", stmt, rop, rop.getClass().getName());
    }

    @Override
    public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
        ignore("caseEnterMonitorStmt", stmt);
    }

    @Override
    public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
        ignore("caseExitMonitorStmt", stmt);
    }

    @Override
    public void caseGotoStmt(GotoStmt stmt) {
        ignore("caseGotoStmt", stmt);
    }

    @Override
    public void caseIfStmt(IfStmt stmt) {
        ignore("caseIfStmt", stmt);
    }

    @Override
    public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
        ignore("caseLookupSwitchStmt", stmt);
    }

    @Override
    public void caseNopStmt(NopStmt stmt) {
        ignore("caseNopStmt", stmt);
    }

    @Override
    public void caseRetStmt(RetStmt stmt) {
        ignore("caseRetStmt", stmt);
    }

    @Override
    public void caseReturnStmt(ReturnStmt stmt) {
        ignore("caseReturnStmt", stmt);
        Value rop = stmt.getOp();
        LanguageConstraints.Box rop_lcb = ccExprVisitor.eval(rop, stmt);
        Res2Constr.putReturn(sm, stmt, rop_lcb);
    }

    @Override
    public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
        ignore("caseReturnVoidStmt", stmt);
    }

    @Override
    public void caseTableSwitchStmt(TableSwitchStmt stmt) {
        ignore("caseTableSwitchStmt", stmt);
    }

    @Override
    public void caseThrowStmt(ThrowStmt stmt) {
        ignore("caseThrowStmt", stmt);
    }

    @Override
    public void defaultCase(Object obj) {
        throw new RuntimeException("Unknown Stmt!");
    }
}
