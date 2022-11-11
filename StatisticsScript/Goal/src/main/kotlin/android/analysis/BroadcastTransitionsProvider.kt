//package android.analysis
//
//import edu.psu.cse.siis.ic3.Ic3Data
//import soot.Scene
//import soot.SootClass
//import soot.SootMethod
//import soot.jimple.InvokeStmt
//import soot.jimple.infoflow.android.SetupApplication
//import soot.jimple.infoflow.android.callbacks.CallbackDefinition
//import soot.util.HashMultiMap
//import soot.util.MultiMap
//import java.lang.RuntimeException
//
///**
// * Finds transitions between broadcast receivers and activities
// */
//class BroadcastTransitionsProvider(private val flowDroidAnalysis: SetupApplication,
//                                   private val ic3Data: Ic3Data.Application) {
//
//    /**
//     * Broadcast receivers present in apk
//     */
//    val receivers: Set<SootClass> get() = transitions.receivers
//
//    /**
//     * Possible transitions from an activity to a receiver
//     */
//    val toReceiver: MultiMap<SootClass, SootClass> get() = transitions.toReceiver
//
//    /**
//     * Possible transitions from a receiver to an activity
//     */
//    val toActivity: MultiMap<SootClass, SootClass> get() = transitions.toActivity
//
//    private val transitions by lazy { findTransitions() }
//    private data class Transitions(val receivers: Set<SootClass>,
//                                   val toReceiver: MultiMap<SootClass, SootClass>,
//                                   val toActivity: MultiMap<SootClass, SootClass>
//    )
//
//    private fun findTransitions(): Transitions{
//        val broadcastComponents = ic3Data.componentsList.filter {
//            it.kind == Ic3Data.Application.Component.ComponentKind.RECEIVER ||
//                    it.kind == Ic3Data.Application.Component.ComponentKind.DYNAMIC_RECEIVER
//        }
//
//        val receivers = HashSet<SootClass>()
//        val toActivity = HashMultiMap<SootClass, SootClass>()
//
//        broadcastComponents.forEach { broadcast ->
//            val brClass = getBroadcastClass(broadcast)
//
//            // get all activities reached by this broadcast
//            val acClasses = broadcast.exitPointsList.flatMap { exitPoint ->
//                val classAttr = exitPoint.intentsList.mapNotNull { intent ->
//                    intent.attributesList.firstOrNull { it.kind == Ic3Data.AttributeKind.CLASS }
//                }
//
//                classAttr.map { getActivityClass(it) }
//            }
//
//            // cache the results
//            receivers.add(brClass)
//            toActivity.putAll(brClass, acClasses.toSet())
//        }
//
//        return Transitions(receivers, findActivityToBroadcastTransitions(), toActivity)
//    }
//
//    private fun findActivityToBroadcastTransitions(): MultiMap<SootClass, SootClass> {
//        val transitions = HashMultiMap<SootClass, SootClass>()
//        val activities = flowDroidAnalysis.entrypointClasses
//
//        activities.forEach { activity ->
//            val widgetCallbacks = flowDroidAnalysis.callbackMethods.get(activity)
//                .filter { it.callbackType == CallbackDefinition.CallbackType.Widget }
//                .map { it.targetMethod }
//
//            widgetCallbacks.forEach { callback ->
//                if (registersBroadcastReceiver(callback)) {
//                    transitions.put(activity, registeredBroadcastReceiver(callback))
//                }
//            }
//        }
//
//        return transitions
//    }
//
//    private fun getActivityClass(intent: Ic3Data.Attribute): SootClass {
//        val className = intent.valueList.first().replace('/', '.')
//        return Scene.v().getSootClass(className)
//    }
//
//    private fun getBroadcastClass(broadcast: Ic3Data.Application.Component): SootClass {
//        return Scene.v().getSootClass(broadcast.name)
//    }
//
//    private fun registersBroadcastReceiver(callback: SootMethod): Boolean {
//        return callback.activeBody.units.any {
//            it is InvokeStmt && it.invokeExpr.methodRef.name == "registerReceiver"
//        }
//    }
//
//    private fun registeredBroadcastReceiver(callback: SootMethod): SootClass {
//        callback.activeBody.units.forEach {
//            if (it is InvokeStmt && it.invokeExpr.methodRef.name == "<init>") {
//                val brClass = it.invokeExpr.methodRef.declaringClass
//
//                if (isBroadcastReceiver(brClass)) {
//                    return brClass
//                }
//            }
//        }
//
//        throw RuntimeException("Could not find the registered broadcast receiver")
//    }
//
//    private fun isBroadcastReceiver(sc: SootClass): Boolean {
//        val brClass = Scene.v().getSootClass("android.content.BroadcastReceiver")
//        return Scene.v().activeHierarchy.isClassSubclassOf(sc, brClass)
//    }
//}
