package android.goal.explorer.analysis.value.analysis.strings;

import soot.G;
import soot.Local;
import soot.SootMethod;
import soot.Value;
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
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.IntConstant;
import soot.jimple.InterfaceInvokeExpr;
import soot.jimple.InvokeExpr;
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

public class CCExprVisitor implements JimpleValueSwitch {
    private final boolean dbg_all_string_functions_known = false;
    static int verbose_level = 0;
    Stmt stmt;
    LanguageConstraints.Box result;
    CCVisitor svis;
    SootMethod sm;

    protected void dbg0(String what, Value v) {
        G.v().out.println("DBG:ccExprVisitor." + what + " v=" + v + " class= " + v.getClass().getName()
                + " type= " + v.getType() + " type-class= " + v.getType().getClass().getName());
    }

    protected void ignore(String what, Value v) {
        if (verbose_level > 10)
            dbg0(what, v);
        result = LanguageConstraints.BottomBox();
    }

    protected void warning(String what, Value v) {
        if (verbose_level >= 1)
            dbg0("WARNING/ERROR:" + what, v);
        result = LanguageConstraints.BottomBox();
    }

    protected void dbg(String what, Value v) {
        if (verbose_level > 10)
            dbg0(what, v);
    }

    CCExprVisitor(CCVisitor svis0, SootMethod sm0) {
        svis = svis0;
        sm = sm0;
    }

    LanguageConstraints.Box eval(Value v) {
        Stmt old_stmt = stmt;
        result = null;
        assert (stmt != null);
        v.apply(this);
        stmt = old_stmt;
        return result;
    }

    LanguageConstraints.Box eval(Value v, Stmt s) {
        stmt = s;
        return eval(v);
    }

    LanguageConstraints.Box evalBase(InstanceInvokeExpr iiexpr) {
        Value base = iiexpr.getBase();
        return eval(base, stmt);
    }

    LanguageConstraints.Box evalArg(InvokeExpr iexpr, int arg_num) {
        Value v = iexpr.getArg(arg_num);
        return eval(v, stmt);
    }

    LanguageConstraints.Box[] evalArguments(InvokeExpr iexpr) {
        int num_args = iexpr.getArgCount();
        LanguageConstraints.Box[] res = new LanguageConstraints.Box[num_args];
        for (int i = 0; i < num_args; ++i) {
            Value v = iexpr.getArg(i);
            res[i] = eval(v, stmt);
        }
        return res;
    }

    boolean hasSignature(InvokeExpr iexpr, String str) {
        SootMethod sm = iexpr.getMethod();
        String m_signature = sm.getSignature();
        return m_signature.equals(str);
    }

    boolean hasPrefixSignature(InvokeExpr iexpr, String prefix) {
        SootMethod sm = iexpr.getMethod();
        String m_signature = sm.getSignature();
        return m_signature.startsWith(prefix);
    }

    LanguageConstraints.Box
    getCaleeAndPutCallArguments(Stmt stmt, LanguageConstraints.Box[] arguments) {
        Res2Constr.putCallArguments(sm, stmt, arguments);
        return new LanguageConstraints.Box(new LanguageConstraints.Call(sm, stmt, arguments));
    }

    /*************************************************/
    /*********** from ConstantSwitch *****************/
    @Override
    public void caseDoubleConstant(DoubleConstant v) {
        ignore("caseDoubleConstant", v);
    }

    @Override
    public void caseFloatConstant(FloatConstant v) {
        ignore("caseFloatConstant", v);
    }

    @Override
    public void caseIntConstant(IntConstant v) {
        ignore("caseIntConstant", v);
    }

    @Override
    public void caseLongConstant(LongConstant v) {
        ignore("caseLongConstant", v);
    }

    @Override
    public void caseNullConstant(NullConstant v) {
        dbg("caseNullConstant", v);
        result = LanguageConstraints.NullConstantBox();
    }

    @Override
    public void caseStringConstant(StringConstant v) {
        dbg("caseStringConstant", v);
        String str = v.value;
        result = new LanguageConstraints.Box(new LanguageConstraints.Terminal(str));
    }

    @Override
    public void caseClassConstant(ClassConstant v) {
        ignore("caseClassConstant", v);
    }

    /************* from ExprSwitch ********************/
    @Override
    public void caseAddExpr(AddExpr v) {
        ignore("caseAddExpr", v);
    }

    @Override
    public void caseAndExpr(AndExpr v) {
        ignore("caseAndExpr", v);
    }

    @Override
    public void caseCmpExpr(CmpExpr v) {
        ignore("caseCmpExpr", v);
    }

    @Override
    public void caseCmpgExpr(CmpgExpr v) {
        ignore("caseCmpgExpr", v);
    }

    @Override
    public void caseCmplExpr(CmplExpr v) {
        ignore("caseCmplExpr", v);
    }

    @Override
    public void caseDivExpr(DivExpr v) {
        ignore("caseDivExpr", v);
    }

    @Override
    public void caseEqExpr(EqExpr v) {
        ignore("caseEqExpr", v);
    }

    @Override
    public void caseNeExpr(NeExpr v) {
        ignore("caseNeExpr", v);
    }

    @Override
    public void caseGeExpr(GeExpr v) {
        ignore("caseGeExpr", v);
    }

    @Override
    public void caseGtExpr(GtExpr v) {
        ignore("caseGtExpr", v);
    }

    @Override
    public void caseLeExpr(LeExpr v) {
        ignore("caseLeExpr", v);
    }

    @Override
    public void caseLtExpr(LtExpr v) {
        ignore("caseLtExpr", v);
    }

    @Override
    public void caseMulExpr(MulExpr v) {
        ignore("caseMulExpr", v);
    }

    @Override
    public void caseOrExpr(OrExpr v) {
        ignore("caseOrExpr", v);
    }

    @Override
    public void caseRemExpr(RemExpr v) {
        ignore("caseRemExpr", v);
    }

    @Override
    public void caseShlExpr(ShlExpr v) {
        ignore("caseShlExpr", v);
    }

    @Override
    public void caseShrExpr(ShrExpr v) {
        ignore("caseShrExpr", v);
    }

    @Override
    public void caseUshrExpr(UshrExpr v) {
        ignore("caseUshrExpr", v);
    }

    @Override
    public void caseSubExpr(SubExpr v) {
        ignore("caseSubExpr", v);
    }

    @Override
    public void caseXorExpr(XorExpr v) {
        ignore("caseXorExpr", v);
    }

    @Override
    public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
        ignore("caseInterfaceInvokeExpr", v);
    }

    @Override
    public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
        dbg("caseSpecialInvokeExpr", v);
        if (hasSignature(v, "<java.lang.StringBuilder: void <init>(java.lang.String)>")) {
            result = evalArg(v, 0);
            return;
        }
        if (hasSignature(v, "<java.lang.StringBuilder: void <init>()>")) {
            result = new LanguageConstraints.Box(new LanguageConstraints.Terminal(""));
            return;
        }
        // throw new RuntimeException("Unhandled SpecialInvokeExpr: v=" + v);
        // warning("Unhandled SpecialInvokeExpr: v=", v);
        LanguageConstraints.Box[] arguments = evalArguments(v);
        result = getCaleeAndPutCallArguments(stmt, arguments);
    }

    @Override
    public void caseStaticInvokeExpr(StaticInvokeExpr v) {
        dbg("caseStaticInvokeExpr", v);// TBD staticinvoke <java.lang.String:
        // java.lang.String
        // valueOf(java.lang.Object)>(r1)
        // DBG.dbgEdgesOutOf(stmt);
        if (hasSignature(v, "<java.lang.String: java.lang.String valueOf(java.lang.Object)>")) {
            result = evalArg(v, 0);
            return;
        }
        if (dbg_all_string_functions_known) {
            G.v().out.println("DBG:rop-expected-Invoke at " + stmt + " found " + v + "  of class= "
                    + v.getClass().getName() + " type: " + v.getType() + " type's class:"
                    + v.getType().getClass().getName());
            throw new RuntimeException("Unexpected string operation!");
        }

        LanguageConstraints.Box[] arguments = evalArguments(v);
        result = getCaleeAndPutCallArguments(stmt, arguments);
    }

    @Override
    public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
        ignore("caseDynamicInvokeExpr", v);
    }

    @Override
    public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
        dbg("caseVirtualInvokeExpr", v);
        // $r5.<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>(r2)

        if (hasSignature(v,
                "<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>")) {
            LanguageConstraints.Box l = evalBase(v);
            LanguageConstraints.Box r = evalArg(v, 0);
            result = new LanguageConstraints.Box(new LanguageConstraints.Concatenate(l, r));
            return;
        }
        if (hasPrefixSignature(v, "<java.lang.StringBuilder: java.lang.StringBuilder append(")) {
            LanguageConstraints.Box l = evalBase(v);
            // LanguageConstraints.Box r = evalArg(v, 0);
            result =
                    new LanguageConstraints.Box(new LanguageConstraints.Concatenate(l,
                            LanguageConstraints.BottomBox()));
            return;
        }
        if (hasSignature(v, "<java.lang.StringBuilder: java.lang.String toString()>")) {
            result = evalBase(v);
            return;
        }

        if (dbg_all_string_functions_known) {
            G.v().out.println("DBG:rop-expected-Invoke at " + stmt + " found " + v + "  of class= "
                    + v.getClass().getName() + " type: " + v.getType() + " type's class:"
                    + v.getType().getClass().getName());
            throw new RuntimeException("Unexpected string operation!");
        }

        LanguageConstraints.Box[] arguments = evalArguments(v);
        result = getCaleeAndPutCallArguments(stmt, arguments);
        // result = LanguageConstraints.BottomBox();
    }

    @Override
    public void caseCastExpr(CastExpr v) {
        ignore("caseCastExpr", v);
    }

    @Override
    public void caseInstanceOfExpr(InstanceOfExpr v) {
        ignore("caseInstanceOfExpr", v);
    }

    @Override
    public void caseNewArrayExpr(NewArrayExpr v) {
        // ignore("caseNewArrayExpr", v);
        result = new LanguageConstraints.Box(LanguageConstraints.Top.v());
    }

    @Override
    public void caseNewMultiArrayExpr(NewMultiArrayExpr v) {
        ignore("caseNewMultiArrayExpr", v);
    }

    @Override
    public void caseNewExpr(NewExpr v) {
        dbg("caseNewExpr", v);
        result = new LanguageConstraints.Box(LanguageConstraints.Top.v());
    }

    @Override
    public void caseLengthExpr(LengthExpr v) {
        ignore("caseLengthExpr", v);
    }

    @Override
    public void caseNegExpr(NegExpr v) {
        ignore("caseNegExpr", v);
    }

    /************* from RefSwitch ********************/
    @Override
    public void caseArrayRef(ArrayRef v) {
        dbg("caseArrayRef", v);
        Value base = v.getBase();
        result = eval(base);
    }

    @Override
    public void caseStaticFieldRef(StaticFieldRef v) {
        dbg("caseStaticFieldRef", v);
        result = Res2Constr.getField(v);
    }

    @Override
    public void caseInstanceFieldRef(InstanceFieldRef v) {
        ignore("caseInstanceFieldRef", v);
        result = Res2Constr.getField(v);
    }

    @Override
    public void caseParameterRef(ParameterRef v) {
        dbg("caseParameterRef", v);
        result = new LanguageConstraints.Box(new LanguageConstraints.Parameter(sm, v.getIndex()));
    }

    @Override
    public void caseCaughtExceptionRef(CaughtExceptionRef v) {
        ignore("caseCaughtExceptionRef", v);
    }

    @Override
    public void caseThisRef(ThisRef v) {
        ignore("caseThisRef", v);
    }

    /**************************************************/
    @Override
    public void caseLocal(Local l) {
        dbg("caseLocal", l);
        result = svis.mergeDefsOfAt(l, stmt); // Copy this?
    }

    @Override
    public void defaultCase(Object o) {
        G.v().out.println("DBG:ccExprVisitor.defaultCase" + o + " class= " + o.getClass().getName());
    }

    @Override
    public void caseMethodHandle(MethodHandle handle) {
        throw new RuntimeException("MethodHandle not handled.");
    }

    @Override
    public void caseMethodType(MethodType methodType) {

    }
}
