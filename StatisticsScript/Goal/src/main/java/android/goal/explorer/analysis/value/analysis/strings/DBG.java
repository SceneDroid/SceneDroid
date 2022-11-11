package android.goal.explorer.analysis.value.analysis.strings;

import soot.G;
import soot.Local;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AddExpr;
import soot.jimple.AndExpr;
import soot.jimple.ArrayRef;
import soot.jimple.CastExpr;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.ClassConstant;
import soot.jimple.CmpExpr;
import soot.jimple.CmpgExpr;
import soot.jimple.CmplExpr;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.EqExpr;
import soot.jimple.FloatConstant;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.JimpleValueSwitch;
import soot.jimple.LeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LongConstant;
import soot.jimple.LtExpr;
import soot.jimple.MethodHandle;
import soot.jimple.MethodType;
import soot.jimple.MulExpr;
import soot.jimple.NeExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.OrExpr;
import soot.jimple.ParameterRef;
import soot.jimple.RemExpr;
import soot.jimple.ShlExpr;
import soot.jimple.ShrExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;
import soot.jimple.ThisRef;
import soot.jimple.UshrExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.XorExpr;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SmartLocalDefs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class DBG {
    static int verbose_level = 0;

    public static void dbgEdgesOutOf(Unit u) {
        CallGraph cg = Scene.v().getCallGraph();
        for (Iterator<Edge> it = cg.edgesOutOf(u); it.hasNext();) {
            Edge e = it.next();
            SootMethod tgt = e.tgt();
            G.v().out.println("tgt:" + tgt);
            G.v().out.println("edge:" + e);
        }
    }

    static void imprecise(String msg) {
        if (verbose_level > 100)
            G.v().out.println("IMPRECISION:" + msg);
    }

    public static void dbgValue(String msg, Value v) {
        if (verbose_level > 10) {
            G.v().out.print("DBG:Value:" + msg + ":");
            v.apply(new DBGExprVisitor());
        }
    }

    public static void print(String what, Object... args) {
        if (verbose_level > 10)
            G.v().out.println(String.format(what, args));
    }

    public static void dbgfIELD(StaticFieldRef sfr) {

    }

    public static void dbgUses(Value v) {
        Iterator<ValueBox> it = v.getUseBoxes().iterator();
        while (it.hasNext()) {
            Value o = it.next().getValue();
            dbgValue("USE:", o);
        }
    }

    public static void dbgStmt(Stmt s, String msg, Object... args) {
        if (verbose_level > 10)
            G.v().out.println("dbgStmt:" + String.format(msg, args) + " at stmt: " + s + " of type "
                    + s.getClass().getName());
    }

    public static void dbgFieldMap() {
        Map<String, LanguageConstraints.Box> fmap = Res2Constr.get_field2constr();
        SortedSet<String> keys = new TreeSet<String>(fmap.keySet());
        for (String key : keys) {
            LanguageConstraints.Box lcb = fmap.get(key);
            showLCB(lcb, "field-" + key);
        }
    }

    static void showLCB(LanguageConstraints.Box lcb, String msg) {
        if (verbose_level < 5)
            return;
        G.v().out.println("RESULT-lcb:[" + msg + "]\t=======>\t" + lcb);
        /*
         * Now, in theory we can use any one of the available solvers ...
         *
         * In particular, DAGSolverVisitorLC is a simple solver (and the only one available for now)
         * that only works if there are no recursive constraints (ex. in a loop) and the set is finite.
         */
        // DAGSolverVisitorLC dagvlc = new DAGSolverVisitorLC();
        // RecursiveDAGSolverVisitorLC dagvlc = new RecursiveDAGSolverVisitorLC();
        RecursiveDAGSolverVisitorLC dagvlc = new RecursiveDAGSolverVisitorLC(5);
        if (dagvlc.solve(lcb)) {
            /*
             * dagvlc.result is a set of strings which can contain .* for unknown substrings.
             */
            ArrayList<Object> list = new ArrayList<Object>(dagvlc.result);
            // Collections.sort(list);
            G.v().out.print("RESULT-set:[" + msg + "]\t=======>\t{");
            for (Iterator<Object> lit = list.iterator(); lit.hasNext();) {
                Object lstr = lit.next();
                G.v().out.print("\"" + lstr + "\"; ");
            }
            G.v().out.println("}");
        }
    }

    public static void BUG(String msg) {
        G.v().out.println("KNOWN-BUG:" + msg);
    }

    static void dbgDefsOfAt(Value v, Stmt stmt, ExceptionalUnitGraph graph) {
        if (verbose_level <= 10)
            return;
        SmartLocalDefs sld = new SmartLocalDefs(graph, new SimpleLiveLocals(graph));
        if (!(v instanceof Local)) {
            dbgValue("not a local", v);
        }
        Local l = (Local) v;
        G.v().out.println("dbgDefsOf v=" + v + " at " + stmt + ">>>>>>>>>>>>>>");
        List<Unit> defs = sld.getDefsOfAt(l, stmt);
        Iterator<Unit> rDefsIt = defs.iterator();
        while (rDefsIt.hasNext()) {
            Stmt sdef = (Stmt) rDefsIt.next();
            dbgStmt(sdef, "definition:");
        }
        G.v().out.println("<<<<<<<<<<<<<<");
    }
}

// //////////////////////////////////////////////////////////////////////////
class DBGExprVisitor implements JimpleValueSwitch {
    static int verbose_level = 0;

    protected void show(String what, Value v) {
        if (verbose_level > 5)
            G.v().out.println(what + " v=" + v + "  of class= " + v.getClass().getName() + " type: "
                    + v.getType() + " type's class:" + v.getType().getClass().getName());
    }

    /*************************************************/
    /*********** from ConstantSwitch *****************/
    @Override
    public void caseDoubleConstant(DoubleConstant v) {
        show("caseDoubleConstant", v);
    }

    @Override
    public void caseFloatConstant(FloatConstant v) {
        show("caseFloatConstant", v);
    }

    @Override
    public void caseIntConstant(IntConstant v) {
        show("caseIntConstant", v);
    }

    @Override
    public void caseLongConstant(LongConstant v) {
        show("caseLongConstant", v);
    }

    @Override
    public void caseNullConstant(NullConstant v) {
        show("caseNullConstant", v);
    }

    @Override
    public void caseStringConstant(StringConstant v) {
        show("caseStringConstant", v);
    }

    @Override
    public void caseClassConstant(ClassConstant v) {
        show("caseClassConstant", v);
    }

    /************* from ExprSwitch ********************/
    @Override
    public void caseAddExpr(AddExpr v) {
        show("caseAddExpr", v);
    }

    @Override
    public void caseAndExpr(AndExpr v) {
        show("caseAndExpr", v);
    }

    @Override
    public void caseCmpExpr(CmpExpr v) {
        show("caseCmpExpr", v);
    }

    @Override
    public void caseCmpgExpr(CmpgExpr v) {
        show("caseCmpgExpr", v);
    }

    @Override
    public void caseCmplExpr(CmplExpr v) {
        show("caseCmplExpr", v);
    }

    @Override
    public void caseDivExpr(DivExpr v) {
        show("caseDivExpr", v);
    }

    @Override
    public void caseEqExpr(EqExpr v) {
        show("caseEqExpr", v);
    }

    @Override
    public void caseNeExpr(NeExpr v) {
        show("caseNeExpr", v);
    }

    @Override
    public void caseGeExpr(GeExpr v) {
        show("caseGeExpr", v);
    }

    @Override
    public void caseGtExpr(GtExpr v) {
        show("caseGtExpr", v);
    }

    @Override
    public void caseLeExpr(LeExpr v) {
        show("caseLeExpr", v);
    }

    @Override
    public void caseLtExpr(LtExpr v) {
        show("caseLtExpr", v);
    }

    @Override
    public void caseMulExpr(MulExpr v) {
        show("caseMulExpr", v);
    }

    @Override
    public void caseOrExpr(OrExpr v) {
        show("caseOrExpr", v);
    }

    @Override
    public void caseRemExpr(RemExpr v) {
        show("caseRemExpr", v);
    }

    @Override
    public void caseShlExpr(ShlExpr v) {
        show("caseShlExpr", v);
    }

    @Override
    public void caseShrExpr(ShrExpr v) {
        show("caseShrExpr", v);
    }

    @Override
    public void caseUshrExpr(UshrExpr v) {
        show("caseUshrExpr", v);
    }

    @Override
    public void caseSubExpr(SubExpr v) {
        show("caseSubExpr", v);
    }

    @Override
    public void caseXorExpr(XorExpr v) {
        show("caseXorExpr", v);
    }

    @Override
    public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
        show("caseInterfaceInvokeExpr", v);
    }

    @Override
    public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
        show("caseSpecialInvokeExpr", v);
    }

    @Override
    public void caseStaticInvokeExpr(StaticInvokeExpr v) {
        show("caseStaticInvokeExpr", v);
    }

    @Override
    public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
        show("caseDynamicInvokeExpr", v);
    }

    @Override
    public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
        show("caseVirtualInvokeExpr", v);
    }

    @Override
    public void caseCastExpr(CastExpr v) {
        show("caseCastExpr", v);
    }

    @Override
    public void caseInstanceOfExpr(InstanceOfExpr v) {
        show("caseInstanceOfExpr", v);
    }

    @Override
    public void caseNewArrayExpr(NewArrayExpr v) {
        show("caseNewArrayExpr", v);
    }

    @Override
    public void caseNewMultiArrayExpr(NewMultiArrayExpr v) {
        show("caseNewMultiArrayExpr", v);
    }

    @Override
    public void caseNewExpr(NewExpr v) {
        show("caseNewExpr", v);
    }

    @Override
    public void caseLengthExpr(LengthExpr v) {
        show("caseLengthExpr", v);
    }

    @Override
    public void caseNegExpr(NegExpr v) {
        show("caseNegExpr", v);
    }

    /************* from RefSwitch ********************/
    @Override
    public void caseArrayRef(ArrayRef v) {
        show("caseArrayRef", v);
    }

    @Override
    public void caseStaticFieldRef(StaticFieldRef v) {
        show("caseStaticFieldRef", v);
    }

    @Override
    public void caseInstanceFieldRef(InstanceFieldRef v) {
        show("caseInstanceFieldRef", v);
    }

    @Override
    public void caseParameterRef(ParameterRef v) {
        show("caseParameterRef", v);
    }

    @Override
    public void caseCaughtExceptionRef(CaughtExceptionRef v) {
        show("caseCaughtExceptionRef", v);
    }

    @Override
    public void caseThisRef(ThisRef v) {
        show("caseThisRef", v);
    }

    /**************************************************/
    @Override
    public void caseLocal(Local l) {
        show("caseLocal", l);
    }

    @Override
    public void defaultCase(Object o) {
        G.v().out.println("?!?!?! DBG:ccExprVisitor.defaultCase" + o + " class= "
                + o.getClass().getName());
    }

    @Override
    public void caseMethodHandle(MethodHandle handle) {
        show("caseMethodHandle", handle);
    }

    @Override
    public void caseMethodType(MethodType methodType) {

    }
}
