package android.analysis

import android.providers.ARSCInfoProvider
import org.slf4j.LoggerFactory
import soot.Local
import soot.SootClass
import soot.SootMethod
import soot.jimple.AssignStmt
import soot.jimple.CastExpr
import soot.jimple.InstanceInvokeExpr
import soot.jimple.IntConstant
import soot.jimple.InvokeStmt
import soot.jimple.Stmt
import soot.jimple.infoflow.android.SetupApplication
import soot.jimple.infoflow.android.callbacks.AndroidCallbackDefinition
import soot.jimple.infoflow.android.resources.controls.AndroidLayoutControl
import java.lang.RuntimeException

/**
 * provides information about the widget that triggers the given callback
 */
class CallbackWidgetProvider(private val flowDroidAnalysis: SetupApplication) {
    /**
     * Store of previously computed results
     */
   /* private val layoutControls = HashMap<SootMethod, AndroidLayoutControl?>()
    private val userControls by lazy { flowDroidAnalysis.layoutFileParser.userControlsByID }

    /**
     * Gets the layout control associated with the callback
     */
    fun findLayoutControl(callback: CallbackDefinition): AndroidLayoutControl? {
        assert(callback.callbackType == CallbackDefinition.CallbackType.Widget) {
            "Callback $callback is not of type widget. Cannot have associated layout"
        }

        if (layoutControls.containsKey(callback.targetMethod)) {
            return layoutControls.getValue(callback.targetMethod)
        }

        return findLayoutControl(callback.targetMethod)
    }

    fun findLayoutControl(sootMethod: SootMethod): AndroidLayoutControl? {
        logger.debug("Finding layout for $sootMethod")

        // TODO do we need to propagate up the call graph to check methods that may call this?
        val layouts = findLayoutsForComponent(sootMethod.declaringClass)
        var layoutControl = layouts?.find { it.clickListener == sootMethod.name }

        // Check if layout control has been found, otherwise look for dynamic registration
        layoutControl?.also {
            layoutControls[sootMethod] = it
            return it
        }

        logger.debug("Layout for $sootMethod not statically declared, looking for dynamic registration")
        // Looks into dynamic registrations, throws an error if nothing can be found
        layoutControl = findResourceId(sootMethod)
            ?.let { userControls[it] }

        layoutControl?.also {
            layoutControls[sootMethod] = it
            return it
        }

        logger.warn("No layout control found for callback $sootMethod")
        return null
    }

    private fun findLayoutsForComponent(component: SootClass): Set<AndroidLayoutControl>? {
        // We analyze the lifecycle method onCreate to find the associate resource ID for the layout file
        val resId = findLayoutResourceId(component) ?: return null
        val fileName = ARSCInfoProvider(flowDroidAnalysis.resources).getFileNameForLayoutId(resId)

        // Then we get the layouts within the file through flow-droid
        return flowDroidAnalysis.layoutFileParser.userControls.get(fileName)
    }

    private fun findLayoutResourceId(component: SootClass): Int? {
        val resIds = flowDroidAnalysis.callbackAnalyzer.layoutClasses.get(component) ?: return null
        return resIds.firstOrNull()
    }

    private fun findResourceId(sootMethod: SootMethod): Int? {
        // Get the call sites where the callback class is registered
        val callSites = flowDroidAnalysis.callbackAnalyzer.setterCallbackMap.get(sootMethod.declaringClass)

        // TODO: need some logic to possibly narrow down call sites to only relevant ones
        for (callSite in callSites) {
            // go backwards through the method until the view registered is seen
            val invokeExpr = (callSite.o2 as InvokeStmt).invokeExpr as InstanceInvokeExpr
            var base = invokeExpr.base

            var stmt = callSite.o2 as Stmt
            val units = callSite.o1.activeBody.units
            while (stmt != units.first) {
                stmt = units.getPredOf(stmt) as Stmt

                // follow register assignments
                if (stmt is AssignStmt && stmt.leftOp == base && (stmt.rightOp is Local || stmt.rightOp is CastExpr)) {
                    val ref = when (val rightOp = stmt.rightOp) {
                        is Local -> { rightOp }
                        is CastExpr -> { rightOp.op }
                        else -> throw RuntimeException("No logic to handle type ${rightOp.javaClass}")
                    }
                    base = ref
                }

                // extracts the proper resource ID for the view
                if (stmt.containsInvokeExpr() && stmt.invokeExpr.method.name == "findViewById"
                    && stmt is AssignStmt && stmt.leftOp == base) {
                        return (stmt.invokeExpr.args.first() as IntConstant).value
                }
            }
        }
        return null
    }

    companion object {
        private val logger = LoggerFactory.getLogger(CallbackWidgetProvider::class.java)
    }*/
}
