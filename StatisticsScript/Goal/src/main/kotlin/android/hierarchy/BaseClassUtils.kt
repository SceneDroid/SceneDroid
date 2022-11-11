package android.hierarchy

import org.slf4j.LoggerFactory
import soot.Scene
import soot.SootClass

class BaseClassUtils {
    companion object {
        private val logger = LoggerFactory.getLogger(BaseClassUtils::class.java)

        @JvmStatic
        fun getBaseActivitySootClassesInScene(): List<SootClass> {
            val activitySootClasses = getSootClassesWithNames(BaseClassNames.getBaseActivityClassNames())

            assert(activitySootClasses.isNotEmpty()) { "No Activity Soot Classes present in Scene" }
            logger.debug("Activity Soot Classes found : $activitySootClasses")
            return activitySootClasses
        }

        @JvmStatic
        fun getBaseFragmentSootClassesInScene(): List<SootClass> {
            val fragmentSootClasses = getSootClassesWithNames(BaseClassNames.getBaseFragmentClassNames())

            assert(fragmentSootClasses.isNotEmpty()) { "No Fragment Soot Classes present in Scene" }
            logger.debug("Fragment Soot Classes found : $fragmentSootClasses")
            return fragmentSootClasses
        }

        @JvmStatic
        private fun getSootClassesWithNames(classNames: List<String>): List<SootClass> {
            return classNames
                    // if the Soot Class cannot be found, do not allow the creation of a phantom class
                    .mapNotNull { Scene.v().getSootClassUnsafe(it, false) }
        }
    }
}
