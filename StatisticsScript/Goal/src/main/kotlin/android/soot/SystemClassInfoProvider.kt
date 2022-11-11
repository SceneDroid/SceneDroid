package android.soot

import soot.SootClass
import soot.jimple.infoflow.util.SystemClassHandler

class SystemClassInfoProvider {
    companion object {
        @JvmStatic
        fun isClassInSystemPackage(sootClass: SootClass): Boolean = isClassInSystemPackage(sootClass.name)

        @JvmStatic
        fun isClassInSystemPackage(className: String): Boolean {
            return SystemClassHandler.v().isClassInSystemPackage(className)
                || className.startsWith("kotlin.") || className.startsWith("androidx.")
        }
    }
}
