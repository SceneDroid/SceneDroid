package android.goal.explorer.analysis;

import android.goal.explorer.analysis.value.identifiers.Argument;
import android.goal.explorer.analysis.value.managers.ArgumentValueManager;
import android.goal.explorer.analysis.value.values.propagation.PropagationConstants;
import android.goal.explorer.model.widget.ClickWidget;
import heros.solver.Pair;
import soot.Local;
import soot.Scene;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.*;
import soot.jimple.infoflow.util.SootMethodRepresentationParser;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;
import soot.toolkits.scalar.SimpleLocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AnalysisUtils {
    /**
     * Checks if the method invocation is a wrapper method for findViewById
     * @param sm The method invocation
     * @return True if the method invocation is a wrapper method for findViewById
     */
    static boolean isWrapperForFindViewById(SootMethod sm) {
        if (sm.getReturnType().toString().equalsIgnoreCase("android.view.View")) {
            int i = 0;
            for (Type paramType : sm.getParameterTypes()) {
                if (paramType.toString().equals("int")) {
                    Local paramLocal = sm.retrieveActiveBody().getParameterLocal(i);
                    // Check the usage of this local variable
                    BriefUnitGraph unitGraph = new BriefUnitGraph(sm.retrieveActiveBody());
                    SimpleLocalDefs localDefs = new SimpleLocalDefs(unitGraph);
                    SimpleLocalUses localUses = new SimpleLocalUses(unitGraph, localDefs);

                    List<Unit> intDefs = localDefs.getDefsOf(paramLocal);
                    for (Unit intDef : intDefs) {
                        List<UnitValueBoxPair> usePair = localUses.getUsesOf(intDef);
                        if (usePair.size()>0) {
                            Unit u = usePair.get(0).getUnit();
                            if (u instanceof Stmt) {
                                Stmt stmt = (Stmt) u;
                                if (stmt.containsInvokeExpr()) {
                                    InvokeExpr inv = stmt.getInvokeExpr();
                                    if (invokesFindViewById(inv)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                    break;
                }
                i++;
            }
        }
        return false;
    }

    /**
     * Checks whether this invocation calls Android's findViewById method
     * @param inv The invocaton to check
     * @return True if this invocation calls findViewById, otherwise false
     */
    static boolean invokesFindViewById(InvokeExpr inv) {
        String methodName = SootMethodRepresentationParser.v()
                .getMethodNameFromSubSignature(inv.getMethodRef().getSubSignature().getString());
        String returnType = inv.getMethod().getReturnType().toString();

        return returnType.equalsIgnoreCase("android.view.View") &&
                methodName.equalsIgnoreCase("findViewById");

    }


    /**
     * Check if the value is reassigned to another local
     * @param usePair The use pair to check for reassignment
     * @return The local the was reassigned, otherwise null
     */
    static Pair<Local, Unit> reassignsLocal(List<UnitValueBoxPair> usePair){
        for (UnitValueBoxPair anUsePair : usePair) {
            Unit useUnit = anUsePair.getUnit();

            if (useUnit instanceof Stmt) {
                Stmt newStmt = (Stmt) useUnit;
                if (newStmt instanceof AssignStmt) {
                    AssignStmt assignStmt = (AssignStmt) newStmt;
                    Value newRightOp = assignStmt.getRightOp();

                    if (newRightOp instanceof CastExpr) {
                        Value leftOp = assignStmt.getLeftOp();
                        if (leftOp instanceof Local) {
                            return new Pair<>((Local) leftOp, useUnit);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Finds the basic block that contains given unit
     * @param blockList The basic block list
     * @param u The unit
     * @return The block, null if not found
     */
    @Nullable
    static Block findBasicBlockWithUnit(List<Block> blockList, Unit u) {
        for (Block block : blockList) {
            for (Unit unit : block) {
                if (unit.equals(u))
                    return block;
            }
        }
        return null;
    }

    public static Integer getIntValue(Value value, Unit unit) {
        InvokeExpr inv = ((Stmt)unit).getInvokeExpr();
        Integer intValue = null;
        if(!(value instanceof IntConstant)){
            Argument arg = extractIntArgumentFrom(inv);
            //TODO should keep the index of the desired value
            Set<Object> values = ArgumentValueManager.v().getArgumentValues(arg, unit, null);
            if (values!=null && !values.isEmpty()) {
                Object valueObj = values.iterator().next();
                if (valueObj instanceof Integer) {
                    intValue = (Integer)valueObj;
                }
            }
        }
        else{
            return ((IntConstant)value).value; //what about other int type, check best way to deal with
        }
        return intValue;
    }


    /**
     * Gets the argument from the invoke expresion
     * @param inv The invoke expresion
     * @return The argument created from the invoke expresion
     */
    public static Argument extractIntArgumentFrom(InvokeExpr inv) {
        Argument arg = new Argument();
        int[] argNum = new int[inv.getArgCount()];
        int n = 0;
        for (int i = 0; i < inv.getArgCount(); i++) {
            Value argValue = inv.getArg(i);
            if (argValue.getType().equals(Scene.v().getTypeUnsafe("int"))) {
                argNum[n++] = i;
            }
        }
        arg.setArgNum(argNum);
        arg.setActions(new String[]{PropagationConstants.DefaultActions.Scalar.REPLACE});
        arg.setType(PropagationConstants.DefaultArgumentTypes.Scalar.INT);
        return arg;
    }

    /**
     * Gets the argument from the invoke expresion
     * @param inv The invoke expresion
     * @return The argument created from the invoke expresion
     */
    public static Argument extractStringArgumentFrom(InvokeExpr inv) {
        Argument arg = new Argument();
        int[] argNum = new int[inv.getArgCount()];
        int n = 0;
        for (int i = 0; i < inv.getArgCount(); i++) {
            Value argValue = inv.getArg(i);
            if (argValue.getType().equals(Scene.v().getTypeUnsafe("java.lang.String")) || argValue.getType().equals(Scene.v().getTypeUnsafe("java.lang.CharSequence"))) {
                argNum[n++] = i;
            }
        }
        arg.setArgNum(argNum);
        arg.setActions(new String[]{PropagationConstants.DefaultActions.Scalar.REPLACE});
        arg.setType(PropagationConstants.DefaultArgumentTypes.Scalar.STRING);
        return arg;
    }

    /**
     * Gets the argument from the invoke expresion
     * @param inv The invoke expresion
     * @return The argument created from the invoke expresion
     */
    public static Argument extractClassArgumentFrom(InvokeExpr inv) {
        Argument arg = new Argument();
        int[] argNum = new int[inv.getArgCount()];
        int n = 0;
        for (int i = 0; i < inv.getArgCount(); i++) {
            Value argValue = inv.getArg(i);
            if (argValue.getType().equals(Scene.v().getTypeUnsafe("java.lang.Class"))) {
                argNum[n++] = i;
            }
        }
        arg.setArgNum(argNum);
        arg.setActions(new String[]{PropagationConstants.DefaultActions.Scalar.REPLACE});
        arg.setType(PropagationConstants.DefaultArgumentTypes.Scalar.CLASS);
        return arg;
    }

    /**
     * Gets the click widgets with given click listener
     * @param clickListener The click listener to look for
     * @param widgetNodeList The list of widgets to look through
     * @return A set of widgets with the given click listener
     */
    static Set<ClickWidget> findWidgetsWithClickListener(String clickListener,
                                                         List<ClickWidget> widgetNodeList) {
        Set<ClickWidget> clickWidgetSet = new HashSet<>();
        for (ClickWidget clickWidgetNode : widgetNodeList) {
            if (clickWidgetNode.getClickListener().equals(clickListener))
                clickWidgetSet.add(clickWidgetNode);
        }
        return clickWidgetSet;
    }
}
