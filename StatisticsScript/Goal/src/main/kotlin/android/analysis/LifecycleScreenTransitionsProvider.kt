package android.analysis

import android.hierarchy.MethodSignatures
import android.model.Screen
import android.soot.FragmentChangeAnalysis
import soot.SootClass
import soot.SootMethod
import soot.jimple.infoflow.android.SetupApplication

/**
 * Determines the screen transitions due to chains of lifecycle methods being called
 */
class LifecycleScreenTransitionsProvider(private val flowDroidAnalysis: SetupApplication) {
    /**
     * Find the screens that result from having the current screen go through the lifecycle chains
     * @param screen the screen to perform analysis for
     * @return possible screens after the lifecycle chains have been run
     */
    /*fun findScreens(screen: Screen): List<Screen> =
        lifecycleChains.flatMap { findScreensAfterChain(screen, it) }

    private fun findScreensAfterChain(screen: Screen,
                                      lifecycleChain: List<MethodSignatures.ActivityLifecycleMethods>): List<Screen> {
        val lifecycleMethods = findLifecycleMethodsForChain(screen.activity, lifecycleChain)
        val fragmentStates = FragmentChangeAnalysis.calculateFragmentChanges(lifecycleMethods, screen.dynamicFragments)
        return fragmentStates.map { Screen(screen.activity, it) }
    }

    private fun findLifecycleMethodsForChain(activity: SootClass,
                                             lifecycleChain: List<MethodSignatures.ActivityLifecycleMethods>)
    : List<SootMethod> {
        val methods = mutableListOf<SootMethod>()
        val callbacks = flowDroidAnalysis.get.get(activity)
            .map { it.targetMethod }

        for (lifecycleMethod in lifecycleChain) {
            val match = callbacks.find { it.subSignature == lifecycleMethod.sootMethodSubSignature }
            if (match != null) {
                methods.add(match)
            }
        }

        return methods
    }

    companion object {
        /**
         * Chains of lifecycle methods considered in analysis
         */
        private val lifecycleChains = listOf(
            listOf(
                MethodSignatures.ActivityLifecycleMethods.ON_PAUSE,
                MethodSignatures.ActivityLifecycleMethods.ON_RESUME
            ),
            listOf(
                MethodSignatures.ActivityLifecycleMethods.ON_PAUSE,
                MethodSignatures.ActivityLifecycleMethods.ON_STOP,
                MethodSignatures.ActivityLifecycleMethods.ON_RESTART,
                MethodSignatures.ActivityLifecycleMethods.ON_PAUSE
            ),
        )
    }*/
}
