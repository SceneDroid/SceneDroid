//package android.analysis
//
//import edu.psu.cse.siis.ic3.Ic3Data
//import soot.RefType
//import soot.Scene
//import soot.SootClass
//import soot.SootMethod
//import soot.jimple.ClassConstant
//import soot.jimple.InvokeStmt
//import soot.jimple.infoflow.android.SetupApplication
//import soot.jimple.infoflow.android.callbacks.CallbackDefinition
//import soot.util.HashMultiMap
//import soot.util.MultiMap
//import java.lang.RuntimeException
//
///**
// * Finds transitions between a service to an activity
// */
//class ServiceTransitionsProvider(private val flowDroidAnalysis: SetupApplication,
//                                 private val ic3Data: Ic3Data.Application) {
//    /**
//     * Services present in apk
//     */
//    val services: Set<SootClass> get() = transitions.services
//
//    /**
//     * Possible transitions from an activity to a service
//     */
//    val toService: MultiMap<SootClass, SootClass> get() = transitions.toService
//
//    /**
//     * Possible transitions from a service to an activity
//     */
//    val toActivity: MultiMap<SootClass, SootClass> get() = transitions.toActivity
//
//    private val transitions by lazy { findTransitions() }
//    private data class Transitions(val services: Set<SootClass>,
//                                   val toService: MultiMap<SootClass, SootClass>,
//                                   val toActivity: MultiMap<SootClass, SootClass>)
//
//    private fun findTransitions(): Transitions{
//        val serviceComponents = ic3Data.componentsList.filter {
//            it.kind == Ic3Data.Application.Component.ComponentKind.SERVICE
//        }
//
//        val services = HashSet<SootClass>()
//        val toActivity = HashMultiMap<SootClass, SootClass>()
//
//        serviceComponents.forEach { service ->
//            val svClass = getServiceClass(service)
//
//            // get all activities reached by this broadcast
//            val acClasses = service.exitPointsList.flatMap { exitPoint ->
//                val classAttr = exitPoint.intentsList.mapNotNull { intent ->
//                    intent.attributesList.firstOrNull { it.kind == Ic3Data.AttributeKind.CLASS }
//                }
//
//                classAttr.map { getActivityClass(it) }
//            }
//
//            // cache the results
//            services.add(svClass)
//            toActivity.putAll(svClass, acClasses.toSet())
//        }
//
//        return Transitions(services, findActivityToServiceTransitions(), toActivity)
//    }
//
//    private fun findActivityToServiceTransitions(): MultiMap<SootClass, SootClass> {
//        val transitions = HashMultiMap<SootClass, SootClass>()
//        val activities = flowDroidAnalysis.entrypointClasses
//
//        activities.forEach { activity ->
//            val widgetCallbacks = flowDroidAnalysis.callbackMethods.get(activity)
//                .filter { it.callbackType == CallbackDefinition.CallbackType.Widget }
//                .map { it.targetMethod }
//
//            widgetCallbacks.forEach { callback ->
//                if (startsService(callback)) {
//                    transitions.put(activity, startedService(callback))
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
//    private fun getServiceClass(broadcast: Ic3Data.Application.Component): SootClass {
//        return Scene.v().getSootClass(broadcast.name)
//    }
//
//    private fun startsService(callback: SootMethod): Boolean {
//        return callback.activeBody.units.any {
//            it is InvokeStmt && it.invokeExpr.methodRef.name == "startService"
//        }
//    }
//
//    private fun startedService(callback: SootMethod): SootClass {
//        callback.activeBody.units.forEach { stmt ->
//
//            // check for intialization of an Intent
//            if (stmt is InvokeStmt && stmt.invokeExpr.methodRef.name == "<init>") {
//                // go through arguments to determine the class of service
//                stmt.invokeExpr.args.forEach {
//                    // check if the value represents a service class type
//                    if (it is ClassConstant && it.isRefType && isService((it.toSootType() as RefType).sootClass)) {
//                        return (it.toSootType() as RefType).sootClass
//                    }
//                }
//            }
//        }
//
//        throw RuntimeException("Could not find the started service")
//    }
//
//    private fun isService(sc: SootClass): Boolean {
//        val svClass = Scene.v().getSootClass("android.app.Service")
//        return Scene.v().activeHierarchy.isClassSubclassOf(sc, svClass)
//    }
//}
