package android.goal.explorer.analysis.value.analysis.strings;

import soot.Body;
import soot.G;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.toolkits.graph.DominatorsFinder;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.SimpleDominatorsFinder;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.SmartLocalDefs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LocalDefUse {

    private final Map<Local, Set<Unit>> l2def;
    private final Map<Local, Set<Unit>> l2use;
    private SootMethod sm;

    Body body;
    ExceptionalUnitGraph graph;
    /*
     * Note that sld should be updated after each statement modification. Same for graph
     */
    SmartLocalDefs sld;
    DominatorsFinder dFinder;
    LocalDefUse ldu;

    void add(Map<Local, Set<Unit>> l2x, Local l, Unit stmt) {
        if (!l2x.containsKey(l))
            l2x.put(l, new HashSet<Unit>());
        Set<Unit> _set = l2x.get(l);
        _set.add(stmt);
    }

    void remove(Map<Local, Set<Unit>> l2x, Local l, Unit stmt) {
        if (l2x.containsKey(l)) {
            Set<Unit> _set = l2x.get(l);
            _set.remove(stmt);
        }
    }

    void add_facts(List<ValueBox> lst, Map<Local, Set<Unit>> l2x, Unit stmt) {
        Iterator<ValueBox> it = lst.iterator();
        while (it.hasNext()) {
            Value v = it.next().getValue();
            if (v instanceof Local) {
                add(l2x, (Local) v, stmt);
            }
        }
    }

    void remove_facts(List<ValueBox> lst, Map<Local, Set<Unit>> l2x, Unit stmt) {
        Iterator<ValueBox> it = lst.iterator();
        while (it.hasNext()) {
            Value v = it.next().getValue();
            if (v instanceof Local) {
                remove(l2x, (Local) v, stmt);
            }
        }
    }

    void recordUnit(Unit stmt) {
        List<ValueBox> uselst = stmt.getUseBoxes();
        List<ValueBox> deflst = stmt.getDefBoxes();
        // fill in Local->definition
        add_facts(deflst, l2def, stmt);
        // fill in Local->Use
        add_facts(uselst, l2use, stmt);
    }

    void deleteUnit(Unit stmt) {
        List<ValueBox> uselst = stmt.getUseBoxes();
        List<ValueBox> deflst = stmt.getDefBoxes();
        // remove from Local->definition
        remove_facts(deflst, l2def, stmt);
        // remove from Local->Use
        remove_facts(uselst, l2use, stmt);
    }

    LocalDefUse(Body body) {
        // Iterator localsIt = method.getActiveBody().getLocals().iterator();
        // while(localsIt.hasNext()){}
        l2def = new HashMap<Local, Set<Unit>>();
        l2use = new HashMap<Local, Set<Unit>>();
        this.body = body;
        graph = new ExceptionalUnitGraph(body);
        if (graph.getHeads().size() != 1) {
            body.getUnits().addFirst(Jimple.v().newNopStmt());
            graph = new ExceptionalUnitGraph(body);
        }
        sld = new SmartLocalDefs(graph, new SimpleLiveLocals(graph));
        dFinder = new SimpleDominatorsFinder(graph);

        Iterator<Unit> unitIt = this.body.getUnits().snapshotIterator();

        while (unitIt.hasNext()) {
            Unit stmt = unitIt.next();
            recordUnit(stmt);
        }
    }

    Set<Unit> getDefs(Local l) {
        return l2def.get(l);
    }

    Set<Unit> getUses(Local l) {
        return l2use.get(l);
    }

    void dbg(Value v, String msg) {
        if (v instanceof Local) {
            Local l = (Local) v;
            G.v().out.println(msg + ":USES:" + getUses(l));
            G.v().out.println(msg + ":DEFS:" + getDefs(l));
        }
    }

    // stmt: res := base.SomeStringFunctThatChangesBaseAndReturnsBase(...)
    boolean shouldMergeResultAndBase(Local res, Local base, Stmt stmt) {
        if (res == base)
            return false;
        Set<Unit> usesBase = getUses(base);
        if (usesBase.size() == 1)
            return false; // this is the last (and only) use of base.
        // Set<Unit> usesRes = getUses(res);
        // Set<Unit> defsRes = getDefs(res);
        // Set<Unit> defsBase = getDefs(base);
        return true;
    }

    // Can we replace stmt: oldRes = newRes.expr(.) with newRes = newRes.expr(.)?
    // That depends on the uses of oldRes ...
    // THIS ONLY HOLDS FOR STATEMENTS WHOSE SIDE EFFECT IS oldRes=newRes
    // i.e. statements like $r3 = r2.append(...);
    boolean canReplaceResult(Local oldRes, Local newRes, Stmt stmt) {
        Set<Unit> usesOld = getUses(oldRes);
        Set<Unit> defsOld = getDefs(oldRes);
        Set<Unit> defsNew = getDefs(newRes);
        if (defsOld.size() == 1)
            return true; // expr uses newRes, so def must dominate stmt.

        // What was the purpose of the following (commented) code? It seems suspicious...
        // List<Unit> defsNewAtStmt = sld.getDefsOfAt(newRes, stmt);
        // if (defsNewAtStmt.size() == defsNew.size())
        // return true;

        Iterator<Unit> oit = usesOld.iterator();
        while (oit.hasNext()) {
            Unit use = oit.next();
            List<Unit> defsOldAtStmt = sld.getDefsOfAt(oldRes, use);
            if (defsOldAtStmt.size() > 1)
                return false;
        }
        return true;
    }

    boolean replaceResultAndUses(Local oldRes, Local newRes, Stmt stmt) {
        AssignStmt astmt = (AssignStmt) stmt;
        assert (astmt.getLeftOp() == oldRes);

        Set<Unit> usesOld = getUses(oldRes);
        if (usesOld != null) {
            Iterator<Unit> oit = usesOld.iterator();
            while (oit.hasNext()) {
                Unit u = oit.next();
                List<ValueBox> uselst = u.getUseBoxes();
                Iterator<ValueBox> it = uselst.iterator();
                while (it.hasNext()) {
                    ValueBox vb = it.next();
                    Value v = vb.getValue();
                    if (v == oldRes) {
                        vb.setValue(newRes); // replace old use
                        remove(l2use, oldRes, stmt); // old def no longer used here
                        add(l2use, newRes, stmt); // new def used now
                    }
                }
            }
            graph = new ExceptionalUnitGraph(body);
            sld = new SmartLocalDefs(graph, new SimpleLiveLocals(graph));
        }
        astmt.setLeftOp(newRes); // replace result
        remove(l2def, oldRes, stmt); // old def no longer cached
        add(l2def, newRes, stmt); // new def cached

        return true;
    }
}
