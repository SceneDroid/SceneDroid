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

class ActivityFactory {
    companion object {
        private val logger = LoggerFactory.getLogger(ActivityFactory::class.java)

        /**
         * Retrieve all activities that are loaded by Soot
         */
        @JvmStatic
        fun getActivitiesInActiveHierarchy(): List<Activity> {
            assert(Scene.v().doneResolving()) { "Soot Scene must be resolved before trying to resolve active hierarchy" }
            return getImplementingActivityClassesInActiveHierarchy()
                    .map {  activityClass ->
                        val lifeCycleMethods = getLifeCycleSootMethodsForActivityClass(activityClass)
                        val layoutId = getLayoutId(lifeCycleMethods)
                        Activity(activityClass, lifeCycleMethods, layoutId) }
        }

        /**
         * Find all activity classes that extend the android activity classes
         */
        @JvmStatic
        private fun getImplementingActivityClassesInActiveHierarchy(): List<SootClass> {
            val implementingActivityClasses = BaseClassUtils.getBaseActivitySootClassesInScene()
                    .flatMap { Scene.v().activeHierarchy.getSubclassesOf(it) }
                    .filter { it.isConcrete }
                    .filterNot { SystemClassInfoProvider.isClassInSystemPackage(it) }
                    .distinct()

            logger.info("Found ${implementingActivityClasses.size} classes implementing activity")
            logger.debug("Activity implementation classes : $implementingActivityClasses")
            return implementingActivityClasses
        }

        /**
         * Collect all the android lifecycle methods for the activity
         */
        @JvmStatic
        private fun getLifeCycleSootMethodsForActivityClass(activityClass: SootClass): List<SootMethod> {
            return MethodSignatures.getActivityLifecycleMethodSignatures()
                    .map { MethodFinder.findMethodImplementation(activityClass, it) }
        }

        /**
         * Figure out the R.layout.id associated with the activity.
         * Most likely we can just look only in the onCreate lifecycle method to find it
         * https://stackoverflow.com/questions/22227950/android-is-using-setcontentview-multiple-times-bad-while-changing-layouts
         */
        @JvmStatic
        private fun getLayoutId(lifecycleMethods: List<SootMethod>): Int {
            val setContentViewStmt = lifecycleMethods
                .first { it.subSignature == MethodSignatures.ActivityLifecycleMethods.ON_CREATE.sootMethodSubSignature }
                .retrieveActiveBody().units
                .filter { (it as Stmt).containsInvokeExpr() && it.invokeExpr.method.subSignature == "void setContentView(int)" }

            if (setContentViewStmt.size != 1) {
                logger.warn("Found ${setContentViewStmt.size} statements that setContentView")
            }

            return ((setContentViewStmt.first() as Stmt)
                .invokeExpr.args.first() as IntConstant)
                .value
        }
    }
}
