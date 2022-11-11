package android.util

import soot.Scene
import soot.SootClass

class ActivityUtils {
    companion object {
        /**
         * Checks if the SootClass is an activity class
         */
        fun isActivityClass(sootClass: SootClass): Boolean {
            return SootUtils.ActivityClasses.any {
                Scene.v().activeHierarchy.isClassSubclassOfIncluding(sootClass, it)
            }
        }
    }
}
