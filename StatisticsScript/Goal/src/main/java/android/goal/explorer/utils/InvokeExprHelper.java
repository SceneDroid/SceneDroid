package android.goal.explorer.utils;

import soot.SootClass;
import soot.jimple.InvokeExpr;
import soot.jimple.infoflow.util.SootMethodRepresentationParser;

public class InvokeExprHelper {
    /**
     * Checks whether this invocation calls Android's Activity.setContentView method
     *
     * @param inv The invocaton to check
     * @return True if this invocation calls setContentView, otherwise false
     */
    public static boolean invokesSetContentView(InvokeExpr inv) {
        String methodName = SootMethodRepresentationParser.v()
                .getMethodNameFromSubSignature(inv.getMethodRef().getSubSignature().getString());
        if (!methodName.equals("setContentView"))
            return false;

        // In some cases, the bytecode points the invocation to the current
        // class even though it does not implement setContentView, instead
        // of using the superclass signature
        SootClass curClass = inv.getMethod().getDeclaringClass();
        while (curClass != null) {
            if (curClass.getName().equals("android.app.Activity")
                    || curClass.getName().equals("android.support.v7.app.ActionBarActivity")
                    || curClass.getName().equals("android.support.v7.app.AppCompatActivity"))
                return true;
            curClass = curClass.hasSuperclass() ? curClass.getSuperclass() : null;
        }
        return false;
    }

    /**
     * Checks whether this invocation calls Android's LayoutInflater.inflate method
     *
     * @param inv
     *            The invocaton to check
     * @return True if this invocation calls inflate, otherwise false
     */
    public static boolean invokesInflate(InvokeExpr inv) {
        String methodName = SootMethodRepresentationParser.v()
                .getMethodNameFromSubSignature(inv.getMethodRef().getSubSignature().getString());
        if (!methodName.equals("inflate"))
            return false;

        // In some cases, the bytecode points the invocation to the current
        // class even though it does not implement setContentView, instead
        // of using the superclass signature
        SootClass curClass = inv.getMethod().getDeclaringClass();
        while (curClass != null) {
            if (curClass.declaresMethod("android.view.View inflate(int,android.view.ViewGroup,boolean)"))
                return true;
            curClass = curClass.hasSuperclass() ? curClass.getSuperclass() : null;
        }
        return false;
    }

    /**
     * Checks whether this invocation calls Android's MenuInflater.inflate method
     *
     * @param inv
     *            The invocaton to check
     * @return True if this invocation calls inflate, otherwise false
     */
    public static boolean invokesMenuInflate(InvokeExpr inv) {
        String methodName = SootMethodRepresentationParser.v()
                .getMethodNameFromSubSignature(inv.getMethodRef().getSubSignature().getString());
        if (!methodName.equals("inflate"))
            return false;

        SootClass curClass = inv.getMethod().getDeclaringClass();
        while (curClass != null) {
            if (curClass.declaresMethod("void inflate(int,android.view.Menu)"))
                return true;
            curClass = curClass.hasSuperclass() ? curClass.getSuperclass() : null;
        }
        return false;
    }

    /**
     * Checks whether this invocation opens a drawer
     *
     * @param inv
     *            The invocaton to check
     * @return True if this invocation opens the drawer, otherwise false
     */
    public static boolean invokesDrawerOpen(InvokeExpr inv) {
        String methodName = SootMethodRepresentationParser.v()
                .getMethodNameFromSubSignature(inv.getMethodRef().getSubSignature().getString());
        if (!methodName.equals("openDrawer"))
            return false;

        SootClass curClass = inv.getMethod().getDeclaringClass();
        while (curClass != null) {
            if (curClass.declaresMethod("void openDrawer(int)"))
                return true;
            curClass = curClass.hasSuperclass() ? curClass.getSuperclass() : null;
        }
        return false;
    }

    //TO-DO: add invokesAddDrawerListener

    public static boolean invokesAddDrawerListener(InvokeExpr inv){
        return false;
    }

    /**
     * Checks whether this invocation get the menu item id
     *
     * @param inv
     *            The invocaton to check
     * @return True if this invocation opens the drawer, otherwise false
     */
    public static boolean invokesGetItemId(InvokeExpr inv) {
        String methodName = SootMethodRepresentationParser.v()
                .getMethodNameFromSubSignature(inv.getMethodRef().getSubSignature().getString());
        return methodName.equals("getItemId") &&
                inv.getMethod().getDeclaringClass().toString().equals("android.view.MenuItem");
    }
}
