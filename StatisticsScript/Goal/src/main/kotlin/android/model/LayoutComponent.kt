package android.model

import soot.SootClass
import soot.SootMethod

open class LayoutComponent(
    sootClass: SootClass,
    lifeCycleMethods: List<SootMethod>,
    val layoutId: Int
) : Component(sootClass, lifeCycleMethods) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as LayoutComponent

        if (layoutId != other.layoutId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + layoutId
        return result
    }

    override fun toString(): String {
        return "LayoutComponent(sootClass=$sootClass, layoutId=$layoutId)"
    }
}
