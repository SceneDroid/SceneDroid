package android.util

import org.slf4j.LoggerFactory
import soot.Scene
import soot.SootClass
import soot.jimple.infoflow.android.entryPointCreators.AndroidEntryPointConstants

class SootUtils {
    companion object {
        private val logger = LoggerFactory.getLogger(SootUtils::class.java)
        val ActivityClasses: List<SootClass> by lazy {
            val activities = listOf(
                Scene.v().getSootClass(AndroidEntryPointConstants.ACTIVITYCLASS),
                Scene.v().getSootClass(AndroidEntryPointConstants.APPCOMPATACTIVITYCLASS_V4),
                Scene.v().getSootClass(AndroidEntryPointConstants.APPCOMPATACTIVITYCLASS_V7)
            )

            activities.filter { it.isPhantom }
                .forEach { logger.warn("Activity Class ($it) was phantom") }

            activities
        }
    }
}
