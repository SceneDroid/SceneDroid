package android.goal.explorer.analysis.value.analysis.strings;

import soot.G;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.FieldRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.ParameterRef;
import soot.jimple.RefSwitch;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;

import java.util.Iterator;
import java.util.List;

public class CCRefVisitor implements RefSwitch {
    public static final boolean ARRAY_FIELDS = false;
    static int verbose_level = 0;
    LanguageConstraints.Box lcb;
    Stmt stmt;
    CCVisitor svis;

    public CCRefVisitor s(LanguageConstraints.Box lcb0, Stmt s) {
        lcb = lcb0;
        stmt = s;
        return this;
    }

    public CCRefVisitor(CCVisitor svis0) {
        svis = svis0;
    }

    protected void dbg0(String what, Value v) {
        G.v().out.println("DBG:CCRefVisitor." + what + " v=" + v + " class= " + v.getClass().getName()
                + " type= " + v.getType() + " type-class= " + v.getType().getClass().getName());
    }

    protected void ignore(String what, Value v) {
        if (verbose_level > 10)
            dbg0(what, v);
    }

    protected void dbg(String what, Value v) {
        if (verbose_level > 5)
            dbg0(what, v);
    }

    // ///////////////////////////////////////////////////////////////////////////

    @Override
    public void caseArrayRef(ArrayRef v) {// TBD: revise and make this alias aware
        ignore("caseArrayRef", v);
        Value base = v.getBase();
        DBG.dbgValue("caseArrayRef's base", base);
        if (base instanceof Local) {
            Local l = (Local) base;
            List<Unit> bdefs = svis.mld.getDefsOfAt(l, stmt);
            Iterator<Unit> bDefsIt = bdefs.iterator();
            while (bDefsIt.hasNext()) {
                Stmt sdef = (Stmt) bDefsIt.next();
                sdef.apply(svis);// recursive
                LanguageConstraints.Box defb = Res2Constr.getStmt((sdef));
                assert (defb != null);
                LanguageConstraints.Union lcu = new LanguageConstraints.Union();
                lcu.addLCB(new LanguageConstraints.Box(defb.getLC()));
                lcu.addLCB(lcb);
                defb.setLC(lcu);
                if (ARRAY_FIELDS) {
                    /*
                     * hack to get array fields working This is a hack, because I need to treat
                     * references/point2 aliases in a more systematic way
                     */
                    if (sdef instanceof AssignStmt) {// assignment to field array
                        AssignStmt astmt = (AssignStmt) sdef;
                        Value rop = astmt.getRightOp();
                        boolean hasfr = astmt.containsFieldRef();
                        if (hasfr) {
                            FieldRef fr = astmt.getFieldRef();
                            Res2Constr.putField(fr, lcb);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void caseStaticFieldRef(StaticFieldRef v) {
        dbg("caseStaticFieldRef", v);
        Res2Constr.putField(v, lcb);
    }

    @Override
    public void caseInstanceFieldRef(InstanceFieldRef v) {
        ignore("caseInstanceFieldRef", v);
        Res2Constr.putField(v, lcb);
    }

    @Override
    public void caseParameterRef(ParameterRef v) {
        ignore("caseParameterRef", v);
    }

    @Override
    public void caseCaughtExceptionRef(CaughtExceptionRef v) {
        ignore("caseCaughtExceptionRef", v);
    }

    @Override
    public void caseThisRef(ThisRef v) {
        ignore("caseThisRef", v);
    }

    @Override
    public void defaultCase(Object obj) {
        G.v().out.println("Ignore: CCRefVisitor.defaultCase obj=" + obj + " class= "
                + obj.getClass().getName());
    }
}
