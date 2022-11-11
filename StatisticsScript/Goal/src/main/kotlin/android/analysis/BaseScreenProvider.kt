package android.analysis

import android.hierarchy.MethodSignatures
import android.model.Screen
import android.soot.FragmentChangeAnalysis
import android.util.ActivityUtils
import soot.SootClass
import soot.SootMethod
import soot.jimple.infoflow.android.SetupApplication

/**
 * Determines the base screen nodes for the application
 *
 * The call-graph for flow-droid has to have been constructed
 *
 * For activities we determine what are the fragments associated when first created.
 * This would mean all the statically declared fragments within the layout file for the activity
 * and the dynamically added fragments within the lifecycle methods.
 *
 * This provider only deals with the dynamically registered fragments.
 * For static fragments, it can be obtained from the flow-droid analysis
 *
 * The lifecycle methods are activated in the following order
 * https://stackoverflow.com/questions/8515936/android-activity-life-cycle-what-are-all-these-methods-for
 *   1. onCreate
 *   2. onStart
 *   3. onResume
 */
class BaseScreenProvider(private val flowDroidAnalysis: SetupApplication) {
    /*val baseScreens: Map<SootClass, Set<Screen>> by lazy { findBaseScreens() }

    private fun findBaseScreens(): HashMap<SootClass, HashSet<Screen>> {
        val baseScreens = HashMap<SootClass, HashSet<Screen>>()

        flowDroidAnalysis.entrypointClasses
            .filter { ActivityUtils.isActivityClass(it) }
            .forEach { baseScreens[it] = findBaseScreensForActivity(it) }

        return baseScreens
    }

    private fun findBaseScreensForActivity(activity: SootClass): HashSet<Screen> {
        val baseScreens = HashSet<Screen>()
        val lifecycleMethods = findLifecycleMethodsInCallbacks(activity)
        val fragmentStates = FragmentChangeAnalysis.calculateFragmentChanges(lifecycleMethods)
        val staticFragments = flowDroidAnalysis.fragmentClasses.get(activity)

        fragmentStates.forEach { state ->
            baseScreens.add(Screen(activity, state, staticFragments))
        }

        return baseScreens
    }

    private fun findLifecycleMethodsInCallbacks(activity: SootClass): List<SootMethod> {
        val methods = mutableListOf<SootMethod>()
        val callbacks = flowDroidAnalysis.callbackMethods.get(activity) //extended classes ?
            .map { it.targetMethod }

        for (lifecycleMethod in launchLifecycleMethods) {
            val match = callbacks.find { it.subSignature == lifecycleMethod.sootMethodSubSignature }
            if (match != null) {
                methods.add(match)
            }
        }

        return methods
    }

    companion object {
        private val launchLifecycleMethods = listOf(
            MethodSignatures.ActivityLifecycleMethods.ON_CREATE,
            MethodSignatures.ActivityLifecycleMethods.ON_START,
            MethodSignatures.ActivityLifecycleMethods.ON_RESUME
        )
    }*/
}
