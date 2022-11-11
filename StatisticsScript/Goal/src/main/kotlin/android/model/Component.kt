package android.model

import soot.SootClass
import soot.SootMethod

open class Component protected constructor(
        val sootClass: SootClass,
        val lifecycleMethods: List<SootMethod>
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Component

        if (sootClass != other.sootClass) return false
        if (lifecycleMethods != other.lifecycleMethods) return false

        return true
    }

    override fun hashCode(): Int {
        var result = sootClass.hashCode()
        result = 31 * result + lifecycleMethods.hashCode()
        return result
    }

    override fun toString(): String {
        return "Component(sootClass=$sootClass)"
    }
}
