package android.goal.explorer.analysis;

import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
import soot.Type;
import soot.Value;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;
import soot.jimple.spark.geom.dataMgr.Obj_full_extractor;
import soot.jimple.spark.geom.dataMgr.PtSensVisitor;
import soot.jimple.spark.geom.dataRep.IntervalContextVar;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.geomPA.GeomQueries;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.toolkits.callgraph.Edge;
import soot.*;
import soot.NullType;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pmw.tinylog.Logger;

public class TypeAnalyzer {
    private static TypeAnalyzer instance;

    public static synchronized TypeAnalyzer v() {
        if (instance == null)
            instance = new TypeAnalyzer();
        return instance;
    }

    public synchronized Set<Type> getPossibleTypesByBackwardAnalysis(Value arg, Stmt stmt, Set<Type> blacklistedTypes, LocalDefs localDefs){
        Set<Type> possibleTypes = new HashSet<>();
        getPossibleTypesByBackwardAnalysis(arg, stmt, blacklistedTypes, possibleTypes, localDefs);
        return possibleTypes;
    }

    public synchronized boolean extendsDefaultType(Value arg, Set<Type> possibleTypes){
        return possibleTypes.stream().anyMatch(type -> !(arg.getType() instanceof NullType) && Scene.v().getFastHierarchy().canStoreType(arg.getType(), type));
    }

    //TO-DO double check if flows are accounted for for local defs
    public synchronized void getPossibleTypesByBackwardAnalysis(Value arg, Stmt stmt, Set<Type> blacklistedTypes, Set<Type> possibleTypes, LocalDefs localDefs){
        //how do you follow flows here ?
        Logger.debug("Invoked def analysis for {} with type {} at stmt {}", arg, arg.getType(), stmt);
        if(!(blacklistedTypes.contains(arg.getType()))){
            if(extendsDefaultType(arg, blacklistedTypes)){
                Logger.debug("Adding to possible types {}", arg.getType());
                    possibleTypes.add(arg.getType());
                    return;
            }   
        }
        if(arg instanceof Local){
            List<Unit> defs = localDefs.getDefsOfAt((Local)arg, stmt);
            Logger.debug("Checking defs for local {}", arg);
            //each def represent a different possible path
            for (Unit def: defs){
                DefinitionStmt defStmt = (DefinitionStmt)def;
                Logger.debug("Possible definition {}", def);
                Value leftOp = defStmt.getLeftOp();
                Value rightOp = defStmt.getRightOp();
                if (rightOp instanceof CastExpr){
                    rightOp = ((CastExpr)rightOp).getOp();
                }
                /*if(rightOp.getType() != blacklistedType)
                    possibleTypes.add(rightOp.getType())
                else */
                getPossibleTypesByBackwardAnalysis(rightOp, defStmt, blacklistedTypes, possibleTypes, localDefs);
            }
        }
    }

    /**
     * Gets the possible types of a value
     * @param arg The local variable
     * @return The set of possible types
     */
    public synchronized Set<Type> getPointToPossibleTypes(Value arg) {
        PointsToAnalysis PTA = Scene.v().getPointsToAnalysis();
        if(!(arg instanceof Local)){
            Logger.error("Can not retrieve reaching objects for non-local {}", arg);
            return new HashSet<>();
        }
        PointsToSet reachingObjects = PTA.reachingObjects((Local)arg);
        return reachingObjects.possibleTypes();
    }

    /**
     * Gets the possible types of a value (context-sensitive)
     * @param arg The local variable
     * @param x The edge in the callgraph that contains the context
     * @return The set of possible types
     */
    public synchronized Set<Type> getContextPointToPossibleTypes(Value arg, Edge x) {
        return getContextPointToPossibleTypes(arg, new Edge[]{x});
    }

    /**
     * Gets the possible types of a value (context-sensitive)
     * @param arg The local variable
     * @param x The set of k edges that maintains the kCFA context
     * @return The set of possible types
     */
    public synchronized Set<Type> getContextPointToPossibleTypes(Value arg, Edge[] x) {
        GeomPointsTo geomPTA = (GeomPointsTo) Scene.v().getPointsToAnalysis();
        GeomQueries geomQueries = new GeomQueries(geomPTA);
        Set<Type> geomContextTypes = new HashSet<>();
        PtSensVisitor<?> visitor = new Obj_full_extractor();
        if (geomQueries.kCFA(x, (Local)arg, visitor)) {
            for (Object icv_obj : visitor.outList) {
                IntervalContextVar icv = (IntervalContextVar) icv_obj;
                AllocNode obj = (AllocNode) icv.var;
                Type type = obj.getType();
                geomContextTypes.add(type);
            }
        }
        return geomContextTypes;
    }
}
