package android.model

import android.hierarchy.BaseClassUtils
import android.hierarchy.MethodSignatures
import android.soot.MethodFinder
import android.soot.SystemClassInfoProvider
import org.slf4j.LoggerFactory
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.jimple.IntConstant
import soot.jimple.Stmt

class FragmentFactory {
    companion object {
        private val logger = LoggerFactory.getLogger(FragmentFactory::class.java)

        @JvmStatic
        fun getFragmentsInActiveHierarchy(): List<Fragment> {
            assert(Scene.v().doneResolving()) { "Soot Scene must be resolved before trying to resolve active hierarchy" }
            return getImplementingFragmentClassesInActiveHierarchy()
                .map {
                    val lifeCycleMethods = getLifeCycleSootMethodsForFragmentClass(it)
                    val layoutId = getLayoutId(lifeCycleMethods)
                    Fragment(it, lifeCycleMethods, layoutId)
                }
        }

        @JvmStatic
        private fun getImplementingFragmentClassesInActiveHierarchy(): List<SootClass> {
            val implementingFragmentClasses = BaseClassUtils.getBaseFragmentSootClassesInScene()
                .flatMap { Scene.v().activeHierarchy.getSubclassesOf(it) }
                .filter { it.isConcrete }
                .filterNot { SystemClassInfoProvider.isClassInSystemPackage(it) }
                .distinct()

            logger.info("Found ${implementingFragmentClasses.size} classes implementing fragment")
            logger.debug("Fragment implementation classes : $implementingFragmentClasses")
            return implementingFragmentClasses
        }

        @JvmStatic
        private fun getLifeCycleSootMethodsForFragmentClass(fragmentClass: SootClass): List<SootMethod> {
            return MethodSignatures.getFragmentLifecycleMethodSignatures()
                .map { MethodFinder.findMethodImplementation(fragmentClass, it) }
        }

        /**
         * Figure out the R.layout.id associated with the activity.
         * Most likely we can just look only in the onCreateView lifecycle method to find it
         * https://stackoverflow.com/questions/28929637/difference-and-uses-of-oncreate-oncreateview-and-onactivitycreated-in-fra
         */
        @JvmStatic
        private fun getLayoutId(lifecycleMethods: List<SootMethod>): Int {
            val setContentViewStmt = lifecycleMethods
                .first { it.subSignature == MethodSignatures.FragmentLifecycleMethods.ON_CREATE_VIEW.sootMethodSubSignature }
                .retrieveActiveBody().units
                .filter {
                    (it as Stmt).containsInvokeExpr() &&
                    it.invokeExpr.method.subSignature == "android.view.View inflate(int,android.view.ViewGroup,boolean)"
                }

            if (setContentViewStmt.size != 1) {
                logger.warn("Found ${setContentViewStmt.size} statements that setContentView")
            }

            return ((setContentViewStmt.first() as Stmt)
                .invokeExpr.args.first() as IntConstant)
                .value
        }
    }
}
