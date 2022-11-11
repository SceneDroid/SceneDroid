//package android.analysis
//
//import edu.psu.cse.siis.ic3.Ic3Data
//import org.slf4j.LoggerFactory
//import soot.Scene
//import soot.SootClass
//import soot.SootMethod
//import soot.jimple.infoflow.android.SetupApplication
//import soot.jimple.infoflow.android.resources.controls.AndroidLayoutControl
//import soot.util.HashMultiMap
//import soot.util.MultiMap
//import java.lang.RuntimeException
//
///**
// * Finds the transitions between activities
// */
//class InterActivityTransitionsProvider(private val flowDroidAnalysis: SetupApplication,
//                                       private val callbackWidgetProvider: CallbackWidgetProvider,
//                                       private val ic3Data: Ic3Data.Application) {
//
//    /**
//     * Activities included in the transitions
//     */
//    val activities: Set<SootClass> get() = transitions.activities
//
//    /**
//     * Exit point into the activity
//     */
//    val inEdges: MultiMap<SootClass, ExitPoint> get() = transitions.inEdges
//
//    /**
//     * Exit points out of the activity
//     */
//    val outEdges: MultiMap<SootClass, ExitPoint> get() = transitions.outEdges
//
//    private val transitions by lazy { findTransitions() }
//    private data class Transitions(val activities: Set<SootClass>,
//                                   val inEdges: MultiMap<SootClass, ExitPoint>,
//                                   val outEdges: MultiMap<SootClass, ExitPoint>)
//
//    /**
//     * @param source source activity to transition from
//     * @param target target activity to transition to
//     * @param callback the callback to is triggered to cause transition
//     * @param widget widget item that is clicked to cause transition that callback is associated with
//     */
//    data class ExitPoint(val source: SootClass,
//                         val target: SootClass,
//                         val callback: SootMethod,
//                         val widget: AndroidLayoutControl?)
//
//    private fun findTransitions(): Transitions {
//        val activityComponents = ic3Data.componentsList
//            .filter { it.kind == Ic3Data.Application.Component.ComponentKind.ACTIVITY }
//
//        val activities = HashSet<SootClass>()
//        val inEdges = HashMultiMap<SootClass, ExitPoint>()
//        val outEdges = HashMultiMap<SootClass, ExitPoint>()
//
//        for (component in activityComponents) {
//            val activity = findActivity(component)
//            val exitPoints = findExitPoints(activity, component)
//
//            for (exitPoint in exitPoints) {
//                activities.apply {
//                    add(exitPoint.source)
//                    add(exitPoint.target)
//                }
//
//                inEdges.put(exitPoint.target, exitPoint)
//                outEdges.put(exitPoint.source, exitPoint)
//            }
//        }
//
//        return Transitions(activities, inEdges, outEdges)
//    }
//
//    private fun findActivity(component: Ic3Data.Application.Component): SootClass {
//        return flowDroidAnalysis.entrypointClasses.find { it.name == component.name }
//            ?: throw RuntimeException("Activity ${component.name} not present in FlowDroid entry points")
//    }
//
//    private fun findExitPoints(activity: SootClass, component: Ic3Data.Application.Component): List<ExitPoint> {
//        // finds the exit points that start other activities
//        val activityExitPoints = component.exitPointsList
//            .filter { it.instruction.statement.contains("startActivity") }
//
//        return activityExitPoints.mapNotNull { exitPoint ->
//            logger.debug("Parsing transition information for ${exitPoint.instruction.method}")
//
//            val className = exitPoint.intentsList
//                .flatMap { it.attributesList }
//                .firstOrNull { it.kind == Ic3Data.AttributeKind.CLASS }
//                ?.valueList?.first()
//
//            val method = Scene.v().getMethod(exitPoint.instruction.method)
//            if (method.declaringClass != activity) {
//                logger.warn("Declaring class for ${exitPoint.instruction.method} was not $activity")
//            }
//
//            if (className == null) return@mapNotNull null;
//
//            val tgtActivity = getClass(className)
//            val layout = callbackWidgetProvider.findLayoutControl(method)
//            ExitPoint(activity, tgtActivity, method, layout)
//        }
//    }
//
//    private fun getClass(className: String): SootClass {
//        return Scene.v().getSootClass(className.replace('/', '.'))
//    }
//
//    companion object {
//        private val logger = LoggerFactory.getLogger(InterActivityTransitionsProvider::class.java)
//    }
//}
