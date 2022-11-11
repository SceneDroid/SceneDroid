package android.goal.explorer.analysis.value.factory;

import org.pmw.tinylog.Logger;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.spark.sets.EmptyPointsToSet;

import java.util.List;

/**
 * Utility functions for edge function and flow function factories.
 */
public class FunctionFactoryUtils {
    private static PointsToAnalysis pointsToAnalysis = null;

    /**
     * Gets the points-to set for a given value. The value can be, for example, a variable or a field.
     *
     * @param value A value.
     * @return The points-to set for the input value.
     */
    public static PointsToSet getPointsToSetForValue(Value value) {
        if (pointsToAnalysis == null) {
            pointsToAnalysis = Scene.v().getPointsToAnalysis();
        }

        if (value instanceof Local) {
            return pointsToAnalysis.reachingObjects((Local) value);
        } else if (value instanceof InstanceFieldRef) {
            InstanceFieldRef sootFieldRef = (InstanceFieldRef) value;
            PointsToSet refPointsToSet = pointsToAnalysis.reachingObjects((Local) sootFieldRef.getBase());

            return pointsToAnalysis.reachingObjects(refPointsToSet, sootFieldRef.getField());
        } else if (value instanceof StaticFieldRef) {
            StaticFieldRef sootFieldRef = (StaticFieldRef) value;

            return pointsToAnalysis.reachingObjects(sootFieldRef.getField());
        } else if (value instanceof ArrayRef) {
            ArrayRef arrayRef = (ArrayRef) value;
            PointsToSet arrayPointsToSet = pointsToAnalysis.reachingObjects((Local) arrayRef.getBase());

            return pointsToAnalysis.reachingObjectsOfArrayElement(arrayPointsToSet);
        } else {
            return EmptyPointsToSet.v();
        }
    }

    /**
     * Determines if a value potentially points to the same location as one of the values in a list.
     * This is useful for example to determine if a variable should be propagated through the
     * boundaries of a called method.
     *
     * @param source The reference value.
     * @param arguments A list of value.
     * @return The index of the element in the list that the reference value is pointing to, or -1 if
     *         no common heap location is found.
     */
    public static int shouldPropagateSource(Value source, List<Value> arguments) {
        Logger.debug("**Source: " + source);
        for (int i = 0; i < arguments.size(); ++i) {
            Logger.debug("**Argument: " + arguments.get(i));
            if (sourcePointsToArgument(source, arguments.get(i))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Determines if an instance field should be propagated through a method call. This method only
     * checks propagation rule for the field base. It does not check if the field points to an
     * argument, which should be done outside this method.
     *
     * @param instanceFieldRef An instance field reference.
     * @param invokeExpr An invoke expression for the called method.
     * @return True if the field should be propagated.
     */
    public static boolean shouldPropagateInstanceField(InstanceFieldRef instanceFieldRef,
                                                       InvokeExpr invokeExpr) {
        Value fieldBase = instanceFieldRef.getBase();
        List<Value> argList = invokeExpr.getArgs();
        // A field reference should be propagated if the base of the field points to a method argument.
        for (Value anArgList : argList) {
            if (sourcePointsToArgument(fieldBase, anArgList)) {
                return true;
            }
        }

        // A field reference should be propagated if the base of the field points to the base of the
        // method call for an instance call.
        if (invokeExpr instanceof InstanceInvokeExpr) {
            Value invokeExprBase = ((InstanceInvokeExpr) invokeExpr).getBase();
            return sourcePointsToArgument(fieldBase, invokeExprBase);
        }

        return false;
    }

    /**
     * Check if a source value points to an argument value. This includes source values that are field
     * references.
     *
     * @param source A value.
     * @param argument Another value.
     * @return True if the source and the argument values point to the same location.
     */
    private static boolean sourcePointsToArgument(Value source, Value argument) {
        PointsToSet sourcePointsToSet = FunctionFactoryUtils.getPointsToSetForValue(source);
        PointsToSet argumentPointsToSet = FunctionFactoryUtils.getPointsToSetForValue(argument);

        Logger.debug(source + " " + argument + ": " + source.equivTo(argument) + " - "
                    + sourcePointsToSet.hasNonEmptyIntersection(argumentPointsToSet));
        return source.equivTo(argument) || sourcePointsToSet.hasNonEmptyIntersection(argumentPointsToSet);
    }
}
