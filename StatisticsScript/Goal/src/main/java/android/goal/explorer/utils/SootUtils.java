package android.goal.explorer.utils;

import android.goal.explorer.model.component.AbstractComponent;
import org.pmw.tinylog.Logger;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.infoflow.util.SystemClassHandler;

import javax.annotation.Nullable;
import java.util.Set;

public class SootUtils {

    /**
     * Find and add method to the list
     * @param x The method signature
     * @param component The activity or fragment
     */
    @Nullable
    public static MethodOrMethodContext findAndAddMethod(String x, AbstractComponent component) {
        try {
            return findMethodBySig(x, component.getMainClass(), component.getAddedClasses());
        } catch (RuntimeException e) {
            Logger.debug(e.getMessage());
        }
        return null;
    }

    /**
     * Find a method by its signature.
     * It also checks the superclass for methods that were not overridden
     * @param methodSig The method signature
     * @param sc The class to search
     * @param addedClasses The superclasses to search
     * @return The SootMethod found
     */
    private static MethodOrMethodContext findMethodBySig(String methodSig, SootClass sc, Set<SootClass> addedClasses) {
        SootMethod sm = sc.getMethodUnsafe(methodSig);
        if (sm!=null) {
            Logger.debug("Found the method {}", sm);
            if (!SystemClassHandler.v().isClassInSystemPackage(sm.getDeclaringClass().getName()))
                return sm;
        } else if (addedClasses!=null && !addedClasses.isEmpty()) {
            for (SootClass superClass : addedClasses) {
                SootMethod smSuper = superClass.getMethodUnsafe(methodSig);
                if (smSuper != null) {
                    if (!SystemClassHandler.v().isClassInSystemPackage(smSuper.getDeclaringClass().getName()))
                        return smSuper;
                }
            }
        }
        throw new RuntimeException(String.format("Cannot find method %s -> %s and its superclasses do not override this method", methodSig, sc.getName())) ;
    }

    public static boolean isSubclassOf(SootClass child, SootClass parent) {
        return parent != null && child != null && Scene.v().getActiveHierarchy().isClassSubclassOf(child, parent);
    }
}
